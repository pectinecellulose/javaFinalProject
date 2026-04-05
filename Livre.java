public class Livre {
    private String idLivre;
    private String titre;
    private String auteur;
    private String genre;
    private int anneePublication;

    public Livre(String idLivre, String titre, String auteur, String genre, int anneePublication) {
        if (idLivre == null || idLivre.isEmpty()) throw new IllegalArgumentException("ISBN invalide");
        if (anneePublication < 0)                 throw new IllegalArgumentException("Année invalide");
        this.idLivre          = idLivre;
        this.titre            = titre;
        this.auteur           = auteur;
        this.genre            = genre;
        this.anneePublication = anneePublication;
    }

    public String getDetails() {
        return "[" + idLivre + "] " + titre + " — " + auteur + " (" + anneePublication + ")";
    }

    public String getIsbn()             { return idLivre; }
    public String getTitre()            { return titre; }
    public String getAuteur()           { return auteur; }
    public String getGenre()            { return genre; }
    public int    getAnneePublication() { return anneePublication; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Livre)) return false;
        return idLivre.equals(((Livre) o).idLivre);
    }

    @Override
    public int hashCode() { return idLivre.hashCode(); }

    @Override
    public String toString() {
        return "Livre[" + idLivre + "] \"" + titre + "\" — " + auteur + " (" + anneePublication + ")";
    }
}
