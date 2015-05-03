package org.codefx.libfx.nesting;

import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import org.codefx.libfx.nesting.Employee.Address;
import org.codefx.libfx.nesting.property.NestedDoubleProperty;
import org.codefx.libfx.nesting.property.NestedProperty;
import org.codefx.libfx.nesting.property.NestedStringProperty;

/**
 * Demonstrates some features of the nesting API.
 */
public class NestedDemo {

	// #begin FIELDS

	/**
	 * The currently selected employee.
	 */
	private final Property<Employee> currentEmployee;

	//#end FIELDS

	// #begin CONSTRUCTION & MAIN

	/**
	 * Creates a new demo.
	 */
	private NestedDemo() {
		this.currentEmployee = new SimpleObjectProperty<>(new Employee(54000, "Some Street"));
	}

	/**
	 * Runs this demo.
	 *
	 * @param args
	 *            command line arguments (will not be used)
	 */
	public static void main(String[] args) {
		NestedDemo demo = new NestedDemo();

		demo.nestingCreation();
		demo.nestingCreationWithMethodReferences();
		demo.nestedListenerCreation();
		demo.nestedPropertyCreation();
		demo.nestedPropertyCreationWithBuilder();
		demo.nestedPropertyBinding();
		demo.nestedPropertyBindingWithMissingInnerObservable();
		demo.additionalNestedFeatures();
	}

	//#end CONSTRUCTION & MAIN

	// #begin DEMOS

	/**
	 * Demonstrates how to create a {@link Nesting}.
	 */
	private void nestingCreation() {
		print("NESTING CREATION");

		/*
		 * A 'Nesting' is the basic building block of this API. Its Javadoc explains the terminology which is used in
		 * these demos as well as in the rest of the documentation.
		 */

		/*
		 * A 'Nesting'-instance is created in several steps, which are shown here. It can then be used to create other
		 * nested objects like nested properties or nested listeners. Very often the nesting itself is not needed and
		 * the goal is the creation of those other objects based in it. In those cases the builder methods for those
		 * objects (e.g. 'buildProperty') can and should be called directly. What is important in this demo method is
		 * that all possibilities before calling a builder method apply to all kinds of nested functionality like nested
		 * properties and nested listeners.
		 */

		// all created nestings wrap an observable which contains the current employee's street name (which is a String)

		// create a 'Nesting<Property<String>>' by starting on the 'currentEmployee' property,
		// nest to the employee's address and then to the address' street name;
		Nesting<Property<String>> withObjectProperty = Nestings.on(currentEmployee)
				.nestProperty(employee -> employee.addressProperty())
				.nestProperty(address -> address.streetNameProperty())
				.buildNesting();
		print("The 'Nesting<Property<String>>' has the value: \"" + getValueFromNesting(withObjectProperty) + "\"");

		// now, create a 'Nesting<StringProperty>' instead; note the second nesting step which is different from above
		Nesting<StringProperty> withStringProperty = Nestings.on(currentEmployee)
				.nestProperty(employee -> employee.addressProperty())
				.nestStringProperty(address -> address.streetNameProperty())
				.buildNesting();
		print("The 'Nesting<StringProperty>' has the value: \"" + getValueFromNesting(withStringProperty) + "\"");

		// calls to 'nestProperty' can be cut short; note the first nesting step which is different from above
		Nesting<StringProperty> withShortcut = Nestings.on(currentEmployee)
				.nest(employee -> employee.addressProperty())
				.nestStringProperty(address -> address.streetNameProperty())
				.buildNesting();
		print("The 'Nesting<StringProperty>' (with shortcut) has the value: \""
				+ getValueFromNesting(withShortcut) + "\"");

		// if 'employee.addressProperty' were no property but an 'ObservableValue', a 'Nesting<ObservableValue<String>'
		// could also be created; note the second nesting call which differs from those above
		Nesting<ObservableValue<String>> withObservableValue = Nestings.on(currentEmployee)
				.nestProperty(employee -> employee.addressProperty())
				.nestObservableValue(address -> address.streetNameProperty())
				.buildNesting();
		print("The 'Nesting<ObservableValue<String>>' has the value: \""
				+ getValueFromNesting(withObservableValue) + "\"");

		// the same is true, if it were only an 'Observable'
		Nesting<Observable> withObservable = Nestings.on(currentEmployee)
				.nestProperty(employee -> employee.addressProperty())
				.nestObservable(address -> address.streetNameProperty())
				.buildNesting();
		print("The 'Nesting<Observable>'s value can not be accessed, so let's call 'toString': \""
				+ withObservable.innerObservableProperty().getValue().get().toString() + "\"");

		print();
	}

