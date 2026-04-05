import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * SYSTÈME DE GESTION — BIBLIOTHÈQUE MUNICIPALE
 * Classe Main — Simulation d'un scénario réaliste complet
 *
 * SCÉNARIO SIMULÉ (10 étapes) :
 *  0. Initialisation de la base de données
 *  1. Création des annexes
 *  2. Ajout de livres au catalogue
 *  3. Ajout d'exemplaires au stock
 *  4. Enregistrement de membres
 *  5. Emprunt de livres + tentative invalide (exception)
 *  6. Retour en retard → génération automatique d'une amende
 *  7. Paiement de l'amende → mise à jour de la caisse
 *  8. Livraison inter-annexes par véhicule
 *  9. Relevé et surveillance des conditions de conservation
 * 10. Rapport final — état global du système
 */
public class Main {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {

        banner("SYSTÈME DE GESTION — BIBLIOTHÈQUE MUNICIPALE",
               "Simulation d'un scénario d'utilisation réaliste et complet");

        // ══════════════════════════════════════════════════
        // ÉTAPE 0 — Initialisation de la base de données
        // ══════════════════════════════════════════════════
        section("ÉTAPE 0 — Connexion & initialisation de la base de données");

        DatabaseManager db = DatabaseManager.getInstance();
        db.initialiserSchema();
        System.out.println("  ✔ Connexion établie (SQLite/MySQL).");
        System.out.println("  ✔ Schéma créé : livres, membres, exemplaires, emprunts,");
        System.out.println("                  amendes, caisse, vehicules, livraisons,");
        System.out.println("                  annexes, capteurs_conditions.");
        etatBD(db);

        // ══════════════════════════════════════════════════
        // ÉTAPE 1 — Création des annexes
        // ══════════════════════════════════════════════════
        section("ÉTAPE 1 — Création des annexes");

        Annexe annexeCentrale = new Annexe("Bibliothèque Centrale", "1 Place de la République");
        Annexe annexeNord     = new Annexe("Annexe Nord",           "45 Avenue des Lilas");
        Annexe annexeSud      = new Annexe("Annexe Sud",            "12 Rue Victor Hugo");

        db.sauvegarder(annexeCentrale);
        db.sauvegarder(annexeNord);
        db.sauvegarder(annexeSud);

        System.out.println("  ✔ " + annexeCentrale);
        System.out.println("  ✔ " + annexeNord);
        System.out.println("  ✔ " + annexeSud);
        etatBD(db);

        // ══════════════════════════════════════════════════
        // ÉTAPE 2 — Ajout de livres au catalogue
        // ══════════════════════════════════════════════════
        section("ÉTAPE 2 — Ajout de livres au catalogue");

        Livre livre1 = new Livre("978-2-07-036822-8", "Le Petit Prince",  "Antoine de Saint-Exupéry", "Conte",           1943);
        Livre livre2 = new Livre("978-2-07-040850-4", "L'Étranger",       "Albert Camus",             "Roman",           1942);
        Livre livre3 = new Livre("978-2-07-041239-6", "Les Misérables",   "Victor Hugo",              "Roman historique",1862);
        Livre livre4 = new Livre("978-2-07-036024-6", "Madame Bovary",    "Gustave Flaubert",         "Roman",           1857);

        db.sauvegarder(livre1);
        db.sauvegarder(livre2);
        db.sauvegarder(livre3);
        db.sauvegarder(livre4);

        System.out.println("  ✔ " + livre1);
        System.out.println("  ✔ " + livre2);
        System.out.println("  ✔ " + livre3);
        System.out.println("  ✔ " + livre4);
        afficherCatalogue(db);

        // ══════════════════════════════════════════════════
        // ÉTAPE 3 — Ajout d'exemplaires au stock
        // ══════════════════════════════════════════════════
        section("ÉTAPE 3 — Ajout d'exemplaires au stock");

        Stock stock = Stock.getInstance();

