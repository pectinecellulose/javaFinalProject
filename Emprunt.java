//Classe qui permet de gerer les Emprunts effectuees pour chaque Membre 
//Importation des bibliotheques
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Emprunt {
//Enumeration pour l'attribut Statut
    public enum Statut { EN_COURS, RENDU, EN_RETARD }

//Declaration des attributs
    private static int compteur = 1;
    private final int id;
    private  final Membres membre;
    private final  Exemplaires exemplaire;
    private final LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
    private Statut statut;

    private static final int DUREE_JOURS = 14;
    private static final double AMENDE_PAR_JOUR = 0.50;

//Declaration du Constructeur avec parametres
    public Emprunt(Membres membre, Exemplaires exemplaire) {
        this.id = compteur++;
        this.membre = membre;
        this.exemplaire = exemplaire;
        this.dateEmprunt = LocalDate.now();
        this.dateRetourPrevue = dateEmprunt.plusDays(DUREE_JOURS);
        this.statut = Statut.EN_COURS;
    }

//Fonction qui permet de verifier si un Emprunt est en retard ou pas
    public boolean estEnRetard() {
        return LocalDate.now().isAfter(dateRetourPrevue) && statut == Statut.EN_COURS;
    }

//Calculer une amende
    public double calculerAmende() {
        if (!estEnRetard()) return 0;
        long joursRetard = ChronoUnit.DAYS.between(dateRetourPrevue, LocalDate.now());
        return joursRetard * AMENDE_PAR_JOUR;
    }

//Cloturer un Emprunt
    public double cloture(Exemplaires.Etat etatRetour) {
        if (statut != Statut.EN_COURS) throw new IllegalStateException("Emprunt déjà clôturé");
        this.dateRetourEffective = LocalDate.now();
        this.statut = Statut.RENDU;
        double amende = calculerAmende();
        exemplaire.marquerRetourne(etatRetour);
        return amende;
    }

//Prolonger un Emprunt
    public void prolonger(int jours) {
        if (statut != Statut.EN_COURS) throw new IllegalStateException("Impossible de prolonger");
        if (estEnRetard()) throw new IllegalStateException("Emprunt déjà en retard");
        if (jours <= 0) throw new IllegalArgumentException("Durée invalide");
        this.dateRetourPrevue = dateRetourPrevue.plusDays(jours);
    }

// Getters
    public int getId() { return id; }
    public Membres getMembre() { return membre; }
    public Exemplaires getExemplaire() { return exemplaire; }
    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    public LocalDate getDateRetourEffective() { return dateRetourEffective; }
    public Statut getStatut() { return statut; }
}