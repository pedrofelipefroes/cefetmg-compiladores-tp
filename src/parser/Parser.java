package parser;

import java.io.FileNotFoundException;
import java.io.IOException;

import lexer.Lexer;
import symbol.Token;

public class Parser {

	private String fileName;
	private Lexer lexer;

	public Parser(String fileName) throws FileNotFoundException {
		this.fileName = fileName;
		this.lexer = new Lexer(fileName);
	}

	public void run() throws IOException {
		getToken();
	}

	private void getToken() throws IOException {
		Token token = lexer.run(fileName);

		while (!token.toString().equals("EOF")) {
			System.out.println(token);
			token = lexer.run(fileName);
		}
	}

}
