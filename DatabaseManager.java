import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static DatabaseManager instance;

    private final List<Annexe>           annexes     = new ArrayList<>();
    private final List<Livre>            livres      = new ArrayList<>();
    private final List<Exemplaire>       exemplaires = new ArrayList<>();
    private final List<Membre>           membres     = new ArrayList<>();
    private final List<Emprunt>          emprunts    = new ArrayList<>();
    private final List<Amende>           amendes     = new ArrayList<>();
    private final List<Vehicule>         vehicules   = new ArrayList<>();
    private final List<Livraison>        livraisons  = new ArrayList<>();
    private final List<SenseurCondition> capteurs    = new ArrayList<>();

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    public void initialiserSchema() {
        // En production : CREATE TABLE IF NOT EXISTS ...
    }

    public void sauvegarder(Object obj) {
        if      (obj instanceof Annexe)           { if (!annexes.contains(obj))     annexes.add((Annexe) obj); }
        else if (obj instanceof Livre)            { if (!livres.contains(obj))      livres.add((Livre) obj); }
        else if (obj instanceof Exemplaire)       { if (!exemplaires.contains(obj)) exemplaires.add((Exemplaire) obj); }
        else if (obj instanceof Membre)           { if (!membres.contains(obj))     membres.add((Membre) obj); }
        else if (obj instanceof Emprunt)          { if (!emprunts.contains(obj))    emprunts.add((Emprunt) obj); }
        else if (obj instanceof Amende)           { if (!amendes.contains(obj))     amendes.add((Amende) obj); }
        else if (obj instanceof Vehicule)         { if (!vehicules.contains(obj))   vehicules.add((Vehicule) obj); }
        else if (obj instanceof Livraison)        { if (!livraisons.contains(obj))  livraisons.add((Livraison) obj); }
        else if (obj instanceof SenseurCondition) { if (!capteurs.contains(obj))    capteurs.add((SenseurCondition) obj); }
    }

    public void mettreAJour(Object obj) {
        // En mémoire, l'objet est déjà modifié par référence.
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(Class<T> clazz) {
        if (clazz == Annexe.class)           return (List<T>) new ArrayList<>(annexes);
        if (clazz == Livre.class)            return (List<T>) new ArrayList<>(livres);
        if (clazz == Exemplaire.class)       return (List<T>) new ArrayList<>(exemplaires);
        if (clazz == Membre.class)           return (List<T>) new ArrayList<>(membres);
        if (clazz == Emprunt.class)          return (List<T>) new ArrayList<>(emprunts);
        if (clazz == Amende.class)           return (List<T>) new ArrayList<>(amendes);
        if (clazz == Vehicule.class)         return (List<T>) new ArrayList<>(vehicules);
        if (clazz == Livraison.class)        return (List<T>) new ArrayList<>(livraisons);
        if (clazz == SenseurCondition.class) return (List<T>) new ArrayList<>(capteurs);
        return new ArrayList<>();
    }

    public List<Emprunt> findEmpruntsEnCours() {
        List<Emprunt> result = new ArrayList<>();
        for (Emprunt e : emprunts)
            if (!e.isEstRetourne()) result.add(e);
        return result;
    }

    public List<Amende> findAmendesImpayees() {
        List<Amende> result = new ArrayList<>();
        for (Amende a : amendes)
            if (!a.estPayee()) result.add(a);
        return result;
    }

    public int compterTousLesObjets() {
        return annexes.size() + livres.size() + exemplaires.size()
             + membres.size() + emprunts.size() + amendes.size()
             + vehicules.size() + livraisons.size() + capteurs.size();
    }

    public void fermerConnexion() {
        System.out.println(" ✔ Connexion fermée.");
    }
}
