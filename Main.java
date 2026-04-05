import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.io.*;
import java.sql.*;

public class Main {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static Scanner scanner;
    private static Connection connection;
    
    public static void main(String[] args) {
        // Configuration pour l'encodage UTF-8
        try {
            System.setOut(new PrintStream(System.out, true, "CP850")); // Windows console
        } catch (UnsupportedEncodingException e) {
            System.err.println("Impossible de configurer l'encodage UTF-8");
        }
        
        scanner = new Scanner(System.in, "CP850");
        
        // Initialisation de la base de données
        if (initialiserBaseDeDonnees()) {
            menuPrincipal();
        } else {
            System.out.println("Impossible de demarrer le systeme sans base de donnees.");
        }
    }
    
    private static boolean initialiserBaseDeDonnees() {
        try {
            // Afficher la configuration actuelle
            DatabaseConfig.displayConfig();
            
            // Chargement du driver MySQL
            Class.forName(DatabaseConfig.getMysqlDriver());
            
            // Connexion à la base de données
            connection = DriverManager.getConnection(
                DatabaseConfig.getJdbcUrl(),
                DatabaseConfig.getDbUser(),
                DatabaseConfig.getDbPassword()
            );
            System.out.println("Connexion a la base de donnees etablie avec succes.");
            
            // Création des tables si elles n'existent pas
            creerTables();
            
            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("AVERTISSEMENT: Driver MySQL non trouve. Mode demo active.");
            System.out.println("Pour activer la base de donnees, installez MySQL Connector/J.");
            connection = null;
            return true; // Continuer en mode demo
        } catch (SQLException e) {
            System.out.println("AVERTISSEMENT: Impossible de se connecter a la base de donnees. Mode demo active.");
            System.out.println("Erreur: " + e.getMessage());
            System.out.println("Verifiez votre configuration dans le fichier .env");
            connection = null;
            return true; // Continuer en mode demo
        }
    }
    
