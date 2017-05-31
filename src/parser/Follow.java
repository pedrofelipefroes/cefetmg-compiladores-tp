package parser;

import java.util.ArrayList;
import symbol.Tag;

public class Follow
{
    private String name;
    private ArrayList<Integer> list;
    
    public Follow(String name) {
        this.name = name;
        list = new ArrayList<Integer>();
    }
    
    public String getName() {
        return name;
    }
    
    public ArrayList<Integer> getList() {
        return list;
    }
    
    public void add(int tag) {
        this.list.add(Integer.valueOf(tag));
    }
    
    public static boolean isFollow(ArrayList<Follow> followList, String name, int tag) {
        for(int i = 0; i < followList.size(); i++)
            if(followList.get(i).getName().equals(name)) {
                for(int j = 0; j < followList.get(i).getList().size(); j++)
                    if(followList.get(i).getList().get(j).intValue() == tag)
                        return true;
                break;
            }
        
        return false;
    }
    
    public static void initializeFollowList(ArrayList<Follow> followList) {
        followList.add(new Follow("program"));
        Follow.addFollow(followList, "program", Tag.EOF);
        
        followList.add(new Follow("declList"));
        Follow.addFollow(followList, "declList", Tag.ID);
        Follow.addFollow(followList, "declList", Tag.IF);
        Follow.addFollow(followList, "declList", Tag.BEGIN);
        Follow.addFollow(followList, "declList", Tag.WHILE);
        Follow.addFollow(followList, "declList", Tag.READ);
        Follow.addFollow(followList, "declList", Tag.WRITE);
        
        followList.add(new Follow("decl"));
        Follow.addFollow(followList, "decl", Tag.DOT_COM);
        
        followList.add(new Follow("identList"));
        Follow.addFollow(followList, "identList", Tag.IS);
        
        followList.add(new Follow("type"));
        Follow.addFollow(followList, "type", Tag.DOT_COM);
        
        followList.add(new Follow("stmtList"));
        Follow.addFollow(followList, "stmtList", Tag.STOP);
        Follow.addFollow(followList, "stmtList", Tag.END);
        Follow.addFollow(followList, "stmtList", Tag.WHILE);
        
        followList.add(new Follow("stmt"));
        Follow.addFollow(followList, "stmt", Tag.DOT_COM);
        
        followList.add(new Follow("assignStmt"));
        Follow.addFollow(followList, "assignStmt", Tag.DOT_COM);
        
        followList.add(new Follow("ifStmt"));
        Follow.addFollow(followList, "ifStmt", Tag.DOT_COM);
        
        followList.add(new Follow("condition"));
        Follow.addFollow(followList, "condition", Tag.PAR_CLOSE);
        
        followList.add(new Follow("doStmt"));
        Follow.addFollow(followList, "doStmt", Tag.DOT_COM);
        
        followList.add(new Follow("doSuffix"));
        Follow.addFollow(followList, "doSuffix", Tag.DOT_COM);
        
        followList.add(new Follow("readStmt"));
        Follow.addFollow(followList, "readStmt", Tag.DOT_COM);
        
        followList.add(new Follow("writeStmt"));
        Follow.addFollow(followList, "writeStmt", Tag.DOT_COM);
        
        followList.add(new Follow("writable"));
        Follow.addFollow(followList, "writable", Tag.PAR_CLOSE);
        
        followList.add(new Follow("expression"));
        Follow.addFollow(followList, "expression", Tag.PAR_CLOSE);
        
        followList.add(new Follow("simpleExpr"));
        Follow.addFollow(followList, "simpleExpr", Tag.PAR_CLOSE);
        Follow.addFollow(followList, "simpleExpr", Tag.EQUAL);
        Follow.addFollow(followList, "simpleExpr", Tag.GREATER);
        Follow.addFollow(followList, "simpleExpr", Tag.GREATER_EQUAL);
        Follow.addFollow(followList, "simpleExpr", Tag.LOWER);
        Follow.addFollow(followList, "simpleExpr", Tag.LOWER_EQUAL);
        Follow.addFollow(followList, "simpleExpr", Tag.NOT_EQUAL);
        
        followList.add(new Follow("simpleExprZ"));
        Follow.addFollow(followList, "simpleExprZ", Tag.PAR_CLOSE);
        Follow.addFollow(followList, "simpleExprZ", Tag.EQUAL);
        Follow.addFollow(followList, "simpleExprZ", Tag.GREATER);
        Follow.addFollow(followList, "simpleExprZ", Tag.GREATER_EQUAL);
        Follow.addFollow(followList, "simpleExprZ", Tag.LOWER);
        Follow.addFollow(followList, "simpleExprZ", Tag.LOWER_EQUAL);
        Follow.addFollow(followList, "simpleExprZ", Tag.NOT_EQUAL);
        
        followList.add(new Follow("term"));
        Follow.addFollow(followList, "term", Tag.PAR_CLOSE);
        Follow.addFollow(followList, "term", Tag.EQUAL);
        Follow.addFollow(followList, "term", Tag.GREATER);
        Follow.addFollow(followList, "term", Tag.GREATER_EQUAL);
        Follow.addFollow(followList, "term", Tag.LOWER);
        Follow.addFollow(followList, "term", Tag.LOWER_EQUAL);
        Follow.addFollow(followList, "term", Tag.NOT_EQUAL);
        
        followList.add(new Follow("termZ"));
        Follow.addFollow(followList, "termZ", Tag.PAR_CLOSE);
        Follow.addFollow(followList, "termZ", Tag.EQUAL);
        Follow.addFollow(followList, "termZ", Tag.GREATER);
        Follow.addFollow(followList, "termZ", Tag.GREATER_EQUAL);
        Follow.addFollow(followList, "termZ", Tag.LOWER);
        Follow.addFollow(followList, "termZ", Tag.LOWER_EQUAL);
        Follow.addFollow(followList, "termZ", Tag.NOT_EQUAL);
        
        followList.add(new Follow("factorA"));
        Follow.addFollow(followList, "factorA", Tag.PAR_CLOSE);
        Follow.addFollow(followList, "factorA", Tag.EQUAL);
        Follow.addFollow(followList, "factorA", Tag.GREATER);
        Follow.addFollow(followList, "factorA", Tag.GREATER_EQUAL);
        Follow.addFollow(followList, "factorA", Tag.LOWER);
        Follow.addFollow(followList, "factorA", Tag.LOWER_EQUAL);
        Follow.addFollow(followList, "factorA", Tag.NOT_EQUAL);
        
        followList.add(new Follow("factor"));
        Follow.addFollow(followList, "factor", Tag.PAR_CLOSE);
        Follow.addFollow(followList, "factor", Tag.EQUAL);
        Follow.addFollow(followList, "factor", Tag.GREATER);
        Follow.addFollow(followList, "factor", Tag.GREATER_EQUAL);
        Follow.addFollow(followList, "factor", Tag.LOWER);
        Follow.addFollow(followList, "factor", Tag.LOWER_EQUAL);
        Follow.addFollow(followList, "factor    ", Tag.NOT_EQUAL);
        
        followList.add(new Follow("relop"));
        Follow.addFollow(followList, "relop", Tag.ID);
        Follow.addFollow(followList, "relop", Tag.INTEGER);
        Follow.addFollow(followList, "relop", Tag.QUOTE);
        Follow.addFollow(followList, "relop", Tag.PAR_OPEN);
        Follow.addFollow(followList, "relop", Tag.NOT);
        Follow.addFollow(followList, "relop", Tag.SUBTRACT);
        
        followList.add(new Follow("addop"));
        Follow.addFollow(followList, "addop", Tag.ID);
        Follow.addFollow(followList, "addop", Tag.INTEGER);
        Follow.addFollow(followList, "addop", Tag.QUOTE);
        Follow.addFollow(followList, "addop", Tag.PAR_OPEN);
        Follow.addFollow(followList, "addop", Tag.NOT);
        Follow.addFollow(followList, "addop", Tag.SUBTRACT);
        
        followList.add(new Follow("mulop"));
        Follow.addFollow(followList, "mulop", Tag.ID);
        Follow.addFollow(followList, "mulop", Tag.INTEGER);
        Follow.addFollow(followList, "mulop", Tag.QUOTE);
        Follow.addFollow(followList, "mulop", Tag.PAR_OPEN);
        Follow.addFollow(followList, "mulop", Tag.NOT);
        Follow.addFollow(followList, "mulop", Tag.SUBTRACT);
        
        followList.add(new Follow("constant"));
        Follow.addFollow(followList, "constant", Tag.PAR_CLOSE);
        Follow.addFollow(followList, "constant", Tag.EQUAL);
        Follow.addFollow(followList, "constant", Tag.GREATER);
        Follow.addFollow(followList, "constant", Tag.GREATER_EQUAL);
        Follow.addFollow(followList, "constant", Tag.LOWER);
        Follow.addFollow(followList, "constant", Tag.LOWER_EQUAL);
        Follow.addFollow(followList, "constant", Tag.NOT_EQUAL);
        
        followList.add(new Follow("integerConst"));
        Follow.addFollow(followList, "integerConst", Tag.PAR_CLOSE);
        Follow.addFollow(followList, "integerConst", Tag.EQUAL);
        Follow.addFollow(followList, "integerConst", Tag.GREATER);
        Follow.addFollow(followList, "integerConst", Tag.GREATER_EQUAL);
        Follow.addFollow(followList, "integerConst", Tag.LOWER);
        Follow.addFollow(followList, "integerConst", Tag.LOWER_EQUAL);
        Follow.addFollow(followList, "integerConst", Tag.NOT_EQUAL);
        
        followList.add(new Follow("literal"));
        Follow.addFollow(followList, "literal", Tag.PAR_CLOSE);
        Follow.addFollow(followList, "literal", Tag.EQUAL);
        Follow.addFollow(followList, "literal", Tag.GREATER);
        Follow.addFollow(followList, "literal", Tag.GREATER_EQUAL);
        Follow.addFollow(followList, "literal", Tag.LOWER);
        Follow.addFollow(followList, "literal", Tag.LOWER_EQUAL);
        Follow.addFollow(followList, "literal", Tag.NOT_EQUAL);
        
        followList.add(new Follow("identifier"));
        Follow.addFollow(followList, "identifier", Tag.COM);
        Follow.addFollow(followList, "identifier", Tag.IS);
        Follow.addFollow(followList, "identifier", Tag.ASSIGN);
        Follow.addFollow(followList, "identifier", Tag.PAR_CLOSE);
        Follow.addFollow(followList, "identifier", Tag.EQUAL);
        Follow.addFollow(followList, "identifier", Tag.GREATER);
        Follow.addFollow(followList, "identifier", Tag.GREATER_EQUAL);
        Follow.addFollow(followList, "identifier", Tag.LOWER);
        Follow.addFollow(followList, "identifier", Tag.LOWER_EQUAL);
        Follow.addFollow(followList, "identifier", Tag.NOT_EQUAL);
        
        followList.add(new Follow("letter"));
        Follow.addFollow(followList, "letter", Tag.COM);
        Follow.addFollow(followList, "letter", Tag.IS);
        Follow.addFollow(followList, "letter", Tag.ASSIGN);
        Follow.addFollow(followList, "letter", Tag.PAR_CLOSE);
        Follow.addFollow(followList, "letter", Tag.EQUAL);
        Follow.addFollow(followList, "letter", Tag.GREATER);
        Follow.addFollow(followList, "letter", Tag.GREATER_EQUAL);
        Follow.addFollow(followList, "letter", Tag.LOWER);
        Follow.addFollow(followList, "letter", Tag.LOWER_EQUAL);
        Follow.addFollow(followList, "letter", Tag.NOT_EQUAL);
        Follow.addFollow(followList, "letter", Tag.ID);
        Follow.addFollow(followList, "letter", Tag.CONST);
        Follow.addFollow(followList, "letter", (int) '_');
        
        followList.add(new Follow("digit"));
        Follow.addFollow(followList, "digit", Tag.PAR_CLOSE);
        Follow.addFollow(followList, "digit", Tag.EQUAL);
        Follow.addFollow(followList, "digit", Tag.GREATER);
        Follow.addFollow(followList, "digit", Tag.GREATER_EQUAL);
        Follow.addFollow(followList, "digit", Tag.LOWER);
        Follow.addFollow(followList, "digit", Tag.LOWER_EQUAL);
        Follow.addFollow(followList, "digit", Tag.NOT_EQUAL);
        Follow.addFollow(followList, "digit", Tag.ID);
        Follow.addFollow(followList, "digit", Tag.CONST);
        Follow.addFollow(followList, "digit", (int) '_');
        
        followList.add(new Follow("noZero"));
        Follow.addFollow(followList, "noZero", Tag.PAR_CLOSE);
        Follow.addFollow(followList, "noZero", Tag.EQUAL);
        Follow.addFollow(followList, "noZero", Tag.GREATER);
        Follow.addFollow(followList, "noZero", Tag.GREATER_EQUAL);
        Follow.addFollow(followList, "noZero", Tag.LOWER);
        Follow.addFollow(followList, "noZero", Tag.LOWER_EQUAL);
        Follow.addFollow(followList, "noZero", Tag.NOT_EQUAL);
        Follow.addFollow(followList, "noZero", Tag.ID);
        Follow.addFollow(followList, "noZero", Tag.CONST);
        Follow.addFollow(followList, "noZero", (int) '_');
        
        followList.add(new Follow("caractere"));
        Follow.addFollow(followList, "caractere", Tag.QUOTE);
    }
    
    public static void addFollow(ArrayList<Follow> followList, String name, int tag) {
        for(int i = followList.size()-1; i >= 0 ; i--)
            if(followList.get(i).getName().equals(name)) {
                followList.get(i).add(Integer.valueOf(tag));
                break;
            }
    }
}
