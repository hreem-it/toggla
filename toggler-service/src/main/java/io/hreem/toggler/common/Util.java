package io.hreem.toggler.common;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import javax.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hreem.toggler.project.model.Environment;
import io.hreem.toggler.toggle.model.Toggle;

@ApplicationScoped
public class Util {

    private static ObjectMapper mapper = new ObjectMapper();

    public <T> T convert(String jsonString, Class<T> pojo) {
        try {
            return mapper.readValue(jsonString, pojo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String convert(Object pojo) {
        try {
            return mapper.writeValueAsString(pojo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void replaceIf(List<T> list, Predicate<? super T> pred, UnaryOperator<T> op) {
        list.replaceAll(t -> pred.test(t) ? op.apply(t) : t);
    }

}
