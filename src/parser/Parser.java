package parser;

import java.io.FileNotFoundException;
import java.io.IOException;

import lexer.Lexer;
import symbol.Tag;
import symbol.Token;

public class Parser {

	private String fileName;
	private Lexer lexer;
	private Token token;

	public Parser(String fileName) throws FileNotFoundException {
		this.fileName = fileName;
		this.lexer = new Lexer(fileName);
	}

	public void run() throws IOException {
		getToken();
		program();
	}

	private void getToken() {
		// Token token = lexer.run(fileName);

		// while (!token.toString().equals("EOF")) {
		// System.out.println(token);
		token = lexer.run(fileName);
		// }
		// lexer.printHashtable();
	}

	public void error() {
		System.out.println("Erro na linha " + Lexer.line);
	}

	public void eat(int tag) {
		if (token.getTag() == tag)
			getToken();
		else
			error();
	}

	public void program() {
		switch (token.getTag()) {
		case Tag.INIT:
			eat(Tag.INIT);
			if (token.getTag() == Tag.ID)
				declList();
			stmtList();
			eat(Tag.STOP);
			break;

		default:
			error();
			break;
		}
	}

	public void declList() {
		switch (token.getTag()) {
		case Tag.ID:
			decl();
			eat(Tag.DOT_COM);
			decl();
			eat(Tag.DOT_COM);
			break;

		default:
			error();
			break;
		}
	}

	public void decl() {
		switch (token.getTag()) {
		case Tag.ID:
			identList();
			eat(Tag.IS);
			type();
			break;

		default:
			error();
			break;
		}
	}

	public void identList() {
		switch (token.getTag()) {
		case Tag.ID:
			identifier();
			while (token.getTag() == Tag.DOT_COM) {
				eat(Tag.DOT_COM);
				identifier();
			}
			break;

		default:
			error();
			break;
		}
	}

	public void type() {
		switch (token.getTag()) {
		case Tag.INTEGER:
			eat(Tag.INTEGER);
			break;

		case Tag.STRING:
			eat(Tag.STRING);
			break;

		default:
			error();
			break;
		}
	}

	public void stmtList() {
		switch (token.getTag()) {
		case Tag.ID:
			stmt();
			eat(Tag.DOT_COM);
			while (token.getTag() == Tag.DOT_COM) {
				stmt();
				eat(Tag.DOT_COM);
			}
			break;

		case Tag.BEGIN:
			stmt();
			eat(Tag.DOT_COM);
			while (token.getTag() == Tag.DOT_COM) {
				stmt();
				eat(Tag.DOT_COM);
			}
			break;

		case Tag.DO:
			stmt();
			eat(Tag.DOT_COM);
			while (token.getTag() == Tag.DOT_COM) {
				stmt();
				eat(Tag.DOT_COM);
			}
			break;

		case Tag.IF:
			stmt();
			eat(Tag.DOT_COM);
			while (token.getTag() == Tag.DOT_COM) {
				stmt();
				eat(Tag.DOT_COM);
			}
			break;

		case Tag.READ:
			stmt();
			eat(Tag.DOT_COM);
			while (token.getTag() == Tag.DOT_COM) {
				stmt();
				eat(Tag.DOT_COM);
			}
			break;

		case Tag.WRITE:
			stmt();
			eat(Tag.DOT_COM);
			while (token.getTag() == Tag.DOT_COM) {
				stmt();
				eat(Tag.DOT_COM);
			}
			break;

		default:
			error();
			break;
		}
	}

	public void stmt() {
		switch (token.getTag()) {
		case Tag.ID:
			assignStmt();
			break;

		case Tag.BEGIN:
			ifStmt(); // TO-DO: na enumeração das produções tem 5 possíveis, e aqui tem seis.
			break;

		case Tag.DO:
			doStmt();
			break;

		case Tag.IF:
			ifStmt();
			break;

		case Tag.READ:
			readStmt();
			break;

		case Tag.WRITE:
			writeStmt();
			break;

		default:
			error();
			break;
		}
	}

	public void assignStmt() {
		switch (token.getTag()) {
		case Tag.INTEGER:
			identifier();
			eat(Tag.ASSIGN);
			simpleExpr();
			break;

		default:
			error();
			break;
		}
	}

