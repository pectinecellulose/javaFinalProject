
public class Exemplaire {
    public enum Etat { BON, ABIME, PERDU }

    private static int compteur = 0;
    private final int  idExemplaire;
    private final Livre livre;
    private EtatExemplaire etat;
    private Etat   etatConservation;
    private Annexe localisation;

    public Exemplaire(Livre livre, Annexe localisation, EtatExemplaire etat) {
        if (livre       == null) throw new IllegalArgumentException("Livre ne peut pas être null");
        if (localisation == null) throw new IllegalArgumentException("Annexe invalide");
        this.idExemplaire    = ++compteur;
        this.livre           = livre;
        this.localisation    = localisation;
        this.etat            = (etat != null) ? etat : EtatExemplaire.DISPONIBLE;
        this.etatConservation = Etat.BON;
    }

    public void marquerEmprunte() {
        if (etat != EtatExemplaire.DISPONIBLE)
            throw new IllegalStateException("Exemplaire non disponible");
        this.etat = EtatExemplaire.EMPRUNTE;
    }

    public void marquerRetourne(Etat nouvelEtat) {
        this.etat             = EtatExemplaire.DISPONIBLE;
        this.etatConservation = nouvelEtat;
    }

    // Transfert inter-annexes (utilisé dans Livraison)
    public void transferer(Annexe nouvelleAnnexe) {
        if (etat == EtatExemplaire.EMPRUNTE)
            throw new IllegalStateException("Impossible de transférer un exemplaire emprunté");
        if (nouvelleAnnexe == null) throw new IllegalArgumentException("Annexe invalide");
        this.localisation = nouvelleAnnexe;
    }

    public boolean isDisponible() { return etat == EtatExemplaire.DISPONIBLE; }

    // Getters
    public int            getId()              { return idExemplaire; }
    public Livre          getLivre()           { return livre; }
    public EtatExemplaire getEtat()            { return etat; }
    public Etat           getEtatConservation(){ return etatConservation; }
    public Annexe         getLocalisation()    { return localisation; }

    // Setters (utilisés par Stock)
    public void setEtat(EtatExemplaire etat)          { this.etat = etat; }
    public void setLocalisation(Annexe localisation)  { this.localisation = localisation; }

    @Override
    public String toString() {
        return "Exemplaire[" + idExemplaire + "] \"" + livre.getTitre()
               + "\" @ " + localisation.getNom() + " — " + etat;
    }
}
