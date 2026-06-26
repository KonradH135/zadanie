package duzeZadanie.kolejkaZdarzen.zdarzenia;

import duzeZadanie.czas.Moment;
import duzeZadanie.dziennik.Dziennik;

public abstract class Zdarzenie implements Comparable<Zdarzenie> {

    private static int liczbaZdarzen; // globalna liczba zdarzen, w celu zachowania stabilnosci

    private int numerZdarzenia;

    protected final Moment moment;

    public Zdarzenie(Moment moment) {
        this.moment = moment;
        numerZdarzenia = liczbaZdarzen;
        liczbaZdarzen++;
    }

    public Moment moment() {
        return moment;
    }

    public int numerZdarzenia() {
        return numerZdarzenia;
    }

    /**
     * Pozwala przeprowadzać kilka symulacji w jednej metodzie main
     * zerujac licznik wszystkich zdarzeń. Za to czy nowo tworzone zdarzenia zależą
     * od poprzednich odpowiada Symulacji.
     */
    public static void noweZdarzenia() {
        liczbaZdarzen = 0;
    }

    /**
     * Wywołuje odpowiednie efekty zdarzenia i daje nowo stworzone zdarzenia
     * w kolejności niemalejących momentów.
     */
    public abstract Zdarzenie[] przetworz(Dziennik dziennik);

    public abstract boolean czyPrzetwarzacPoZakonczeniuSymulacji();

    @Override
    public String toString() {
        return "Zdarzenie [moment=" + moment + "]";
    }

    @Override
    public int compareTo(Zdarzenie inne) {
        int wynikCzasu = this.moment.compareTo(inne.moment);
        if (wynikCzasu != 0) {
            return wynikCzasu;
        }
        return Integer.compare(this.numerZdarzenia, inne.numerZdarzenia());
    }
}
