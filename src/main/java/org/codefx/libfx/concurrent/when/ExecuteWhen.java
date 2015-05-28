package org.codefx.libfx.concurrent.when;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.beans.value.ObservableValue;

/**
 * <p>
 * Builder for {@link ExecuteAlwaysWhen} and {@link ExecuteOnceWhen}.
 * </p>
 * <h2>Example</h2>A typical use would look like this:
 *
 * <pre>
 * 	ObservableValue&lt;State&gt; workerState;
 *	ExecuteWhen.on(workerState)
 *		.when(state -&gt; state == State.SUCCEEDED)
 *		.thenOnce(state -&gt; logSuccess())
 *		.executeWhen();
 * </pre>
 *
 * @param <T>
 *            the type the {@link ObservableValue} which will be observed by the constructed instance wraps
 */
public class ExecuteWhen<T> {

	// #begin FIELDS

	/**
	 * The {@link ObservableValue} upon whose value the action's execution depends.
	 */
	private final ObservableValue<T> observable;

	/**
	 * The condition the {@link #observable}'s value must fulfill for the action to be executed.
	 */
	private Optional<Predicate<? super T>> condition;

	// #end FIELDS

	// #begin CONSTRUCTION

	/**
	 * Creates a new instance for the specified observable
	 *
	 * @param observable
	 *            the {@link ObservableValue} which will be observed by the created {@code Execute...When} instances
	 */
	private ExecuteWhen(ObservableValue<T> observable) {
		Objects.requireNonNull(observable, "The argument 'observable' must not be null.");

		this.observable = observable;
		condition = Optional.empty();
	}

	/**
	 * Creates a new builder. The built instance of {@code Execute...When} will observe the specified observable.
	 *
	 * @param <T>
	 *            the type the {@link ObservableValue} which will be observed by the constructed instance wraps
	 * @param observable
	 *            the {@link ObservableValue} which will be observed by the created {@code Execute...When} instances
	 * @return a new builder instance
	 */
	public static <T> ExecuteWhen<T> on(ObservableValue<T> observable) {
		return new ExecuteWhen<>(observable);
	}

	// #end CONSTRUCTION

	// #begin SETTING VALUES

	/**
	 * Specifies the condition the observable's value must fulfill in order for the action to be executed.
	 *
	 * @param condition
	 *            the condition as a {@link Predicate}
	 * @return this builder
	 */
	public ExecuteWhen<T> when(Predicate<? super T> condition) {
		Objects.requireNonNull(condition, "The argument 'condition' must not be null.");

		this.condition = Optional.of(condition);
		return this;
	}

	// #end SETTING VALUES

	// #begin BUILD

	/**
	 * Creates an instance which:
	 * <ul>
	 * <li>observes the {@link ObservableValue} (specified for this builder's construction) for new values
	 * <li>checks each new value against the condition set with {@link #when(Predicate)} (calling which is required)
	 * <li>executes the specified {@code action} once if a value fulfills the condition
	 * </ul>
	 * Note that the observation does not start until {@link ExecuteOnceWhen#executeWhen()} is called. See
	 * {@link ExecuteOnceWhen} for details.
	 *
	 * @param action
	 *            the {@link Consumer} of the value which passed the condition
	 * @return an instance of {@link ExecuteOnceWhen}
	 * @throws IllegalStateException
	 *             if {@link #when(Predicate)} was not called
	 */
	public ExecuteOnceWhen<T> thenOnce(Consumer<? super T> action) throws IllegalStateException {
		Objects.requireNonNull(action, "The argument 'action' must not be null.");

		ensureConditionWasSet();
		return new ExecuteOnceWhen<T>(observable, condition.get(), action);
	}

	/**
	 * Creates an instance which:
	 * <ul>
	 * <li>observes the {@link ObservableValue} (specified for this builder's construction) for new values
	 * <li>checks each new value against the condition set with {@link #when(Predicate)} (calling which is required)
	 * <li>executes the specified {@code action} every time a value fulfills the condition
	 * </ul>
	 * Note that the observation does not start until {@link ExecuteAlwaysWhen#executeWhen()} is called. See
	 * {@link ExecuteAlwaysWhen} for details.
	 *
	 * @param action
	 *            the {@link Consumer} of the value which passed the condition
	 * @return an instance of {@link ExecuteOnceWhen}
	 * @throws IllegalStateException
	 *             if {@link #when(Predicate)} was not called
	 */
	public ExecuteAlwaysWhen<T> thenAlways(Consumer<? super T> action) throws IllegalStateException {
		Objects.requireNonNull(action, "The argument 'action' must not be null.");

		ensureConditionWasSet();
		return new ExecuteAlwaysWhen<T>(observable, condition.get(), action);
	}

	/**
	 * Makes sure that {@link #condition} was set, i.e. the {@link Optional} is not empty.
	 *
	 * @throws IllegalStateException
	 *             if {@link #condition} was not set
	 */
	private void ensureConditionWasSet() throws IllegalStateException {
		boolean noCondition = !condition.isPresent();
		if (noCondition)
			throw new IllegalStateException(
					"Set a condition with 'when(Predicate<? super T>)' before calling any 'then...' method.");
	}

	// #end BUILD

}
