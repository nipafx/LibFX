package org.codefx.libfx.concurrent.act;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ActRepeated<T> {

	private final ObservableValue<T> observable;

	private final Predicate<T> gateway;

	private final Consumer<T> action;

	private final ChangeListener<T> listenerWhichExecutesActionRepeatedly;

	private final AtomicBoolean actWasAlreadyCalled;

	private final AtomicBoolean alreadyExecutedOnce;

	public ActRepeated(ObservableValue<T> observable, Predicate<T> gateway, Consumer<T> action) {
		this.observable = observable;
		this.gateway = gateway;
		this.action = action;

		listenerWhichExecutesActionRepeatedly = (obs, oldValue, newValue) -> tryExecuteActionRepeatedly(newValue);
		actWasAlreadyCalled = new AtomicBoolean(false);
		alreadyExecutedOnce = new AtomicBoolean(false);
	}

	/**
	 * Executes the action as soon as the observable's value passes the gateway.
	 * <p>
	 * This is a one way function that must only be called once. Calling it again throws an
	 * {@link IllegalStateException}.
	 *
	 * @throws IllegalStateException
	 *             if this method is called more than once
	 */
	public void act() throws IllegalStateException {
		boolean wasAlreadyCalled = actWasAlreadyCalled.getAndSet(true);
		if (wasAlreadyCalled)
			throw new IllegalStateException("The method 'act' must only be called once.");

		observable.addListener(listenerWhichExecutesActionRepeatedly);
		tryExecuteActionTheFirstTime(observable.getValue());
	}

	private void tryExecuteActionTheFirstTime(T currentValue) {
		boolean valueFailsGateway = !gateway.test(currentValue);
		if (valueFailsGateway)
			return;

		boolean wasAlreadyExecuted = alreadyExecutedOnce.getAndSet(true);
		if (wasAlreadyExecuted)
			return;

		action.accept(currentValue);
	}

	private void tryExecuteActionRepeatedly(T currentValue) {
		boolean valueFailsGateway = !gateway.test(currentValue);
		if (valueFailsGateway)
			return;

		alreadyExecutedOnce.set(true);
		action.accept(currentValue);
	}

	public void cancel() {
		observable.removeListener(listenerWhichExecutesActionRepeatedly);
	}

}
