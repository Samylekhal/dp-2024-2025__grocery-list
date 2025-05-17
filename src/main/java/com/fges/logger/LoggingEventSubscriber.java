package com.fges.logger;

import com.fges.core.Event;
import com.fges.core.EventSubscriber;

/**
 * Subscriber qui enregistre les événements dans le journal
 */
public class LoggingEventSubscriber implements EventSubscriber {
    @Override
    public void handle(Event event) {
        System.out.println("[LOG] Event: " + event.getName() + " was processed successfully");
    }
}