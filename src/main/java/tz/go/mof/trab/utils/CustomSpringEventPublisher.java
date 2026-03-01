package tz.go.mof.trab.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class CustomSpringEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(CustomSpringEventPublisher.class);

    private ApplicationEventPublisher applicationEventPublisher;

    CustomSpringEventPublisher(ApplicationEventPublisher applicationEventPublisher){
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void doStuffAndPublishAnEvent(final String message) {
        log.debug("Publishing custom event.");
        CustomSpringEvent customSpringEvent = new CustomSpringEvent(this, message);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }
}
