package org.example.assessment.resource;

import org.example.assessment.model.Book;
import org.example.assessment.model.Chapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * @author silay
 */
public class BookResource {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final Session systemSession;

    public BookResource(Session systemSession) {
        this.systemSession = systemSession;

    }


    @Path("/")
    @Produces({"application/json"})
    @GET
    public List<Book> listBooks() {

        return fetchBooks();
    }

    @Path("/search")
    @Produces({"application/json"})
    @GET
    public List<Book> searchBooks(@QueryParam("text") String text) {

        return queryBooks(text);
    }

    @Path("/")
    @Consumes({"application/json"})
    @POST
    public void addBooks( List<Book> books) {
        saveBooks(books);
    }

    private void saveBooks(List<Book> books) {

        try {

            // gets root node from repository, checks if books content exist, if not adds books node to the root
            Node booksNode = null;
            String path = "content/documents/myhippoproject/books";
            //String path = "books";
            if (systemSession.getRootNode().hasNode(path)) {
                booksNode = systemSession.getRootNode().getNode(path);
            } else {
                booksNode = systemSession.getRootNode().addNode(path);
            }

            // sets books node type as nt:folder
            booksNode.setPrimaryType(NodeType.NT_FOLDER);

            //iterate over books and add child node for each of them
            for (Book book : books) {
                Node bookNode = booksNode.addNode(book.getName(), NodeType.NT_UNSTRUCTURED);
                bookNode.setProperty("name", book.getName());
                bookNode.setProperty("author", book.getAuthor());
                bookNode.setProperty("introduction", book.getIntroduction());
                bookNode.setProperty("ISBN", book.getISBN());
                bookNode.setProperty("paragraphs", book.getParagraphs());
            }

            systemSession.save();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts book node to pojo
     *
     * @param bookNode
     * @return book pojo
     * @throws RepositoryException
     */
    private Book convertBook(Node bookNode) throws RepositoryException {
        Book book = new Book();

        book.setName(bookNode.getProperty("name").getString());
        book.setAuthor(bookNode.getProperty("author").getString());
        book.setIntroduction(bookNode.getProperty("introduction").getString());
        book.setISBN(bookNode.getProperty("ISBN").getString());
        //book.setParagraphs(bookNode.getProperty("paragraphs").getValues());
        return book;

    }

    /**
     * Searches books containing text
     *
     * @param text
     * @return list of books
     */
    private List<Book> queryBooks(String text) {

        log.info("Searching books contains {}", text);
        List<Book> books = new ArrayList<>();

        try {
            QueryManager manager = systemSession.getWorkspace().getQueryManager();

            //Query repository for the books containing text
            Query query = manager.createQuery("//element(*, books)[jcr:contains(.,'" + text + "')]", Query.XPATH);

            QueryResult r = query.execute();
            for (NodeIterator nodeIterator = r.getNodes(); nodeIterator.hasNext(); ) {
                try {
                    books.add(convertBook(nodeIterator.nextNode()));
                } catch (RepositoryException e) {
                    log.error("Error converting book node to pojo", e);
                }
            }
            return books;

        } catch (RepositoryException e) {
            log.error("Error querying books from repository", e);
        }
        return null;
    }

    /**
     * Fetches books from repository
     *
     * @return list of books
     */
    private List<Book> fetchBooks() {
        try {
            String path = "content/documents/myhippoproject/books";
            List<Book> books = new ArrayList<>();
            if (!systemSession.getRootNode().hasNode(path)) {
                systemSession.getRootNode().addNode(path);
                return books;
            }
            Node booksNode = systemSession.getRootNode().getNode(path);
            NodeIterator nodeIterator = booksNode.getNodes();

            while (nodeIterator.hasNext()) {
                try {
                    books.add(convertBook(nodeIterator.nextNode()));
                } catch (RepositoryException e) {
                    log.error("Error converting book node to pojo", e);
                }
            }
            return books;
        } catch (RepositoryException e) {
            log.error("Error converting book node to pojo", e);
        }
        return null;
    }


}
