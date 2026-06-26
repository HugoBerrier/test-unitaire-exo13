# language: fr
Fonctionnalité: Réservations d'ouvrages
  En tant que bibliothécaire
  Je veux gérer les réservations
  Afin de satisfaire les adhérents quand un ouvrage est indisponible

  Scénario: Réserver un ouvrage indisponible
    Étant donné l'adhérent "Alice" avec l'identifiant "M1"
    Et l'ouvrage "1984" avec l'identifiant "W1"
    Et l'ouvrage "W1" est emprunté par "M1" depuis le "2026-01-10"
    Quand l'adhérent "M2" nommé "Bob" réserve l'ouvrage "W1" le "2026-01-12T09:00:00"
    Alors la réservation est en attente pour l'ouvrage "W1"

  Scénario: Plusieurs réservations sur le même ouvrage
    Étant donné l'adhérent "Alice" avec l'identifiant "M1"
    Et l'ouvrage "1984" avec l'identifiant "W1"
    Et l'ouvrage "W1" est emprunté par "M1" depuis le "2026-01-10"
    Quand l'adhérent "M2" nommé "Bob" réserve l'ouvrage "W1" le "2026-01-12T09:00:00"
    Et l'adhérent "M3" nommé "Claire" réserve l'ouvrage "W1" le "2026-01-12T10:00:00"
    Alors il y a 2 réservations en attente pour l'ouvrage "W1"

  Scénario: Restitution d'un ouvrage réservé
    Étant donné l'adhérent "Alice" avec l'identifiant "M1"
    Et l'ouvrage "1984" avec l'identifiant "W1"
    Et l'ouvrage "W1" est emprunté par "M1" depuis le "2026-01-10"
    Et l'adhérent "M2" nommé "Bob" réserve l'ouvrage "W1" le "2026-01-12T09:00:00"
    Quand l'ouvrage "W1" est restitué le "2026-01-20"
    Alors la première réservation pour l'ouvrage "W1" est honorée

  Scénario: Refus d'une réservation pour un adhérent suspendu
    Étant donné l'adhérent "Alice" avec l'identifiant "M1"
    Et l'ouvrage "1984" avec l'identifiant "W1"
    Et l'ouvrage "W1" est emprunté par "M1" depuis le "2026-01-10"
    Et l'adhérent "M4" nommé "David" est suspendu
    Quand l'adhérent suspendu "M4" tente de réserver l'ouvrage "W1" le "2026-01-12T09:00:00"
    Alors la réservation est refusée pour suspension

  Scénario: Impossible d'emprunter un ouvrage déjà emprunté
    Étant donné l'adhérent "Alice" avec l'identifiant "M1"
    Et l'adhérent "Bob" avec l'identifiant "M2"
    Et l'ouvrage "1984" avec l'identifiant "W1"
    Et l'ouvrage "W1" est emprunté par "M1" depuis le "2026-01-10"
    Quand l'adhérent "M2" tente d'emprunter l'ouvrage "W1" le "2026-01-15"
    Alors l'emprunt est refusé car l'ouvrage est indisponible
