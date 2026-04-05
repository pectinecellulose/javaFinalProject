# Configuration du Système de Gestion de Bibliothèque

## 🔐 **Configuration de la Base de Données**

### 1. **Créer le fichier `.env`**
Copiez le fichier `.env.example` et renommez-le en `.env` :

```bash
cp .env.example .env
```

### 2. **Éditer le fichier `.env`**
Modifiez les valeurs selon votre configuration MySQL :

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

### 3. **Variables Disponibles**

| Variable | Description | Valeur par défaut |
|----------|-------------|------------------|
| `DB_HOST` | Hôte de la base de données | `localhost` |
| `DB_PORT` | Port de connexion | `3306` |
| `DB_NAME` | Nom de la base | `bibliotheque` |
| `DB_USER` | Utilisateur MySQL | `root` |
| `DB_PASSWORD` | Mot de passe MySQL | `""` (vide) |
| `MYSQL_DRIVER` | Driver JDBC | `com.mysql.cj.jdbc.Driver` |
| `DB_USE_SSL` | Connexion SSL | `false` |
| `DB_ALLOW_PUBLIC_KEY_RETRIEVAL` | Récupération clé publique | `true` |
| `DB_SERVER_TIMEZONE` | Fuseau horaire serveur | `UTC` |

## 🚀 **Démarrage du Système**

### **Mode Démo (sans base de données)**
```bash
javac *.java
java Main
```

### **Mode Base de Données (complet)**
```bash
# 1. Placer le driver MySQL dans le dossier
# 2. Configurer le fichier .env
# 3. Compiler avec le driver
javac -cp ".;mysql-connector-j-9.6.0.jar" *.java

# 4. Exécuter avec le driver
java -cp ".;mysql-connector-j-9.6.0.jar" Main
```

## 📋 **Vérification de la Configuration**

Au démarrage, le système affiche :
- ✅ **Configuration chargée** si `.env` est trouvé
- ⚠️ **Mode démo activé** si le driver n'est pas trouvé
- ❌ **Erreur de connexion** si les identifiants sont incorrects

### **Exemple de sortie réussie :**
```
Configuration chargee depuis le fichier .env
Configuration de la base de donnees:
  Hote: localhost
  Port: 3306
  Base: bibliotheque
  Utilisateur: root
  Mot de passe: ***
  Driver: com.mysql.cj.jdbc.Driver
  URL: jdbc:mysql://localhost:3306/bibliotheque?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
Connexion a la base de donnees etablie avec succes.
Tables de la base de donnees verifiees/creees avec succes.
```

## 🔧 **Configuration MySQL Requise**

### **Installation de MySQL Server**
1. **Windows** : Télécharger depuis https://dev.mysql.com/downloads/mysql/
2. **macOS** : `brew install mysql`
3. **Linux** : `sudo apt install mysql-server`

### **Création de la base de données**
```sql
-- Se connecter à MySQL
mysql -u root -p

-- Créer la base
CREATE DATABASE bibliotheque CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Créer utilisateur (optionnel)
CREATE USER 'biblio_user'@'localhost' IDENTIFIED BY 'votre_mot_de_passe';
GRANT ALL PRIVILEGES ON bibliotheque.* TO 'biblio_user'@'localhost';
FLUSH PRIVILEGES;
```

### **Import des tables**
```bash
mysql -u root -p bibliotheque < database_setup.sql
```

## 🛡️ **Sécurité**

### **Protection des Données**
- ✅ Fichier `.env` ignoré par Git
- ✅ Mot de passe masqué dans les logs
- ✅ Configuration séparée du code source
- ⚠️ **Ne jamais partager** le fichier `.env`

### **Bonnes Pratiques**
- Utilisez un mot de passe fort pour MySQL
- Limitez les privilèges de l'utilisateur
- Activez SSL en production
- Sauvegardez régulièrement votre base de données

## 🔍 **Dépannage**

### **Erreurs Courantes**

#### **Driver non trouvé**
```
AVERTISSEMENT: Driver MySQL non trouve. Mode demo active.
```
**Solution** : Installez MySQL Connector/J et ajoutez-le au classpath

#### **Connexion refusée**
```
AVERTISSEMENT: Impossible de se connecter a la base de donnees. Mode demo active.
Erreur: Access denied for user 'root'@'localhost'
```
**Solution** : Vérifiez les identifiants dans `.env`

#### **Base de données inexistante**
```
Erreur: Unknown database 'bibliotheque'
```
**Solution** : Créez la base avec `database_setup.sql`

### **Test de Connexion**
```java
// Test simple de connexion
import java.sql.*;

public class TestDB {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bibliotheque",
                "root", 
                "votre_mot_de_passe"
            );
            System.out.println("Connexion réussie !");
            conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }
}
```

## 📚 **Documentation Complémentaire**

- `INSTALLATION_DRIVER.md` : Installation détaillée du driver
- `README_BDD.md` : Guide d'utilisation avec base de données
- `database_setup.sql` : Script de création des tables
- `DatabaseConfig.java` : Classe de gestion de configuration

---

**Le système est maintenant configuré pour une utilisation sécurisée et flexible !**
