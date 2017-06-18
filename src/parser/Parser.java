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
    private boolean error = false; //Se false ao final da análise, não há erro de compilação

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

        if (!error)
            System.out.println("Código compilado sem erro sintático! :D");
        else
            System.out.println("\nParece que há alguns erros sintáticos no código. :(");
    }

    private void getToken()
    {
        token.add(lexer.run(fileName));
    }

    public void error(String name, int[] tag)
    {
        //Mensagem de erro
        System.out.print("Erro na linha " + Lexer.line + " no reconhecimento de " + name + ".\n\tToken esperado: ");
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

        if (token.get(0).getTag() == Tag.EOF)
            System.exit(0);
    }

    public void eat(int tag, String name)
    {
        if (token.get(0).getTag() == tag) {
            token.remove(0);
            getToken();
        } else {
            int[] tags = {tag};
            error(name, tags);
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
                        stmtList();
                        pop();
                        eat(Tag.STOP, "program");
                        //Se o token seguinte for ':='
                    } else if (token.get(1).getTag() == Tag.ASSIGN) {
                        stmtList();
                        pop();
                        eat(Tag.STOP, "program");
                    } else {
                        int[] tags = {Tag.COM, Tag.IS, Tag.ASSIGN};
                        error("program-case-init", tags);
                    }
                }
                break;

            default:
                int[] tags = {Tag.INIT};
                error("program", tags);
                break;
        }
    }

    public void declList()
    {
        switch (token.get(0).getTag()) {
            case Tag.ID:
                decl();
                pop();
                semanticStack.push(new Semantic(semanticObj.getType(), null, "declList"));
                eat(Tag.DOT_COM, "declList");
                while (token.get(0).getTag() == Tag.ID) {
                    decl();
                    pop();
                    semanticStack.push(new Semantic(semanticObj.getType(), null, "declList"));
                    eat(Tag.DOT_COM, "declList");
                }
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "declList"));
                int[] tags = {Tag.ID};
                error("declList", tags);
                break;
        }
    }

    public void decl()
    {
        switch (token.get(0).getTag()) {
            case Tag.ID:
                ArrayList<String> valueList = identList();
                pop();
                eat(Tag.IS, "decl");
                type();
                pop();

                for (int i = 0; i < valueList.size(); i++)
                    Word.setType(valueList.get(i), semanticObj.getType());

                semanticStack.push(new Semantic(Type.VOID, null, "decl"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "decl"));
                int[] tags = {Tag.ID};
                error("decl", tags);
                break;
        }
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
                semanticStack.push(new Semantic(Type.VOID, null, "identList"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "identList"));
                int[] tags = {Tag.ID};
                error("identList", tags);
                break;
        }
        return valueList;
    }

    public void type()
    {
        switch (token.get(0).getTag()) {
            case Tag.INTEGER:
                semanticStack.push(new Semantic(Type.INTEGER, null, "type"));
                eat(Tag.INTEGER, "type");
                break;

            case Tag.STRING:
                semanticStack.push(new Semantic(Type.STRING, null, "type"));
                eat(Tag.STRING, "type");
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "type"));
                int[] tags = {Tag.INTEGER, Tag.STRING};
                error("type", tags);
                break;
        }
    }

    public void stmtList()
    {
        switch (token.get(0).getTag()) {
            case Tag.ID:
            case Tag.DO:
            case Tag.IF:
            case Tag.READ:
            case Tag.WRITE:
                stmt();
                pop();
                semanticStack.push(new Semantic(semanticObj.getType(), null, "stmtList"));
                eat(Tag.DOT_COM, "stmtList");
                while (token.get(0).getTag() == Tag.ID || token.get(0).getTag() == Tag.DO || token.get(0).getTag() == Tag.IF || token.get(0).getTag() == Tag.READ || token.get(0).getTag() == Tag.WRITE) {
                    stmt();
                    pop();
                    semanticStack.push(new Semantic(semanticObj.getType(), null, "stmtList"));
                    eat(Tag.DOT_COM, "stmtList");
                }
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "stmtList"));
                int[] tags = {Tag.ID, Tag.DO, Tag.IF, Tag.READ, Tag.WRITE};
                error("stmtList", tags);
                break;
        }
    }

    public void stmt()
    {
        switch (token.get(0).getTag()) {
            case Tag.ID:
                assignStmt();
                pop();
                semanticStack.push(new Semantic(semanticObj.getType(), null, "stmt"));
                break;

            case Tag.IF:
                ifStmt();
                pop();
                semanticStack.push(new Semantic(semanticObj.getType(), null, "stmt"));
                break;

            case Tag.DO:
                doStmt();
                pop();
                semanticStack.push(new Semantic(semanticObj.getType(), null, "stmt"));
                break;

            case Tag.READ:
                readStmt();
                pop();
                semanticStack.push(new Semantic(semanticObj.getType(), null, "stmt"));
                break;

            case Tag.WRITE:
                writeStmt();
                pop();
                semanticStack.push(new Semantic(semanticObj.getType(), null, "stmt"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "stmt"));
                int[] tags = {Tag.ID, Tag.IF, Tag.DO, Tag.READ, Tag.WRITE};
                error("stmt", tags);
                break;
        }
    }

    public void assignStmt()
    {
        String value;
        switch (token.get(0).getTag()) {
            case Tag.ID:
                identifier();
                pop();
                value = semanticObj.getString();
                eat(Tag.ASSIGN, "assignStmt");
                simpleExpr();
                pop();
                Word.setValue(value, semanticObj.getString());
                semanticStack.push(new Semantic(Type.VOID, null, "assignStmt")); //TO-DO: Considerar tipo
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "assignStmt"));
                int[] tags = {Tag.ID};
                error("assignStmt", tags);
                break;
        }
    }

    public void ifStmt()
    {
        switch (token.get(0).getTag()) {
            case Tag.IF:
                eat(Tag.IF, "ifStmt");
                eat(Tag.PAR_OPEN, "ifStmt");
                condition();
                pop(); //TO-DO: Considerar condição do if
                eat(Tag.PAR_CLOSE, "ifStmt");
                eat(Tag.BEGIN, "ifStmt");
                stmtList();
                pop(); //TO-DO: Considerar condição do if
                eat(Tag.END, "ifStmt");
                if (token.get(0).getTag() == Tag.ELSE) {
                    eat(Tag.ELSE, "ifStmt");
                    eat(Tag.BEGIN, "ifStmt");
                    stmtList();
                    pop(); //TO-DO: Considerar condição do if
                    eat(Tag.END, "ifStmt");
                }
                semanticStack.push(new Semantic(Type.VOID, semanticObj.getString(), "ifStmt")); //TO-DO: Considerar condição do if
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "ifStmt"));
                int[] tags = {Tag.IF};
                error("ifStmt", tags);
                break;
        }
    }

    public void condition()
    {
        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.NOT:
            case Tag.QUOTE:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                expression();
                pop();
                semanticStack.push(new Semantic(semanticObj.getType(), semanticObj.getString(), "condition"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "condition"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.ID, Tag.NOT, Tag.QUOTE, Tag.PAR_OPEN, Tag.SUBTRACT};
                error("condition", tags);
                break;
        }
    }

    public void doStmt()
    {
        Semantic returnA;
        Semantic returnB;
        switch (token.get(0).getTag()) {
            case Tag.DO:
                eat(Tag.DO, "doStmt");
                stmtList();
                pop();
                returnA = semanticObj;
                doSuffix();
                pop();
                returnB = semanticObj;

                if (returnA.getType() == Type.VOID && returnB.getType() == Type.VOID)
                    semanticStack.push(new Semantic(Type.VOID, null, "doStmt"));
                else
                    semanticStack.push(new Semantic(Type.ERROR, null, "doStmt"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "doStmt"));
                int[] tags = {Tag.DO};
                error("doStmt", tags);
                break;
        }
    }

    public void doSuffix()
    {
        switch (token.get(0).getTag()) {
            case Tag.WHILE:
                eat(Tag.WHILE, "doSuffix");
                eat(Tag.PAR_OPEN, "doSuffix");
                condition();
                eat(Tag.PAR_CLOSE, "doSuffix");
                pop();
                if (semanticObj.getType() == Type.BOOLEAN)
                    semanticStack.push(new Semantic(Type.VOID, null, "doSuffix"));
                else
                    semanticStack.push(new Semantic(Type.ERROR, null, "doSuffix"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "doSuffix"));
                int[] tags = {Tag.WHILE};
                error("doSuffix", tags);
                break;
        }
    }

    public void readStmt()
    {
        switch (token.get(0).getTag()) {
            case Tag.READ:
                eat(Tag.READ, "readStmt");
                eat(Tag.PAR_OPEN, "readStmt");
                identifier();
                pop();
                eat(Tag.PAR_CLOSE, "readStmt");

                System.out.print("Read: ");
                String value = reader.nextLine();
                Word.setValue(semanticObj.getString(), value);
                semanticStack.push(new Semantic(Type.VOID, null, "readStmt"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "readStmt"));
                int[] tags = {Tag.READ};
                error("readStmt", tags);
                break;
        }
    }

    public void writeStmt()
    {
        switch (token.get(0).getTag()) {
            case Tag.WRITE:
                eat(Tag.WRITE, "writeStmt");
                eat(Tag.PAR_OPEN, "writeStmt");
                writable();
                pop();
                eat(Tag.PAR_CLOSE, "writeStmt");

                if (semanticObj.getType() == Type.ID)
                    System.out.println("Write: " + Word.getValue(semanticObj.getString()));
                else
                    System.out.println("Write: " + semanticObj.getString());

                semanticStack.push(new Semantic(Type.VOID, null, "writeStmt"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "writeStmt"));
                int[] tags = {Tag.WRITE};
                error("writeStmt", tags);
                break;
        }
    }

    public void writable()
    {
        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.NOT:
            case Tag.QUOTE:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                simpleExpr();
                pop();
                semanticStack.push(new Semantic(semanticObj.getType(), semanticObj.getString(), "writable"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "writable"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.ID, Tag.NOT, Tag.QUOTE, Tag.PAR_OPEN, Tag.SUBTRACT};
                error("writable", tags);
                break;
        }
    }

    public void expression()
    {
        Semantic aux;
        int tag;
        int comparison;
        boolean bool;

        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.NOT:
            case Tag.QUOTE:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                simpleExpr();
                pop();
                aux = semanticObj;

                if (token.get(0).getTag() >= Tag.EQUAL && token.get(0).getTag() <= Tag.NOT_EQUAL) {
                    tag = token.get(0).getTag();
                    relop();
                    pop();
                    simpleExpr();
                    pop();
                    switch (tag) {
                        case Tag.EQUAL:
                            bool = aux.getString() == semanticObj.getString();
                            aux = new Semantic(Type.BOOLEAN, getBool(bool), "expression");
                            break;

                        case Tag.GREATER:
                            comparison = aux.getString().compareTo(semanticObj.getString());
                            if (comparison > 0)
                                bool = true;
                            else
                                bool = false;

                            aux = new Semantic(Type.BOOLEAN, getBool(bool), "expression");
                            break;

                        case Tag.GREATER_EQUAL:
                            comparison = aux.getString().compareTo(semanticObj.getString());
                            if (comparison >= 0)
                                bool = true;
                            else
                                bool = false;

                            aux = new Semantic(Type.BOOLEAN, getBool(bool), "expression");
                            break;

                        case Tag.LOWER:
                            comparison = aux.getString().compareTo(semanticObj.getString());
                            if (comparison < 0)
                                bool = true;
                            else
                                bool = false;

                            aux = new Semantic(Type.BOOLEAN, getBool(bool), "expression");
                            break;

                        case Tag.LOWER_EQUAL:
                            comparison = aux.getString().compareTo(semanticObj.getString());
                            if (comparison <= 0)
                                bool = true;
                            else
                                bool = false;

                            aux = new Semantic(Type.BOOLEAN, getBool(bool), "expression");
                            break;

                        case Tag.NOT_EQUAL:
                            bool = aux.getString() != semanticObj.getString();
                            aux = new Semantic(Type.BOOLEAN, getBool(bool), "expression");
                            break;
                    }
                }
                semanticStack.push(new Semantic(aux.getType(), aux.getString(), "expression"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "expression"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.ID, Tag.NOT, Tag.QUOTE, Tag.PAR_OPEN, Tag.SUBTRACT};
                error("expression", tags);
                break;
        }
    }

    public void simpleExpr()
    {
        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.NOT:
            case Tag.QUOTE:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                term();
                pop();
                simpleExprZ(semanticObj);
                pop();
                semanticStack.push(new Semantic(semanticObj.getType(), semanticObj.getString(), "simpleExpr"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "simpleExpr"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.ID, Tag.NOT, Tag.QUOTE, Tag.PAR_OPEN, Tag.SUBTRACT};
                error("simpleExpr", tags);
                break;
        }
    }

    public void simpleExprZ(Semantic valueA)
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
                term();
                pop();
                valueB = semanticObj;

                if (valueA.getType() == Type.BOOLEAN && valueB.getType() == Type.BOOLEAN) {
                    boolValue = Boolean.getBoolean(valueA.getString()) || Boolean.getBoolean(valueB.getString());
                    aux = new Semantic(Type.BOOLEAN, getBool(boolValue), "simpleExprZ");
                    semanticStack.push(aux);
                } else
                    semanticStack.push(new Semantic(Type.ERROR, null, "simpleExprZ"));

                simpleExprZ(aux);
                break;

            case Tag.SUM:
                addop();
                pop();
                term();
                pop();
                valueB = semanticObj;

                if (valueA.getType() == Type.ID)
                    A = Word.getValue(valueA.getString());
                else if (valueA.getType() == Type.INTEGER)
                    A = valueA.getString();
                if (valueB.getType() == Type.ID)
                    B = Word.getValue(valueB.getString());
                else if (valueB.getType() == Type.INTEGER)
                    B = valueB.getString();

                if (A != null && B != null) {
                    intValue = Integer.valueOf(A) + Integer.valueOf(B);
                    aux = new Semantic(Type.INTEGER, String.valueOf(intValue), "simpleExprZ");
                    semanticStack.push(aux);
                } else
                    semanticStack.push(new Semantic(Type.ERROR, null, "simpleExprZ"));

                simpleExprZ(aux);
                break;

            case Tag.SUBTRACT:
                addop();
                pop();
                term();
                pop();
                valueB = semanticObj;

                if (valueA.getType() == Type.ID)
                    A = Word.getValue(valueA.getString());
                else if (valueA.getType() == Type.INTEGER)
                    A = valueA.getString();
                if (valueB.getType() == Type.ID)
                    B = Word.getValue(valueB.getString());
                else if (valueB.getType() == Type.INTEGER)
                    B = valueB.getString();

                if (A != null && B != null) {
                    intValue = Integer.valueOf(A) - Integer.valueOf(B);
                    aux = new Semantic(Type.INTEGER, String.valueOf(intValue), "simpleExprZ");
                    semanticStack.push(aux);
                } else
                    semanticStack.push(new Semantic(Type.ERROR, null, "simpleExprZ"));

                simpleExprZ(aux);
                break;

            case Tag.PAR_CLOSE:
            case Tag.EQUAL:
            case Tag.GREATER:
            case Tag.GREATER_EQUAL:
            case Tag.LOWER:
            case Tag.LOWER_EQUAL:
            case Tag.NOT_EQUAL:
            case Tag.DOT_COM:
                semanticStack.push(new Semantic(valueA.getType(), valueA.getString(), "simpleExprZ"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "simpleExprZ"));
                int[] tags = {Tag.OR, Tag.SUM, Tag.SUBTRACT, Tag.PAR_CLOSE, Tag.EQUAL, Tag.GREATER, Tag.GREATER_EQUAL, Tag.LOWER, Tag.LOWER_EQUAL, Tag.NOT_EQUAL, Tag.DOT_COM};
                error("simpleExprZ", tags);
                break;
        }
    }

    public void term()
    {
        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.NOT:
            case Tag.QUOTE:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                factorA();
                pop();
                termZ(semanticObj);
                pop();
                semanticStack.push(new Semantic(semanticObj.getType(), semanticObj.getString(), "term"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "term"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.ID, Tag.NOT, Tag.QUOTE, Tag.PAR_OPEN, Tag.SUBTRACT};
                error("term", tags);
                break;
        }
    }

    public void termZ(Semantic valueA)
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
                factorA();
                pop();
                valueB = semanticObj;

                if (valueA.getType() == Type.BOOLEAN && valueB.getType() == Type.BOOLEAN) {
                    boolValue = Boolean.getBoolean(valueA.getString()) && Boolean.getBoolean(valueB.getString());
                    aux = new Semantic(Type.BOOLEAN, getBool(boolValue), "termZ");
                    semanticStack.push(aux);
                } else
                    semanticStack.push(new Semantic(Type.ERROR, null, "termZ"));

                termZ(aux);
                break;

            case Tag.MULTIPLY:
                mulop();
                pop();
                factorA();
                pop();
                valueB = semanticObj;

                if (valueA.getType() == Type.ID)
                    A = Word.getValue(valueA.getString());
                else if (valueA.getType() == Type.INTEGER)
                    A = valueA.getString();
                if (valueB.getType() == Type.ID)
                    B = Word.getValue(valueB.getString());
                else if (valueB.getType() == Type.INTEGER)
                    B = valueB.getString();

                if (A != null && B != null) {
                    intValue = Integer.valueOf(A) * Integer.valueOf(B);
                    aux = new Semantic(Type.INTEGER, String.valueOf(intValue), "termZ");
                    semanticStack.push(aux);
                } else
                    semanticStack.push(new Semantic(Type.ERROR, null, "termZ"));

                termZ(aux);
                break;

            case Tag.DIVIDE:
                mulop();
                pop();
                factorA();
                pop();
                valueB = semanticObj;

                if (valueA.getType() == Type.INTEGER && valueB.getType() == Type.INTEGER) {
                    intValue = valueA.getInt() / valueB.getInt();
                    aux = new Semantic(Type.INTEGER, String.valueOf(intValue), "termZ");
                    semanticStack.push(aux);
                } else
                    semanticStack.push(new Semantic(Type.ERROR, null, "termZ"));

                termZ(aux);
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
                semanticStack.push(new Semantic(valueA.getType(), valueA.getString(), "termZ"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "termZ"));
                int[] tags = {Tag.AND, Tag.MULTIPLY, Tag.DIVIDE, Tag.OR, Tag.PAR_CLOSE, Tag.SUM, Tag.SUBTRACT, Tag.EQUAL, Tag.GREATER, Tag.GREATER_EQUAL, Tag.LOWER, Tag.LOWER_EQUAL, Tag.NOT_EQUAL,
                    Tag.DOT_COM};
                error("termZ", tags);
                break;
        }
    }

    public void factorA()
    {
        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.QUOTE:
            case Tag.PAR_OPEN:
                factor();
                pop();
                semanticStack.push(new Semantic(semanticObj.getType(), semanticObj.getString(), "factorA"));
                break;

            case Tag.NOT:
                eat(Tag.NOT, "factorA");
                factor();
                pop();

                if (semanticObj.getType() == Type.BOOLEAN) {
                    boolean value = !Boolean.getBoolean(semanticObj.getString());
                    semanticStack.push(new Semantic(semanticObj.getType(), getBool(value), "factorA"));
                } else
                    semanticStack.push(new Semantic(Type.ERROR, null, "factor"));
                break;

            case Tag.SUBTRACT:
                eat(Tag.SUBTRACT, "factorA");
                factor();
                pop();
                if (semanticObj.getType() == Type.INTEGER) {
                    Integer value = -Integer.parseInt(semanticObj.getString());
                    semanticStack.push(new Semantic(semanticObj.getType(), value.toString(), "factorA"));
                } else
                    semanticStack.push(new Semantic(Type.ERROR, null, "factorA"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "factorA"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.ID, Tag.QUOTE, Tag.PAR_OPEN, Tag.NOT, Tag.SUBTRACT};
                error("factorA", tags);
                break;
        }
    }

    public void factor()
    {
        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
            case Tag.QUOTE:
                constant();
                pop();
                semanticStack.push(new Semantic(semanticObj.getType(), semanticObj.getString(), "factor"));
                break;

            case Tag.ID:
                identifier();
                pop();
                semanticStack.push(new Semantic(semanticObj.getType(), semanticObj.getString(), "factor"));
                break;

            case Tag.PAR_OPEN:
                eat(Tag.PAR_OPEN, "factor");
                expression();
                eat(Tag.PAR_CLOSE, "factor");
                pop();
                semanticStack.push(new Semantic(semanticObj.getType(), semanticObj.getString(), "factor"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "factor"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.QUOTE, Tag.ID, Tag.PAR_OPEN};
                error("factor", tags);
                break;
        }
    }

    public void relop()
    {
        switch (token.get(0).getTag()) {
            case Tag.EQUAL:
                semanticStack.push(new Semantic(Type.VOID, String.valueOf(Tag.EQUAL), "relop"));
                eat(Tag.EQUAL, "relop");
                break;

            case Tag.GREATER:
                semanticStack.push(new Semantic(Type.VOID, String.valueOf(Tag.GREATER), "relop"));
                eat(Tag.GREATER, "relop");
                break;

            case Tag.GREATER_EQUAL:
                semanticStack.push(new Semantic(Type.VOID, String.valueOf(Tag.GREATER_EQUAL), "relop"));
                eat(Tag.GREATER_EQUAL, "relop");
                break;

            case Tag.LOWER:
                semanticStack.push(new Semantic(Type.VOID, String.valueOf(Tag.LOWER), "relop"));
                eat(Tag.LOWER, "relop");
                break;

            case Tag.LOWER_EQUAL:
                semanticStack.push(new Semantic(Type.VOID, String.valueOf(Tag.LOWER_EQUAL), "relop"));
                eat(Tag.LOWER_EQUAL, "relop");
                break;

            case Tag.NOT_EQUAL:
                semanticStack.push(new Semantic(Type.VOID, String.valueOf(Tag.NOT_EQUAL), "relop"));
                eat(Tag.NOT_EQUAL, "relop");
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "relop"));
                int[] tags = {Tag.EQUAL, Tag.GREATER, Tag.GREATER_EQUAL, Tag.LOWER, Tag.LOWER_EQUAL, Tag.NOT_EQUAL};
                error("relop", tags);
                break;
        }
    }

    public void addop()
    {
        switch (token.get(0).getTag()) {
            case Tag.OR:
                semanticStack.push(new Semantic(Type.VOID, String.valueOf(Tag.OR), "addop"));
                eat(Tag.OR, "addop");
                break;

            case Tag.SUM:
                semanticStack.push(new Semantic(Type.VOID, String.valueOf(Tag.SUM), "addop"));
                eat(Tag.SUM, "addop");
                break;

            case Tag.SUBTRACT:
                semanticStack.push(new Semantic(Type.VOID, String.valueOf(Tag.SUBTRACT), "addop"));
                eat(Tag.SUBTRACT, "addop");
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "addop"));
                int[] tags = {Tag.OR, Tag.SUM, Tag.SUBTRACT};
                error("addop", tags);
                break;
        }
    }

    public void mulop()
    {
        switch (token.get(0).getTag()) {
            case Tag.AND:
                semanticStack.push(new Semantic(Type.VOID, String.valueOf(Tag.AND), "mulop"));
                eat(Tag.AND, "mulop");
                break;

            case Tag.MULTIPLY:
                semanticStack.push(new Semantic(Type.VOID, String.valueOf(Tag.MULTIPLY), "mulop"));
                eat(Tag.MULTIPLY, "mulop");
                break;

            case Tag.DIVIDE:
                semanticStack.push(new Semantic(Type.VOID, String.valueOf(Tag.DIVIDE), "mulop"));
                eat(Tag.DIVIDE, "mulop");
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "mulop"));
                int[] tags = {Tag.AND, Tag.MULTIPLY, Tag.DIVIDE};
                error("mulop", tags);
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
                semanticStack.push(new Semantic(Type.INTEGER, semanticObj.getString(), "constant"));
                break;

            case Tag.QUOTE:
                literal();
                pop();
                semanticStack.push(new Semantic(Type.STRING, semanticObj.getString(), "constant"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "constant"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.QUOTE};
                error("constant", tags);
                break;
        }
    }

    public void integerConst()
    {
        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
                semanticStack.push(new Semantic(Type.INTEGER, token.get(0).toString(), "integerConst"));
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

                semanticStack.push(new Semantic(Type.INTEGER, value.toString(), "integerConst"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "integerConst"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO};
                error("integerConst", tags);
                break;
        }
    }

    public void literal()
    {
        switch (token.get(0).getTag()) {
            case Tag.QUOTE:
                eat(Tag.QUOTE, "literal");
                caractere();
                eat(Tag.QUOTE, "literal");

                pop();
                semanticStack.push(new Semantic(Type.STRING, semanticObj.getString(), "literal"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "literal"));
                int[] tags = {Tag.QUOTE};
                error("literal", tags);
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

                semanticStack.push(new Semantic(Type.ID, stringValue, "identifier"));
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "identifier"));

                int[] tags = {Tag.ID};
                error("identifier", tags);
                break;
        }
    }

    public void letter()
    {
        switch (token.get(0).getTag()) {
            case Tag.ID:
                semanticStack.push(new Semantic(Type.STRING, token.get(0).toString(), "letter"));
                eat(Tag.ID, "letter");
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "letter"));
                int[] tags = {Tag.ID};
                error("letter", tags);
                break;
        }
    }

    public void digit()
    {
        switch (token.get(0).getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
                semanticStack.push(new Semantic(Type.INTEGER, token.get(0).toString(), "digit"));
                eat(Tag.INTEGER, "digit");
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "digit"));
                int[] tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO};
                error("digit", tags);
                break;
        }
    }

    public void noZero()
    {
        switch (token.get(0).getTag()) {
            case Tag.CONST_NOT_ZERO:
                semanticStack.push(new Semantic(Type.INTEGER, token.get(0).toString(), "noZero"));
                eat(Tag.CONST_NOT_ZERO, "noZero");
                break;

            default:
                semanticStack.push(new Semantic(Type.ERROR, null, "noZero"));
                int[] tags = {Tag.CONST_NOT_ZERO};
                error("noZero", tags);
                break;
        }
    }

    public void caractere()
    {
        if (isAscii(token.get(0))) {
            semanticStack.push(new Semantic(Type.VOID, token.get(0).toString(), "caractere"));
            eat(Tag.ID, "caractere");
        } else {
            semanticStack.push(new Semantic(Type.ERROR, null, "caractere"));
            int[] tags = {Tag.CONST_ASCII};
            error("caractere", tags);
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
        semanticObj = (Semantic) semanticStack.pop();
    }

    public String getBool(boolean value)
    {
        if (value)
            return ("true");
        else
            return ("false");
    }
}
