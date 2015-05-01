package org.codefx.libfx.collection.tree.navigate;

import java.awt.Component;
import java.awt.Panel;
import java.awt.TextArea;

/**
 * Tests {@link ComponentHierarchyNavigator}.
 */
public class ComponentHierarchyNavigatorTest extends AbstractTreeNavigatorTest<Component> {

	@Override
	protected TreeNavigator<Component> createNavigator() {
		return new ComponentHierarchyNavigator();
	}

	@Override
	protected Component createSingletonNode() {
		return new TextArea("A component without parent and children.");
	}

	@Override
	protected Component createNodeWithChildren(int nrOfChildren) {
		Panel panel = new Panel();
		for (int childIndex = 0; childIndex < nrOfChildren; childIndex++)
			panel.add(new TextArea("Child #" + childIndex));
		return panel;
	}

	@Override
	protected Component getChildOfParent(Component parent, int childIndex) {
		return ((Panel) parent).getComponent(childIndex);
	}

}
