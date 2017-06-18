package semantic;

import symbol.Tag;
import symbol.Word;

public class Condition
{

    private String valueA;
    private boolean idA;
    private String valueB;
    private boolean idB;
    private int stmtType;
    private int op;

    private boolean finalValue;
    private boolean value;

    public static Condition condition = new Condition(true, true);
    public final static int tagIF = 1;
    public final static int tagWHILE = 2;
    public final static int tagVOID = 3;

    /**
     * 
     * @param valueA primeiro operando
     * @param idA true se @valueA for um ID, falso caso contrário
     * @param valueB segundo operando
     * @param idB true se @valueB for um ID, falso caso contrário
     * @param stmtType tag que indicará se a condição é fruto de um IF ou de um WHILE
     * @param op tag do operador relacional
     */
    public Condition(String valueA, boolean idA, String valueB, boolean idB, int stmtType, int op)
    {
        this.valueA = valueA;
        this.idA = idA;
        this.valueB = valueB;
        this.idB = idB;
        this.stmtType = stmtType;
        this.op = op;
    }

    public Condition(boolean finalValue, boolean value)
    {
        this.finalValue = finalValue;
        this.value = value;
        this.stmtType = tagIF;
    }

    //No caso do 'else', considera-se o operador invertido
    public Condition switchOperators()
    {
        return new Condition(this.valueA, this.idA, this.valueB, this.idB, this.stmtType, switchOperator(this.op));
    }

    //Nega-se o operador
    public int switchOperator(int op)
    {
        int newOp;
        switch (op) {
            case Tag.EQUAL:
                newOp = Tag.NOT_EQUAL;
                break;

            case Tag.GREATER:
                newOp = Tag.LOWER_EQUAL;
                break;

            case Tag.GREATER_EQUAL:
                newOp = Tag.LOWER;
                break;

            case Tag.LOWER:
                newOp = Tag.GREATER_EQUAL;
                break;

            case Tag.LOWER_EQUAL:
                newOp = Tag.GREATER;
                break;

            case Tag.NOT_EQUAL:
                newOp = Tag.EQUAL;
                break;

            default:
                newOp = -1;
                break;
        }
        return newOp;
    }

    public boolean isRepeat()
    {
        if (stmtType == tagWHILE)
            return true;
        return false;
    }

    //Avalia-se a condição do objeto em questão
    public boolean evaluate()
    {
        if (finalValue)
            return value;

        boolean bool = false;
        
        Integer intA = null;
        Integer intB = null;

        //Se for ID, recolhe-se valor da tabela de símbolos
        if (idA)
            intA = Integer.valueOf(Word.getValue(this.valueA));
        else
            intA = Integer.valueOf(valueA);

        if (idB)
            intB = Integer.valueOf(Word.getValue(this.valueB));
        else
            intB = Integer.valueOf(valueB);

        //Realiza-se operação
        switch (op) {
            case Tag.EQUAL:
                bool = intA == intB;
                break;

            case Tag.GREATER:
                bool = intA > intB;
                break;

            case Tag.GREATER_EQUAL:
                bool = intA >= intB;
                break;

            case Tag.LOWER:
                bool = intA < intB;
                break;

            case Tag.LOWER_EQUAL:
                bool = intA <= intB;
                break;

            case Tag.NOT_EQUAL:
                bool = intA != intB;
                break;
        }

        return bool;
    }
}
