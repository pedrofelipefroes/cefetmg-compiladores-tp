package parser;

import java.io.FileNotFoundException;
import java.io.IOException;

import lexer.Lexer;
import symbol.Tag;
import symbol.Token;

public class Parser {

    private String fileName;
    private Lexer lexer;
    private Token token;

    public Parser(String fileName) throws FileNotFoundException {
        this.fileName = fileName;
        this.lexer = new Lexer(fileName);
    }

    public void run() throws IOException {
        getToken();
        program();
    }

    private void getToken() {
        // Token token = lexer.run(fileName);

        // while (!token.toString().equals("EOF")) {
        // System.out.println(token);
        token = lexer.run(fileName);
        // }
        // lexer.printHashtable();
    }

    public void error() {
        System.out.println("Erro na linha " + Lexer.line);
    }

    public void eat(int tag) {
        if (token.getTag() == tag)
            getToken();
        else
            error();
    }

    public void program() {
        switch (token.getTag()) {
            case Tag.INIT:
                eat(Tag.INIT);
                if (token.getTag() == Tag.ID)
                    declList();
                stmtList();
                eat(Tag.STOP);
                break;

            default:
                error();
                break;
        }
    }

    public void declList() {
        switch (token.getTag()) {
            case Tag.ID:
                decl();
                eat(Tag.DOT_COM);
                decl();
                eat(Tag.DOT_COM);
                break;

            default:
                error();
                break;
        }
    }

    public void decl() {
        switch (token.getTag()) {
            case Tag.ID:
                identList();
                eat(Tag.IS);
                type();
                break;

            default:
                error();
                break;
        }
    }

    public void identList() {
        switch (token.getTag()) {
            case Tag.ID:
                identifier();
                while (token.getTag() == Tag.COM) {
                    eat(Tag.COM);
                    identifier();
                }
                break;

            default:
                error();
                break;
        }
    }

    public void type() {
        switch (token.getTag()) {
            case Tag.INTEGER:
                eat(Tag.INTEGER);
                break;

            case Tag.STRING:
                eat(Tag.STRING);
                break;

            default:
                error();
                break;
        }
    }

    public void stmtList() {
        switch (token.getTag()) {
            case Tag.ID:
            case Tag.BEGIN:
            case Tag.DO:
            case Tag.IF:
            case Tag.READ:
            case Tag.WRITE:
                stmt();
                eat(Tag.DOT_COM);
                //Conjunto FIRST de um stmt
                while (token.getTag() == Tag.ID || token.getTag() == Tag.IF || token.getTag() == Tag.BEGIN ||
                       token.getTag() == Tag.DO || token.getTag() == Tag.READ || token.getTag() == Tag.WRITE) {
                    stmt();
                    eat(Tag.DOT_COM);
                }
                break;

            default:
                error();
                break;
        }
    }

    public void stmt() {
        switch (token.getTag()) {
            case Tag.ID:
                assignStmt();
                break;

            case Tag.BEGIN:
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
                error();
                break;
        }
    }

    public void assignStmt() {
        switch (token.getTag()) {
            case Tag.INTEGER:
                identifier();
                eat(Tag.ASSIGN);
                simpleExpr();
                break;

            default:
                error();
                break;
        }
    }

    public void ifStmt() {
        switch (token.getTag()) {
            case Tag.IF:
                eat(Tag.IF);
                eat(Tag.PAR_OPEN);
                condition();
                eat(Tag.PAR_CLOSE);
                eat(Tag.BEGIN);
                stmtList();
                eat(Tag.END);
                if (token.getTag() == Tag.ELSE) {
                    eat(Tag.ELSE);
                    eat(Tag.BEGIN);
                    stmtList();
                    eat(Tag.END);
                }
                break;

            default:
                error();
                break;
        }
    }

    public void condition() {
        switch (token.getTag()) {
            case Tag.INTEGER:
            case Tag.ID:
            case Tag.NOT:
            case Tag.QUOTE:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                expression();
                break;

            default:
                error();
                break;
        }
    }

    public void doStmt() {
        switch (token.getTag()) {
            case Tag.DO:
                eat(Tag.DO);
                stmtList();
                doSuffix();
                break;

            default:
                error();
                break;
        }
    }

    public void doSuffix() {
        switch (token.getTag()) {
            case Tag.WHILE:
                eat(Tag.WHILE);
                eat(Tag.PAR_OPEN);
                condition();
                eat(Tag.PAR_CLOSE);
                break;

            default:
                error();
                break;
        }
    }

    public void readStmt() {
        switch (token.getTag()) {
            case Tag.READ:
                eat(Tag.READ);
                eat(Tag.PAR_OPEN);
                identifier();
                eat(Tag.PAR_CLOSE);
                break;

            default:
                error();
                break;
        }
    }

    public void writeStmt() {
        switch (token.getTag()) {
            case Tag.WRITE:
                eat(Tag.WRITE);
                eat(Tag.PAR_OPEN);
                writable();
                eat(Tag.PAR_CLOSE);
                break;

            default:
                error();
                break;
        }
    }

    public void writable() {
        switch (token.getTag()) {
            case Tag.INTEGER:
            case Tag.ID:
            case Tag.NOT:
            case Tag.QUOTE:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                simpleExpr();
                break;

            default:
                error();
                break;
        }
    }

    public void expression() {
        switch (token.getTag()) {
            case Tag.INTEGER:
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
                error();
                break;
        }
    }