	public void ifStmt() {
		switch (token.getTag()) {
		case Tag.BEGIN:
			eat(Tag.IF);
			eat(Tag.PAR_OPEN);
			condition();
			eat(Tag.PAR_CLOSE);
			eat(Tag.BEGIN);
			stmtList();
			eat(Tag.END);
			if (token.getTag() == Tag.ELSE) { // TO-DO: tinha um lambda, coloquei isso como condicional
				eat(Tag.ELSE);
				eat(Tag.BEGIN);
				stmtList();
				eat(Tag.END);
			}
			break;

		case Tag.IF: // TO-DO: coloquei a mesma produção do case Tag.BEGIN porque não havia separação na lista de produções
			eat(Tag.IF);
			eat(Tag.PAR_OPEN);
			condition();
			eat(Tag.PAR_CLOSE);
			eat(Tag.BEGIN);
			stmtList();
			eat(Tag.END);
			if (token.getTag() == Tag.ELSE) { // TO-DO: tinha um lambda, coloquei isso como condicional
				eat(Tag.ELSE);
				eat(Tag.BEGIN);
				stmtList();
				eat(Tag.END);
			}
			break;

		default:
			error();
			break;
		}
	}

	public void condition() {
		switch (token.getTag()) {
		case Tag.INTEGER:
			expression();
			break;

		case Tag.ID:
			expression();
			break;

		case Tag.NOT:
			expression();
			break;

		case Tag.QUOTE:
			expression();
			break;

		case Tag.PAR_OPEN:
			expression();
			break;

		case Tag.SUBTRACT:
			expression();
			break;

		default:
			error();
			break;
		}
	}

	public void doStmt() {
		switch (token.getTag()) {
		case Tag.DO:
			eat(Tag.DO);
			stmtList();
			doSuffix();
			break;

		default:
			error();
			break;
		}
	}

	public void doSuffix() {
		switch (token.getTag()) {
		case Tag.WHILE:
			eat(Tag.WHILE);
			eat(Tag.PAR_OPEN);
			condition();
			eat(Tag.PAR_CLOSE);
			break;

		default:
			error();
			break;
		}
	}

	public void readStmt() {
		switch (token.getTag()) {
		case Tag.READ:
			eat(Tag.READ);
			eat(Tag.PAR_OPEN);
			condition();
			eat(Tag.PAR_CLOSE);
			break;

		default:
			error();
			break;
		}
	}

	public void writeStmt() {
		switch (token.getTag()) {
		case Tag.WRITE:
			eat(Tag.WRITE);
			eat(Tag.PAR_OPEN);
			condition();
			eat(Tag.PAR_CLOSE);
			break;

		default:
			error();
			break;
		}
	}

	public void writable() {
		switch (token.getTag()) {
		case Tag.INTEGER:
			simpleExpr();
			break;

		case Tag.ID:
			simpleExpr();
			break;

		case Tag.NOT:
			simpleExpr();
			break;

		case Tag.QUOTE:
			simpleExpr();
			break;

		case Tag.PAR_OPEN:
			simpleExpr();
			break;

		case Tag.SUBTRACT:
			simpleExpr();
			break;

		default:
			error();
			break;
		}
	}

	public void expression() {
		switch (token.getTag()) {
		case Tag.INTEGER:
			simpleExpr();
			if (token.getTag() == (Tag.EQUAL || Tag.GREATER || Tag.GREATER_EQUAL || Tag.LOWER || Tag.LOWER_EQUAL || Tag.NOT_EQUAL)) {
				relop(); // TO-DO: conferir expressão acima
				simpleExpr();
			}
			break;

		case Tag.ID:
			simpleExpr();
			if (token.getTag() == (Tag.EQUAL || Tag.GREATER || Tag.GREATER_EQUAL || Tag.LOWER || Tag.LOWER_EQUAL || Tag.NOT_EQUAL)) {
				relop();
				simpleExpr();
			}
			break;

		case Tag.NOT:
			simpleExpr();
			if (token.getTag() == (Tag.EQUAL || Tag.GREATER || Tag.GREATER_EQUAL || Tag.LOWER || Tag.LOWER_EQUAL || Tag.NOT_EQUAL)) {
				relop();
				simpleExpr();
			}
			break;

		case Tag.QUOTE:
			simpleExpr();
			if (token.getTag() == (Tag.EQUAL || Tag.GREATER || Tag.GREATER_EQUAL || Tag.LOWER || Tag.LOWER_EQUAL || Tag.NOT_EQUAL)) {
				relop();
				simpleExpr();
			}
			break;

		case Tag.PAR_OPEN:
			simpleExpr();
			if (token.getTag() == (Tag.EQUAL || Tag.GREATER || Tag.GREATER_EQUAL || Tag.LOWER || Tag.LOWER_EQUAL || Tag.NOT_EQUAL)) {
				relop();
				simpleExpr();
			}
			break;

		case Tag.SUBTRACT:
			simpleExpr();
			if (token.getTag() == (Tag.EQUAL || Tag.GREATER || Tag.GREATER_EQUAL || Tag.LOWER || Tag.LOWER_EQUAL || Tag.NOT_EQUAL)) {
				relop();
				simpleExpr();
			}
			break;

		default:
			error();
			break;
		}
	}

