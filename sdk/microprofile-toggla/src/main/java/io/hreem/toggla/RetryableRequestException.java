package io.hreem.toggla;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class RetryableRequestException extends WebApplicationException {
    public RetryableRequestException(Response response) {
        super(response);
    }
}
