package io.hreem.toggla;

import java.util.Arrays;
import java.util.List;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import jakarta.annotation.Priority;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@Priority(1000)
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
            case 403:
                return new ForbiddenException(response);
            case 401:
                return new ForbiddenException(response);
            default:
                return new WebApplicationException(response);
        }
    }
}