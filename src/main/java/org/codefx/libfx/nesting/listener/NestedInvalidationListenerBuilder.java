package org.codefx.libfx.nesting.listener;

import java.util.Objects;

import javafx.beans.InvalidationListener;

import org.codefx.libfx.nesting.Nesting;

/**
 * A builder for a {@link NestedInvalidationListener}.
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
	 * @return a {@link NestedInvalidationListenerBuilder} which provides a {@link Buildable#build() build}-method
	 */
	public Buildable withListener(InvalidationListener listener) {
		Objects.requireNonNull(listener, "The argument 'listener' must not be null.");

		this.listener = listener;
		return new Buildable(this);
	}

	//#end METHODS

	// #region PRIVATE CLASSES

	/**
	 * A subtype of {@link NestedInvalidationListenerBuilder} which can actually build a listener with {@link #build()}.
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
		 * Builds a nested invalidation listener. This method can only be called once as the same
		 * {@link InvalidationListener} should not be added more than once to the same {@link Nesting}.
		 *
		 * @return a new instance of {@link NestedChangeListener}
		 */
		public NestedInvalidationListener build() {
			if (built)
				throw new IllegalStateException("This builder can only build one 'NestedInvalidationListener'.");

			built = true;
			return new NestedInvalidationListener(nesting, listener);
		}

	}

	//#end region

}
