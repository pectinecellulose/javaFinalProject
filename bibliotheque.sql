-- ============================================================
--  BIBLIOTHÈQUE MUNICIPALE — Schéma complet SQLite/MySQL
--  Création des tables, contraintes et données de test
-- ============================================================

PRAGMA foreign_keys = ON;

-- ------------------------------------------------------------
-- 1. LIVRE
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS LIVRE (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    isbn             VARCHAR(20)  NOT NULL UNIQUE,
    titre            VARCHAR(200) NOT NULL,
    auteur           VARCHAR(150) NOT NULL,
    genre            VARCHAR(80)  NOT NULL,
    annee_publication SMALLINT    NOT NULL,
    CONSTRAINT chk_annee CHECK (annee_publication BETWEEN 1000 AND 2100)
);

-- ------------------------------------------------------------
-- 2. EXEMPLAIRE
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS EXEMPLAIRE (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    livre_id     INTEGER      NOT NULL,
    numero_serie VARCHAR(30)  NOT NULL UNIQUE,
    etat         VARCHAR(10)  NOT NULL DEFAULT 'bon',
    disponible   BOOLEAN      NOT NULL DEFAULT 1,
    CONSTRAINT fk_ex_livre   FOREIGN KEY (livre_id) REFERENCES LIVRE(id) ON DELETE RESTRICT,
    CONSTRAINT chk_etat      CHECK (etat IN ('bon', 'abime', 'perdu'))
);

-- ------------------------------------------------------------
-- 3. MEMBRE
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS MEMBRE (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    nom              VARCHAR(100) NOT NULL,
    prenom           VARCHAR(100) NOT NULL,
    email            VARCHAR(150) NOT NULL UNIQUE,
    date_inscription DATE         NOT NULL DEFAULT (DATE('now'))
);

-- ------------------------------------------------------------
-- 4. CARTE_MEMBRE
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS CARTE_MEMBRE (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    membre_id        INTEGER     NOT NULL UNIQUE,
    numero           VARCHAR(20) NOT NULL UNIQUE,
    date_expiration  DATE        NOT NULL,
    statut           VARCHAR(12) NOT NULL DEFAULT 'active',
    CONSTRAINT fk_cm_membre  FOREIGN KEY (membre_id) REFERENCES MEMBRE(id) ON DELETE CASCADE,
    CONSTRAINT chk_statut    CHECK (statut IN ('active', 'suspendue', 'expiree'))
);

-- ------------------------------------------------------------
-- 5. EMPRUNT
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS EMPRUNT (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    membre_id      INTEGER NOT NULL,
    exemplaire_id  INTEGER NOT NULL,
    date_emprunt   DATE    NOT NULL DEFAULT (DATE('now')),
    date_limite    DATE    NOT NULL,
    date_retour    DATE             DEFAULT NULL,
    jours_retard   INTEGER          DEFAULT 0,
    CONSTRAINT fk_emp_membre     FOREIGN KEY (membre_id)     REFERENCES MEMBRE(id)     ON DELETE RESTRICT,
    CONSTRAINT fk_emp_exemplaire FOREIGN KEY (exemplaire_id) REFERENCES EXEMPLAIRE(id) ON DELETE RESTRICT,
    CONSTRAINT chk_retard        CHECK (jours_retard >= 0),
    CONSTRAINT chk_dates         CHECK (date_limite > date_emprunt)
);

-- ------------------------------------------------------------
-- 6. AMENDE
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS AMENDE (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    emprunt_id    INTEGER        NOT NULL UNIQUE,
    montant       DECIMAL(10, 2) NOT NULL,
    payee         BOOLEAN        NOT NULL DEFAULT 0,
    date_creation DATE           NOT NULL DEFAULT (DATE('now')),
    CONSTRAINT fk_am_emprunt FOREIGN KEY (emprunt_id) REFERENCES EMPRUNT(id) ON DELETE CASCADE,
    CONSTRAINT chk_montant   CHECK (montant > 0)
);

