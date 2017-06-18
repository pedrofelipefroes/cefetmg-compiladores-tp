package parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import lexer.Lexer;
import semantic.*;
import symbol.Tag;
import symbol.Token;
import symbol.Word;

public class Parser
{

    private String fileName;
    private Lexer lexer;
    private ArrayList<Token> token;
    private ArrayList<Follow> followList;
    public static boolean error = false; //Se false ao final da análise, não há erro de compilação

    Stack semanticStack = new Stack();
    Semantic semanticObj;
    Scanner reader = new Scanner(System.in);

    public Parser(String fileName) throws FileNotFoundException
    {
        this.fileName = fileName;
        this.lexer = new Lexer(fileName);
        this.followList = new ArrayList<Follow>();
        this.token = new ArrayList<Token>();
        Follow.initializeFollowList(followList);
    }

    public void run() throws IOException
    {
        getToken();
        program();
        Lexer.printHashtable();
        printResults();
    }

    private void getToken()
    {
        token.add(lexer.run(fileName));
    }

    public void errorParser(String name, int[] tag)
    {
        //Mensagem de erro
        System.out.print("Sintático: Erro na linha " + Lexer.line + " no reconhecimento de " + name + ".\n\tToken esperado: ");
        for (int i = 0; i < tag.length; i++)
            System.out.print(Tag.getName(tag[i]) + " ");
        System.out.println("\n\tPróximo token: '" + Tag.getName(token.get(0).getTag()) + "'.");

        //RECUPERAÇÃO DE ERRO: modo pânico
        System.out.println("Modo pânico ativado!");
        while (!Follow.isFollow(followList, name, token.get(0).getTag()) && token.get(0).getTag() != Tag.EOF) {
            token.remove(0);
            getToken();
        }
        System.out.println("Modo pânico desativado!\n");

        error = true;

        if (token.get(0).getTag() == Tag.EOF) {
            Lexer.printHashtable();
            printResults();
            System.exit(0);
        }
    }

    public void eat(int tag, String name)
    {
        if (token.get(0).getTag() == tag) {
            token.remove(0);
            getToken();
        } else {
            int[] tags = {tag};
            errorParser(name, tags);
        }
    }

    public void program()
    {
        switch (token.get(0).getTag()) {
            case Tag.INIT:
                eat(Tag.INIT, "program");
                //PARTE NÃO LL(1): É preciso conhecer o token seguinte
                if (token.get(0).getTag() == Tag.ID) {
                    getToken();
                    //Se o token seguinte for ',' ou 'is'
                    if (token.get(1).getTag() == Tag.COM || token.get(1).getTag() == Tag.IS) {
                        declList();
                        pop();
                        stmtList(Condition.condition, Condition.tagVOID);
                        pop();
                        eat(Tag.STOP, "program");
                        //Se o token seguinte for ':='
                    } else if (token.get(1).getTag() == Tag.ASSIGN) {
                        stmtList(Condition.condition, Condition.tagVOID);
                        pop();
                        eat(Tag.STOP, "program");
                    } else {
                        int[] tags = {Tag.COM, Tag.IS, Tag.ASSIGN};
                        errorParser("program-case-init", tags);
                    }
                }
                break;

            default:
                int[] tags = {Tag.INIT};
                errorParser("program", tags);
                break;
        }
    }

    public void declList()
    {
        ArrayList<String> valueList = null;
        ArrayList<String> aux;

        switch (token.get(0).getTag()) {
            case Tag.ID:
                valueList = decl();
                pop();
                push(new Semantic(semanticObj.getType(), null, "declList"));
                eat(Tag.DOT_COM, "declList");
                while (token.get(0).getTag() == Tag.ID) {
                    aux = decl();
                    pop();
                    push(new Semantic(semanticObj.getType(), null, "declList"));
                    eat(Tag.DOT_COM, "declList");

                    for (int i = 0; i < aux.size(); i++)
                        valueList.add(aux.get(i));
                }
                break;

            default:
                push(new Semantic(Type.ERROR, null, "declList"));
                int[] tags = {Tag.ID};
                errorParser("declList", tags);
                break;
        }

        verifyUnicity(valueList);
    }

