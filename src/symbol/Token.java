package symbol;

public class Token
{

    public final int tag;

    public Token(int tag)
    {
        this.tag = tag;
    }

    public int getTag()
    {
        return tag;
    }

    @Override
    public String toString()
    {
        return "" + ((char) tag);
    }

}
