package duzeZadanie.mapkarz;

import duzeZadanie.czas.Moment;
import duzeZadanie.osrodek.Osrodek;
import duzeZadanie.osrodek.Wezel;
import duzeZadanie.osrodek.krawedz.Krawedz;
import duzeZadanie.osrodek.krawedz.Trasa;
import duzeZadanie.osrodek.krawedz.wyciag.Wyciag;
import duzeZadanie.sportowcy.Sportowiec;
import kadra.mapki.GeneratorMapek;
import kadra.mapki.pliki.WyjatekSystemuPlikow;
import kadra.mapki.styl.GruboscKonturu;
import kadra.mapki.styl.StylKrawedzi;
import kadra.mapki.styl.StylLinii;
import kadra.mapki.styl.StylWezla;

import java.util.HashMap;
import java.util.List;

public class Mapkarz {


    /**
     * Tworzy mapki: parametrów krawędzi, statystyk krawędzi i statystyk sportowców.
     * @param osrodek
     * @param sportowcy
     * @param poczatekDnia
     * @param katalog
     * @throws WyjatekSystemuPlikow
     */
    public static void stworzMapki(
            Osrodek osrodek,
            Sportowiec[] sportowcy,
            Moment poczatekDnia,
            String katalog
    ) throws WyjatekSystemuPlikow {

        GeneratorMapek generatorMapek = new GeneratorMapek(katalog);
        stworzMapkeParametry(osrodek, generatorMapek);
        stworzMapkeStatystyki(osrodek, generatorMapek, poczatekDnia);
        stworzMapkiSportowcow(osrodek, sportowcy, generatorMapek);
    }

    /**
     * Metoda pomocnicza dodająca węzły do rysowanego grafu.
     * @param osrodek
     * @param generatorMapek
     */
    private static void dodajWezlyDoMapki(Osrodek osrodek, GeneratorMapek generatorMapek) {
        for (Wezel w: osrodek.wezly()) {
            if (w.czyStartowy()) {
                generatorMapek.dodajWezel(w.id(),
                        w.wspolrzednaX(),
                        w.wspolrzednaY(),
                        new StylWezla(GruboscKonturu.POGRUBIONY));
            }
            else {
                generatorMapek.dodajWezel(w.id(),
                        w.wspolrzednaX(),
                        w.wspolrzednaY(),
                        new StylWezla(GruboscKonturu.ZWYKLY));
            }
        }
    }

    /**
     * Tworzy mapkę parametrów tras i wyciagów.
     * @param osrodek
     * @param generatorMapek
     * @throws WyjatekSystemuPlikow
     */
    private static void stworzMapkeParametry(Osrodek osrodek, GeneratorMapek generatorMapek) throws WyjatekSystemuPlikow {
        dodajWezlyDoMapki(osrodek, generatorMapek);

        for (Trasa t: osrodek.trasy()) {
            generatorMapek.dodajKrawedz(t.poczatek().id(),
                    t.koniec().id(),
                    new StylKrawedzi(StylLinii.CIAGLA),
                    List.of(t.pierwszaLiniaParametrow(),
                            t.drugaLiniaParametrow()));
        }

        for (Wyciag w: osrodek.wyciagi()) {
            generatorMapek.dodajKrawedz(w.poczatek().id(),
                    w.koniec().id(),
                    new StylKrawedzi(StylLinii.PRZERYWANA),
                    List.of(w.pierwszaLiniaParametrow(),
                            w.drugaLiniaParametrow()));
        }

        generatorMapek.tworzMapke("Parametry");
        generatorMapek.zeruj();
    }

    /**
     * Tworzy mapkę statystyk tras i wyciągów.
     * @param osrodek
     * @param generatorMapek
     * @param poczatekDnia
     * @throws WyjatekSystemuPlikow
     */
    private static void stworzMapkeStatystyki(
            Osrodek osrodek,
            GeneratorMapek generatorMapek,
            Moment poczatekDnia
    ) throws WyjatekSystemuPlikow {
        dodajWezlyDoMapki(osrodek, generatorMapek);

        for (Trasa t: osrodek.trasy()) {
            generatorMapek.dodajKrawedz(t.poczatek().id(),
                    t.koniec().id(),
                    new StylKrawedzi(StylLinii.CIAGLA),
                    List.of(t.pierwszaLiniaStatystyk(),
                            t.drugaLinaiStatystyk()));
        }

        for (Wyciag w: osrodek.wyciagi()) {
            generatorMapek.dodajKrawedz(w.poczatek().id(),
                    w.koniec().id(),
                    new StylKrawedzi(StylLinii.PRZERYWANA),
                    List.of(w.pierwszaLiniaStatystyk(poczatekDnia),
                            w.drugaLinaiStatystyk(poczatekDnia)));
        }

        generatorMapek.tworzMapke("Statystyki");
        generatorMapek.zeruj();
    }

    /**
     * Tworzy mapki statystyk sportowców.
     * @param osrodek
     * @param sportowcy
     * @param generatorMapek
     * @throws WyjatekSystemuPlikow
     */
    private static void stworzMapkiSportowcow(
            Osrodek osrodek,
            Sportowiec[] sportowcy,
            GeneratorMapek generatorMapek
    ) throws WyjatekSystemuPlikow {

        for (Sportowiec sportowiec: sportowcy) {
            if (sportowiec.historiaSportowca() != null) {
                // sledzony = true /iff historia != null

                dodajWezlyDoMapki(osrodek, generatorMapek);

                HashMap<Krawedz, String> przejazdy = sportowiec.historiaSportowca().kolejnoscAkcjiSportowca();
                for (Trasa t: osrodek.trasy()) {
                    if (!przejazdy.containsKey(t)) {
                        przejazdy.put(t, "");
                    }
                    int liczbaWizyt = sportowiec.historiaSportowca().liczbaWizyt(t);
                    generatorMapek.dodajKrawedz(t.poczatek().id(),
                            t.koniec().id(),
                            new StylKrawedzi(StylLinii.CIAGLA),
                            String.format("t%d(%d): ", t.id(), liczbaWizyt) + przejazdy.get(t));
                }

                for (Wyciag w: osrodek.wyciagi()) {
                    if (!przejazdy.containsKey(w)) {
                        przejazdy.put(w, "");
                    }
                    int liczbaWizyt = sportowiec.historiaSportowca().liczbaWizyt(w);
                    generatorMapek.dodajKrawedz(w.poczatek().id(),
                            w.koniec().id(),
                            new StylKrawedzi(StylLinii.PRZERYWANA),
                            String.format("w%d(%d): ", w.id(), liczbaWizyt) + przejazdy.get(w));
                }

                generatorMapek.tworzMapke(String.format("Sportowiec%d", sportowiec.id()));
                generatorMapek.zeruj();
            }
        }
    }
}