    public ArrayList<String> decl()
    {
        ArrayList<String> valueList = null;

        switch (token.get(0).getTag()) {
            case Tag.ID:
                valueList = identList();
                pop();
                eat(Tag.IS, "decl");
                type();
                pop();

                for (int i = 0; i < valueList.size(); i++)
                    setType(valueList.get(i), semanticObj.getType());

                push(new Semantic(Type.VOID, null, "decl"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "decl"));
                int[] tags = {Tag.ID};
                errorParser("decl", tags);
                break;
        }
        return valueList;
    }

    public ArrayList<String> identList()
    {
        ArrayList<String> valueList = new ArrayList<String>();

        switch (token.get(0).getTag()) {
            case Tag.ID:
                identifier();
                pop();
                valueList.add(semanticObj.getString());
                while (token.get(0).getTag() == Tag.COM) {
                    eat(Tag.COM, "identList");
                    identifier();
                    pop();
                    valueList.add(semanticObj.getString());
                }
                push(new Semantic(Type.VOID, null, "identList"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "identList"));
                int[] tags = {Tag.ID};
                errorParser("identList", tags);
                break;
        }
        return valueList;
    }

    public void type()
    {
        switch (token.get(0).getTag()) {
            case Tag.INTEGER:
                push(new Semantic(Type.INTEGER, null, "type"));
                eat(Tag.INTEGER, "type");
                break;

            case Tag.STRING:
                push(new Semantic(Type.STRING, null, "type"));
                eat(Tag.STRING, "type");
                break;

            default:
                push(new Semantic(Type.ERROR, null, "type"));
                int[] tags = {Tag.INTEGER, Tag.STRING};
                errorParser("type", tags);
                break;
        }
    }

    public void stmtList(Condition condition, int stmtType)
    {
        switch (token.get(0).getTag()) {
            case Tag.ID:
            case Tag.DO:
            case Tag.IF:
            case Tag.READ:
            case Tag.WRITE:
                stmt(condition, stmtType);
                pop();
                push(new Semantic(semanticObj.getType(), null, "stmtList"));
                eat(Tag.DOT_COM, "stmtList");
                while (token.get(0).getTag() == Tag.ID || token.get(0).getTag() == Tag.DO || token.get(0).getTag() == Tag.IF || token.get(0).getTag() == Tag.READ || token.get(0).getTag() == Tag.WRITE) {
                    stmt(condition, stmtType);
                    pop();
                    push(new Semantic(semanticObj.getType(), null, "stmtList"));
                    eat(Tag.DOT_COM, "stmtList");
                }
                break;

            default:
                push(new Semantic(Type.ERROR, null, "stmtList"));
                int[] tags = {Tag.ID, Tag.DO, Tag.IF, Tag.READ, Tag.WRITE};
                errorParser("stmtList", tags);
                break;
        }
    }

    public void stmt(Condition condition, int stmtType)
    {
        switch (token.get(0).getTag()) {
            case Tag.ID:
                assignStmt(condition, stmtType);
                pop();
                push(new Semantic(semanticObj.getType(), null, "stmt"));
                break;

            case Tag.IF:
                ifStmt(condition);
                pop();
                push(new Semantic(semanticObj.getType(), null, "stmt"));
                break;

            case Tag.DO:
                doStmt(condition);
                pop();
                push(new Semantic(semanticObj.getType(), null, "stmt"));
                break;

            case Tag.READ:
                readStmt(condition);
                pop();
                push(new Semantic(semanticObj.getType(), null, "stmt"));
                break;

            case Tag.WRITE:
                writeStmt(condition, stmtType);
                pop();
                push(new Semantic(semanticObj.getType(), null, "stmt"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "stmt"));
                int[] tags = {Tag.ID, Tag.IF, Tag.DO, Tag.READ, Tag.WRITE};
                errorParser("stmt", tags);
                break;
        }
    }

    public void assignStmt(Condition condition, int stmtType)
    {
        String value;
        switch (token.get(0).getTag()) {
            case Tag.ID:
                identifier();
                pop();
                value = semanticObj.getString();
                eat(Tag.ASSIGN, "assignStmt");
                simpleExpr(stmtType);
                pop();

                if (condition.evaluate())
                    do
                        setValue(value, semanticObj.getString());
                    while (condition.isRepeat() && condition.evaluate());

                push(new Semantic(Type.VOID, null, "assignStmt")); //TO-DO: Considerar tipo
                break;

            default:
                push(new Semantic(Type.ERROR, null, "assignStmt"));
                int[] tags = {Tag.ID};
                errorParser("assignStmt", tags);
                break;
        }
    }

