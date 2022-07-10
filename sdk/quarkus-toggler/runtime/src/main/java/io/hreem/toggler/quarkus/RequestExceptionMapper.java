package io.hreem.toggler.quarkus;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Priority;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

@Priority(Priorities.ENTITY_CODER)
public class RequestExceptionMapper implements ResponseExceptionMapper<WebApplicationException> {

    private static final List<Integer> RETRYABLE_STATUS_CODES = Arrays.asList(
            // retryable 400 type errors
            Response.Status.CONFLICT.getStatusCode(),
            Response.Status.REQUEST_TIMEOUT.getStatusCode(),
            Response.Status.TOO_MANY_REQUESTS.getStatusCode(),
            // 500 type errors
            Response.Status.BAD_GATEWAY.getStatusCode(),
            Response.Status.SERVICE_UNAVAILABLE.getStatusCode(),
            Response.Status.GATEWAY_TIMEOUT.getStatusCode(),
            Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

    @Override
    public WebApplicationException toThrowable(Response response) {
        if (RETRYABLE_STATUS_CODES.contains(response.getStatus())) {
            return new RetryableRequestException(response);
        }
        switch (response.getStatus()) {
            case 400:
                return new BadRequestException(response);
            case 404:
                return new NotFoundException(response);
            default:
                return new WebApplicationException(response);
        }
    }
}