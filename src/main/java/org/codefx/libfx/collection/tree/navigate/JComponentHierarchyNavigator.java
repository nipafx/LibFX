package org.codefx.libfx.collection.tree.navigate;

import java.awt.Component;
import java.awt.Container;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

import javax.swing.JComponent;

/**
 * A {@link TreeNavigator} for a Swing component hierarchy.
 * <p>
 * Note that {@link #getParent(JComponent) getParent} will return an (optional) {@link JComponent}. This is not possible
 * if the parent is not of a subtype (e.g. a {@link javax.swing.JFrame JFrame}) in which case an empty {@code Optional}
 * is returned. Similarly, children of {@code JComponent}s might not be {@code JComponent}s themselves. For this reason
 * {@link #getChild(JComponent, int) getChild} will return an empty {@code Optional} for a child which is no
 * {@code JComponent}. If this is undesired, consider using a {@link ComponentHierarchyNavigator} instead.
 * <p>
 * This implementation is thread-safe in the sense that individual method calls will not fail if the component hierarchy
 * is changed concurrently. But it can not prevent return values from getting stale so chaining calls might lead to
 * unexpected results, e.g.:
 *
 * <pre>
 * JComponent component = ...
 * if (getParent(component).isPresent()) {
 * 	// the component has a parent, so it should have a child index, too;
 * 	// but the component hierarchy may have changed, so 'indexPresent' may be false
 * 	boolean indexPresent = getChildIndex(component).isPresent();
 * }
 * </pre>
 * Similarly:
 *
 * <pre>
 * JComponent parent = ...
 * Optional&lt;JComponent&gt; child1 = getChild(parent, 0);
 * Optional&lt;JComponent&gt; child2 = getChild(parent, 0);
 * // if the component hierarchy changed between the two calls, this may be false
 * boolean sameChildren = child1.equals(child2);
 * </pre>
 */
public class JComponentHierarchyNavigator implements TreeNavigator<JComponent> {

	@Override
	public Optional<JComponent> getParent(JComponent child) {
		Objects.requireNonNull(child, "The argument 'child' must not be null.");

		Container parent = child.getParent();
		if (!(parent instanceof JComponent))
			return Optional.empty();

		return Optional.ofNullable((JComponent) parent);
	}

	@Override
	public OptionalInt getChildIndex(JComponent node) {
		Objects.requireNonNull(node, "The argument 'node' must not be null.");

		Component parent = node.getParent();
		if (parent == null)
			return OptionalInt.empty();

		if (!(parent instanceof JComponent))
			return OptionalInt.empty();

		Component[] siblings = ((JComponent) parent).getComponents();
		return getIndex(node, siblings);
	}

	private static OptionalInt getIndex(Component node, Component[] siblings) {
		try {
			for (int i = 0; i < siblings.length; i++)
				if (siblings[i] == node)
					return OptionalInt.of(i);
			return OptionalInt.empty();
		} catch (ArrayIndexOutOfBoundsException ex) {
			return OptionalInt.empty();
		}
	}

	@Override
	public int getChildrenCount(JComponent parent) {
		Objects.requireNonNull(parent, "The argument 'parent' must not be null.");

		return parent.getComponents().length;
	}

	@Override
	public Optional<JComponent> getChild(JComponent parent, int childIndex) {
		Objects.requireNonNull(parent, "The argument 'parent' must not be null.");
		if (childIndex < 0)
			throw new IllegalArgumentException("The argument 'childIndex' must be non-negative.");

		if (parent.getComponents().length <= childIndex)
			return Optional.empty();

		try {
			// even though we checked first, due to threading this might fail
			Component child = parent.getComponent(childIndex);
			if (!(child instanceof JComponent))
				return Optional.empty();
			return Optional.of((JComponent) child);
		} catch (ArrayIndexOutOfBoundsException ex) {
			return Optional.empty();
		}
	}

}
