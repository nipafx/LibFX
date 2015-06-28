package org.codefx.libfx.utils;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Demonstrates some use cases for {@link ClassFX}.
 */
@SuppressWarnings({ "static-method", "unused" })
public class ClassFXDemo {

	// #begin CONSTRUCTION & MAIN

	/**
	 * Creates a new demo.
	 */
	private ClassFXDemo() {
		// nothing to do yet
	}

	/**
	 * Runs this demo.
	 *
	 * @param args
	 *            command line arguments (will not be used)
	 */
	public static void main(String[] args) {
		ClassFXDemo demo = new ClassFXDemo();

		demo.castIntoOptional();
	}

	//#end CONSTRUCTION & MAIN

	// #begin DEMOS

	private void castIntoOptional() {
		Optional<Object> someOptional = Optional.of("42");

		// the usual way to get an 'Optional<String>' is to filter, then cast
		Optional<String> filterThenCast = someOptional
				.filter(String.class::isInstance)
				.map(String.class::cast);

		// with 'castIntoOptional' this is a single step
		Optional<String> castIntoOptional = someOptional
				.flatMap(obj -> ClassFX.castIntoOptional(obj, String.class));
	}

	private void castIntoStream() {
		Stream<Object> someStream = Stream.of(1, "42", new Object());

		// the usual way to get an 'Stream<String>' is to filter, then cast
		Stream<String> filterThenCast = someStream
				.filter(String.class::isInstance)
				.map(String.class::cast);

		// with 'castIntoStream' this is a single step
		Stream<String> castIntoStream = someStream
				.flatMap(obj -> ClassFX.castIntoStream(obj, String.class));
	}

	// #end DEMOS

}
