# Exercice 14 — MédiaCity (prêts et réservations)

Module Maven de gestion des prêts d'ouvrages, retours, pénalités et réservations pour médiathèque.

**Dépôt GitHub :** https://github.com/HugoBerrier/test-unitaire-exo13

## Prérequis

- Java 21
- Maven 3.9+

## Technologies

- JUnit 5, AssertJ, Mockito
- Cucumber (BDD)
- JaCoCo (couverture ≥ 80 % sur `com.mediacity.service`)

## Commandes

```bash
cd exercice14
mvn test
mvn verify
mvn site
```

## Rapports

Après `mvn verify`, les rapports sont copiés dans `reports/` :

- Surefire : `reports/surefire-reports/`
- JaCoCo : `reports/jacoco/index.html`
- Cucumber : `reports/cucumber-reports/cucumber-report.html`

Le site Maven (`mvn site`) regroupe aussi les rapports dans `target/site/`.

## Règles métier

- Prêt : date de retour = date d'emprunt + 21 jours
- Pénalité : 0,15 € par jour de retard
- Retard important : ≥ 7 jours → suspension après 3 retards dans l'année
- Réservation possible uniquement si l'ouvrage est emprunté