    public void ifStmt(Condition condition)
    {
        switch (token.get(0).getTag()) {
            case Tag.IF:
                eat(Tag.IF, "ifStmt");
                eat(Tag.PAR_OPEN, "ifStmt");
                Condition aux = condition(Condition.tagIF);
                pop();
                eat(Tag.PAR_CLOSE, "ifStmt");
                eat(Tag.BEGIN, "ifStmt");
                stmtList(aux, Condition.tagIF);
                pop();
                eat(Tag.END, "ifStmt");
                if (token.get(0).getTag() == Tag.ELSE) {
                    eat(Tag.ELSE, "ifStmt");
                    eat(Tag.BEGIN, "ifStmt");
                    stmtList(aux.switchOperators(), Condition.tagIF);
                    pop();
                    eat(Tag.END, "ifStmt");
                }
                push(new Semantic(Type.VOID, semanticObj.getString(), "ifStmt"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "ifStmt"));
                int[] tags = {Tag.IF};
                errorParser("ifStmt", tags);
                break;
        }
    }

    public Condition condition(int stmtType)
    {
        Condition condition = null;

        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.NOT:
            case Tag.LITERAL:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                condition = expression(stmtType);
                pop();
                push(new Semantic(semanticObj.getType(), semanticObj.getString(), "condition"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "condition"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.ID, Tag.NOT, Tag.QUOTE, Tag.PAR_OPEN, Tag.SUBTRACT};
                errorParser("condition", tags);
                break;
        }

        return condition;
    }

    public void doStmt(Condition condition)
    {
        Semantic returnA;
        Semantic returnB;
        switch (token.get(0).getTag()) {
            case Tag.DO:
                eat(Tag.DO, "doStmt");
                stmtList(condition, Condition.tagWHILE);
                pop();
                returnA = semanticObj;
                doSuffix();
                pop();
                returnB = semanticObj;

                if (returnA.getType() == Type.VOID && returnB.getType() == Type.VOID)
                    push(new Semantic(Type.VOID, null, "doStmt"));
                else
                    push(new Semantic(Type.ERROR, null, "doStmt"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "doStmt"));
                int[] tags = {Tag.DO};
                errorParser("doStmt", tags);
                break;
        }
    }

    public Condition doSuffix()
    {
        Condition condition = null;

        switch (token.get(0).getTag()) {
            case Tag.WHILE:
                eat(Tag.WHILE, "doSuffix");
                eat(Tag.PAR_OPEN, "doSuffix");
                condition = condition(Condition.tagWHILE);
                eat(Tag.PAR_CLOSE, "doSuffix");
                pop();
                if (semanticObj.getType() == Type.BOOLEAN)
                    push(new Semantic(Type.VOID, null, "doSuffix"));
                else
                    push(new Semantic(Type.ERROR, null, "doSuffix"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "doSuffix"));
                int[] tags = {Tag.WHILE};
                errorParser("doSuffix", tags);
                break;
        }

        return condition;
    }

    public void readStmt(Condition condition)
    {
        switch (token.get(0).getTag()) {
            case Tag.READ:
                eat(Tag.READ, "readStmt");
                eat(Tag.PAR_OPEN, "readStmt");
                identifier();
                pop();
                eat(Tag.PAR_CLOSE, "readStmt");

                if (condition.evaluate()) {
                    System.out.print("Read: ");
                    String value = reader.nextLine();
                    verifyType(getType(semanticObj.getString()), value);
                    setValue(semanticObj.getString(), value);
                }
                push(new Semantic(Type.VOID, null, "readStmt"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "readStmt"));
                int[] tags = {Tag.READ};
                errorParser("readStmt", tags);
                break;
        }
    }

