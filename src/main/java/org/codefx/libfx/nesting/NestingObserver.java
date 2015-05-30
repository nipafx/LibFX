package org.codefx.libfx.nesting;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.beans.Observable;

/**
 * Usability class which observes a {@link Nesting} and executes some methods when the nesting's
 * {@link Nesting#innerObservableProperty() innerObservable} changes. These are the methods and the order in which they
 * are called:
 * <ol>
 * <li>if the old inner observable was present, a method is called with that observable as its argument; the method is
 * specified during building (see {@link NestingObserverBuilder#withOldInnerObservable(Consumer) withOldInnerObservable})
 * <li>if the new inner observable is present, a method is called with that observable as its argument; the method is
 * specified during building (see {@link NestingObserverBuilder#withNewInnerObservable(Consumer) withNewInnerObservable})
 * <li>in every case, another method is called with two Booleans as its arguments which indicate whether the old and the
 * new observable were/are present; the method is specified during building (see
 * {@link NestingObserverBuilder#whenInnerObservableChanges(BiConsumer) whenInnerObservableChanges})
 * </ol>
 * These methods are also called once during construction. At this point, there is of course no old inner observable
 * present.
 * <p>
 * The observer is created with a {@link NestingObserverBuilder} which can be obtained from
 * {@link NestingObserver#forNesting(Nesting) forNesting}. After setting some of the methods mentioned above, the
 * observer is built by calling {@link NestingObserverBuilder#observe()}.
 *
 * @param <O>
 *            the type of the nesting hierarchy's inner {@link Observable}
 */
public final class NestingObserver<O extends Observable> {

	// #begin PROPERTIES

	/**
	 * The observed {@link Nesting}.
	 */
	private final Nesting<? extends O> nesting;

	/**
	 * Called when the inner observable is replaced and an old inner observable was present. That observable is also the
	 * argument.
	 */
	private final Consumer<? super O> oldInnerObservableConsumer;

	/**
	 * Called when the inner observable is replaced and the new inner observable is present. That observable is also the
	 * argument.
	 */
	private final Consumer<? super O> newInnerObservableConsumer;

	/**
	 * Called when the inner observable is replaced. The arguments are two Booleans indicating whether the old and new
	 * inner observables are present.
	 */
	private final BiConsumer<Boolean, Boolean> innerObservableChanges;

	//#end PROPERTIES

	// #begin CONSTRUCTION

	/**
	 * Creates a new {@link NestingObserver} from the specified {@link NestingObserverBuilder builder}.
	 *
	 * @param builder
	 *            the {@link NestingObserverBuilder} from which the observer is created
	 */
	private NestingObserver(NestingObserverBuilder<O> builder) {
		nesting = builder.nesting;
		oldInnerObservableConsumer = builder.oldInnerObservableConsumer;
		newInnerObservableConsumer = builder.newInnerObservableConsumer;
		innerObservableChanges = builder.innerObservableChanges;

		initializeObserver();
	}

	/**
	 * Starts building a new {@link NestingObserver} which observes the specified nesting.
	 *
	 * @param <O>
	 *            the type of the nesting hierarchy's inner {@link Observable}
	 * @param nesting
	 *            the observed {@link Nesting}
	 * @return a new {@link NestingObserverBuilder}
	 */
	public static <O extends Observable> NestingObserverBuilder<O> forNesting(Nesting<O> nesting) {
		return new NestingObserverBuilder<>(nesting);
	}

	//#end CONSTRUCTION

	// #begin OBSERVE

	/**
	 * Initializes the observer by observing the initial status and any changes made to it.
	 */
	private void initializeObserver() {
		// observe the initial status
		observeInnerObservableChange(Optional.empty(), nesting.innerObservableProperty().getValue());

		// add a listener to the nesting which observes changes
		nesting.innerObservableProperty().addListener(
				(o, oldInnerObservable, newInnerObservable)
				-> observeInnerObservableChange(oldInnerObservable, newInnerObservable));
	}

