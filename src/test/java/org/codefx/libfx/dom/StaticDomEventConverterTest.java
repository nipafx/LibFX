package org.codefx.libfx.dom;

import javax.swing.event.HyperlinkEvent;

import org.w3c.dom.events.Event;

/**
 * Test the class {@link StaticDomEventConverter}.
 */
public class StaticDomEventConverterTest extends AbstractDomEventConverterTest {

	@Override
	protected boolean canConvertToHyperlinkEvent(Event domEvent) {
		return StaticDomEventConverter.canConvertToHyperlinkEvent(domEvent);
	}

	@Override
	protected HyperlinkEvent convertToHyperlinkEvent(Event domEvent, Object source) {
		return StaticDomEventConverter.convertToHyperlinkEvent(domEvent, source);
	}

}
