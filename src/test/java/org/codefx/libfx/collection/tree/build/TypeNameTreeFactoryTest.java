package org.codefx.libfx.collection.tree.build;

import static org.codefx.libfx.collection.tree.build.HierarchicalTreeFactoryTest.assertEquals;

import org.codefx.libfx.collection.tree.MutableTreeNode;
import org.codefx.libfx.collection.tree.TreeTestHelper.Node;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Tests {@link TypeNameTreeFactory}.
 * <p>
 * Most of the functionality is tested by {@link HierarchicalTreeFactoryTest}, so this class only runs some sanity
 * checks.
 */
public class TypeNameTreeFactoryTest {

	private TypeNameTreeFactory<String> factory;

	@Before
	public void setUp() {
		factory = new TypeNameTreeFactory<>(TypeNameTreeFactory.nameElementsAsNodeContent());
	}

	@Test(expected = NullPointerException.class)
	public void create_toHierarchyNull_throwsNullPointerException() {
		new TypeNameTreeFactory<>(null);
	}

	@Test
	public void createForest_manyElements() {
		List<MutableTreeNode<String>> forest = factory.createForest(
				"java.lang.Object", "java.lang.String", "java.lang.Integer",
				"javax.xml.validation.Schema", "javax.xml.validation.SchemaFactory",
				"javax.xml.transform.Templates", "javax.xml.transform.Transform", "javax.xml.transform.Transformer"
		);

		Node java =
				Node.node("java",
						Node.node("lang",
								Node.node("Object"),
								Node.node("String"),
								Node.node("Integer")));
		Node javax =
				Node.node("javax",
						Node.node("xml",
								Node.node("validation",
										Node.node("Schema"),
										Node.node("SchemaFactory")),
								Node.node("transform",
										Node.node("Templates"),
										Node.node("Transform"),
										Node.node("Transformer"))));
		assertEquals(forest, java, javax);
	}

}