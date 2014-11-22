package org.codefx.libfx.nesting;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.codefx.libfx.nesting.listener.NestedChangeListenerHandle;
import org.codefx.libfx.nesting.listener.NestedInvalidationListenerHandle;
import org.codefx.libfx.nesting.property.NestedDoubleProperty;
import org.codefx.libfx.nesting.property.NestedProperty;

/**
 * <p>
 * This class provides static functions to obtain builders for nested classes like {@link Nesting} or
 * {@link NestedProperty}.
 * <h2>Builders</h2> Calling {@code on} will return a builder whose type depends on the type of the specified
 * observable. Similarly a call to one of the builders' {@code nest...}-methods returns a (new) builder whose type
 * depends on the type of observable the nesting step will return. Each type of builder allows only those functions
 * which are supported by that observable.
 * <h3>Examples on Builder Types</h3> If the last nesting step provides a {@link javafx.beans.property.DoubleProperty
 * DoubleProperty}, a {@link DoublePropertyNestingBuilder} will be returned. Because a {@code Double} cannot contain
 * another observable no further nesting is possible and hence no {@code nest...}-methods are available. But it can be
 * used to build a {@link NestedDoubleProperty} which in turn is not possible e.g. on an {@link ObservableValue} and is
 * hence not provided by an {@link ObservableValueNestingBuilder}.
 * <p>
 * Likewise a {@link ChangeListener} cannot be added if the last step provides an {@link Observable} because it only
 * accepts {@link InvalidationListener InvalidationListeners}.
 * <h2>Examples</h2> More examples and explanations can be found online.
 * <h3>Change Listener</h3>
 *
 * <pre>
 * {@code
 * Nestings.on(currentEmployee)
 * 	.nestProperty(employee -> employee.addressProperty())
 * 	.nestProperty(address -> address.streetNameProperty())
 * 	.addListener((observable, oldValue, newValue) -> ... );
 * }
 * </pre>
 * <h3>Nested Property</h3>
 *
 * <pre>
 * {@code
 * NestedStringProperty asNestedStringProperty = Nestings.on(currentEmployee)
 * 	.nestProperty(employee -> employee.addressProperty())
 * 	.nestStringProperty(address -> address.streetNameProperty())
 * 	.buildProperty();
 * }
 * </pre>
 *
 * @see Nesting
 * @see NestedProperty
 * @see NestedChangeListenerHandle
 * @see NestedInvalidationListenerHandle
 */
public class Nestings {

	/**
	 * Starts a nesting with the specified outer observable.
	 *
	 * @param outerObservable
	 *            the outer {@link Observable} on which the nesting begins
	 * @return an instance of {@link ObservableNestingBuilder} which depends on the specified outer observable
	 */
	public static ObservableNestingBuilder on(Observable outerObservable) {
		return new ObservableNestingBuilder(outerObservable);
	}

	/**
	 * Starts a nesting with the specified outer observable.
	 *
	 * @param <T>
	 *            the type the outer observable wraps
	 * @param outerObservable
	 *            the outer {@link ObservableValue} on which the nesting begins
	 * @return an instance of {@link ObservableValueNestingBuilder} which depends on the specified outer observable
	 */
	public static <T> ObservableValueNestingBuilder<T> on(ObservableValue<T> outerObservable) {
		return new ObservableValueNestingBuilder<T>(outerObservable);
	}

	/**
	 * Starts a nesting with the specified outer property.
	 *
	 * @param <T>
	 *            the type the outer property wraps
	 * @param outerProperty
	 *            the outer {@link Property} on which the nesting begins
	 * @return an instance of {@link ObjectPropertyNestingBuilder} which depends on the specified outer observable value
	 */
	public static <T> ObjectPropertyNestingBuilder<T> on(Property<T> outerProperty) {
		return new ObjectPropertyNestingBuilder<T>(outerProperty);
	}

}
