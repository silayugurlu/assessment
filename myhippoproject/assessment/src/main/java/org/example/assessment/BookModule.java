package org.example.assessment;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.example.assessment.resource.BookResource;
import org.onehippo.repository.jaxrs.RepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsService;
import org.onehippo.repository.modules.DaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

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
                        .singleton(new BookResource(systemSession)));
        log.debug("/books endpoint added");
    }

    @Override
    public void shutdown() {
        RepositoryJaxrsService.removeEndpoint("/books");
        log.debug("/books endpoint removed");
    }
}
