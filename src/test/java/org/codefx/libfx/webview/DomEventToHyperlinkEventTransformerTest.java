package org.codefx.libfx.webview;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventTarget;
import org.xml.sax.InputSource;

public class DomEventToHyperlinkEventTransformerTest {

	private static final String LINK_ID = "the_link";

	private static final String htmlString = ""
			+ "<a"
			+ "    id=\"" + LINK_ID + "\""
			+ "    href=\"http://www.w3.org/TR/DOM-Level-3-Events/#event-types-list\">"
			+ "  link"
			+ "</a>";

	private Document htmlDocument;

	@Before
	public void setUp() throws Exception {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setValidating(false);
		builderFactory.setNamespaceAware(true);
		builderFactory.setIgnoringComments(false);
		builderFactory.setIgnoringElementContentWhitespace(false);
		builderFactory.setExpandEntityReferences(false);
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		htmlDocument = builder.parse(new InputSource(new StringReader(htmlString)));
	}

	@Test
	public void testCanTransform() throws Exception {
		EventTarget element = (EventTarget) htmlDocument.getElementById(LINK_ID);
		element.dispatchEvent(null);
	}

}