    public void writeStmt(Condition condition, int stmtType)
    {
        switch (token.get(0).getTag()) {
            case Tag.WRITE:
                eat(Tag.WRITE, "writeStmt");
                eat(Tag.PAR_OPEN, "writeStmt");
                writable(stmtType);
                pop();
                eat(Tag.PAR_CLOSE, "writeStmt");

                if (condition.evaluate())
                    if (semanticObj.getType() == Type.ID)
                        System.out.println("Write: " + getValue(semanticObj.getString()));
                    else
                        System.out.println("Write: " + semanticObj.getString());

                push(new Semantic(Type.VOID, null, "writeStmt"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "writeStmt"));
                int[] tags = {Tag.WRITE};
                errorParser("writeStmt", tags);
                break;
        }
    }

    public void writable(int stmtType)
    {
        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.NOT:
            case Tag.LITERAL:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                simpleExpr(stmtType);
                pop();
                push(new Semantic(semanticObj.getType(), semanticObj.getString(), "writable"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "writable"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.ID, Tag.NOT, Tag.LITERAL, Tag.PAR_OPEN, Tag.SUBTRACT};
                errorParser("writable", tags);
                break;
        }
    }

    public Condition expression(int stmtType)
    {
        Semantic aux;
        int tag;
        boolean idA = false;
        boolean idB = false;
        Condition condition = null;

        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.NOT:
            case Tag.LITERAL:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                simpleExpr(stmtType);
                pop();
                aux = semanticObj;

                if (token.get(0).getTag() >= Tag.EQUAL && token.get(0).getTag() <= Tag.NOT_EQUAL) {
                    tag = token.get(0).getTag();
                    relop();
                    pop();
                    simpleExpr(stmtType);
                    pop();

                    if (aux.getType() == Type.ID)
                        idA = true;
                    if (semanticObj.getType() == Type.ID)
                        idB = true;

                    condition = new Condition(aux.getString(), idA, semanticObj.getString(), idB, stmtType, tag);
                }
                push(new Semantic(aux.getType(), aux.getString(), "expression"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "expression"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.ID, Tag.NOT, Tag.LITERAL, Tag.PAR_OPEN, Tag.SUBTRACT};
                errorParser("expression", tags);
                break;
        }

        return condition;
    }

    public void simpleExpr(int stmtType)
    {
        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.NOT:
            case Tag.LITERAL:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                term(stmtType);
                pop();
                simpleExprZ(semanticObj, stmtType);
                pop();
                push(new Semantic(semanticObj.getType(), semanticObj.getString(), "simpleExpr"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "simpleExpr"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.ID, Tag.NOT, Tag.LITERAL, Tag.PAR_OPEN, Tag.SUBTRACT};
                errorParser("simpleExpr", tags);
                break;
        }
    }

