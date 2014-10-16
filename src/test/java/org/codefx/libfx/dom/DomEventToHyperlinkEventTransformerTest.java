package org.codefx.libfx.dom;

import javax.swing.event.HyperlinkEvent;

import org.w3c.dom.events.Event;

/**
 * Test the class {@link DefaultEventTransformer}.
 */
public class DomEventToHyperlinkEventTransformerTest extends AbstractEventTransformerTest {

	@Override
	protected boolean canTransformToHyperlinkEvent(Event domEvent) {
		Object source = "no source needed";
		DomEventToHyperlinkEventTransformer transformer = new DomEventToHyperlinkEventTransformer(domEvent, source);
		return transformer.canTransform();
	}

	@Override
	protected HyperlinkEvent transformToHyperlinkEvent(Event domEvent, Object source) {
		DomEventToHyperlinkEventTransformer transformer = new DomEventToHyperlinkEventTransformer(domEvent, source);
		return transformer.transform();
	}

}
