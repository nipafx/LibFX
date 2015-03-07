package org.codefx.libfx.collection.transform;

public class ElementTypes {

	public static class Mammal {

		private final String name;

		public Mammal(String name) {
			this.name = name;
		}

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

	public static class Feline extends Mammal {

		public Feline(String name) {
			super(name);
		}

		@Override
		public String toString() {
			return "Feline [name=" + getName() + "]";
		}

	}

	public static class Cat extends Feline {

		public Cat(String name) {
			super(name);
		}

		@Override
		public String toString() {
			return "Cat [name=" + getName() + "]";
		}

	}

}
