package com.fges.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Bus de commandes simplifié qui dirige les commandes vers leurs gestionnaires
 */
public class CommandBus {
    private static final Map<Class<?>, CommandHandler<?, ?>> handlers = new HashMap<>();

    public static <C extends Command<P>, P> void register(Class<C> commandClass, CommandHandler<C, P> handler) {
        handlers.put(commandClass, handler);
    }

    @SuppressWarnings("unchecked")
    public static <C extends Command<P>, P> int dispatch(C command) throws Exception {
        CommandHandler<C, P> handler = (CommandHandler<C, P>) handlers.get(command.getClass());
        if (handler == null) {
            throw new IllegalArgumentException("No handler registered for " + command.getClass().getName());
        }

        int result = handler.handle(command);

        // Si la commande a réussi, publier un événement correspondant
        if (result == 0) {
            String eventName = command.getClass().getSimpleName().replace("Command", "Event");
            Event event = new GenericEvent(eventName, command.getPayload());
            EventBus.publish(event);
        }

        return result;
    }

    // Événement générique pour simplifier l'implémentation
    private static class GenericEvent implements Event {
        private final String name;
        private final Object payload;

        public GenericEvent(String name, Object payload) {
            this.name = name;
            this.payload = payload;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Object getPayload() {
            return payload;
        }
    }
}