package duzeZadanie.sportowcy;

import duzeZadanie.czas.Moment;
import duzeZadanie.kolejkaZdarzen.zdarzenia.Zdarzenie;
import duzeZadanie.losowosc.MaszynaLosujaca;
import duzeZadanie.osrodek.Wezel;
import duzeZadanie.osrodek.krawedz.Trasa;
import duzeZadanie.osrodek.krawedz.wyciag.Wyciag;

public class SportowiecLokalny extends Sportowiec {

    private final static char RODZAJ = 'L';

    public SportowiecLokalny(int id,
        int poziomZaawansowania,
        double wspolczynnikSpontanicznosci,
        double wspolczynnikZnudzenia,
        double wspolczynnikTrudnosci,
        double wspolczynnikNawierzchni,
        double wagaZnudzenia,
        boolean sledzony,
        Wezel wezelStartowy,
        Moment momentStartu,
        MaszynaLosujaca maszynaLosujaca,
        Trasa[] trasy) {
            
        super(id,
            poziomZaawansowania,
            wspolczynnikSpontanicznosci,
            wspolczynnikZnudzenia,
            wspolczynnikTrudnosci,
            wspolczynnikNawierzchni,
            wagaZnudzenia,
            sledzony,
            wezelStartowy,
            momentStartu,
            maszynaLosujaca,
            trasy);
    }

    public char rodzaj() {
        return RODZAJ;
    }

    /** 
     * Ta metoda niepowinna się wykonać, gdyż sportowcy lokalni nie planują podróży.
    */
    protected Zdarzenie kontynuujZaplanowanaPodroz(Moment moment, Wezel obecnyWezel) {
        assert true : "Spotrowiec Lokalny zaplanował więcej niż jeden zjazd!";
        return null;
    }

    /**
     * Przemyślana decyzja polega na wybraniu najatrakcyjniejszej trasy spośród sumy tras
     * zaczynających się w obecnym wierzchołku oraz tras których początkowe wierzchołki
     * są osiągalne wyciągiem zaczynającym się w obecnym wierzchołku.
     */
    protected Zdarzenie podejmijPrzemyslanaDecyzje(Moment moment, Wezel obecnyWezel) {
        Trasa[] bezposrednieTrasy = obecnyWezel.wychodzaceTrasy();
        Wyciag[] wyciagi = obecnyWezel.wychodzaceWyciagi();

        double najwiekszaAtrakcyjnosc = -1;
        Trasa najlepszaTrasa = null;
        Wyciag nastepnyWyciag = null;

        for (Trasa trasa : bezposrednieTrasy) {
            double atrakcyjnosc = lacznaAtrakcyjnosc(trasa);
            if (atrakcyjnosc > najwiekszaAtrakcyjnosc) {
                najwiekszaAtrakcyjnosc = atrakcyjnosc;
                najlepszaTrasa = trasa;
            }
        }

        for (Wyciag wyciag : wyciagi) {
            for (Trasa trasa : wyciag.koniec().wychodzaceTrasy()) {
                double atrakcyjnosc = lacznaAtrakcyjnosc(trasa);
                if (atrakcyjnosc > najwiekszaAtrakcyjnosc) {
                    najwiekszaAtrakcyjnosc = atrakcyjnosc;
                    najlepszaTrasa = trasa;
                    nastepnyWyciag = wyciag;
                }
            }
        }

        if (najlepszaTrasa == null) {
            // Zbiór dostępnych tras jest pusty, wiec wybieramy dowolny wyciąg.
            // Mamy gwarancję że taki istnieje, ponieważ graf jest silnie spójny.
            return nastepnyKrokWyciag(moment, wyciagi[0]);
        } else if (nastepnyWyciag == null) {
            // Wybrana trasa zaczyna się w obecnym wierzchołku.
            return nastepnyKrokTrasa(moment, najlepszaTrasa);
        } else {
            // Musimy wjechac wyciągiem by dotrzeć do upatrzonej trasy.
            return nastepnyKrokWyciag(moment, nastepnyWyciag);
        }
    }
}
