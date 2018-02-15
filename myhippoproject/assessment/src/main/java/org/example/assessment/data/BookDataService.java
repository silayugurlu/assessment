package org.example.assessment.data;

import org.example.assessment.model.Book;
import org.example.assessment.util.BookUtil;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author silay
 */
public class BookDataService {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final Session systemSession;

    private static String PATH = "content/documents/myhippoproject/books";

    public BookDataService(Session systemSession) throws RepositoryException {
        this.systemSession = systemSession;
    }


    /**
     * Inserts books to the repository
     * @param books
     * @return
     */
    public boolean saveBooks(List<Book> books) {

        try {

            // gets root node from repository, checks if books content exist, if not adds books node to the root
            Node booksNode = null;

            if (systemSession.getRootNode().hasNode(PATH)) {
                booksNode = systemSession.getRootNode().getNode(PATH);
            } else {
                booksNode = systemSession.getRootNode().addNode(PATH);
            }

            // sets books node type as nt:folder
            booksNode.setPrimaryType(NodeType.NT_UNSTRUCTURED);

            //iterate over books and add child node for each of them
            for (Book book : books) {
                Node bookNode = booksNode.addNode(book.getName());
                bookNode.setProperty("name", book.getName());
                bookNode.setProperty("author", book.getAuthor());
                bookNode.setProperty("introduction", book.getIntroduction());
                bookNode.setProperty("ISBN", book.getISBN());
                bookNode.setProperty("paragraphs", book.getParagraphs());
            }

            systemSession.save();
            return true;
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return false;
    }



    /**
     * Searches books containing text
     *
     * @param text
     * @return list of books
     */
    public List<Book> queryBooks(String text) {

        log.info("Searching books contains {}", text);
        List<Book> books = new ArrayList<>();

        try {
            QueryManager manager = systemSession.getWorkspace().getQueryManager();

            //Query repository for the books containing text
            Query query = manager.createQuery("//*[@paragraphs='"+text+"']", Query.XPATH);

            QueryResult r = query.execute();
            for (NodeIterator nodeIterator = r.getNodes(); nodeIterator.hasNext(); ) {
                try {
                    books.add(BookUtil.convertBook(nodeIterator.nextNode()));
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
    public List<Book> fetchBooks() {

        try {
            List<Book> books = new ArrayList<>();
            if (!systemSession.getRootNode().hasNode(PATH)) {
                log.info("No book found in repository");
                systemSession.getRootNode().addNode(PATH);
                return books;
            }
            Node booksNode = systemSession.getRootNode().getNode(PATH);
            NodeIterator nodeIterator = booksNode.getNodes();

            while (nodeIterator.hasNext()) {
                try {
                    books.add(BookUtil.convertBook(nodeIterator.nextNode()));
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
