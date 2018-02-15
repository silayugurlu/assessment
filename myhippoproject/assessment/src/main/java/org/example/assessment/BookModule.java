package org.example.assessment;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.example.assessment.data.BookDataService;
import org.example.assessment.resource.BookStoreResource;
import org.example.assessment.rest.BookRestService;
import org.onehippo.repository.jaxrs.RepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsService;
import org.onehippo.repository.modules.DaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * @author silay
 */
public class BookModule implements DaemonModule {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void initialize(final Session systemSession) throws RepositoryException {
        RepositoryJaxrsService.addEndpoint(
                new RepositoryJaxrsEndpoint("/books")
                        .singleton(new JacksonJsonProvider())
                        .singleton(new BookRestService(new BookDataService(systemSession))));
        log.debug("/books endpoint added");
        RepositoryJaxrsService.addEndpoint(
                new RepositoryJaxrsEndpoint("/bookstore")
                        .singleton(new JacksonJsonProvider())
                        .singleton(new BookStoreResource(systemSession)));
        log.debug("/bookstore endpoint added");

    }

    @Override
    public void shutdown() {
        RepositoryJaxrsService.removeEndpoint("/books");
        log.debug("/books endpoint removed");
    }
}
