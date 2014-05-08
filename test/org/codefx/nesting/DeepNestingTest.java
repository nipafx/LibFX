package org.codefx.nesting;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import org.codefx.nesting.testhelper.InnerValue;
import org.codefx.nesting.testhelper.OuterValue;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the class {@link DeepNesting}. All tests are defined in {@link NestingTests} and called from here.
 */
public class DeepNestingTest {

	// #region OUTER OBSERVALE & NESTINGS

	/**
	 * An outer observable from which nested instances are instantiated.
	 */
	private ObservableValue<OuterValue> outerObservable;

	/**
	 * A nesting on {@link #outerObservable} -> outerType -> innerType -> observable.
	 */
	private Nesting<Observable> nestingOnObservable;

	//#end OUTER OBSERVALE & NESTINGS

	/**
	 * Initializes all nestings before each test.
	 */
	@Before
	@SuppressWarnings("rawtypes")
	private void initialiteNestings() {
		outerObservable = new SimpleObjectProperty<>(OuterValue.createWithInnerType());

		List<NestingStep> nestingSteps = new ArrayList<>();
		nestingSteps.add(outerType -> ((OuterValue) outerType).innerValueProperty());
		nestingSteps.add(innerType -> ((InnerValue) innerType).observable());
		nestingOnObservable = new DeepNesting<>(outerObservable, nestingSteps);
	}

	// #region OBSERVABLE TESTS

	@Test
	@SuppressWarnings("javadoc")
	public void testInnerObservableInitiallyCorrect() {
		NestingTests.testInnerObservableInitiallyCorrect(nestingOnObservable, outerObservable);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void testInnerObservableWhenSettingNewInnerType() {
		NestingTests.testInnerObservableWhenSettingNewInnerType(nestingOnObservable, outerObservable);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void testInnerObservableWhenSettingNewInnerTypeWithNulls() {
		NestingTests.testInnerObservableWhenSettingNewInnerTypeWithNulls(
				nestingOnObservable, outerObservable);
	}

	//#end OBSERVABLE TESTS

}
