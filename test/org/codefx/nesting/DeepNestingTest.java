package org.codefx.nesting;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

import org.codefx.nesting.testhelper.InnerValue;
import org.codefx.nesting.testhelper.NestingAccess;
import org.codefx.nesting.testhelper.OuterValue;
import org.codefx.nesting.testhelper.SomeValue;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests the {@link Nesting} implementation {@link DeepNesting}.
 */
@RunWith(Suite.class)
@SuiteClasses({
	DeepNestingTest.WithObservable.class,
	DeepNestingTest.WithProperty.class,
	DeepNestingTest.WithIntegerProperty.class,
})
public class DeepNestingTest {

	/**
	 * Tests a {@link DeepNesting} with an {@link Observable} as inner observable.
	 */
	public static class WithObservable extends AbstractDeepNestingTestForDefaultNesting<Observable> {

		@Override
		protected Nesting<Observable> createNewNestingFromOuterObservable(Property<OuterValue> outerObservable) {
			@SuppressWarnings("rawtypes")
			List<NestingStep> nestingSteps = new ArrayList<>();
			nestingSteps.add(outerValue -> ((OuterValue) outerValue).innerValueProperty());
			nestingSteps.add(innerValue -> ((InnerValue) innerValue).observable());

			return new DeepNesting<>(outerObservable, nestingSteps);
		}

		@Override
		protected Observable getInnerObservable(Property<OuterValue> outerObservable) {
			return NestingAccess.getInnerObservable(outerObservable);
		}

	}

	/**
	 * Tests a {@link DeepNesting} with an {@link ObservableValue} as inner observable.
	 */
	public static class WithProperty
			extends AbstractDeepNestingTestForDefaultNesting<Property<SomeValue>> {

		@Override
		protected Nesting<Property<SomeValue>> createNewNestingFromOuterObservable(
				Property<OuterValue> outerObservable) {

			@SuppressWarnings("rawtypes")
			List<NestingStep> nestingSteps = new ArrayList<>();
			nestingSteps.add(outerValue -> ((OuterValue) outerValue).innerValueProperty());
			nestingSteps.add(innerValue -> ((InnerValue) innerValue).property());

			return new DeepNesting<>(outerObservable, nestingSteps);
		}

		@Override
		protected Property<SomeValue> getInnerObservable(Property<OuterValue> outerObservable) {
			return NestingAccess.getInnerProperty(outerObservable);
		}

	}

	/**
	 * Tests a {@link DeepNesting} with an {@link ObservableValue} as inner observable.
	 */
	public static class WithIntegerProperty
			extends AbstractDeepNestingTestForDefaultNesting<IntegerProperty> {

		@Override
		protected Nesting<IntegerProperty> createNewNestingFromOuterObservable(
				Property<OuterValue> outerObservable) {

			@SuppressWarnings("rawtypes")
			List<NestingStep> nestingSteps = new ArrayList<>();
			nestingSteps.add(outerValue -> ((OuterValue) outerValue).innerValueProperty());
			nestingSteps.add(innerValue -> ((InnerValue) innerValue).integerProperty());

			return new DeepNesting<>(outerObservable, nestingSteps);
		}

		@Override
		protected IntegerProperty getInnerObservable(Property<OuterValue> outerObservable) {
			return NestingAccess.getInnerIntegerProperty(outerObservable);
		}

	}

}
