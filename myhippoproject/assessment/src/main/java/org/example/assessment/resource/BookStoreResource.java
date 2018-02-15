package org.example.assessment.resource;

import org.example.assessment.model.Book;
import org.example.assessment.model.BookStore;
import org.example.assessment.util.BookUtil;
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
import javax.ws.rs.Produces;
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


    public BookStoreResource(Session systemSession) {
        this.systemSession = systemSession;
        this.bookStore = new BookStore();

        //initially load books to store
        updateBooks();
        try {
            // add event listener to listen changes of books from console
            this.systemSession.getWorkspace().getObservationManager().addEventListener(new EventListener() {
                public void onEvent(EventIterator events) {
                    log.info("Books changed from console");
                    //update book store
                    updateBooks();
                }
            }, Event.NODE_ADDED | Event.NODE_MOVED | Event.NODE_REMOVED |
                    Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED | Event.PERSIST, "/content/documents/myhippoproject/books", true, null, null, false);
        } catch (RepositoryException e) {
            log.error("Error adding event listener");
        }

    }

    /**
     * Lists books in book store object
     *
     * @return
     */
    @Path("/")
    @Produces({"application/json"})
    @GET
    public Map<String, Book> listBooks() {
        log.info("Book store list service called");
        return bookStore.getBooks();
    }


    /**
     * Update books in book store object
     */
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
                    books.put(bookNode.getName(), BookUtil.convertBook(bookNode));
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