-- ------------------------------------------------------------
-- 7. PAIEMENT
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS PAIEMENT (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    amende_id      INTEGER        NOT NULL,
    montant        DECIMAL(10, 2) NOT NULL,
    date_paiement  DATE           NOT NULL DEFAULT (DATE('now')),
    mode_paiement  VARCHAR(20)    NOT NULL DEFAULT 'especes',
    CONSTRAINT fk_pa_amende FOREIGN KEY (amende_id) REFERENCES AMENDE(id) ON DELETE CASCADE,
    CONSTRAINT chk_pa_montant  CHECK (montant > 0),
    CONSTRAINT chk_mode        CHECK (mode_paiement IN ('especes', 'carte', 'virement'))
);

-- ------------------------------------------------------------
-- 8. CAPTEUR_ENV
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS CAPTEUR_ENV (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    salle       VARCHAR(100) NOT NULL,
    temperature REAL         NOT NULL,
    humidite    REAL         NOT NULL,
    horodatage  DATETIME     NOT NULL DEFAULT (DATETIME('now')),
    CONSTRAINT chk_temp CHECK (temperature BETWEEN -10 AND 60),
    CONSTRAINT chk_hum  CHECK (humidite    BETWEEN   0 AND 100)
);

-- ------------------------------------------------------------
-- 9. ALERTE_CONSERVATION
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ALERTE_CONSERVATION (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    capteur_id  INTEGER      NOT NULL,
    type_alerte VARCHAR(20)  NOT NULL,
    message     VARCHAR(300) NOT NULL,
    date_alerte DATETIME     NOT NULL DEFAULT (DATETIME('now')),
    resolue     BOOLEAN      NOT NULL DEFAULT 0,
    CONSTRAINT fk_al_capteur   FOREIGN KEY (capteur_id) REFERENCES CAPTEUR_ENV(id) ON DELETE CASCADE,
    CONSTRAINT chk_type_alerte CHECK (type_alerte IN ('temperature', 'humidite', 'combinee'))
);

-- ------------------------------------------------------------
-- 10. VEHICULE
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS VEHICULE (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    immatriculation VARCHAR(20)    NOT NULL UNIQUE,
    kilometrage     DECIMAL(10, 2) NOT NULL DEFAULT 0,
    capacite        SMALLINT       NOT NULL,
    statut          VARCHAR(15)    NOT NULL DEFAULT 'disponible',
    CONSTRAINT chk_km       CHECK (kilometrage >= 0),
    CONSTRAINT chk_capacite CHECK (capacite > 0),
    CONSTRAINT chk_statut_v CHECK (statut IN ('disponible', 'en_livraison', 'maintenance'))
);

-- ------------------------------------------------------------
-- 11. LIVRAISON
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS LIVRAISON (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    vehicule_id         INTEGER      NOT NULL,
    exemplaire_id       INTEGER      NOT NULL,
    annexe_source       VARCHAR(100) NOT NULL,
    annexe_destination  VARCHAR(100) NOT NULL,
    date_livraison      DATE         NOT NULL DEFAULT (DATE('now')),
    statut              VARCHAR(15)  NOT NULL DEFAULT 'planifiee',
    CONSTRAINT fk_li_vehicule    FOREIGN KEY (vehicule_id)   REFERENCES VEHICULE(id)   ON DELETE RESTRICT,
    CONSTRAINT fk_li_exemplaire  FOREIGN KEY (exemplaire_id) REFERENCES EXEMPLAIRE(id) ON DELETE RESTRICT,
    CONSTRAINT chk_statut_l      CHECK (statut IN ('planifiee', 'en_cours', 'livree', 'annulee')),
    CONSTRAINT chk_annexes       CHECK (annexe_source <> annexe_destination)
);


-- ============================================================
--  INDEX pour les recherches fréquentes
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_exemplaire_livre    ON EXEMPLAIRE(livre_id);
CREATE INDEX IF NOT EXISTS idx_exemplaire_dispo    ON EXEMPLAIRE(disponible);
CREATE INDEX IF NOT EXISTS idx_emprunt_membre      ON EMPRUNT(membre_id);
CREATE INDEX IF NOT EXISTS idx_emprunt_exemplaire  ON EMPRUNT(exemplaire_id);
CREATE INDEX IF NOT EXISTS idx_emprunt_retour      ON EMPRUNT(date_retour);
CREATE INDEX IF NOT EXISTS idx_amende_payee        ON AMENDE(payee);
CREATE INDEX IF NOT EXISTS idx_capteur_horodatage  ON CAPTEUR_ENV(horodatage);
CREATE INDEX IF NOT EXISTS idx_alerte_resolue      ON ALERTE_CONSERVATION(resolue);


