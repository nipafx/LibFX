package org.codefx.libfx.concurrent.act;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ActOnce<T> {

	private final ObservableValue<T> observable;

	private final Predicate<T> gateway;

	private final Consumer<T> action;

	/**
	 * Indicates whether the action will be executed. This is the case if the value is strictly positive <b>before</b>
	 * the {@link AtomicInteger#decrementAndGet() decrementAndGet()}.
	 */
	private final AtomicInteger actionIndicator;

	private final ChangeListener<T> listenerWhichExecutesAction;

	private final AtomicBoolean actWasAlreadyCalled;

	public ActOnce(ObservableValue<T> observable, Predicate<T> gateway, Consumer<T> action) {
		this.observable = observable;
		this.gateway = gateway;
		this.action = action;

		actionIndicator = new AtomicInteger(0);
		listenerWhichExecutesAction = (obs, oldValue, newValue) -> tryExecuteAction(newValue);
		actWasAlreadyCalled = new AtomicBoolean(false);
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

		actionIndicator.set(1);
		observable.addListener(listenerWhichExecutesAction);
		tryExecuteAction(observable.getValue());
	}

	private void tryExecuteAction(T currentValue) {
		boolean valueFailsGateway = !gateway.test(currentValue);
		if (valueFailsGateway)
			return;

		boolean actionAlreadyExecuted = actionIndicator.decrementAndGet() < 0;
		if (actionAlreadyExecuted)
			return;

		action.accept(currentValue);
		observable.removeListener(listenerWhichExecutesAction);
	}

	public void cancel() {
		actionIndicator.set(0);
	}

	public boolean isWaiting() {
		return actionIndicator.get() > 0;
	}

}
