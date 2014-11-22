package org.codefx.libfx.serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Optional;
import java.util.Random;

/**
 * Demonstrates how to use the {@link SerializableOptional}.
 */
@SuppressWarnings("static-method")
public class SerializableOptionalDemo {

	/**
	 * Runs this demo.
	 *
	 * @param args
	 *            command line arguments (will not be used)
	 * @throws Exception
	 *             if (de)serialization fails
	 */
	public static void main(String[] args) throws Exception {
		SerializableOptionalDemo demo = new SerializableOptionalDemo();

		demo.serializeString();
		demo.failSerializingOptional();
		demo.serializeEmptySerializableOptional();
		demo.serializeNonEmptySerializableOptional();

		print("");

		demo.callMethodsWithSerializableOptional();
	}

	// DEMO

	// serialize "simple" objects, i.e. ones which contain no further instances, to demo serialization in general

	/**
	 * To get started, serialize a string, deserialize it and print its value.
	 *
	 * @throws Exception
	 *             if (de)serialization fails
	 */
	private void serializeString() throws Exception {
		String someString = "a string";
		String deserializedString = serializeAndDeserialize(someString);
		print("The deserialized 'String' is \"" + deserializedString + "\".");
	}

	/**
	 * Try the same with an {@code Optional<String>}, which will fail as {@link Optional} is not {@link Serializable}.
	 *
	 * @throws Exception
	 *             if (de)serialization fails
	 */
	private void failSerializingOptional() throws Exception {
		try {
			Optional<String> someOptional = Optional.of("another string");
			Optional<String> deserializedOptional = serializeAndDeserialize(someOptional);
			print("The deserialized 'Optional' should have the value \"" + deserializedOptional.get() + "\".");
		} catch (NotSerializableException e) {
			print("Serialization of 'Optional' failed as expected.");
		}
	}

	/**
	 * Create a {@link SerializableOptional} from an empty {@link Optional} and (de)serialize it successfully.
	 *
	 * @throws Exception
	 *             if (de)serialization fails
	 */
	private void serializeEmptySerializableOptional() throws Exception {
		Optional<String> someOptional = Optional.empty();
		SerializableOptional<String> serializableOptional = SerializableOptional.fromOptional(someOptional);
		Optional<String> deserializedOptional = serializeAndDeserialize(serializableOptional).asOptional();
		print("The deserialized empty 'SerializableOptional' has no value: " + !deserializedOptional.isPresent() + ".");
	}

	/**
	 * Create a {@link SerializableOptional} from a nonempty {@link Optional} and (de)serialize it successfully.
	 *
	 * @throws Exception
	 *             if (de)serialization fails
	 */
	private void serializeNonEmptySerializableOptional() throws Exception {
		Optional<String> someOptional = Optional.of("another string");
		SerializableOptional<String> serializableOptional = SerializableOptional.fromOptional(someOptional);
		Optional<String> deserializedOptional = serializeAndDeserialize(serializableOptional).asOptional();
		print("The deserialized non-empty 'SerializableOptional' has the value \"" + deserializedOptional.get() + "\".");
	}

	// use 'SerializableOptional' in method signatures

	/**
	 * Shows how to quickly wrap and unwrap an {@link Optional} for RPC method calls which rely on serialization.
	 * <p>
	 * Note that {@link SearchAndLog}'s methods have {@link SerializableOptional} as argument and return type.
	 */
	private void callMethodsWithSerializableOptional() {
		SearchAndLog searchAndLog = new SearchAndLog();
		for (int id = 0; id < 7; id++) {
			// unwrap the returned optional using 'asOptional'
			Optional<String> searchResult = searchAndLog.search(id).asOptional();
			// wrap the optional using 'fromOptional'; if used often, this could be a static import
			searchAndLog.log(id, SerializableOptional.fromOptional(searchResult));
		}
	}

	// USABILITY

	/**
	 * Serializes the specified instance to disk. Then deserializes the file and returns the deserialized value.
	 *
	 * @param <T>
	 *            the type of the serialized instance
	 * @param serialized
	 *            the instance to be serialized
	 * @return the deserialized instance
	 * @throws Exception
	 *             if (de)serialization fails
	 */
	private static <T> T serializeAndDeserialize(T serialized) throws Exception {
		File serializeFile = new File("_serialized");
		// serialize
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(serializeFile))) {
			out.writeObject(serialized);
		}
		// deserialize
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(serializeFile))) {
			@SuppressWarnings("unchecked")
			T deserialized = (T) in.readObject();
			return deserialized;
		}
	}

	/**
	 * Prints the specified text to the console.
	 *
	 * @param text
	 *            the text to print
	 */
	private static void print(String text) {
		System.out.println(text);
	}

	// INNER CLASS FOR METHOD CALLS

	/**
	 * A class with methods which have an optional return value or argument.
	 */
	@SuppressWarnings("javadoc")
	private static class SearchAndLog {

		Random random = new Random();

		public SerializableOptional<String> search(@SuppressWarnings("unused") int id) {
			boolean searchSuccessfull = random.nextBoolean();
			if (searchSuccessfull)
				return SerializableOptional.of("found something!");
			else
				return SerializableOptional.empty();
		}

		public void log(int id, SerializableOptional<String> item) {
			print("Search result for id " + id + ": " + item.asOptional().orElse("empty search result"));
		}

	}

}
