# Système de Gestion d'Accès aux Portes Automatiques - Backend

## Vue d'ensemble

Ce backend Spring Boot gère un système complet de contrôle d'accès pour portes automatiques avec badges physiques et mobiles. Le système permet la gestion des utilisateurs, des portes, des badges et des règles d'accès temporelles.

## Architecture

Le projet suit une **architecture hexagonale** avec séparation claire des responsabilités :

### 📦 Structure des packages

```
fr.emse.tb3pwme.project/
├── domain/                 # Logique métier pure (immutable)
│   ├── User.java
│   ├── Badge.java
│   ├── Door.java
│   ├── AccessRule.java
│   ├── AccessLog.java
│   ├── TimeSlot.java
│   ├── UserRole.java (enum)
│   ├── BadgeType.java (enum)
│   └── AccessResult.java (enum)
│
├── persistence/            # Couche de persistence JPA
│   ├── *Entity.java       # Entités JPA mutables
│   ├── *Repository.java   # Repositories Spring Data
│   └── EntityMapper.java  # Conversion Domain ↔ Entity
│
├── application/            # Services applicatifs
│   ├── UserService.java
│   ├── BadgeService.java
│   ├── DoorService.java
│   ├── AccessRuleService.java
│   ├── AccessLogService.java
│   └── AccessControlService.java  # Service principal de vérification d'accès
│
├── web/                    # Contrôleurs REST et DTOs
│   ├── LockController.java        # API publique pour serrures IoT
│   ├── UserController.java
│   ├── BadgeController.java
│   ├── DoorController.java
│   ├── AccessRuleController.java
│   ├── AccessLogController.java
│   └── RepresentationMapper.java  # DTOs et conversions
│
├── security/               # Helpers de sécurité
│   ├── UserSecurity.java
│   └── BadgeSecurity.java
│
├── SecurityConfig.java     # Configuration Spring Security + OAuth2
└── DataLoader.java         # Données de démonstration (profil dev)
```

## Fonctionnalités principales

### 🚪 Gestion des portes
- Création, modification, activation/désactivation de portes
- Association d'un deviceId unique pour chaque serrure IoT
- Localisation et description des portes

### 👤 Gestion des utilisateurs
- CRUD complet des utilisateurs
- Rôles : ADMIN, USER, VISITOR
- Activation/désactivation de comptes
- Authentification via Keycloak (OAuth2/JWT)

### 🎫 Gestion des badges
- Badges physiques et mobiles
- Date d'expiration
- Activation/désactivation
- Association utilisateur ↔ badge(s)

### ⏰ Règles d'accès temporelles
- Définition de plages horaires par jour de semaine
- Règles User → Door avec créneaux multiples
- Accès 24/7 possible (liste vide de TimeSlots)
- Activation/désactivation de règles

### 📊 Logs d'accès
- Enregistrement automatique de toutes les tentatives d'accès
- Raisons de refus détaillées
- Historique par utilisateur ou par porte
- Filtrage par période

## API REST

### 🔓 API Publique (Serrures IoT)

#### POST `/api/locks/verify-access`
Endpoint **PUBLIC** appelé par les serrures pour vérifier si un badge a accès.

**Request:**
```json
{
  "badgeNumber": "BADGE-USER-001",
  "doorDeviceId": "DOOR-MAIN-001"
}
```

**Response:**
```json
{
  "result": "GRANTED",
  "message": "Access granted"
}
```

**Résultats possibles:**
- `GRANTED` - Accès autorisé
- `DENIED_INVALID_BADGE` - Badge invalide ou inactif
- `DENIED_INACTIVE_USER` - Utilisateur inactif
- `DENIED_NO_PERMISSION` - Pas de règle d'accès
- `DENIED_TIME_RESTRICTION` - En dehors des horaires autorisés
- `DENIED_EXPIRED_BADGE` - Badge expiré
- `DENIED_INACTIVE_DOOR` - Porte inactive

### 🔐 API Sécurisée (Administration)

Tous les endpoints suivants nécessitent une authentification OAuth2/JWT.

#### Users `/api/users`
- `GET /api/users` - Liste tous les utilisateurs (ADMIN)
- `GET /api/users/{id}` - Détails utilisateur (ADMIN ou propriétaire)
- `POST /api/users` - Créer un utilisateur (ADMIN)
- `PUT /api/users/{id}` - Modifier un utilisateur (ADMIN ou propriétaire)
- `POST /api/users/{id}/activate` - Activer (ADMIN)
- `POST /api/users/{id}/deactivate` - Désactiver (ADMIN)
- `DELETE /api/users/{id}` - Supprimer (ADMIN)

#### Badges `/api/badges`
- `GET /api/badges` - Liste tous les badges (ADMIN)
- `GET /api/badges/{id}` - Détails badge (ADMIN ou propriétaire)
- `GET /api/badges/user/{userId}` - Badges d'un utilisateur
- `POST /api/badges` - Créer un badge (ADMIN)
- `PUT /api/badges/{id}/expiry` - Modifier l'expiration (ADMIN)
- `POST /api/badges/{id}/activate` - Activer (ADMIN)
- `POST /api/badges/{id}/deactivate` - Désactiver (ADMIN)
- `DELETE /api/badges/{id}` - Supprimer (ADMIN)

#### Doors `/api/doors`
- `GET /api/doors` - Liste toutes les portes (ADMIN/USER)
- `GET /api/doors/{id}` - Détails porte (ADMIN/USER)
- `POST /api/doors` - Créer une porte (ADMIN)
- `PUT /api/doors/{id}` - Modifier une porte (ADMIN)
- `POST /api/doors/{id}/activate` - Activer (ADMIN)
- `POST /api/doors/{id}/deactivate` - Désactiver (ADMIN)
- `DELETE /api/doors/{id}` - Supprimer (ADMIN)

