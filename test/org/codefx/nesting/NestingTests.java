package org.codefx.nesting;

import static org.codefx.nesting.testhelper.NestingAccess.getInnerIntegerProperty;
import static org.codefx.nesting.testhelper.NestingAccess.getInnerObservable;
import static org.codefx.nesting.testhelper.NestingAccess.getNestingObservable;
import static org.codefx.nesting.testhelper.NestingAccess.setInnerValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;

import org.codefx.nesting.testhelper.InnerValue;
import org.codefx.nesting.testhelper.OuterValue;

/**
 * This class defines tests which can be executed on specified instances of {@link Nesting}. They can thus be reused
 * across different test classes.
 */
public class NestingTests {

	// #region OBSERVABLE

	/**
	 * Tests whether the specified nesting's {@link Nesting#innerObservable()} contains the specified outer observable's
	 * {@link InnerValue#observable()}.
	 *
	 * @param nesting
	 *            the nesting to test
	 * @param outerObservable
	 *            the {@link ObservableValue} which is the outer observable for the nesting
	 */
	public static void testInnerObservableInitiallyCorrect(
			Nesting<Observable> nesting, ObservableValue<OuterValue> outerObservable) {

		assertSame(getNestingObservable(nesting), getInnerObservable(outerObservable));
	}

	/**
	 * Tests whether the specified nesting's {@link Nesting#innerObservable()} is updated correctly when the outer
	 * observables inner type is replaced.
	 *
	 * @param nesting
	 *            the nesting to test
	 * @param outerObservable
	 *            the {@link ObservableValue} which is the outer observable for the nesting
	 */
	public static void testInnerObservableWhenSettingNewInnerType(
			Nesting<Observable> nesting, ObservableValue<OuterValue> outerObservable) {

		InnerValue newInner = InnerValue.createWithObservables();
		setInnerValue(outerObservable, newInner);

		assertSame(getNestingObservable(nesting), newInner.observable());
	}

	/**
	 * Tests whether the specified nesting's {@link Nesting#innerObservable()} is updated correctly when the outer
	 * observable's inner type is replaced with one whose {@link InnerValue#observable()} is null.
	 *
	 * @param nesting
	 *            the nesting to test
	 * @param outerObservable
	 *            the {@link ObservableValue} which is the outer observable for the nesting
	 */
	public static void testInnerObservableWhenSettingNewInnerTypeWithNulls(
			Nesting<Observable> nesting, ObservableValue<OuterValue> outerObservable) {

		InnerValue newInnerWithNulls = InnerValue.createWithNulls();
		setInnerValue(outerObservable, newInnerWithNulls);

		assertNull(getNestingObservable(nesting));
	}

	//#end OBSERVABLE

	// #region INTEGER PROPERTY

	/**
	 * Tests whether the the specified nesting's {@link Nesting#innerObservable()} contains the specified outer
	 * observable's {@link InnerValue#integerProperty()}.
	 *
	 * @param nesting
	 *            the nesting to test
	 * @param outerObservable
	 *            the {@link ObservableValue} which is the outer observable for the nesting
	 */
	public static void testInnerIntegerPropertyInitiallyCorrect(
			Nesting<Observable> nesting, ObservableValue<OuterValue> outerObservable) {

		assertSame(getNestingObservable(nesting), getInnerIntegerProperty(outerObservable));
	}

	//#end INTEGER PROPERTY

}
