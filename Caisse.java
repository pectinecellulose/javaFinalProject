// Basé sur Caisse.java 
public class Caisse {
    private static Caisse instance;
    private double solde              = 0.0;
    private double totalEncaisse      = 0.0;
    private int    nombreTransactions = 0;

    private Caisse() {}

    public static Caisse getInstance() {
        if (instance == null) instance = new Caisse();
        return instance;
    }

    // Méthode principale 
    public void encaisserAmende(Amende amende) throws AmendeDejaPaYeeException {
        if (amende == null)    throw new IllegalArgumentException("Amende invalide.");
        if (amende.estPayee()) throw new AmendeDejaPaYeeException(
                "L'amende a déjà été payée (ID=" + amende.getId() + ").");
        amende.payer();
        solde              += amende.getMontant();
        totalEncaisse      += amende.getMontant();
        nombreTransactions++;
        System.out.printf("  Paiement de %.2f FCFA enregistré. Solde : %.2f FCFA%n",
                amende.getMontant(), solde);
    }

    public double getSolde()              { return solde; }
    public double getTotalEncaisse()      { return totalEncaisse; }
    public int    getNombreTransactions() { return nombreTransactions; }

    // Conservé pour compatibilité (tests / reset)
    public void setSolde(double solde)    { this.solde = solde; }
}
