package duzeZadanie.sportowcy.bfs;

import duzeZadanie.czas.Interwal;
import duzeZadanie.osrodek.Wezel;
import duzeZadanie.osrodek.krawedz.Krawedz;
import duzeZadanie.osrodek.krawedz.Trasa;
import duzeZadanie.osrodek.krawedz.wyciag.Wyciag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BFSPoGrafieTests {
    /**
     * Z uwagi na to, że zaimplementowany algorytm bfs posiada
     * modyfikacje (znajduję najkrótszą ścieżkę z wierzchołka do krawędzi włącznie)
     * to format wymaganych testów nieznacznie uległ zmianie tj. zamiast szukać
     * najkrótszej ścieżki z wierzchołka do wierzchołka szuka najkrótszej ścieżki
     * z wierzchołka do dowolnej krawędzi wychodzącej z docelowego wierzchołka.
     * Pozwala to zachować główną ideę narzuconych testów, ale wynik w sensie długości
     * ścieżki będzie wtedy większy o dokładnie jeden (dodatkowa krawędż). Mam nadzieje,
     * że nie utrudni to procesu sprawdzania :).
     */

    private Wezel[] wezly = new Wezel[6];
    private Trasa[] trasy = new Trasa[7];
    private Wyciag[] wyciagi = new Wyciag[4];

    @BeforeEach
    void przygotujGraf() {
        wezly[0] = new Wezel(0, 6, 1, 1, true);
        wezly[1] = new Wezel(1, 8, 1, 1, true);
        wezly[2] = new Wezel(2, 7, 1, 1, true);
        wezly[3] = new Wezel(3, 9, 1, 1, true);
        wezly[4] = new Wezel(4, 8, 1, 1, true);
        wezly[5] = new Wezel(5, 10, 1, 1, true);

        wyciagi[0] = new Wyciag(0, wezly[0], wezly[1],
                new Interwal(1), new Interwal(1), 1); // nieznaczace dane
        wyciagi[1] = new Wyciag(1, wezly[2], wezly[3],
                new Interwal(1), new Interwal(1), 1); // nieznaczace dane
        wyciagi[2] = new Wyciag(2, wezly[2], wezly[4],
                new Interwal(1), new Interwal(1), 1); // nieznaczace dane
        wyciagi[3] = new Wyciag(3, wezly[4], wezly[5],
                new Interwal(1), new Interwal(1), 1); // nieznaczace dane

        trasy[0] = new Trasa(0, wezly[1], wezly[0],
                new Interwal(1), 1, 1, 1); // nieznaczace dane
        trasy[1] = new Trasa(1, wezly[1], wezly[2],
                new Interwal(1), 1, 1, 1); // nieznaczace dane
        trasy[2] = new Trasa(2, wezly[2], wezly[0],
                new Interwal(1), 1, 1, 1); // nieznaczace dane
        trasy[3] = new Trasa(3, wezly[3], wezly[1],
                new Interwal(1), 1, 1, 1); // nieznaczace dane
        trasy[4] = new Trasa(4, wezly[3], wezly[4],
                new Interwal(1), 1, 1, 1); // nieznaczace dane
        trasy[5] = new Trasa(5, wezly[5], wezly[3],
                new Interwal(1), 1, 1, 1); // nieznaczace dane
        trasy[6] = new Trasa(6, wezly[5], wezly[3],
                new Interwal(1), 1, 1, 1); // nieznaczace dane

        wezly[0].wychodzaceTrasy(new Trasa[] {});
        wezly[0].wychodzaceWyciagi(new Wyciag[] {wyciagi[0]});
        wezly[1].wychodzaceTrasy(new Trasa[] {trasy[0], trasy[1]});
        wezly[1].wychodzaceWyciagi(new Wyciag[] {});
        wezly[2].wychodzaceTrasy(new Trasa[] {trasy[2]});
        wezly[2].wychodzaceWyciagi(new Wyciag[] {wyciagi[1], wyciagi[2]});
        wezly[3].wychodzaceTrasy(new Trasa[] {trasy[3], trasy[4]});
        wezly[3].wychodzaceWyciagi(new Wyciag[] {});
        wezly[4].wychodzaceTrasy(new Trasa[] {});
        wezly[4].wychodzaceWyciagi(new Wyciag[] {wyciagi[3]});
        wezly[5].wychodzaceTrasy(new Trasa[] {trasy[5], trasy[6]});
        wezly[5].wychodzaceWyciagi(new Wyciag[] {});
    }

    @Test
    public void testZwyklaSciezka() {
        ArrayDeque<Krawedz> sciezka = BFSPoGrafie.bfs(wezly[0], wyciagi[3]); // 0 -> 4 + wyciagi[3]

        assertIterableEquals(new ArrayDeque<>(List.of(wyciagi[3], wyciagi[2], trasy[1], wyciagi[0])), sciezka);
    }

    @Test
    public void testBezposredniaSciezka() {
        ArrayDeque<Krawedz> sciezka = BFSPoGrafie.bfs(wezly[3], trasy[0]); // 3 -> 1 + trasy[0]

        assertIterableEquals(new ArrayDeque<>(List.of(trasy[0], trasy[3])), sciezka);
    }

    @Test
    public void testPustaSciezka() {
        ArrayDeque<Krawedz> sciezka = BFSPoGrafie.bfs(wezly[2], trasy[2]); // 2 -> 2 + trasy[2]

        assertIterableEquals(new ArrayDeque<>(List.of(trasy[2])), sciezka);
    }

    @Test
    public void testDwaDobreWybory() {
        ArrayDeque<Krawedz> sciezka = BFSPoGrafie.bfs(wezly[4], trasy[4]); // 4 -> 3 + trasy[4]
        List<Krawedz> aktualnyWynik = new ArrayList<>(sciezka);

        List<Krawedz> opcja1 = List.of(trasy[4], trasy[5], wyciagi[3]);
        List<Krawedz> opcja2 = List.of(trasy[4], trasy[6], wyciagi[3]);

        assertTrue(
                opcja1.equals(aktualnyWynik) || opcja2.equals(aktualnyWynik),
                "Żadna poprawna ścieżka nie została znaleziona!"
        );
    }
}
