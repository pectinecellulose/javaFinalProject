//Classes qui permet de gerer les Livres de la Bibliotheque
public class Livres{
//Declaration Attributs
    private String idLivre;
    private String titre;
    private String auteur;
    private String genre;
    private int anneePublication;

// Declaration Constructeur(avec parametres)
 public Livres(String idLivre, String titre, String auteur, String genre, int anneePublication) {
        if (idLivre == null || idLivre.isEmpty()) throw new IllegalArgumentException("ISBN invalide");
        if (anneePublication < 0) throw new IllegalArgumentException("Année invalide");
        this.idLivre= idLivre;
        this.titre = titre;
        this.auteur = auteur;
        this.genre = genre;
        this.anneePublication = anneePublication;
    }
//Description 
    public String getDetails() {
        return "[" + idLivre + "] " + titre + " — " + auteur + " (" + anneePublication + ")";
    }

// Getters
    public String getIsbn() { return idLivre; }
    public String getTitre() { return titre; }
    public String getAuteur() { return auteur; }
    public String getGenre() { return genre; }
    public int getAnneePublication() { return anneePublication; }
}
