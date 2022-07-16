package io.hreem.toggla.quarkus.deployment;

import io.hreem.toggla.quarkus.AuthHeaderFactory;
import io.hreem.toggla.quarkus.Poller;
import io.hreem.toggla.quarkus.TogglerClient;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class QuarkusTogglerProcessor {

    private static final String FEATURE = "quarkus-toggla";

    @BuildStep
    FeatureBuildItem feature(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer) {
        AdditionalBeanBuildItem unremovableProducer = AdditionalBeanBuildItem.unremovableOf(Poller.class);
        additionalBeanProducer.produce(unremovableProducer);
        unremovableProducer = AdditionalBeanBuildItem.unremovableOf(AuthHeaderFactory.class);
        additionalBeanProducer.produce(unremovableProducer);
        unremovableProducer = AdditionalBeanBuildItem.unremovableOf(TogglerClient.class);
        additionalBeanProducer.produce(unremovableProducer);
        return new FeatureBuildItem(FEATURE);
    }

}
