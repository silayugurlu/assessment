package org.example.assessment.model;

import java.util.Map;
/**
 * @author silay
 */
public class BookStore {

    private Map<String,Book> books;

    public Map<String, Book> getBooks() {
        return books;
    }

    public void setBooks(Map<String, Book> books) {
        this.books = books;
    }
}
