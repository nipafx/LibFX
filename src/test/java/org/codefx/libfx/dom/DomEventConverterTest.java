package org.codefx.libfx.dom;

import javax.swing.event.HyperlinkEvent;

import org.w3c.dom.events.Event;

/**
 * Test the class {@link DomEventConverter}.
 */
public class DomEventConverterTest extends AbstractDomEventConverterTest {

	// #begin FIELDS & INITIALIZATION

	/**
	 * The tested {@link DomEventConverter}.
	 */
	private DomEventConverter converter;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		converter = new DomEventConverter();
	}

	// #end FIELDS & INITIALIZATION

	@Override
	protected boolean canConvertToHyperlinkEvent(Event domEvent) {
		return converter.canConvertToHyperlinkEvent(domEvent);
	}

	@Override
	protected HyperlinkEvent convertToHyperlinkEvent(Event domEvent, Object source) {
		return converter.convertToHyperlinkEvent(domEvent, source);
	}

}
