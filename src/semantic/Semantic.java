package semantic;

public class Semantic
{
    private int type;
    private String string;
    private String functionName;

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
    
    public String getString() {
        return string;
    }

    public int getInt()
    {
        return Integer.valueOf(string);
    }

    public String getFunctionName()
    {
        return functionName;
    }
}
