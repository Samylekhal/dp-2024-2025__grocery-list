package com.fges.core;

/**
 * Interface pour les gestionnaires de commandes
 */
public interface CommandHandler<C extends Command<P>, P> {
    int handle(C command) throws Exception;
}