// Basé sur Membres.java (groupe) — renommé en Membre, adapté pour le Main
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Membre {
    private static int compteur = 0;
    private final int       idmembre;
    private final String    nom;
    private final String    prenom;
    private final String    email;
    private final String    telephone;
    private final LocalDate dateInscription;
    private double          amendeTotal;
    private final List<Emprunt> historique;

    public Membre(String nom, String prenom, String email, String telephone) {
        if (nom   == null || nom.isEmpty())   throw new IllegalArgumentException("Nom invalide");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Email invalide");
        this.idmembre        = ++compteur;
        this.nom             = nom;
        this.prenom          = prenom;
        this.email           = email;
        this.telephone       = telephone;
        this.dateInscription = LocalDate.now();
        this.amendeTotal     = 0;
        this.historique      = new ArrayList<>();
    }

    // Enregistre un emprunt dans l'historique
    public void ajouterEmprunt(Emprunt emprunt) { historique.add(emprunt); }

    // Payer une amende
    public void ajouterAmende(double montant) { this.amendeTotal += montant; }

    public List<Emprunt> getHistoriqueEmprunts() { return new ArrayList<>(historique); }

    // Getters
    public int       getId()             { return idmembre; }
    public int       getIdmembre()       { return idmembre; }
    public String    getNom()            { return nom; }
    public String    getPrenom()         { return prenom; }
    public String    getNomComplet()     { return prenom + " " + nom; }
    public String    getEmail()          { return email; }
    public String    getTelephone()      { return telephone; }
    public LocalDate getDateInscription(){ return dateInscription; }
    public double    getAmendeTotal()    { return amendeTotal; }

    @Override
    public String toString() {
        return "Membre[" + idmembre + "] " + getNomComplet() + " — " + email;
    }
}
