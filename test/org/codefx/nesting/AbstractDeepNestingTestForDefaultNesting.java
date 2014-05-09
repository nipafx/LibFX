package org.codefx.nesting;

import static org.codefx.nesting.testhelper.NestingAccess.setInnerValue;
import static org.codefx.nesting.testhelper.NestingAccess.setOuterValue;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import org.codefx.nesting.testhelper.InnerValue;
import org.codefx.nesting.testhelper.OuterValue;

/**
 * Superclass for tests for deep nestings which are based on the nesting hierarchy in the package
 * {@link org.codefx.nesting.testhelper testhelper}.
 * <p>
 * It leaves {@link #createNewNestingFromOuterObservable(Observable)} and {@link #getInnerObservable(Observable)}
 * unimplemented because they refer to the inner observable of generic type {@code O}.
 *
 * @param <O>
 *            the type the nesting hierarchy's inner observable which is also the type wrapped by the nesting
 */
public abstract class AbstractDeepNestingTestForDefaultNesting<O extends Observable>
		extends AbstractDeepNestingTest<Property<OuterValue>, O> {

	@Override
	protected Property<OuterValue> createNewNestingHierarchy() {
		OuterValue outer = OuterValue.createWithInnerType();
		return new SimpleObjectProperty<>(outer);
	}

	@Override
	protected void setNewValue(Property<OuterValue> outerObservable, Level level, Value kindOfNewValue) {

		switch (level) {
			case NESTED:
				switch (kindOfNewValue) {
					case ANY:
						setInnerValue(outerObservable, InnerValue.createWithObservables());
						break;
					case ANY_WITH_NULL_OBSERVABLE:
						setInnerValue(outerObservable, InnerValue.createWithNulls());
						break;
					case NULL:
						setInnerValue(outerObservable, null);
						break;
					default:
						throw new IllegalArgumentException();
				}
				break;
			case OUTER:
				switch (kindOfNewValue) {
					case ANY:
						setOuterValue(outerObservable, OuterValue.createWithInnerType());
						break;
					case ANY_WITH_NULL_OBSERVABLE:
						setOuterValue(outerObservable, OuterValue.createWithNull());
						break;
					case NULL:
						setOuterValue(outerObservable, null);
						break;
					default:
						throw new IllegalArgumentException();
				}
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

}
