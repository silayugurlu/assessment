package org.example.assessment.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;

public class NodePrinter {

    protected static final Logger log = LoggerFactory.getLogger(NodePrinter.class);


    /**
     * Prints path, type and values of a node
     * @param node
     */
    public static void printNode(Node node) {
        try {
            printPath(node);
            //printType(node);
            printValues(node);
        } catch (RepositoryException e) {
            log.error("Error reading node values",e);
        }
    }

    /**
     * Prints primary type of node
     * @param node
     * @throws RepositoryException
     */
    public static void printType(Node node) throws RepositoryException {
        printWhitespaces(node.getDepth() + 1);
        System.out.print("jcr:primaryType : " + node.getPrimaryNodeType().getName());
        System.out.println();
    }

    /**
     * Prints path of node
     * @param node
     * @throws RepositoryException
     */
    public static void printPath(Node node) throws RepositoryException {
        printWhitespaces(node.getDepth());
        System.out.print(node.getPath());
        System.out.println();
    }

    /**
     * Prints value(s) of node
     * @param node
     * @throws RepositoryException
     */
    public static void printValues(Node node) throws RepositoryException {
        PropertyIterator propertyIterator = node.getProperties();
        while (propertyIterator.hasNext()) {

            printWhitespaces(node.getDepth() + 1);
            try {
                printProperty(propertyIterator);
            } catch (RepositoryException e) {
                log.error("Error getting value of property",e);
            }
            System.out.println();
        }
    }

    /**
     * Prints property(s) of node
     *
     * @param propertyIterator
     * @throws RepositoryException
     */
    public static void printProperty(PropertyIterator propertyIterator) throws RepositoryException {
        Property property = propertyIterator.nextProperty();
        if (property.isMultiple()) {
            System.out.print(property.getName() + " : [");
            for (int i = 0; i < property.getValues().length; i++) {
                Value value = property.getValues()[i];
                System.out.print(value.getString());
                if (i != property.getValues().length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.print("]");
        } else {
            System.out.print(property.getName() + " : " + property.getString());
        }
    }



    public static void printWhitespaces(int count) {
        for (int i = 0; i < count; i++)
            System.out.print(" ");
    }
}
