package org.codefx.libfx.collection.tree.navigate;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

import org.codefx.tarkastus.JavaFXRule;
import org.junit.Rule;

/**
 * Tests {@link SceneGraphNavigator}.
 */
public class SceneGraphNavigatorTest extends AbstractTreeNavigatorTest<Node> {

	/**
	 * Runs all tests in the JavaFX platform thread.
	 */
	@Rule
	public JavaFXRule javaFXRule = new JavaFXRule();

	@Override
	protected TreeNavigator<Node> createNavigator() {
		return new SceneGraphNavigator();
	}

	@Override
	protected Node createSingletonNode() {
		return new TextArea("A node without parents and children.");
	}

	@Override
	protected Node createNodeWithChildren(int nrOfChildren) {
		Group parent = new Group();
		for (int i = 0; i < nrOfChildren; i++)
			parent.getChildren().add(new TextArea("Child #" + nrOfChildren));
		return parent;
	}

	@Override
	protected Node getChildOfParent(Node parent, int childIndex) {
		return ((Group) parent).getChildren().get(childIndex);
	}

}
