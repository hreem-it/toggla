package io.hreem.toggla.quarkus;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "toggla", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class TogglaConfig {

    /**
     * The API key to use for the Toggla API.
     * quarkus.toggla.api-key
     */
    @ConfigItem
    public String apiKey;

    /**
     * Base URI of the Toggla API. e.g mytoggla.instance.com
     * quarkus.toggla.api-key
     */
    @ConfigItem
    public String baseUri;

    /**
     * How often to poll the Toggla API for changes.
     * quarkus.toggla.auto-toggle-refresh-rate=30s
     */
    @ConfigItem
    public String autoToggleRefreshRate;
}
