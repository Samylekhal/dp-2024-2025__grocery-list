package com.fges.core;

/**
 * Interface de base pour toutes les commandes (mutations)
 */
public interface Command<T> {
    T getPayload();
}