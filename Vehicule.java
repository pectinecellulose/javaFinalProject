import java.util.Objects;

/**
 * Représente un véhicule de la bibliothèque.
 * Attributs : identifiant, immatriculation, capacité maximale d'exemplaires.
 * Encapsulation stricte, validation des données.
 */
public class Vehicule {
    private int id;
    private String immatriculation;
    private int capaciteMax;   // nombre maximal d'exemplaires transportables

    // Constructeur avec validation
    public Vehicule(int id, String immatriculation, int capaciteMax) {
        this.id = id;
        setImmatriculation(immatriculation);
        setCapaciteMax(capaciteMax);
    }

    // --- Getters / Setters avec contrôles ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) {
        if (immatriculation == null || immatriculation.trim().isEmpty())
            throw new IllegalArgumentException("L'immatriculation ne peut pas être vide");
        this.immatriculation = immatriculation;
    }

    public int getCapaciteMax() { return capaciteMax; }
    public void setCapaciteMax(int capaciteMax) {
        if (capaciteMax <= 0)
            throw new IllegalArgumentException("La capacité maximale doit être positive");
        this.capaciteMax = capaciteMax;
    }

    @Override
    public String toString() {
        return String.format("Vehicule{id=%d, immat=%s, capacite=%d}", id, immatriculation, capaciteMax);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicule)) return false;
        Vehicule vehicule = (Vehicule) o;
        return id == vehicule.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}