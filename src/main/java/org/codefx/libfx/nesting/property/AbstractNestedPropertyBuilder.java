package org.codefx.libfx.nesting.property;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javafx.beans.property.Property;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.property.InnerObservableMissingBehavior.WhenInnerObservableGoesMissing;
import org.codefx.libfx.nesting.property.InnerObservableMissingBehavior.WhenInnerObservableMissingOnUpdate;

/**
 * Abstract superclass to nested property builders. Collects common builder settings; e.g. for the new property's
 * {@link Property#getBean() bean} and {@link Property#getName() name}.
 *
 * @param <T>
 *            the most concrete type of the value wrapped by the property which will be built
 * @param <O>
 *            the type of the nesting hierarchy's inner observable (which is a {@link Property})
 * @param <P>
 *            the type of {@link Property} which will be built
 * @param <B>
 *            the most concrete type of this builder (used for fluent API)
 */
abstract class AbstractNestedPropertyBuilder<T, O extends Property<?>, P extends NestedProperty<?>, B extends AbstractNestedPropertyBuilder<T, O, P, B>> {

	// #begin PROPERTIES

	/**
	 * The nesting which will be used for all nested properties.
	 */
	private final Nesting<O> nesting;

	/**
	 * The behavior for the case that the inner observable is missing.
	 */
	private final MutableInnerObservableMissingBehavior<T> innerObservableMissingBehavior;

	/**
	 * The property's future {@link Property#getBean() bean}.
	 */
	private Object bean;

	/**
	 * The property's future {@link Property#getName() name}.
	 */
	private String name;

	//#end PROPERTIES

	// #begin CONSTRUCTOR

	/**
	 * Creates a new abstract builder which uses the specified nesting.
	 *
	 * @param nesting
	 *            the nesting which will be used for all nested properties
	 */
	protected AbstractNestedPropertyBuilder(Nesting<O> nesting) {
		Objects.requireNonNull(nesting, "The argument 'nesting' must not be null.");
		this.nesting = nesting;
		this.innerObservableMissingBehavior = new MutableInnerObservableMissingBehavior<>();
	}

	//#end CONSTRUCTOR

	// #begin ABSTRACT METHODS

	/**
	 * Creates a new property instance. This method can be called arbitrarily often and each call returns a new
	 * instance.
	 *
	 * @return the new instance of {@code P}, i.e. an implementation of {@link NestedProperty}
	 */
	public abstract P build();

	//#end ABSTRACT METHODS

	// #begin MUTATORS

	/**
	 * Sets the property's {@link Property#getBean() bean}.
	 *
	 * @param bean
	 *            the property's future bean
	 * @return this builder
	 */
	public final B setBean(Object bean) {
		Objects.requireNonNull(bean, "The argument 'bean' must not be null.");
		this.bean = bean;
		return thisAsB();
	}

	/**
	 * Sets the property's {@link Property#getName() name}.
	 *
	 * @param name
	 *            the property's future name
	 * @return this builder
	 */
	public B setName(String name) {
		Objects.requireNonNull(name, "The argument 'name' must not be null.");
		this.name = name;
		return thisAsB();
	}

	/**
	 * The property will keep its value when the inner observable goes missing (see {@link NestedProperty} for details
	 * on this).
	 * <p>
	 * This is the default behavior.
	 *
	 * @return this builder
	 */
	public B onInnerObservableMissingKeepValue() {
		innerObservableMissingBehavior.whenGoesMissing(WhenInnerObservableGoesMissing.KEEP_VALUE);
		return thisAsB();
	}

	/**
	 * The property will change to the default value for the wrapped type when the inner observable goes missing (see
	 * {@link NestedProperty} for details on this).
	 * <p>
	 * For primitive wrapping properties (e.g. {@link NestedIntegerProperty}), this will set the primitive default (e.g.
	 * 0); for reference wrapping properties this will be null.
	 *
	 * @return this builder
	 */
	public B onInnerObservableMissingSetDefaultValue() {
		innerObservableMissingBehavior.whenGoesMissing(WhenInnerObservableGoesMissing.SET_DEFAULT_VALUE);
		return thisAsB();
	}

	/**
	 * The property will change to the specified value when the inner observable goes missing (see
	 * {@link NestedProperty} for details on this).
	 * <p>
	 * This method does not accept null as a value. Call {@link #onInnerObservableMissingSetDefaultValue()} if the
	 * property should change to the default value for the wrapped type (e.g. 0 for {@link NestedIntegerProperty}).
	 *
	 * @param value
	 *            the value to set
	 * @return this builder
	 */
	public B onInnerObservableMissingSetValue(T value) {
		Objects.requireNonNull(value, "The argument 'value' must not be null.");

		innerObservableMissingBehavior.whenGoesMissing(WhenInnerObservableGoesMissing.SET_VALUE_FROM_SUPPLIER);
		innerObservableMissingBehavior.valueForMissing(() -> value);
		return thisAsB();
	}

