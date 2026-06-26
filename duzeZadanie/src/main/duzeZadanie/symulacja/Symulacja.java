package duzeZadanie.symulacja;

import duzeZadanie.czas.Moment;
import duzeZadanie.dziennik.Dziennik;
import duzeZadanie.kolejkaZdarzen.zdarzenia.OdjazdWyciagu;
import duzeZadanie.kolejkaZdarzen.zdarzenia.PoczatekDnia;
import duzeZadanie.kolejkaZdarzen.zdarzenia.Zdarzenie;
import duzeZadanie.osrodek.Osrodek;
import duzeZadanie.osrodek.krawedz.wyciag.Wyciag;
import duzeZadanie.raporty.Raportant;
import duzeZadanie.sportowcy.Sportowiec;
import kadra.mapki.pliki.WyjatekSystemuPlikow;
import java.util.PriorityQueue;

public class Symulacja {

    private static final Moment POCZATEK_DNIA = new Moment(9, 0, 0);

    private static final Moment KONIEC_SYMULACJI = new Moment(15, 0, 0);

    public void przeprowadzSymulacje(Dziennik dziennik,
        PriorityQueue<Zdarzenie> kolejkaZdarzen,
        Osrodek osrodek,
        Sportowiec[] sportowcy,
        String katalog) throws WyjatekSystemuPlikow {

        przygotujPoczatkoweZdarzenia(kolejkaZdarzen, osrodek, sportowcy);
        glownaPetla(kolejkaZdarzen, dziennik);
        Raportant.raportuj(osrodek, sportowcy, dziennik, POCZATEK_DNIA, KONIEC_SYMULACJI, katalog);
    }

    /**
     * Inicjuje kolejkę zdarzeń poprzez wrzucenie na nią początkowych zdarzeń:
     * 1. Początek dnia na stoku dla każdego sportowca zgodnie z jego momentem startu.
     * 2. Pierwszy odjazd wagonika dla każdego wyciągu dokładnie o 9:00:00.
     */
    private void przygotujPoczatkoweZdarzenia(PriorityQueue<Zdarzenie> kolejkaZdarzen, Osrodek osrodek, Sportowiec[] sportowcy) {
        Zdarzenie.noweZdarzenia();
        for (Sportowiec sportowiec : sportowcy) {
            kolejkaZdarzen.add(new PoczatekDnia(sportowiec.momentStartu(), sportowiec.wezelStartowy(), sportowiec));
        }
        for (Wyciag wyciag : osrodek.wyciagi()) {
            kolejkaZdarzen.add(new OdjazdWyciagu(POCZATEK_DNIA, wyciag));
        }
    }

    /**
     * Dopóki kolejka zdarzeń nie jest pusta, kolejno:
     * 1. Zdejmujemy następne zdarzenie z kolejki,
     * 2. Przetwarzamy je,
     * 3. Dorzucamy na kolejkę nowe zdarzenia, o ile chcemy je jeszcze przetwarzać.
     */
    private void glownaPetla(PriorityQueue<Zdarzenie> kolejkaZdarzen, Dziennik dziennik) {
        while (!kolejkaZdarzen.isEmpty()) {
            Zdarzenie nastepneZdarzenie = kolejkaZdarzen.poll();
            Zdarzenie[] noweZdarzenia = nastepneZdarzenie.przetworz(dziennik);

            for (Zdarzenie noweZdarzenie : noweZdarzenia) {
                if (noweZdarzenie != null) {
                    if (noweZdarzenie.moment().wczesniejNiz(KONIEC_SYMULACJI)
                            || noweZdarzenie.czyPrzetwarzacPoZakonczeniuSymulacji()) {
                        kolejkaZdarzen.add(noweZdarzenie);
                    }
                }
                else {
                    assert true : "W kolejce są zdarzenia, które są nullem.";
                }
            }
        }
    }
}
