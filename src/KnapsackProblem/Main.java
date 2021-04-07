package KnapsackProblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * https://mp.weixin.qq.com/s?__biz=MzI1MTIzMzI2MA==&mid=2650573144&idx=1&sn=429dfe10b9c0231c72b82996f359e067&chksm=f1fe3fdbc689b6cd2187ef2a982c653efbfaf35eae8fbacb7a0148a8207bf15b6934b2bd0962&mpshare=1&scene=24&srcid=0406XwQ3tI4wS2B7w6h527nc&sharer_sharetime=1617710440053&sharer_shareid=2b93309840caae1000e16d75192a4a89&ascene=14&devicetype=android-29&version=27001543&nettype=sl2sfr&lang=fr&exportkey=CfmK23lO8zOHtGPOpclMdkM%3D&pass_ticket=AUGVPgZOoJBP1i%2B%2F1DhHfUlxy8paLz8rpL%2FY9GzrNHlWunjdQblzbj9x9hJJOZzj&wx_header=1
 * </br>
 * </br>
 * Sack : size 5kg</br>
 * item 1 : w1 = 1KG, v1 = 3$</br>
 * item 2 : w2 = 2KG, v2 = 4$</br>
 * item 3 : w3 = 3KG, v3 = 5$</br>
 * item 4 : w4 = 4KG, v4 = 6$</br>
 * </br>
 * Iterative thinking :</br>
 * I would like to get all the combinaision of the 4 items :</br>
 * Putting only 1 items, </br>
 * Putting only 2 items, </br>
 * Putting only 3 items, </br>
 * Putting only 4 items, </br>
 * and compute the value of each possibilities(also ignore the cases that is not
 * possible), then get the max value. </br>
 * To do that, for each possibilities, each item have 2 states : added, not
 * added </br>
 * </br>
 * Trying to put it in recursive / dynamic programming, </br>
 * Define : F(w,i) = for the sack of given siez w, best value of putting the
 * first i items</br>
 * </br>
 * in this case, we need to find F(5,4) : sack size of 5, putting first 4 items
 * (4 in total)</br>
 * </br>
 * step 1 :</br>
 * F(5,4), let's focus on item 4, 2 possibilities :</br>
 * 1. put the item 4, value is F((5-w4,i-1) + v4 = F(1,3) + 6</br>
 * 2. not putting the item 4, value is F(5, i-1) = F(5,3)</br>
 * </br>
 * => F(5,4) = max(F(1,3) + 6, F(5,3)) </br>
 * => F(W,I) = max(F(W-wI, I-1) + vI, F(W, I-1)) <b>state transition
 * equation</b></br>
 * => F(W-wI, I-1) and F(W, I-1) are <b>best children of F(W,I)</b></br>
 * Searching for <b>limit values</b>: </br>
 * F(0,any) = 0</br>
 * F(4,1) = F(3,1) = F(2,&) = F(1,1) = v1 = 3 => F(any,1) = v1</br>
 */
public class Main {

	public static final int value_not_set = -1;

	public static void main(String[] args) {
		// F(5,4)
		final int sackSize = 5;
		final List<Item> itemList = Arrays.asList(new Item(1, 3), new Item(2, 4), new Item(3, 5), new Item(4, 6));
		final int result = knapsackProblem(sackSize, itemList);
		System.out.println("result: " + result);
		
		//F(9,6)
		/*final int sackSize2 = 9;
		final List<Item> itemList2 = Arrays.asList(new Item(1, 2), new Item(2, 4), new Item(3, 5), new Item(4, 6), new Item(5, 7), new Item(6, 8));
		final int result2 = knapsackProblem(sackSize2, itemList2);
		System.out.println("result2: " + result2);*/
	}

	public static int knapsackProblem(final int sackSize, final List<Item> itemList) {
		//See the output, there are many unused cell in the table, I may use a map of sub problem to save on the space complexity
		final int[][] values = initializeValueTable(sackSize, itemList);
		// display table
		printTable(values);
		final Queue<SubProblem> equationQueue = new LinkedList<>();
		final Stack<SubProblem> computeStack = new Stack<>();
		// the first sub problem is the problem itself
		equationQueue.add(new SubProblem(sackSize, itemList.size()));
		//Cut the problem into sub problems, and store them in computeStack
		while (!equationQueue.isEmpty()) {
			final SubProblem sp = equationQueue.poll();
			if (sp.sackSize >= 0 && sp.availableItems >= 0
					&& value_not_set == values[sp.sackSize][sp.availableItems - 1]) {
				// add the sub problem to be computed later
				computeStack.push(sp);
				// add childrens of this sub problem in equation queue
				equationQueue.addAll(getChildrenSubProblems(itemList, sp));
			}
		}
		// Solve the sub problems from the smallest one to the problem itself
		while (!computeStack.isEmpty()) {
			final SubProblem sp = computeStack.pop();
			System.out.println("******************************************");
			System.out.println("Computing " + sp);
			// F(W,I) = max(F(W-wI, I-1) + vI, F(W, I-1))
			values[sp.sackSize][sp.availableItems - 1] = Math.max(
					getValueSafe(values, sp.sackSize - getLastItemForSubProblem(itemList, sp).weight,
							sp.availableItems - 1) + getLastItemForSubProblem(itemList, sp).value,
					getValueSafe(values, sp.sackSize, sp.availableItems - 1));
			printTable(values);
		}

		return getValueSafe(values, sackSize, itemList.size());
	}

	/**
	 * equation : F(W,I) = max(F(W-wI, I-1) + vI, F(W, I-1)) </br>
	 * childrens are F(W-wI, I-1) & F(W, I-1)</br>
	 */
	private static List<SubProblem> getChildrenSubProblems(final List<Item> itemList, final SubProblem sp) {
		List<SubProblem> list = new ArrayList<>();
		list.add(new SubProblem(sp.sackSize - getLastItemForSubProblem(itemList, sp).weight,
				sp.availableItems - 1));
		list.add(new SubProblem(sp.sackSize, sp.availableItems - 1));
		//remove not possible childrens
		list.removeIf(children -> children.sackSize < 0 || children.availableItems < 1);
		return list;
	}

	private static int getValueSafe(int[][] values, int i, int j) {
		if (i >= 0 && j > 0) {
			return values[i][j-1];
		}
		return Integer.MIN_VALUE;
	}

	private static Item getLastItemForSubProblem(final List<Item> itemList, final SubProblem sp) {
		return itemList.get(sp.availableItems - 1);
	}

	private static int[][] initializeValueTable(final int sackSize, final List<Item> itemList) {
		int[][] values = new int[sackSize + 1][];
		for (int i = 0; i <= sackSize; i++) {
			values[i] = new int[itemList.size()];
			if (0 == i) {
				//when size = 0, no item can be put in the sack => 0
				Arrays.setAll(values[i], pos -> 0);
			} else {
				//Position 0 is the case when we can put only the first item in sack no matter the size 
				//=> allways the value of first item
				Arrays.setAll(values[i], pos -> 0 == pos ? itemList.get(0).value : value_not_set);
			}
		}
		return values;
	}

	private static void printTable(final int[][] values) {
		System.out.println(Arrays.deepToString(values).replace("], ", "]\n"));
	}
}
