# Architecture du système de liste de courses en Java

Ce document présente une architecture Java basée sur les principes CQRS (Command Query Responsibility Segregation) et Event Sourcing. Le système implémente une application simple de liste de courses avec plusieurs fonctionnalités comme l'ajout d'articles, la suppression d'articles et l'affichage de la liste.

## Principes d'architecture

### CQRS (Command Query Responsibility Segregation)

L'architecture CQRS sépare les opérations de lecture (Queries) des opérations d'écriture (Commands) :

- **Commands** : Modifient l'état du système (ajout d'un article, suppression d'un article)
- **Queries** : Lisent les données sans modifier l'état (liste des articles, informations système)

Cette séparation permet une meilleure organisation du code, une meilleure testabilité et une plus grande flexibilité pour faire évoluer chaque partie indépendamment.

### Bus de commandes et de requêtes

Le système utilise des bus pour diriger les commandes et les requêtes vers leurs gestionnaires appropriés :

- **CommandBus** : Dirige les commandes vers les gestionnaires de commandes
- **QueryBus** : Dirige les requêtes vers les gestionnaires de requêtes
- **EventBus** : Propage les événements aux abonnés après l'exécution des commandes

## Structure du code

### Core (Noyau)

Le noyau contient les interfaces et classes de base qui définissent l'architecture :

1. **Command** : Interface pour toutes les commandes avec une méthode `getPayload()`
2. **CommandHandler** : Interface pour les gestionnaires de commandes avec une méthode `handle()`
3. **CommandBus** : Registre et répartiteur de commandes
4. **Query** : Interface pour toutes les requêtes
5. **QueryHandler** : Interface pour les gestionnaires de requêtes
6. **QueryBus** : Registre et répartiteur de requêtes
7. **Event** : Interface pour tous les événements
8. **EventBus** : Système de publication/abonnement pour les événements
9. **EventSubscriber** : Interface pour les abonnés aux événements

### Commands

Les commandes représentent des intentions de modifier l'état du système :

1. **AddItemCommand** : Ajouter un article à la liste
2. **RemoveItemCommand** : Supprimer un article de la liste
3. **DeleteFileCommand** : Supprimer le fichier de la liste

### Queries

Les requêtes représentent des demandes de lecture de données :

1. **ListItemsQuery** : Obtenir la liste des articles
2. **InfoQuery** : Obtenir des informations sur le système

### Handlers

Les gestionnaires exécutent la logique associée aux commandes et aux requêtes :

1. **AddItemCommandHandler** : Traite l'ajout d'un article
2. **RemoveItemCommandHandler** : Traite la suppression d'un article
3. **DeleteFileCommandHandler** : Traite la suppression du fichier
4. **ListItemsQueryHandler** : Traite l'affichage de la liste des articles
5. **InfoQueryHandler** : Traite l'affichage des informations système

### Repository

Le système utilise un repository (GroceryRepository) pour gérer la persistance des données. Le repository est injecté dans les handlers qui en ont besoin.

### Logger

Un simple système de journalisation des événements est implémenté via **LoggingEventSubscriber**, qui s'abonne aux événements du système et les enregistre.

## Flux d'exécution

1. Une commande (Command) ou une requête (Query) est créée avec ses paramètres
2. La commande/requête est envoyée au bus correspondant
3. Le bus trouve le gestionnaire approprié et lui transmet la commande/requête
4. Le gestionnaire exécute la logique métier et interagit avec le repository si nécessaire
5. Pour les commandes réussies, un événement correspondant est publié
6. Les abonnés aux événements (comme le Logger) traitent l'événement

## Avantages de cette architecture

1. **Séparation des responsabilités** : Les opérations de lecture et d'écriture sont clairement séparées
2. **Extensibilité** : Facile d'ajouter de nouvelles commandes ou requêtes sans modifier le code existant
3. **Testabilité** : Chaque composant peut être testé isolément
4. **Traçabilité** : Les événements fournissent un historique des actions réalisées

## Remarque sur MyOptions

La classe "MyOptions" n'est pas présente dans les fichiers fournis. Cette classe pourrait être utilisée pour gérer les options de configuration de l'application, mais elle n'apparaît pas dans les sources actuelles.

## Diagramme simplifié de l'architecture

```
Diagramme simplifié de l'architecture
                   ┌─────────────┐
                   │  Interface  │
                   │    CLI      │
                   └──────┬──────┘
                          │
               ┌──────────▼──────────┐
               │      MyOptions      │
               │ (Analyse arguments) │
               └──────────┬──────────┘
                          │
       ┌─────────────────┴───────────────────┐
       │                                     │
┌──────▼─────┐                         ┌─────▼──────┐
│ CommandBus │                         │  QueryBus  │
└──────┬─────┘                         └─────┬──────┘
       │                                     │
┌──────▼──────────────┐           ┌──────────▼────────┐
│ CommandHandlers     │           │ QueryHandlers     │
│ - AddItemHandler    │           │ - ListItemsHandler│
│ - RemoveItemHandler │           │ - InfoHandler     │
│ - DeleteFileHandler │           └──────────┬────────┘
└──────┬──────────────┘                      │
       │                                     │
       │                                     │
┌──────▼──────┐                    ┌─────────▼───────┐
│ Repository  │◄───────────────────│ Résultats       │
└──────┬──────┘                    └─────────────────┘
       │
       │
┌──────▼──────┐
│  EventBus   │
└──────┬──────┘
       │
┌──────▼──────────────┐
│ EventSubscribers    │
│ - LoggingSubscriber │
└─────────────────────┘
```

## Conclusion

Cette architecture fournit une base solide pour une application de liste de courses et peut être facilement étendue pour prendre en charge d'autres fonctionnalités. La séparation entre commandes et requêtes améliore la maintenabilité et la testabilité du code.