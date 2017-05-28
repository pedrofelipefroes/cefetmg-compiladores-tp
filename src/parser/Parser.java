package parser;

import java.io.FileNotFoundException;
import java.io.IOException;

import lexer.Lexer;
import symbol.Tag;
import symbol.Token;

public class Parser
{

    private String fileName;
    private Lexer lexer;
    private Token token;

    public Parser(String fileName) throws FileNotFoundException
    {
        this.fileName = fileName;
        this.lexer = new Lexer(fileName);
    }

    public void run() throws IOException
    {
        getToken();
        program();
    }

    private void getToken()
    {
        //Token token = lexer.run(fileName);

        //while (!token.toString().equals("EOF")) {
        //System.out.println(token);
        token = lexer.run(fileName);
        //}
        //lexer.printHashtable();
    }

    public void error()
    {
        System.out.println("Erro na linha " + Lexer.line);
    }

    public void eat(int tag)
    {
        if (token.getTag() == tag)
            getToken();
        else
            error();
    }

    public void program()
    {
        switch (token.getTag())
        {
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

    public void declList()
    {
        switch (token.getTag())
        {
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

    public void decl()
    {
        switch (token.getTag())
        {
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

    public void identList()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void type()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void stmtList()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void stmt()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void assignStmt()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void ifStmt()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void condition()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void doStmt()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void doSuffix()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void readStmt()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void writeStmt()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void writable()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void expression()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void simpleExpr()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void simpleExprZ()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void term()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void termZ()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void factorA()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void factor()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void relop()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void addop()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void mulop()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void constant()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void integerConst()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void literal()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void identifier()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void letter()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void digit()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void noZero()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }

    public void caractere()
    {
        switch (token.getTag())
        {
            
            
            default:
                error();
                break;
        }
    }
}
