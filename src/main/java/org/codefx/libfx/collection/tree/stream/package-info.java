/**
 * Provides the possibility to stream the elements of <a
 * href="http://en.wikipedia.org/wiki/Tree_%28data_structure%29">tree</a>-like data structures.
 * <p>
 * Its main purpose is to enable easy creation of {@link java.util.stream.Stream Stream}s of nodes over tree-like data
 * structures without requiring them to implement a specific interface. Instead of utilizing such an interface to
 * navigate the tree, a {@link org.codefx.libfx.collection.tree.navigate.TreeNavigator TreeNavigator} is used.
 * <p>
 * Use {@link org.codefx.libfx.collection.tree.stream.TreeStreams TreeStreams} to create such streams. If the existing
 * iteration strategies (like, e.g., depth-first search) do not suffice, a
 * {@link org.codefx.libfx.collection.tree.stream.TreeIterationStrategy TreeIterationStrategy} can be specified. The
 * strategy might make use of a {@link org.codefx.libfx.collection.tree.navigate.TreeNavigator TreeNavigator}.
 */
package org.codefx.libfx.collection.tree.stream;

