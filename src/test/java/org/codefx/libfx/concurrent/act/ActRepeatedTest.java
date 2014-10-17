package org.codefx.libfx.concurrent.act;

import static org.junit.Assert.assertEquals;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the class {@link ActRepeated}.
 */
public class ActRepeatedTest {

	// #region ATTRIBUTES & INITIALIZATION

	/**
	 * The string which passes the {@link #ACTION_GATEWAY}.
	 */
	private static final String ACTION_STRING = "action!";

	/**
	 * A string which does not pass the {@link #ACTION_GATEWAY}.
	 */
	private static final String NO_ACTION_STRING = "no action...";

	/**
	 * The gateway which has to be passed for the action to be executed.
	 */
	private static final Predicate<String> ACTION_GATEWAY = string -> Objects.equals(string, ACTION_STRING);

	/**
	 * The observable on which is acted.
	 */
	private Property<String> observable;

	/**
	 * The action which is undertaken. Increases {@link #executedActionCount}.
	 */
	private Consumer<String> action;

	/**
	 * Counts how many actions were executed.
	 */
	private AtomicInteger executedActionCount;

	/**
	 * Initializes the instances used to test.
	 */
	@Before
	public void setUp() {
		observable = new SimpleStringProperty(NO_ACTION_STRING);
		executedActionCount = new AtomicInteger(0);
		action = string -> executedActionCount.incrementAndGet();
	}

	// #end ATTRIBUTES & INITIALIZATION

	// #region SINGLE-THREADED TESTS

	/**
	 * Tests whether an {@link IllegalStateException} is thrown when {@link ActRepeated#act() act()} is called for the
	 * second time.
	 */
	@Test(expected = IllegalStateException.class)
	public void testThrowExceptionIfCallActTwice() {
		ActRepeated<String> act = new ActRepeated<>(observable, ACTION_GATEWAY, action);
		act.act();
		act.act();
	}

	/**
	 * Tests whether no action is executed if the initial value does not pass the {@link #ACTION_GATEWAY}.
	 */
	@Test
	public void testDoNotActIfInitialValueWrong() {
		ActRepeated<String> act = new ActRepeated<>(observable, ACTION_GATEWAY, action);
		act.act();

		assertEquals(0, executedActionCount.get());
	}

	/**
	 * Tests whether the action is executed when the initial value passes the {@link #ACTION_GATEWAY}.
	 */
	@Test
	public void testActWhenInitialValueCorrect() {
		observable.setValue(ACTION_STRING);
		ActRepeated<String> act = new ActRepeated<>(observable, ACTION_GATEWAY, action);
		act.act();

		assertEquals(1, executedActionCount.get());
	}

	/**
	 * Tests whether the action is executed repeatedly.
	 */
	@Test
	public void testActRepeatedlyWhenInitialValueWasCorrect() {
		observable.setValue(ACTION_STRING);
		ActRepeated<String> act = new ActRepeated<>(observable, ACTION_GATEWAY, action);
		// this executes the action for the first time
		act.act();

		// change the value and set the action string again; this must execute the action again
		observable.setValue(NO_ACTION_STRING);
		observable.setValue(ACTION_STRING);

		assertEquals(2, executedActionCount.get());
	}

	/**
	 * Tests whether the action is executed when the value is changed to one which passes the {@link #ACTION_GATEWAY}
	 * after waiting began.
	 */
	@Test
	public void testActWhenCorrectValueIsObserved() {
		ActRepeated<String> act = new ActRepeated<>(observable, ACTION_GATEWAY, action);
		act.act();

		observable.setValue(ACTION_STRING);

		assertEquals(1, executedActionCount.get());
	}

	/**
	 * Tests whether the action is executed repeatedly.
	 */
	@Test
	public void testActRepeatedlyWhenCorrectValueWasObserved() {
		ActRepeated<String> act = new ActRepeated<>(observable, ACTION_GATEWAY, action);
		act.act();

		// this executes the action for the first time
		observable.setValue(ACTION_STRING);

		// change the value and set the action string again; this must execute the action again
		observable.setValue(NO_ACTION_STRING);
		observable.setValue(ACTION_STRING);

		assertEquals(2, executedActionCount.get());
	}

	/**
	 * Tests whether {@link ActRepeated#cancel()} correctly prevents the execution of the action if it was not yet
	 * executed.
	 */
	@Test
	public void testCancelAfterNoAction() {
		ActRepeated<String> act = new ActRepeated<>(observable, ACTION_GATEWAY, action);
		act.act();

		// cancel and then set the value, which would lead to action execution
		act.cancel();
		observable.setValue(ACTION_STRING);

		assertEquals(0, executedActionCount.get());
	}

	/**
	 * Tests whether {@link ActRepeated#cancel()} correctly prevents the execution of the action if the initial value
	 * was correct.
	 */
	@Test
	public void testCancelAfterInitialValueWasCorrect() {
		observable.setValue(ACTION_STRING);
		ActRepeated<String> act = new ActRepeated<>(observable, ACTION_GATEWAY, action);
		// this executes the action for the first time
		act.act();

		// cancel and then reset the value, which would lead to action execution
		act.cancel();
		observable.setValue(NO_ACTION_STRING);
		observable.setValue(ACTION_STRING);

		assertEquals(1, executedActionCount.get());
	}

	/**
	 * Tests whether {@link ActRepeated#cancel()} correctly prevents the execution of the action if the correct value
	 * was already observed.
	 */
	@Test
	public void testCancelAfterCorrectValueWasObserved() {
		ActRepeated<String> act = new ActRepeated<>(observable, ACTION_GATEWAY, action);
		act.act();
		// this executes the action for the first time
		observable.setValue(ACTION_STRING);

		// cancel and then reset the value, which would lead to action execution
		act.cancel();
		observable.setValue(NO_ACTION_STRING);
		observable.setValue(ACTION_STRING);

		assertEquals(1, executedActionCount.get());
	}

	// #end SINGLE-THREADED TESTS

	// #region MULTI-THREADED TESTS

	/*
	 * Unfortunately I could not come up with multi-threaded tests... :(
	 */

	// #end MULTI-THREADED TESTS

}
