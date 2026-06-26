package duzeZadanie.sportowcy;


import duzeZadanie.osrodek.krawedz.Krawedz;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoriaSportowca {
    private final Sportowiec sportowiec;

    private ArrayList<Krawedz> historia;

    public HistoriaSportowca(Sportowiec sportowiec) {
        this.sportowiec = sportowiec;
        historia = new ArrayList<>();
    }

    /**
     * Dodaje właśnie przemierzaną krawędź do historii sportowca.
     * @param krawedz
     */
    public void wydarzenie(Krawedz krawedz) {
        historia.add(krawedz);
    }

    /**
     * zwraca całą dotychczasową historie sportowca.
     * @return
     */
    public ArrayList<Krawedz> historia() {
        return historia;
    }

    /**
     * Porządkuje historie sportowca przypisując każdej przemierzonej
     * krawędzi porządkowe numery przejazdów.
     * @return
     */
    public HashMap<Krawedz, String> kolejnoscAkcjiSportowca() {
        int licznik = 1;
        HashMap<Krawedz, String> przejazdy = new HashMap<>();
        for (Krawedz krawedz: sportowiec.historiaSportowca().historia()) {
            if (!przejazdy.containsKey(krawedz)) {
                przejazdy.put(krawedz, Integer.toString(licznik));
            }
            else {
                przejazdy.put(krawedz, przejazdy.get(krawedz) + "," + licznik);
            }
            ++licznik;
        }
        return przejazdy;
    }

    /**
     * Oblicza ile razy sportowiec odwiedził daną krawędź.
     * @param krawedz
     * @return
     */
    public int liczbaWizyt(Krawedz krawedz) {
        return (int)historia
                .stream()
                .filter(k -> k.equals(krawedz))
                .count();
    }
}
