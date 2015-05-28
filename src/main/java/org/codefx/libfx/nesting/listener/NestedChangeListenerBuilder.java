package org.codefx.libfx.nesting.listener;

import java.util.Objects;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.codefx.libfx.nesting.Nesting;

/**
 * A builder for a {@link NestedChangeListenerHandle}.
 *
 * @param <T>
 *            the type of the value wrapped by the {@link ObservableValue}
 * @param <O>
 *            the type of the nesting hierarchy's inner observable
 */
public class NestedChangeListenerBuilder<T, O extends ObservableValue<T>> {

	// #begin PROPERTIES

	/**
	 * The {@link Nesting} to which the listener will be added.
	 */
	private final Nesting<O> nesting;

	/**
	 * The {@link ChangeListener} which will be added to the nesting's inner observable value.
	 */
	private ChangeListener<? super T> listener;

	//#end PROPERTIES

	// #begin CONSTRUCTION

	/**
	 * Creates a new builder for the specified nesting.
	 *
	 * @param nesting
	 *            the {@link Nesting} to which the listener will be added
	 */
	private NestedChangeListenerBuilder(Nesting<O> nesting) {
		Objects.requireNonNull(nesting, "The argument 'nesting' must not be null.");

		this.nesting = nesting;
	}

	/**
	 * Copy constructor.
	 *
	 * @param other
	 *            the other {@link NestedChangeListenerBuilder} from which this builder will be copied
	 */
	private NestedChangeListenerBuilder(NestedChangeListenerBuilder<T, O> other) {
		this.nesting = other.nesting;
	}

	/**
	 * Creates a new builder for the specified nesting.
	 *
	 * @param <T>
	 *            the type of the value wrapped by the observable value
	 * @param <O>
	 *            the type of the nesting hierarchy's inner {@link ObservableValue}
	 * @param nesting
	 *            the {@link Nesting} to which the listener will be added
	 * @return the new builder
	 */
	public static <T, O extends ObservableValue<T>> NestedChangeListenerBuilder<T, O> forNesting(Nesting<O> nesting) {
		return new NestedChangeListenerBuilder<>(nesting);
	}

	//#end CONSTRUCTION

	// #begin METHODS

	/**
	 * Specified the listener which will be added to the nesting.
	 *
	 * @param listener
	 *            the {@link ChangeListener} which will be added to the nesting's inner observable value
	 * @return a {@link NestedChangeListenerBuilder} which provides a {@link Buildable#buildAttached() build}-method
	 */
	public Buildable withListener(ChangeListener<? super T> listener) {
		Objects.requireNonNull(listener, "The argument 'listener' must not be null.");

		this.listener = listener;
		return new Buildable(this);
	}

	//#end METHODS

	// #begin PRIVATE CLASSES

	/**
	 * A subtype of {@link NestedChangeListenerBuilder} which can actually build a listener with
	 * {@link #buildAttached()}.
	 */
	public class Buildable extends NestedChangeListenerBuilder<T, O> {

		/**
		 * Indicates whether the nested listener was already built.
		 */
		private boolean built;

		/**
		 * Creates a buildable {@link NestedChangeListenerBuilder}.
		 *
		 * @param builder
		 *            the builder from which this builder is copied
		 */
		private Buildable(NestedChangeListenerBuilder<T, O> builder) {
			super(builder);
		}

		/**
		 * Builds and {@link NestedChangeListenerHandle#attach() attaches} a nested change listener and returns the
		 * handle for it.
		 * <p>
		 * This method can only be called once as the same {@link ChangeListener} should not be added more than once to
		 * the same {@link Nesting}.
		 *
		 * @return a new instance of {@link NestedChangeListenerHandle}; initially attached
		 * @see #buildDetached()
		 */
		public NestedChangeListenerHandle<T> buildAttached() {
			NestedChangeListenerHandle<T> listenerHandle = buildDetached();
			listenerHandle.attach();
			return listenerHandle;
		}

		/**
		 * Builds a nested change listener and returns the handle for it.
		 * <p>
		 * Note that the listener is not yet {@link NestedChangeListenerHandle#attach() attached}!
		 * <p>
		 * This method can only be called once as the same {@link ChangeListener} should not be added more than once to
		 * the same {@link Nesting}.
		 *
		 * @return a new instance of {@link NestedChangeListenerHandle}; initially detached
		 * @see #buildAttached()
		 */
		public NestedChangeListenerHandle<T> buildDetached() {
			if (built)
				throw new IllegalStateException("This builder can only build one 'NestedChangeListenerHandle'.");

			built = true;
			return new NestedChangeListenerHandle<T>(nesting, listener);
		}

	}

	//#end region

}
