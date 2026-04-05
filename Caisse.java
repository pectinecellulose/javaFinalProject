import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Caisse centrale de la bibliothèque (Singleton).
 * Le solde est persisté dans une table "Caisse" (une seule ligne).
 */
public class Caisse {
    private static Caisse instance;
    private double solde;

    // Constructeur privé pour Singleton
    private Caisse() {}

    public static Caisse getInstance() {
        if (instance == null) {
            instance = new Caisse();
        }
        return instance;
    }

    /**
     * Initialise ou recharge le solde depuis la base de données.
     * À appeler au démarrage du programme.
     */
    public void initialiserSolde(Connection conn) throws SQLException {
        String sql = "SELECT solde FROM Caisse WHERE id = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                this.solde = rs.getDouble("solde");
            } else {
                // Première exécution : créer la ligne avec solde = 0
                String insert = "INSERT INTO Caisse (id, solde) VALUES (1, 0.0)";
                try (PreparedStatement psi = conn.prepareStatement(insert)) {
                    psi.executeUpdate();
                }
                this.solde = 0.0;
            }
        }
    }

    /**
     * Enregistre le paiement d'une amende.
     * @param montant montant à ajouter à la caisse
     * @param amende  l'amende concernée (sera marquée comme payée)
     * @param conn    connexion JDBC active
     * @throws SQLException si erreur base de données
     * @throws IllegalArgumentException si montant négatif ou nul
     */
    public void encaisser(double montant, Amende amende, Connection conn)
            throws SQLException, IllegalArgumentException {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant à encaisser doit être positif");
        }

        // 1. Mettre à jour le solde en mémoire
        this.solde += montant;

        // 2. Mettre à jour la base (table Caisse)
        String updateCaisse = "UPDATE Caisse SET solde = ? WHERE id = 1";
        try (PreparedStatement ps = conn.prepareStatement(updateCaisse)) {
            ps.setDouble(1, this.solde);
            ps.executeUpdate();
        }

        // 3. Marquer l'amende comme payée et persister le changement
        amende.setPayee(true);
        new AmendeDAO(conn).update(amende);   // suppose l'existence d'un AmendeDAO

        System.out.printf("Paiement de %.2f € enregistré. Nouveau solde : %.2f €\n", montant, this.solde);
    }

    public double getSolde() {
        return solde;
    }

    // Utilisé uniquement pour les tests ou la réinitialisation
    public void setSolde(double solde) {
        this.solde = solde;
    }
}