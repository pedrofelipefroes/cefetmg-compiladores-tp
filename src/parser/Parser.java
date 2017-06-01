package parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import lexer.Lexer;
import symbol.Tag;
import symbol.Token;

public class Parser {

    private String fileName;
    private Lexer lexer;
    private Token token;
    private ArrayList<Follow> followList;
    private boolean error = false;

    public Parser(String fileName) throws FileNotFoundException {
        this.fileName = fileName;
        this.lexer = new Lexer(fileName);
        this.followList = new ArrayList<Follow>();
        Follow.initializeFollowList(followList);
    }

    public void run() throws IOException {
        getToken();
        program();
        
        if(!error)
            System.out.println("Programa executado sem erro sintático! :D");
    }

    private void getToken() {
        token = lexer.run(fileName);
    }

    public void error(String name, int []tag) {
        System.out.print("Erro na linha " + Lexer.line + " no reconhecimento de " + name + ".\n\tToken esperado: "); 
        for(int i = 0; i < tag.length; i++)
            System.out.print(Tag.getName(tag[i]) + " ");
        System.out.println("\n\tPróximo token: '" + Tag.getName(token.getTag()) + "'.");
        
        System.out.println("Modo pânico ativado!");
        while(!Follow.isFollow(followList, name, token.getTag()) && token.getTag() != Tag.EOF) 
            getToken();
        System.out.println("Modo pânico desativado!\n");
        
        error = true;
        
        if(token.getTag() == Tag.EOF)
            System.exit(0);
    }

    public void eat(int tag, String name) {
        if (token.getTag() == tag)
            getToken();
        else {
            int []tags = {tag};
            error(name, tags);
        }
    }

    public void program() {
        switch (token.getTag()) {
            case Tag.INIT:
                eat(Tag.INIT, "program");
                if (token.getTag() == Tag.ID)
                    declList();
                stmtList();
                eat(Tag.STOP, "program");
                break;

            default:
                int []tags = {Tag.INIT};
                error("program", tags);
                break;
        }
    }

    public void declList() {
        switch (token.getTag()) {
            case Tag.ID:
                decl();
                eat(Tag.DOT_COM, "declList");
                while(token.getTag() == Tag.ID) {
                    decl();
                    eat(Tag.DOT_COM, "declList");
                }
                break;

            default:
                int []tags = {Tag.ID};
                error("declList", tags);
                break;
        }
    }

    public void decl() {
        switch (token.getTag()) {
            case Tag.ID:
                identList();
                eat(Tag.IS, "decl");
                type();
                break;

            default:
                int []tags = {Tag.ID};
                error("decl", tags);
                break;
        }
    }

    public void identList() {
        switch (token.getTag()) {
            case Tag.ID:
                identifier();
                while (token.getTag() == Tag.COM) {
                    eat(Tag.COM, "identList");
                    identifier();
                }
                break;

            default:
                int []tags = {Tag.ID};
                error("identList", tags);
                break;
        }
    }

    public void type() {
        switch (token.getTag()) {
            case Tag.INTEGER:
                eat(Tag.INTEGER, "type");
                break;

            case Tag.STRING:
                eat(Tag.STRING, "type");
                break;

            default:
                int []tags = {Tag.INTEGER, Tag.STRING};
                error("type", tags);
                break;
        }
    }

    public void stmtList() {
        switch (token.getTag()) {
            case Tag.ID:
            case Tag.DO:
            case Tag.IF:
            case Tag.READ:
            case Tag.WRITE:
                stmt();
                eat(Tag.DOT_COM, "stmtList");
                while (token.getTag() == Tag.ID || token.getTag() == Tag.DO || token.getTag() == Tag.IF ||
                       token.getTag() == Tag.READ || token.getTag() == Tag.WRITE) {
                    stmt();
                    eat(Tag.DOT_COM, "stmtList");
                }
                break;

            default:
                int []tags = {Tag.ID, Tag.DO, Tag.IF, Tag.READ, Tag.WRITE};
                error("stmtList", tags);
                break;
        }
    }

