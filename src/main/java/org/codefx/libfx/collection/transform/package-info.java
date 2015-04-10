/**
 * <p>
 * This package contains transforming collections.
 * </p>
 * <h2>Overview</h2>
 * <p>
 * A transforming collection is essentially a decorator of an existing collection which transforms the collection's
 * elements from one type to another. Note that all such classes are views! They operate on the wrapped collection and
 * all changes to either of them is reflected in the other.
 * <p>
 * The decorated collection is usually referred to as the <i>inner collection</i> and it's generic type accordingly as
 * the <i>inner type</i>. The transforming collection and its generic type are referred to as <i>outer collection</i>
 * and <i>outer type</i>, respectively.
 * </p>
 * <h2>Details</h2> <h3>Transformation</h3>
 * <p>
 * The transformation is computed with a pair of functions. One is used to transform outer elements to inner elements
 * and another one for the other direction. In the case of a {@link java.util.Map Map} two such pairs exist: one for
 * keys and one for values.
 * <p>
 * The transforming functions must be inverse to each other with regard to {@link java.lang.Object#equals(Object)
 * equals}, i.e. {@code outer.equals(toOuter(toInner(outer))} and {@code inner.equals(toInner(toOuter(inner))} must be
 * true for all {@code outer} and {@code inner} elements. If this is not the case, the collections might behave in an
 * unpredictable manner.
 * <p>
 * Note that the same is not true for identity, i.e. {@code outer == toOuter(toInner(outer))} may be false. It is
 * explicitly allowed to create new outer elements from inner elements and vice versa. This means that outer elements
 * may have no meaningful identity. E.g. on adding an outer instance {@code outerOrg} it can be transformed to
 * {@code inner} and on access back to {@code outer}. Whether {@code outerOrg == outer} holds, depends on the
 * transformation functions and is generally unspecified - it might never, sometimes or always be true. Transforming
 * collections might give more details on their behavior regarding this.
 * <p>
 * A special case of transformation occurs when the inner and outer type have a type relationship. This can shortcut one
 * of the transformations to the identity, i.e. because an instance of one type is also of the other type the
 * corresponding transformation can simply return the instance itself. This makes the collection "leak" elements of the
 * wrong type. If the suptype does not fully obey the <a
 * href="https://en.wikipedia.org/wiki/Liskov_substitution_principle">Liskov Substitution Principle</a> this can lead to
 * unexpected behavior.
 * </p>
 * <h3>Type Safety</h3>
 * <p>
 * All operations on transforming collections are type safe in the usual static, compile-time way. But since many
 * methods from the collection interfaces allow {@link java.lang.Object Object}s (e.g
 * {@link java.util.Collection#contains(Object) Collection.contains(Object)}) or collections of unknown generic type
 * (e.g {@link java.util.Collection#addAll(java.util.Collection) Collection.addAll(Collection&lt;?&gt;)}) as arguments,
 * this does not cover all cases which can occur at runtime.
 * <p>
 * If one of those methods is called with a type which does not match the transforming collection's outer type the
 * method may throw a {@link java.lang.ClassCastException ClassCastException}. While this is in accordance with the
 * methods' contracts it might still be unexpected.
 * <p>
 * One way to circumvent this to pay close attention when using the collection and ensuring that such calls can not
 * occur (which is often easy). Another way is to write wrappers which catch and silently ignore the exception.
 * </p>
 * <h2>Similar Features From Other Libraries</h2>
 * <p>
 * To my (<a href="http://blog.codefx.org/about-nicolai-parlog/">nipa</a>'s) knowledge two other libraries offer similar
 * functionality, namely <a href="http://commons.apache.org/proper/commons-collections/">Apache Commons Collections</a>
 * and <a href="https://github.com/google/guava">Google Guava</a>. Both have shortcomings which this implementation aims
 * to overcome.
 * </p>
 * <h3>Apache Commons Collections</h3>
 * <p>
 * Commons provides the <a href=
 * "https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/collection/TransformedCollection.html"
 * > {@code TransformedCollection}</a>. It only affects add methods,
 * "thus objects must be removed or searched for using their transformed form." The implementations in this package
 * suffer from no such limitation.
 * </p>
 * <h3>Google Guava</h3>
 * <p>
 * Guava contains a utility method <a href=
 * "http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/collect/Lists.html#transform%28java.util.List,%20com.google.common.base.Function%29"
 * >{@code transform(List, Function)}</a> which returns a transformed view of the specified map. But
 * "the transform is one-way and new items cannot be stored in the returned list." The implementations in this package
 * explicitly allow editing both instances.
 * </p>
 */
package org.codefx.libfx.collection.transform;