import java.io.IOException;

import parser.Parser;

public class Compiler {
	
	private String fileName;
	
	public Compiler(String fileName) {
		this.fileName = fileName;
	}

	public void run() throws IOException {
		Parser parser = new Parser(fileName);
		parser.run();
	}
	
	
}
