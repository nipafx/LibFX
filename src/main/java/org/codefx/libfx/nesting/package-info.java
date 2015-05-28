/**
 * <p>
 * Provides functionality around nesting hierarchies - a term which is explained in all detail in the comment on
 * {@link org.codefx.libfx.nesting.Nesting Nesting}.
 * <h2>Nesting</h2> A {@code Nesting} encapsulates a hierarchy of nested {@code ObservableValues} and collapses them
 * into a property which always contains the current innermost {@code Observable} in that hierarchy. A {@code Nesting}
 * can be used as a basic building block for other nested functionality (see below).
 * <p>
 * See the comment on {@link org.codefx.libfx.nesting.Nesting Nesting} for details.
 * <h2>Nested Property</h2> A {@code NestedProperty} uses a {@code Nesting} to bind its value to the inner
 * {@code Property} in a nesting hierarchy, updating the binding as the inner observable changes its value or is
 * replaced. It can thus be used to collapse a nesting hierarchy into a single property.
 * <p>
 * See the comment on {@link org.codefx.libfx.nesting.property.NestedProperty NestedProperty} for details.
 * <h2>Nested Listeners</h2> A {@code Nesting} can also be used to add listeners to its inner observable. These
 * listeners are moved from one observable to the next as they are replaced.
 * <p>
 * See the comments on {@link org.codefx.libfx.nesting.listener.NestedChangeListenerHandle NestedChangeListener} and
 * {@link org.codefx.libfx.nesting.listener.NestedInvalidationListenerHandle NestedInvalidationListener} for details.
 * <h2>Builders</h2> Instances of the classes described above can be build by starting with the methods in
 * {@link org.codefx.libfx.nesting.Nestings Nestings}.
 *
 * @see org.codefx.libfx.nesting.Nesting Nesting
 * @see org.codefx.libfx.nesting.property.NestedProperty NestedProperty
 * @see org.codefx.libfx.nesting.listener.NestedChangeListenerHandle NestedChangeListener
 * @see org.codefx.libfx.nesting.listener.NestedInvalidationListenerHandle NestedInvalidationListener
 */
package org.codefx.libfx.nesting;

