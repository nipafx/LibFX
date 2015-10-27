package org.codefx.libfx.collection.tree.stream;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.codefx.libfx.collection.tree.navigate.TreeNavigator;

/**
 * Creates streams of nodes.
 * <p>
 * Unless otherwise noted all streams are {@link Spliterator#ORDERED ordered} and sequential. They are generated lazily,
 * i.e. the {@link TreeNavigator} is only used to find nodes which are known to be processed by the stream. This is
 * implies that short-circuiting operation (like {@link Stream#limit(long) limit}) will lead to the evaluation of less
 * nodes.
 * <p>
 * The streams are only defined on trees, i.e. connected, directed, acyclic graphs. Creating them on other graphs can
 * lead to unexpected behavior including infinite streams.
 */
public class TreeStreams {

	// #begin DFS

	// forward

	/**
	 * Returns a stream that enumerates nodes in the (sub-)tree rooted in the specified root in the order of a <a
	 * href="https://en.wikipedia.org/wiki/Depth-first_search">depth-first search</a>.
	 * <p>
	 * It is not necessary for the specified node to be the tree's actual root. It will be treated as the root of a
	 * subtree and only this subtree will be streamed.
	 *
	 * @param <N>
	 *            the type of nodes contained in the tree
	 * @param navigator
	 *            the navigator used to navigate the tree
	 * @param root
	 *            the root node for the searched (sub-)tree
	 * @return a stream of nodes
	 */
	public static <N> Stream<N> dfsFromRoot(TreeNavigator<N> navigator, N root) {
		TreePath<TreeNode<N>> initialPath = TreePathFactory.createWithSingleNode(root);
		TreeIterationStrategy<N> strategy = new DfsTreeIterationStrategy<>(navigator, initialPath);
		return byStrategy(strategy);
	}

	/**
	 * Returns a stream that enumerates nodes in a tree in the order of a <a
	 * href="https://en.wikipedia.org/wiki/Depth-first_search">depth-first search</a> that starts in the specified
	 * node.
	 * <p>
	 * While the search will start in the specified node it will eventually backtrack above it, i.e. the search is not
	 * limited to the subtree rooted in the node. This is equivalent to starting a full depth-first search in the tree's
	 * root but ignoring all encountered nodes until the specified start node is found.
	 *
	 * @param <N>
	 *            the type of nodes contained in the tree
	 * @param navigator
	 *            the navigator used to navigate the tree
	 * @param startNode
	 *            the node in which the search starts
	 * @return a stream of nodes
	 */
	public static <N> Stream<N> dfsFromWithin(TreeNavigator<N> navigator, N startNode) {
		TreePath<TreeNode<N>> initialPath = TreePathFactory.createFromRootToNode(navigator, startNode);
		TreeIterationStrategy<N> strategy = new DfsTreeIterationStrategy<>(navigator, initialPath);
		return byStrategy(strategy);
	}

	/**
	 * Returns a stream that enumerates nodes in the (sub-)tree rooted in the specified root in the order of a <a
	 * href="https://en.wikipedia.org/wiki/Depth-first_search">depth-first search</a> that starts in the specified
	 * start node.
	 * <p>
	 * This is a combination of {@link #dfsFromRoot(TreeNavigator, Object) dfsFromRoot} and
	 * {@link #dfsFromWithin(TreeNavigator, Object) dfsFromWithin}. Only the (sub-)tree rooted in the specified root is
	 * searched (i.e. the search will never backtrack above it) but the stream actually starts in the specified start
	 * node.
	 *
	 * @param <N>
	 *            the type of nodes contained in the tree
	 * @param navigator
	 *            the navigator used to navigate the tree
	 * @param root
	 *            the root node for the searched (sub-)tree
	 * @param startNode
	 *            the node in which the search starts
	 * @return a stream of nodes
	 */
	public static <N> Stream<N> dfsFromWithin(TreeNavigator<N> navigator, N root, N startNode) {
		TreePath<TreeNode<N>> initialPath = TreePathFactory.createFromNodeToDescendant(navigator, root, startNode);
		TreeIterationStrategy<N> strategy = new DfsTreeIterationStrategy<>(navigator, initialPath);
		return byStrategy(strategy);
	}

