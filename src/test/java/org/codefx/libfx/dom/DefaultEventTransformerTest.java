package org.codefx.libfx.dom;

import javax.swing.event.HyperlinkEvent;

import org.w3c.dom.events.Event;

/**
 * Test the class {@link DefaultEventTransformer}.
 */
public class DefaultEventTransformerTest extends AbstractEventTransformerTest {

	// #region FIELDS & INITIALIZATION

	/**
	 * The tested {@link DefaultEventTransformer}.
	 */
	private DefaultEventTransformer transformer;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		transformer = new DefaultEventTransformer();
	}

	// #end FIELDS & INITIALIZATION

	@Override
	protected boolean canTransformToHyperlinkEvent(Event domEvent) {
		return transformer.canTransformToHyperlinkEvent(domEvent);
	}

	@Override
	protected HyperlinkEvent transformToHyperlinkEvent(Event domEvent, Object source) {
		return transformer.transformToHyperlinkEvent(domEvent, source);
	}

}