    public void simpleExprZ(Semantic valueA, int stmtType)
    {
        int intValue;
        boolean boolValue;
        Semantic valueB;
        Semantic aux = null;
        String A = null;
        String B = null;

        switch (token.get(0).getTag()) {
            case Tag.OR:
                addop();
                pop();
                term(stmtType);
                pop();
                valueB = semanticObj;

                if (valueA.getType() == Type.BOOLEAN && valueB.getType() == Type.BOOLEAN) {
                    boolValue = valueA.getBool() || valueB.getBool();
                    aux = new Semantic(Type.BOOLEAN, getBool(boolValue), "simpleExprZ");
                    push(aux);
                } else
                    push(new Semantic(Type.ERROR, null, "simpleExprZ"));

                simpleExprZ(aux, stmtType);
                break;

            case Tag.SUM:
                addop();
                pop();
                term(stmtType);
                pop();
                valueB = semanticObj;

                if (!error()) {
                    if (valueA.getType() == Type.ID)
                        A = getValue(valueA.getString());
                    else if (valueA.getType() == Type.INTEGER)
                        A = valueA.getString();
                    if (valueB.getType() == Type.ID)
                        B = getValue(valueB.getString());
                    else if (valueB.getType() == Type.INTEGER)
                        B = valueB.getString();
                }

                if (A != null && B != null) {
                    intValue = Integer.valueOf(A) + Integer.valueOf(B);
                    aux = new Semantic(Type.INTEGER, String.valueOf(intValue), "simpleExprZ");
                    push(aux);
                } else
                    push(new Semantic(Type.ERROR, null, "simpleExprZ"));

                simpleExprZ(aux, stmtType);
                break;

            case Tag.SUBTRACT:
                addop();
                pop();
                term(stmtType);
                pop();
                valueB = semanticObj;

                if (!error()) {
                    if (valueA.getType() == Type.ID)
                        A = getValue(valueA.getString());
                    else if (valueA.getType() == Type.INTEGER)
                        A = valueA.getString();
                    if (valueB.getType() == Type.ID)
                        B = getValue(valueB.getString());
                    else if (valueB.getType() == Type.INTEGER)
                        B = valueB.getString();
                }

                if (A != null && B != null) {
                    intValue = Integer.valueOf(A) - Integer.valueOf(B);
                    aux = new Semantic(Type.INTEGER, String.valueOf(intValue), "simpleExprZ");
                    push(aux);
                } else
                    push(new Semantic(Type.ERROR, null, "simpleExprZ"));

                simpleExprZ(aux, stmtType);
                break;

            case Tag.PAR_CLOSE:
            case Tag.EQUAL:
            case Tag.GREATER:
            case Tag.GREATER_EQUAL:
            case Tag.LOWER:
            case Tag.LOWER_EQUAL:
            case Tag.NOT_EQUAL:
            case Tag.DOT_COM:
                if (valueA != null)
                    push(new Semantic(valueA.getType(), valueA.getString(), "simpleExprZ"));
                else
                    push(new Semantic(Type.ERROR, null, "simpleExprZ"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "simpleExprZ"));
                int[] tags = {Tag.OR, Tag.SUM, Tag.SUBTRACT, Tag.PAR_CLOSE, Tag.EQUAL, Tag.GREATER, Tag.GREATER_EQUAL, Tag.LOWER, Tag.LOWER_EQUAL, Tag.NOT_EQUAL, Tag.DOT_COM};
                errorParser("simpleExprZ", tags);
                break;
        }
    }

    public void term(int stmtType)
    {
        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.NOT:
            case Tag.LITERAL:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                factorA(stmtType);
                pop();
                termZ(semanticObj, stmtType);
                pop();
                push(new Semantic(semanticObj.getType(), semanticObj.getString(), "term"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "term"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.ID, Tag.NOT, Tag.LITERAL, Tag.PAR_OPEN, Tag.SUBTRACT};
                errorParser("term", tags);
                break;
        }
    }

    public void termZ(Semantic valueA, int stmtType)
    {
        int intValue;
        boolean boolValue;
        Semantic valueB;
        Semantic aux = null;
        String A = null;
        String B = null;

        switch (token.get(0).getTag()) {
            case Tag.AND:
                mulop();
                pop();
                factorA(stmtType);
                pop();
                valueB = semanticObj;

                if (!error() && valueA.getType() == Type.BOOLEAN && valueB.getType() == Type.BOOLEAN) {
                    boolValue = valueA.getBool() && valueB.getBool();
                    aux = new Semantic(Type.BOOLEAN, getBool(boolValue), "termZ");
                    push(aux);
                } else
                    push(new Semantic(Type.ERROR, null, "termZ"));

                termZ(aux, stmtType);
                break;

            case Tag.MULTIPLY:
                mulop();
                pop();
                factorA(stmtType);
                pop();
                valueB = semanticObj;

                if (valueA.getType() == Type.ID)
                    A = getValue(valueA.getString());
                else if (valueA.getType() == Type.INTEGER)
                    A = valueA.getString();
                if (valueB.getType() == Type.ID)
                    B = getValue(valueB.getString());
                else if (valueB.getType() == Type.INTEGER)
                    B = valueB.getString();

                if (A != null && B != null) {
                    intValue = Integer.valueOf(A) * Integer.valueOf(B);
                    aux = new Semantic(Type.INTEGER, String.valueOf(intValue), "termZ");
                    push(aux);
                } else
                    push(new Semantic(Type.ERROR, null, "termZ"));

                termZ(aux, stmtType);
                break;

            case Tag.DIVIDE:
                mulop();
                pop();
                factorA(stmtType);
                pop();
                valueB = semanticObj;

                if (!error() && valueA.getType() == Type.INTEGER && valueB.getType() == Type.INTEGER) {
                    intValue = valueA.getInt() / valueB.getInt();
                    aux = new Semantic(Type.INTEGER, String.valueOf(intValue), "termZ");
                    push(aux);
                } else
                    push(new Semantic(Type.ERROR, null, "termZ"));

                termZ(aux, stmtType);
                break;

            case Tag.OR:
            case Tag.PAR_CLOSE:
            case Tag.SUM:
            case Tag.SUBTRACT:
            case Tag.EQUAL:
            case Tag.GREATER:
            case Tag.GREATER_EQUAL:
            case Tag.LOWER:
            case Tag.LOWER_EQUAL:
            case Tag.NOT_EQUAL:
            case Tag.DOT_COM:
                if (valueA != null)
                    push(new Semantic(valueA.getType(), valueA.getString(), "termZ"));
                else
                    push(new Semantic(Type.ERROR, null, "termZ"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "termZ"));
                int[] tags = {Tag.AND, Tag.MULTIPLY, Tag.DIVIDE, Tag.OR, Tag.PAR_CLOSE, Tag.SUM, Tag.SUBTRACT, Tag.EQUAL, Tag.GREATER, Tag.GREATER_EQUAL, Tag.LOWER, Tag.LOWER_EQUAL, Tag.NOT_EQUAL,
                    Tag.DOT_COM};
                errorParser("termZ", tags);
                break;
        }
    }

