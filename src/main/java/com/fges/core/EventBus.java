package com.fges.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bus d'événements simplifié
 */
public class EventBus {
    private static final Map<String, List<EventSubscriber>> subscribers = new HashMap<>();

    public static void subscribe(String eventName, EventSubscriber subscriber) {
        subscribers.computeIfAbsent(eventName, k -> new ArrayList<>()).add(subscriber);
    }

    public static void publish(Event event) {
        if (subscribers.containsKey(event.getName())) {
            subscribers.get(event.getName()).forEach(subscriber -> {
                try {
                    subscriber.handle(event);
                } catch (Exception e) {
                    System.err.println("Error handling event: " + e.getMessage());
                }
            });
        }
    }
}