	public void simpleExpr() {
		switch (token.getTag()) {
		case Tag.INTEGER:
			term();
			simpleExprZ();
			break;

		case Tag.ID:
			term();
			simpleExprZ();
			break;

		case Tag.NOT:
			term();
			simpleExprZ();
			break;

		case Tag.QUOTE:
			term();
			simpleExprZ();
			break;

		case Tag.PAR_OPEN:
			term();
			simpleExprZ();
			break;

		case Tag.SUBTRACT:
			term();
			simpleExprZ();
			break;

		default:
			error();
			break;
		}
	}

	public void simpleExprZ() { // TO-DO: simpleExprZ também gera lambda – como tratar isso?
		switch (token.getTag()) {
		case Tag.OR:
			addop();
			term();
			simpleExprZ();
			break;

		case Tag.PAR_CLOSE:
			addop();
			term();
			simpleExprZ();
			break;

		case Tag.SUM:
			addop();
			term();
			simpleExprZ();
			break;

		case Tag.SUBTRACT:
			addop();
			term();
			simpleExprZ();
			break;

		case Tag.EQUAL:
			addop();
			term();
			simpleExprZ();
			break;

		case Tag.GREATER:
			addop();
			term();
			simpleExprZ();
			break;

		case Tag.GREATER_EQUAL:
			addop();
			term();
			simpleExprZ();
			break;

		case Tag.LOWER:
			addop();
			term();
			simpleExprZ();
			break;

		case Tag.LOWER_EQUAL:
			addop();
			term();
			simpleExprZ();
			break;

		case Tag.NOT_EQUAL:
			addop();
			term();
			simpleExprZ();
			break;

		default:
			error();
			break;
		}
	}

	public void term() {
		switch (token.getTag()) {
		case Tag.INTEGER:
			factorA();
			termZ();
			break;

		case Tag.ID:
			factorA();
			termZ();
			break;

		case Tag.NOT:
			factorA();
			termZ();
			break;

		case Tag.QUOTE:
			factorA();
			termZ();
			break;

		case Tag.PAR_OPEN:
			factorA();
			termZ();
			break;

		case Tag.SUBTRACT:
			factorA();
			termZ();
			break;

		default:
			error();
			break;
		}
	}

	public void termZ() { // TO-DO: termZ também gera lambda – como tratar isso?
		switch (token.getTag()) {
		case Tag.AND:
			mulop();
			factorA();
			termZ();
			break;

		case Tag.PAR_CLOSE:
			mulop();
			factorA();
			termZ();
			break;

		case Tag.MULTIPLY:
			mulop();
			factorA();
			termZ();
			break;

		case Tag.DIVIDE:
			mulop();
			factorA();
			termZ();
			break;

		case Tag.EQUAL:
			mulop();
			factorA();
			termZ();
			break;

		case Tag.GREATER:
			mulop();
			factorA();
			termZ();
			break;

		case Tag.GREATER_EQUAL:
			mulop();
			factorA();
			termZ();
			break;

		case Tag.LOWER:
			mulop();
			factorA();
			termZ();
			break;

		case Tag.LOWER_EQUAL:
			mulop();
			factorA();
			termZ();
			break;

		case Tag.NOT_EQUAL:
			mulop();
			factorA();
			termZ();
			break;

		default:
			error();
			break;
		}
	}

