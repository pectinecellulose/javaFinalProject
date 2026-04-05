public class Annexe {
    private static int compteur = 0;
    private final int id;
    private final String nom;
    private final String adresse;

    public Annexe(String nom, String adresse) {
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Nom de l'annexe invalide.");
        this.id      = ++compteur;
        this.nom     = nom;
        this.adresse = adresse;
    }

    public int    getId()      { return id; }
    public String getNom()     { return nom; }
    public String getAdresse() { return adresse; }

    @Override
    public String toString() { return "Annexe[" + id + "] " + nom + " — " + adresse; }
}