    public void factorA(int stmtType)
    {
        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.LITERAL:
            case Tag.PAR_OPEN:
                factor(stmtType);
                pop();
                push(new Semantic(semanticObj.getType(), semanticObj.getString(), "factorA"));
                break;

            case Tag.NOT:
                eat(Tag.NOT, "factorA");
                factor(stmtType);
                pop();

                if (semanticObj.getType() == Type.BOOLEAN) {
                    boolean value = !semanticObj.getBool();
                    push(new Semantic(semanticObj.getType(), getBool(value), "factorA"));
                } else
                    push(new Semantic(Type.ERROR, null, "factor"));
                break;

            case Tag.SUBTRACT:
                eat(Tag.SUBTRACT, "factorA");
                factor(stmtType);
                pop();
                if (semanticObj.getType() == Type.INTEGER) {
                    Integer value = -Integer.parseInt(semanticObj.getString());
                    push(new Semantic(semanticObj.getType(), value.toString(), "factorA"));
                } else
                    push(new Semantic(Type.ERROR, null, "factorA"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "factorA"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.ID, Tag.LITERAL, Tag.PAR_OPEN, Tag.NOT, Tag.SUBTRACT};
                errorParser("factorA", tags);
                break;
        }
    }

    public Condition factor(int stmtType)
    {
        Condition condition = null;

        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
            case Tag.LITERAL:
                constant();
                pop();
                push(new Semantic(semanticObj.getType(), semanticObj.getString(), "factor"));
                break;

            case Tag.ID:
                identifier();
                pop();
                push(new Semantic(semanticObj.getType(), semanticObj.getString(), "factor"));
                break;

            case Tag.PAR_OPEN:
                eat(Tag.PAR_OPEN, "factor");
                condition = expression(stmtType);
                eat(Tag.PAR_CLOSE, "factor");
                pop();
                push(new Semantic(semanticObj.getType(), semanticObj.getString(), "factor"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "factor"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.LITERAL, Tag.ID, Tag.PAR_OPEN};
                errorParser("factor", tags);
                break;
        }

        return condition;
    }

    public void relop()
    {
        switch (token.get(0).getTag()) {
            case Tag.EQUAL:
                push(new Semantic(Type.VOID, String.valueOf(Tag.EQUAL), "relop"));
                eat(Tag.EQUAL, "relop");
                break;

            case Tag.GREATER:
                push(new Semantic(Type.VOID, String.valueOf(Tag.GREATER), "relop"));
                eat(Tag.GREATER, "relop");
                break;

            case Tag.GREATER_EQUAL:
                push(new Semantic(Type.VOID, String.valueOf(Tag.GREATER_EQUAL), "relop"));
                eat(Tag.GREATER_EQUAL, "relop");
                break;

            case Tag.LOWER:
                push(new Semantic(Type.VOID, String.valueOf(Tag.LOWER), "relop"));
                eat(Tag.LOWER, "relop");
                break;

            case Tag.LOWER_EQUAL:
                push(new Semantic(Type.VOID, String.valueOf(Tag.LOWER_EQUAL), "relop"));
                eat(Tag.LOWER_EQUAL, "relop");
                break;

            case Tag.NOT_EQUAL:
                push(new Semantic(Type.VOID, String.valueOf(Tag.NOT_EQUAL), "relop"));
                eat(Tag.NOT_EQUAL, "relop");
                break;

            default:
                push(new Semantic(Type.ERROR, null, "relop"));
                int[] tags = {Tag.EQUAL, Tag.GREATER, Tag.GREATER_EQUAL, Tag.LOWER, Tag.LOWER_EQUAL, Tag.NOT_EQUAL};
                errorParser("relop", tags);
                break;
        }
    }

