package org.codefx.libfx.nesting;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * An implementation of {@link Nesting} which uses an outer {@link ObservableValue} and a series of nesting steps to get
 * the {@link #innerObservableProperty() innerObservable}.
 *
 * @param <O>
 *            the type of the nesting hierarchy's inner {@link Observable}
 */
@SuppressWarnings("rawtypes")
final class DeepNesting<O extends Observable> implements Nesting<O> {

	//#formatter:off

	/*
	 * GENERIC TYPES
	 *
	 * Because the depth of the nesting is not fixed, the number of involved types is determined at runtime. This class
	 * can hence not use generics for type safety. So it uses tons of raw types, which only works out if the constructor
	 * is called with the correctly typed outer observable and nesting steps.
	 *
	 *
	 * DATA STRUCTURES
	 *
	 * This nesting uses arrays to store instances which are needed to resolve the nesting. Those arrays all have the
	 * same length ('maxLevel') and a uniform structure: the same indices correspond to the same levels in the nesting
	 * hierarchy.
	 *
	 * observables:	outer nested ... nested inner
	 * level:		  0     1    ...  n-1    n
	 * steps[]:		  x     x     x    x			// each step uses values[level] to get the new observ.[level + 1]
	 * observ.[]:	  x     x     x    x			// stored to remove listeners; [0] only stored for uniform loop
	 * values[]:	  x     x     x    x			// stored to compare values and end loop upon reaching same value
	 * listeners[]:	  x     x     x    x			// stored to remove and add the listeners
	 *
	 *
	 * BEHAVIOR
	 *
	 * Whenever a listener registers a changing value it calls 'updateNestingFromLevel' with the level on which the
	 * value changed. The method will start on that level and use the nesting steps to get to the higher ones until it
	 * reaches the inner observable which will be stored in 'innerObservable'. Check the method for details.
	 *
	 */

	//#formatter:on

	// #region PROPERTIES

	/**
	 * The level of the nesting, which is also the length of the arrays.
	 */
	private final int maxLevel;

	/**
	 * The steps one observable's value to the next.
	 */
	private final NestingStep[] nestingSteps;

	/**
	 * The current hierarchy of observables.
	 */
	private final ObservableValue[] observables;

	/**
	 * The values currently held by the observables.
	 */
	private final Object[] values;

	/**
	 * The change listeners which are added to the observables.
	 */
	private final ChangeListener[] changeListeners;

	/**
	 * The property holding the current inner observable.
	 */
	private final Property<Optional<O>> inner;

	//#end PROPERTIES

	// #region CONSTRUCTION

	/**
	 * Creates a new deep nesting which depends on the specified outer observable and uses specified nesting steps.
	 *
	 * @param outerObservable
	 *            the {@link ObservableValue} on which this nesting depends
	 * @param nestingSteps
	 *            the {@link NestingStep NestingSteps} from one observable's value to the next observable; they must be
	 *            ordered such that:
	 *            <ul>
	 *            <li>the first accepts an argument of the type wrapped by the {@code outerObservable} and returns an
	 *            {@link ObservableValue}
	 *            <li>each next accepts an argument of the type wrapped by the observable returned by the step before
	 *            and returns an {@link ObservableValue}
	 *            <li>only the last step might return an {@link Observable}
	 *            </ul>
	 *            These conditions are not checked by the compiler nor during construction. Violations will later lead
	 *            to {@link ClassCastException ClassCastExceptions}.
	 * @throws IllegalArgumentException
	 *             if the list is empty
	 */
	public DeepNesting(ObservableValue outerObservable, List<NestingStep> nestingSteps) {
		Objects.requireNonNull(outerObservable, "The argument 'outerObservable' must not be null.");
		Objects.requireNonNull(nestingSteps, "The argument 'nestedObservableGetters' must not be null.");
		if (nestingSteps.size() < 1)
			throw new IllegalArgumentException("The list 'nestedObservableGetters' must have at least length 1.");

		maxLevel = nestingSteps.size();

		this.observables = createObservables(outerObservable, maxLevel);
		this.values = new Object[maxLevel];
		this.nestingSteps = nestingSteps.toArray(new NestingStep[maxLevel]);
		this.changeListeners = createChangeListeners(maxLevel);
		this.inner = new SimpleObjectProperty<>(this, "inner");

		initializeNesting();
	}

	/**
	 * Creates an initialized array of observables. Its first item is the specified outer observable (its other items
	 * are null).
	 *
	 * @param outerObservable
	 *            the outer observable upon which this nesting depends
	 * @param levels
	 *            the number of levels, which is also the new array's length
	 * @return an initialized array of {@link ObservableValue ObservableValues}
	 */
	private static ObservableValue[] createObservables(ObservableValue outerObservable, int levels) {
		ObservableValue[] observables = new ObservableValue[levels];
		observables[0] = outerObservable;
		return observables;
	}

	/**
	 * Creates an array of change listeners.
	 *
	 * @param levels
	 *            the number of levels, which is also the new array's length
	 * @return an array of {@link ChangeListener ChangeListeners}
	 */
	private ChangeListener[] createChangeListeners(int levels) {
		ChangeListener[] listeners = new ChangeListener[levels];
		for (int level = 0; level < levels; level++) {
			final int theLevel = level;
			listeners[level] = (observable, oldValue, newValue) -> updateNestingFromLevel(theLevel);
		}
		return listeners;
	}

	/**
	 * Initializes this nesting by filling the arrays {@link #observables} and {@link #values} and adding the
	 * corresponding {@link #changeListeners changeListener} to each observable.
	 */
	private void initializeNesting() {
		new NestingInitializer().initialize();
	}

	//#end CONSTRUCTION

	/**
	 * Updates the nesting from the specified level on. This includes moving listeners from old to new observables and
	 * updating the arrays {@link #observables} and {@link #values}.
	 *
	 * @param startLevel
	 *            the level on which to start updating; this will be the one to which the {@link #observables
	 *            observable} which changed its value belongs
	 */
	private void updateNestingFromLevel(int startLevel) {
		new NestingUpdater(startLevel).update();
	}

	// #region ACCESSORS

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReadOnlyProperty<Optional<O>> innerObservableProperty() {
		return inner;
	}

	//#end ACCESSORS

	// #region PRIVATE CLASSES

	/**
	 * Initializes {@link DeepNesting#observables}, {@link DeepNesting#values} and {@link DeepNesting#inner} as well as
	 * adding {@link DeepNesting#changeListeners} to all observables.
	 */
	private class NestingInitializer {

		/**
		 * Initializes the {@code DeepNesting} by filling the arrays {@link DeepNesting#observables} and
		 * {@link DeepNesting#values}, setting {@link DeepNesting#inner} and adding the corresponding
		 * {@link DeepNesting#changeListeners} to each observable.
		 */
		@SuppressWarnings("unchecked")
		public void initialize() {

			// WARNING:
			// This method is highly coupled to 'NestingUpdater.updateCurrentLevel'!
			// Make sure to inspect both methods upon changing one of them.

			/*
			 * Simply update the nesting from level 0 on. But if the updater encounters the same property in the
			 * 'observables' array as on the currently checked level, it does not add a listener so do that here.
			 */
			observables[0].addListener(changeListeners[0]);
			new NestingUpdater(0).update();
		}

	}

	/**
	 * Updates the {@code DeepNesting} when an observable in the nesting hierarchy changes its value - the level on
	 * which the change occurred (i.e. the 'startLevel') is specified during construction.
	 * <p>
	 * The updater loops through the levels {@code [startLevel; innerLevel - 1]}, updates {@code observables} and
	 * {@code values} and moves the {@link DeepNesting#changeListeners} from the old to the new observables. It stops
	 * when a level is found where the stored value equals the current one. In that case all higher levels must be
	 * identical and nothing more needs to be updated.
	 * <p>
	 * Note that the loop will not stop on null observables and null values. Instead it continues and replaces all
	 * stored observables and values with null. This is the desired behavior as the hierarchy is in now an incomplete
	 * state and the old observables and values are obsolete and have to be replaced.
	 */
	private class NestingUpdater {

		/**
		 * The level the updater is currently working on.
		 */
		private int currentLevel;

		/**
		 * Indicates whether the {@link #currentLevel} is the inner level.
		 */
		private boolean currentLevelIsInnerLevel;

		/**
		 * The {@link ObservableValue} on the {@link #currentLevel}.
		 */
		private ObservableValue currentObservable;

		/**
		 * The {@link #currentObservable}'s value.
		 */
		private Object currentValue;

		/**
		 * Indicates whether the {@link #currentValue} differs from the value stored in {@link DeepNesting#values}.
		 */
		private boolean currentValueChanged;

		/**
		 * The observable on the inner level. Must be stored separately because {@link DeepNesting#observables} only
		 * accepts {@link ObservableValue ObservableValues}, which is also the reason why it is too short to also hold
		 * the inner observable.
		 */
		private Observable innerObservable;

		/**
		 * Creates a new updater which starts updating on the specified level.
		 *
		 * @param startLevel
		 *            the level on which this updater starts updating
		 */
		public NestingUpdater(int startLevel) {
			currentLevel = startLevel;
			// there is no listener on the inner level's observable so the start level can never be the inner level
			currentLevelIsInnerLevel = false;

			currentObservable = observables[startLevel];
			currentValue = currentObservable.getValue();
			// note that unless the observable has a strange implementation which calls change listeners
			// even though nothing changed, this will always be true
			currentValueChanged = values[currentLevel] != currentObservable.getValue();
		}

		/**
		 * Updates the nesting from the {@link #currentLevel} on.
		 */
		public void update() {
			while (mustUpdateCurrentLevel()) {
				updateCurrentLevel();
				moveToNextLevel();
			}
			updateInnerObservable();
		}

		/**
		 * Indicates whether the current level must be updated.
		 *
		 * @return true if the {@link #currentLevel} must be updated
		 */
		private boolean mustUpdateCurrentLevel() {
			return currentValueChanged && !currentLevelIsInnerLevel;
		}

		/**
		 * Updates the {@link DeepNesting#observables} and {@link DeepNesting#values} on the {@link #currentLevel}.
		 */
		private void updateCurrentLevel() {

			// WARNING:
			// This method is highly coupled to 'NestingInitializer.initializeNestingLevel0'!
			// Make sure to inspect both methods upon changing one of them.

			updateObservableOnCurrentLevel();
			updateValueOnCurrentLevel();
		}

		/**
		 * Updates {@link DeepNesting#observables}[{@link #currentLevel}] to {@link #currentObservable} and moves the
		 * listener from the old to the new observable.
		 */
		@SuppressWarnings("unchecked")
		private void updateObservableOnCurrentLevel() {
			ObservableValue storedObservable = DeepNesting.this.observables[currentLevel];
			if (storedObservable != currentObservable) {
				DeepNesting.this.observables[currentLevel] = currentObservable;
				if (storedObservable != null)
					storedObservable.removeListener(changeListeners[currentLevel]);
				if (currentObservable != null)
					currentObservable.addListener(changeListeners[currentLevel]);
			}
		}

		/**
		 * Updates {@link #currentValue} and {@link #currentValueChanged} and sets {@link DeepNesting#values}[
		 * {@link #currentLevel}] to {@link #currentValue}.
		 */
		private void updateValueOnCurrentLevel() {
			if (currentObservable == null)
				currentValue = null;
			else
				currentValue = currentObservable.getValue();

			Object storedValue = DeepNesting.this.values[currentLevel];
			currentValueChanged = storedValue != currentValue;
			if (currentValueChanged)
				DeepNesting.this.values[currentLevel] = currentValue;
		}

		/**
		 * Moves to the next level by updating {@link #currentLevel}, {@link #currentLevelIsInnerLevel},
		 * {@link #currentObservable} and possibly {@link #innerObservable}.
		 */
		@SuppressWarnings("unchecked")
		private void moveToNextLevel() {
			Observable nextObservable = null;
			if (currentValue != null)
				nextObservable = nestingSteps[currentLevel].step(currentValue);
			boolean nextIsInnerLevel = (currentLevel + 1 == maxLevel);

			// ... assign them ...
			if (nextIsInnerLevel) {
				currentLevelIsInnerLevel = true;
				innerObservable = nextObservable;
			} else {
				currentLevelIsInnerLevel = false;
				// only the last nesting step is allowed to return an 'Observable'
				currentObservable = (ObservableValue) nextObservable;
			}

			// ... and finally increase level counter
			currentLevel++;
		}

		/**
		 * Updates {@link #innerObservable} if the loop reached it.
		 */
		@SuppressWarnings("unchecked")
		private void updateInnerObservable() {
			// if the loop encountered a level where the stored and the current value are identical,
			// all higher levels are identical as well and the inner observable can not have changed
			if (currentLevelIsInnerLevel) {
				Optional innerObservableOptional = Optional.ofNullable(innerObservable);
				inner.setValue(innerObservableOptional);
			}
		}

	}

	//#end PRIVATE CLASSES

}
