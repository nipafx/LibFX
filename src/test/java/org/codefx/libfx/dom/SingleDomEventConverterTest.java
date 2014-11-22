package org.codefx.libfx.dom;

import javax.swing.event.HyperlinkEvent;

import org.w3c.dom.events.Event;

/**
 * Test the class {@link SingleDomEventConverter}.
 */
public class SingleDomEventConverterTest extends AbstractDomEventConverterTest {

	@Override
	protected boolean canConvertToHyperlinkEvent(Event domEvent) {
		Object source = "no source needed";
		SingleDomEventConverter converter = new SingleDomEventConverter(domEvent, source);
		return converter.canConvert();
	}

	@Override
	protected HyperlinkEvent convertToHyperlinkEvent(Event domEvent, Object source) {
		SingleDomEventConverter converter = new SingleDomEventConverter(domEvent, source);
		return converter.convert();
	}

}
