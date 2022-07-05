package io.hreem.toggler.common.http;

import javax.enterprise.context.RequestScoped;

import lombok.Data;

@Data
@RequestScoped
public class RequestContext {
    private String environment;
    private String projectKey;
    private String apiKey;
}
