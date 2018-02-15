package org.example.assessment.resource;

import org.example.assessment.model.Book;
import org.example.assessment.model.BookStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author silay
 */
public class BookStoreResource {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final Session systemSession;

    private BookStore bookStore;

    private Book convertBook(Node bookNode) throws RepositoryException {
        Book book = new Book();

        book.setName(bookNode.getProperty("name").getString());
        book.setAuthor(bookNode.getProperty("author").getString());
        book.setIntroduction(bookNode.getProperty("introduction").getString());
        book.setISBN(bookNode.getProperty("ISBN").getString());
        //book.setParagraphs(bookNode.getProperty("paragraphs").getValues());
        return book;

    }

    public BookStoreResource(Session systemSession) {
        this.systemSession = systemSession;

        try {
            this.systemSession.getWorkspace().getObservationManager().addEventListener(new EventListener() {
                public void onEvent(EventIterator events) {
                    updateBooks();
                }
            }, Event.NODE_ADDED | Event.NODE_MOVED | Event.NODE_REMOVED |
                    Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED | Event.PERSIST, "/books", true, null, null, false);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

    }

    @Path("/")
    @GET
    public Map<String, Book> listBooks() {
        return bookStore.getBooks();
    }


    public void updateBooks() {
        try {
            String path = "content/documents/myhippoproject/books";
            Map<String, Book> books = new HashMap<>();
            if (!systemSession.getRootNode().hasNode(path)) {
                bookStore.setBooks(null);
                return;
            }
            Node booksNode = systemSession.getRootNode().getNode(path);
            NodeIterator nodeIterator = booksNode.getNodes();

            while (nodeIterator.hasNext()) {
                try {
                    Node bookNode = nodeIterator.nextNode();
                    books.put(bookNode.getName(), convertBook(bookNode));
                } catch (RepositoryException e) {
                    log.error("Error converting book node to pojo", e);
                }
            }
            bookStore.setBooks(books);
        } catch (RepositoryException e) {
            log.error("Error converting book node to pojo", e);
        }
    }
}
