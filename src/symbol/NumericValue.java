package symbol;

public class NumericValue extends Token {
	public final int value;

	public NumericValue(int value) {
		super(Tag.INTEGER);
		this.value = value;
	}

	@Override
	public String toString() {
		return "" + value;
	}
}
