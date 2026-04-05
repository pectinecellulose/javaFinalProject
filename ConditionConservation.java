import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.sql.*;

public class ConditionConservation {
    private int id;
    private String nomZone;
    private double temperature;
    private double humidite;
    private double luminosite;
    private String qualiteAir;
    private LocalDateTime dernierControle;
    private boolean estConforme;
    private String observations;
    private static Connection connection;
    
    private static final double TEMP_MIN = 16.0;
    private static final double TEMP_MAX = 24.0;
    private static final double HUMIDITE_MIN = 40.0;
    private static final double HUMIDITE_MAX = 60.0;
    
    public ConditionConservation(int id, String nomZone) {
        if (id <= 0) {
            throw new IllegalArgumentException("L'ID doit être positif");
        }
        if (nomZone == null || nomZone.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la zone ne peut pas être vide");
        }
        
        this.id = id;
        this.nomZone = nomZone.trim();
        this.temperature = 20.0;
        this.humidite = 50.0;
        this.luminosite = 300.0;
        this.qualiteAir = "Bonne";
        this.dernierControle = LocalDateTime.now();
        this.estConforme = true;
        this.observations = "";
    }
    
    public ConditionConservation(int id, String nomZone, double temperature, double humidite, 
                               double luminosite, String qualiteAir) {
        this(id, nomZone);
        setTemperature(temperature);
        setHumidite(humidite);
        setLuminosite(luminosite);
        setQualiteAir(qualiteAir);
        verifierConformite();
    }
    
    // Getters
    public int getId() { return id; }
    public String getNomZone() { return nomZone; }
    public double getTemperature() { return temperature; }
    public double getHumidite() { return humidite; }
    public double getLuminosite() { return luminosite; }
    public String getQualiteAir() { return qualiteAir; }
    public LocalDateTime getDernierControle() { return dernierControle; }
    public boolean estConforme() { return estConforme; }
    public String getObservations() { return observations; }
    
    // Setters avec validation
    public void setTemperature(double temperature) {
        if (temperature < -50 || temperature > 60) {
            throw new IllegalArgumentException("Température hors limites acceptables (-50°C à 60°C)");
        }
        this.temperature = temperature;
        verifierConformite();
    }
    
    public void setHumidite(double humidite) {
        if (humidite < 0 || humidite > 100) {
            throw new IllegalArgumentException("Humidité doit être entre 0% et 100%");
        }
        this.humidite = humidite;
        verifierConformite();
    }
    
    public void setLuminosite(double luminosite) {
        if (luminosite < 0) {
            throw new IllegalArgumentException("La luminosité ne peut pas être négative");
        }
        this.luminosite = luminosite;
        verifierConformite();
    }
    
    public void setQualiteAir(String qualiteAir) {
        if (qualiteAir == null || qualiteAir.trim().isEmpty()) {
            throw new IllegalArgumentException("La qualité de l'air ne peut pas être vide");
        }
        this.qualiteAir = qualiteAir.trim();
        verifierConformite();
    }
    
    public void setObservations(String observations) {
        this.observations = observations != null ? observations.trim() : "";
    }
    
