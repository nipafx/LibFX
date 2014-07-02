package org.codefx.libfx.nesting;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

import org.codefx.libfx.nesting.property.NestedProperty;

/**
 * Demonstrates some features of the nesting API.
 */
public class NestingDemo {

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
	private NestingDemo() {
		this.currentEmployee = new SimpleObjectProperty<>(new Employee(54000));
	}

	/**
	 * Runs this demo.
	 *
	 * @param args
	 *            command line arguments (will not be used)
	 */
	public static void main(String[] args) {
		NestingDemo demo = new NestingDemo();
		demo.demoNestedProperties();
	}

	//#end CONSTRUCTION & MAIN

	// #region DEMOS

	/**
	 * Demonstrates the use of {@link NestedProperty}.
	 */
	private void demoNestedProperties() {
		DoubleProperty currentEmployeesSalary = Nestings.on(currentEmployee)
				.nestDoubleProperty(employee -> employee.salaryProperty())
				.buildProperty();

		System.out
				.println("Check that the object hierarchy's value is initially the same as the nested property's value.");
		outputSalaryValues(currentEmployee, currentEmployeesSalary);

		System.out
				.println("Check that when the object hierarchy's value is changed, the nested property's value changes as well.");
		currentEmployee.getValue().salaryProperty().setValue(58000);
		outputSalaryValues(currentEmployee, currentEmployeesSalary);

		System.out
				.println("Check that when the nested property's value is changed, the object hierarchy's value changes as well.");
		currentEmployeesSalary.setValue(62000);
		outputSalaryValues(currentEmployee, currentEmployeesSalary);
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
	private static void outputSalaryValues(Property<Employee> currentEmployee, Property<Number> currentEmployeesSalary) {
		System.out.println("Salary - object hierarchy: " + currentEmployee.getValue().salaryProperty().getValue());
		System.out.println("Salary - nested property: " + currentEmployeesSalary.getValue());
		System.out.println();
	}

	//#end DEMOS

	// #region PRIVATE CLASSES

	/**
	 * A simple demo class which represents an employee.
	 */
	private static class Employee {

		/**
		 * The salary.
		 */
		private final DoubleProperty salary;

		/**
		 * Creates a new employee with the specified salary.
		 *
		 * @param initialSalary
		 *            the employee's initial salary
		 */
		private Employee(double initialSalary) {
			this.salary = new SimpleDoubleProperty(this, "salary", initialSalary);
		}

		/**
		 * The salary.
		 *
		 * @return the salary as a property
		 */
		public DoubleProperty salaryProperty() {
			return salary;
		}

	}

	//#end PRIVATE CLASSES

}
