package io.hreem.toggla;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;

public class TogglerPollingJob implements Job {

    private final static Logger Log = Logger.getLogger(TogglerPollingJob.class.getName());

    @Inject
    ToggleStore toggleStore;

    @Inject
    @RestClient
    TogglerGateway togglaGateway;

    @Inject
    Utils utils;

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        Log.log(Level.FINE, "Attempting to renew toggles");
        // Fetch the keys from the client
        Set<String> toggleKeys = toggleStore.getToggleKeys();

        // Fetch the status for each key
        for (String toggleKey : toggleKeys) {
            final var keyParts = toggleKey.split(":");
            var status = false;
            if (keyParts.length == 1)
                status = renew(keyParts[0], null);
            else
                status = renew(keyParts[0], keyParts[1]);
            toggleStore.putToggle(toggleKey, status);
        }
    }

    boolean renew(String key, String variationKey) {
        final var toggleKey = utils.constructKey(key, variationKey);
        try {
            boolean status = false;
            if (variationKey != null) {
                status = togglaGateway.getToggleVariationStatus(key, variationKey);
            } else {
                status = togglaGateway.getToggleStatus(key);
            }
            Log.log(Level.FINE, "Renewed toggle " + toggleKey + " with status " + status);
            return status;
        } catch (ForbiddenException e) {
            return utils.handleToggleCallException("Incorrect API Key provided, cannot invoke Toggler API", toggleKey,
                    e);
        } catch (Exception e) {
            return utils.handleToggleCallException("Failed to fetch toggle from Toggler API, falling back to 'false'",
                    toggleKey, e);
        }
    }

}
