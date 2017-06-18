package semantic;

import lexer.Lexer;

public class Semantic
{

    private int type;
    private String string;
    private String functionName;
    
    public static boolean error = false;

    public Semantic(int type, String value, String functionName)
    {
        this.type = type;
        this.string = value;
        this.functionName = functionName;
    }

    public int getType()
    {
        return type;
    }

    public String getString()
    {
        return string;
    }

    public int getInt()
    {
        try {
            int number = Integer.valueOf(string);
            return number;
        } catch (Exception e) {
            return 0;
        }
    }
    
    public boolean getBool() {
        if(string.equals("true"))
            return true;
        else
            return false;
    }

    public String getFunctionName()
    {
        return functionName;
    }

    public static void errorType(String esperado, String encontrado)
    {
        error = true;
        System.out.println("Semântico: Erro de tipo na linha " + Lexer.line + ". Esperava-se " + esperado + " e obteve-se " + encontrado + ".");
    }

    public static void errorType(String mensagem)
    {
        error = true;
        System.out.println("Semântico: Erro de tipo na linha " + Lexer.line + ". Mensagem: " + mensagem);
    }

    public static void errorUnicity(String variavel)
    {
        error = true;
        System.out.println("Semântico: Erro de unicidade na linha " + Lexer.line + ". A variável " + variavel + " foi declarada mais de uma vez.");
    }
}
