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

public class SportowiecKolekcjoner extends Sportowiec {

    private final static char RODZAJ = 'K';

    private ArrayDeque<Krawedz> plan;

    private int[] liczbaZjazdow; // id - indeks trasy

    public SportowiecKolekcjoner(int id,
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
        liczbaZjazdow = new int[trasy().length];
    }

    public char rodzaj() {
        return RODZAJ;
    }

    /**
     * Aktualizuje wartość znudzenia podczas zjazdu trasa.
     */
    @Override
    public void zjedz(Trasa trasa) {
        super.zjedz(trasa);
        liczbaZjazdow[trasa.id()]++;
    }

    /**
     * Wybiera trase do której sportowiec chce się udać, potem znajdując mu
     * najbardziej optymalną ścieżkę. Szuka trasy, którą odwiedził najmniej razy.
     * W przypadku remisu rozstrzyga odległość, potem atrakcyjność.
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
        assert true : "Błąd deweloperski w klasie SportowiecKolekcjoner!";
        return null;
    }

    /**
     * Przemyślana decyzja polega na wybraniu najbliższej/najatrakcyjniejszej trasy
     * spośród sumy tras ośrodka takiej, że odległość od obecnego węzła jest najmniejsza.
     */
    protected Zdarzenie podejmijPrzemyslanaDecyzje(Moment moment, Wezel obecnyWezel) {
        Trasa wymarzonaTrasa = trasy()[0];
        ArrayDeque<Krawedz> potencjalnyPlan = BFSPoGrafie.bfs(obecnyWezel, wymarzonaTrasa);
        for (Trasa t: trasy()) {
            if (liczbaZjazdow[wymarzonaTrasa.id()] > liczbaZjazdow[t.id()]) {
                wymarzonaTrasa = t;
                potencjalnyPlan = BFSPoGrafie.bfs(obecnyWezel, wymarzonaTrasa);
            }
            else if (liczbaZjazdow[wymarzonaTrasa.id()] == liczbaZjazdow[t.id()]) {
                ArrayDeque<Krawedz> rozwazanyPlan = BFSPoGrafie.bfs(obecnyWezel, wymarzonaTrasa);
                if (rozwazanyPlan.size() < potencjalnyPlan.size()) {
                    wymarzonaTrasa = t;
                    potencjalnyPlan = rozwazanyPlan;
                }
                else if (rozwazanyPlan.size() == potencjalnyPlan.size()
                    && lacznaAtrakcyjnosc(wymarzonaTrasa) < lacznaAtrakcyjnosc(t)) {
                    wymarzonaTrasa = t;
                    potencjalnyPlan = rozwazanyPlan;
                }
            }
        }

        // Został znaleziony plan podróży, czyli można go beztrosko realizować.
        ustawCzyZaplanowane(true);
        plan = potencjalnyPlan;
        return kontynuujZaplanowanaPodroz(moment, obecnyWezel);
    }
}
