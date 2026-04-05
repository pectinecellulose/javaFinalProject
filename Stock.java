import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Stock {
    private static Stock instance;
    private final List<Exemplaire> exemplaires = new ArrayList<>();

    private Stock() {}

    public static Stock getInstance() {
        if (instance == null) instance = new Stock();
        return instance;
    }

    public void ajouterExemplaire(Exemplaire exemplaire) {
        if (exemplaire == null) throw new IllegalArgumentException("Exemplaire invalide.");
        exemplaires.add(exemplaire);
    }

    public Emprunt emprunter(Membre membre, Livre livre, LocalDate dateEmprunt)
            throws LivreNonDisponibleException {
        Exemplaire dispo = exemplaires.stream()
                .filter(e -> e.getLivre().equals(livre) && e.isDisponible())
                .findFirst()
                .orElseThrow(() -> new LivreNonDisponibleException(
                        "Aucun exemplaire disponible de \"" + livre.getTitre() + "\""));
        dispo.setEtat(EtatExemplaire.EMPRUNTE);
        return new Emprunt(membre, dispo, dateEmprunt);
    }

    public Amende retournerExemplaire(Emprunt emprunt, LocalDate dateRetour)
            throws EmpruntIntrouvableException {
        if (!exemplaires.contains(emprunt.getExemplaire()))
            throw new EmpruntIntrouvableException("Exemplaire introuvable dans le stock.");
        emprunt.setDateRetourReelle(dateRetour);
        emprunt.getExemplaire().setEtat(EtatExemplaire.DISPONIBLE);
        int retard = emprunt.calculerJoursRetard(dateRetour);
        return retard > 0 ? new Amende(emprunt, retard) : null;
    }

    public List<Exemplaire> getTousLesExemplaires()  { return new ArrayList<>(exemplaires); }
    public int  getNombreExemplaires()               { return exemplaires.size(); }
    public long getNombreDisponibles()               { return exemplaires.stream().filter(e -> e.getEtat() == EtatExemplaire.DISPONIBLE).count(); }
    public long getNombreEmpruntes()                 { return exemplaires.stream().filter(e -> e.getEtat() == EtatExemplaire.EMPRUNTE).count(); }
    public long getNombreEnReparation()              { return exemplaires.stream().filter(e -> e.getEtat() == EtatExemplaire.EN_REPARATION).count(); }
}
