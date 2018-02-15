package org.example.assessment.util;

import org.example.assessment.model.Book;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * @author silay
 */
public class BookUtil {

    /**
     * Converts book node to pojo
     *
     * @param bookNode
     * @return book pojo
     * @throws RepositoryException
     */
    public static Book convertBook(Node bookNode) throws RepositoryException {
        Book book = new Book();

        book.setName(bookNode.getProperty("name").getString());
        book.setAuthor(bookNode.getProperty("author").getString());
        book.setIntroduction(bookNode.getProperty("introduction").getString());
        book.setISBN(bookNode.getProperty("ISBN").getString());
        book.setParagraphs(bookNode.getProperty("paragraphs").getString());
        return book;

    }
}
