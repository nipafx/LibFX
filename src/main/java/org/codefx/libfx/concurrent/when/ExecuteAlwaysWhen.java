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
 * This class guarantees that regardless of the way different threads interact with the {@code ObservableValue} the
 * action will be executed...
 * <ul>
 * <li>... once during {@code executeWhen()} if either the observable's initial value or one it was changed to passes
 * the condition
 * <li>... every time a new value passes the condition after {@code executeWhen()} returns
 * </ul>
 * If the observable is manipulated by several threads during {@code executeWhen()}, this class does not guarantee that
 * the first value to pass the condition is the one handed to the action. Depending on the interaction of those threads
 * it might be the initial value or one of several which were set by those threads.
 * <p>
 * Use {@link ExecuteWhen} to build an instance of this class.
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
	 * processed, the action would be executed twice. Actually, if the value is switched fast enough by different
	 * threads, each could be in the middle of executing the listener when the check of the initial value finally
	 * proceeds. This would lead to multiple desired executions plus the undesired one of the initial check. (Note that
	 * this problem can only occur until the initial value is processed. After that only the listener can execute the
	 * action and there is only one so no funny stuff can happen.) That is the reason why the contract states that a
	 * correct value will only be processed once during executeWhen().
	 */
	/*
	 * To achieve this, two atomic booleans are used. The first, 'executeAlways', is false after construction and will
	 * be set to true at the end of 'executeWhen'. When it is true, all values will be processed. Until then only one
	 * execution is allowed. This is monitored using 'alreadyExecuted', which is initially false and will be set to true
	 * on the first execution of the action.
	 */

	// #begin FIELDS

	/**
	 * The {@link ObservableValue} upon whose value the action's execution depends.
	 */
	private final ObservableValue<T> observable;

	/**
	 * The condition the {@link #observable}'s value must fulfill for {@link #action} to be executed.
	 */
	private final Predicate<? super T> condition;

	/**
	 * The action which will be executed.
	 */
	private final Consumer<? super T> action;

	/**
	 * The listener which executes {@link #action} and sets {@link #alreadyExecuted} accordingly.
	 */
	private final ChangeListener<? super T> listenerWhichExecutesAction;

	/**
	 * Indicates whether {@link #executeWhen()} was already called. If so, it can not be called again.
	 */
	private final AtomicBoolean executeWhenWasAlreadyCalled;

	/**
	 * Indicates whether {@link #action} is executed each time the value passes the {@link #condition}.
	 */
	private final AtomicBoolean executeAlways;

	/**
	 * Indicates whether {@link #action} was already executed once.
	 */
	private final AtomicBoolean alreadyExecuted;

	// #end FIELDS

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
	ExecuteAlwaysWhen(ObservableValue<T> observable, Predicate<? super T> condition, Consumer<? super T> action) {
		assert observable != null : "The argument 'observable' must not be null.";
		assert condition != null : "The argument 'condition' must not be null.";
		assert action != null : "The argument 'action' must not be null.";

		this.observable = observable;
		this.condition = condition;
		this.action = action;

		listenerWhichExecutesAction = (obs, oldValue, newValue) -> tryExecuteAction(newValue);
		executeAlways = new AtomicBoolean(false);
		executeWhenWasAlreadyCalled = new AtomicBoolean(false);
		alreadyExecuted = new AtomicBoolean(false);
	}

	// #begin METHODS

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
		tryExecuteAction(observable.getValue());
		executeAlways.set(true);
	}

	/**
	 * Executes {@link #action} if the specified value fulfills the {@link #condition}. Sets {@link #alreadyExecuted} to
	 * indicate that the action was executed at least once.
	 * <p>
	 * Called by {@link #listenerWhichExecutesAction} every time {@link #observable} changes its value.
	 *
	 * @param currentValue
	 *            the {@link #observable}'s current value
	 */
	private void tryExecuteAction(T currentValue) {
		boolean valueFailsGateway = !condition.test(currentValue);
		if (valueFailsGateway)
			return;

		boolean canNotExecuteNow = !canExecuteNow();
		if (canNotExecuteNow)
			return;

		action.accept(currentValue);
	}

	/**
	 * Indicates whether the {@link #action} can be executed now by checking {@link #executeAlways} and
	 * {@link #alreadyExecuted}.
	 * <p>
	 * Potentially changes the state of {@code alreadyExecuted} so it must only be called if {@link #action} is really
	 * executed afterwards (i.e. the value should already have passed the {@link #condition}).
	 *
	 * @return true if the {@link #action} can be executed now
	 */
	private boolean canExecuteNow() {
		if (executeAlways.get()) {
			alreadyExecuted.set(true);
			return true;
		}

		// in this case the action must only be executed once, so make sure that didn't happen yet
		boolean alreadyExecutedOnce = alreadyExecuted.getAndSet(true);
		boolean canExecuteNow = !alreadyExecutedOnce;
		return canExecuteNow;
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
