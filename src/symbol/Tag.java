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
    
    public static String getName(int tag) {
        if(tag >= 0 && tag <= 127)
            return String.valueOf((char) tag);
        if(tag == 256)
            return "ASSIGN";
        if(tag == 65533)
            return "QUOTE";        
        if(tag == 65535)
            return "EOF";        
        if(tag == 270)
            return "ID";        
        if(tag == 271)
            return "CONST";        
        if(tag == 272)
            return "DIVIDE";        
        if(tag == 273)
            return "LITERAL";        
        if(tag == 274)
            return "MULTIPLY";        
        if(tag == 275)
            return "SUBTRACT";        
        if(tag == 276)
            return "SUM";        
        if(tag == 277)
            return "CONST_ZERO";        
        if(tag == 278)
            return "CONST_NOT_ZERO";        
        if(tag == 279)
            return "CONST_ASCII";        
        if(tag == 285)
            return "EQUAL";        
        if(tag == 286)
            return "GREATER";        
        if(tag == 287)
            return "GREATER_EQUAL";        
        if(tag == 288)
            return "LOWER";        
        if(tag == 289)
            return "LOWER_EQUAL";        
        if(tag == 290)
            return "NOT_EQUAL";        
        if(tag == 300)
            return "AND";        
        if(tag == 301)
            return "BEGIN";        
        if(tag == 302)
            return "DO";        
        if(tag == 303)
            return "ELSE";        
        if(tag == 304)
            return "END";        
        if(tag == 305)
            return "IF";        
        if(tag == 306)
            return "INIT";        
        if(tag == 307)
            return "INTEGER";        
        if(tag == 308)
            return "IS";        
        if(tag == 309)
            return "NOT";        
        if(tag == 310)
            return "OR";        
        if(tag == 311)
            return "READ";        
        if(tag == 312)
            return "STOP";        
        if(tag == 313)
            return "STRING";        
        if(tag == 314)
            return "WHILE";        
        if(tag == 315)
            return "WRITE";        
        return "DESCONHECIDO";
    }
}
