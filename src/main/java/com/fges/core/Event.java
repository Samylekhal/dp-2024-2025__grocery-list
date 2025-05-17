package com.fges.core;

/**
 * Interface de base pour tous les événements du système
 */
public interface Event {
    String getName();
    Object getPayload();
}