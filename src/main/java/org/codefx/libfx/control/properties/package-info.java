/**
 * Makes using a {@link javafx.scene.control.Control Control}'s {@link javafx.scene.control.Control#getProperties()
 * propertyMap} more convenient.
 * <p>
 * As such its main use will be to creators of controls.
 * </p>
 * <h2>Listening to the Property Map</h2>
 * <p>
 * In order to use the property map, a control has to create a listener which does these things:
 * <ul>
 * <li>identify the correct key
 * <li>check whether the value is of the correct type and cast it
 * <li>process the value
 * <li>remove the value so when the same value is added again, the listener notices that
 * </ul>
 * While implementing such a listener is not difficult, some details have to be considered. This makes the code a little
 * lengthy and hinders readability while at the same time repeating the same pattern over and over.
 * <h2>ControlPropertyListener</h2>
 * <p>
 * This package provides usability functions to create such a listener in a concise and readable way (this code would be
 * inside a control):
 *
 * <pre>
 * ControlProperties.on(getProperties())
 * 	.forKey("SomeKey")
 * 	.processValue(valueString -&gt; System.out.println(valueString))
 * 	.buildAndAttach();
 * </pre>
 * It returns an instance of {@link org.codefx.libfx.control.properties.ControlPropertyListenerHandle
 * ControlPropertyListenerHandle} which can be used to easily detach and reattach the listener.
 *
 * @see org.codefx.libfx.control.properties.ControlPropertyListenerHandle ControlPropertyListener
 * @see org.codefx.libfx.control.properties.ControlPropertyListenerBuilder ControlPropertyListenerBuilder
 */
package org.codefx.libfx.control.properties;

