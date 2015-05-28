package org.codefx.libfx.collection.tree.navigate;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Tests {@link JComponentHierarchyNavigator}.
 */
public class JComponentHierarchyNavigatorTest extends AbstractTreeNavigatorTest<JComponent> {

	@Override
	protected TreeNavigator<JComponent> createNavigator() {
		return new JComponentHierarchyNavigator();
	}

	@Override
	protected JComponent createSingletonNode() {
		return new JTextArea("A jComponent without parent and children.");
	}

	@Override
	protected JComponent createNodeWithChildren(int nrOfChildren) {
		JPanel panel = new JPanel();
		for (int childIndex = 0; childIndex < nrOfChildren; childIndex++)
			panel.add(new JTextArea("Child #" + childIndex));
		return panel;
	}

	@Override
	protected JComponent getChildOfParent(JComponent parent, int childIndex) {
		return (JTextArea) ((JPanel) parent).getComponent(childIndex);
	}

}