    public void addop()
    {
        switch (token.get(0).getTag()) {
            case Tag.OR:
                push(new Semantic(Type.VOID, String.valueOf(Tag.OR), "addop"));
                eat(Tag.OR, "addop");
                break;

            case Tag.SUM:
                push(new Semantic(Type.VOID, String.valueOf(Tag.SUM), "addop"));
                eat(Tag.SUM, "addop");
                break;

            case Tag.SUBTRACT:
                push(new Semantic(Type.VOID, String.valueOf(Tag.SUBTRACT), "addop"));
                eat(Tag.SUBTRACT, "addop");
                break;

            default:
                push(new Semantic(Type.ERROR, null, "addop"));
                int[] tags = {Tag.OR, Tag.SUM, Tag.SUBTRACT};
                errorParser("addop", tags);
                break;
        }
    }

    public void mulop()
    {
        switch (token.get(0).getTag()) {
            case Tag.AND:
                push(new Semantic(Type.VOID, String.valueOf(Tag.AND), "mulop"));
                eat(Tag.AND, "mulop");
                break;

            case Tag.MULTIPLY:
                push(new Semantic(Type.VOID, String.valueOf(Tag.MULTIPLY), "mulop"));
                eat(Tag.MULTIPLY, "mulop");
                break;

            case Tag.DIVIDE:
                push(new Semantic(Type.VOID, String.valueOf(Tag.DIVIDE), "mulop"));
                eat(Tag.DIVIDE, "mulop");
                break;

            default:
                push(new Semantic(Type.ERROR, null, "mulop"));
                int[] tags = {Tag.AND, Tag.MULTIPLY, Tag.DIVIDE};
                errorParser("mulop", tags);
                break;
        }
    }

    public void constant()
    {
        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
                integerConst();
                pop();
                push(new Semantic(Type.INTEGER, semanticObj.getString(), "constant"));
                break;

            case Tag.LITERAL:
                literal();
                pop();
                push(new Semantic(Type.STRING, semanticObj.getString(), "constant"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "constant"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.LITERAL};
                errorParser("constant", tags);
                break;
        }
    }

    public void integerConst()
    {
        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
                push(new Semantic(Type.INTEGER, token.get(0).toString(), "integerConst"));
                eat(Tag.CONST_ZERO, "integerConst");
                break;

            case Tag.CONST_NOT_ZERO:
                Integer value;

                noZero();
                pop();
                value = semanticObj.getInt();

                while (token.get(0).getTag() == Tag.CONST_NOT_ZERO || token.get(0).getTag() == Tag.CONST_ZERO) {
                    digit();
                    pop();
                    value = value + semanticObj.getInt();
                }

                push(new Semantic(Type.INTEGER, value.toString(), "integerConst"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "integerConst"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO};
                errorParser("integerConst", tags);
                break;
        }
    }

    public void literal()
    {
        switch (token.get(0).getTag()) {
            case Tag.LITERAL:
                push(new Semantic(Type.STRING, token.get(0).toString(), "literal"));
                eat(Tag.LITERAL, "literal");
                break;

            default:
                push(new Semantic(Type.ERROR, null, "literal"));
                int[] tags = {Tag.QUOTE};
                errorParser("literal", tags);
                break;
        }
    }

