package org.codefx.libfx.listener.handle;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * A builder for a {@link ListenerHandle}. Note that it is abstract enough to be used for all kinds of
 * observable/listener relation and not just for those occurring in JavaFX.
 * <p>
 * The created handle manages whether the listener is currently attached. The functions specified to
 * {@link #onAttach(BiConsumer)} and {@link #onDetach(BiConsumer)} are only called when necessary. This is the case
 * <ul>
 * <li>if {@link ListenerHandle#attach() attach} is called when the listener is not currently added to the observable
 * <li>if {@link ListenerHandle#detach() detach} is called when the listener is currently added to the observable
 * </ul>
 * This implies that they can be stateless functions which simply add and remove the listener. The functions are called
 * with the observable and listener specified during construction.
 * <p>
 * The {@link ListenerHandle} returned by this builder is not yet attached, i.e. it does not initially call the
 * functions given to {@code onAttach} or {@code onDetach}.
 * </p>
 * <h2>Example</h2>
 * <p>
 * A typical use looks like this:
 *
 * <pre>
 * Property&lt;String&gt; textProperty;
 * ChangeListener&lt;String&gt; textListener;
 *
 * ListenerHandle textListenerHandle = ListenerHandleBuilder
 * 	.from(textProperty, textListener)
 * 	.onAttach((property, listener) -&gt; property.addListener(listener))
 * 	.onDetach((property, listener) -&gt; property.removeListener(listener))
 * 	.build();
 * </pre>
 * Or, with method references:
 *
 * <pre>
 * Property&lt;String&gt; textProperty;
 * ChangeListener&lt;String&gt; textListener;
 *
 * ListenerHandle textListenerHandle = ListenerHandleBuilder
 * 	.from(textProperty, textListener)
 * 	.onAttach(Property::addListener))
 * 	.onDetach(Property::removeListener)
 * 	.build();
 * </pre>
 *
 * @param <O>
 *            the type of the observable instance (e.g {@link javafx.beans.value.ObservableValue ObservableValue} or
 *            {@link javafx.collections.ObservableMap ObservableMap}) to which the listener will be added
 * @param <L>
 *            the type of the listener which will be added to the observable
 */
public final class ListenerHandleBuilder<O, L> {

	// #begin FIELDS

	/**
	 * The observable instance to which the {@link #listener} will be added.
	 */
	private final O observable;

	/**
	 * The listener which will be added to the {@link #observable}.
	 */
	private final L listener;

	/**
	 * Called on {@link ListenerHandle#attach()}.
	 */
	private Optional<BiConsumer<? super O, ? super L>> add;

	/**
	 * Called on {@link ListenerHandle#detach()}.
	 */
	private Optional<BiConsumer<? super O, ? super L>> remove;

	// #end FIELDS

	// #begin CONSTRUCTION

	/**
	 * Creates a builder for a generic {@link ListenerHandle}.
	 *
	 * @param observable
	 *            the observable instance to which the {@code listener} will be added
	 * @param listener
	 *            the listener which will be added to the {@code observable}
	 */
	private ListenerHandleBuilder(O observable, L listener) {
		Objects.requireNonNull(observable, "The argument 'observable' must not be null.");
		Objects.requireNonNull(listener, "The argument 'listener' must not be null.");

		this.observable = observable;
		this.listener = listener;

		add = Optional.empty();
		remove = Optional.empty();
	}

	/**
	 * Creates a builder for a generic {@link ListenerHandle}.
	 *
	 * @param <O>
	 *            the type of the observable instance (e.g {@link javafx.beans.value.ObservableValue ObservableValue} or
	 *            {@link javafx.collections.ObservableMap ObservableMap}) to which the listener will be added
	 * @param <L>
	 *            the type of the listener which will be added to the observable
	 * @param observable
	 *            the observable instance to which the {@code listener} will be added
	 * @param listener
	 *            the listener which will be added to the {@code observable}
	 * @return a {@link ListenerHandleBuilder} for a {@link ListenerHandle}.
	 */
	public static <O, L> ListenerHandleBuilder<O, L> from(O observable, L listener) {
		return new ListenerHandleBuilder<>(observable, listener);
	}

	// #end CONSTRUCTION

	// #begin SET AND BUILD

	/**
	 * Sets the function which is executed when the built {@link ListenerHandle} must add the listener because
	 * {@link ListenerHandle#attach() attach} was called.
	 * <p>
	 * Because the built handle manages whether the listener is currently attached, the function is only called when
	 * necessary, i.e. when {@code attach} is called when the listener is currently not added to the observable.
	 *
	 * @param add
	 *            the {@link BiConsumer} called on {@code attach}; the arguments for the function are the observable and
	 *            listener specified during this builder's construction
	 * @return this builder for fluent calls
	 */
	public ListenerHandleBuilder<O, L> onAttach(BiConsumer<? super O, ? super L> add) {
		Objects.requireNonNull(add, "The argument 'add' must not be null.");

		this.add = Optional.of(add);
		return this;
	}

	/**
	 * Sets the function which is executed when the built {@link ListenerHandle} must remove the listener because
	 * {@link ListenerHandle#attach() detach} was called.
	 * <p>
	 * Because the built handle manages whether the listener is currently attached, the function is only called when
	 * necessary, i.e. when {@code detach} is called when the listener is currently added to the observable.
	 *
	 * @param remove
	 *            the {@link BiConsumer} called on {@code detach}; the arguments for the function are the observable and
	 *            listener specified during this builder's construction
	 * @return this builder for fluent calls
	 */
	public ListenerHandleBuilder<O, L> onDetach(BiConsumer<? super O, ? super L> remove) {
		Objects.requireNonNull(remove, "The argument 'remove' must not be null.");

		this.remove = Optional.of(remove);
		return this;
	}

	/**
	 * Creates a new listener handle and attaches the listener. This will only succeed if {@link #onAttach(BiConsumer)}
	 * and {@link #onDetach(BiConsumer)} have been called.
	 *
	 * @return a new {@link ListenerHandle}; initially attached
	 * @throws IllegalStateException
	 *             if {@link #onAttach(BiConsumer)} or {@link #onDetach(BiConsumer)} have not been called
	 */
	public ListenerHandle buildAttached() throws IllegalStateException {
		ListenerHandle handle = buildDetached();
		handle.attach();
		return handle;
	}

	/**
	 * Creates a new, initially detached listener handle. This will only succeed if {@link #onAttach(BiConsumer)} and
	 * {@link #onDetach(BiConsumer)} have been called.
	 *
	 * @return a new {@link ListenerHandle}; initially detached
	 * @throws IllegalStateException
	 *             if {@link #onAttach(BiConsumer)} or {@link #onDetach(BiConsumer)} have not been called
	 */
	public ListenerHandle buildDetached() throws IllegalStateException {
		verifyAddAndRemovePresent();
		return new GenericListenerHandle<>(observable, listener, add.get(), remove.get());
	}

	/**
	 * Verifies that {@link #add} and {@link #remove} are present.
	 *
	 * @throws IllegalStateException
	 *             if {@link #add} or {@link #remove} is empty.
	 */
	private void verifyAddAndRemovePresent() throws IllegalStateException {
		boolean onAttachCalled = add.isPresent();
		boolean onDetachCalled = remove.isPresent();
		boolean canBuild = onAttachCalled && onDetachCalled;

		if (canBuild)
			return;
		else
			throwExceptionForMissingCall(onAttachCalled, onDetachCalled);
	}

	/**
	 * Throws an {@link IllegalStateException} for a missing call.
	 *
	 * @param onAttachCalled
	 *            indicates whether {@link #onAttach(BiConsumer)} has been called
	 * @param onDetachCalled
	 *            indicates whether {@link #onDetach(BiConsumer)} has been called
	 * @throws IllegalStateException
	 *             if at least one of the specified booleans is true
	 */
	private static void throwExceptionForMissingCall(boolean onAttachCalled, boolean onDetachCalled)
			throws IllegalStateException {

		if (!onAttachCalled && !onDetachCalled)
			throw new IllegalStateException(
					"A listener handle can not be build until 'onAttach' and 'onDetach' have been called.");

		if (!onAttachCalled)
			throw new IllegalStateException("A listener handle can not be build until 'onAttach' has been called.");

		if (!onDetachCalled)
			throw new IllegalStateException("A listener handle can not be build until 'onDetach' has been called.");
	}

	// #end SET AND BUILD
}