	/**
	 * The property will change to the value computed by the specified supplier when the inner observable goes missing
	 * (see {@link NestedProperty} for details on this).
	 * <p>
	 * The supplier may produce null in which case primitive wrapping properties will fall back to the type's default
	 * value (e.g. 0 for {@link NestedIntegerProperty}).
	 *
	 * @param valueSupplier
	 *            the supplier which computes the value to set; may produce null
	 * @return this builder
	 */
	public B onInnerObservableMissingComputeValue(Supplier<T> valueSupplier) {
		Objects.requireNonNull(valueSupplier, "The argument 'valueSupplier' must not be null.");

		innerObservableMissingBehavior.whenGoesMissing(WhenInnerObservableGoesMissing.SET_VALUE_FROM_SUPPLIER);
		innerObservableMissingBehavior.valueForMissing(valueSupplier);
		return thisAsB();
	}

	/**
	 * The property will throw an {@link IllegalStateException} when it is updated (e.g. by calling
	 * {@link Property#setValue(Object) setValue} or via a binding) while the inner observable is missing (see
	 * {@link NestedProperty} for details on this).
	 * <p>
	 * This is the default behavior.
	 *
	 * @return this builder
	 */
	public B onUpdateWhenInnerObservableMissingThrowException() {
		innerObservableMissingBehavior.onUpdate(WhenInnerObservableMissingOnUpdate.THROW_EXCEPTION);
		return thisAsB();
	}

	/**
	 * The property will accept new values when it is updated (e.g. by calling {@link Property#setValue(Object)
	 * setValue} or via a binding) while the inner observable is missing (see {@link NestedProperty} for details on
	 * this).
	 * <p>
	 * Once the nesting changes to a new (non-missing) inner observable, the property will change to that observable's
	 * value.
	 *
	 * @return this builder
	 */
	public B onUpdateWhenInnerObservableMissingAcceptValues() {
		innerObservableMissingBehavior
				.onUpdate(WhenInnerObservableMissingOnUpdate.ACCEPT_VALUE_UNTIL_NEXT_INNER_OBSERVABLE);
		return thisAsB();
	}

	/**
	 * Performs an unchecked cast to {@code B} which
	 *
	 * @return this builder as an instance of {@code B}
	 */
	@SuppressWarnings("unchecked")
	private B thisAsB() {
		B thisAsB = (B) this;
		return thisAsB;
	}

	// #end MUTATORS

	// #begin ACCESSORS FOR SUBCLASSES

	/**
	 * @return the nesting which will be used for all nested properties
	 */
	protected final Nesting<O> getNesting() {
		return nesting;
	}

	/**
	 * @return the property's {@link Property#getBean() bean}.
	 */
	protected final Object getBean() {
		return bean;
	}

	/**
	 * @return the property's {@link Property#getBean() bean}.
	 */
	protected final String getName() {
		return name;
	}

	/**
	 * @return the property's behavior for the case that the inner observable is missing
	 */
	protected final InnerObservableMissingBehavior<T> getInnerObservableMissingBehavior() {
		return new ImmutableInnerObservableMissingBehavior<>(innerObservableMissingBehavior);
	}

	//#end ACCESSORS FOR SUBCLASSES

	// #begin NESTED CLASSES

	private static class MutableInnerObservableMissingBehavior<T> {

		private static final WhenInnerObservableGoesMissing DEFAULT_WHEN_GOES_MISSING = WhenInnerObservableGoesMissing.KEEP_VALUE;
		private static final WhenInnerObservableMissingOnUpdate DEFAULT_ON_UPDATE = WhenInnerObservableMissingOnUpdate.THROW_EXCEPTION;

		private WhenInnerObservableGoesMissing whenGoesMissing;
		private Optional<? extends Supplier<T>> valueForMissing;
		private WhenInnerObservableMissingOnUpdate onUpdate;

		public MutableInnerObservableMissingBehavior() {
			this.whenGoesMissing = DEFAULT_WHEN_GOES_MISSING;
			this.valueForMissing = Optional.empty();
			this.onUpdate = DEFAULT_ON_UPDATE;
		}

		public void whenGoesMissing(WhenInnerObservableGoesMissing whenGoesMissing) {
			assert whenGoesMissing != null : "The argument 'whenGoesMissing' must not be null.";
			this.whenGoesMissing = whenGoesMissing;
		}

		public void valueForMissing(Supplier<T> valueForMissing) {
			this.valueForMissing = Optional.of(valueForMissing);
		}

		public void onUpdate(WhenInnerObservableMissingOnUpdate onUpdate) {
			assert onUpdate != null : "The argument 'onUpdate' must not be null.";
			this.onUpdate = onUpdate;
		}

	}

	private static class ImmutableInnerObservableMissingBehavior<T> implements InnerObservableMissingBehavior<T> {

		private final WhenInnerObservableGoesMissing whenGoesMissing;
		private final Optional<? extends Supplier<T>> valueForMissing;
		private final WhenInnerObservableMissingOnUpdate onUpdate;

		public ImmutableInnerObservableMissingBehavior(MutableInnerObservableMissingBehavior<T> behavior) {
			this.whenGoesMissing = behavior.whenGoesMissing;
			this.valueForMissing = behavior.valueForMissing;
			this.onUpdate = behavior.onUpdate;
		}

		@Override
		public WhenInnerObservableGoesMissing whenGoesMissing() {
			return whenGoesMissing;
		}

		@Override
		public Optional<? extends Supplier<T>> valueForMissing() {
			return valueForMissing;
		}

		@Override
		public WhenInnerObservableMissingOnUpdate onUpdate() {
			return onUpdate;
		}

	}

	// #end NESTED CLASSES

}
