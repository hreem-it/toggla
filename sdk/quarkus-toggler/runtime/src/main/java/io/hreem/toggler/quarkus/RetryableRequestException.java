package io.hreem.toggler.quarkus;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class RetryableRequestException extends WebApplicationException {
    public RetryableRequestException(Response response) {
        super(response);
    }
}
