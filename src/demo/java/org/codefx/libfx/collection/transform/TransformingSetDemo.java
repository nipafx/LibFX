package org.codefx.libfx.collection.transform;

import java.util.HashSet;
import java.util.Set;

/**
 * Demonstrates how to use the {@link TransformingSet}.
 * <p>
 * The demonstrated example is based on the situation that a {@link Set} of {@link String}s which only ever contains
 * natural numbers as character sequences is to be represented as a {@code Set} of {@link Integer}s.
 * <p>
 * This is not entirely trivial as leading zeros allow multiple strings to be mapped to the same integer which will make
 * the transformation function non-inverse.
 */
public class TransformingSetDemo {

	// #begin FIELDS

	/**
	 * The set of strings which will be the inner/transformed set.
	 */
	private final Set<String> innerSet;

	/**
	 * A view ion the set which uses integers instead.
	 */
	private final Set<Integer> transformingSet;

	// #end FIELDS

	// #begin CONSTRUCTION, MAIN & TRANSFORMATION

	/**
	 * Creates a new demo.
	 */
	public TransformingSetDemo() {
		innerSet = new HashSet<>();
		transformingSet = new TransformingSet<>(
				innerSet,
				String.class, this::stringToInteger,
				Integer.class, this::integerToString);

		print("-- Initial state --");
		print("\t -> " + innerSet + " ~ " + transformingSet);
		print();
	}

	private Integer stringToInteger(String string) {
		return Integer.parseInt(string);
	}

	private String integerToString(Integer integer) {
		return integer.toString();
	}

	/**
	 * Runs this demo.
	 *
	 * @param args
	 *            command line arguments (will not be used)
	 */
	public static void main(String[] args) {
		print("Outputs are written as 'Modification -> innerSet.toString ~ transformingSet.toString'".toUpperCase());
		print();

		TransformingSetDemo demo = new TransformingSetDemo();

		demo.modifyingInnerSet();
		demo.modifyingTransformedSet();
		demo.breakingInverseFunctions();
		demo.typeSafety();
	}

	// #end CONSTRUCTION, MAIN & TRANSFORMATION

	// #begin DEMOS

	private void modifyingInnerSet() {
		print("-- Modifying the inner set --");

		print("Insert '0', '1', '2'");
		innerSet.add("0");
		innerSet.add("1");
		innerSet.add("2");
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Remove '1'");
		innerSet.remove("1");
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Insert '10', '11', '12'");
		innerSet.add("10");
		innerSet.add("11");
		innerSet.add("12");
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Remove strings ending in '0'");
		innerSet.removeIf(string -> string.endsWith("0"));
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Clear");
		innerSet.clear();
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print();
	}

	private void modifyingTransformedSet() {
		print("-- Modifying the transforming set --");

		print("Insert 0, 1, 2");
		transformingSet.add(0);
		transformingSet.add(1);
		transformingSet.add(2);
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Remove 1");
		transformingSet.remove(1);
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Insert 10, 11, 12");
		transformingSet.add(10);
		transformingSet.add(11);
		transformingSet.add(12);
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Remove odd numbers");
		transformingSet.removeIf(integer -> integer % 2 != 0);
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Clear");
		transformingSet.clear();
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print();
	}

	private void breakingInverseFunctions() {
		print("-- Breaking the contract with non-inverse functions --");
		print("The functions are non-inverse:");
		String leadingZeroTenString = "010";
		Integer ten = stringToInteger(leadingZeroTenString);
		String tenString = integerToString(ten);
		print("\t " + leadingZeroTenString + " -toInteger-> " + ten + " -toString-> " + tenString);
		print("This can be used to create unexpected behavior...");
		print();

		print("Insert '010' into inner set");
		innerSet.add("010");
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Insert '10' into inner set");
		innerSet.add("10");
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Sizes of different sets");
		print("\t inner set: " + innerSet.size());
		print("\t transforming set: " + transformingSet.size());
		print("\t new set: " + new HashSet<>(transformingSet).size() + " (!)");

		print("Now try to remove the value from the transforming set:");
		print("\t before: " + transformingSet);
		print("\t remove 10 (returns " + transformingSet.remove(10) + ") -> " + transformingSet);
		print("\t remove 10 (returns " + transformingSet.remove(10) + ") -> " + transformingSet);
		print("\t Damn it!");

		print("The transforming set does not contain its own elements:");
		print("\t 'transformingSet.contains(transformingSet.iterator().next())' -> "
				+ transformingSet.contains(transformingSet.iterator().next()));

		print("Clear");
		innerSet.clear();
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print();
	}

	private void typeSafety() {
		print("-- Using type tokens to increase type safety --");
		Set<Integer> transformingSetWithoutTokens = new TransformingSet<>(
				innerSet,
				Object.class, this::stringToInteger,
				Object.class, this::integerToString);

		print("Insert 0, 1, 2");
		transformingSet.add(0);
		transformingSet.add(1);
		transformingSet.add(2);
		print("\t -> " + innerSet + " ~ " + transformingSet + " ~ " + transformingSetWithoutTokens);

		print("Calling contains with an 'Object o'");
		Object o = new Object();
		print("\t 'innerSet.contains(o)': " + innerSet.contains(o));
		print("\t 'transformingSet.contains(o)': " + transformingSet.contains(o));
		try {
			print("\t 'transformingSetWithoutTokens.contains(o)': " + transformingSetWithoutTokens.contains(o));
		} catch (ClassCastException ex) {
			print("\t 'transformingSetWithoutTokens.contains(o)': CLASS CAST EXEPTION");
		}
	}

	// #end DEMOS

	private static void print() {
		System.out.println();
	}

	private static void print(String text) {
		System.out.println(text);
	}

}
