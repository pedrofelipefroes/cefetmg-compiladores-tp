package semantic;

import symbol.Tag;
import symbol.Word;

public class Condition
{
    String valueA;
    String valueB;
    int op;

    public Condition(String valueA, String valueB, int op)
    {
        this.valueA = valueA;
        this.valueB = valueB;
        this.op = op;
    }

    public boolean evaluate()
    {
        boolean bool = false;
        int comparison;
        
        switch (op) {
            case Tag.EQUAL:
                bool = valueA == valueB;
                break;

            case Tag.GREATER:
                comparison = valueA.compareTo(valueB);
                if (comparison > 0)
                    bool = true;
                else
                    bool = false;
                break;

            case Tag.GREATER_EQUAL:
                comparison = valueA.compareTo(valueB);
                if (comparison >= 0)
                    bool = true;
                else
                    bool = false;
                break;

            case Tag.LOWER:
                comparison = valueA.compareTo(valueB);
                if (comparison < 0)
                    bool = true;
                else
                    bool = false;
                break;

            case Tag.LOWER_EQUAL:
                comparison = valueA.compareTo(valueB);
                if (comparison <= 0)
                    bool = true;
                else
                    bool = false;
                break;

            case Tag.NOT_EQUAL:
                bool = valueA != valueB;
                break;
        }
        
        return bool;
    }
}
