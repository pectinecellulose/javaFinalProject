import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.*;

public class Annexe {
    private int id;
    private String nom;
    private String adresse;
    private int capaciteMax;
    private int nombreLivresActuels;
    private boolean estOuverte;
    private List<String> equipements;
    private static Connection connection;
    
    public Annexe(int id, String nom, String adresse, int capaciteMax) {
        if (id <= 0) {
            throw new IllegalArgumentException("L'ID de l'annexe doit être positif");
        }
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'annexe ne peut pas être vide");
        }
        if (adresse == null || adresse.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse de l'annexe ne peut pas être vide");
        }
        if (capaciteMax <= 0) {
            throw new IllegalArgumentException("La capacité maximale doit être positive");
        }
        
        this.id = id;
        this.nom = nom.trim();
        this.adresse = adresse.trim();
        this.capaciteMax = capaciteMax;
        this.nombreLivresActuels = 0;
        this.estOuverte = true;
        this.equipements = new ArrayList<>();
    }
    
    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getAdresse() { return adresse; }
    public int getCapaciteMax() { return capaciteMax; }
    public int getNombreLivresActuels() { return nombreLivresActuels; }
    public boolean estOuverte() { return estOuverte; }
    public List<String> getEquipements() { return new ArrayList<>(equipements); }
    
    // Setters avec validation
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'annexe ne peut pas être vide");
        }
        this.nom = nom.trim();
    }
    
    public void setAdresse(String adresse) {
        if (adresse == null || adresse.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse de l'annexe ne peut pas être vide");
        }
        this.adresse = adresse.trim();
    }
    
    public void setCapaciteMax(int capaciteMax) {
        if (capaciteMax <= 0) {
            throw new IllegalArgumentException("La capacité maximale doit être positive");
        }
        if (capaciteMax < nombreLivresActuels) {
            throw new IllegalArgumentException("La capacité maximale ne peut pas être inférieure au nombre actuel de livres");
        }
        this.capaciteMax = capaciteMax;
    }
    
    public void setNombreLivresActuels(int nombreLivresActuels) {
        if (nombreLivresActuels < 0) {
            throw new IllegalArgumentException("Le nombre de livres ne peut pas être négatif");
        }
        if (nombreLivresActuels > capaciteMax) {
            throw new IllegalArgumentException("Le nombre de livres ne peut pas dépasser la capacité maximale");
        }
        this.nombreLivresActuels = nombreLivresActuels;
    }
    
    public void setEstOuverte(boolean estOuverte) {
        this.estOuverte = estOuverte;
    }
    
    // Méthode pour définir la connexion à la base de données
    public static void setConnection(Connection conn) {
        connection = conn;
    }
    
    // Méthode pour logger les actions
    private static void loggerAction(String typeAction, String description, Integer idAnnexe) {
        if (connection == null) return;
        
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
            
        } catch (SQLException e) {
            System.err.println("Erreur lors du logging de l'action: " + e.getMessage());
        }
    }
    
    // Méthodes de gestion
    public void ouvrir() {
        this.estOuverte = true;
        System.out.println("L'annexe \"" + nom + "\" est maintenant ouverte.");
        loggerAction("OUVERTURE_ANNEXE", "Ouverture de l'annexe '" + nom + "'", id);
    }
    
    public void fermer() {
        this.estOuverte = false;
        System.out.println("L'annexe \"" + nom + "\" est maintenant fermée.");
        loggerAction("FERMETURE_ANNEXE", "Fermeture de l'annexe '" + nom + "'", id);
    }
    
    public boolean ajouterLivres(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité de livres à ajouter doit être positive");
        }
        if (!estOuverte) {
            System.out.println("Impossible d'ajouter des livres : l'annexe \"" + nom + "\" est fermée.");
            return false;
        }
        if (nombreLivresActuels + quantite > capaciteMax) {
            System.out.println("Impossible d'ajouter " + quantite + " livres : capacité maximale dépassée.");
            System.out.println("Capacité actuelle : " + nombreLivresActuels + "/" + capaciteMax);
            return false;
        }
        
        nombreLivresActuels += quantite;
        System.out.println(quantite + " livre(s) ajouté(s) à l'annexe \"" + nom + "\".");
        System.out.println("Nouveau total : " + nombreLivresActuels + "/" + capaciteMax);
        loggerAction("AJOUT_LIVRES", "Ajout de " + quantite + " livres dans l'annexe '" + nom + "'", id);
        return true;
    }
    
    public boolean retirerLivres(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité de livres à retirer doit être positive");
        }
        if (!estOuverte) {
            System.out.println("Impossible de retirer des livres : l'annexe \"" + nom + "\" est fermée.");
            return false;
        }
        if (nombreLivresActuels < quantite) {
            System.out.println("Impossible de retirer " + quantite + " livres : stock insuffisant.");
            System.out.println("Stock actuel : " + nombreLivresActuels + " livres");
            return false;
        }
        
        nombreLivresActuels -= quantite;
        System.out.println(quantite + " livre(s) retiré(s) de l'annexe \"" + nom + "\".");
        System.out.println("Nouveau total : " + nombreLivresActuels + "/" + capaciteMax);
        loggerAction("RETRAIT_LIVRES", "Retrait de " + quantite + " livres de l'annexe '" + nom + "'", id);
        return true;
    }
    
    public void ajouterEquipement(String equipement) {
        if (equipement == null || equipement.trim().isEmpty()) {
            throw new IllegalArgumentException("L'équipement ne peut pas être vide");
        }
        String equipementTrim = equipement.trim();
        if (!equipements.contains(equipementTrim)) {
            equipements.add(equipementTrim);
            System.out.println("Équipement \"" + equipementTrim + "\" ajouté à l'annexe \"" + nom + "\".");
            loggerAction("AJOUT_EQUIPEMENT", "Ajout de l'équipement '" + equipementTrim + "' à l'annexe '" + nom + "'", id);
        } else {
            System.out.println("L'équipement \"" + equipementTrim + "\" est déjà présent dans l'annexe \"" + nom + "\".");
        }
    }
    
    public void retirerEquipement(String equipement) {
        if (equipement == null || equipement.trim().isEmpty()) {
            throw new IllegalArgumentException("L'équipement ne peut pas être vide");
        }
        String equipementTrim = equipement.trim();
        if (equipements.remove(equipementTrim)) {
            System.out.println("Équipement \"" + equipementTrim + "\" retiré de l'annexe \"" + nom + "\".");
            loggerAction("RETRAIT_EQUIPEMENT", "Retrait de l'équipement '" + equipementTrim + "' de l'annexe '" + nom + "'", id);
        } else {
            System.out.println("L'équipement \"" + equipementTrim + "\" n'existe pas dans l'annexe \"" + nom + "\".");
        }
    }
    
    public double getTauxRemplissage() {
        return (double) nombreLivresActuels / capaciteMax * 100;
    }
    
    public boolean estPleine() {
        return nombreLivresActuels >= capaciteMax;
    }
    
    public int getCapaciteDisponible() {
        return capaciteMax - nombreLivresActuels;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ANNEXE ").append(id).append(" ===\n");
        sb.append("Nom : ").append(nom).append("\n");
        sb.append("Adresse : ").append(adresse).append("\n");
        sb.append("État : ").append(estOuverte ? "Ouverte" : "Fermée").append("\n");
        sb.append("Capacité : ").append(nombreLivresActuels).append("/").append(capaciteMax)
          .append(" (").append(String.format("%.1f", getTauxRemplissage())).append("%)\n");
        sb.append("Capacité disponible : ").append(getCapaciteDisponible()).append(" livres\n");
        if (!equipements.isEmpty()) {
            sb.append("Équipements : ").append(String.join(", ", equipements)).append("\n");
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Annexe annexe = (Annexe) obj;
        return id == annexe.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    // Menu interactif pour la gestion de l'annexe
    public static void menuInteractif() {
        Scanner scanner = new Scanner(System.in);
        Annexe annexe = creerExemple();
        
        // Définir la connexion pour cette classe
        if (Main.getConnection() != null) {
            setConnection(Main.getConnection());
        }
        
        while (true) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("MENU GESTION D'ANNEXE");
            System.out.println("=".repeat(60));
            System.out.println("1. Afficher les informations de l'annexe");
            System.out.println("2. Ajouter des livres");
            System.out.println("3. Retirer des livres");
            System.out.println("4. Ajouter un équipement");
            System.out.println("5. Retirer un équipement");
            System.out.println("6. Ouvrir/Fermer l'annexe");
            System.out.println("7. Modifier les informations");
            System.out.println("8. Afficher les statistiques");
            System.out.println("9. Créer une nouvelle annexe");
            System.out.println("0. Retour au menu principal");
            System.out.print("Votre choix : ");
            
            try {
                int choix = scanner.nextInt();
                scanner.nextLine(); // Consommer la ligne
                
                switch (choix) {
                    case 1:
                        System.out.println(annexe.toString());
                        break;
                    case 2:
                        System.out.print("Nombre de livres à ajouter : ");
                        int nbAjout = scanner.nextInt();
                        scanner.nextLine();
                        annexe.ajouterLivres(nbAjout);
                        break;
                    case 3:
                        System.out.print("Nombre de livres à retirer : ");
                        int nbRetrait = scanner.nextInt();
                        scanner.nextLine();
                        annexe.retirerLivres(nbRetrait);
                        break;
                    case 4:
                        System.out.print("Nom de l'équipement à ajouter : ");
                        String equipAjout = scanner.nextLine();
                        annexe.ajouterEquipement(equipAjout);
                        break;
                    case 5:
                        System.out.print("Nom de l'équipement à retirer : ");
                        String equipRetrait = scanner.nextLine();
                        annexe.retirerEquipement(equipRetrait);
                        break;
                    case 6:
                        if (annexe.estOuverte()) {
                            annexe.fermer();
                        } else {
                            annexe.ouvrir();
                        }
                        break;
                    case 7:
                        modifierInformations(annexe, scanner);
                        break;
                    case 8:
                        afficherStatistiques(annexe);
                        break;
                    case 9:
                        annexe = creerNouvelleAnnexe(scanner);
                        break;
                    case 0:
                        System.out.println("Retour au menu principal...");
                        return;
                    default:
                        System.out.println("Choix invalide. Veuillez réessayer.");
                }
            } catch (Exception e) {
                System.out.println("❌ Erreur : " + e.getMessage());
                scanner.nextLine(); // Nettoyer le buffer
            }
        }
    }
    
    // Crée un exemple d'annexe pour démonstration
    public static Annexe creerExemple() {
        Annexe exemple = new Annexe(1, "Bibliothèque Centrale", 
                                   "15 Rue de la République, 75001 Paris", 50000);
        exemple.ajouterEquipement("Climatisation centrale");
        exemple.ajouterEquipement("Système anti-incendie");
        exemple.ajouterEquipement("Caméras de surveillance");
        exemple.ajouterLivres(35000);
        System.out.println("✅ Annexe exemple créée : Bibliothèque Centrale");
        return exemple;
    }
    
    // Crée une nouvelle annexe avec saisie utilisateur
    private static Annexe creerNouvelleAnnexe(Scanner scanner) {
        try {
            System.out.println("\n📝 Création d'une nouvelle annexe");
            System.out.print("ID de l'annexe : ");
            int id = scanner.nextInt();
            scanner.nextLine();
            
            System.out.print("Nom de l'annexe : ");
            String nom = scanner.nextLine();
            
            System.out.print("Adresse : ");
            String adresse = scanner.nextLine();
            
            System.out.print("Capacité maximale : ");
            int capacite = scanner.nextInt();
            scanner.nextLine();
            
            Annexe nouvelle = new Annexe(id, nom, adresse, capacite);
            System.out.println("✅ Nouvelle annexe créée avec succès !");
            return nouvelle;
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la création : " + e.getMessage());
            return creerExemple();
        }
    }
    
    // Modifie les informations de l'annexe
    private static void modifierInformations(Annexe annexe, Scanner scanner) {
        System.out.println("\n✏️  Modification des informations");
        System.out.println("1. Modifier le nom");
        System.out.println("2. Modifier l'adresse");
        System.out.println("3. Modifier la capacité maximale");
        System.out.print("Votre choix : ");
        
        try {
            int choix = scanner.nextInt();
            scanner.nextLine();
            
            switch (choix) {
                case 1:
                    System.out.print("Nouveau nom : ");
                    String nouveauNom = scanner.nextLine();
                    annexe.setNom(nouveauNom);
                    break;
                case 2:
                    System.out.print("Nouvelle adresse : ");
                    String nouvelleAdresse = scanner.nextLine();
                    annexe.setAdresse(nouvelleAdresse);
                    break;
                case 3:
                    System.out.print("Nouvelle capacité maximale : ");
                    int nouvelleCapacite = scanner.nextInt();
                    scanner.nextLine();
                    annexe.setCapaciteMax(nouvelleCapacite);
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur : " + e.getMessage());
            scanner.nextLine();
        }
    }
    
    // Affiche les statistiques détaillées
    private static void afficherStatistiques(Annexe annexe) {
        System.out.println("\n📊 STATISTIQUES DÉTAILLÉES");
        System.out.println("-".repeat(40));
        System.out.println("ID : " + annexe.getId());
        System.out.println("Nom : " + annexe.getNom());
        System.out.println("Adresse : " + annexe.getAdresse());
        System.out.println("État : " + (annexe.estOuverte() ? "🟢 OUVERTE" : "🔴 FERMÉE"));
        System.out.println("Livres : " + annexe.getNombreLivresActuels() + "/" + annexe.getCapaciteMax());
        System.out.println("Taux de remplissage : " + String.format("%.1f", annexe.getTauxRemplissage()) + "%");
        System.out.println("Capacité disponible : " + annexe.getCapaciteDisponible() + " livres");
        System.out.println("Nombre d'équipements : " + annexe.getEquipements().size());
        System.out.println("Statut : " + (annexe.estPleine() ? "⚠️  PLEINE" : "✅ DISPONIBLE"));
        
        if (!annexe.getEquipements().isEmpty()) {
            System.out.println("\n🔧 Équipements :");
            for (String equip : annexe.getEquipements()) {
                System.out.println("  • " + equip);
            }
        }
    }
}
