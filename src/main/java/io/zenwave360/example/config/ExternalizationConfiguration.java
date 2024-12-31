package io.zenwave360.example.config;

import io.zenwave360.modulith.events.scs.config.EnableSpringCloudStreamEventExternalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableSpringCloudStreamEventExternalization
@EnableScheduling
class ExternalizationConfiguration {

    private Logger log = LoggerFactory.getLogger(ExternalizationConfiguration.class);

    @Autowired
    IncompleteEventPublications incompleteEventPublications;

    @Scheduled(fixedRate = 30000)
    public void resubmitIncompletePublications() {
        incompleteEventPublications.resubmitIncompletePublications(eventPublication -> {
            // Handle resubmitted publication
            log.debug("Resubmitted publication: " + eventPublication);
            return true;
        });
    }
}