        Exemplaire ex1 = new Exemplaire(livre1, annexeCentrale, EtatExemplaire.DISPONIBLE);
        Exemplaire ex2 = new Exemplaire(livre1, annexeCentrale, EtatExemplaire.DISPONIBLE);
        Exemplaire ex3 = new Exemplaire(livre2, annexeCentrale, EtatExemplaire.DISPONIBLE);
        Exemplaire ex4 = new Exemplaire(livre3, annexeNord,     EtatExemplaire.DISPONIBLE);
        Exemplaire ex5 = new Exemplaire(livre4, annexeSud,      EtatExemplaire.EN_REPARATION);

        stock.ajouterExemplaire(ex1);
        stock.ajouterExemplaire(ex2);
        stock.ajouterExemplaire(ex3);
        stock.ajouterExemplaire(ex4);
        stock.ajouterExemplaire(ex5);

        System.out.println("  ✔ " + stock.getNombreExemplaires() + " exemplaires ajoutés.");
        afficherStock(stock);

        // ══════════════════════════════════════════════════
        // ÉTAPE 4 — Enregistrement des membres
        // ══════════════════════════════════════════════════
        section("ÉTAPE 4 — Enregistrement des membres");

        Membre aminata  = new Membre("Diallo",  "Aminata",  "aminata.diallo@email.sn",  "+221 77 123 4567");
        Membre ibrahima = new Membre("Ndiaye",  "Ibrahima", "ibrahima.ndiaye@email.sn", "+221 78 987 6543");
        Membre fatou    = new Membre("Sow",     "Fatou",    "fatou.sow@email.sn",       "+221 76 555 0011");

        db.sauvegarder(aminata);
        db.sauvegarder(ibrahima);
        db.sauvegarder(fatou);

        System.out.println("  ✔ " + aminata);
        System.out.println("  ✔ " + ibrahima);
        System.out.println("  ✔ " + fatou);
        afficherMembres(db);

        // ══════════════════════════════════════════════════
        // ÉTAPE 5a — Emprunt normal (Aminata)
        // ══════════════════════════════════════════════════
        section("ÉTAPE 5a — Emprunt normal (Aminata emprunte « Le Petit Prince »)");

        LocalDate dateEmprunt1    = LocalDate.now().minusDays(20);
        LocalDate dateRetourPrevue1 = dateEmprunt1.plusDays(14);
        Emprunt emprunt1 = null;

        try {
            emprunt1 = stock.emprunter(aminata, livre1, dateEmprunt1);
            db.sauvegarder(emprunt1);
            System.out.println("  ✔ Emprunt enregistré :");
            System.out.println("     Membre      : " + aminata.getNomComplet());
            System.out.println("     Livre       : " + livre1.getTitre());
            System.out.println("     Exemplaire  : " + emprunt1.getExemplaire().getId());
            System.out.println("     Emprunté le : " + dateEmprunt1.format(FMT));
            System.out.println("     Retour prévu: " + dateRetourPrevue1.format(FMT));
        } catch (LivreNonDisponibleException e) {
            System.err.println("  ✘ Erreur inattendue : " + e.getMessage());
        }

        // ══════════════════════════════════════════════════
        // ÉTAPE 5b — Second emprunt normal (Ibrahima)
        // ══════════════════════════════════════════════════
        section("ÉTAPE 5b — Second emprunt normal (Ibrahima emprunte « L'Étranger »)");

        LocalDate dateEmprunt2 = LocalDate.now().minusDays(5);
        try {
            Emprunt emprunt2 = stock.emprunter(ibrahima, livre2, dateEmprunt2);
            db.sauvegarder(emprunt2);
            System.out.println("  ✔ Emprunt enregistré pour " + ibrahima.getNomComplet()
                    + " — retour prévu le " + dateEmprunt2.plusDays(14).format(FMT));
        } catch (LivreNonDisponibleException e) {
            System.err.println("  ✘ " + e.getMessage());
        }

        // ══════════════════════════════════════════════════
        // ÉTAPE 5c — Tentative invalide (EN_REPARATION)
        // ══════════════════════════════════════════════════
        section("ÉTAPE 5c — Tentative d'emprunt invalide (exemplaire EN_REPARATION)");

