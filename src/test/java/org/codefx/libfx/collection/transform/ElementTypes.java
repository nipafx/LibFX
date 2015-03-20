package org.codefx.libfx.collection.transform;

import java.util.Objects;

/**
 * Contains inner classes which are used for {@code Transforming...Tests}.
 */
class ElementTypes {

	/**
	 * A mammal sits at the top of the inheritance hierarchy.
	 */
	public static class Mammal {

		private final String name;

		/**
		 * Creates a new mammal with the specified name.
		 *
		 * @param name
		 *            the animal's name
		 */
		public Mammal(String name) {
			Objects.requireNonNull(name, "The argument 'name' must not be null.");
			this.name = name;
		}

		/**
		 * @return the animal's name
		 */
		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "Mammal [name=" + name + "]";
		}

		@Override
		public final int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public final boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Mammal))
				return false;
			Mammal other = (Mammal) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

	}

	/**
	 * A feline sits in the middle of the inheritance hierarchy.
	 */
	public static class Feline extends Mammal {

		/**
		 * Creates a new feline with the specified name.
		 *
		 * @param name
		 *            the animal's name
		 */
		public Feline(String name) {
			super(name);
		}

		@Override
		public String toString() {
			return "Feline [name=" + getName() + "]";
		}

	}

	/**
	 * A cat sits at the bottom of the inheritance hierarchy.
	 */
	public static class Cat extends Feline {

		/**
		 * Creates a new cat with the specified name.
		 *
		 * @param name
		 *            the animal's name
		 */
		public Cat(String name) {
			super(name);
		}

		@Override
		public String toString() {
			return "Cat [name=" + getName() + "]";
		}

	}

}
