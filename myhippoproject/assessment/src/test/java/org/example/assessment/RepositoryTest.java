package org.example.assessment;

import javax.jcr.*;
import javax.xml.bind.JAXBException;

import org.example.assessment.helper.NodePrinter;
import org.hippoecm.repository.HippoRepositoryFactory;
import org.hippoecm.repository.WebCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onehippo.repository.mock.MockNodeFactory;
import org.onehippo.repository.testutils.RepositoryTestCase;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RepositoryTest extends RepositoryTestCase {

    @Test
    public void test_minimal() throws RepositoryException {
        Node rootNode = session.getRootNode();
        rootNode.addNode("test");
        rootNode.addNode("books");
        session.save();
        assertTrue(session.nodeExists("/test"));
        assertTrue(session.nodeExists("/books"));
    }

    @BeforeClass

    public static void setUpClass() throws Exception {
        background = HippoRepositoryFactory.getHippoRepository("storage");
    }
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp(false);

    }

    @After
    @Override
    public void tearDown() throws Exception {
        removeNode("/books");
        super.tearDown(); // removes /test node and checks repository clean state
    }


    @Test
    public void testTraverseQueries() throws RepositoryException {
        session.getRepository().login(new SimpleCredentials("admin","admin".toCharArray()));

        // can read session data
         Node queriesNode = session.getNode("/hippo:configuration/hippo:queries");

        assertNotNull(queriesNode);

        traverse(queriesNode);
    }


    @Test
    public void testTraverseMockQueries() throws RepositoryException {

        // Create mock nodes queries
        Node queriesNode = null;
        try {
            queriesNode = MockNodeFactory.fromXml("/org.example.assessment/hippo_queries.xml");
        } catch (IOException e) {
            log.error("Error reading mock node file",e);
        } catch (JAXBException e) {
            log.error("Error parsing mock node file",e);
        }

        assertNotNull(queriesNode);

        traverse(queriesNode);
    }

    /**
     * Traverses and prints nodes
     *
     * @param node
     */
    private void traverse(Node node) {


        // print path, type and value(s) of node
        NodePrinter.printNode(node);

        try {
            log.info("Getting subnodes of node : %s", node.getName());
            //get sub nodes
            NodeIterator nodeIterator = node.getNodes();

            while (nodeIterator.hasNext()) {
                traverse(nodeIterator.nextNode());
            }
        } catch (RepositoryException e) {
            log.error("Error getting sub nodes ", e);
        }


    }




}