    private static void creerTables() {
        try {
            Statement stmt = connection.createStatement();
            
            // Supprimer toutes les tables existantes
            stmt.executeUpdate("DROP TABLE IF EXISTS actions");
            stmt.executeUpdate("DROP TABLE IF EXISTS equipements");
            stmt.executeUpdate("DROP TABLE IF EXISTS zones_conservation");
            stmt.executeUpdate("DROP TABLE IF EXISTS annexes");
            
            // Recréer les tables avec la structure correcte
            String sqlAnnexes = "CREATE TABLE annexes (" +
                               "id INT PRIMARY KEY, " +
                               "nom VARCHAR(255) NOT NULL, " +
                               "adresse VARCHAR(255) NOT NULL, " +
                               "capacite_max INT NOT NULL, " +
                               "nombre_livres INT DEFAULT 0, " +
                               "est_ouverte BOOLEAN DEFAULT TRUE, " +
                               "date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                               ")";
            stmt.executeUpdate(sqlAnnexes);
            
            // Table des équipements
            String sqlEquipements = "CREATE TABLE equipements (" +
                                   "id INT AUTO_INCREMENT PRIMARY KEY, " +
                                   "id_annexe INT NOT NULL, " +
                                   "nom_equipement VARCHAR(255) NOT NULL, " +
                                   "date_ajout TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                   "FOREIGN KEY (id_annexe) REFERENCES annexes(id)" +
                                   ")";
            stmt.executeUpdate(sqlEquipements);
            
            // Table des actions (journal)
            String sqlActions = "CREATE TABLE actions (" +
                               "id INT AUTO_INCREMENT PRIMARY KEY, " +
                               "type_action VARCHAR(100) NOT NULL, " +
                               "description TEXT NOT NULL, " +
                               "id_annexe INT, " +
                               "date_action TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                               "FOREIGN KEY (id_annexe) REFERENCES annexes(id) ON DELETE SET NULL" +
                               ")";
            stmt.executeUpdate(sqlActions);
            
            // Table des zones de conservation
            String sqlZones = "CREATE TABLE zones_conservation (" +
                             "id INT PRIMARY KEY, " +
                             "nom_zone VARCHAR(255) NOT NULL, " +
                             "temperature DECIMAL(5,2) NOT NULL, " +
                             "humidite DECIMAL(5,2) NOT NULL, " +
                             "luminosite DECIMAL(10,2) NOT NULL, " +
                             "qualite_air VARCHAR(50) NOT NULL, " +
                             "dernier_controle TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                             "est_conforme BOOLEAN DEFAULT FALSE, " +
                             "observations TEXT" +
                             ")";
            stmt.executeUpdate(sqlZones);
            
            // Insérer les données de test
            stmt.executeUpdate("INSERT INTO annexes (id, nom, adresse, capacite_max, nombre_livres, est_ouverte) VALUES " +
                           "(1, 'Bibliotheque Centrale', '15 Rue de la Republique, 75001 Paris', 50000, 35000, TRUE), " +
                           "(2, 'Annexe Nord', '45 Avenue des Champs-Elysees, 75008 Paris', 15000, 12000, TRUE), " +
                           "(3, 'Mediatheque Jeunesse', '8 Rue des Ecoles, 75005 Paris', 8000, 6000, TRUE) " +
                           "ON DUPLICATE KEY UPDATE nom=VALUES(nom), adresse=VALUES(adresse), capacite_max=VALUES(capacite_max)");
            
            stmt.executeUpdate("INSERT INTO equipements (id_annexe, nom_equipement) VALUES " +
                           "(1, 'Climatisation centrale'), " +
                           "(1, 'Systeme anti-incendie'), " +
                           "(1, 'Cameras de surveillance'), " +
                           "(2, 'Climatisation'), " +
                           "(2, 'Detecteurs de fumee'), " +
                           "(3, 'Climatisation'), " +
                           "(3, 'Espace multimedia'), " +
                           "(3, 'Salle d''animation') " +
                           "ON DUPLICATE KEY UPDATE nom_equipement=VALUES(nom_equipement)");
            
            stmt.executeUpdate("INSERT INTO zones_conservation (id, nom_zone, temperature, humidite, luminosite, qualite_air) VALUES " +
                           "(1, 'Zone Collections Rares', 19.5, 48.0, 250.0, 'Excellente'), " +
                           "(2, 'Zone Adultes', 21.0, 52.0, 400.0, 'Bonne'), " +
                           "(3, 'Zone Souterrain', 25.5, 68.0, 1200.0, 'Moyenne') " +
                           "ON DUPLICATE KEY UPDATE nom_zone=VALUES(nom_zone)");
            
            stmt.close();
            System.out.println("Tables de la base de donnees recrées avec succès.");
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création des tables: " + e.getMessage());
        }
    }
    
    // Méthode pour logger les actions dans la base de données
    private static void loggerAction(String typeAction, String description, Integer idAnnexe) {
        if (connection == null) {
            System.out.println("[MODE DEMO] " + typeAction + ": " + description);
            return;
        }
        
        try {
            String sql = "INSERT INTO actions (type_action, description, id_annexe) VALUES (?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, typeAction);
            pstmt.setString(2, description);
            if (idAnnexe != null) {
                pstmt.setInt(3, idAnnexe);
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.executeUpdate();
            pstmt.close();
            
            System.out.println("[ACTION LOGGÉE] " + typeAction + ": " + description);
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du logging de l'action: " + e.getMessage());
        }
    }
    
    public static Connection getConnection() {
        return connection;
    }
    
    public static void menuPrincipal() {
        while (true) {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("SYSTÈME DE GESTION DE BIBLIOTHÈQUE MUNICIPALE");
            System.out.println("=".repeat(80));
            System.out.println("1. Gestion des annexes");
            System.out.println("2. Gestion des conditions de conservation");
            System.out.println("3. Afficher les statistiques globales");
            System.out.println("4. A propos du systeme");
            System.out.println("5. Afficher le journal des actions");
            System.out.println("0. Quitter");
            System.out.print("\nVotre choix : ");
            
            try {
                if (scanner.hasNextLine()) {
                    String ligne = scanner.nextLine();
                    if (ligne.trim().isEmpty()) {
                        continue; // Ignorer les lignes vides
                    }
                    int choix = Integer.parseInt(ligne.trim());
                    
                    switch (choix) {
                    case 1:
                        menuAnnexes();
                        break;
                    case 2:
                        menuConservation();
                        break;
                    case 3:
                        afficherStatistiquesGlobales();
                        break;
                    case 4:
                        afficherAPropos();
                        break;
                    case 5:
                        afficherJournalActions();
                        break;
                    case 0:
                        System.out.println("\nMerci d'avoir utilise le systeme de gestion de bibliotheque !");
                        System.out.println("Fermeture : " + LocalDateTime.now().format(formatter));
                        loggerAction("FERMETURE_SYSTEME", "Fermeture du systeme de gestion", null);
                        if (scanner != null) {
                            scanner.close();
                        }
                        try {
                            if (connection != null) {
                                connection.close();
                            }
                        } catch (SQLException e) {
                            System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
                        }
                        return;
                    default:
                        System.out.println("Choix invalide. Veuillez réessayer.");
                    }
                } else {
                    // Pas d'entrée, sortie du programme
                    return;
                }
            } catch (Exception e) {
                System.out.println("Erreur : " + e.getMessage());
                if (scanner != null) {
                    scanner.nextLine(); // Nettoyer le buffer
                }
            }
        }
    }
    
    private static void menuAnnexes() {
        System.out.println("\nMENU GESTION DES ANNEXES");
        System.out.println("1. Accéder au menu interactif des annexes");
        System.out.println("2. Créer une nouvelle annexe rapidement");
        System.out.println("3. Afficher toutes les annexes existantes");
        System.out.println("4. Opérations rapides sur les livres");
        System.out.println("0. Retour au menu principal");
        System.out.print("Votre choix : ");
        
        try {
            int choix = scanner.nextInt();
            scanner.nextLine();
            
            switch (choix) {
                case 1:
                    Annexe.menuInteractif();
                    break;
                case 2:
                    creerAnnexeRapide();
                    break;
                case 3:
                    afficherToutesLesAnnexes();
                    break;
                case 4:
                    operationsRapidesLivres();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Choix invalide.");
            }
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
            if (scanner != null) {
                scanner.nextLine();
            }
        }
    }
    
    private static void menuConservation() {
        System.out.println("\nMENU GESTION DE LA CONSERVATION");
        System.out.println("1. Accéder au menu interactif des conditions");
        System.out.println("2. Créer une nouvelle zone de conservation");
        System.out.println("3. Contrôle rapide de toutes les zones");
        System.out.println("4. Afficher les normes et recommandations");
        System.out.println("0. Retour au menu principal");
        System.out.print("Votre choix : ");
        
        try {
            int choix = scanner.nextInt();
            scanner.nextLine();
            
            switch (choix) {
                case 1:
                    ConditionConservation.menuInteractif();
                    break;
                case 2:
                    creerZoneRapide();
                    break;
                case 3:
                    controleRapideZones();
                    break;
                case 4:
                    afficherNormes();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Choix invalide.");
            }
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
            if (scanner != null) {
                scanner.nextLine();
            }
        }
    }
    
    private static void creerAnnexeRapide() {
        if (connection == null) {
            System.out.println("Mode demo : Base de donnees non disponible.");
            return;
        }
        
        try {
            System.out.println("\nCREATION RAPIDE D'UNE NOUVELLE ANNEXE");
            System.out.print("ID de l'annexe : ");
            int id = scanner.nextInt();
            scanner.nextLine();
            
            System.out.print("Nom de l'annexe : ");
            String nom = scanner.nextLine();
            
            System.out.print("Adresse : ");
            String adresse = scanner.nextLine();
            
            System.out.print("Capacite maximale : ");
            int capacite = scanner.nextInt();
            scanner.nextLine();
            
            // Insérer dans la base de données
            String sql = "INSERT INTO annexes (id, nom, adresse, capacite_max, nombre_livres, est_ouverte) VALUES (?, ?, ?, ?, 0, true)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setString(2, nom);
            pstmt.setString(3, adresse);
            pstmt.setInt(4, capacite);
            pstmt.executeUpdate();
            pstmt.close();
            
            Annexe nouvelle = new Annexe(id, nom, adresse, capacite);
            System.out.println("Annexe creee avec succes !");
            loggerAction("CREATION_ANNEXE", "Creation de l'annexe '" + nom + "' (ID: " + id + ")", id);
            System.out.println(nouvelle.toString());
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println("Erreur : Une annexe avec cet ID existe deja.");
            } else {
                System.out.println("Erreur lors de la creation : " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la creation : " + e.getMessage());
        }
    }
    
    private static void creerZoneRapide() {
        if (connection == null) {
            System.out.println("Mode demo : Base de donnees non disponible.");
            return;
        }
        
        try {
            System.out.println("\nCREATION RAPIDE D'UNE NOUVELLE ZONE");
            System.out.print("ID de la zone : ");
            int id = scanner.nextInt();
            scanner.nextLine();
            
            System.out.print("Nom de la zone : ");
            String nom = scanner.nextLine();
            
            System.out.print("Temperature (°C) : ");
            double temperature = scanner.nextDouble();
            scanner.nextLine();
            
            System.out.print("Humidite (%) : ");
            double humidite = scanner.nextDouble();
            scanner.nextLine();
            
            System.out.print("Luminosite (lux) : ");
            double luminosite = scanner.nextDouble();
            scanner.nextLine();
            
            System.out.print("Qualite de l'air : ");
            String qualiteAir = scanner.nextLine();
            
            // Insérer dans la base de données
            String sql = "INSERT INTO zones_conservation (id, nom_zone, temperature, humidite, luminosite, qualite_air) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setString(2, nom);
            pstmt.setDouble(3, temperature);
            pstmt.setDouble(4, humidite);
            pstmt.setDouble(5, luminosite);
            pstmt.setString(6, qualiteAir);
            pstmt.executeUpdate();
            pstmt.close();
            
            ConditionConservation nouvelle = new ConditionConservation(id, nom, temperature, humidite, luminosite, qualiteAir);
            System.out.println("Zone creee avec succes !");
            loggerAction("CREATION_ZONE", "Creation de la zone de conservation '" + nom + "' (ID: " + id + ")", null);
            System.out.println(nouvelle.toString());
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println("Erreur : Une zone avec cet ID existe deja.");
            } else {
                System.out.println("Erreur lors de la creation : " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la creation : " + e.getMessage());
        }
    }
    
    private static void afficherToutesLesAnnexes() {
        System.out.println("\nLISTE DES ANNEXES EXISTANTES");
        loggerAction("AFFICHAGE_ANNEXES", "Affichage de la liste de toutes les annexes", null);
        
        if (connection == null) {
            System.out.println("Mode demo : Base de donnees non disponible.");
            return;
        }
        
        try {
            String sql = "SELECT * FROM annexes ORDER BY id";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                Annexe annexe = new Annexe(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("adresse"),
                    rs.getInt("capacite_max")
                );
                annexe.setNombreLivresActuels(rs.getInt("nombre_livres"));
                annexe.setEstOuverte(rs.getBoolean("est_ouverte"));
                System.out.println(annexe.toString());
            }
            
            if (!hasData) {
                System.out.println("Aucune annexe trouvee dans la base de donnees.");
                System.out.println("Utilisez l'option 2 pour creer une nouvelle annexe.");
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage : " + e.getMessage());
        }
    }
    
    private static void operationsRapidesLivres() {
        if (connection == null) {
            System.out.println("Mode demo : Base de donnees non disponible.");
            return;
        }
        
        try {
            System.out.println("\nOPERATIONS RAPIDES SUR LES LIVRES");
            System.out.print("ID de l'annexe : ");
            int id = scanner.nextInt();
            scanner.nextLine();
            
            // Vérifier si l'annexe existe
            String checkSql = "SELECT * FROM annexes WHERE id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                System.out.println("Erreur : Aucune annexe trouvee avec l'ID " + id);
                rs.close();
                checkStmt.close();
                return;
            }
            
            String nom = rs.getString("nom");
            int capacite = rs.getInt("capacite_max");
            int livresActuels = rs.getInt("nombre_livres");
            rs.close();
            checkStmt.close();
            
            System.out.println("\n1. Ajouter des livres");
            System.out.println("2. Retirer des livres");
            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine();
            
            if (choix == 1) {
                System.out.print("Nombre de livres a ajouter : ");
                int quantite = scanner.nextInt();
                scanner.nextLine();
                
                int nouveauTotal = livresActuels + quantite;
                if (nouveauTotal > capacite) {
                    System.out.println("Erreur : Capacite maximale depassee (" + capacite + " livres maximum)");
                    return;
                }
                
                String updateSql = "UPDATE annexes SET nombre_livres = ? WHERE id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setInt(1, nouveauTotal);
                updateStmt.setInt(2, id);
                updateStmt.executeUpdate();
                updateStmt.close();
                
                loggerAction("AJOUT_LIVRES", "Ajout de " + quantite + " livres dans l'annexe '" + nom + "' (ID: " + id + ")", id);
                System.out.println(quantite + " livres ajoutes avec succes. Total : " + nouveauTotal + "/" + capacite);
                
            } else if (choix == 2) {
                System.out.print("Nombre de livres a retirer : ");
                int quantite = scanner.nextInt();
                scanner.nextLine();
                
                if (quantite > livresActuels) {
                    System.out.println("Erreur : Impossible de retirer plus de livres que disponibles (" + livresActuels + ")");
                    return;
                }
                
                int nouveauTotal = livresActuels - quantite;
                String updateSql = "UPDATE annexes SET nombre_livres = ? WHERE id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setInt(1, nouveauTotal);
                updateStmt.setInt(2, id);
                updateStmt.executeUpdate();
                updateStmt.close();
                
                loggerAction("RETRAIT_LIVRES", "Retrait de " + quantite + " livres de l'annexe '" + nom + "' (ID: " + id + ")", id);
                System.out.println(quantite + " livres retires avec succes. Total : " + nouveauTotal + "/" + capacite);
            }
            
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
            if (scanner != null) {
                scanner.nextLine();
            }
        }
    }
    
    private static void controleRapideZones() {
        if (connection == null) {
            System.out.println("Mode demo : Base de donnees non disponible.");
            return;
        }
        
        System.out.println("\nCONTROLE RAPIDE DES ZONES");
        loggerAction("CONTROLE_ZONES", "Controle rapide de toutes les zones de conservation", null);
        
        try {
            String sql = "SELECT * FROM zones_conservation ORDER BY id";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                int id = rs.getInt("id");
                String nom = rs.getString("nom_zone");
                double temperature = rs.getDouble("temperature");
                double humidite = rs.getDouble("humidite");
                double luminosite = rs.getDouble("luminosite");
                String qualiteAir = rs.getString("qualite_air");
                
                ConditionConservation zone = new ConditionConservation(id, nom, temperature, humidite, luminosite, qualiteAir);
                zone.effectuerControle();
                System.out.println("Niveau de risque : " + zone.getNiveauRisque());
                System.out.println();
            }
            
            if (!hasData) {
                System.out.println("Aucune zone de conservation trouvee dans la base de donnees.");
                System.out.println("Utilisez l'option 2 pour creer une nouvelle zone.");
            }
            
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Erreur lors du controle : " + e.getMessage());
        }
    }
    
    private static void afficherNormes() {
        System.out.println("\nNORMES DE CONSERVATION");
        System.out.println("=".repeat(50));
        System.out.println("TEMPERATURE : 16°C - 24°C");
        System.out.println("HUMIDITE RELATIVE : 40% - 60%");
        System.out.println("LUMINOSITE : < 1000 lux");
        System.out.println("QUALITE DE L'AIR : Bonne a Excellente");
        System.out.println("=".repeat(50));
        System.out.println("Ces normes assurent une conservation optimale");
        System.out.println("des documents sur le long terme.");
        loggerAction("AFFICHAGE_NORMES", "Affichage des normes de conservation", null);
    }
    
    private static void afficherStatistiquesGlobales() {
        System.out.println("\nSTATISTIQUES GLOBALES DU SYSTEME");
        System.out.println("=".repeat(50));
        loggerAction("AFFICHAGE_STATISTIQUES", "Affichage des statistiques globales", null);
        
        if (connection == null) {
            System.out.println("Mode demo : Base de donnees non disponible.");
            return;
        }
        
        try {
            // Statistiques des annexes
            String sqlAnnexes = "SELECT COUNT(*) as total_annexes, SUM(capacite_max) as capacite_totale, SUM(nombre_livres) as livres_actuels FROM annexes";
            Statement stmtAnnexes = connection.createStatement();
            ResultSet rsAnnexes = stmtAnnexes.executeQuery(sqlAnnexes);
            
            if (rsAnnexes.next()) {
                int totalAnnexes = rsAnnexes.getInt("total_annexes");
                int capaciteTotale = rsAnnexes.getInt("capacite_totale");
                int livresActuels = rsAnnexes.getInt("livres_actuels");
                
                double tauxRemplissage = capaciteTotale > 0 ? (livresActuels * 100.0 / capaciteTotale) : 0;
                
                System.out.println("Total d'annexes : " + totalAnnexes);
                System.out.println("Capacite totale : " + capaciteTotale + " livres");
                System.out.println("Livres actuels : " + livresActuels);
                System.out.println("Taux de remplissage : " + String.format("%.1f", tauxRemplissage) + "%");
            }
            rsAnnexes.close();
            stmtAnnexes.close();
            
            // Statistiques des zones de conservation
            String sqlZones = "SELECT COUNT(*) as total_zones FROM zones_conservation";
            Statement stmtZones = connection.createStatement();
            ResultSet rsZones = stmtZones.executeQuery(sqlZones);
            
            if (rsZones.next()) {
                int totalZones = rsZones.getInt("total_zones");
                System.out.println("Zones de conservation : " + totalZones);
                
                // Compter les zones conformes
                String sqlConformes = "SELECT COUNT(*) as zones_conformes FROM zones_conservation WHERE temperature BETWEEN 16 AND 24 AND humidite BETWEEN 40 AND 60 AND luminosite < 1000";
                Statement stmtConformes = connection.createStatement();
                ResultSet rsConformes = stmtConformes.executeQuery(sqlConformes);
                
                if (rsConformes.next()) {
                    int zonesConformes = rsConformes.getInt("zones_conformes");
                    System.out.println("Zones conformes : " + zonesConformes + "/" + totalZones);
                }
                rsConformes.close();
                stmtConformes.close();
            }
            rsZones.close();
            stmtZones.close();
            
            // Statistiques des actions
            String sqlActions = "SELECT COUNT(*) as total_actions FROM actions";
            Statement stmtActions = connection.createStatement();
            ResultSet rsActions = stmtActions.executeQuery(sqlActions);
            
            if (rsActions.next()) {
                int totalActions = rsActions.getInt("total_actions");
                System.out.println("Total d'actions loggees : " + totalActions);
            }
            rsActions.close();
            stmtActions.close();
            
            System.out.println("\nSysteme operationnel");
            
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage des statistiques : " + e.getMessage());
        }
    }
    
    private static void afficherAPropos() {
        System.out.println("\nA PROPOS DU SYSTEME");
        System.out.println("=".repeat(50));
        System.out.println("Systeme de Gestion de Bibliotheque Municipale v1.0");
        System.out.println();
        System.out.println("Fonctionnalites principales :");
        System.out.println("• Gestion des annexes (creation, modification, suivi)");
        System.out.println("• Suivi des conditions de conservation");
        System.out.println("• Journalisation des actions");
        System.out.println("• Mode demonstration et production");
        System.out.println();
        System.out.println("Base de donnees : MySQL (optionnel)");
        System.out.println("Mode demo : Disponible sans installation");
        System.out.println();
        System.out.println("Developpe en Java avec gestion complete des erreurs");
        loggerAction("AFFICHAGE_A_PROPOS", "Affichage des informations du systeme", null);
    }
    
    private static void creerAnnexes() {
        System.out.println("Creation des annexes de la bibliotheque municipale...\n");
        
        // Annexe principale
        Annexe annexePrincipale = new Annexe(1, "Bibliotheque Centrale", 
                                           "15 Rue de la Republique, 75001 Paris", 50000);
        annexePrincipale.ajouterEquipement("Climatisation centrale");
        annexePrincipale.ajouterEquipement("Systeme anti-incendie");
        annexePrincipale.ajouterEquipement("Cameras de surveillance");
        annexePrincipale.ajouterLivres(35000);
        System.out.println(annexePrincipale);
        
        // Annexe secondaire
        Annexe annexeSecondaire = new Annexe(2, "Annexe Nord", 
                                            "45 Avenue des Champs-Élysées, 75008 Paris", 15000);
        annexeSecondaire.ajouterEquipement("Climatisation");
        annexeSecondaire.ajouterEquipement("Détecteurs de fumée");
        annexeSecondaire.ajouterLivres(12000);
        System.out.println(annexeSecondaire);
        
        // Annexe spécialisée
        Annexe annexeSpecialisee = new Annexe(3, "Médiathèque Jeunesse", 
                                             "8 Rue des Écoles, 75005 Paris", 8000);
        annexeSpecialisee.ajouterEquipement("Climatisation");
        annexeSpecialisee.ajouterEquipement("Espace multimédia");
        annexeSpecialisee.ajouterEquipement("Salle d'animation");
        annexeSpecialisee.ajouterLivres(6000);
        System.out.println(annexeSpecialisee);
        
        System.out.println("✅ Création des 3 annexes terminée avec succès");
    }
    
    private static void gererConditionsConservation() {
        System.out.println("Mise en place des conditions de conservation...\n");
        
        // Zone principale - conditions optimales
        ConditionConservation zonePrincipale = new ConditionConservation(1, "Zone Collections Rares");
        zonePrincipale.setTemperature(19.5);
        zonePrincipale.setHumidite(48.0);
        zonePrincipale.setLuminosite(250.0);
        zonePrincipale.setQualiteAir("Excellente");
        zonePrincipale.effectuerControle();
        System.out.println(zonePrincipale);
        
        // Zone secondaire - conditions acceptables
        ConditionConservation zoneSecondaire = new ConditionConservation(2, "Zone Adultes");
        zoneSecondaire.setTemperature(21.0);
        zoneSecondaire.setHumidite(52.0);
        zoneSecondaire.setLuminosite(400.0);
        zoneSecondaire.setQualiteAir("Bonne");
        zoneSecondaire.effectuerControle();
        System.out.println(zoneSecondaire);
        
        // Zone problème - conditions dégradées
        ConditionConservation zoneProbleme = new ConditionConservation(3, "Zone Souterrain");
        zoneProbleme.setTemperature(25.5);
        zoneProbleme.setHumidite(68.0);
        zoneProbleme.setLuminosite(1200.0);
        zoneProbleme.setQualiteAir("Moyenne");
        zoneProbleme.effectuerControle();
        System.out.println(zoneProbleme);
        
        System.out.println("✅ Configuration des conditions de conservation terminée");
    }
    
    private static void effectuerOperationsAnnexes() {
        System.out.println("Simulation des opérations quotidiennes...\n");
        
        // Création d'une annexe pour les tests
        Annexe annexeTest = new Annexe(10, "Annexe Test", "123 Rue Test, 75000 Paris", 10000);
        annexeTest.ajouterLivres(8000);
        
        System.out.println("État initial de l'annexe test :");
        System.out.println("Capacité : " + annexeTest.getNombreLivresActuels() + "/" + annexeTest.getCapaciteMax());
        System.out.println("Taux de remplissage : " + String.format("%.1f", annexeTest.getTauxRemplissage()) + "%");
        System.out.println();
        
        // Ajout de livres
        System.out.println("📥 Ajout de livres :");
        annexeTest.ajouterLivres(1500);
        System.out.println();
        
        // Tentative d'ajout qui dépasse la capacité
        System.out.println("📥 Tentative d'ajout excessif :");
        annexeTest.ajouterLivres(1000);
        System.out.println();
        
        // Retrait de livres
        System.out.println("📤 Retrait de livres :");
        annexeTest.retirerLivres(500);
        System.out.println();
        
        // Gestion des équipements
        System.out.println("🔧 Gestion des équipements :");
        annexeTest.ajouterEquipement("Nouveaux ordinateurs");
        annexeTest.ajouterEquipement("Imprimante professionnelle");
        annexeTest.retirerEquipement("Équipement inexistant");
        System.out.println();
        
        // Ouverture/fermeture
        System.out.println("🚪 Gestion des horaires :");
        annexeTest.fermer();
        annexeTest.ajouterLivres(100);
        annexeTest.ouvrir();
        annexeTest.ajouterLivres(100);
        System.out.println();
        
        System.out.println("État final de l'annexe test :");
        System.out.println("Capacité : " + annexeTest.getNombreLivresActuels() + "/" + annexeTest.getCapaciteMax());
        System.out.println("Taux de remplissage : " + String.format("%.1f", annexeTest.getTauxRemplissage()) + "%");
    }
    
    private static void simulerProblemesEtCorrections() {
        System.out.println("Simulation de problèmes et de leurs corrections...\n");
        
        // Problème 1: Température trop élevée
        System.out.println("🌡️  Problème 1: Température excessive");
        ConditionConservation zoneChaleur = new ConditionConservation(4, "Zone Mansarde");
        zoneChaleur.setTemperature(26.8);
        zoneChaleur.setHumidite(45.0);
        zoneChaleur.effectuerControle();
        System.out.println("⚠️  Niveau de risque : " + zoneChaleur.getNiveauRisque());
        System.out.println("💡 Recommandations : " + zoneChaleur.getRecommandations());
        System.out.println();
        
        // Correction
        System.out.println("🔧 Correction du problème :");
        zoneChaleur.setTemperature(20.5);
        zoneChaleur.effectuerControle();
        System.out.println("✅ Niveau de risque après correction : " + zoneChaleur.getNiveauRisque());
        System.out.println();
        
        // Problème 2: Taux d'humidité trop faible
        System.out.println("💧 Problème 2: Humidité insuffisante");
        ConditionConservation zoneSeche = new ConditionConservation(5, "Zone Chaufferie");
        zoneSeche.setTemperature(22.0);
        zoneSeche.setHumidite(32.0);
        zoneSeche.effectuerControle();
        System.out.println("⚠️  Niveau de risque : " + zoneSeche.getNiveauRisque());
        System.out.println("💡 Recommandations : " + zoneSeche.getRecommandations());
        System.out.println();
        
        // Correction
        System.out.println("🔧 Correction du problème :");
        zoneSeche.setHumidite(50.0);
        zoneSeche.effectuerControle();
        System.out.println("✅ Niveau de risque après correction : " + zoneSeche.getNiveauRisque());
        System.out.println();
        
        // Problème 3: Annexe pleine
        System.out.println("📚 Problème 3: Annexe à capacité maximale");
        Annexe annexePleine = new Annexe(11, "Petite Annexe", "1 Rue Petitesse, 75000 Paris", 1000);
        annexePleine.ajouterLivres(1000);
        System.out.println("État : " + (annexePleine.estPleine() ? "PLEINE" : "Non pleine"));
        System.out.println("Capacité disponible : " + annexePleine.getCapaciteDisponible() + " livres");
        annexePleine.ajouterLivres(10);
        System.out.println();
        
        // Solution: transfert vers autre annexe
        System.out.println("🚚 Solution: Transfert vers une autre annexe");
        Annexe annexeDestination = new Annexe(12, "Annexe Réception", "2 Rue Réception, 75000 Paris", 2000);
        annexeDestination.ajouterLivres(500);
        annexePleine.retirerLivres(200);
        annexeDestination.ajouterLivres(200);
        
        System.out.println("Après transfert :");
        System.out.println("Annexe source - Capacité disponible : " + annexePleine.getCapaciteDisponible() + " livres");
        System.out.println("Annexe destination - Capacité disponible : " + annexeDestination.getCapaciteDisponible() + " livres");
    }
    
    private static void afficherEtatFinal() {
        System.out.println("Récapitulatif de l'état actuel du système :\n");
        
        // Création d'un état final consolidé
        Annexe[] annexes = {
            new Annexe(1, "Bibliothèque Centrale", "15 Rue de la République", 50000),
            new Annexe(2, "Annexe Nord", "45 Avenue des Champs-Élysées", 15000),
            new Annexe(3, "Médiathèque Jeunesse", "8 Rue des Écoles", 8000)
        };
        
        // Remplissage avec des valeurs réalistes
        annexes[0].ajouterLivres(45000);
        annexes[1].ajouterLivres(13500);
        annexes[2].ajouterLivres(7200);
        
        ConditionConservation[] zones = {
            new ConditionConservation(1, "Collections Rares", 19.5, 48.0, 250.0, "Excellente"),
            new ConditionConservation(2, "Zone Adultes", 21.0, 52.0, 400.0, "Bonne"),
            new ConditionConservation(3, "Zone Jeunesse", 20.5, 50.0, 350.0, "Bonne")
        };
        
        // Affichage des statistiques
        int totalLivres = 0;
        int capaciteTotale = 0;
        int zonesConformes = 0;
        
        System.out.println("📊 STATISTIQUES GLOBALES");
        System.out.println("-".repeat(30));
        
        for (Annexe annexe : annexes) {
            totalLivres += annexe.getNombreLivresActuels();
            capaciteTotale += annexe.getCapaciteMax();
            System.out.println("• " + annexe.getNom() + ": " + 
                             annexe.getNombreLivresActuels() + "/" + annexe.getCapaciteMax() + 
                             " livres (" + String.format("%.1f", annexe.getTauxRemplissage()) + "%)");
        }
        
        for (ConditionConservation zone : zones) {
            if (zone.estConforme()) zonesConformes++;
        }
        
        System.out.println("\n📈 RÉSUMÉ");
        System.out.println("-".repeat(30));
        System.out.println("Total de livres gérés : " + totalLivres);
        System.out.println("Capacité totale : " + capaciteTotale);
        System.out.println("Taux de remplissage global : " + 
                         String.format("%.1f", (double) totalLivres / capaciteTotale * 100) + "%");
        System.out.println("Nombre d'annexes : " + annexes.length);
        System.out.println("Zones de conservation : " + zones.length);
        System.out.println("Zones conformes : " + zonesConformes + "/" + zones.length);
        
        System.out.println("\nSYSTÈME FONCTIONNEL");
        System.out.println("Toutes les composantes principales sont opérationnelles");
        System.out.println("Les mécanismes de détection et de correction fonctionnent correctement");
    }
    
    private static void afficherJournalActions() {
        System.out.println("\nJOURNAL DES ACTIONS");
        System.out.println("=".repeat(80));
        
        if (connection == null) {
            System.out.println("Mode démo : Base de données non disponible.");
            System.out.println("Pour activer le journal, installez MySQL Connector/J et configurez la base de données.");
            System.out.println("\nAppuyez sur Entrée pour continuer...");
            scanner.nextLine();
            return;
        }
        
        try {
            String sql = "SELECT * FROM actions ORDER BY date_action DESC LIMIT 50";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println("ID | Type Action | Description | ID Annexe | Date");
            System.out.println("-".repeat(80));
            
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                int id = rs.getInt("id");
                String typeAction = rs.getString("type_action");
                String description = rs.getString("description");
                Integer idAnnexe = rs.getInt("id_annexe");
                if (rs.wasNull()) idAnnexe = null;
                Timestamp dateAction = rs.getTimestamp("date_action");
                
                System.out.printf("%-3d | %-12s | %-30s | %-8s | %s%n",
                    id, typeAction, description, 
                    idAnnexe != null ? idAnnexe.toString() : "N/A",
                    dateAction.toString());
            }
            
            if (!hasResults) {
                System.out.println("Aucune action enregistrée dans le journal.");
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage du journal: " + e.getMessage());
        }
        
        System.out.println("\nAppuyez sur Entrée pour continuer...");
        try {
            scanner.nextLine(); // Attendre l'entrée utilisateur
        } catch (Exception e) {
            // Ignorer si pas d'entrée disponible
        }
    }
}