	/**
	 * Calls {@link #oldInnerObservableConsumer}, {@link #newInnerObservableConsumer} and
	 * {@link #innerObservableChanges} when the inner observable is replaced.
	 *
	 * @param oldInnerObservable
	 *            the old {@link Nesting#innerObservableProperty() innerObservable}
	 * @param newInnerObservable
	 *            the new {@link Nesting#innerObservableProperty() innerObservable}
	 */
	private void observeInnerObservableChange(
			Optional<? extends O> oldInnerObservable, Optional<? extends O> newInnerObservable) {

		oldInnerObservable.ifPresent(oldObservable -> oldInnerObservableConsumer.accept(oldObservable));
		newInnerObservable.ifPresent(newObservable -> newInnerObservableConsumer.accept(newObservable));

		boolean oldInnerObservablePresent = oldInnerObservable.isPresent();
		boolean newInnerObservablePresent = newInnerObservable.isPresent();
		innerObservableChanges.accept(oldInnerObservablePresent, newInnerObservablePresent);
	}

	//#end BIND

	// #begin INNER CLASSES

	/**
	 * Builds a {@link NestingObserver}.
	 *
	 * @param <O>
	 *            the type of the nesting hierarchy's inner {@link Observable}
	 */
	public static class NestingObserverBuilder<O extends Observable> {

		/**
		 * The future value for {@link NestingObserver#nesting}.
		 */
		private final Nesting<? extends O> nesting;

		/**
		 * The future value for {@link NestingObserver#oldInnerObservableConsumer}.
		 */
		private Consumer<? super O> oldInnerObservableConsumer;

		/**
		 * The future value for {@link NestingObserver#newInnerObservableConsumer}.
		 */
		private Consumer<? super O> newInnerObservableConsumer;

		/**
		 * The future value for {@link NestingObserver#innerObservableChanges}.
		 */
		private BiConsumer<Boolean, Boolean> innerObservableChanges;

		/**
		 * Creates a new builder for a nesting observer which observes the specified nesting.
		 *
		 * @param nesting
		 *            the nesting which will be observed by the created {@link NestingObserver}
		 */
		private NestingObserverBuilder(Nesting<? extends O> nesting) {
			this.nesting = nesting;
			oldInnerObservableConsumer = observable -> {/* by default do nothing */};
			newInnerObservableConsumer = observable -> {/* by default do nothing */};
			innerObservableChanges = (oldObservablePresent, newObservablePresent) -> {/* by default do nothing */};
		}

		/**
		 * The specified method will be executed when the {@link Nesting#innerObservableProperty() innerObservable}
		 * changes <b>and</b> the old observable was present.
		 *
		 * @param oldInnerObservableConsumer
		 *            the executed method; its argument is the old inner observable
		 * @return this builder for fluent calls
		 */
		public NestingObserverBuilder<O> withOldInnerObservable(Consumer<? super O> oldInnerObservableConsumer) {
			this.oldInnerObservableConsumer = oldInnerObservableConsumer;
			return this;
		}

		/**
		 * The specified method will be executed when the {@link Nesting#innerObservableProperty() innerObservable}
		 * changes <b>and</b> the new observable is present.
		 *
		 * @param newInnerObservableConsumer
		 *            the executed method; its argument is the new inner observable
		 * @return this builder for fluent calls
		 */
		public NestingObserverBuilder<O> withNewInnerObservable(Consumer<? super O> newInnerObservableConsumer) {
			this.newInnerObservableConsumer = newInnerObservableConsumer;
			return this;
		}

		/**
		 * The specified method will be executed when the {@link Nesting#innerObservableProperty() innerObservable}
		 * changes.
		 *
		 * @param innerObservableChanges
		 *            the executed method; its argument are two Booleans indicating whether the old and new inner
		 *            observables are present.
		 * @return this builder for fluent calls
		 */
		public NestingObserverBuilder<O> whenInnerObservableChanges(BiConsumer<Boolean, Boolean> innerObservableChanges) {
			this.innerObservableChanges = innerObservableChanges;
			return this;
		}

		/**
		 * Builds a observer from this builder's settings.
		 */
		public void observe() {
			@SuppressWarnings("unused")
			NestingObserver<O> observer = new NestingObserver<O>(this);
		}

	}

	//#end region

}
