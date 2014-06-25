package org.codefx.libfx.nesting;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

import org.codefx.libfx.nesting.Nestings;
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
		this.currentEmployee = new SimpleObjectProperty<>(new Employee(54_000));
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
		// TODO change this to DoubleProperty as soon as that is implemented
		Property<Number> currentEmployeesSalary = Nestings.on(currentEmployee)
				.nest(employee -> employee.salaryProperty())
				.buildProperty();

		System.out.println("Salary - object hierarchy: " + currentEmployee.getValue().salaryProperty().getValue());
		System.out.println("Salary - nested property: " + currentEmployeesSalary.getValue());
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
