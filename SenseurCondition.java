public class SenseurCondition {
    private static final double TEMP_MAX       = 25.0;
    private static final double TEMP_MIN       = 15.0;
    private static final double HUMIDITE_MAX   = 70.0;
    private static final double LUMINOSITE_MAX = 300.0;

    private static int compteur = 0;
    private final int    id;
    private final String type;
    private final double valeur;
    private final String unite;
    private final String zone;
    private final Annexe annexe;

    public SenseurCondition(String type, double valeur, String unite, String zone, Annexe annexe) {
        if (type   == null || type.isBlank()) throw new IllegalArgumentException("Type invalide.");
        if (annexe == null)                   throw new IllegalArgumentException("Annexe invalide.");
        this.id     = ++compteur;
        this.type   = type;
        this.valeur = valeur;
        this.unite  = unite;
        this.zone   = zone;
        this.annexe = annexe;
    }

    public boolean estEnAlerte() {
        return switch (type.toLowerCase()) {
            case "température" -> valeur > TEMP_MAX || valeur < TEMP_MIN;
            case "humidité"    -> valeur > HUMIDITE_MAX;
            case "luminosité"  -> valeur > LUMINOSITE_MAX;
            default            -> false;
        };
    }

    public int    getId()     { return id; }
    public String getType()   { return type; }
    public double getValeur() { return valeur; }
    public String getUnite()  { return unite; }
    public String getZone()   { return zone; }
    public Annexe getAnnexe() { return annexe; }

    @Override
    public String toString() {
        return "SenseurCondition[" + id + "] " + type + " = " + valeur + " " + unite
               + " @ " + zone + (estEnAlerte() ? " ⚠ ALERTE" : " ✔ Normal");
    }
}
