-- Script pour recréer les tables avec la structure correcte
DROP TABLE IF EXISTS actions;
DROP TABLE IF EXISTS equipements;
DROP TABLE IF EXISTS zones_conservation;
DROP TABLE IF EXISTS annexes;

-- Recréer la table annexes avec la structure correcte
CREATE TABLE annexes (
    id INT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    adresse VARCHAR(255) NOT NULL,
    capacite_max INT NOT NULL,
    nombre_livres INT DEFAULT 0,
    est_ouverte BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX (id)
);

-- Recréer les autres tables
CREATE TABLE equipements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_annexe INT NOT NULL,
    nom_equipement VARCHAR(255) NOT NULL,
    date_ajout TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_annexe) REFERENCES annexes(id) ON DELETE CASCADE,
    INDEX (id_annexe)
);

CREATE TABLE actions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type_action VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    id_annexe INT,
    date_action TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_annexe) REFERENCES annexes(id) ON DELETE SET NULL,
    INDEX (id_annexe, date_action)
);

CREATE TABLE zones_conservation (
    id INT PRIMARY KEY,
    nom_zone VARCHAR(255) NOT NULL,
    temperature DECIMAL(5,2) NOT NULL,
    humidite DECIMAL(5,2) NOT NULL,
    luminosite DECIMAL(10,2) NOT NULL,
    qualite_air VARCHAR(50) NOT NULL,
    dernier_controle TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    est_conforme BOOLEAN DEFAULT FALSE,
    observations TEXT,
    INDEX (id)
);

-- Réinsérer les données de test
INSERT INTO annexes (id, nom, adresse, capacite_max, nombre_livres, est_ouverte) VALUES
(1, 'Bibliotheque Centrale', '15 Rue de la Republique, 75001 Paris', 50000, 35000, TRUE),
(2, 'Annexe Nord', '45 Avenue des Champs-Elysees, 75008 Paris', 15000, 12000, TRUE),
(3, 'Mediatheque Jeunesse', '8 Rue des Ecoles, 75005 Paris', 8000, 6000, TRUE)
ON DUPLICATE KEY UPDATE nom=VALUES(nom), adresse=VALUES(adresse), capacite_max=VALUES(capacite_max);

INSERT INTO equipements (id_annexe, nom_equipement) VALUES
(1, 'Climatisation centrale'),
(1, 'Systeme anti-incendie'),
(1, 'Cameras de surveillance'),
(2, 'Climatisation'),
(2, 'Detecteurs de fumee'),
(3, 'Climatisation'),
(3, 'Espace multimedia'),
(3, 'Salle d''animation')
ON DUPLICATE KEY UPDATE nom_equipement=VALUES(nom_equipement);

INSERT INTO zones_conservation (id, nom_zone, temperature, humidite, luminosite, qualite_air) VALUES
(1, 'Zone Collections Rares', 19.5, 48.0, 250.0, 'Excellente'),
(2, 'Zone Adultes', 21.0, 52.0, 400.0, 'Bonne'),
(3, 'Zone Souterrain', 25.5, 68.0, 1200.0, 'Moyenne')
ON DUPLICATE KEY UPDATE nom_zone=VALUES(nom_zone);

SELECT 'Tables recrées avec succès' AS message;