	/**
	 * Demonstrates how to create a {@link Nesting}.
	 */
	private void nestingCreationWithMethodReferences() {
		print("NESTING CREATION WITH METHOD REFERENCES");

		/*
		 * The nesting steps take as input a function from an object in the nesting hierarchy to the property containing
		 * the next object. Instead of using lambda expressions to create those functions, method references are also
		 * possible. They might even be considered more expressive and are hence the recommended way to create nestings.
		 */

		Nesting<StringProperty> withShortcut = Nestings.on(currentEmployee)
				.nest(Employee::addressProperty)
				.nestStringProperty(Address::streetNameProperty)
				.buildNesting();
		print("The 'Nesting<StringProperty>' (with method references) has the value: \""
				+ getValueFromNesting(withShortcut) + "\"");

		print();
	}

	/**
	 * Demonstrates how to create nested listener.
	 */
	private void nestedListenerCreation() {
		print("LISTENER CREATION");

		/*
		 * The listener creation is similar to the nesting creation (see above) and only differs in the final call to
		 * 'build...'. Note that a listener can only be added if the type of the Nesting's inner observable allows it.
		 * This means that a 'InvalidationListener' can always be added, but a 'ChangeListener' only to an
		 * 'ObservableValue'.
		 */

		// nest as above and then add a change listener
		Nestings.on(currentEmployee)
				.nestProperty(Employee::addressProperty)
				.nestProperty(Address::streetNameProperty)
				.addListener((observable, oldValue, newValue) -> {/* do something here */});

		// an invalidation listener could even be added if 'employee.addressProperty' were only an observable
		Nestings.on(currentEmployee)
				.nestProperty(Employee::addressProperty)
				.nestObservable(Address::streetNameProperty)
				.addListener(observable -> {/* do something here */});

		print();
	}

	/**
	 * Demonstrates how to create some nested properties.
	 */
	private void nestedPropertyCreation() {
		print("PROPERTY CREATION");

		/*
		 * The property creation is similar to the nesting creation (see above) and only differs in the final call to
		 * 'build...'. Note that a property can only be created if the type of the Nesting's inner observable is also a
		 * 'Property'. The reason for this is that only properties allow reading and writing their value.
		 */

		// nest as above but instead of creating a 'Nesting<Property<String>>', create a 'Property<String>'
		Property<String> asObjectProperty = Nestings.on(currentEmployee)
				.nestProperty(Employee::addressProperty)
				.nestProperty(Address::streetNameProperty)
				.buildProperty();
		print("The nested 'Property<String>' has the value: \"" + asObjectProperty.getValue() + "\"");

		// now, create a 'StringProperty instead'
		StringProperty asStringProperty = Nestings.on(currentEmployee)
				.nestProperty(Employee::addressProperty)
				.nestStringProperty(Address::streetNameProperty)
				.buildProperty();
		print("The nested 'StringProperty' has the value: \"" + asStringProperty.getValue() + "\"");

		// 'buildProperty' actually returns a 'Nested...Property', which also implements the interface 'Nested'
		// (its additional functionality is demonstrated further below)
		NestedStringProperty asNestedStringProperty = Nestings.on(currentEmployee)
				.nestProperty(Employee::addressProperty)
				.nestStringProperty(Address::streetNameProperty)
				.buildProperty();
		print("The 'NestedStringProperty' has the value: \"" + asNestedStringProperty.getValue() + "\"");

		print();
	}