	public void factorA() {
		switch (token.getTag()) {
		case Tag.INTEGER:
			factor();
			break;

		case Tag.ID:
			factor();
			break;

		case Tag.NOT:
			eat(Tag.NOT);
			factor();
			break;

		case Tag.QUOTE:
			break;

		case Tag.PAR_OPEN:
			factor();
			break;

		case Tag.SUBTRACT:
			eat(Tag.SUBTRACT);
			factor();
			break;

		default:
			error();
			break;
		}
	}

	public void factor() { // TO-DO: checar se está correto, porque no relatório tinham menos produções que os cases
		switch (token.getTag()) {
		case Tag.INTEGER:
			identifier();
			break;

		case Tag.ID:
			constant();
			break;

		case Tag.QUOTE:
			constant();
			break;

		case Tag.PAR_OPEN:
			eat(Tag.PAR_OPEN);
			expression();
			eat(Tag.PAR_CLOSE);
			break;

		default:
			error();
			break;
		}
	}

	public void relop() {
		switch (token.getTag()) {
		case Tag.EQUAL:
			eat(Tag.EQUAL);
			break;

		case Tag.GREATER:
			eat(Tag.GREATER);
			break;

		case Tag.GREATER_EQUAL:
			eat(Tag.GREATER_EQUAL);
			break;

		case Tag.LOWER:
			eat(Tag.LOWER);
			break;

		case Tag.LOWER_EQUAL:
			eat(Tag.LOWER_EQUAL);
			break;

		case Tag.NOT_EQUAL:
			eat(Tag.NOT_EQUAL);
			break;

		default:
			error();
			break;
		}
	}

	public void addop() {
		switch (token.getTag()) {
		case Tag.OR:
			eat(Tag.OR);
			break;

		case Tag.SUM:
			eat(Tag.SUM);
			break;

		case Tag.SUBTRACT:
			eat(Tag.SUBTRACT);
			break;

		default:
			error();
			break;
		}
	}

	public void mulop() {
		switch (token.getTag()) {
		case Tag.AND:
			eat(Tag.AND);
			break;

		case Tag.MULTIPLY:
			eat(Tag.MULTIPLY);
			break;

		case Tag.DIVIDE:
			eat(Tag.DIVIDE);
			break;

		default:
			error();
			break;
		}
	}

	public void constant() {
		switch (token.getTag()) {
		case Tag.INTEGER:
			integerConst();
			break;

		case Tag.QUOTE:
			literal();
			break;

		default:
			error();
			break;
		}
	}

	public void integerConst() {
		switch (token.getTag()) {
		case Tag.INTEGER:
			noZero();
			digit();
			break;

		case Tag.QUOTE:
			// eat(Tag.CONST_ZERO); // TO-DO: criar tag e descomentar
			break;

		default:
			error();
			break;
		}
	}

	public void literal() {
		switch (token.getTag()) {
		case Tag.QUOTE:
			eat(Tag.QUOTE);
			caractere();
			eat(Tag.QUOTE);
			break;

		default:
			error();
			break;
		}
	}

	public void identifier() {
		switch (token.getTag()) {
		case Tag.ID:
			letter();
			while (token.getTag() == Tag.ID) {
				letter();
			}
			while (token.getTag() == Tag.INTEGER) {
				digit();
			}
			// while (token.getTag() == Tag.UNDERSCORE) { // TO-DO: criar tag e descomentar
			//	eat(Tag.UNDERSCORE);
			// }
			break;

		default:
			error();
			break;
		}
	}

	public void letter() {
		switch (token.getTag()) {
		case Tag.ID:
			eat(Tag.LITERAL);
			break;

		default:
			error();
			break;
		}
	}

	public void digit() {
		switch (token.getTag()) {
		case Tag.INTEGER:
			eat(Tag.INTEGER);
			break;

		default:
			error();
			break;
		}
	}

	public void noZero() {
		switch (token.getTag()) {
		case Tag.INTEGER:
			// eat(Tag.CONST_NOT_ZERO); // TO-DO: criar tag e descomentar
			break;

		default:
			error();
			break;
		}
	}

	public void caractere() {
		switch (token.getTag()) {
		case Tag.ID:
			//eat(Tag.CONST_ASCII); // TO-DO: criar tag e descomentar;
			break;

		default:
			error();
			break;
		}
	}
}
