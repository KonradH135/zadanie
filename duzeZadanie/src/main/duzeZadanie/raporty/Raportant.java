package duzeZadanie.raporty;

import duzeZadanie.czas.Moment;
import duzeZadanie.dziennik.Dziennik;
import duzeZadanie.mapkarz.Mapkarz;
import duzeZadanie.osrodek.Osrodek;
import duzeZadanie.osrodek.krawedz.Krawedz;
import duzeZadanie.osrodek.krawedz.Trasa;
import duzeZadanie.osrodek.krawedz.wyciag.Wyciag;
import duzeZadanie.sportowcy.Sportowiec;
import kadra.mapki.pliki.WyjatekSystemuPlikow;

import java.util.HashMap;

public class Raportant {
    public static void raportuj(
            Osrodek osrodek,
            Sportowiec[] sportowcy,
            Dziennik dziennik,
            Moment poczatekSymulacji,
            Moment koniecSymulacji,
            String katalog
    ) throws WyjatekSystemuPlikow {

        przygotujStatystyki(osrodek, koniecSymulacji);
        zbierzStatystyki(osrodek, sportowcy, dziennik, poczatekSymulacji);
        Mapkarz.stworzMapki(osrodek, sportowcy, poczatekSymulacji, katalog);

    }

    private static void przygotujStatystyki(Osrodek osrodek, Moment koniecSymulacji) {
        for (Wyciag w: osrodek.wyciagi()) {
            w.koniecSymulacji(koniecSymulacji);
        }
    }

    /**
     * Wypisuje statystyki końcowe symulacji.
     */
    private static void zbierzStatystyki(
            Osrodek osrodek,
            Sportowiec[] sportowcy,
            Dziennik dziennik,
            Moment poczatekSymylacji
    ) {
        zbierzParametryKrawedzi(osrodek, dziennik);
        zbierzStatystykiKrawedzi(osrodek, dziennik, poczatekSymylacji);
        zbierzStatystykiSportowcow(osrodek, sportowcy, dziennik);
    }

    /**
     * Wypisuje parametry tras i wyciagów.
     * @param osrodek
     * @param dziennik
     */
    private static void zbierzParametryKrawedzi(Osrodek osrodek, Dziennik dziennik) {
        for (Trasa t: osrodek.trasy()) {
            dziennik.dodajLinie(t.pierwszaLiniaParametrow());
            dziennik.dodajLinie(t.drugaLiniaParametrow());
        }

        for (Wyciag w: osrodek.wyciagi()) {
            dziennik.dodajLinie(w.pierwszaLiniaParametrow());
            dziennik.dodajLinie(w.drugaLiniaParametrow());
        }
    }

    /**
     * Wypisuje statystyki tras i wyciagów.
     * @param osrodek
     * @param dziennik
     * @param poczatekSymulacji
     */
    private static void zbierzStatystykiKrawedzi(Osrodek osrodek, Dziennik dziennik, Moment poczatekSymulacji) {
        for (Trasa t: osrodek.trasy()) {
            dziennik.dodajLinie(t.pierwszaLiniaStatystyk());
            dziennik.dodajLinie(t.drugaLinaiStatystyk());
        }

        for (Wyciag w: osrodek.wyciagi()) {
            dziennik.dodajLinie(w.pierwszaLiniaStatystyk(poczatekSymulacji));
            dziennik.dodajLinie(w.drugaLinaiStatystyk(poczatekSymulacji));
        }
    }

    /**
     * Wypisuje statystyki sportowców.
     * @param osrodek
     * @param sportowcy
     * @param dziennik
     */
    private static void zbierzStatystykiSportowcow(Osrodek osrodek, Sportowiec[] sportowcy, Dziennik dziennik) {
        for (Sportowiec sportowiec: sportowcy) {
            if (sportowiec.sledzony()) {
                String opis = String.format("Statystyki sportowca nr. %d", sportowiec.id());
                dziennik.dodajLinie(opis);

                HashMap<Krawedz, String> przejazdy = sportowiec.historiaSportowca().kolejnoscAkcjiSportowca();
                for (Trasa t: osrodek.trasy()) {
                    if (!przejazdy.containsKey(t)) {
                        przejazdy.put(t, "");
                    }
                    int liczbaWizyt = sportowiec.historiaSportowca().liczbaWizyt(t);
                    dziennik.dodajLinie(String.format("t%d(%d): ", t.id(), liczbaWizyt) + przejazdy.get(t));
                }

                for (Wyciag w: osrodek.wyciagi()) {
                    if (!przejazdy.containsKey(w)) {
                        przejazdy.put(w, "");
                    }
                    int liczbaWizyt = sportowiec.historiaSportowca().liczbaWizyt(w);
                    dziennik.dodajLinie(String.format("w%d(%d): ", w.id(), liczbaWizyt) + przejazdy.get(w));
                }
            }
        }
    }
}