    public void stmt() {
        switch (token.getTag()) {
            case Tag.ID:
                assignStmt();
                break;

            case Tag.IF:
                ifStmt();
                break;

            case Tag.DO:
                doStmt();
                break;

            case Tag.READ:
                readStmt();
                break;

            case Tag.WRITE:
                writeStmt();
                break;

            default:
                int []tags = {Tag.ID, Tag.DO, Tag.READ, Tag.WRITE};
                error("stmt", tags);
                break;
        }
    }

    public void assignStmt() {
        switch (token.getTag()) {
            case Tag.ID:
                identifier();
                eat(Tag.ASSIGN, "assignStmt");
                simpleExpr();
                break;

            default:
                int []tags = {Tag.INTEGER};
                error("assignStmt", tags);
                break;
        }
    }

    public void ifStmt() {
        switch (token.getTag()) {
            case Tag.IF:
                eat(Tag.IF, "ifStmt");
                eat(Tag.PAR_OPEN, "ifStmt");
                condition();
                eat(Tag.PAR_CLOSE, "ifStmt");
                eat(Tag.BEGIN, "ifStmt");
                stmtList();
                eat(Tag.END, "ifStmt");
                if (token.getTag() == Tag.ELSE) {
                    eat(Tag.ELSE, "ifStmt");
                    eat(Tag.BEGIN, "ifStmt");
                    stmtList();
                    eat(Tag.END, "ifStmt");
                }
                break;

            default:
                int []tags = {Tag.IF};
                error("ifStmt", tags);
                break;
        }
    }

    public void condition() {
        switch (token.getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.NOT:
            case Tag.QUOTE:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                expression();
                break;

            default:
                int []tags = {Tag.INTEGER, Tag.ID, Tag.NOT, Tag.QUOTE, Tag.PAR_OPEN, Tag.SUBTRACT};
                error("condition", tags);
                break;
        }
    }

    public void doStmt() {
        switch (token.getTag()) {
            case Tag.DO:
                eat(Tag.DO, "doStmt");
                stmtList();
                doSuffix();
                break;

            default:
                int []tags = {Tag.DO};
                error("doStmt", tags);
                break;
        }
    }

    public void doSuffix() {
        switch (token.getTag()) {
            case Tag.WHILE:
                eat(Tag.WHILE, "doSuffix");
                eat(Tag.PAR_OPEN, "doSuffix");
                condition();
                eat(Tag.PAR_CLOSE, "doSuffix");
                break;

            default:
                int []tags = {Tag.WHILE};
                error("doSuffix", tags);
                break;
        }
    }

    public void readStmt() {
        switch (token.getTag()) {
            case Tag.READ:
                eat(Tag.READ, "readStmt");
                eat(Tag.PAR_OPEN, "readStmt");
                identifier();
                eat(Tag.PAR_CLOSE, "readStmt");
                break;

            default:
                int []tags = {Tag.READ};
                error("readStmt", tags);
                break;
        }
    }

    public void writeStmt() {
        switch (token.getTag()) {
            case Tag.WRITE:
                eat(Tag.WRITE, "writeStmt");
                eat(Tag.PAR_OPEN, "writeStmt");
                writable();
                eat(Tag.PAR_CLOSE, "writeStmt");
                break;

            default:
                int []tags = {Tag.WRITE};
                error("writeStmt", tags);
                break;
        }
    }

