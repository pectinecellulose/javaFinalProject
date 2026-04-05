-- Base de données pour le système de gestion de bibliothèque
-- Créer la base de données si elle n'existe pas
CREATE DATABASE IF NOT EXISTS bibliotheque;
USE bibliotheque;

-- Table des annexes
CREATE TABLE IF NOT EXISTS annexes (
    id INT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    adresse VARCHAR(255) NOT NULL,
    capacite_max INT NOT NULL,
    nombre_livres INT DEFAULT 0,
    est_ouverte BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX (id)
);

-- Table des équipements
CREATE TABLE IF NOT EXISTS equipements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_annexe INT NOT NULL,
    nom_equipement VARCHAR(255) NOT NULL,
    date_ajout TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_annexe) REFERENCES annexes(id) ON DELETE CASCADE,
    INDEX (id_annexe)
);

-- Table des actions (journal)
CREATE TABLE IF NOT EXISTS actions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type_action VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    id_annexe INT,
    date_action TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_annexe) REFERENCES annexes(id) ON DELETE SET NULL,
    INDEX (date_action),
    INDEX (type_action)
);

-- Table des zones de conservation
CREATE TABLE IF NOT EXISTS zones_conservation (
    id INT PRIMARY KEY,
    nom_zone VARCHAR(255) NOT NULL,
    temperature DOUBLE DEFAULT 20.0,
    humidite DOUBLE DEFAULT 50.0,
    luminosite DOUBLE DEFAULT 300.0,
    qualite_air VARCHAR(100) DEFAULT 'Bonne',
    est_conforme BOOLEAN DEFAULT TRUE,
    dernier_controle TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    observations TEXT,
    INDEX (id)
);

-- Insérer quelques données de test
INSERT INTO annexes (id, nom, adresse, capacite_max, nombre_livres_actuels) VALUES
(1, 'Bibliothèque Centrale', '15 Rue de la République, 75001 Paris', 50000, 35000),
(2, 'Annexe Nord', '45 Avenue des Champs-Élysées, 75008 Paris', 15000, 12000),
(3, 'Médiathèque Jeunesse', '8 Rue des Écoles, 75005 Paris', 8000, 6000)
ON DUPLICATE KEY UPDATE nom=VALUES(nom), adresse=VALUES(adresse);

INSERT INTO equipements (id_annexe, nom_equipement) VALUES
(1, 'Climatisation centrale'),
(1, 'Système anti-incendie'),
(1, 'Caméras de surveillance'),
(2, 'Climatisation'),
(2, 'Détecteurs de fumée'),
(3, 'Climatisation'),
(3, 'Espace multimédia'),
(3, 'Salle d''animation')
ON DUPLICATE KEY UPDATE nom_equipement=VALUES(nom_equipement);

INSERT INTO zones_conservation (id, nom_zone, temperature, humidite, luminosite, qualite_air) VALUES
(1, 'Zone Collections Rares', 19.5, 48.0, 250.0, 'Excellente'),
(2, 'Zone Adultes', 21.0, 52.0, 400.0, 'Bonne'),
(3, 'Zone Souterrain', 25.5, 68.0, 1200.0, 'Moyenne')
ON DUPLICATE KEY UPDATE nom_zone=VALUES(nom_zone);

-- Afficher un résumé
SELECT 'Base de données initialisée avec succès' AS message;
