package org.codefx.libfx.nesting;

import java.util.Optional;

import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ObservableValue;

/**
 * <p>
 * A nesting encapsulates a hierarchy of nested {@link ObservableValue ObservableValues}.
 * <p>
 * Its {@link #innerObservableProperty() innerObservable} property always contains the current innermost
 * {@code Observable} in that hierarchy as an {@link Optional}. A {@code Nesting} can be used as a basic building block
 * for other nested functionality.
 * <h2>Nesting Hierarchy</h2> A nesting hierarchy is composed of some {@code ObservableValues}, often simply called
 * <b>observables</b>, and <b>nesting steps</b> which lead from one observable to the next.
 * <p>
 * At the top of the hierarchy stands one of the observables, the so called <b>outer observable</b>. A nesting step will
 * use its value to return the next observable. The next step will use that observable's value to return the next
 * observable and so on. All observables returned by nesting steps are called <b>nested observables</b>. Finally and
 * perhaps most importantly, the last step will lead to the hierarchy's <b>inner observable</b>.
 * <p>
 * As nesting steps require a value to be accessible, all observables on which a step is used must provide a value.
 * Hence they must all implement {@link ObservableValue ObservableValue}. No step is used on the inner observable so it
 * suffices that it implements {@link Observable}.
 * <h3>Example</h3> Consider a class {@code Employee} which has an {@code Property<Address> address}, where
 * {@code Address} has a {@code StringProperty streetName}. There might be a {@code Property<Employee> currentEmployee},
 * which always holds the current employee.
 * <p>
 * In this case the hierarchy would be {@code currentEmployee -> address -> streetName} where {@code currentEmployee} is
 * the outer observable and {@code address} and {@code streetName} are nested observables. Additionally,
 * {@code streetName} is the inner observable.
 * <h2>Present or Missing Inner Observable</h2> If all steps return non-null observables and none of them contains null,
 * the inner observable can be accessed and will be contained in the {@link #innerObservableProperty() innerObservable}
 * property. In this case it is said to be <b>present</b>. The same is true if only the inner observable contains null.
 * <p>
 * If any nesting step returns null or any observable except the inner contains null as a value, the nesting hierarchy
 * can not be fully accessed. The inner observable is said to be <b>missing</b> and the {@code innerObservable} property
 * contains {@link Optional#empty()}.
 * <h2>Evaluation</h2> Nestings will usually be implemented such that they eagerly evaluate the nested observables.
 * <h2>Build</h2> Instances of {@code Nesting} can be created with dedicated builders. These can be obtained by starting
 * with one of the methods in {@link Nestings}. More details can be found there.
 * <p>
 * Nestings are also an important building block for creating other nested instances like
 * {@link org.codefx.libfx.nesting.property.NestedProperty NestedProperty}. A
 * {@link org.codefx.libfx.nesting.NestingObserver NestingObserver} provides a convenient way to work directly with a
 * {@code Nesting}.
 *
 * @see Nestings
 * @param <O>
 *            the type of the nesting hierarchy's inner {@link Observable}
 */
public interface Nesting<O extends Observable> {

	/**
	 * A property holding the current inner observable in the hierarchy as an optional. If some observable or its value
	 * were null, this contains {@link Optional#empty()}.
	 *
	 * @return the inner {@link Observable} in an {@link Optional}
	 */
	ReadOnlyProperty<Optional<O>> innerObservableProperty();

}