-- ============================================================
--  DONNÉES DE TEST
-- ============================================================

-- ------------------------------------------------------------
-- Livres
-- ------------------------------------------------------------
INSERT INTO LIVRE (isbn, titre, auteur, genre, annee_publication) VALUES
    ('978-2-07-036024-5', 'Les Misérables',            'Victor Hugo',         'Roman classique',   1862),
    ('978-2-07-040850-4', 'Le Petit Prince',            'Antoine de St-Exupéry','Conte philosophique',1943),
    ('978-2-07-036822-7', 'Germinal',                   'Émile Zola',          'Roman social',      1885),
    ('978-2-07-041239-6', 'L''Étranger',               'Albert Camus',        'Roman existentiel', 1942),
    ('978-2-07-036025-2', 'Madame Bovary',              'Gustave Flaubert',    'Roman réaliste',    1857),
    ('978-2-07-036823-4', 'Le Rouge et le Noir',        'Stendhal',            'Roman psychologique',1830),
    ('978-2-07-054125-8', 'Harry Potter T1',            'J.K. Rowling',        'Fantasy',           1997),
    ('978-2-07-054126-5', 'Harry Potter T2',            'J.K. Rowling',        'Fantasy',           1998);

-- ------------------------------------------------------------
-- Exemplaires (2-3 par livre)
-- ------------------------------------------------------------
INSERT INTO EXEMPLAIRE (livre_id, numero_serie, etat, disponible) VALUES
    (1, 'EX-001-A', 'bon',   1),
    (1, 'EX-001-B', 'abime', 1),
    (2, 'EX-002-A', 'bon',   1),
    (2, 'EX-002-B', 'bon',   0),  -- actuellement emprunté
    (3, 'EX-003-A', 'bon',   1),
    (4, 'EX-004-A', 'bon',   1),
    (4, 'EX-004-B', 'bon',   0),  -- actuellement emprunté
    (5, 'EX-005-A', 'bon',   1),
    (6, 'EX-006-A', 'abime', 1),
    (7, 'EX-007-A', 'bon',   1),
    (7, 'EX-007-B', 'bon',   1),
    (8, 'EX-008-A', 'bon',   1);

-- ------------------------------------------------------------
-- Membres
-- ------------------------------------------------------------
INSERT INTO MEMBRE (nom, prenom, email, date_inscription) VALUES
    ('Diallo',   'Aminata',   'aminata.diallo@email.sn',   '2023-01-15'),
    ('Ndiaye',   'Moussa',    'moussa.ndiaye@email.sn',    '2023-03-22'),
    ('Sow',      'Fatou',     'fatou.sow@email.sn',        '2023-06-10'),
    ('Fall',     'Ibrahim',   'ibrahim.fall@email.sn',     '2024-01-05'),
    ('Traoré',   'Mariama',   'mariama.traore@email.sn',   '2024-04-18'),
    ('Diop',     'Cheikh',    'cheikh.diop@email.sn',      '2024-07-30'),
    ('Ba',       'Aissatou',  'aissatou.ba@email.sn',      '2025-02-11');

-- ------------------------------------------------------------
-- Cartes membres
-- ------------------------------------------------------------
INSERT INTO CARTE_MEMBRE (membre_id, numero, date_expiration, statut) VALUES
    (1, 'CARD-2023-001', '2026-01-15', 'active'),
    (2, 'CARD-2023-002', '2026-03-22', 'suspendue'),  -- amende impayée
    (3, 'CARD-2023-003', '2026-06-10', 'active'),
    (4, 'CARD-2024-001', '2027-01-05', 'active'),
    (5, 'CARD-2024-002', '2027-04-18', 'active'),
    (6, 'CARD-2024-003', '2027-07-30', 'active'),
    (7, 'CARD-2025-001', '2028-02-11', 'active');

