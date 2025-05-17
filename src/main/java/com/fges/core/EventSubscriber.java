package com.fges.core;

/**
 * Interface pour les abonnés aux événements
 */
public interface EventSubscriber {
    void handle(Event event) throws Exception;
}