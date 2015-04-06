package org.codefx.libfx.collection.pool;

import java.util.Objects;
import java.util.OptionalInt;

import org.codefx.libfx.collection.pool.ResourcePoolStrategy.MaintenanceAction;
import org.codefx.libfx.collection.pool.ResourcePoolStrategy.MaintenanceInstruction;

/**
 * A simple implementation of {@link MaintenanceInstruction}.
 *
 * @param <K>
 *            the type of keys used to identify resources
 */
public final class SimpleMaintenanceInstruction<K> implements MaintenanceInstruction<K> {

	private final K key;

	private final MaintenanceAction action;

	private final OptionalInt argument;

	private SimpleMaintenanceInstruction(K key, MaintenanceAction action, OptionalInt argument) {
		Objects.requireNonNull(key, "The argument 'key' must not be null.");
		Objects.requireNonNull(action, "The argument 'action' must not be null.");
		Objects.requireNonNull(argument, "The argument 'argument' must not be null.");

		this.key = key;
		this.action = action;
		this.argument = argument;
	}

	/**
	 * Creates a new {@code MaintenanceInstruction} with the specified arguments.
	 *
	 * @param <K>
	 *            the type of keys used to identify resources
	 * @param key
	 *            the key for which the action has to be executed
	 * @param action
	 *            the action to execute
	 * @param argument
	 *            the argument for the {@link #action()} if applicable
	 * @return a {@link MaintenanceInstruction}
	 */
	public static <K> MaintenanceInstruction<K> create(K key, MaintenanceAction action, OptionalInt argument) {
		return new SimpleMaintenanceInstruction<>(key, action, argument);
	}

	@Override
	public K forKey() {
		return key;
	}

	@Override
	public MaintenanceAction action() {
		return action;
	}

	@Override
	public OptionalInt argument() {
		return argument;
	}

	@Override
	public String toString() {
		return "Maintenance Instruction [key: " + key + " / action: " + action + " / arg: " + argument + "]";
	}

}
