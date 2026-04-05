public class Amende {
    private static final double TARIF_PAR_JOUR = 100.0; // FCFA
    private static int compteur = 0;

    private final int    id;
    private final Emprunt emprunt;
    private final int     joursRetard;
    private final double  montant;
    private StatutAmende  statut;

    public Amende(Emprunt emprunt, int joursRetard) {
        if (emprunt    == null) throw new IllegalArgumentException("Emprunt invalide.");
        if (joursRetard <= 0)   throw new IllegalArgumentException("Jours de retard invalides.");
        this.id          = ++compteur;
        this.emprunt     = emprunt;
        this.joursRetard = joursRetard;
        this.montant     = joursRetard * TARIF_PAR_JOUR;
        this.statut      = StatutAmende.EN_ATTENTE;
    }

    public void payer()          { this.statut = StatutAmende.PAYEE; }
    public void setPayee(boolean b) { if (b) this.statut = StatutAmende.PAYEE; }
    public boolean estPayee()    { return statut == StatutAmende.PAYEE; }

    public int          getId()          { return id; }
    public Emprunt      getEmprunt()     { return emprunt; }
    public int          getJoursRetard() { return joursRetard; }
    public double       getMontant()     { return montant; }
    public StatutAmende getStatut()      { return statut; }

    @Override
    public String toString() {
        return "Amende[" + id + "] " + joursRetard + " j — "
               + String.format("%.2f", montant) + " FCFA — " + statut;
    }
}