    public void identifier()
    {
        switch (token.get(0).getTag()) {
            case Tag.ID:
                String stringValue = "";

                letter();
                pop();
                stringValue = stringValue + semanticObj.getString();

                while (token.get(0).getTag() == Tag.ID || token.get(0).getTag() == Tag.INTEGER || token.get(0).getTag() == '_')
                    if (token.get(0).getTag() == Tag.ID) {
                        letter();
                        pop();
                        stringValue.concat(semanticObj.getString());
                    } else if (token.get(0).getTag() == Tag.INTEGER) {
                        digit();
                        pop();
                        stringValue.concat(semanticObj.getString());
                    } else {
                        stringValue.concat(token.get(0).toString());
                        eat('_', "identifier");
                    }

                push(new Semantic(Type.ID, stringValue, "identifier"));
                break;

            default:
                push(new Semantic(Type.ERROR, null, "identifier"));

                int[] tags = {Tag.ID};
                errorParser("identifier", tags);
                break;
        }
    }

    public void letter()
    {
        switch (token.get(0).getTag()) {
            case Tag.ID:
                push(new Semantic(Type.STRING, token.get(0).toString(), "letter"));
                eat(Tag.ID, "letter");
                break;

            default:
                push(new Semantic(Type.ERROR, null, "letter"));
                int[] tags = {Tag.ID};
                errorParser("letter", tags);
                break;
        }
    }

    public void digit()
    {
        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
                push(new Semantic(Type.INTEGER, token.get(0).toString(), "digit"));
                eat(Tag.INTEGER, "digit");
                break;

            default:
                push(new Semantic(Type.ERROR, null, "digit"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO};
                errorParser("digit", tags);
                break;
        }
    }

    public void noZero()
    {
        switch (token.get(0).getTag()) {
            case Tag.CONST_NOT_ZERO:
                push(new Semantic(Type.INTEGER, token.get(0).toString(), "noZero"));
                eat(Tag.CONST_NOT_ZERO, "noZero");
                break;

            default:
                push(new Semantic(Type.ERROR, null, "noZero"));
                int[] tags = {Tag.CONST_NOT_ZERO};
                errorParser("noZero", tags);
                break;
        }
    }

    public void caractere()
    {
        if (isAscii(token.get(0))) {
            push(new Semantic(Type.VOID, token.get(0).toString(), "caractere"));
            eat(Tag.ID, "caractere");
        } else {
            push(new Semantic(Type.ERROR, null, "caractere"));
            int[] tags = {Tag.CONST_ASCII};
            errorParser("caractere", tags);
        }
    }

    public boolean isAscii(Token token)
    {
        if (token.getTag() >= 0 && token.getTag() <= 127 && token.getTag() != (int) '"' && token.getTag() != (int) '\n')
            return true;
        return false;
    }

    public void pop()
    {
        if (!error())
            semanticObj = (Semantic) semanticStack.pop();
    }

    public void push(Semantic semantic)
    {
        if (!error())
            semanticStack.push(semantic);
    }

    public String getValue(String value)
    {
        if (!error())
            return Word.getValue(value);
        return null;
    }

    public int getType(String lexem)
    {
        if (!error())
            return Word.getType(lexem);
        return Type.ERROR;
    }

    public void setType(String lexem, int type)
    {
        if (!error())
            Word.setType(lexem, type);
    }

    public void setValue(String lexem, String value)
    {
        if (!error())
            Word.setValue(lexem, value);
    }

    public String getBool(boolean value)
    {
        if (value)
            return ("true");
        else
            return ("false");
    }

    public boolean error()
    {
        return (Parser.error || Semantic.error);
    }

    public void verifyType(int type, String value)
    {
        if (type == Type.INTEGER)
            try {
                int number = Integer.parseInt(value);
            } catch (Exception e) {
                Semantic.errorType("INTEGER", "STRING");
            }
    }

    public void verifyUnicity(ArrayList<String> list)
    {
        for (int i = 0; i < list.size(); i++)
            for (int j = i + 1; j < list.size(); j++)
                if (list.get(i).equals(list.get(j)))
                    Semantic.errorUnicity(list.get(j));
    }

    public void printResults()
    {
        if (!Parser.error) {
            System.out.println("SINTÁTICO: Código compilado com sucesso!");

            if (!Semantic.error)
                System.out.println("SEMÂNTICO: Código compilado com sucesso!");
            else
                System.out.println("SEMÂNTICO: Código com erro!");
        } else
            System.out.println("SINTÁTICO: Código com erro!");
    }
}
