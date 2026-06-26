package duzeZadanie.sportowcy.bfs;

import duzeZadanie.osrodek.Wezel;
import duzeZadanie.osrodek.krawedz.Krawedz;
import duzeZadanie.osrodek.krawedz.Trasa;
import duzeZadanie.osrodek.krawedz.wyciag.Wyciag;

import java.util.*;

public class BFSPoGrafie {

    /**
     * Metoda daje w wyniku dowolną najkrótszą drogę do krawedz,
     * zaczynającą się w węźle wezel. W zamyśle krawedz ma byc
     * wymarzona trasa sportowca (ale nic nie szkodzi, by sportowiec
     * lubił tylko wyciągi).
     */
    public static ArrayDeque<Krawedz> bfs(Wezel wezel, Krawedz krawedz) {
        Map<Wezel, Krawedz> poprzedniaKrawedz = new HashMap<>();
        Queue<Wezel> doOdwiedzenia = new LinkedList<>();

        doOdwiedzenia.add(wezel);
        poprzedniaKrawedz.put(wezel, null);
        while (!doOdwiedzenia.isEmpty()) {
            Wezel odwiedzanyWezel = doOdwiedzenia.poll();
            Trasa[] trasy = odwiedzanyWezel.wychodzaceTrasy();

            for (Trasa t: trasy) {
                if (!poprzedniaKrawedz.containsKey(t.koniec())) {
                    poprzedniaKrawedz.put(t.koniec(), t);
                    doOdwiedzenia.add(t.koniec());
                }
            }

            Wyciag[] wyciagi = odwiedzanyWezel.wychodzaceWyciagi();
            for (Wyciag w: wyciagi) {
                if (!poprzedniaKrawedz.containsKey(w.koniec())) {
                    poprzedniaKrawedz.put(w.koniec(), w);
                    doOdwiedzenia.add(w.koniec());
                }
            }
        }

        // Skoro Graf z założenia jest silnie spójny to można beztrosko odtworzyć drogę
        ArrayDeque<Krawedz> wynik = new ArrayDeque<>();
        wynik.addLast(krawedz);
        Krawedz obecnaKrawedz = krawedz;
        while (poprzedniaKrawedz.get(obecnaKrawedz.poczatek()) != null) {
            obecnaKrawedz = poprzedniaKrawedz.get(obecnaKrawedz.poczatek());
            wynik.addLast(obecnaKrawedz);
        }

        return wynik;

    }
}