    public void writable() {
        switch (token.getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.NOT:
            case Tag.QUOTE:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                simpleExpr();
                break;

            default:
                int []tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.ID, Tag.NOT, Tag.QUOTE, Tag.PAR_OPEN, Tag.SUBTRACT};
                error("writable", tags);
                break;
        }
    }

    public void expression() {
        switch (token.getTag()) {
        	case Tag.CONST_ZERO:
        	case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.NOT:
            case Tag.QUOTE:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                simpleExpr();
                if(token.getTag() >= Tag.EQUAL && token.getTag() <= Tag.NOT_EQUAL) {
                    relop();
                    simpleExpr();
                }
                break;

            default:
                int []tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.ID, Tag.NOT, Tag.QUOTE, Tag.PAR_OPEN, Tag.SUBTRACT};
                error("expression", tags);
                break;
        }
    }

    public void simpleExpr() {
        switch (token.getTag()) {
        	case Tag.CONST_ZERO:
        	case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.NOT:
            case Tag.QUOTE:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                term();
                simpleExprZ();
                break;

            default:
                int []tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.ID, Tag.NOT, Tag.QUOTE, Tag.PAR_OPEN, Tag.SUBTRACT};
                error("simpleExpr", tags);
                break;
        }
    }

    public void simpleExprZ() {
        switch (token.getTag()) {
            case Tag.OR:
            case Tag.SUM:
            case Tag.SUBTRACT:
                addop();
                term();
                simpleExprZ();
                break;

            case Tag.PAR_CLOSE:
            case Tag.EQUAL:
            case Tag.GREATER:
            case Tag.GREATER_EQUAL:
            case Tag.LOWER:
            case Tag.LOWER_EQUAL:
            case Tag.NOT_EQUAL:
            case Tag.DOT_COM:
                break;

            default:
                int []tags = {Tag.OR, Tag.SUM, Tag.SUBTRACT, Tag.PAR_CLOSE, Tag.EQUAL, Tag.GREATER, Tag.GREATER_EQUAL, Tag.LOWER, Tag.LOWER_EQUAL, Tag.NOT_EQUAL};
                error("simpleExprZ", tags);
                break;
        }
    }

    public void term() {
        switch (token.getTag()) {
        	case Tag.CONST_ZERO:
        	case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.NOT:
            case Tag.QUOTE:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                factorA();
                termZ();
                break;

            default:
                int []tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.ID, Tag.NOT, Tag.QUOTE, Tag.PAR_OPEN, Tag.SUBTRACT};
                error("term", tags);
                break;
        }
    }

    public void termZ() {
        switch (token.getTag()) {
            case Tag.AND:
            case Tag.MULTIPLY:
            case Tag.DIVIDE:
                mulop();
                factorA();
                termZ();
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
                break;

            default:
                int []tags = {Tag.AND, Tag.MULTIPLY, Tag.DIVIDE, Tag.OR, Tag.PAR_CLOSE, Tag.SUM, Tag.SUBTRACT, Tag.EQUAL, Tag.GREATER, Tag.GREATER_EQUAL, Tag.LOWER, Tag.LOWER_EQUAL, Tag.NOT_EQUAL};
                error("termZ", tags);
                break;
        }
    }

    public void factorA() {
        switch (token.getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
            case Tag.ID:
            case Tag.QUOTE:
            case Tag.PAR_OPEN:
                factor();
                break;

            case Tag.NOT:
                eat(Tag.NOT, "factorA");
                factor();
                break;

            case Tag.SUBTRACT:
                eat(Tag.SUBTRACT, "factorA");
                factor();
                break;

            default:
                int []tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.ID, Tag.QUOTE, Tag.PAR_OPEN, Tag.NOT, Tag.SUBTRACT};
                error("factorA", tags);
                break;
        }
    }

    public void factor() {
        switch (token.getTag()) {
        	case Tag.CONST_ZERO:
        	case Tag.CONST_NOT_ZERO:
            case Tag.QUOTE:
                constant();
                break;

            case Tag.ID:
                identifier();
                break;

            case Tag.PAR_OPEN:
                eat(Tag.PAR_OPEN, "factor");
                expression();
                eat(Tag.PAR_CLOSE, "factor");
                break;

            default:
                int []tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO, Tag.QUOTE, Tag.ID, Tag.PAR_OPEN};
                error("factor", tags);
                break;
        }
    }

    public void relop() {
        switch (token.getTag()) {
            case Tag.EQUAL:
                eat(Tag.EQUAL, "relop");
                break;

            case Tag.GREATER:
                eat(Tag.GREATER, "relop");
                break;

            case Tag.GREATER_EQUAL:
                eat(Tag.GREATER_EQUAL, "relop");
                break;

            case Tag.LOWER:
                eat(Tag.LOWER, "relop");
                break;

            case Tag.LOWER_EQUAL:
                eat(Tag.LOWER_EQUAL, "relop");
                break;

            case Tag.NOT_EQUAL:
                eat(Tag.NOT_EQUAL, "relop");
                break;

            default:
                int []tags = {Tag.EQUAL, Tag.GREATER, Tag.GREATER_EQUAL, Tag.LOWER, Tag.LOWER_EQUAL, Tag.NOT_EQUAL};
                error("relop", tags);
                break;
        }
    }

    public void addop() {
        switch (token.getTag()) {
            case Tag.OR:
                eat(Tag.OR, "addop");
                break;

            case Tag.SUM:
                eat(Tag.SUM, "addop");
                break;

            case Tag.SUBTRACT:
                eat(Tag.SUBTRACT, "addop");
                break;

            default:
                int []tags = {Tag.OR, Tag.SUM, Tag.SUBTRACT};
                error("addop", tags);
                break;
        }
    }

    public void mulop() {
        switch (token.getTag()) {
            case Tag.AND:
                eat(Tag.AND, "mulop");
                break;

            case Tag.MULTIPLY:
                eat(Tag.MULTIPLY, "mulop");
                break;

            case Tag.DIVIDE:
                eat(Tag.DIVIDE, "mulop");
                break;

            default:
                int []tags = {Tag.AND, Tag.MULTIPLY, Tag.DIVIDE};
                error("mulop", tags);
                break;
        }
    }

    public void constant() {
        switch (token.getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
                integerConst();
                break;

            case Tag.QUOTE:
                literal();
                break;

            default:
                int []tags = {Tag.INTEGER, Tag.QUOTE};
                error("constant", tags);
                break;
        }
    }

    public void integerConst() {
        switch (token.getTag()) {
            case Tag.CONST_ZERO:
                eat(Tag.CONST_ZERO, "integerConst");
                break;

            case Tag.CONST_NOT_ZERO:
                noZero();
                while(token.getTag() == Tag.CONST_NOT_ZERO || token.getTag() == Tag.CONST_ZERO)
                    digit();
                break;

            default:
                int []tags = {Tag.CONST_ZERO, Tag.CONST_NOT_ZERO};
                error("integerConst", tags);
                break;
        }
    }

    public void literal() {
        switch (token.getTag()) {
            case Tag.QUOTE:
                eat(Tag.QUOTE, "literal");
                caractere();
                eat(Tag.QUOTE, "literal");
                break;

            default:
                int []tags = {Tag.QUOTE};
                error("literal", tags);
                break;
        }
    }

    public void identifier() {
        switch (token.getTag()) {
            case Tag.ID:
                letter();
                while (token.getTag() == Tag.ID || token.getTag() == Tag.INTEGER || token.getTag() == '_') {
                    if (token.getTag() == Tag.ID) {
                        letter();
                    } else if (token.getTag() == Tag.INTEGER) {
                        digit();
                    } else {
                        eat('_', "identifier");
                    }
                }
                break;

            default:
                int []tags = {Tag.ID};
                error("identifier", tags);
                break;
        }
    }

    public void letter() {
        switch (token.getTag()) {
            case Tag.ID:
                eat(Tag.ID, "letter");
                break;

            default:
                int []tags = {Tag.ID};
                error("letter", tags);
                break;
        }
    }

    public void digit() {
        switch (token.getTag()) {
            case Tag.CONST_ZERO:
            case Tag.CONST_NOT_ZERO:
                eat(Tag.INTEGER, "digit");
                break;

            default:
                int []tags = {Tag.INTEGER};
                error("digit", tags);
                break;
        }
    }

    public void noZero() {
        switch (token.getTag()) {            
            case Tag.CONST_NOT_ZERO:
                eat(Tag.CONST_NOT_ZERO, "noZero");
                break;

            default:
                int []tags = {Tag.CONST_NOT_ZERO};
                error("noZero", tags);
                break;
        }
    }

    public void caractere() {
        if (isAscii(token))
                eat(Tag.ID, "caractere");
        else {
                int []tags = {Tag.CONST_ASCII};
                error("caractere", tags);
        }
    }
    
    public boolean isAscii(Token token) {
        if(token.getTag() >= 0 && token.getTag() <= 127 && token.getTag() != (int) '"' && token.getTag() != (int) '\n')
            return true;
        return false;
    }
}
