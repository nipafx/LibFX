package org.codefx.libfx.nesting;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;

import org.codefx.libfx.nesting.property.NestedDoubleProperty;
import org.codefx.libfx.nesting.property.NestedProperty;
import org.codefx.libfx.nesting.property.NestedStringProperty;

/**
 * Demonstrates some features of the nesting API.
 */
public class NestedPropertyDemo {

	// #region ATTRIBUTES

	/**
	 * The currently selected employee.
	 */
	private final Property<Employee> currentEmployee;

	//#end ATTRIBUTES

	// #region CONSTRUCTION & MAIN

	/**
	 * Creates a new demo.
	 */
	private NestedPropertyDemo() {
		this.currentEmployee = new SimpleObjectProperty<>(new Employee(54000, "Some Street"));
	}

	/**
	 * Runs this demo.
	 *
	 * @param args
	 *            command line arguments (will not be used)
	 */
	public static void main(String[] args) {
		NestedPropertyDemo demo = new NestedPropertyDemo();

		demo.nestedPropertyCreation();
		demo.nestedPropertyCreationWithBuilder();
		demo.nestedPropertyBinding();
		demo.nestedPropertyBindingWithMissingInnerObservable();
		demo.additionalNestedFeatures();
	}

	//#end CONSTRUCTION & MAIN

	// #region DEMOS

	/**
	 * Demonstrates how to create some nested properties.
	 */
	private void nestedPropertyCreation() {
		print("CREATION");

		// all created properties wrap the current employee's street name (which is a String)

		// create a Property<String> by starting on the 'currentEmployee' property,
		// nest to the employee's address and then to the address' street name;
		Property<String> asObjectProperty = Nestings.on(currentEmployee)
				.nestProperty(employee -> employee.addressProperty())
				.nestProperty(address -> address.streetNameProperty())
				.buildProperty();
		print("The nested 'Property<String>' has the value: \"" + asObjectProperty.getValue() + "\"");

		// now, create a StringProperty instead; note the second nesting step which is different from above
		StringProperty asStringProperty = Nestings.on(currentEmployee)
				.nestProperty(employee -> employee.addressProperty())
				.nestStringProperty(address -> address.streetNameProperty())
				.buildProperty();
		print("The nested 'StringProperty' has the value: \"" + asStringProperty.getValue() + "\"");

		// 'buildProperty' actually returns a 'Nested...Property', which also implements the interface 'Nested'
		NestedStringProperty asNestedStringProperty = Nestings.on(currentEmployee)
				.nest(employee -> employee.addressProperty())
				.nestStringProperty(address -> address.streetNameProperty())
				.buildProperty();
		print("The 'NestedStringProperty' has the value: \"" + asNestedStringProperty.getValue() + "\"");

		// calls to 'nestProperty' can be cut short; note the first nesting step which is different from above
		NestedStringProperty withShortcut = Nestings.on(currentEmployee)
				.nest(employee -> employee.addressProperty())
				.nestStringProperty(address -> address.streetNameProperty())
				.buildProperty();
		print("The 'NestedStringProperty' (with shortcut) has the value: \"" + withShortcut.getValue() + "\"");

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
				.nest(employee -> employee.addressProperty())
				.nestStringProperty(address -> address.streetNameProperty())
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
				.nestDoubleProperty(employee -> employee.salaryProperty())
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
	 * Demonstrates how a {@link NestedProperty} behaves when the inner
	 */
	private void nestedPropertyBindingWithMissingInnerObservable() {
		print("NESTED PROPERTY BINDING WHEN INNER OBSERVABLE IS MISSING");

		// create a nested property for the current employee's street name
		NestedStringProperty currentEmployeesStreetName = Nestings.on(currentEmployee)
				.nest(employee -> employee.addressProperty())
				.nestStringProperty(address -> address.streetNameProperty())
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
				.nest(employee -> employee.addressProperty())
				.nestStringProperty(address -> address.streetNameProperty())
				.buildProperty();

		// the interface 'Nested' has a property which indicates whether the inner observable is present;
		// one use would be to automatically disable a UI element which displays the property's value;
		// in this case, a change listener is added which simply prints the new state
		currentEmployeesStreetName.innerObservablePresentProperty()
		.addListener(
				(observable, oldValue, newValue) -> print("\tInner observable present changed to \"" + newValue
						+ "\"."));

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
