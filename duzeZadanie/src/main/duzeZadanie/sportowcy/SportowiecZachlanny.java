package duzeZadanie.sportowcy;

import duzeZadanie.czas.Moment;
import duzeZadanie.kolejkaZdarzen.zdarzenia.DolaczenieDoKolejki;
import duzeZadanie.kolejkaZdarzen.zdarzenia.RozpoczecieZjazdu;
import duzeZadanie.kolejkaZdarzen.zdarzenia.Zdarzenie;
import duzeZadanie.losowosc.MaszynaLosujaca;
import duzeZadanie.osrodek.Wezel;
import duzeZadanie.osrodek.krawedz.Krawedz;
import duzeZadanie.osrodek.krawedz.Trasa;
import duzeZadanie.osrodek.krawedz.wyciag.Wyciag;
import duzeZadanie.sportowcy.bfs.BFSPoGrafie;

import java.util.ArrayDeque;

public class SportowiecZachlanny extends Sportowiec {

    private final static char RODZAJ = 'Z';

    private ArrayDeque<Krawedz> plan;

    public SportowiecZachlanny(int id,
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

        plan = new ArrayDeque<>();
    }

    public char rodzaj() {
        return RODZAJ;
    }

    /**
     * Wybiera trase do której sportowiec chce się udać, potem znajdując mu
     * najbardziej optymalną ścieżkę. Szuka trasy, która jest najatrakcyjniejsza
     * w całym ośrodku, w którym jeździ sportowiec.
     * @param moment
     * @param obecnyWezel
     * @return
     */
    protected Zdarzenie kontynuujZaplanowanaPodroz(Moment moment, Wezel obecnyWezel) {
        Krawedz nastepnaKrawedz = plan.pollLast();
        if (plan.isEmpty()) {
            ustawCzyZaplanowane(false);
        }

        if (nastepnaKrawedz instanceof Trasa) {
            Trasa nastepnaTrasa = (Trasa)nastepnaKrawedz;
            return new RozpoczecieZjazdu(moment, nastepnaTrasa, this);
        }
        else if (nastepnaKrawedz instanceof Wyciag) {
            Wyciag nastepnyWyciag = (Wyciag) nastepnaKrawedz;
            return new DolaczenieDoKolejki(moment, nastepnyWyciag, this);
        }
        assert true : "Błąd deweloperski w klasie SportowiecZachlanny!";
        return null;
    }

    /**
     * Przemyślana decyzja polega na wybraniu najatrakcyjniejszej trasy spośród sumy tras
     * zaczynających się w obecnym wierzchołku oraz tras których początkowe wierzchołki
     * są osiągalne wyciągiem zaczynającym się w obecnym wierzchołku.
     */
    protected Zdarzenie podejmijPrzemyslanaDecyzje(Moment moment, Wezel obecnyWezel) {
        Trasa wymarzonaTrasa = trasy()[0];
        for (Trasa t: trasy()) {
            if (lacznaAtrakcyjnosc(t) > lacznaAtrakcyjnosc(wymarzonaTrasa)) {
                wymarzonaTrasa = t;
            }
        }

        plan = BFSPoGrafie.bfs(obecnyWezel, wymarzonaTrasa);
        ustawCzyZaplanowane(true);
        return kontynuujZaplanowanaPodroz(moment, obecnyWezel);
    }
}
