package symbol;

public class Tag {
	public final static int
		// auxiliary symbols
		ASSIGN = 256, COM = 257, DOT_COM = 258, PAR_OPEN = 259, PAR_CLOSE = 260, QUOTE = 261,

		// end of file
		EOF = 65535,
		
		// identifier
		ID = 270,

		// numeric constant and operators
		CONST = 271, DIVIDE = 272, MULTIPLY = 273, SUBTRACT = 274, SUM = 275,

		// relational operators
		EQUAL = 285, GREATER = 286, GREATER_EQUAL = 287, LOWER = 288, LOWER_EQUAL = 289, NOT_EQUAL = 290,

		// reserved words
		AND = 300, BEGIN = 301, DO = 302, ELSE = 303, IF = 304, INIT = 305, INTEGER = 306, IS = 307, NOT = 308,
		OR = 309, READ = 310, STOP = 311, STRING = 312, WHILE = 313;
}
