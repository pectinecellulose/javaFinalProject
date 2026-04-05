# 📚 Système de Gestion de Bibliothèque Municipale

Application Java complète pour la gestion d'une bibliothèque municipale avec base de données MySQL intégrée.



## 🚀 Installation et Démarrage

### 1. Configuration de la Base de Données

#### Installation MySQL
# Windows
# Télécharger depuis https://dev.mysql.com/downloads/mysql/

# macOS
brew install mysql

# Linux (Ubuntu/Debian)
sudo apt update
sudo apt install mysql-server
```


### 2. Configuration des Identifiants

#### Créer et editer le fichier `.env`


#### Éditer le fichier `.env`
```env
# Configuration de la connexion
DB_HOST=localhost
DB_PORT=3306
DB_NAME=bibliotheque
DB_USER=root
DB_PASSWORD=votre_mot_de_passe_ici

# Configuration du driver MySQL
MYSQL_DRIVER=com.mysql.cj.jdbc.Driver

# Options de connexion (optionnel)
DB_USE_SSL=false
DB_ALLOW_PUBLIC_KEY_RETRIEVAL=true
DB_SERVER_TIMEZONE=UTC
```

### 3. Installation du Driver MySQL

#### Option 1 : Téléchargement Manuel
1. Télécharger [MySQL Connector/J 9.6.0](https://dev.mysql.com/downloads/connector/j/)
2. Placer `mysql-connector-j-9.6.0.jar` dans le dossier du projet



### 4. Compilation et Exécution

#### Mode Démo (sans base de données)
```bash
javac *.java
java Main
```

#### Mode Base de Données (complet)
```bash
javac -cp ".;mysql-connector-j-9.6.0.jar" *.java
java -cp ".;mysql-connector-j-9.6.0.jar" Main
```

## 📋 Structure des Données



## 🖥️ Interface Utilisateur

### Menu Principal
```
================================================================================
SYSTÈME DE GESTION DE BIBLIOTHÈQUE MUNICIPALE
================================================================================
1. Gestion des annexes
2. Gestion des conditions de conservation
3. Afficher les statistiques globales
4. A propos du systeme
5. Afficher le journal des actions
0. Quitter
```


# 2. Compiler
javac -cp ".;mysql-connector-j-9.6.0.jar" *.java

# 3. Exécuter
java -cp ".;mysql-connector-j-9.6.0.jar" Main
```
