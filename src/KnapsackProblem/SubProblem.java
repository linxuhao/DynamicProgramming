package KnapsackProblem;

public class SubProblem {
	
	public final int sackSize;
	public final int availableItems;
	
	public SubProblem(int sackSize, int availableItems) {
		super();
		this.sackSize = sackSize;
		this.availableItems = availableItems;
	}

	@Override
	public String toString() {
		return "F (" + sackSize + ", " + availableItems + ")";
	}

	
}
