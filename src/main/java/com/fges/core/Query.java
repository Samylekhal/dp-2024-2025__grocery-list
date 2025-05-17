package com.fges.core;

/**
 * Interface de base pour toutes les requêtes (lectures)
 */
public interface Query<T> {
    T getParameters();
}