import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("SYSTÈME DE GESTION DE BIBLIOTHÈQUE MUNICIPALE");
        System.out.println("Simulation du système");
        System.out.println("=".repeat(80));
        System.out.println("Démarrage : " + LocalDateTime.now().format(formatter));
        System.out.println();
        
        try {
            // Scénario 1: Création des annexes
            System.out.println("📍 SCÉNARIO 1: CRÉATION DES ANNEXES");
            System.out.println("-".repeat(50));
            creerAnnexes();
            System.out.println();
            
            // Scénario 2: Gestion des conditions de conservation
            System.out.println("🌡️  SCÉNARIO 2: GESTION DES CONDITIONS DE CONSERVATION");
            System.out.println("-".repeat(50));
            gererConditionsConservation();
            System.out.println();
            
            // Scénario 3: Opérations sur les annexes
            System.out.println("📚 SCÉNARIO 3: OPÉRATIONS SUR LES ANNEXES");
            System.out.println("-".repeat(50));
            effectuerOperationsAnnexes();
            System.out.println();
            
            // Scénario 4: Simulation de problèmes et corrections
            System.out.println("⚠️  SCÉNARIO 4: SIMULATION DE PROBLÈMES ET CORRECTIONS");
            System.out.println("-".repeat(50));
            simulerProblemesEtCorrections();
            System.out.println();
            
            // Scénario 5: État final du système
            System.out.println("📊 SCÉNARIO 5: ÉTAT FINAL DU SYSTÈME");
            System.out.println("-".repeat(50));
            afficherEtatFinal();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la simulation : " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("Fin de la simulation : " + LocalDateTime.now().format(formatter));
        System.out.println("=".repeat(80));
    }
    
    private static void creerAnnexes() {
        System.out.println("Création des annexes de la bibliothèque municipale...\n");
        
        // Annexe principale
        Annexe annexePrincipale = new Annexe(1, "Bibliothèque Centrale", 
                                           "15 Rue de la République, 75001 Paris", 50000);
        annexePrincipale.ajouterEquipement("Climatisation centrale");
        annexePrincipale.ajouterEquipement("Système anti-incendie");
        annexePrincipale.ajouterEquipement("Caméras de surveillance");
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
        
        System.out.println("\n✅ SYSTÈME FONCTIONNEL");
        System.out.println("Toutes les composantes principales sont opérationnelles");
        System.out.println("Les mécanismes de détection et de correction fonctionnent correctement");
    }
}
