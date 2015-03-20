package org.codefx.libfx.collection.transform;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Demonstrates how to use the {@link TransformingSet}.
 * <p>
 * The demonstrated example is based on the situation that some code produces a {@link Set} of {@link Optional} strings.
 * Since this is generally undesired, the {@code TransformingSet} is used to extract the strings.
 * <p>
 * This is a tricky task. The contract for {@code TransformingSet} fixes the mapping for nulls to {@code null <-> null},
 * which means we can not map {@code Optional.empty() <-> null}. Instead we have to represent it with another string
 * (see {@link #DEFAULT_STRING}). This is risky because if this string occurs in an Optional in the {@link #innerSet},
 * the transformations below are not inverse to one another which breaks the contract of {@link TransformingSet}.
 */
public class TransformingSetDemo {

	// #region FIELDS

	/**
	 * The string corresponding to an empty {@link Optional}.
	 */
	private static final String DEFAULT_STRING = "DEFAULT";

	/**
	 * The inner set, which - for some strange
	 */
	private final Set<Optional<String>> innerSet;

	private final Set<String> transformingSet;

	// #end FIELDS

	// #region CONSTRUCTION, MAIN & TRANSFORMATION

	/**
	 * Creates a new demo.
	 */
	public TransformingSetDemo() {
		innerSet = new HashSet<>();
		transformingSet = new TransformingSet<>(
				innerSet,
				Optional.class, this::optionalToString,
				String.class, this::stringToOptional);
	}

	/**
	 * Runs this demo.
	 *
	 * @param args
	 *            command line arguments (will not be used)
	 */
	public static void main(String[] args) {
		print("Outputs are written as 'Modification -> innerSet.toStrin ~ transformingSet.toString'".toUpperCase());
		print();

		TransformingSetDemo demo = new TransformingSetDemo();

		demo.modifyingInnerSet();
		demo.modifyingTransformedSet();
		demo.breakingInverseFunctions();
	}

	private String optionalToString(Optional<String> optional) {
		return optional.orElse(DEFAULT_STRING);
	}

	private Optional<String> stringToOptional(String string) {
		return DEFAULT_STRING.equals(string) ? Optional.empty() : Optional.of(string);
	}

	// #end CONSTRUCTION, MAIN & TRANSFORMATION

	// #region DEMOS

	private void modifyingInnerSet() {
		print("-- Modifying the inner set --");

		print("Insert optionals for 'A', 'B', 'C'");
		innerSet.add(Optional.of("A"));
		innerSet.add(Optional.of("B"));
		innerSet.add(Optional.of("C"));
		print("\t-> " + innerSet + " ~ " + transformingSet);

		print("Remove optional for 'B'");
		innerSet.remove(Optional.of("B"));
		print("\t-> " + innerSet + " ~ " + transformingSet);

		print("Insert empty optional");
		innerSet.add(Optional.empty());
		print("\t-> " + innerSet + " ~ " + transformingSet);

		print("Insert optionals for 'Ax', 'Cx', 'Cy'");
		innerSet.add(Optional.of("Ax"));
		innerSet.add(Optional.of("Cx"));
		innerSet.add(Optional.of("Cy"));
		print("\t-> " + innerSet + " ~ " + transformingSet);

		print("Remove optionals with content starting with 'C'");
		innerSet.removeIf(optional -> optional.map(string -> string.startsWith("C")).orElse(false));
		print("\t-> " + innerSet + " ~ " + transformingSet);

		print("Clear");
		innerSet.clear();
		print("\t-> " + innerSet + " ~ " + transformingSet);

		print();
	}

	private void modifyingTransformedSet() {
		print("-- Modifying the transforming set --");

		print("Insert 'A', 'B', 'C'");
		transformingSet.add(("A"));
		transformingSet.add(("B"));
		transformingSet.add(("C"));
		print("\t-> " + innerSet + " ~ " + transformingSet);

		print("Remove 'B'");
		transformingSet.remove(("B"));
		print("\t-> " + innerSet + " ~ " + transformingSet);

		print("Insert default value (for empty optional)");
		transformingSet.add(DEFAULT_STRING);
		print("\t-> " + innerSet + " ~ " + transformingSet);

		print("Insert 'Ax', 'Cx', 'Cy'");
		transformingSet.add(("Ax"));
		transformingSet.add(("Cx"));
		transformingSet.add(("Cy"));
		print("\t-> " + innerSet + " ~ " + transformingSet);

		print("Remove strings starting with 'C'");
		transformingSet.removeIf(string -> string.startsWith("C"));
		print("\t-> " + innerSet + " ~ " + transformingSet);

		print("Clear");
		transformingSet.clear();
		print("\t-> " + innerSet + " ~ " + transformingSet);

		print();
	}

	private void breakingInverseFunctions() {
		print("-- Breaking the contract with non-inverse functions --");
		print("The functions are non-inverse:");
		Optional<String> defaultOptional = Optional.of(DEFAULT_STRING);
		String defaultString = optionalToString(defaultOptional);
		Optional<String> emptyOptional = stringToOptional(defaultString);
		print("\t" + defaultOptional + " -toString-> " + defaultString + " -toOptional-> " + emptyOptional);
		print("This can be used to create unexpected behavior...");
		print();

		print("Insert empty optional into inner set");
		innerSet.add(Optional.empty());
		print("\t-> " + innerSet + " ~ " + transformingSet);

		print("Insert optional with default string into inner set");
		innerSet.add(Optional.of(DEFAULT_STRING));
		print("\t-> " + innerSet + " ~ " + transformingSet);

		print("Sizes of different sets");
		print("\tinner set: " + innerSet.size());
		print("\ttransforming set: " + transformingSet.size());
		print("\tnew set: " + new HashSet<>(transformingSet).size() + " (!)");

		print("Now try to remove the value from the transforming set:");
		print("\tbefore: " + transformingSet);
		print("\tremove (returns " + transformingSet.remove(DEFAULT_STRING) + ") -> " + transformingSet);
		print("\tremove (returns " + transformingSet.remove(DEFAULT_STRING) + ") -> " + transformingSet);
		print("\tDamn it!");

		print("The transforming set does not contain its own elements:");
		print("\t'transformingSet.contains(transformingSet.iterator().next())' -> "
				+ transformingSet.contains(transformingSet.iterator().next()));

		print("Clear");
		innerSet.clear();
		print("\t-> " + innerSet + " ~ " + transformingSet);

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