    private void verifierConformite() {
        StringBuilder nouvellesObservations = new StringBuilder();
        
        if (temperature < TEMP_MIN || temperature > TEMP_MAX) {
            nouvellesObservations.append("Température hors normes (")
                .append(TEMP_MIN).append("°C - ").append(TEMP_MAX).append("°C); ");
        }
        
        if (humidite < HUMIDITE_MIN || humidite > HUMIDITE_MAX) {
            nouvellesObservations.append("Humidité hors normes (")
                .append(HUMIDITE_MIN).append("% - ").append(HUMIDITE_MAX).append("%); ");
        }
        
        if (luminosite > 1000) {
            nouvellesObservations.append("Luminosité excessive (>1000 lux); ");
        }
        
        if (!qualiteAir.equalsIgnoreCase("Bonne") && !qualiteAir.equalsIgnoreCase("Excellente")) {
            nouvellesObservations.append("Qualité d'air insuffisante; ");
        }
        
        this.observations = nouvellesObservations.toString();
        this.estConforme = this.observations.isEmpty();
        this.dernierControle = LocalDateTime.now();
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
    
    public void effectuerControle() {
        verifierConformite();
        System.out.println("Contrôle effectué pour la zone \"" + nomZone + "\"");
        System.out.println("Conformité : " + (estConforme ? "CONFORME" : "NON CONFORME"));
        if (!estConforme) {
            System.out.println("Observations : " + observations);
        }
        loggerAction("CONTROLE_ZONE", "Contrôle de la zone '" + nomZone + "' - " + (estConforme ? "CONFORME" : "NON CONFORME"), null);
    }
    
    public String getNiveauRisque() {
        int alertes = 0;
        
        if (temperature < TEMP_MIN - 2 || temperature > TEMP_MAX + 2) alertes++;
        if (humidite < HUMIDITE_MIN - 5 || humidite > HUMIDITE_MAX + 5) alertes++;
        if (luminosite > 1500) alertes++;
        if (qualiteAir.equalsIgnoreCase("Mauvaise")) alertes++;
        
        if (alertes == 0) return "FAIBLE";
        if (alertes <= 2) return "MOYEN";
        return "ÉLEVÉ";
    }
    
    public String getRecommandations() {
        StringBuilder recommandations = new StringBuilder();
        
        if (temperature < TEMP_MIN) {
            recommandations.append("Augmenter la température; ");
        } else if (temperature > TEMP_MAX) {
            recommandations.append("Baisser la température; ");
        }
        
        if (humidite < HUMIDITE_MIN) {
            recommandations.append("Augmenter l'humidité; ");
        } else if (humidite > HUMIDITE_MAX) {
            recommandations.append("Réduire l'humidité; ");
        }
        
        if (luminosite > 1000) {
            recommandations.append("Réduire la luminosité; ");
        }
        
        if (!qualiteAir.equalsIgnoreCase("Bonne") && !qualiteAir.equalsIgnoreCase("Excellente")) {
            recommandations.append("Améliorer la qualité de l'air; ");
        }
        
        return recommandations.length() > 0 ? recommandations.toString() : "Aucune action requise";
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== CONDITIONS DE CONSERVATION ").append(id).append(" ===\n");
        sb.append("Zone : ").append(nomZone).append("\n");
        sb.append("Température : ").append(String.format("%.1f", temperature)).append("°C\n");
        sb.append("Humidité : ").append(String.format("%.1f", humidite)).append("%\n");
        sb.append("Luminosité : ").append(String.format("%.0f", luminosite)).append(" lux\n");
        sb.append("Qualité air : ").append(qualiteAir).append("\n");
        sb.append("Conformité : ").append(estConforme ? "CONFORME" : "NON CONFORME").append("\n");
        sb.append("Niveau risque : ").append(getNiveauRisque()).append("\n");
        sb.append("Dernier contrôle : ").append(dernierControle.toString()).append("\n");
        if (!observations.isEmpty()) {
            sb.append("Observations : ").append(observations).append("\n");
        }
        sb.append("Recommandations : ").append(getRecommandations()).append("\n");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ConditionConservation that = (ConditionConservation) obj;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    // Menu interactif pour la gestion des conditions de conservation
    public static void menuInteractif() {
        Scanner scanner = new Scanner(System.in);
        ConditionConservation condition = creerExemple();
        
        // Définir la connexion pour cette classe
        if (Main.getConnection() != null) {
            setConnection(Main.getConnection());
        }
        
        while (true) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("MENU CONDITIONS DE CONSERVATION");
            System.out.println("=".repeat(60));
            System.out.println("1. Afficher les conditions actuelles");
            System.out.println("2. Modifier la température");
            System.out.println("3. Modifier l'humidité");
            System.out.println("4. Modifier la luminosité");
            System.out.println("5. Modifier la qualité de l'air");
            System.out.println("6. Effectuer un contrôle complet");
            System.out.println("7. Afficher les recommandations");
            System.out.println("8. Afficher le niveau de risque");
            System.out.println("9. Créer une nouvelle zone");
            System.out.println("10. Voir les normes de conservation");
            System.out.println("0. Retour au menu principal");
            System.out.print("Votre choix : ");
            
            try {
                int choix = scanner.nextInt();
                scanner.nextLine(); // Consommer la ligne
                
                switch (choix) {
                    case 1:
                        System.out.println(condition.toString());
                        break;
                    case 2:
                        System.out.print("Nouvelle température (°C) : ");
                        double nouvelleTemp = scanner.nextDouble();
                        scanner.nextLine();
                        condition.setTemperature(nouvelleTemp);
                        System.out.println("✅ Température mise à jour : " + nouvelleTemp + "°C");
                        break;
                    case 3:
                        System.out.print("Nouvelle humidité (%) : ");
                        double nouvelleHumidite = scanner.nextDouble();
                        scanner.nextLine();
                        condition.setHumidite(nouvelleHumidite);
                        System.out.println("✅ Humidité mise à jour : " + nouvelleHumidite + "%");
                        break;
                    case 4:
                        System.out.print("Nouvelle luminosité (lux) : ");
                        double nouvelleLuminosite = scanner.nextDouble();
                        scanner.nextLine();
                        condition.setLuminosite(nouvelleLuminosite);
                        System.out.println("✅ Luminosité mise à jour : " + nouvelleLuminosite + " lux");
                        break;
                    case 5:
                        System.out.print("Nouvelle qualité de l'air : ");
                        String nouvelleQualite = scanner.nextLine();
                        condition.setQualiteAir(nouvelleQualite);
                        System.out.println("✅ Qualité de l'air mise à jour : " + nouvelleQualite);
                        break;
                    case 6:
                        condition.effectuerControle();
                        break;
                    case 7:
                        System.out.println("\n💡 RECOMMANDATIONS :");
                        System.out.println(condition.getRecommandations());
                        break;
                    case 8:
                        System.out.println("\n⚠️  NIVEAU DE RISQUE : " + condition.getNiveauRisque());
                        afficherDetailsRisque(condition);
                        break;
                    case 9:
                        condition = creerNouvelleZone(scanner);
                        break;
                    case 10:
                        afficherNormes();
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
    
    // Crée un exemple de condition pour démonstration
    public static ConditionConservation creerExemple() {
        ConditionConservation exemple = new ConditionConservation(1, "Zone Collections Rares");
        exemple.setTemperature(19.5);
        exemple.setHumidite(48.0);
        exemple.setLuminosite(250.0);
        exemple.setQualiteAir("Excellente");
        System.out.println("✅ Zone exemple créée : Collections Rares (température optimale)");
        return exemple;
    }
    
    // Crée une nouvelle zone avec saisie utilisateur
    private static ConditionConservation creerNouvelleZone(Scanner scanner) {
        try {
            System.out.println("\n📝 Création d'une nouvelle zone de conservation");
            System.out.print("ID de la zone : ");
            int id = scanner.nextInt();
            scanner.nextLine();
            
            System.out.print("Nom de la zone : ");
            String nom = scanner.nextLine();
            
            System.out.print("Température initiale (°C) : ");
            double temperature = scanner.nextDouble();
            scanner.nextLine();
            
            System.out.print("Humidité initiale (%) : ");
            double humidite = scanner.nextDouble();
            scanner.nextLine();
            
            System.out.print("Luminosité initiale (lux) : ");
            double luminosite = scanner.nextDouble();
            scanner.nextLine();
            
            System.out.print("Qualité de l'air : ");
            String qualiteAir = scanner.nextLine();
            
            ConditionConservation nouvelle = new ConditionConservation(id, nom, temperature, humidite, luminosite, qualiteAir);
            System.out.println("✅ Nouvelle zone créée avec succès !");
            return nouvelle;
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la création : " + e.getMessage());
            return creerExemple();
        }
    }
    
    // Affiche les détails du niveau de risque
    private static void afficherDetailsRisque(ConditionConservation condition) {
        System.out.println("\n📊 ANALYSE DE RISQUE DÉTAILLÉE");
        System.out.println("-".repeat(40));
        
        // Analyse température
        if (condition.temperature < TEMP_MIN) {
            System.out.println("🌡️  Température trop basse : " + condition.temperature + "°C < " + TEMP_MIN + "°C");
        } else if (condition.temperature > TEMP_MAX) {
            System.out.println("🌡️  Température trop élevée : " + condition.temperature + "°C > " + TEMP_MAX + "°C");
        } else {
            System.out.println("✅ Température normale : " + condition.temperature + "°C");
        }
        
        // Analyse humidité
        if (condition.humidite < HUMIDITE_MIN) {
            System.out.println("💧 Humidité trop basse : " + condition.humidite + "% < " + HUMIDITE_MIN + "%");
        } else if (condition.humidite > HUMIDITE_MAX) {
            System.out.println("💧 Humidité trop élevée : " + condition.humidite + "% > " + HUMIDITE_MAX + "%");
        } else {
            System.out.println("✅ Humidité normale : " + condition.humidite + "%");
        }
        
        // Analyse luminosité
        if (condition.luminosite > 1000) {
            System.out.println("💡 Luminosité excessive : " + condition.luminosite + " lux > 1000 lux");
        } else {
            System.out.println("✅ Luminosité normale : " + condition.luminosite + " lux");
        }
        
        // Analyse qualité air
        if (condition.qualiteAir.equalsIgnoreCase("Excellente") || condition.qualiteAir.equalsIgnoreCase("Bonne")) {
            System.out.println("🌬️  Qualité de l'air correcte : " + condition.qualiteAir);
        } else {
            System.out.println("⚠️  Qualité de l'air insuffisante : " + condition.qualiteAir);
        }
        
        System.out.println("\n🕐 Dernier contrôle : " + condition.dernierControle.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }
    
    // Affiche les normes de conservation
    private static void afficherNormes() {
        System.out.println("\n📋 NORMES DE CONSERVATION DES DOCUMENTS");
        System.out.println("=".repeat(50));
        System.out.println("🌡️  TEMPÉRATURE : " + TEMP_MIN + "°C - " + TEMP_MAX + "°C");
        System.out.println("   • Température idéale : 18-20°C");
        System.out.println("   • Risques : Moisissure (>24°C), Fragilisation (<16°C)");
        System.out.println();
        System.out.println("💧 HUMIDITÉ RELATIVE : " + HUMIDITE_MIN + "% - " + HUMIDITE_MAX + "%");
        System.out.println("   • Humidité idéale : 45-55%");
        System.out.println("   • Risques : Moisissure (>60%), Sécheresse (<40%)");
        System.out.println();
        System.out.println("💡 LUMINOSITÉ : < 1000 lux");
        System.out.println("   • Luminosité idéale : 200-500 lux");
        System.out.println("   • Risques : Décoloration (>1000 lux)");
        System.out.println();
        System.out.println("🌬️  QUALITÉ DE L'AIR : Bonne à Excellente");
        System.out.println("   • Polluants à éviter : SO₂, NOx, O₃");
        System.out.println("   • Ventilation régulière recommandée");
        System.out.println();
        System.out.println("🕐 FRÉQUENCE DES CONTRÔLES :");
        System.out.println("   • Quotidien : Température et humidité");
        System.out.println("   • Hebdomadaire : Luminosité et qualité de l'air");
        System.out.println("   • Mensuel : Contrôle complet et maintenance");
    }
}
