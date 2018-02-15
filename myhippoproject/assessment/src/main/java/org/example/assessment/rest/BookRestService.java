package org.example.assessment.rest;

import org.example.assessment.data.BookDataService;
import org.example.assessment.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.ws.rs.*;
import java.util.List;

/**
 * @author silay
 */
public class BookRestService {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private BookDataService bookDataService;

    public BookRestService(BookDataService bookDataService) throws RepositoryException {

        this.bookDataService = bookDataService;

    }

    /**
     * lists books in repository
     *
     * @return list of books
     */
    @Path("/")
    @Produces({"application/json"})
    @GET
    public List<Book> listBooks() {
        log.info("List books service is called");
        return bookDataService.fetchBooks();
    }

    /**
     * Searches books in repository containing text
     *
     * @param text
     * @return list of books
     */
    @Path("/search")
    @Produces({"application/json"})
    @GET
    public List<Book> searchBooks(@QueryParam("text") String text) {
        log.info("Search books service is called");
        return bookDataService.queryBooks(text);
    }

    /**
     * Adds books to repository
     *
     * @param books
     * @return true/false
     */
    @Path("/")
    @Consumes({"application/json"})
    @POST
    public boolean addBooks(List<Book> books) {
        log.info("Add books service is called");
        return bookDataService.saveBooks(books);
    }
}
