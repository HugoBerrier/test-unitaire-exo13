# Exercice 13 — API bancaire

API REST Spring Boot pour gérer des comptes bancaires (création, consultation, dépôt, retrait, virement).

**Dépôt GitHub :** https://github.com/HugoBerrier/test-unitaire-exo13

## Prérequis

- Java 21
- Maven

## Lancer l'application

```bash
mvn spring-boot:run
```

L'API est disponible sur `http://localhost:8080`

## Endpoints

| Méthode | Route | Description |
|---------|-------|-------------|
| POST | `/accounts` | Créer un compte |
| GET | `/accounts` | Lister les comptes |
| GET | `/accounts/{number}` | Consulter un compte |
| POST | `/accounts/{number}/deposit` | Déposer de l'argent |
| POST | `/accounts/{number}/withdraw` | Retirer de l'argent |
| POST | `/accounts/transfer` | Virement entre comptes |

## Tests

```bash
mvn test
```

Générer les rapports de tests :

```bash
mvn verify
```

## Rapports

Les rapports sont disponibles dans le dossier `reports/` :

- JaCoCo : `reports/jacoco/index.html`
- Cucumber : `reports/cucumber-reports/cucumber-report.html`

Ils sont aussi générés à chaque exécution de la CI GitHub Actions (onglet Actions > artefacts).

## Intégration continue

Le workflow GitHub Actions exécute les tests et archive les rapports à chaque push sur `main`.

## Historique Git

1. Création du projet Spring Boot
2. Développement des fonctionnalités métier
3. Tests unitaires (Mockito)
4. Scénarios BDD Cucumber
5. Rapports JaCoCo et Cucumber
6. GitHub Actions
7. Publication sur GitHub
