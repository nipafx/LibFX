/**
 * Provides functionality around <a href="http://en.wikipedia.org/wiki/Tree_%28data_structure%29">tree</a>-like
 * data structures.
 * <ul>
 * <li>To construct trees, have a look at {@link org.codefx.libfx.collection.tree.TreeNode TreeNode} and its
 * implementations.
 * <li>{@link org.codefx.libfx.collection.tree.navigate.TreeNavigator TreeNavigators} provide a uniform way to navigate
 * {@code TreeNode}-trees as well as other tree-like data structures.
 * <li>Such navigators or, more generally, any implementation of a
 * {@link org.codefx.libfx.collection.tree.stream.TreeIterationStrategy TreeIterationStrategy} can be used to stream
 * the nodes of a tree with {@link org.codefx.libfx.collection.tree.stream.TreeStreams TreeStreams}.
 * </ul>
 */
package org.codefx.libfx.collection.tree;