	/**
	 * Demonstrates how to create nested properties with builders.
	 */
	private void nestedPropertyCreationWithBuilder() {
		print("CREATION WITH BUILDER");

		// after nesting is done, the call to 'buildProperty' can be replaced by 'buildPropertyWithBuilder',
		// which allows to set additional values for the created nested property
		NestedStringProperty addressWithBeanAndName = Nestings.on(currentEmployee)
				.nest(Employee::addressProperty)
				.nestStringProperty(Address::streetNameProperty)
				.buildPropertyWithBuilder()
				.setBean(this)
				.setName("addressWithBean")
				.build();
		print("The 'NestedStringProperty' has bean class \""
				+ addressWithBeanAndName.getBean().getClass().getSimpleName()
				+ "\" and bean name \"" + addressWithBeanAndName.getName() + "\"");

		print();
	}

	/**
	 * Demonstrates how the binding between the object hierarchy's inner property and the nested property works.
	 */
	private void nestedPropertyBinding() {
		print("NESTED PROPERTY BINDING");

		// create a nested property for the current employee's salary
		NestedDoubleProperty currentEmployeesSalary = Nestings.on(currentEmployee)
				.nestDoubleProperty(Employee::salaryProperty)
				.buildProperty();

		print("The object hierarchy's value is initially the same as the nested property's value:");
		printSalaryValues(currentEmployee, currentEmployeesSalary);

		// change the values
		currentEmployee.getValue().salaryProperty().set(58000);
		print("When the object hierarchy's value is changed, the nested property's value changes as well:");
		printSalaryValues(currentEmployee, currentEmployeesSalary);

		currentEmployeesSalary.set(62000);
		print("When the nested property's value is changed, the object hierarchy's value changes as well:");
		printSalaryValues(currentEmployee, currentEmployeesSalary);

		// change the object hierarchy
		print("\nNow change the object hierarchy so that the inner property is a different one.\n");

		Employee oldEmployee = currentEmployee.getValue();
		Employee newEmployee = new Employee(42000, "Another Street");
		currentEmployee.setValue(newEmployee);

		print("When the object hierarchy is changed, the nested property's value changes as well:");
		printSalaryValues(currentEmployee, currentEmployeesSalary);

		currentEmployee.getValue().salaryProperty().set(45000);
		print("When the new object hierarchy's value is changed, the nested property's value changes as well:");
		printSalaryValues(currentEmployee, currentEmployeesSalary);
		print("But the old hierarchy - in this case the old employee's salary - is unchanged: \""
				+ oldEmployee.salaryProperty().get() + "\"");

		currentEmployeesSalary.set(48000);
		print("Similarly, when the nested property's value is changed, the new object hierarchy's value changes as well:");
		printSalaryValues(currentEmployee, currentEmployeesSalary);
		print("Again, the old hierarchy - in this case the old employee's salary - is unchanged: \""
				+ oldEmployee.salaryProperty().get() + "\"");

		print();
	}

