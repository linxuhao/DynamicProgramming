package KnapsackProblem;

public class Item {

	public final int weight;
	public final int value;
	
	public Item(int weight, int value) {
		super();
		this.weight = weight;
		this.value = value;
	}

	@Override
	public String toString() {
		return "Item [w=" + weight + ", v=" + value + "]";
	}
	
}
