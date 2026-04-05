import java.util.Objects;

public class Vehicule {
    private static int compteur = 0;
    private final int    id;
    private String immatriculation;
    private String modele;
    private boolean disponible;

    // Constructeur utilisé par le Main
    public Vehicule(String immatriculation, String modele) {
        this.id = ++compteur;
        setImmatriculation(immatriculation);
        this.modele    = modele;
        this.disponible = true;
    }

    // Constructeur étendu 
    public Vehicule(int id, String immatriculation, String modele) {
        this.id = id;
        setImmatriculation(immatriculation);
        this.modele    = modele;
        this.disponible = true;
    }

    public void effectuerLivraison(Livraison livraison) throws VehiculeIndisponibleException {
        if (!disponible)
            throw new VehiculeIndisponibleException(
                    "Véhicule " + immatriculation + " non disponible.");
        disponible = false;
        livraison.effectuer();
        disponible = true;
    }

    public int     getId()              { return id; }
    public String  getImmatriculation() { return immatriculation; }
    public String  getModele()          { return modele; }
    public boolean isDisponible()       { return disponible; }

    public void setImmatriculation(String immatriculation) {
        if (immatriculation == null || immatriculation.trim().isEmpty())
            throw new IllegalArgumentException("L'immatriculation ne peut pas être vide");
        this.immatriculation = immatriculation;
    }

    @Override
    public String toString() {
        return "Vehicule[" + id + "] " + immatriculation + " — " + modele;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicule)) return false;
        return id == ((Vehicule) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
