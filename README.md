# Système de Gestion de Bibliothèque Municipale

Projet Java orienté objet pour la gestion d'une bibliothèque municipale avec focus sur la gestion des annexes et des conditions de conservation.

## 📋 Description

Ce système permet de :
- Gérer plusieurs annexes de bibliothèque
- Surveiller les conditions de conservation des ouvrages
- Effectuer des opérations de gestion quotidienne
- Détecter et corriger automatiquement les anomalies

## 🏗️ Structure du Projet

```
java/
├── Annexe.java              # Gestion des annexes
├── ConditionConservation.java # Surveillance des conditions
├── Main.java                 # Simulation et tests
└── README.md                 # Documentation
```

## 🔧 Compilation et Exécution

### Prérequis
- JDK 8 ou supérieur
- Terminal Windows (PowerShell)

### Compilation

```powershell
# Naviguer vers le répertoire du projet
cd "c:/Users/Ken Bugul/Documents/Projet/java"

# Compiler tous les fichiers Java
javac *.java
```

**Résultat attendu :**
```
# Aucune erreur si la compilation réussit
# Les fichiers .class sont générés
```

### Exécution

```powershell
# Lancer la simulation complète
java Main
```

**Résultat attendu :**
```
================================================================================
SYSTÈME DE GESTION DE BIBLIOTHÈQUE MUNICIPALE
Simulation du système
================================================================================
Démarrage : 03/04/2026 19:52:17

📍 SCÉNARIO 1: CRÉATION DES ANNEXES
--------------------------------------------------
Création des annexes de la bibliothèque municipale...

Équipement "Climatisation centrale" ajouté à l'annexe "Bibliothèque Centrale".
...
```

## 📊 Fonctionnalités Principales

### Gestion des Annexes
- Création et configuration d'annexes
- Gestion des capacités et stocks
- Ajout/retrait d'équipements
- Contrôle des horaires d'ouverture

### Conditions de Conservation
- Surveillance température (16-24°C)
- Contrôle humidité (40-60%)
- Monitoring luminosité (<1000 lux)
- Qualité de l'air
- Détection automatique d'anomalies

### Simulation Complète
Le programme exécute 5 scénarios :
1. Création des annexes
2. Configuration des conditions
3. Opérations quotidiennes
4. Gestion des problèmes
5. État final et statistiques

## 🛡️ Gestion des Erreurs

Le système inclut une validation complète :
- Contrôle des valeurs négatives
- Vérification des capacités maximales
- Validation des chaînes vides
- Gestion des états incohérents

## 📈 Exemple de Résultat

```
📊 STATISTIQUES GLOBALES
------------------------------
• Bibliothèque Centrale: 45000/50000 livres (90,0%)
• Annexe Nord: 13500/15000 livres (90,0%)
• Médiathèque Jeunesse: 7200/8000 livres (90,0%)

📈 RÉSUMÉ
------------------------------
Total de livres gérés : 65700
Capacité totale : 73000
Taux de remplissage global : 90,0%
Nombre d'annexes : 3
Zones de conservation : 3
Zones conformes : 3/3
```

## 🚀 Démarrage Rapide

```powershell
# 1. Compiler
cd "c:/Users/Ken Bugul/Documents/Projet/java"
javac *.java

# 2. Exécuter
java Main
```

Le programme affichera une simulation complète du système avec tous les états intermédiaires.

## 📝 Notes

- Le code respecte les principes d'encapsulation
- Toutes les validations sont implémentées
- La simulation est réaliste et complète
- Le système est prêt pour l'extension avec une base de données
