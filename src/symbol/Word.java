package symbol;

public class Word extends Token implements Comparable<Word> {
	private String lexem = "";
	public static final Word
		assign = new Word(":=", Tag.ASSIGN),
		eof = new Word("EOF", Tag.EOF),
		equal = new Word("=", Tag.EQUAL),
		greater = new Word(">", Tag.GREATER),
		greater_equal = new Word(">=", Tag.GREATER_EQUAL),
		lower = new Word("<", Tag.LOWER),
		lower_equal = new Word("<=", Tag.LOWER_EQUAL),
		not_equal = new Word("<>", Tag.NOT_EQUAL);
                
		
	public Word(String word, int tag) {
		super(tag);
		this.lexem = word;
	}

        @Override
        public int compareTo(Word word)
        {
            return this.getTag() - word.getTag();
        }
	
	@Override
	public String toString() {
		return "" + lexem;
	}

	public String getLexem() {
		return lexem;
	}
}
