package com.fges.core;

/**
 * Interface pour les gestionnaires de requêtes
 */
public interface QueryHandler<Q extends Query<P>, P, R> {
    R handle(Q query) throws Exception;
}