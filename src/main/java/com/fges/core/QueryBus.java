package com.fges.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Bus de requêtes simplifié qui dirige les requêtes vers leurs gestionnaires
 */
public class QueryBus {
    private static final Map<Class<?>, QueryHandler<?, ?, ?>> handlers = new HashMap<>();

    public static <Q extends Query<P>, P, R> void register(Class<Q> queryClass, QueryHandler<Q, P, R> handler) {
        handlers.put(queryClass, handler);
    }

    @SuppressWarnings("unchecked")
    public static <Q extends Query<P>, P, R> R dispatch(Q query) throws Exception {
        QueryHandler<Q, P, R> handler = (QueryHandler<Q, P, R>) handlers.get(query.getClass());
        if (handler == null) {
            throw new IllegalArgumentException("No handler registered for " + query.getClass().getName());
        }

        return handler.handle(query);
    }
}