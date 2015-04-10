package org.codefx.libfx.collection.transform;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Demonstrates how to use the {@link OptionalTransformingSet}.
 * <p>
 * The demonstrated example is based on the situation that some code produces a {@link Set} of {@link Optional} strings.
 * Since this is generally undesired, the {@code OptionalTransformingSet} is used to extract the strings.
 */
public class OptionalTransformingSetDemo {

	// #region FIELDS

	/**
	 * The inner set, which - for some strange
	 */
	private final Set<Optional<String>> innerSet;

	private final Set<String> transformingSet;

	// #end FIELDS

	// #region CONSTRUCTION & MAIN

	/**
	 * Creates a new demo.
	 */
	public OptionalTransformingSetDemo() {
		innerSet = new HashSet<>();
		transformingSet = new OptionalTransformingSet<String>(innerSet, String.class, null);
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

		OptionalTransformingSetDemo demo = new OptionalTransformingSetDemo();

		demo.modifyingInnerSet();
		demo.modifyingTransformedSet();
		demo.exceptionOnInnerNullElements();
		demo.breakingInverseFunctions();
	}

	// #end CONSTRUCTION & MAIN

	// #region DEMOS

	private void modifyingInnerSet() {
		print("-- Modifying the inner set --");

		print("Insert optionals for 'A', 'B', 'C'");
		innerSet.add(Optional.of("A"));
		innerSet.add(Optional.of("B"));
		innerSet.add(Optional.of("C"));
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Remove optional for 'B'");
		innerSet.remove(Optional.of("B"));
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Insert empty optional");
		innerSet.add(Optional.empty());
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Insert optionals for 'Ax', 'Cx', 'Cy'");
		innerSet.add(Optional.of("Ax"));
		innerSet.add(Optional.of("Cx"));
		innerSet.add(Optional.of("Cy"));
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Remove optionals with content starting with 'C'");
		innerSet.removeIf(optional -> optional.map(string -> string.startsWith("C")).orElse(false));
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Clear");
		innerSet.clear();
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print();
	}

	private void modifyingTransformedSet() {
		print("-- Modifying the transforming set --");

		print("Insert 'A', 'B', 'C'");
		transformingSet.add(("A"));
		transformingSet.add(("B"));
		transformingSet.add(("C"));
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Remove 'B'");
		transformingSet.remove(("B"));
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Insert default value for empty optional (which is null)");
		transformingSet.add(null);
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Insert 'Ax', 'Cx', 'Cy'");
		transformingSet.add(("Ax"));
		transformingSet.add(("Cx"));
		transformingSet.add(("Cy"));
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Remove strings starting with 'C'");
		transformingSet.removeIf(string -> string != null && string.startsWith("C"));
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Clear");
		transformingSet.clear();
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print();
	}

	private void exceptionOnInnerNullElements() {
		print("-- Causing a NullPointerException when accessing an inner map with null --");
		print("The 'OptionalTransformingMap' does not allow the inner map to contain null"
				+ " ('Optional.empty()' should be used instead).");

		print("Inserting null into inner set will cause no exception");
		innerSet.add(null);

		print("But viewing a set with null will:");
		try {
			print("\t -> " + innerSet + " ~ " + transformingSet);
		} catch (NullPointerException ex) {
			print("\t " + ex.toString());
		}

		print("Clear");
		innerSet.clear();
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print();
	}

	private void breakingInverseFunctions() {
		print("-- Breaking the contract with non-inverse functions --");
		print("If a default value is specified which occurs in one of the optionals,"
				+ " the implicitly created transformations are non-inverse.");
		print("This can be used to create unexpected behavior...");
		print();

		print("Creating a map with default value 'DEFAULT'.");
		String defaultValue = "DEFAULT";
		Set<String> transformingSet = new OptionalTransformingSet<String>(innerSet, String.class, defaultValue);
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Insert empy optional into inner set");
		innerSet.add(Optional.empty());
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Insert optional with default string into inner set");
		innerSet.add(Optional.of(defaultValue));
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print("Sizes of different sets");
		print("\t inner set: " + innerSet.size());
		print("\t transforming set: " + transformingSet.size());
		print("\t new set: " + new HashSet<>(transformingSet).size() + " (!)");

		print("Now try to remove the value from the transforming set:");
		print("\t before: " + transformingSet);
		print("\t remove 'DEFAULT' (returns " + transformingSet.remove(defaultValue) + ") -> " + transformingSet);
		print("\t remove 'DEFAULT' (returns " + transformingSet.remove(defaultValue) + ") -> " + transformingSet);
		print("\t Damn it!");

		print("The transforming set does not contain its own elements:");
		print("\t 'transformingSet.contains(transformingSet.iterator().next())' -> "
				+ transformingSet.contains(transformingSet.iterator().next()));

		print("Clear");
		innerSet.clear();
		print("\t -> " + innerSet + " ~ " + transformingSet);

		print();
	}

	// #end DEMOS

	private static void print() {
		System.out.println();
	}

	private static void print(String text) {
		System.out.println(text);
	}

}