	// backward

	/**
	 * Returns a stream that enumerates nodes in a tree in the order of a <em>backwards</em> <a
	 * href="https://en.wikipedia.org/wiki/Depth-first_search">depth-first search</a> that starts in the specified
	 * node.
	 * <p>
	 * The stream enumerates the nodes which would be encountered by starting a depth-first search in the tree's root
	 * and stopping at the specified node, but in reverse order.
	 *
	 * @param <N>
	 *            the type of nodes contained in the tree
	 * @param navigator
	 *            the navigator used to navigate the tree
	 * @param startNode
	 *            the node in which the search starts
	 * @return a stream of nodes
	 */
	public static <N> Stream<N> backwardDfs(TreeNavigator<N> navigator, N startNode) {
		TreePath<TreeNode<N>> initialPath = TreePathFactory.createFromRootToNode(navigator, startNode);
		TreeIterationStrategy<N> strategy = new DfsTreeIterationStrategy<>(navigator, initialPath);
		return byStrategy(strategy);
	}

	/**
	 * Returns a stream that enumerates nodes in the (sub-)tree rooted in the specified root in the order of a
	 * <em>backwards</em> <a href="https://en.wikipedia.org/wiki/Depth-first_search">depth-first search</a> that starts
	 * in the specified node.
	 * <p>
	 * The stream enumerates the nodes which would be encountered by starting a depth-first search in the specified root
	 * and stopping at the specified node, but in reverse order.
	 *
	 * @param <N>
	 *            the type of nodes contained in the tree
	 * @param navigator
	 *            the navigator used to navigate the tree
	 * @param root
	 *            the root node for the searched (sub-)tree
	 * @param startNode
	 *            the node in which the search starts
	 * @return a stream of nodes
	 */
	public static <N> Stream<N> backwardDfsToRoot(TreeNavigator<N> navigator, N root, N startNode) {
		TreePath<TreeNode<N>> initialPath = TreePathFactory.createFromNodeToDescendant(navigator, root, startNode);
		TreeIterationStrategy<N> strategy = new DfsTreeIterationStrategy<>(navigator, initialPath);
		return byStrategy(strategy);
	}

	// #end DFS

	/**
	 * Returns a stream which enumerates a tree's nodes according to the specified {@link TreeIterationStrategy}.
	 *
	 * @param <N>
	 *            the type of nodes contained in the tree
	 * @param strategy
	 *            the strategy used to enumerate the tree's nodes
	 * @return a stream of nodes
	 */
	public static <N> Stream<N> byStrategy(TreeIterationStrategy<N> strategy) {
		return byStrategy(strategy, Spliterator.NONNULL | Spliterator.ORDERED, false);
	}

	/**
	 * Returns a stream which enumerates a tree's nodes according to the specified {@link TreeIterationStrategy} and
	 * stream characteristics.
	 *
	 * @param <N>
	 *            the type of nodes contained in the tree
	 * @param strategy
	 *            the strategy used to enumerate the tree's nodes
	 * @param characteristics
	 *            the characteristics of the {@link Spliterator} used to create the stream
	 * @param parallel
	 *            if true then the returned stream is a parallel stream; if false the returned stream is a sequential
	 *            stream.
	 * @return a stream of nodes
	 */
	public static <N> Stream<N> byStrategy(TreeIterationStrategy<N> strategy, int characteristics, boolean parallel) {
		Iterator<N> iterator = new TreeIterator<>(strategy);
		Spliterator<N> spliterator = Spliterators.spliteratorUnknownSize(iterator, characteristics);
		return StreamSupport.stream(spliterator, parallel);
	}

}
