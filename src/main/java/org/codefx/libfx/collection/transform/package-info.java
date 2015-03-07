/**
 * <p>
 * This package contains transforming collections.
 * </p>
 * <h2>Overview</h2>
 * <p>
 * A transforming collection is essentially a decorator of an existing collection which transforms the collection's
 * elements from one type to another. The decorated collection is usually referred to as the <i>inner collection</i> and
 * it's generic type accordingly as the <i>inner type</i>. The transforming collection and its generic type are referred
 * to as <i>outer collection</i> and <i>outer type</i>, respectively.
 * </p>
 * <h2>Similar Features From Other Libraries</h2>
 * <p>
 * To my (nipa's) knowledge two other libraries offer similar functionality, namely <a
 * href="http://commons.apache.org/proper/commons-collections/">Apache Commons Collections</a> and <a
 * href="https://github.com/google/guava">Google Guava</a>. Both have shortcomings which this implementation aims to
 * overcome.
 * </p>
 * <h3>Apache Commons Collections</h3>
 * <p>
 * Commons provides the <a href=
 * "https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/collection/TransformedCollection.html"
 * > {@code TransformedCollection}</a>. It only affects add methods,
 * "thus objects must be removed or searched for using their transformed form." The implementations in this package
 * suffers from no such limitation.
 * </p>
 * <h3>Google Guava</h3>
 * <p>
 * Guava contains a utility method <a href=
 * "http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/collect/Lists.html#transform%28java.util.List,%20com.google.common.base.Function%29"
 * >{@code transform(List, Function)}</a> which returns a transformed view of the specified map. But
 * "the transform is one-way and new items cannot be stored in the returned list." The implementations in this package
 * explicitly allow editing both instances.
 * </p>
 * <h1>TODO</h1>
 * <ul>
 * <li>Mark transformed list with <a
 * href="http://docs.oracle.com/javase/7/docs/api/java/util/RandomAccess.html?is-external=true">RandomAccess</a>.
 * <li>document sorting details
 * <li>document concurrency details
 * <li>document used default methods
 * <li>document implementation of bulk operations
 * <li>document how functions are used to transform both ways and that (a) they must be inverse to each other and (b)
 * this means that the outer elements have no meaningful identity
 * </ul>
 */
package org.codefx.libfx.collection.transform;