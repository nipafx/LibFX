package org.codefx.libfx.dom;

import javax.swing.event.HyperlinkEvent;

import org.w3c.dom.events.Event;

/**
 * Test the class {@link DefaultEventTransformer}.
 */
public class StaticEventTransformerTest extends AbstractEventTransformerTest {

	@Override
	protected boolean canTransformToHyperlinkEvent(Event domEvent) {
		return StaticEventTransformer.canTransformToHyperlinkEvent(domEvent);
	}

	@Override
	protected HyperlinkEvent transformToHyperlinkEvent(Event domEvent, Object source) {
		return StaticEventTransformer.transformToHyperlinkEvent(domEvent, source);
	}

}
