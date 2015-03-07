package org.codefx.libfx.collection.graph;

import java.util.Optional;
import java.util.OptionalInt;

public interface GraphNavigator<N> {

	// PARENT

	Optional<N> getParent(N child);

	// NODE

	OptionalInt getChildIndex(N node);

	// CHILDREN

	int getChildrenCount(N parent);

	Optional<N> getChild(N parent, int childIndex);

}
