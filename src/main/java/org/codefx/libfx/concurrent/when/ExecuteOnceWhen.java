package org.codefx.libfx.concurrent.when;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Executes an action when an {@link ObservableValue}'s value fulfills a certain condition.
 * <p>
 * The action will not be executed before {@link #executeWhen()} is called. The action is only executed once. If it was
 * not yet executed, this can be prevented by calling {@link #cancel()}.
 * <p>
 * This class guarantees that regardless of the way different threads interact with the {@code ObservableValue} the
 * action will be executed...
 * <ul>
 * <li>... if the value held when {@code executeWhen()} returns passes the condition
 * <li>... if a new value passes the condition (either during {@code executeWhen()} or after it returns)
 * <li>... at most once
 * </ul>
 * If the observable is manipulated by several threads, this class does not guarantee that the first value to pass the
 * condition is the one handed to the action. Depending on the interaction of those threads it might be the initial
 * value (the one tested during {@code executeWhen()}) or one of several which were set by those threads.
 * <p>
 * Use {@link ExecuteWhen} to build an instance of this class.
 * 
 * @param <T>
 *            the type the observed {@link ObservableValue}'s wraps
 */
public class ExecuteOnceWhen<T> {

	/*
	 * If no other threads were involved the class would be simple. It would suffice to check the observable's current
	 * value. If it passes the condition, execute the action; otherwise attach a listener which processes each new value
	 * in the same way.
	 */
	/*
	 * But since other threads are allowed to interfere, this could fail. If a correct value is set between the check
	 * and attaching the listener, this value would not be processed and the action would not be executed. To prevent
	 * this the listener is added first and only then is the current value checked and the action possibly executed.
	 */
	/*
	 * Now, if another thread sets the correct value after the listener was added but before the current value is
	 * processed, the action would be executed twice. To prevent this from happening an atomic boolean is used. It will
	 * contain true when the action can still be executed. When a value (either the initial or a new one) fulfills the
	 * condition, it is checked (and set to false). If it contained true, the action will be executed.
	 */

	// #region FIELDS

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
	 * Indicates whether {@link #action} might still be executed at some point in the future. Is used to prevent the
	 * listener and the initial check (see {@link #executeWhen()}) to both execute the action.
	 */
	private final AtomicBoolean willExecute;

	/**
	 * The listener which executes {@link #action} and sets {@link #willExecute} accordingly.
	 */
	private final ChangeListener<T> listenerWhichExecutesAction;

	/**
	 * Indicates whether {@link #executeWhen()} was already called. If so, it can not be called again.
	 */
	private final AtomicBoolean executeWhenWasAlreadyCalled;

	// #end FIELDS

	/**
	 * Creates a new instance from the specified arguments.
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
	ExecuteOnceWhen(ObservableValue<T> observable, Predicate<? super T> condition, Consumer<? super T> action) {
		this.observable = observable;
		this.condition = condition;
		this.action = action;

		listenerWhichExecutesAction = (obs, oldValue, newValue) -> tryExecuteAction(newValue);
		executeWhenWasAlreadyCalled = new AtomicBoolean(false);
		willExecute = new AtomicBoolean(true);
	}

	// #region METHODS

	/**
	 * Executes the action (once) when the observable's value passes the condition.
	 * <p>
	 * This is a one way function that must only be called once. Calling it again throws an
	 * {@link IllegalStateException}.
	 * <p>
	 * Call {@link #cancel()} to prevent future execution.
	 *
	 * @throws IllegalStateException
	 *             if this method is called more than once
	 */
	public void executeWhen() throws IllegalStateException {
		boolean wasAlreadyCalled = executeWhenWasAlreadyCalled.getAndSet(true);
		if (wasAlreadyCalled)
			throw new IllegalStateException("The method 'executeWhen' can only be called once.");

		observable.addListener(listenerWhichExecutesAction);
		tryExecuteAction(observable.getValue());
	}

	/**
	 * Executes {@link #action} if the specified value fulfills the {@link #condition} and the action was not yet
	 * executed. The latter is indicated by the {@link #willExecute}, which will also be updated.
	 *
	 * @param currentValue
	 *            the {@link #observable}'s current value
	 */
	private void tryExecuteAction(T currentValue) {
		boolean valueFailsGateway = !condition.test(currentValue);
		if (valueFailsGateway)
			return;

		boolean actionCanBeExecuted = willExecute.getAndSet(false);
		if (actionCanBeExecuted) {
			action.accept(currentValue);
			// the action was just executed and will not be executed again so the listener is not needed anymore
			observable.removeListener(listenerWhichExecutesAction);
		}
	}

	/**
	 * Cancels the future execution of the action. If {@link #executeWhen()} was not yet called or the action was
	 * already executed, this is a no-op.
	 */
	public void cancel() {
		willExecute.set(false);
		observable.removeListener(listenerWhichExecutesAction);
	}

	// #end METHODS

}
