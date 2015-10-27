package org.codefx.libfx.collection.tree;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codefx.tarkastus.AssertFX.assertOperationIsUnsupported;

import org.junit.Test;

import java.util.List;

/**
 * Abstract superclass to all implementations of {@link TreeNode}
 *
 * @param <T> the type of tree node tested by this class
 */
public abstract class AbstractTreeNodeTest<T extends TreeNode<String, T>> {

	@Test
	public void getParent_afterConstruction_parentEmpty() {
		T node = create();

		assertThat(node.getParent()).isEmpty();
	}

	@Test
	public void getContent_afterConstruction_returnsContent() {
		String initialContent = "INITIAL_CONTENT";
		T node = create(initialContent);

		assertThat(node.getContent()).isSameAs(initialContent);
	}

	@Test
	public void getChildren_areUnmodifiable() {
		T node = create();
		List<T> children = node.getChildren();

		assertOperationIsUnsupported(() -> children.add(null));
		assertOperationIsUnsupported(() -> children.addAll(null));
		assertOperationIsUnsupported(() -> children.clear());
		assertOperationIsUnsupported(() -> children.remove(0));
		assertOperationIsUnsupported(() -> children.removeAll(null));
		assertOperationIsUnsupported(() -> children.retainAll(null));
		assertOperationIsUnsupported(() -> children.removeAll(null));
		assertOperationIsUnsupported(() -> children.set(0, null));
		assertOperationIsUnsupported(() -> children.sort(null));
	}

	private T create() {
		return create("");
	}

	protected abstract T create(String content);

}
