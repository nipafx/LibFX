package org.codefx.libfx.collection.tree.build;

import static java.util.Objects.requireNonNull;

import org.codefx.libfx.collection.tree.MutableTreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Creates a tree from a set of hierarchically structured elements.
 *
 * <h2>Example</h2>
 * An example of this would be a list of fully qualified type names, from which a package tree is created.
 * <p>
 * In this case the names would be the elements. Each would be split into a list of so called "hierarchy elements", in
 * this case the package names and class/interface name that make it up (e.g. "java.lang.String" -> { "java", "lang",
 * "String" }). This is done by a function given to the factory at construction.
 * <p>
 * Another such function has the responsibility to create the content for each tree node. For this it is given the
 * currently processed element (e,.g, "java.lang.String"), the index of the currently processed hierarchy element (e.g.
 * '1' for "lang") and possibly the already existing node's content. The node might already exist because another
 * element (e.g. "java.lang.Object") might already have led to its creation.
 * <p>
 * The builder will process all elements. For each it uses the first function to split it into a hierarchy and then
 * create nodes for each of the hierarchy elements, aksing the second function to create the content for the nodes.
 *
 * @param <E>
 * 		the type of elements
 * @param <H>
 * 		the type of hierarchy elements into which the elements are split
 * @param <C>
 * 		the type of content carried by the nodes in the resulting tree
 */
public final class HierarchicalTreeFactory<E, H, C> {

	private final Function<E, List<H>> toHierarchy;
	private final ToContentFunction<E, C> toContent;

	/**
	 * Creates a new factory with the specified transformations.
	 *
	 * @param toHierarchy
	 * 		computes a list of hierarchy elements from a given element
	 * @param toContent
	 * 		computes a tree node's content from a given element and hierarchy index
	 */
	public HierarchicalTreeFactory(
			Function<E, List<H>> toHierarchy, ToContentFunction<E, C> toContent) {
		this.toHierarchy = requireNonNull(toHierarchy, "The argument 'toHierarchy' must not be null.");
		this.toContent = requireNonNull(toContent, "The argument 'toContent' must not be null.");
	}

	/**
	 * Creates a forest for the specified elements.
	 *
	 * @param elements
	 * 		a variable number of elements
	 *
	 * @return a list of the tree roots created from the {@code elements}
	 */
	@SafeVarargs
	public final List<MutableTreeNode<C>> createForest(E... elements) {
		return createForest(Arrays.stream(elements));
	}

	/**
	 * Creates a forest for the specified elements.
	 *
	 * @param elements
	 * 		an iterable of elements
	 *
	 * @return a list of the tree roots created from the {@code elements}
	 */
	public List<MutableTreeNode<C>> createForest(Iterable<E> elements) {
		requireNonNull(elements, "The argument 'elements' must not be null.");

		Stream<E> elementStream = StreamSupport.stream(elements.spliterator(), false);
		return createForest(elementStream);
	}

	/**
	 * Creates a forest for the specified elements.
	 *
	 * @param elements
	 * 		a stream of elements
	 *
	 * @return a list of the tree roots created from the {@code elements}
	 */
	public List<MutableTreeNode<C>> createForest(Stream<E> elements) {
		requireNonNull(elements, "The argument 'elements' must not be null.");

		Construction<H, C> construction = new Construction<>();
		elements.forEach(element -> createNodesAlongHierarchy(construction, element));
		return construction.roots;
	}

	private void createNodesAlongHierarchy(Construction<H, C> construction, E element) {
		List<H> hierarchy = toHierarchy.apply(element);

		List<MutableTreeNode<C>> currentNodes = construction.roots;
		Consumer<MutableTreeNode<C>> addChildToCurrentNodes = currentNodes::add;
		for (int currentDepth = 0; currentDepth < hierarchy.size(); currentDepth++) {
			// look for the child belonging to the current hierarchy element
			H currentElement = hierarchy.get(currentDepth);
			Optional<MutableTreeNode<C>> existingNode =
					construction.selectNodeForHierarchyElement(currentNodes, currentElement);
			final MutableTreeNode<C> nextNode;
			if (existingNode.isPresent()) {
				// if it exists, fold the new element into it
				nextNode = foldIntoExistingNode(element, currentDepth, existingNode.get());
			} else {
				// otherwise create a new node
				nextNode = createNewNode(element, currentDepth);
				construction.addNodeForHierarchyElement(nextNode, currentElement);
				addChildToCurrentNodes.accept(nextNode);
			}
			currentNodes = nextNode.getChildren();
			addChildToCurrentNodes = nextNode::addChild;
		}
	}

	private MutableTreeNode<C> foldIntoExistingNode(E element, int currentDepth, MutableTreeNode<C> existingNode) {
		C newContent = toContent.computeContent(element, currentDepth, Optional.of(existingNode.getContent()));
		existingNode.setContent(newContent);
		return existingNode;
	}

	private MutableTreeNode<C> createNewNode(E element, int currentDepth) {
		C newNodeContent = toContent.computeContent(element, currentDepth, Optional.empty());
		return new MutableTreeNode<>(newNodeContent);
	}

	/**
	 * Computes the content for a tree node - see {@link #computeContent(Object, int, Optional) computeContent} for
	 * details.
	 *
	 * @param <E>
	 * 		the type of elements
	 * @param <C>
	 * 		the type of content carried by the nodes in the resulting tree
	 */
	@FunctionalInterface
	public interface ToContentFunction<E, C> {

		/**
		 * Computes the content for a tree node.
		 * <p>
		 * This method will be called repeatedly for the same node: every time an element corresponds to a path through
		 * that node. The order in which these calls occur depend on the order of elements but are generally
		 * unspecified. This method is hence required to be commutative in the sense that it must not matter for the
		 * final content in which order this method was called as long as the number and arguments of those calls
		 * remain the same.
		 *
		 * @param element
		 * 		the element corresponding to a path through the node
		 * @param hierarchyIndex
		 * 		the index of the hierarchy element corresponding to the node
		 * @param existingContent
		 * 		the current node's content; empty if the node is just being constructed
		 *
		 * @return the node's content
		 *
		 * @apiNote The method is called with the hierarchy element's index because the element itself might not be
		 * unique.
		 */
		C computeContent(E element, int hierarchyIndex, Optional<C> existingContent);
	}

	/**
	 * Used to encapsulate the growing forest as well as other implementation specific state.
	 *
	 * @param <H>
	 * 		the type of hierarchy elements into which the elements are split
	 * @param <C>
	 * 		the type of content carried by the nodes in the resulting tree
	 */
	private static class Construction<H, C> {

		final List<MutableTreeNode<C>> roots = new ArrayList<>();
		final IdentityHashMap<MutableTreeNode<C>, H> nodeToHierarchyElement = new IdentityHashMap<>();

		/**
		 * Looks for that node from the specified list of nodes (most likely the list of some node's children) that
		 * corresponds to the specified hierarchy element.
		 */
		public Optional<MutableTreeNode<C>> selectNodeForHierarchyElement(
				Collection<MutableTreeNode<C>> nodes, H hierarchyElement) {
			return nodes
					.stream()
					.filter(node -> nodeToHierarchyElement.get(node).equals(hierarchyElement))
					.reduce((onlyNode, otherNode) -> {
						throw new IllegalStateException(
								"No two nodes on the same level must contain the same hierarchy element.");
					});
		}

		public void addNodeForHierarchyElement(MutableTreeNode<C> node, H hierarchyElement) {
			nodeToHierarchyElement.put(node, hierarchyElement);
		}

	}

}