	/**
	 * Demonstrates how a {@link NestedProperty} behaves when the inner observable is missing.
	 */
	private void nestedPropertyBindingWithMissingInnerObservable() {
		print("NESTED PROPERTY BINDING WHEN INNER OBSERVABLE IS MISSING");

		// create a nested property for the current employee's street name
		NestedStringProperty currentEmployeesStreetName = Nestings.on(currentEmployee)
				.nest(Employee::addressProperty)
				.nestStringProperty(Address::streetNameProperty)
				.buildProperty();

		print("Nested property's initial street name: \"" + currentEmployeesStreetName.get() + "\"");

		currentEmployee.getValue().addressProperty().setValue(null);
		print("The inner observable is now missing (is present: \""
				+ currentEmployeesStreetName.isInnerObservablePresent() + "\")");

		currentEmployeesStreetName.set("Null Street");
		print("The nested property can still be changed: \"" + currentEmployeesStreetName.get() + "\"");

		currentEmployee.getValue().addressProperty().setValue(new Employee.Address("New Street"));
		print("When a new inner observable is present (\"" + currentEmployeesStreetName.isInnerObservablePresent()
				+ "\"), the nested property holds its value: \"" + currentEmployeesStreetName.get() + "\"");

		print();
	}

	/**
	 * Demonstrates the additional features of the interface {@link Nested}, which is implemented by all nested
	 * properties.
	 */
	private void additionalNestedFeatures() {
		print("FEATURES OF THE INTERFACE 'NESTED'");

		// create a nested property for the current employee's street name
		NestedStringProperty currentEmployeesStreetName = Nestings.on(currentEmployee)
				.nest(Employee::addressProperty)
				.nestStringProperty(Address::streetNameProperty)
				.buildProperty();

		// the interface 'Nested' has a property which indicates whether the inner observable is present;
		// one use would be to automatically disable a UI element which displays the property's value;
		// in this case, a change listener is added which simply prints the new state
		currentEmployeesStreetName.innerObservablePresentProperty().addListener(
				(observable, oldValue, newValue)
				-> print("\tInner observable present changed to \"" + newValue + "\"."));

		print("Set the 'currentEmployee' to null, which means that no inner observable will be present.");
		Employee notNullEmployee = currentEmployee.getValue();
		currentEmployee.setValue(null);

		print("Reset the old employee, which makes an inner observable present.");
		currentEmployee.setValue(notNullEmployee);

		print("Set a new address for the current employee, which will make *another* inner observable present.");
		currentEmployee.getValue().addressProperty().setValue(new Employee.Address("A new street."));

		print("Set the current employee's address to null, which means that no inner observable will be present.");
		currentEmployee.getValue().addressProperty().setValue(null);

		print("Set a new address with a null street name for the current employee, "
				+ "which will make an inner observable available. "
				+ "That its value will be null does not matter as it is still present.");
		currentEmployee.getValue().addressProperty().setValue(new Employee.Address(null));

		print();
	}

	//#end DEMOS

	/**
	 * Prints an empty line to the console.
	 */
	private static void print() {
		System.out.println();
	}

	/**
	 * Prints the specified text to the console.
	 *
	 * @param text
	 *            the text to print
	 */
	private static void print(String text) {
		System.out.println(text);
	}

	/**
	 * Returns the value held by the specified nesting's inner observable.
	 *
	 * @param <T>
	 *            the type of value contained in the observable
	 * @param nesting
	 *            the {@link Nesting} whose value will be returned
	 * @return 'nesting.innerObservableProperty().getValue().get().getValue()'
	 */
	private static <T> T getValueFromNesting(Nesting<? extends ObservableValue<T>> nesting) {
		return nesting.innerObservableProperty().getValue().get().getValue();
	}

	/**
	 * Outputs the salary of both specified properties.
	 *
	 * @param currentEmployee
	 *            the property holding the current employee; the printed value is accessed by moving through the object
	 *            hierarchy
	 * @param currentEmployeesSalary
	 *            the nested property holding the current employee's salary; the printed value is accessed by simply
	 *            getting it
	 */
	private static void printSalaryValues(Property<Employee> currentEmployee, DoubleProperty currentEmployeesSalary) {
		String salaries = "\tSalaries: ";
		salaries += "\"" + currentEmployee.getValue().salaryProperty().get() + "\" (object hierarchy) ";
		salaries += "\"" + currentEmployeesSalary.get() + "\" (nested property) ";
		print(salaries);
	}

}
