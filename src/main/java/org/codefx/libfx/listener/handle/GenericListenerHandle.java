package org.codefx.libfx.listener.handle;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * A generic implementation of {@link ListenerHandle} which uses functions specified during construction to
 * {@link #attach()} and {@link #detach()} the listener to the observable instance.
 *
 * @param <O>
 *            the type of the observable instance (e.g {@link javafx.beans.value.ObservableValue ObservableValue} or
 *            {@link javafx.collections.ObservableMap ObservableMap}) to which the listener will be added
 * @param <L>
 *            the type of the listener which will be added to the observable
 */
final class GenericListenerHandle<O, L> implements ListenerHandle {

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
	 * Called on {@link #attach()}.
	 */
	private final BiConsumer<? super O, ? super L> add;

	/**
	 * Called on {@link #detach()}.
	 */
	private final BiConsumer<? super O, ? super L> remove;

	/**
	 * Indicates whether the {@link #listener} is currently added to the {@link #observable}.
	 */
	private boolean attached;

	// #end FIELDS

	// #begin CONSTRUCITON

	/**
	 * Creates a new listener handle for the specified arguments. The listener is initially detached.
	 *
	 * @param observable
	 *            the observable instance to which the {@code listener} will be added
	 * @param listener
	 *            the listener which will be added to the {@code observable}
	 * @param add
	 *            called when the {@code listener} must be added to the {@code observable}
	 * @param remove
	 *            called when the {@code listener} must be removed from the {@code observable}
	 */
	public GenericListenerHandle(
			O observable, L listener, BiConsumer<? super O, ? super L> add, BiConsumer<? super O, ? super L> remove) {

		Objects.requireNonNull(observable, "The argument 'observable' must not be null.");
		Objects.requireNonNull(listener, "The argument 'listener' must not be null.");
		Objects.requireNonNull(add, "The argument 'add' must not be null.");
		Objects.requireNonNull(remove, "The argument 'remove' must not be null.");

		this.observable = observable;
		this.listener = listener;
		this.add = add;
		this.remove = remove;
	}

	// #end CONSTRUCITON

	// #begin IMPLEMENTATION OF 'ListenerHandle'

	@Override
	public void attach() {
		if (attached)
			return;

		attached = true;
		add.accept(observable, listener);
	}

	@Override
	public void detach() {
		if (!attached)
			return;

		attached = false;
		remove.accept(observable, listener);
	}

	// #end IMPLEMENTATION OF 'ListenerHandle'

}
