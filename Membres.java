//Classes permettant de gerer les Membres qu'accueille la Bibliotheque
//inclusion des Bibliotheques
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Membres {

//Declaration des attributs
    private int idmembre;
    private String nom;
    private String prenom;
    private String email;
    private LocalDate dateInscription;
    private double amendeTotal;
    private List<Emprunt> historique;

//Declaration du Constructeur avec parametres
    public Membres(int idmembre, String nom, String prenom, String email) {
        if (nom == null || nom.isEmpty()) throw new IllegalArgumentException("Nom invalide");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Email invalide");
        this.idmembre = idmembre;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.dateInscription = LocalDate.now();
        this.amendeTotal = 0;
        this.historique = new ArrayList<>();
    }
// Emprunt
    public Emprunt emprunter(Exemplaires exemplaire) {
        if (exemplaire == null) throw new IllegalArgumentException("Exemplaire invalide");
        if (!exemplaire.estDisponible()) throw new IllegalStateException("Exemplaire non disponible");
        if (amendeTotal > 0) throw new IllegalStateException("Membre a des amendes impayées");

        Emprunt emprunt = new Emprunt(this, exemplaire);
        historique.add(emprunt);
        exemplaire.marquerEmprunte();
        return emprunt;
    }
// Retourner
    public void retourner(Emprunt emprunt, Exemplaires.Etat etat) {
        if (emprunt == null) throw new IllegalArgumentException("Emprunt invalide");
        double amende = emprunt.cloture(etat);
        this.amendeTotal += amende;
    }
//Payer une Amende
    public void payerAmende(double montant, Caisse caisse) {
        if (montant <= 0) throw new IllegalArgumentException("Montant invalide");
        if (montant > amendeTotal) throw new IllegalArgumentException("Montant supérieur à l'amende due");
        this.amendeTotal -= montant;
        caisse.encaisserAmende(montant, this);
    }

//Affichage Historique Emprunt
    public List<Emprunt> getHistoriqueEmprunts() { return new ArrayList<>(historique); }

// Getters
    public int getIdmembre() { return idmembre; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public LocalDate getDateInscription() { return dateInscription; }
    public double getAmendeTotal() { return amendeTotal; }
}