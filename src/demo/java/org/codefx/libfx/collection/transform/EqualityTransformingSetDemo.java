package org.codefx.libfx.collection.transform;

import java.util.Set;

/**
 * Demonstrates how to use the {@link EqualityTransformingSet}.
 * <p>
 * The demonstrated example is based on the situation that we want a set of strings which uses only their length for
 * equality comparison.
 */
public class EqualityTransformingSetDemo {

	/**
	 * A set of strings which uses the length for equality.
	 */
	private final Set<String> lengthSet;

	/**
	 * Creates a new demo.
	 */
	public EqualityTransformingSetDemo() {
		lengthSet = EqualityTransformingCollectionBuilder
				.forType(String.class)
				.withEquals((a, b) -> a.length() == b.length())
				.withHash(String::length)
				.buildSet();
	}

	/**
	 * Runs this demo.
	 *
	 * @param args
	 *            command line arguments (will not be used)
	 */
	public static void main(String[] args) {
		EqualityTransformingSetDemo demo = new EqualityTransformingSetDemo();

		demo.addSomeElements();
	}

	private void addSomeElements() {
		lengthSet.add("a");
		lengthSet.add("b");
		print(lengthSet.toString());
	}

	private static void print() {
		System.out.println();
	}

	private static void print(String text) {
		System.out.println(text);
	}

}
