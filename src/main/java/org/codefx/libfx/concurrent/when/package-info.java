/**
 * Allows to make sure some action which is triggered by a change on an {@link javafx.beans.value.ObservableValue
 * ObservableValue} gets executed under threading.
 * <p>
 * Refer to the two classes {@link org.codefx.libfx.concurrent.when.ExecuteOnceWhen ExecuteOnceWhen} and
 * {@link org.codefx.libfx.concurrent.when.ExecuteAlwaysWhen ExecuteAlwaysWhen} for a detailed description. Instances of
 * those classes can be built with {@link org.codefx.libfx.concurrent.when.ExecuteWhen ExecuteWhen}.
 *
 * @see org.codefx.libfx.concurrent.when.ExecuteWhen ExecuteWhen
 */
package org.codefx.libfx.concurrent.when;