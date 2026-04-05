//Classe chargee de gerer les exemplaires
public class Exemplaires{
     public enum Etat { BON, ABIME, PERDU }
//Declaration Attributs
    private int idExemplaire;
    private Livres livre;
    private Etat etatConservation;
    private boolean disponible;
    private Annexe annexe;

//Construteurs avec parametres
    public Exemplaires(int idExemplaire, Livres livre, Annexe annexe) {
        if (livre == null) throw new IllegalArgumentException("Livre ne peut pas être null");
        this.idExemplaire = idExemplaire;
        this.livre = livre;
        this.etatConservation = Etat.BON;
        this.disponible = true;
        this.annexe = annexe;
    }

//Emprunt d'un Exemplaire
    public void marquerEmprunte() {
        if (!disponible) throw new IllegalStateException("Exemplaire déjà emprunté");
        this.disponible = false;
    }

//Retourner un exemplaire
    public void marquerRetourne(Etat nouvelEtat) {
        this.disponible = true;
        this.etatConservation = nouvelEtat;
    }
//Transfere d'un Exemplaire
    public void transferer(Annexe nouvelleAnnexe) {
        if (!disponible) throw new IllegalStateException("Impossible de transférer un exemplaire emprunté");
        if (nouvelleAnnexe == null) throw new IllegalArgumentException("Annexe invalide");
        this.annexe = nouvelleAnnexe;
    }
//Disponibilite d'un exemplaire
    public boolean estDisponible() { return disponible; }

 // Getters
    public int getIdExemplaire() { return idExemplaire; }
    public Livres getLivre() { return livre; }
    public Etat getEtatConservation() { return etatConservation; }
    public Annexe getAnnexe() { return annexe; }
}
