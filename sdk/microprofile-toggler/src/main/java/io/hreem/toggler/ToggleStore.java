package io.hreem.toggler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ToggleStore {

    final Map<String, Boolean> toggleStatusCache = new HashMap<>();

    public Boolean getToggle(String toggleKey) {
        return toggleStatusCache.get(toggleKey);
    }

    public void putToggle(String toggleKey, boolean status) {
        toggleStatusCache.put(toggleKey, status);
    }

    public Set<String> getToggleKeys() {
        return toggleStatusCache.keySet();
    }

}
