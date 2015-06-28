package org.codefx.libfx.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

/**
 * Tests {@link ClassFX}.
 */
@SuppressWarnings("javadoc")
public class ClassFXTest {

	// #begin CAST_INTO_OPTIONAL

	@Test
	public void castIntoOptional_nullInstance_emptyOptional() throws Exception {
		Optional<String> cast = ClassFX.castIntoOptional(null, String.class);

		assertThat(cast).isEmpty();
	}

	@Test
	public void castIntoOptional_instanceOfWrongType_emptyOptional() throws Exception {
		Optional<String> cast = ClassFX.castIntoOptional(new Integer(42), String.class);

		assertThat(cast).isEmpty();
	}

	@Test
	public void castIntoOptional_instanceOfCorrectType_optionalWithSameInstance() throws Exception {
		Optional<String> cast = ClassFX.castIntoOptional("42", String.class);

		assertThat(cast).contains("42");
	}

	// #end CAST_INTO_OPTIONAL

	// #begin CAST_INTO_STREAM

	@Test
	public void castIntoStream_nullInstance_emptyStream() throws Exception {
		Stream<String> cast = ClassFX.castIntoStream(null, String.class);

		List<String> asList = cast.collect(Collectors.toList());
		assertThat(asList).isEmpty();
	}

	@Test
	public void castIntoStream_instanceOfWrongType_emptyStream() throws Exception {
		Stream<String> cast = ClassFX.castIntoStream(new Integer(42), String.class);

		List<String> asList = cast.collect(Collectors.toList());
		assertThat(asList).isEmpty();
	}

	@Test
	public void castIntoStream_instanceOfCorrectType_optionalWithSameInstance() throws Exception {
		Stream<String> cast = ClassFX.castIntoStream("42", String.class);

		List<String> asList = cast.collect(Collectors.toList());
		assertThat(asList).containsExactly("42");
	}

	// #end CAST_INTO_STREAM

}
