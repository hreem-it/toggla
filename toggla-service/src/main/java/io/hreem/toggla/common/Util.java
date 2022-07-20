package io.hreem.toggla.common;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import javax.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;

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

    public String constructKey(String... keyParts) {
        return String.join(":", keyParts);
    }

    public <T> void replaceIf(List<T> list, Predicate<? super T> pred, UnaryOperator<T> op) {
        list.replaceAll(t -> pred.test(t) ? op.apply(t) : t);
    }

    public String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public Date formatDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            Log.errorf("Failed to parse date: " + dateStr);
            return null;
        }
    }

}
