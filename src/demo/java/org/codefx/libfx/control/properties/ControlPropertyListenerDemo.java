package org.codefx.libfx.control.properties;

import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import org.codefx.libfx.control.properties.ControlProperties;
import org.codefx.libfx.control.properties.ControlPropertyListenerHandle;

/**
 * Demonstrates how to use the {@link ControlPropertyListenerHandle} and its builder.
 */
@SuppressWarnings("static-method")
public class ControlPropertyListenerDemo {

	// #begin CONSTRUCTION & MAIN

	/**
	 * Creates a new demo.
	 */
	private ControlPropertyListenerDemo() {
		// nothing to do
	}

	/**
	 * Runs this demo.
	 *
	 * @param args
	 *            command line arguments (will not be used)
	 */
	public static void main(String[] args) {
		ControlPropertyListenerDemo demo = new ControlPropertyListenerDemo();

		demo.simpleCase();
		demo.attachAndDetach();

		demo.timeNoTypeCheck();
		demo.timeWithTypeCheck();

		demo.castVsTypeChecking();
	}

	// #end CONSTRUCTION & MAIN

	// #begin DEMOS

	/**
	 * Demonstrates the simple case, in which a value processor is added for some key.
	 */
	private void simpleCase() {
		ObservableMap<Object, Object> properties = FXCollections.observableHashMap();

		// build and attach the listener
		ControlProperties.<String> on(properties)
				.forKey("Key")
				.processValue(value -> System.out.println(" -> " + value))
				.buildAttached();

		// set values of the correct type for the correct key
		System.out.print("Set \"Value\" for the correct key for the first time: ");
		properties.put("Key", "Value");
		System.out.print("Set \"Value\" for the correct key for the second time: ");
		properties.put("Key", "Value");

		// set values of the wrong type:
		System.out.println("Set an Integer for the correct key: ... (nothing will happen)");
		properties.put("Key", 5);

		// set values for the wrong key
		System.out.println("Set \"Value\" for another key: ... (nothing will happen)");
		properties.put("OtherKey", "Value");

		System.out.println();
	}

	/**
	 * Demonstrates how a listener can be attached and detached.
	 */
	private void attachAndDetach() {
		ObservableMap<Object, Object> properties = FXCollections.observableHashMap();

		// build the listener (but don't attach it yet) and assign it to a variable
		ControlPropertyListenerHandle listener = ControlProperties.<String> on(properties)
				.forKey("Key")
				.processValue(value -> System.out.println(" -> " + value))
				.buildDetached();

		// set a value when the listener is not yet attached
		System.out.println(
				"Set \"ExistingValue\" before attaching the listener: ... (nothing will happen)");
		properties.put("Key", "ExistingValue");

		// now attach the listener
		System.out.print("When the listener is set, \"ExistingValue\" is processed and removed: ");
		listener.attach();

		System.out.print("Set \"Value\": ");
		properties.put("Key", "Value");

		// detach the listener
		listener.detach();
		System.out.println("Set \"UnnoticedValue\" when the listener is detached: ... (nothing will happen)");

		System.out.println();
	}

	/**
	 * Measures the time it takes to get a lot of {@link ClassCastException}.
	 */
	private void timeNoTypeCheck() {
		ObservableMap<Object, Object> properties = FXCollections.observableHashMap();

		Consumer<String> unreached = value -> {
			throw new RuntimeException("Should not be executed!");
		};

		// build and a attach a listener which does no type check before cast
		ControlProperties.<String> on(properties)
				.forKey("Key")
				.processValue(unreached)
				.buildAttached();

		// add a couple of values of the wrong type to average the time that takes
		Integer valueOfWrongType = 5;
		int runs = (int) 1e5;
		long startTimeInNS = System.nanoTime();

		for (int i = 0; i < runs; i++)
			properties.put("Key", valueOfWrongType);

		long endTimeInNS = System.nanoTime();
		long timePerRunInNS = (endTimeInNS - startTimeInNS) / runs;
		System.out.println("For unchecked casts, adding a value of the wrong type takes ~" + timePerRunInNS + " ns.");

		System.out.println();
	}

	/**
	 * Demonstrates how type checking increases performance if values of an incorrect type are added frequently.
	 */
	private void timeWithTypeCheck() {
		ObservableMap<Object, Object> properties = FXCollections.observableHashMap();

		Consumer<String> unreached = value -> {
			throw new RuntimeException("Should not be executed!");
		};

		// build and a attach a listener which does a type check before cast
		ControlProperties.<String> on(properties)
				.forKey("Key")
				.forValueType(String.class)
				.processValue(unreached)
				.buildAttached();

		// add a couple of values of the wrong type to average the time that takes
		Integer valueOfWrongType = 5;
		int runs = (int) 1e5;
		long startTimeInNS = System.nanoTime();

		for (int i = 0; i < runs; i++)
			properties.put("Key", valueOfWrongType);

		long endTimeInNS = System.nanoTime();
		long timePerRunInNS = (endTimeInNS - startTimeInNS) / runs;
		System.out.println("For checked casts, adding a value of the wrong type takes ~" + timePerRunInNS + " ns.");

		System.out.println();
	}

	// #end DEMOS

	/**
	 * TODO (nipa): I don't get it. The simple test below clearly shows that raising an exception takes about 6.000 ns.
	 * So why the hell does {@link #timeNoTypeCheck()} run way faster than that?!
	 * <p>
	 * Some days later: I ran this again and discovered that the time difference is now very measurable and looks
	 * correct. Perhaps some JVM optimization because I ran it so often?
	 */
	private void castVsTypeChecking() {
		int runs = (int) 1e5;
		Object integer = 3;

		// CAST
		long start = System.nanoTime();
		for (int i = 0; i < runs; i++)
			try {
				String string = (String) integer;
				System.out.println(string);
			} catch (ClassCastException e) {
				// do nothing
			}
		long end = System.nanoTime();
		System.out.println("Each unchecked cast took ~" + (end - start) / runs + " ns.");

		// TYPE CHECK
		start = System.nanoTime();
		for (int i = 0; i < runs; i++)
			if (String.class.isInstance(integer)) {
				String bar = (String) integer;
				System.out.println(bar);
			}
		end = System.nanoTime();
		System.out.println("Each type check took ~" + (end - start) / runs + " ns.");
	}
}
