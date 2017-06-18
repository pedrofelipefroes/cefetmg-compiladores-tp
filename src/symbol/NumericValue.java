package symbol;

public class NumericValue extends Token
{

    public final int value;
    private static int constTag;

    public NumericValue(int value, int constTag)
    {
        super(getTag(value));
        this.value = value;
    }

    private static int getTag(int value)
    {
        if (value == 0) constTag = Tag.CONST_ZERO;
        else constTag = Tag.CONST_NOT_ZERO;

        return constTag;
    }

    @Override
    public String toString()
    {
        return "" + value;
    }
}