-- ------------------------------------------------------------
-- Emprunts
-- ------------------------------------------------------------
INSERT INTO EMPRUNT (membre_id, exemplaire_id, date_emprunt, date_limite, date_retour, jours_retard) VALUES
    -- Emprunts clôturés (retournés à temps)
    (1, 1, '2025-10-01', '2025-10-22', '2025-10-18', 0),
    (3, 3, '2025-11-05', '2025-11-26', '2025-11-20', 0),
    (4, 6, '2025-12-01', '2025-12-22', '2025-12-15', 0),
    -- Emprunts clôturés avec retard
    (2, 9, '2025-09-01', '2025-09-22', '2025-10-10', 18),  -- 18j de retard → amende
    (5, 5, '2025-11-01', '2025-11-22', '2025-12-05', 13),  -- 13j de retard → amende
    -- Emprunts en cours (non rendus)
    (1, 4, '2026-03-01', '2026-03-22', NULL, 0),
    (6, 7, '2026-03-10', '2026-03-31', NULL, 0);

-- ------------------------------------------------------------
-- Amendes (générées par les retards)
-- ------------------------------------------------------------
INSERT INTO AMENDE (emprunt_id, montant, payee, date_creation) VALUES
    (4, 900.00,  0, '2025-10-10'),  -- Moussa : 18j × 50 FCFA = 900 FCFA, non payée → suspension
    (5, 650.00,  1, '2025-12-05');  -- Mariama : 13j × 50 FCFA = 650 FCFA, payée

-- ------------------------------------------------------------
-- Paiements
-- ------------------------------------------------------------
INSERT INTO PAIEMENT (amende_id, montant, date_paiement, mode_paiement) VALUES
    (2, 650.00, '2025-12-07', 'especes');  -- Mariama solde son amende

-- ------------------------------------------------------------
-- Capteurs environnementaux
-- ------------------------------------------------------------
INSERT INTO CAPTEUR_ENV (salle, temperature, humidite, horodatage) VALUES
    ('Salle principale',    21.5, 52.0, '2026-03-30 08:00:00'),
    ('Salle principale',    22.1, 55.3, '2026-03-30 14:00:00'),
    ('Réserve archives',    26.8, 63.5, '2026-03-30 14:00:00'),  -- hors seuil !
    ('Salle de lecture',    20.0, 48.0, '2026-03-30 08:00:00'),
    ('Réserve archives',    24.2, 58.1, '2026-03-31 08:00:00'),
    ('Salle principale',    21.8, 51.0, '2026-04-01 08:00:00');

-- ------------------------------------------------------------
-- Alertes conservation (déclenchées par les mesures hors seuil)
-- ------------------------------------------------------------
INSERT INTO ALERTE_CONSERVATION (capteur_id, type_alerte, message, date_alerte, resolue) VALUES
    (3, 'combinee',
     'Réserve archives : température 26.8°C (seuil 25°C) et humidité 63.5% (seuil 60%) — risque détérioration des ouvrages.',
     '2026-03-30 14:05:00', 0);

-- ------------------------------------------------------------
-- Véhicules
-- ------------------------------------------------------------
INSERT INTO VEHICULE (immatriculation, kilometrage, capacite, statut) VALUES
    ('DK-1234-AB', 45230.50, 50, 'disponible'),
    ('DK-5678-CD', 12800.00, 30, 'maintenance'),
    ('DK-9012-EF', 78500.75, 80, 'disponible');

-- ------------------------------------------------------------
-- Livraisons
-- ------------------------------------------------------------
INSERT INTO LIVRAISON (vehicule_id, exemplaire_id, annexe_source, annexe_destination, date_livraison, statut) VALUES
    (1, 10, 'Annexe Plateau',   'Bibliothèque centrale', '2026-03-28', 'livree'),
    (1, 11, 'Bibliothèque centrale', 'Annexe Médina',    '2026-04-02', 'planifiee'),
    (3, 12, 'Annexe Pikine',    'Bibliothèque centrale', '2026-04-03', 'planifiee');

