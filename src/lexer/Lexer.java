package lexer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
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
        
        public void printHashtable() {
            System.out.println("\n\tTABELA DE SÍMBOLOS");
            Enumeration<Word> list = words.elements();
            
            for (int i = 0; i < words.size(); i++) {
                Word element = list.nextElement();
                System.out.println(element.toString());
            }
        }

	public Token run(String fileName) throws IOException {

		// checks non-token characters
		while (true) {
			if (c == Tag.EOF) {
				return Word.eof;
			} else if (c == ' ' || c == '\t' || c == '\r' || c == '\b') {
				// checks blank and tab spaces
                                readCharacter();
				continue;
			} else if (c == '\n') {
                                readCharacter();
				// checks new line
				line++;
			} else if (c == '/') {
				// checks one line comments
				if (readCharacter('/')) {
					while (!readCharacter('\n'));
				} else {
                                        return new Token('/');
				}
			} else if (c == '{') {
				// checks block comments
                                while(c != '}' && c != Tag.EOF) {
                                    readCharacter();
                                }
                                if (c == Tag.EOF)
                                    return Word.eof;
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
			} while (Character.isLetterOrDigit(c) || c == '_');

			String string = stringBuffer.toString();
			Word word = (Word) words.get(string.toLowerCase());

			if (word != null)
				return word; // word is already present in Hash Table words

			// word is not present in Hash Table words
			word = new Word(string.toLowerCase(), Tag.ID);
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
			if (readCharacter('=')) {
                                c = ' ';
				return Word.assign;
                        } else {
                                return new Token(':');
                        }

		case '=':
                        c = ' ';
			return Word.equal;

		case '>':
			if (readCharacter('=')) {
                                c = ' ';
				return Word.greater_equal;
                        } else {
				return Word.greater;
                        }

		case '<':
			if (readCharacter('=')) {
                                c = ' ';
				return Word.lower_equal;
                        } else if (readCharacter('>')) {
                                c = ' ';
				return Word.not_equal;
                        } else {
				return Word.lower;
                        }
                        
                case Tag.QUOTE:
                        c = ' ';
                        return Word.quote;
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
		reserveWord(new Word("if", Tag.IF));
		reserveWord(new Word("init", Tag.INIT));
		reserveWord(new Word("integer", Tag.INTEGER));
		reserveWord(new Word("is", Tag.IS));
		reserveWord(new Word("not", Tag.NOT));
		reserveWord(new Word("stop", Tag.STOP));
		reserveWord(new Word("string", Tag.STRING));
		reserveWord(new Word("while", Tag.WHILE));
	}
}
