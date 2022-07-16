package io.hreem.toggler;

import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Utils {
    private final static Logger Log = Logger.getLogger(Utils.class.getName());

    public String constructKey(String key, String variationKey) {
        return variationKey != null ? key + ":" + variationKey : key;
    }

    public boolean handleToggleCallException(String message, String toggleKey, Throwable e) {
        Log.log(Level.WARNING, message, e);
        Log.log(Level.WARNING, "Toggle fetch failed for " + toggleKey + " falling back to 'false'.");
        return false;
    }

}
