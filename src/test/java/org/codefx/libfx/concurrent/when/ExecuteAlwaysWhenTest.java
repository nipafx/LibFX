package org.codefx.libfx.concurrent.when;

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
 * Tests the class {@link ExecuteAlwaysWhen}.
 */
public class ExecuteAlwaysWhenTest {

	// #region FIELDS & INITIALIZATION

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

	// #end FIELDS & INITIALIZATION

	// #region SINGLE-THREADED TESTS

	/**
	 * Tests whether an {@link IllegalStateException} is thrown when {@link ExecuteAlwaysWhen#executeWhen()
	 * executeWhen()} is called for the second time.
	 */
	@Test(expected = IllegalStateException.class)
	public void testThrowExceptionIfCallActTwice() {
		ExecuteAlwaysWhen<String> execute = new ExecuteAlwaysWhen<>(observable, ACTION_GATEWAY, action);
		execute.executeWhen();
		execute.executeWhen();
	}

	/**
	 * Tests whether no action is executed if the initial value does not pass the {@link #ACTION_GATEWAY}.
	 */
	@Test
	public void testDoNotActIfInitialValueWrong() {
		ExecuteAlwaysWhen<String> execute = new ExecuteAlwaysWhen<>(observable, ACTION_GATEWAY, action);
		execute.executeWhen();

		assertEquals(0, executedActionCount.get());
	}

	/**
	 * Tests whether the action is executed when the initial value passes the {@link #ACTION_GATEWAY}.
	 */
	@Test
	public void testExecuteWhenWhenInitialValueCorrect() {
		observable.setValue(ACTION_STRING);
		ExecuteAlwaysWhen<String> execute = new ExecuteAlwaysWhen<>(observable, ACTION_GATEWAY, action);
		execute.executeWhen();

		assertEquals(1, executedActionCount.get());
	}

	/**
	 * Tests whether the action is executed repeatedly.
	 */
	@Test
	public void testExecuteWhenRepeatedlyWhenInitialValueWasCorrect() {
		observable.setValue(ACTION_STRING);
		ExecuteAlwaysWhen<String> execute = new ExecuteAlwaysWhen<>(observable, ACTION_GATEWAY, action);
		// this executes the action for the first time
		execute.executeWhen();

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
	public void testExecuteWhenWhenCorrectValueIsObserved() {
		ExecuteAlwaysWhen<String> execute = new ExecuteAlwaysWhen<>(observable, ACTION_GATEWAY, action);
		execute.executeWhen();

		observable.setValue(ACTION_STRING);

		assertEquals(1, executedActionCount.get());
	}

	/**
	 * Tests whether the action is executed repeatedly.
	 */
	@Test
	public void testExecuteWhenRepeatedlyWhenCorrectValueWasObserved() {
		ExecuteAlwaysWhen<String> execute = new ExecuteAlwaysWhen<>(observable, ACTION_GATEWAY, action);
		execute.executeWhen();

		// this executes the action for the first time
		observable.setValue(ACTION_STRING);

		// change the value and set the action string again; this must execute the action again
		observable.setValue(NO_ACTION_STRING);
		observable.setValue(ACTION_STRING);

		assertEquals(2, executedActionCount.get());
	}

	/**
	 * Tests whether {@link ExecuteAlwaysWhen#cancel()} correctly prevents the execution of the action if it was not yet
	 * executed.
	 */
	@Test
	public void testCancelAfterNoAction() {
		ExecuteAlwaysWhen<String> execute = new ExecuteAlwaysWhen<>(observable, ACTION_GATEWAY, action);
		execute.executeWhen();

		// cancel and then set the value, which would lead to action execution
		execute.cancel();
		observable.setValue(ACTION_STRING);

		assertEquals(0, executedActionCount.get());
	}

	/**
	 * Tests whether {@link ExecuteAlwaysWhen#cancel()} correctly prevents the execution of the action if the initial
	 * value was correct.
	 */
	@Test
	public void testCancelAfterInitialValueWasCorrect() {
		observable.setValue(ACTION_STRING);
		ExecuteAlwaysWhen<String> execute = new ExecuteAlwaysWhen<>(observable, ACTION_GATEWAY, action);
		// this executes the action for the first time
		execute.executeWhen();

		// cancel and then reset the value, which would lead to action execution
		execute.cancel();
		observable.setValue(NO_ACTION_STRING);
		observable.setValue(ACTION_STRING);

		assertEquals(1, executedActionCount.get());
	}

	/**
	 * Tests whether {@link ExecuteAlwaysWhen#cancel()} correctly prevents the execution of the action if the correct
	 * value was already observed.
	 */
	@Test
	public void testCancelAfterCorrectValueWasObserved() {
		ExecuteAlwaysWhen<String> execute = new ExecuteAlwaysWhen<>(observable, ACTION_GATEWAY, action);
		execute.executeWhen();
		// this executes the action for the first time
		observable.setValue(ACTION_STRING);

		// cancel and then reset the value, which would lead to action execution
		execute.cancel();
		observable.setValue(NO_ACTION_STRING);
		observable.setValue(ACTION_STRING);

		assertEquals(1, executedActionCount.get());
	}

	// #end SINGLE-THREADED TESTS

	// #region MULTI-THREADED TESTS

	/*
	 * Unfortunately I could not come up with multi-threaded tests... :( The problem is that the only interesting part
	 * where threads interact is during the call to 'executeWhen'. So to check whether everything works as intended, it
	 * would be necessary to precisely count the number of actions executed during that call. Due to threading this
	 * seems impossible to do precisely; and because the time window in which the measurement would have to take place
	 * is so tiny (the method doesn't do much after all), I expect the margin of error to be so big to make any result
	 * meaningless.
	 */

	// #end MULTI-THREADED TESTS

}
