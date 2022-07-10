package io.hreem.toggler.quarkus;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "toggler", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class TogglerConfig {

    /**
     * The API key to use for the Toggler API.
     * quarkus.toggler.api-key
     */
    @ConfigItem
    public String apiKey;

    /**
     * Base URI of the Toggler API. e.g mytoggler.instance.com
     * quarkus.toggler.api-key
     */
    @ConfigItem
    public String baseUri;

    /**
     * How often to poll the Toggler API for changes.
     * quarkus.toggler.auto-toggle-refresh-rate=30s
     */
    @ConfigItem
    public String autoToggleRefreshRate;
}
