package org.codefx.libfx.nesting;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A simple demo class which represents an employee.
 */
class Employee {

	/**
	 * The salary.
	 */
	private final DoubleProperty salary;

	/**
	 * The address.
	 */
	private final ObjectProperty<Address> address;

	/**
	 * Creates a new employee with the specified salary.
	 *
	 * @param initialSalary
	 *            the employee's initial salary
	 * @param streetName
	 *            the name of the street the employee lives in
	 */
	public Employee(double initialSalary, String streetName) {
		this.salary = new SimpleDoubleProperty(this, "salary", initialSalary);
		this.address = new SimpleObjectProperty<>(this, "address", new Address(streetName));
	}

	/**
	 * The salary.
	 *
	 * @return the salary as a property
	 */
	public DoubleProperty salaryProperty() {
		return salary;
	}

	/**
	 * The address.
	 *
	 * @return the address as a property
	 */
	public Property<Address> addressProperty() {
		return address;
	}

	// #region INNER CLASSES

	/**
	 * A simple demo class which represents an employee's address.
	 */
	public static class Address {

		/**
		 * The street name.
		 */
		private final StringProperty streetName;

		/**
		 * Creates a new address with the specified street name.
		 *
		 * @param streetName
		 *            the name of the street
		 */
		public Address(String streetName) {
			this.streetName = new SimpleStringProperty(this, "streetName", streetName);
		}

		/**
		 * The street name.
		 *
		 * @return the street name as a property
		 */
		public StringProperty streetNameProperty() {
			return streetName;
		}

	}

	//#end PRIVATE CLASSES

}
