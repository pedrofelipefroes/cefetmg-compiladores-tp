package lexer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import symbol.*;

public class Lexer {
	public static int line = 1;
	private char c = ' ';
	private FileReader fileReader;
	private BufferedReader bufferedReader;
	private Hashtable<String, Word> words = new Hashtable<String, Word>();

	public Lexer(String fileName) throws FileNotFoundException {
		this.fileReader = new FileReader(fileName);
		this.bufferedReader = new BufferedReader(fileReader);

		reserveWords();
	}

	public Token run(String fileName) throws IOException {

		// checks non-token characters
		while (true) {
			readCharacter();

			if (c == Tag.EOF) {
				return Word.eof;
			} else if (c == ' ' || c == '\t' || c == '\r' || c == '\b') {
				// checks blank and tab spaces
				continue;
			} else if (c == '\n') {
				// checks new line
				line++;
			} else if (c == '/') {
				// checks one line comments
				if (readCharacter('/')) {
					while (!readCharacter('\n'))
						;
				} else {
					break;
				}
			} else if (c == '{') {
				// checks block comments
				while (!readCharacter('}'))
					;
			} else {
				break;
			}
		}

		// checks identifiers
		if (Character.isLetter(c)) {
			StringBuffer stringBuffer = new StringBuffer();

			do {
				stringBuffer.append(c);
				readCharacter();
			} while (Character.isLetterOrDigit(c));

			String string = stringBuffer.toString();
			Word word = (Word) words.get(string);

			if (word != null)
				return word; // word is already present in Hash Table words

			// word is not present in Hash Table words
			word = new Word(string, Tag.ID);
			words.put(string, word);

			return word;
		}

		// checks numeric constants
		if (Character.isDigit(c)) {
			int value = 0;

			do {
				value = 10 * value + Character.digit(c, 10);
				readCharacter();
			} while (Character.isDigit(c));

			return new NumericValue(value);
		}

		// checks numeric and relational operators
		switch (c) {
		case ':':
			if (readCharacter('='))
				return Word.assign;

		case '=':
			return Word.equal;

		case '>':
			if (readCharacter('='))
				return Word.greater_equal;
			else
				return Word.greater;

		case '<':
			if (readCharacter('='))
				return Word.lower_equal;
			else if (readCharacter('>'))
				return Word.not_equal;
			else
				return Word.lower;
		}

		// checks non-identified characters
		Token token = new Token(c);
		c = ' ';
		return token;
	}

	private void readCharacter() throws IOException {
		c = (char) bufferedReader.read();
	}

	private boolean readCharacter(char c) throws IOException {
		readCharacter();

		if (this.c != c)
			return false;

		c = ' ';
		return true;
	}

	private void reserveWord(Word word) {
		words.put(word.getLexem().toLowerCase(), word);
	}

	private void reserveWords() {
		reserveWord(new Word("begin", Tag.BEGIN));
		reserveWord(new Word("do", Tag.DO));
		reserveWord(new Word("else", Tag.ELSE));
		reserveWord(new Word("if", Tag.INIT));
		reserveWord(new Word("integer", Tag.INTEGER));
		reserveWord(new Word("is", Tag.IS));
		reserveWord(new Word("not", Tag.NOT));
		reserveWord(new Word("stop", Tag.STOP));
		reserveWord(new Word("string", Tag.STRING));
		reserveWord(new Word("while", Tag.WHILE));
	}
}