    public void simpleExpr() {
        switch (token.getTag()) {
            case Tag.INTEGER:
            case Tag.ID:
            case Tag.NOT:
            case Tag.QUOTE:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                term();
                simpleExprZ();
                break;

            default:
                error();
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
                break;

            default:
                error();
                break;
        }
    }

    public void term() {
        switch (token.getTag()) {
            case Tag.INTEGER:
            case Tag.ID:
            case Tag.NOT:
            case Tag.QUOTE:
            case Tag.PAR_OPEN:
            case Tag.SUBTRACT:
                factorA();
                termZ();
                break;

            default:
                error();
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

            case Tag.PAR_CLOSE:
            case Tag.EQUAL:
            case Tag.GREATER:
            case Tag.GREATER_EQUAL:
            case Tag.LOWER:
            case Tag.LOWER_EQUAL:
            case Tag.NOT_EQUAL:
                break;

            default:
                error();
                break;
        }
    }

    public void factorA() {
        switch (token.getTag()) {
            case Tag.INTEGER:
            case Tag.ID:
            case Tag.QUOTE:
            case Tag.PAR_OPEN:
                factor();
                break;

            case Tag.NOT:
                eat(Tag.NOT);
                factor();
                break;

            case Tag.SUBTRACT:
                eat(Tag.SUBTRACT);
                factor();
                break;

            default:
                error();
                break;
        }
    }

    public void factor() {
        switch (token.getTag()) {
            case Tag.INTEGER:
            case Tag.QUOTE:
                constant();
                break;

            case Tag.ID:
                identifier();
                break;

            case Tag.PAR_OPEN:
                eat(Tag.PAR_OPEN);
                expression();
                eat(Tag.PAR_CLOSE);
                break;

            default:
                error();
                break;
        }
    }

    public void relop() {
        switch (token.getTag()) {
            case Tag.EQUAL:
                eat(Tag.EQUAL);
                break;

            case Tag.GREATER:
                eat(Tag.GREATER);
                break;

            case Tag.GREATER_EQUAL:
                eat(Tag.GREATER_EQUAL);
                break;

            case Tag.LOWER:
                eat(Tag.LOWER);
                break;

            case Tag.LOWER_EQUAL:
                eat(Tag.LOWER_EQUAL);
                break;

            case Tag.NOT_EQUAL:
                eat(Tag.NOT_EQUAL);
                break;

            default:
                error();
                break;
        }
    }

    public void addop() {
        switch (token.getTag()) {
            case Tag.OR:
                eat(Tag.OR);
                break;

            case Tag.SUM:
                eat(Tag.SUM);
                break;

            case Tag.SUBTRACT:
                eat(Tag.SUBTRACT);
                break;

            default:
                error();
                break;
        }
    }

    public void mulop() {
        switch (token.getTag()) {
            case Tag.AND:
                eat(Tag.AND);
                break;

            case Tag.MULTIPLY:
                eat(Tag.MULTIPLY);
                break;

            case Tag.DIVIDE:
                eat(Tag.DIVIDE);
                break;

            default:
                error();
                break;
        }
    }

    public void constant() {
        switch (token.getTag()) {
            case Tag.INTEGER:
                integerConst();
                break;

            case Tag.QUOTE:
                literal();
                break;

            default:
                error();
                break;
        }
    }

    public void integerConst() {
        switch (token.getTag()) {
            case Tag.CONST_ZERO: // TO-DO: criar tag no léxico
                eat(Tag.CONST_ZERO);
                break;

            case Tag.CONST_NOT_ZERO: // TO-DO: criar tag no léxico
                noZero();
                digit();
                break;

            case Tag.INTEGER: // TO-DO: comentar este case se os de cima funcionarem
                noZero();
                digit();
                break;

            default:
                error();
                break;
        }
    }

    public void literal() {
        switch (token.getTag()) {
            case Tag.QUOTE:
                eat(Tag.QUOTE);
                caractere();
                eat(Tag.QUOTE);
                break;

            default:
                error();
                break;
        }
    }

    public void identifier() {
        switch (token.getTag()) {
            case Tag.ID:
                letter();
                while (token.getTag() == Tag.ID) {
                    letter();
                }
                while (token.getTag() == Tag.ID || token.getTag() == Tag.INTEGER || token.getTag() == '_') {
                    if (token.getTag() == Tag.ID) {
                        letter();
                    } else if (token.getTag() == Tag.INTEGER) {
                        digit();
                    } else {
                        eat('_');
                    }
                }
                break;

            default:
                error();
                break;
        }
    }

    public void letter() {
        switch (token.getTag()) {
            case Tag.ID:
                eat(Tag.ID);
                break;

            default:
                error();
                break;
        }
    }

    public void digit() {
        switch (token.getTag()) {
            case Tag.INTEGER:
                eat(Tag.INTEGER);
                break;

            default:
                error();
                break;
        }
    }

    public void noZero() {
        switch (token.getTag()) {
            case Tag.INTEGER:
                eat(Tag.CONST_NOT_ZERO); // TO-DO: criar tag no léxico
                break;

            default:
                error();
                break;
        }
    }

    public void caractere() {
        switch (token.getTag()) {
            case Tag.ID:
                eat(Tag.ID);
                // eat(Tag.CONST_ASCII); // TO-DO: criar tag e descomentar se for usar Tag.CONST_ASCII ao invés de Tag.ID;
                break;

            default:
                error();
                break;
        }
    }
}
