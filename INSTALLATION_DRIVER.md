# Installation du Driver MySQL Connector/J

## Étape 1 : Télécharger MySQL Connector/J

### Option A : Téléchargement Manuel
1. Allez sur le site officiel MySQL : https://dev.mysql.com/downloads/connector/j/
2. Cliquez sur "MySQL Connector/J"
3. Choisissez la version recommandée (généralement 8.0.x)
4. Sélectionnez "Platform Independent" et téléchargez le fichier ZIP ou TAR.GZ
5. Décompressez le fichier téléchargé

### Option B : Téléchargement Direct (Recommandé)
**Lien direct pour la version 8.0.33 :**
https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-8.0.33.zip

## Étape 2 : Installation

### Pour Windows :
1. Décompressez le fichier ZIP dans un dossier (ex: `C:\mysql-connector`)
2. Localisez le fichier JAR : `mysql-connector-j-8.0.33.jar`

### Pour macOS/Linux :
```bash
# Créer un dossier pour les drivers
mkdir ~/mysql-drivers
cd ~/mysql-drivers

# Télécharger et décompresser
wget https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-8.0.33.tar.gz
tar -xzf mysql-connector-j-8.0.33.tar.gz
```

## Étape 3 : Configuration du Classpath

### Option 1 : Classpath Temporaire (pour tester)

#### Windows :
```cmd
# Méthode 1 : En ligne de commande
javac -cp ".;C:\mysql-connector\mysql-connector-j-8.0.33.jar" *.java
java -cp ".;C:\mysql-connector\mysql-connector-j-8.0.33.jar" Main

# Méthode 2 : Variable d'environnement
set CLASSPATH=.;C:\mysql-connector\mysql-connector-j-8.0.33.jar
javac *.java
java Main
```

#### macOS/Linux :
```bash
# Méthode 1 : En ligne de commande
javac -cp ".:/home/user/mysql-drivers/mysql-connector-j-8.0.33.jar" *.java
java -cp ".:/home/user/mysql-drivers/mysql-connector-j-8.0.33.jar" Main

# Méthode 2 : Variable d'environnement
export CLASSPATH=.:~/mysql-drivers/mysql-connector-j-8.0.33.jar
javac *.java
java Main
```

### Option 2 : Copie dans le projet (plus simple)

1. Copiez le fichier `mysql-connector-j-8.0.33.jar` dans votre dossier de projet :
   ```
   c:\Users\Ken Bugul\Documents\Projet\java\javaFinalProject\
   ```

2. Compilez et exécutez :
   ```cmd
   javac -cp ".;mysql-connector-j-8.0.33.jar" *.java
   java -cp ".;mysql-connector-j-8.0.33.jar" Main
   ```

## Étape 4 : Vérification de l'Installation

### Test de compilation :
```cmd
javac -cp ".;mysql-connector-j-8.0.33.jar" *.java
```

Si vous voyez des erreurs, vérifiez :
- Le chemin vers le fichier JAR est correct
- Le nom du fichier JAR est exact
- Les guillemets sont corrects (Windows utilise `;` comme séparateur)

### Test d'exécution :
```cmd
java -cp ".;mysql-connector-j-8.0.33.jar" Main
```

**Message attendu si tout va bien :**
```
Connexion à la base de données établie avec succès.
Tables de la base de données vérifiées/créées avec succès.
```

## Étape 5 : Configuration de MySQL

### Installation de MySQL Server (si pas déjà installé)

#### Windows :
1. Téléchargez MySQL Installer : https://dev.mysql.com/downloads/mysql/
2. Exécutez l'installateur
3. Choisissez "Developer Default"
4. Configurez le mot de passe root (notez-le !)

#### macOS :
```bash
# Avec Homebrew
brew install mysql

# Démarrer MySQL
brew services start mysql

# Sécuriser l'installation
mysql_secure_installation
```

#### Linux (Ubuntu/Debian) :
```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
sudo mysql_secure_installation
```

### Création de la base de données :
```bash
mysql -u root -p
CREATE DATABASE bibliotheque;
EXIT;
```

## Étape 6 : Script d'Installation Automatique

Créez un fichier `setup.bat` (Windows) ou `setup.sh` (Linux/macOS) :

### setup.bat (Windows) :
```batch
@echo off
echo Installation du Driver MySQL Connector/J...

if not exist "mysql-connector-j-8.0.33.jar" (
    echo Téléchargement du driver...
    powershell -Command "Invoke-WebRequest -Uri 'https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-8.0.33.zip' -OutFile 'mysql-connector.zip'"
    powershell -Command "Expand-Archive 'mysql-connector.zip' -DestinationPath '.'"
    del mysql-connector.zip
)

echo Compilation du projet...
javac -cp ".;mysql-connector-j-8.0.33.jar" *.java

echo.
echo Installation terminée !
echo Pour exécuter : java -cp ".;mysql-connector-j-8.0.33.jar" Main
pause
```

### setup.sh (Linux/macOS) :
```bash
#!/bin/bash
echo "Installation du Driver MySQL Connector/J..."

if [ ! -f "mysql-connector-j-8.0.33.jar" ]; then
    echo "Téléchargement du driver..."
    wget -q https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-8.0.33.tar.gz
    tar -xzf mysql-connector-j-8.0.33.tar.gz
    rm mysql-connector-j-8.0.33.tar.gz
fi

echo "Compilation du projet..."
javac -cp ".:mysql-connector-j-8.0.33.jar" *.java

echo ""
echo "Installation terminée !"
echo "Pour exécuter : java -cp .:mysql-connector-j-8.0.33.jar Main"
```

## Dépannage

### Erreurs courantes :

1. **ClassNotFoundException: com.mysql.cj.jdbc.Driver**
   ```
   Solution : Vérifiez que le JAR est dans le classpath
   ```

2. **No suitable driver found**
   ```
   Solution : Ajoutez le driver au classpath avec -cp
   ```

3. **Access denied for user 'root'@'localhost'**
   ```
   Solution : Vérifiez le mot de passe MySQL
   mysql -u root -p
   ```

4. **Unknown database 'bibliotheque'**
   ```
   Solution : Créez la base de données
   mysql -u root -p < database_setup.sql
   ```

### Vérification finale :
```cmd
# Vérifier que le driver est bien dans le classpath
java -cp ".;mysql-connector-j-8.0.33.jar" -verbose:class Main 2>&1 | findstr mysql
```

Si vous voyez `mysql-connector-j-8.0.33.jar`, le driver est bien trouvé !

---

**Résumé rapide :**
1. Téléchargez mysql-connector-j-8.0.33.jar
2. Placez-le dans le dossier du projet
3. Compilez : `javac -cp ".;mysql-connector-j-8.0.33.jar" *.java`
4. Exécutez : `java -cp ".;mysql-connector-j-8.0.33.jar" Main`