#### Access Rules `/api/access-rules`
- `GET /api/access-rules` - Liste toutes les règles (ADMIN)
- `GET /api/access-rules/{id}` - Détails règle (ADMIN)
- `GET /api/access-rules/user/{userId}` - Règles d'un utilisateur
- `GET /api/access-rules/door/{doorId}` - Règles d'une porte (ADMIN)
- `POST /api/access-rules` - Créer une règle (ADMIN)
- `PUT /api/access-rules/{id}` - Modifier les horaires (ADMIN)
- `POST /api/access-rules/{id}/activate` - Activer (ADMIN)
- `POST /api/access-rules/{id}/deactivate` - Désactiver (ADMIN)
- `DELETE /api/access-rules/{id}` - Supprimer (ADMIN)

#### Access Logs `/api/access-logs`
- `GET /api/access-logs` - Tous les logs (ADMIN)
- `GET /api/access-logs/{id}` - Détails log (ADMIN)
- `GET /api/access-logs/user/{userId}` - Logs d'un utilisateur
- `GET /api/access-logs/door/{doorId}` - Logs d'une porte (ADMIN)
- `GET /api/access-logs/date-range?start=&end=` - Logs par période (ADMIN)

## Logique de Vérification d'Accès

Le service `AccessControlService.verifyAccess()` effectue les vérifications suivantes dans l'ordre :

1. ✅ **Badge existe ?** → Sinon `DENIED_INVALID_BADGE`
2. ✅ **Badge expiré ?** → Sinon `DENIED_EXPIRED_BADGE`
3. ✅ **Badge actif ?** → Sinon `DENIED_INVALID_BADGE`
4. ✅ **Utilisateur existe ?** → Sinon `DENIED_INACTIVE_USER`
5. ✅ **Utilisateur actif ?** → Sinon `DENIED_INACTIVE_USER`
6. ✅ **Porte existe ?** → Sinon `DENIED_NO_PERMISSION`
7. ✅ **Porte active ?** → Sinon `DENIED_INACTIVE_DOOR`
8. ✅ **Règle d'accès existe ?** (User → Door) → Sinon `DENIED_NO_PERMISSION`
9. ✅ **Dans les horaires autorisés ?** → Sinon `DENIED_TIME_RESTRICTION`
10. ✅ **Toutes les vérifications OK** → `GRANTED` 🎉

Chaque tentative d'accès est automatiquement enregistrée dans `access_logs`.

## Données de Démonstration

En mode `dev`, le `DataLoader` charge automatiquement :

### Utilisateurs
- **admin@example.com** (ADMIN) - Accès 24/7 partout
- **alice.dupont@example.com** (USER) - Heures de bureau (8h-18h)
- **bob.martin@example.com** (USER) - Heures étendues (6h-22h)
- **visitor@example.com** (VISITOR) - Heures de visite (9h-17h)

### Portes
- **DOOR-MAIN-001** - Entrée Principale
- **DOOR-SERVER-001** - Salle Serveurs
- **DOOR-OFFICE-201** - Bureau Étage 2

### Badges
- Badges physiques pour tous les utilisateurs
- Badge mobile pour Alice
- Expiration : 5 ans (admin), 1 an (users), 7 jours (visiteur)

### Règles d'accès
- Admin : accès total 24/7
- Alice : entrée + bureaux en heures de bureau
- Bob : entrée + serveurs en heures étendues
- Visiteur : entrée uniquement en heures de visite

## Configuration

### application.properties
```properties
spring.application.name=access-control-system
spring.profiles.active=dev

# Database H2 (dev)
spring.datasource.url=jdbc:h2:mem:accessdb
spring.jpa.hibernate.ddl-auto=create-drop

# OAuth2 Keycloak
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9090/realms/tb3pwme
```

### Sécurité
- `/api/locks/**` - **PUBLIC** (pas d'authentification)
- `/api/**` - **SECURED** (JWT requis)
- CSRF désactivé sur `/api/locks/**`
- Annotations `@PreAuthorize` sur les méthodes

## Démarrage

### Prérequis
- Java 21+
- Gradle 8+
- Keycloak 23+ (optionnel en mode dev)

### Lancement
```bash
# En mode dev (sans Keycloak)
./gradlew bootRun --args='--spring.profiles.active=dev'

# En mode production (avec Keycloak)
./start-keycloak.sh  # Terminal 1
./gradlew bootRun    # Terminal 2
```

### Test rapide
```bash
# Vérifier un accès (fonctionne sans authentification)
curl -X POST http://localhost:8080/api/locks/verify-access \
  -H "Content-Type: application/json" \
  -d '{
    "badgeNumber": "BADGE-USER-001",
    "doorDeviceId": "DOOR-MAIN-001"
  }'
```

## Évolutions futures

### Phase 2 - Notifications
- Intégration Firebase Cloud Messaging (FCM)
- Notifications push sur tentatives d'accès refusées
- Alertes temps réel pour les administrateurs

### Phase 3 - Application Mobile
- Scan de badges via NFC
- Génération de badges temporaires
- Gestion Bluetooth pour transfert de credentials

### Phase 4 - Analytics
- Tableau de bord d'utilisation
- Statistiques par utilisateur/porte/période
- Détection d'anomalies

## Support

Pour toute question ou problème, consulter la documentation de Spring Boot et Keycloak.

---

**Version:** 1.0.0  
**Auteur:** TB3 PWM-E Project  
**Licence:** MIT

