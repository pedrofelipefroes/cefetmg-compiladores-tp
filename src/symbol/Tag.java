package symbol;

public class Tag {
    public final static int
    // auxiliary symbols
    ASSIGN = 256, COM = ',', DOT_COM = ';', PAR_OPEN = '(', PAR_CLOSE = ')', QUOTE = 65533,

    // end of file
    EOF = 65535,

    // identifier
    ID = 270,

    // numeric constant, literal and operators
    CONST = 271, DIVIDE = 272, LITERAL = 273, MULTIPLY = 274, SUBTRACT = 275, SUM = 276, CONST_ZERO = 277,
            CONST_NOT_ZERO = 278, CONST_ASCII = 279,

    // relational operators
    EQUAL = 285, GREATER = 286, GREATER_EQUAL = 287, LOWER = 288, LOWER_EQUAL = 289, NOT_EQUAL = 290,

    // reserved words
    AND = 300, BEGIN = 301, DO = 302, ELSE = 303, END = 304, IF = 305, INIT = 306, INTEGER = 307, IS = 308, NOT = 309,
            OR = 310, READ = 311, STOP = 312, STRING = 313, WHILE = 314, WRITE = 315;
}
