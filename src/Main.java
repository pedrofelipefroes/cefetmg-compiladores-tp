public class Main {

	public static void main(String[] args) {
		try {
			if (hasEntryFile(args)) {
				Compiler compiler = new Compiler(args[0]);
				compiler.run();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private static boolean hasEntryFile(String[] args) {
		if (args.length != 1) {
			throw new IllegalArgumentException("Invalid entry file");
		}

		return true;
	}

}