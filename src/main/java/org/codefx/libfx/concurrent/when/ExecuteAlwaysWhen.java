package org.codefx.libfx.concurrent.when;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Executes an action when an {@link ObservableValue}'s value fulfills a certain condition.
 * <p>
 * The action will not be executed before {@link #executeWhen()} is called. The action is executed every time the value
 * passes the condition. If this can happen in parallel in several threads, the action must be thread-safe as no further
 * synchronization is provided by this class. Further execution can be prevented by calling {@link #cancel()}.
 * <p>
 * TODO define the contract
 *
 * @param <T>
 *            the type the observed {@link ObservableValue}'s wraps
 */
public class ExecuteAlwaysWhen<T> {

	/*
	 * If no other threads were involved the class would be simple. It would suffice to check the observable's current
	 * value. If it passes the condition, execute the action. Also, a listener which processes each new value in the
	 * same way has to be added.
	 */
	/*
	 * But since other threads are allowed to interfere, this could fail. If a correct value is set between the check
	 * and attaching the listener, this value would not be processed and the action would not be executed. To prevent
	 * this the listener is added first and only then is the current value checked and the action possibly executed.
	 */
	/*
	 * Now, if another thread sets the correct value after the listener was added but before the current value is
	 * processed, the action would be executed twice. (Actually, if the value is switched fast enough by different
	 * threads, each could be in the middle of executing the listener when the check of the initial value finally
	 * proceeds. This would lead to multiple desired executions plus the undesired one of the initial check.) Note that
	 * this problem can only occur until the initial value is processed. After that only the listener can execute the
	 * action and there is only one so no funny stuff can happen.
	 */

	/*
	 * TODO: The problem is that tryExecuteActionTheFirstTime as well as a lot of tryExecuteActionRepeatedly could each
	 * be at any point in their execution. This makes it hard to come up with a scheme which allows them to communicate
	 * such that the action is not executed n + 1 times if it changes to the correct value n times (both during
	 * executeWhen')
	 */

	// #region ATTRIBUTES

	/**
	 * The {@link ObservableValue} upon whose value the action's execution depends.
	 */
	private final ObservableValue<T> observable;

	/**
	 * The condition the {@link #observable}'s value must fulfill for {@link #action} to be executed.
	 */
	private final Predicate<T> condition;

	/**
	 * The action which will be executed.
	 */
	private final Consumer<T> action;

	/**
	 * The listener which executes {@link #action} and sets {@link #alreadyExecutedOnce} accordingly.
	 */
	private final ChangeListener<T> listenerWhichExecutesAction;

	/**
	 * Indicates whether {@link #executeWhen()} was already called. If so, it can not be called again.
	 */
	private final AtomicBoolean executeWhenWasAlreadyCalled;

	/**
	 * Indicates whether {@link #action} was already executed once.
	 */
	private final AtomicBoolean alreadyExecutedOnce;

	// #end ATTRIBUTES

	/**
	 * Creates a new instance from the specified arguments. *
	 * <p>
	 * Note that for the action to be executed, {@link #executeWhen()} needs to be called.
	 *
	 * @param observable
	 *            the {@link ObservableValue} upon whose value the action's execution depends
	 * @param condition
	 *            the condition the {@link #observable}'s value must fulfill for {@link #action} to be executed
	 * @param action
	 *            the action which will be executed
	 */
	public ExecuteAlwaysWhen(ObservableValue<T> observable, Predicate<T> condition, Consumer<T> action) {
		this.observable = observable;
		this.condition = condition;
		this.action = action;

		listenerWhichExecutesAction = (obs, oldValue, newValue) -> tryExecuteActionRepeatedly(newValue);
		executeWhenWasAlreadyCalled = new AtomicBoolean(false);
		alreadyExecutedOnce = new AtomicBoolean(false);
	}

	// #region METHODS

	/**
	 * Executes the action (every time) when the observable's value passes the condition.
	 * <p>
	 * This is a one way function that must only be called once. Calling it again throws an
	 * {@link IllegalStateException}.
	 * <p>
	 * Call {@link #cancel()} to prevent further execution.
	 *
	 * @throws IllegalStateException
	 *             if this method is called more than once
	 */
	public void executeWhen() throws IllegalStateException {
		boolean wasAlreadyCalled = executeWhenWasAlreadyCalled.getAndSet(true);
		if (wasAlreadyCalled)
			throw new IllegalStateException("The method 'executeWhen' must only be called once.");

		observable.addListener(listenerWhichExecutesAction);
		tryExecuteActionTheFirstTime(observable.getValue());
	}

	/**
	 * Executes {@link #action} if the specified value fulfills the {@link #condition} and the action was not yet
	 * executed. The latter is indicated by the {@link #alreadyExecutedOnce}, which will also be updated.
	 * <p>
	 * Called by {@link #listenerWhichExecutesAction} every time {@link #observable} changes its value.
	 *
	 * @param initialValue
	 *            the {@link #observable}'s initial value
	 */
	private void tryExecuteActionTheFirstTime(T initialValue) {
		boolean valueFailsGateway = !condition.test(initialValue);
		if (valueFailsGateway)
			return;

		boolean wasAlreadyExecuted = alreadyExecutedOnce.getAndSet(true);
		if (wasAlreadyExecuted)
			return;

		action.accept(initialValue);
	}

	/**
	 * Executes {@link #action} if the specified value fulfills the {@link #condition}. Sets
	 * {@link #alreadyExecutedOnce} to indicate that the action was executed at least once.
	 * <p>
	 * Called by {@link #listenerWhichExecutesAction} every time {@link #observable} changes its value.
	 *
	 * @param currentValue
	 *            the {@link #observable}'s current value
	 */
	private void tryExecuteActionRepeatedly(T currentValue) {
		boolean valueFailsGateway = !condition.test(currentValue);
		if (valueFailsGateway)
			return;

		alreadyExecutedOnce.set(true);
		action.accept(currentValue);
	}

	/**
	 * Cancels the future execution of the action. If {@link #executeWhen()} was not yet called or the action was
	 * already executed, this is a no-op.
	 */
	public void cancel() {
		observable.removeListener(listenerWhichExecutesAction);
	}

	// #end METHODS
}
