package symbol;

public class Word extends Token {
	private String lexem = "";
	public static final Word
		assign = new Word(":=", Tag.ASSIGN),
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
                
		
	public Word(String word, int tag) {
		super(tag);
		this.lexem = word;
	}
	
	@Override
	public String toString() {
		return "" + lexem;
	}

	public String getLexem() {
		return lexem;
	}
}