        try {
            Emprunt empruntInvalide = stock.emprunter(fatou, livre4, LocalDate.now());
            db.sauvegarder(empruntInvalide);
            System.out.println("  ✘ Aucune exception levée — comportement incorrect !");
        } catch (LivreNonDisponibleException e) {
            System.out.println("  ✔ Exception attendue capturée : " + e.getMessage());
        }

        afficherStock(stock);
        afficherEmpruntsEnCours(db);

        // ══════════════════════════════════════════════════
        // ÉTAPE 6 — Retour en retard → amende
        // ══════════════════════════════════════════════════
        section("ÉTAPE 6 — Retour du livre en retard (Aminata)");

        LocalDate dateRetourReelle = LocalDate.now();
        Caisse caisse = Caisse.getInstance();
        Amende amende = null;

        if (emprunt1 != null) {
            try {
                amende = stock.retournerExemplaire(emprunt1, dateRetourReelle);
                db.mettreAJour(emprunt1);
                if (amende != null) {
                    db.sauvegarder(amende);
                    System.out.println("  ✔ Retour enregistré — RETARD détecté !");
                    System.out.println("     Date retour réelle : " + dateRetourReelle.format(FMT));
                    System.out.println("     Date retour prévue : " + dateRetourPrevue1.format(FMT));
                    System.out.println("     Jours de retard    : " + amende.getJoursRetard());
                    System.out.printf( "     Montant amende     : %.2f FCFA%n", amende.getMontant());
                    System.out.println("     Statut amende      : " + amende.getStatut());
                } else {
                    System.out.println("  ✔ Retour dans les délais — aucune amende.");
                }
            } catch (EmpruntIntrouvableException e) {
                System.err.println("  ✘ Erreur retour : " + e.getMessage());
            }
        }

        afficherStock(stock);
        afficherEmpruntsEnCours(db);

        // ══════════════════════════════════════════════════
        // ÉTAPE 7 — Paiement de l'amende
        // ══════════════════════════════════════════════════
        if (amende != null) {
            section("ÉTAPE 7 — Paiement de l'amende par Aminata");
            try {
                caisse.encaisserAmende(amende);
                db.mettreAJour(amende);
                System.out.printf("  ✔ Paiement accepté : %.2f FCFA encaissés.%n", amende.getMontant());
                System.out.println("  ✔ Statut amende    : " + amende.getStatut());
            } catch (AmendeDejaPaYeeException e) {
                System.out.println("  ✔ Exception attendue : " + e.getMessage());
            }

            System.out.println("\n  → Tentative de double paiement (gestion d'erreur) :");
            try {
                caisse.encaisserAmende(amende);
            } catch (AmendeDejaPaYeeException e) {
                System.out.println("  ✔ Exception capturée : " + e.getMessage());
            }

            afficherCaisse(caisse);
        }

        // ══════════════════════════════════════════════════
        // ÉTAPE 8 — Livraison inter-annexes
        // ══════════════════════════════════════════════════
        section("ÉTAPE 8 — Livraison inter-annexes");

        Vehicule vehicule = new Vehicule("DK-1247-AB", "Renault Master 2022");
        db.sauvegarder(vehicule);
        System.out.println("  ✔ Véhicule enregistré : " + vehicule);

        Livraison livraison = new Livraison(vehicule, ex4, annexeNord, annexeCentrale, LocalDate.now());
        try {
            vehicule.effectuerLivraison(livraison);
            db.sauvegarder(livraison);
            System.out.println("  ✔ Livraison effectuée :");
            System.out.println("     Exemplaire     : " + ex4.getLivre().getTitre() + " (ID " + ex4.getId() + ")");
            System.out.println("     Départ         : " + annexeNord.getNom());
            System.out.println("     Arrivée        : " + annexeCentrale.getNom());
            System.out.println("     Nouvelle loc.  : " + ex4.getLocalisation().getNom());
        } catch (VehiculeIndisponibleException e) {
            System.err.println("  ✘ " + e.getMessage());
        }

        afficherStock(stock);

        // ══════════════════════════════════════════════════
        // ÉTAPE 9 — Conditions de conservation
        // ══════════════════════════════════════════════════
        section("ÉTAPE 9 — Surveillance des conditions de conservation");

