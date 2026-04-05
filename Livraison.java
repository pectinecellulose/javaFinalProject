import java.time.LocalDate;

public class Livraison {
    private static int compteur = 0;
    private final int        id;
    private final Vehicule   vehicule;
    private final Exemplaire exemplaire;
    private final Annexe     annexeDepart;
    private final Annexe     annexeArrivee;
    private final LocalDate  date;

    public Livraison(Vehicule vehicule, Exemplaire exemplaire,
                     Annexe annexeDepart, Annexe annexeArrivee, LocalDate date) {
        if (vehicule      == null) throw new IllegalArgumentException("Véhicule invalide.");
        if (exemplaire    == null) throw new IllegalArgumentException("Exemplaire invalide.");
        if (annexeDepart  == null) throw new IllegalArgumentException("Annexe de départ invalide.");
        if (annexeArrivee == null) throw new IllegalArgumentException("Annexe d'arrivée invalide.");
        this.id            = ++compteur;
        this.vehicule      = vehicule;
        this.exemplaire    = exemplaire;
        this.annexeDepart  = annexeDepart;
        this.annexeArrivee = annexeArrivee;
        this.date          = (date != null) ? date : LocalDate.now();
    }

    // Déplace l'exemplaire vers l'annexe d'arrivée
    public void effectuer() { exemplaire.setLocalisation(annexeArrivee); }

    public int        getId()            { return id; }
    public Vehicule   getVehicule()      { return vehicule; }
    public Exemplaire getExemplaire()    { return exemplaire; }
    public Annexe     getAnnexeDepart()  { return annexeDepart; }
    public Annexe     getAnnexeArrivee() { return annexeArrivee; }
    public LocalDate  getDate()          { return date; }

    @Override
    public String toString() {
        return "Livraison[" + id + "] " + annexeDepart.getNom()
               + " → " + annexeArrivee.getNom() + " (" + date + ")";
    }
}
