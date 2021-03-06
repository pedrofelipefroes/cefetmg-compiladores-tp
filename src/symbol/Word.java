package symbol;

import java.util.Hashtable;
import lexer.Lexer;
import parser.Parser;
import semantic.Type;

public class Word extends Token implements Comparable<Word>
{

    private String lexem = "";
    private String value = null;
    private int type;

    public static final Word assign = new Word(":=", Tag.ASSIGN),
            and = new Word("and", Tag.AND),
            eof = new Word("EOF", Tag.EOF),
            equal = new Word("=", Tag.EQUAL),
            greater = new Word(">", Tag.GREATER),
            greater_equal = new Word(">=", Tag.GREATER_EQUAL),
            lower = new Word("<", Tag.LOWER),
            lower_equal = new Word("<=", Tag.LOWER_EQUAL),
            not_equal = new Word("<>", Tag.NOT_EQUAL),
            or = new Word("or", Tag.OR),
            quote = new Word(String.valueOf('"'), Tag.QUOTE);

    public Word(String word, int tag)
    {
        super(tag);
        this.lexem = word;
    }

    public static void setValue(String lexem, String value)
    {
        Word word = (Word) Lexer.words.get(lexem);
        word.setValue(value);
        Lexer.words.replace(lexem, word);
    }

    public static void setType(String lexem, int type)
    {
        Word word = (Word) Lexer.words.get(lexem);
        word.setType(type);
        Lexer.words.replace(lexem, word);
    }

    public static String getValue(String lexem)
    {
        Word word = (Word) Lexer.words.get(lexem);
        return word.getValue();
    }

    public static int getType(String lexem)
    {
        Word word = (Word) Lexer.words.get(lexem);
        return word.getType();
    }

    private void setValue(String value)
    {
        this.value = value;
    }

    private void setType(int type)
    {
        this.type = type;
    }

    public String getValue()
    {
        return value;
    }

    public int getType()
    {
        return type;
    }

    public String typeToString()
    {
        switch (type) {
            case (Type.VOID):
                return "void";
            case (Type.STRING):
                return "string";
            case (Type.INTEGER):
                return "integer";
            case (Type.BOOLEAN):
                return "boolean";
            case (Type.ERROR):
                return "error";
            default:
                return "null";
        }
    }

    @Override
    public int compareTo(Word word)
    {
        return this.getTag() - word.getTag();
    }

    @Override
    public String toString()
    {
        return "" + lexem;
    }

    public String getLexem()
    {
        return lexem;
    }
}
