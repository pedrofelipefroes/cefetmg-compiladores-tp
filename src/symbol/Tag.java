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
    CONST = 271, DIVIDE = '/', LITERAL = 273, MULTIPLY = '*', SUBTRACT = '-', SUM = '+', CONST_ZERO = 277,
            CONST_NOT_ZERO = 278, CONST_ASCII = 279, UNDERSCORE = '_',

    // relational operators
    EQUAL = 285, GREATER = 286, GREATER_EQUAL = 287, LOWER = 288, LOWER_EQUAL = 289, NOT_EQUAL = 290,

    // reserved words
    AND = 300, BEGIN = 301, DO = 302, ELSE = 303, END = 304, IF = 305, INIT = 306, INTEGER = 307, IS = 308, NOT = 309,
            OR = 310, READ = 311, STOP = 312, STRING = 313, WHILE = 314, WRITE = 315;
    
    public static String getName(int tag) {
        if(tag >= 0 && tag <= 127)
            return String.valueOf((char) tag);
        
        switch(tag) {
            case Tag.ASSIGN:
                return "ASSIGN";
            case Tag.QUOTE:
                return "QUOTE";        
            case Tag.EOF:
                return "EOF";        
            case Tag.ID:
                return "ID";        
            case Tag.CONST:
                return "CONST";        
            case Tag.DIVIDE:
                return "DIVIDE";        
            case Tag.LITERAL:
                return "LITERAL";        
            case Tag.MULTIPLY:
                return "MULTIPLY";        
            case Tag.SUBTRACT:
                return "SUBTRACT";        
            case Tag.SUM:
                return "SUM";        
            case Tag.CONST_ZERO:
                return "CONST_ZERO";        
            case Tag.CONST_NOT_ZERO:
                return "CONST_NOT_ZERO";        
            case Tag.CONST_ASCII:
                return "CONST_ASCII";        
            case Tag.EQUAL:
                return "EQUAL";        
            case Tag.GREATER:
                return "GREATER";        
            case Tag.GREATER_EQUAL:
                return "GREATER_EQUAL";        
            case Tag.LOWER:
                return "LOWER";        
            case Tag.LOWER_EQUAL:
                return "LOWER_EQUAL";        
            case Tag.NOT_EQUAL:
                return "NOT_EQUAL";        
            case Tag.AND:
                return "AND";        
            case Tag.BEGIN:
                return "BEGIN";        
            case Tag.DO:
                return "DO";        
            case Tag.ELSE:
                return "ELSE";        
            case Tag.END:
               return "END";        
            case Tag.IF:
                return "IF";        
            case Tag.INIT:
                return "INIT";        
            case Tag.INTEGER:
                return "INTEGER";        
            case Tag.IS:
                return "IS";        
            case Tag.NOT:
                return "NOT";        
            case Tag.OR:
                return "OR";        
            case Tag.READ:
                return "READ";        
            case Tag.STOP:
                return "STOP";        
            case Tag.STRING:
                return "STRING";        
            case Tag.WHILE:
                return "WHILE";        
            case Tag.WRITE:
                return "WRITE";        
            default:
                return "DESCONHECIDO";
        }
    }
}
