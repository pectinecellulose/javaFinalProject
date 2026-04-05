import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Emprunt {
    public enum Statut { EN_COURS, RENDU, EN_RETARD }

    private static int compteur = 1;
    private final int       id;
    private final Membre    membre;
    private final Exemplaire exemplaire;
    private final LocalDate  dateEmprunt;
    private LocalDate        dateRetourPrevue;
    private LocalDate        dateRetourEffective;
    private Statut           statut;

    private static final int    DUREE_JOURS    = 14;
    private static final double AMENDE_PAR_JOUR = 100.0; // FCFA

    public Emprunt(Membre membre, Exemplaire exemplaire, LocalDate dateEmprunt) {
        this.id              = compteur++;
        this.membre          = membre;
        this.exemplaire      = exemplaire;
        this.dateEmprunt     = (dateEmprunt != null) ? dateEmprunt : LocalDate.now();
        this.dateRetourPrevue = this.dateEmprunt.plusDays(DUREE_JOURS);
        this.statut          = Statut.EN_COURS;
    }

    public boolean estEnRetard(LocalDate dateReference) {
        return dateReference.isAfter(dateRetourPrevue) && statut == Statut.EN_COURS;
    }

    public int calculerJoursRetard(LocalDate dateRetour) {
        long jours = ChronoUnit.DAYS.between(dateRetourPrevue, dateRetour);
        return jours > 0 ? (int) jours : 0;
    }

    // Clôturer l'emprunt (retour)
    public void setDateRetourReelle(LocalDate date) {
        this.dateRetourEffective = date;
        this.statut = Statut.RENDU;
    }

    public boolean isEstRetourne() { return statut == Statut.RENDU; }

    // Prolonger un emprunt
    public void prolonger(int jours) {
        if (statut != Statut.EN_COURS) throw new IllegalStateException("Impossible de prolonger");
        if (jours <= 0)                throw new IllegalArgumentException("Durée invalide");
        this.dateRetourPrevue = dateRetourPrevue.plusDays(jours);
    }

    // Getters
    public int       getId()                  { return id; }
    public Membre    getMembre()              { return membre; }
    public Exemplaire getExemplaire()         { return exemplaire; }
    public LocalDate  getDateEmprunt()        { return dateEmprunt; }
    public LocalDate  getDateRetourPrevue()   { return dateRetourPrevue; }
    public LocalDate  getDateRetourEffective(){ return dateRetourEffective; }
    public Statut     getStatut()             { return statut; }

    @Override
    public String toString() {
        return "Emprunt[" + id + "] " + membre.getNomComplet()
               + " → \"" + exemplaire.getLivre().getTitre() + "\"";
    }
}
