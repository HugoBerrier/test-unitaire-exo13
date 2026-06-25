Feature: Gestion des comptes bancaires

  As a bank user
  I want to manage my bank accounts
  So that I can deposit, withdraw and transfer money

  Scenario: Création d'un nouveau compte
    Given no accounts exist
    When I create an account with number "ACC001" and holder "Alice"
    Then the account is created with balance 0

  Scenario: Dépôt d'argent sur un compte
    Given an account exists with number "ACC001" and holder "Alice" and balance 100
    When I deposit 50 on account "ACC001"
    Then the account "ACC001" has balance 150

  Scenario: Retrait avec fonds suffisants
    Given an account exists with number "ACC001" and holder "Alice" and balance 100
    When I withdraw 30 from account "ACC001"
    Then the account "ACC001" has balance 70

  Scenario: Retrait avec fonds insuffisants
    Given an account exists with number "ACC001" and holder "Alice" and balance 10
    When I try to withdraw 50 from account "ACC001"
    Then the withdrawal is rejected

  Scenario: Virement entre deux comptes
    Given an account exists with number "ACC001" and holder "Alice" and balance 100
    And an account exists with number "ACC002" and holder "Bob" and balance 50
    When I transfer 30 from account "ACC001" to account "ACC002"
    Then the account "ACC001" has balance 70
    And the account "ACC002" has balance 80

  Scenario: Virement refusé pour solde insuffisant
    Given an account exists with number "ACC001" and holder "Alice" and balance 10
    And an account exists with number "ACC002" and holder "Bob" and balance 50
    When I try to transfer 50 from account "ACC001" to account "ACC002"
    Then the transfer is rejected
