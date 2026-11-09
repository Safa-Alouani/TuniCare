# 🏥 TuniCare

### Plateforme intelligente de gestion des services de santé et d'assurance maladie

## 📌 Présentation du projet

**TuniCare** est une plateforme web qui permet de centraliser et de simplifier la gestion des services de santé et d'assurance maladie.

Elle offre aux assurés un accès facile à leurs couvertures médicales, aux professionnels de santé, aux rendez-vous médicaux, aux dossiers médicaux et aux évaluations des professionnels de santé.

Le projet repose sur une architecture **Microservices**, garantissant la modularité, la sécurité et l'évolutivité de l'application.

## 🎯 Problématique

La gestion traditionnelle des services de santé présente plusieurs difficultés :

- Dispersion des informations médicales et administratives.
- Difficulté d'accès aux couvertures médicales.
- Complexité de la prise de rendez-vous.
- Difficulté de suivi des réclamations.
- Manque de centralisation des avis et évaluations des professionnels de santé.
- Protection des données médicales sensibles.

## 🎯 Objectifs

- Centraliser les services de santé et d'assurance.
- Faciliter la communication entre les différents acteurs.
- Sécuriser les données personnelles et médicales.
- Simplifier la gestion des rendez-vous et des réclamations.
- Permettre aux patients d'évaluer les professionnels de santé.
- Proposer une architecture évolutive basée sur les microservices.

## 👥 Acteurs du système

- **Assuré :** consulter sa couverture, rechercher un médecin, prendre un rendez-vous, déposer une réclamation et évaluer les professionnels de santé.
- **Professionnel de santé :** gérer ses informations et ses rendez-vous, consulter ses évaluations.
- **Agent administratif :** assurer le suivi des opérations administratives.
- **Administrateur :** gérer les utilisateurs et superviser la plateforme.

## ⚙️ Principales fonctionnalités

| Microservice | Responsabilités |
|---|---|
| User Service | Gestion des utilisateurs, des comptes, des rôles et de l'authentification. |
| Coverage Service | Gestion des contrats, des garanties et des remboursements. |
| Provider Service | Gestion des médecins, des cliniques et des pharmacies. |
| Reclamation Service | Création et suivi des réclamations. |
| Appointment Service | Gestion et réservation des rendez-vous. |
| MedicalRecord Service | Gestion et consultation des dossiers médicaux. |
| Evaluation Service | Gestion des évaluations et des avis des patients sur les professionnels de santé. |

## 🏗️ Architecture technique

Le projet utilise une architecture distribuée basée sur plusieurs microservices indépendants.

### Composants principaux

- **Angular :** développement de l'interface utilisateur.
- **API Gateway :** point d'entrée unique qui redirige les requêtes vers les microservices.
- **Eureka Server :** découverte et enregistrement des microservices.
- **Config Server :** centralisation de la configuration.
- **Keycloak :** gestion de l'authentification et des autorisations.
- **RabbitMQ :** communication asynchrone entre les microservices.
- **Docker :** conteneurisation et déploiement des composants.

### Fonctionnement général

1. L'utilisateur accède à l'application Angular.
2. Il s'authentifie à travers Keycloak.
3. Angular envoie une requête accompagnée d'un token JWT à l'API Gateway.
4. L'API Gateway identifie le microservice concerné.
5. Le microservice traite la requête et communique avec sa propre base de données.
6. Le résultat est retourné à l'application Angular.

## 🗄️ Microservices et bases de données

Chaque microservice possède sa propre base de données afin de garantir son indépendance.

| Microservice | Base de données | Justification |
|---|---|---|
| User Service | Base partagée | Gestion centralisée des utilisateurs et de leurs rôles. |
| Coverage Service | MySQL | Gestion des données structurées comme les contrats, les garanties et les remboursements. |
| Provider Service | MongoDB | Stockage flexible des informations des médecins, cliniques et pharmacies. |
| Reclamation Service | PostgreSQL | Gestion des réclamations, de leurs statuts et de leur suivi. |
| Appointment Service | CouchDB | Gestion flexible des rendez-vous et de leurs informations. |
| MedicalRecord Service | Oracle Database | Gestion des dossiers médicaux et des données structurées. |
| Evaluation Service | Firebase | Stockage flexible des évaluations et actualisation rapide des notes. |

## 🔄 Modes de communication

Deux modes de communication sont utilisés :

### 1. Communication synchrone (REST)

Utilisée lorsqu'un microservice a besoin d'une réponse immédiate d'un autre microservice.

**Exemples :**

- Appointment Service contacte Coverage Service pour vérifier l'éligibilité d'un patient avant de confirmer un rendez-vous.
- Evaluation Service contacte User Service pour vérifier l'identité du patient.
- Evaluation Service contacte Provider Service pour récupérer les informations du professionnel de santé.
- Provider Service récupère les notes moyennes et le nombre d'évaluations depuis Evaluation Service.

### 2. Communication asynchrone (RabbitMQ)

Utilisée pour transmettre des événements sans attendre une réponse immédiate.

**Exemples :**

- Appointment Service publie un événement `AppointmentCreated` après la création d'un rendez-vous.
- MedicalRecord Service publie un événement lorsqu'un document médical est ajouté au dossier afin d'informer Reclamation Service qu'un justificatif médical est disponible pour le traitement d'une réclamation.
- Evaluation Service peut publier un événement `EvaluationCreated` après l'ajout d'une nouvelle évaluation.

## 🛠️ Technologies utilisées

| Domaine | Technologies |
|---
