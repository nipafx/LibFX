package org.codefx.libfx.nesting.listener;

import java.util.Objects;

import javafx.beans.InvalidationListener;

import org.codefx.libfx.nesting.Nesting;

/**
 * A builder for a {@link NestedInvalidationListenerHandle}.
 */
public class NestedInvalidationListenerBuilder {

	// #region PROPERTIES

	/**
	 * The {@link Nesting} to which the listener will be added.
	 */
	private final Nesting<?> nesting;

	/**
	 * The {@link InvalidationListener} which will be added to the nesting's inner observable.
	 */
	private InvalidationListener listener;

	//#end PROPERTIES

	// #region CONSTRUCTION

	/**
	 * Creates a new builder for the specified nesting.
	 *
	 * @param nesting
	 *            the {@link Nesting} to which the listener will be added
	 */
	private NestedInvalidationListenerBuilder(Nesting<?> nesting) {
		Objects.requireNonNull(nesting, "The argument 'nesting' must not be null.");

		this.nesting = nesting;
	}

	/**
	 * Copy constructor.
	 *
	 * @param other
	 *            the other {@link NestedInvalidationListenerBuilder} from which this builder will be copied
	 */
	private NestedInvalidationListenerBuilder(NestedInvalidationListenerBuilder other) {
		Objects.requireNonNull(other, "The argument 'other' must not be null.");

		this.nesting = other.nesting;
	}

	/**
	 * Creates a new builder for the specified nesting.
	 *
	 * @param nesting
	 *            the {@link Nesting} to which the listener will be added
	 * @return the new builder
	 */
	public static NestedInvalidationListenerBuilder forNesting(Nesting<?> nesting) {
		return new NestedInvalidationListenerBuilder(nesting);
	}

	//#end CONSTRUCTION

	// #region METHODS

	/**
	 * Specified the listener which will be added to the nesting.
	 *
	 * @param listener
	 *            the {@link InvalidationListener} which will be added to the nesting's inner observable
	 * @return a {@link NestedInvalidationListenerBuilder} which provides a {@link Buildable#buildAttached() build}-method
	 */
	public Buildable withListener(InvalidationListener listener) {
		Objects.requireNonNull(listener, "The argument 'listener' must not be null.");

		this.listener = listener;
		return new Buildable(this);
	}

	//#end METHODS

	// #region PRIVATE CLASSES

	/**
	 * A subtype of {@link NestedInvalidationListenerBuilder} which can actually build a listener with {@link #buildAttached()}.
	 */
	public class Buildable extends NestedInvalidationListenerBuilder {

		/**
		 * Indicates whether the nested listener was already built.
		 */
		private boolean built;

		/**
		 * Creates a buildable {@link NestedInvalidationListenerBuilder}.
		 *
		 * @param builder
		 *            the builder from which this builder is copied
		 */
		private Buildable(NestedInvalidationListenerBuilder builder) {
			super(builder);
		}

		/**
		 * Builds and {@link NestedInvalidationListenerHandle#attach() attaches} a nested invalidation listener and
		 * returns the handle for it.
		 * <p>
		 * This method can only be called once as the same {@link InvalidationListener} should not be added more than
		 * once to the same {@link Nesting}.
		 *
		 * @return a new instance of {@link NestedInvalidationListenerHandle}; initially attached
		 * @see #buildDetached()
		 */
		public NestedInvalidationListenerHandle buildAttached() {
			NestedInvalidationListenerHandle listenerHandle = buildDetached();
			listenerHandle.attach();
			return listenerHandle;
		}

		/**
		 * Builds a nested invalidation listener and returns the handle for it.
		 * <p>
		 * Note that the listener is not yet {@link NestedInvalidationListenerHandle#attach() attached}!
		 * <p>
		 * This method can only be called once as the same {@link InvalidationListener} should not be added more than
		 * once to the same {@link Nesting}.
		 *
		 * @return a new instance of {@link NestedInvalidationListenerHandle}; initially detached
		 * @see #buildAttached()
		 */
		public NestedInvalidationListenerHandle buildDetached() {
			if (built)
				throw new IllegalStateException("This builder can only build one 'NestedInvalidationListenerHandle'.");

			built = true;
			return new NestedInvalidationListenerHandle(nesting, listener);
		}

	}

	//#end region

}
