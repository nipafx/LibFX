package org.codefx.libfx.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Optional;

import org.junit.Test;

/**
 * Tests the class {@link SerializableOptional}.
 */
public class SerializableOptionalTest {

	// #region CONSTRUCTION

	/**
	 * Tests whether a {@code SerializableOptional} can be created from an empty {@code Optional} with
	 * {@link SerializableOptional#fromOptional(Optional) fromOptional(Optional)}.
	 */
	@Test
	public void testFromEmptyOptional() {
		Optional<String> empty = Optional.empty();
		SerializableOptional<String> emptySerializable = SerializableOptional.fromOptional(empty);

		// note that 'Optional' is a value-based class and reference identity must not be relied upon;
		// hence not 'assertSame'
		assertEquals(empty, emptySerializable.asOptional());
	}

	/**
	 * Tests whether a {@code SerializableOptional} can be created from a non-empty {@code Optional} with
	 * {@link SerializableOptional#fromOptional(Optional) fromOptional(Optional)}.
	 */
	@Test
	public void testFromNonEmptyOptional() {
		Optional<String> nonEmpty = Optional.of("Not Empty!");
		SerializableOptional<String> nonEmptySerializable = SerializableOptional.fromOptional(nonEmpty);

		// note that 'Optional' is a value-based class and reference identity must not be relied upon;
		// hence not 'assertSame'
		assertEquals(nonEmpty, nonEmptySerializable.asOptional());
	}

	/**
	 * Tests whether a {@code SerializableOptional} can be created from a null reference with
	 * {@link SerializableOptional#ofNullable(Serializable) ofNullable(Serializable)}.
	 */
	@Test
	public void testOfNullableWithNull() {
		SerializableOptional<String> emptySerializable = SerializableOptional.ofNullable(null);

		assertFalse(emptySerializable.asOptional().isPresent());
	}

	/**
	 * Tests whether a {@code SerializableOptional} can be created from a non-null reference with
	 * {@link SerializableOptional#ofNullable(Serializable) ofNullable(Serializable)}.
	 */
	@Test
	public void testOfNullableWithNonNull() {
		String notNull = "Not Null!";
		SerializableOptional<String> emptySerializable = SerializableOptional.ofNullable(notNull);

		assertEquals(notNull, emptySerializable.asOptional().get());
	}

	// #end CONSTRUCTION

	// #region SERIALIZATION

	/**
	 * Tests whether {@link SerializableOptional} with an empty {@link Optional} can be serialized.
	 *
	 * @throws Exception
	 *             if serialization fails
	 */
	@Test
	public void testSerializeEmpty() throws Exception {
		SerializableOptional<String> empty = SerializableOptional.ofNullable(null);
		// serialize
		try (ObjectOutputStream out = new ObjectOutputStream(new ByteArrayOutputStream())) {
			out.writeObject(empty);
		}
	}

	/**
	 * Tests whether {@link SerializableOptional} with a non-empty {@link Optional} can be serialized.
	 *
	 * @throws Exception
	 *             if serialization fails
	 */
	@Test
	public void testSerializeNonEmpty() throws Exception {
		SerializableOptional<String> nonEmpty = SerializableOptional.ofNullable("Not Null!");
		// serialize
		try (ObjectOutputStream out = new ObjectOutputStream(new ByteArrayOutputStream())) {
			out.writeObject(nonEmpty);
		}
	}

	/**
	 * Tests whether {@link SerializableOptional} with an empty {@link Optional} can be deserialized.
	 *
	 * @throws Exception
	 *             if serialization fails
	 */
	@Test
	public void testDeserializeEmpty() throws Exception {
		SerializableOptional<String> emptyToSerialize = SerializableOptional.ofNullable(null);
		SerializableOptional<String> deserializedEmpty = null;

		ByteArrayOutputStream serialized = new ByteArrayOutputStream();
		// serialize
		try (ObjectOutputStream out = new ObjectOutputStream(serialized)) {
			out.writeObject(emptyToSerialize);
		}
		// deserialize
		try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(serialized.toByteArray()))) {
			@SuppressWarnings("unchecked")
			SerializableOptional<String> deserialized = (SerializableOptional<String>) in.readObject();
			deserializedEmpty = deserialized;
		}

		assertEquals(emptyToSerialize.asOptional(), deserializedEmpty.asOptional());
	}

	/**
	 * Tests whether {@link SerializableOptional} with a non-empty {@link Optional} can be deserialized.
	 *
	 * @throws Exception
	 *             if serialization fails
	 */
	@Test
	public void testDeserializeNonEmpty() throws Exception {
		SerializableOptional<String> nonEmptyToSerialize = SerializableOptional.ofNullable("Not Null!");
		SerializableOptional<String> deserializedNonEmpty = null;

		ByteArrayOutputStream serialized = new ByteArrayOutputStream();
		// serialize
		try (ObjectOutputStream out = new ObjectOutputStream(serialized)) {
			out.writeObject(nonEmptyToSerialize);
		}
		// deserialize
		try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(serialized.toByteArray()))) {
			@SuppressWarnings("unchecked")
			SerializableOptional<String> deserialized = (SerializableOptional<String>) in.readObject();
			deserializedNonEmpty = deserialized;
		}

		assertEquals(nonEmptyToSerialize.asOptional(), deserializedNonEmpty.asOptional());
	}

	// #end SERIALIZATION

}
