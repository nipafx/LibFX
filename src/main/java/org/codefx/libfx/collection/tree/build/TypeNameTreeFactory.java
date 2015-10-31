package org.codefx.libfx.collection.tree.build;

import static java.util.Objects.requireNonNull;

import org.codefx.libfx.collection.tree.MutableTreeNode;
import org.codefx.libfx.collection.tree.build.HierarchicalTreeFactory.ToContentFunction;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Creates a tree from fully qualified dotted type names, in which the nodes correspond to (simple) package, class and
 * interface names.
 *
 * @param <C>
 * 		the type of content carried by the nodes in the resulting tree
 */
public class TypeNameTreeFactory<C> {

	private final HierarchicalTreeFactory<String, String, C> factory;

	/**
	 * Creates a new factory.
	 *
	 * @param toContent
	 * 		computes a tree node's content from a given type name and element index
	 */
	public TypeNameTreeFactory(ToContentFunction<String, C> toContent) {
		requireNonNull(toContent, "The argument 'toContent' must not be null.");
		factory = new HierarchicalTreeFactory<>(TypeNameTreeFactory::splitTypeName, toContent);
	}

	private static List<String> splitTypeName(String typeName) {
		String[] typeNameElements = typeName.split("\\.");
		return Arrays.asList(typeNameElements);
	}

	/**
	 * Returns a function that uses the type name element for the content of the corresponding node.
	 * <p>
	 * It can be used to create a factory like so:
	 * <pre>
	 * {@code new TypeNameTreeFactory<>(TypeNameTreeFactory.nameElementsAsNodeContent());}
	 * </pre>
	 */
	public static ToContentFunction<String, String> nameElementsAsNodeContent() {
		return (typeName, nameElementIndex, existingNodeContent) ->
				existingNodeContent.orElseGet(() -> typeName.split("\\.")[nameElementIndex]);
	}

	/**
	 * Creates a forest for the specified type names.
	 *
	 * @param typeNames
	 * 		a variable number of type names
	 *
	 * @return a list of the tree roots created from the {@code typeNames}
	 */
	public final List<MutableTreeNode<C>> createForest(String... typeNames) {
		return factory.createForest(typeNames);
	}

	/**
	 * Creates a forest for the specified type names.
	 *
	 * @param typeNames
	 * 		an iterable of type names
	 *
	 * @return a list of the tree roots created from the {@code typeNames}
	 */
	public List<MutableTreeNode<C>> createForest(Iterable<String> typeNames) {
		return factory.createForest(typeNames);
	}

	/**
	 * Creates a forest for the specified type names.
	 *
	 * @param typeNames
	 * 		a stream of type names
	 *
	 * @return a list of the tree roots created from the {@code typeNames}
	 */
	public List<MutableTreeNode<C>> createForest(Stream<String> typeNames) {
		return factory.createForest(typeNames);
	}

}