        SenseurCondition sc1 = new SenseurCondition("Température", 21.0, "°C",  "Salle A",  annexeCentrale);
        SenseurCondition sc2 = new SenseurCondition("Humidité",    55.0, "%",   "Salle A",  annexeCentrale);
        SenseurCondition sc3 = new SenseurCondition("Luminosité",  150.0,"lux", "Salle B",  annexeCentrale);
        SenseurCondition sc4 = new SenseurCondition("Température", 36.5, "°C",  "Réserve",  annexeNord); // ⚠ ALERTE
        SenseurCondition sc5 = new SenseurCondition("Humidité",    82.0, "%",   "Réserve",  annexeNord); // ⚠ ALERTE

        for (SenseurCondition sc : List.of(sc1, sc2, sc3, sc4, sc5)) {
            db.sauvegarder(sc);
            afficherCondition(sc);
        }

        // ══════════════════════════════════════════════════
        // ÉTAPE 10 — Rapport final
        // ══════════════════════════════════════════════════
        section("ÉTAPE 10 — RAPPORT FINAL : État global du système");
        afficherRapportFinal(db, stock, caisse);

        System.out.println();
        banner("FIN DE LA SIMULATION",
               "Toutes les opérations ont été exécutées et persistées en base.");

        db.fermerConnexion();
    }

    // ══════════════════════════════════════════════════
    // MÉTHODES D'AFFICHAGE
    // ══════════════════════════════════════════════════

    private static void banner(String ligne1, String ligne2) {
        String bord = "═".repeat(66);
        System.out.println("\n╔" + bord + "╗");
        System.out.printf("║  %-64s║%n", ligne1);
        System.out.printf("║  %-64s║%n", ligne2);
        System.out.println("╚" + bord + "╝\n");
    }

    private static void section(String titre) {
        System.out.println("\n┌── " + titre + " " + "─".repeat(Math.max(0, 65 - titre.length())));
    }

    private static void etatBD(DatabaseManager db) {
        System.out.println("  [BD] Objets persistés total : " + db.compterTousLesObjets());
    }

    private static void afficherCatalogue(DatabaseManager db) {
        List<Livre> livres = db.findAll(Livre.class);
        System.out.println("\n  ┌─ CATALOGUE (" + livres.size() + " livre(s)) ──────────────────────────────────");
        System.out.printf("  │ %-18s %-32s %-22s %s%n", "ISBN", "Titre", "Auteur", "Année");
        System.out.println("  │ " + "─".repeat(78));
        for (Livre l : livres)
            System.out.printf("  │ %-18s %-32s %-22s %d%n",
                    l.getIsbn(), l.getTitre(), l.getAuteur(), l.getAnneePublication());
        System.out.println("  └────────────────────────────────────────────────────────────────────────");
    }

    private static void afficherStock(Stock stock) {
        List<Exemplaire> liste = stock.getTousLesExemplaires();
        System.out.println("\n  ┌─ STOCK (" + liste.size() + " exemplaire(s)"
                + " | dispos: "    + stock.getNombreDisponibles()
                + " | empruntés: " + stock.getNombreEmpruntes()
                + " | réparation: "+ stock.getNombreEnReparation() + ") ────");
        System.out.printf("  │ %-4s %-30s %-20s %-15s%n", "ID", "Titre", "Annexe", "État");
        System.out.println("  │ " + "─".repeat(72));
        for (Exemplaire ex : liste)
            System.out.printf("  │ %-4d %-30s %-20s %-15s%n",
                    ex.getId(),
                    ex.getLivre().getTitre(),
                    ex.getLocalisation().getNom(),
                    ex.getEtat());
        System.out.println("  └────────────────────────────────────────────────────────────────────────");
    }

    private static void afficherMembres(DatabaseManager db) {
        List<Membre> membres = db.findAll(Membre.class);
        System.out.println("\n  ┌─ MEMBRES (" + membres.size() + ") ──────────────────────────────────────────");
        System.out.printf("  │ %-4s %-15s %-15s %-30s%n", "ID", "Nom", "Prénom", "Email");
        System.out.println("  │ " + "─".repeat(66));
        for (Membre m : membres)
            System.out.printf("  │ %-4d %-15s %-15s %-30s%n",
                    m.getId(), m.getNom(), m.getPrenom(), m.getEmail());
        System.out.println("  └────────────────────────────────────────────────────────────────────────");
    }

