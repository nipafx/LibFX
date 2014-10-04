/**
 * <p>
 * This package provides functionality to make using a {@link javafx.scene.control.Control Control}'s
 * {@link javafx.scene.control.Control#getProperties() propertyMap} easier. As such it's main use will be to creators of
 * controls.
 * <p>
 * <h2>Property Map</h2> Quick introduction to the property map.
 * <p>
 * <h3>Model View Controler</h3> A control should always be split into three classes:
 * <ul>
 * <li>the control, which handles the state (the model in MVC)
 * <li>the skin, which handles the layout (the view in MVC)
 * <li>the behavior, which handles how the control reacts to user input (the controller in MVC)
 * </ul>
 * <p>
 * <h3>Interaction in MVC</h3> As such, the behavior and skin interact with the control as any other class would. This
 * means that they have no privilege when it comes to manipulating its state. But sometimes the control's state has to
 * reflect something that depends on one of these classes and which users of the control should be able to observe but
 * not influence.
 * <p>
 * (Think about a status flag that is true as long as there is an ongoing UI interaction with the control like dragging
 * something across it. This must be set by the behavior but should be read-only to the user of the control.)
 * <p>
 * <h3>Property Map</h3> For such occasions, a control's property map provides a "back door" into the control. It is
 * nothing special and can still be used by any other class. But a control does typically not document this interface
 * and it should be regarded as unofficial and mutable.
 * <p>
 * The property map is an {@link javafx.collections.ObservableMap ObservableMap (of Object, Object)} and can be edited
 * like this:
 *
 * <pre>
 * Button button = new Button();
 * button.getProperties().put("SomeKey", "SomeValue");
 * </pre>
 * It is then up to the control to listen to those changes and do something with them. While implementing such a
 * listener is not difficult, some details have to be considered. This makes the code a little lengthy and hinders
 * readability.
 * <p>
 * <h2>ControlPropertyListener</h2> This package provides usability functions to create such a listener in a concise and
 * readable way (this code would be inside a control):
 *
 * <pre>
 * ControlProperties.on(getProperties())
 * 	.forKey("SomeKey")
 * 	.processValue(valueString -> System.out.println(valueString))
 * 	.buildAndAttach();
 * </pre>
 * It returns an instance of {@link org.codefx.libfx.control.ControlPropertyListener ControlPropertyListener} which can
 * be used to easily detach and reattach the listener.
 *
 * @see org.codefx.libfx.control.ControlPropertyListener ControlPropertyListener
 * @see org.codefx.libfx.control.ControlPropertyListenerBuilder ControlPropertyListenerBuilder
 */
package org.codefx.libfx.control;

