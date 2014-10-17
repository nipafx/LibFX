/**
 * This package provides functionality around DOM, i.e. classes from {@code org.w3c.dom}.
 * <p>
 * <h2>Event Transformation</h2> The interface {@link org.codefx.libfx.dom.EventTransformer EventTransformer} defines
 * methods which allow the transformation of {@link org.w3c.dom.events.Event DOM Events} to
 * {@link javax.swing.event.HyperlinkEvent HyperlinkEvents}.
 * <p>
 * {@link org.codefx.libfx.dom.DefaultEventTransformer DefaultEventTransformer} is the only implementation but
 * {@link org.codefx.libfx.dom.StaticEventTransformer StaticEventTransformer} gives access to the same methods without
 * the need of instantiation.
 */
package org.codefx.libfx.dom;