    private static void afficherEmpruntsEnCours(DatabaseManager db) {
        List<Emprunt> liste = db.findEmpruntsEnCours();
        System.out.println("\n  ┌─ EMPRUNTS EN COURS (" + liste.size() + ") ────────────────────────────────");
        if (liste.isEmpty()) {
            System.out.println("  │ (aucun emprunt actif)");
        } else {
            System.out.printf("  │ %-4s %-18s %-28s %-12s %-12s%n",
                    "ID", "Membre", "Livre", "Emprunté", "Dû le");
            System.out.println("  │ " + "─".repeat(78));
            for (Emprunt e : liste)
                System.out.printf("  │ %-4d %-18s %-28s %-12s %-12s%n",
                        e.getId(),
                        e.getMembre().getNomComplet(),
                        e.getExemplaire().getLivre().getTitre(),
                        e.getDateEmprunt().format(FMT),
                        e.getDateRetourPrevue().format(FMT));
        }
        System.out.println("  └────────────────────────────────────────────────────────────────────────");
    }

    private static void afficherCaisse(Caisse caisse) {
        System.out.println("\n  ┌─ CAISSE ───────────────────────────────────────────────────────────");
        System.out.printf("  │ Solde actuel              : %10.2f FCFA%n", caisse.getSolde());
        System.out.printf("  │ Total encaissé (amendes)  : %10.2f FCFA%n", caisse.getTotalEncaisse());
        System.out.printf("  │ Nombre de transactions    : %10d%n",      caisse.getNombreTransactions());
        System.out.println("  └────────────────────────────────────────────────────────────────────────");
    }

    private static void afficherCondition(SenseurCondition s) {
        String icone  = s.estEnAlerte() ? "⚠ ALERTE" : "✔ Normal";
        String valeur = String.format("%.1f %s", s.getValeur(), s.getUnite());
        System.out.printf("  │ [%s] %s : %-10s — Zone %-12s → %s%n",
                s.getAnnexe().getNom(), s.getType(), valeur, s.getZone(), icone);
    }

    private static void afficherRapportFinal(DatabaseManager db, Stock stock, Caisse caisse) {
        long alertes = db.findAll(SenseurCondition.class).stream()
                .filter(SenseurCondition::estEnAlerte).count();
        System.out.println();
        System.out.println("  ╔══ SYNTHÈSE ════════════════════════════════════════════════════════╗");
        System.out.printf( "  ║  Livres au catalogue  : %-4d                                      ║%n", db.findAll(Livre.class).size());
        System.out.printf( "  ║  Exemplaires (total)  : %-4d  dispos: %-3d  empruntés: %-3d         ║%n", stock.getNombreExemplaires(), stock.getNombreDisponibles(), stock.getNombreEmpruntes());
        System.out.printf( "  ║  Membres enregistrés  : %-4d                                      ║%n", db.findAll(Membre.class).size());
        System.out.printf( "  ║  Emprunts en cours    : %-4d  (total: %-3d)                        ║%n", db.findEmpruntsEnCours().size(), db.findAll(Emprunt.class).size());
        System.out.printf( "  ║  Solde caisse         : %-10.2f FCFA                           ║%n", caisse.getSolde());
        System.out.printf( "  ║  Amendes impayées     : %-4d                                      ║%n", db.findAmendesImpayees().size());
        System.out.printf( "  ║  Véhicules            : %-4d  livraisons: %-3d                     ║%n", db.findAll(Vehicule.class).size(), db.findAll(Livraison.class).size());
        System.out.printf( "  ║  Annexes              : %-4d                                      ║%n", db.findAll(Annexe.class).size());
        System.out.printf( "  ║  Capteurs actifs      : %-4d  alertes: %-3d                        ║%n", db.findAll(SenseurCondition.class).size(), alertes);
        System.out.println("  ╚═══════════════════════════════════════════════════════════════════════╝");
    }
}
