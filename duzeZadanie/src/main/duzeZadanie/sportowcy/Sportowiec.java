package duzeZadanie.sportowcy;

import duzeZadanie.czas.Interwal;
import duzeZadanie.czas.Moment;
import duzeZadanie.kolejkaZdarzen.zdarzenia.DolaczenieDoKolejki;
import duzeZadanie.kolejkaZdarzen.zdarzenia.RozpoczecieZjazdu;
import duzeZadanie.kolejkaZdarzen.zdarzenia.Zdarzenie;
import duzeZadanie.losowosc.MaszynaLosujaca;
import duzeZadanie.osrodek.Wezel;
import duzeZadanie.osrodek.krawedz.Trasa;
import duzeZadanie.osrodek.krawedz.wyciag.Wyciag;

public abstract class Sportowiec {

    private final int WSPOLCZYNNIK_ROZNICY_POZIOMOW_TRUDNOSCI = 5;

    private final int WSPOLCZYNNIK_ROZNICY_POZIOMOW_TRUDNOSCI_LATWEJ_TRASY = 7;

    private final double DOMYSLNA_ATRAKCYJNOSC_LATWEJ_TRASY = 0.2;

    private final int id;

    private final int poziomZaawansowania; // {0, 1, ..., 10}

    private final double wspolczynnikSpontanicznosci; // [0, 1]

    private final double wspolczynnikZnudzenia; // [0, 1]

    private final double wspolczynnikTrudnosci; // [0, 1]

    private final double wspolczynnikNawierzchni; // [0, 1]

    private final double wagaZnudzenia; // [0, 1]

    private final boolean sledzony;

    private final Wezel wezelStartowy;

    private final Moment momentStartu;

    private final MaszynaLosujaca maszynaLosujaca;

    private boolean czyZaplanowaneDalszeKroki;

    private double[] znudzenieTrasa;

    private int[] ostatniZjadzTrasa;

    private int licznikZjazdow;

    private Trasa[] trasy;

    private HistoriaSportowca historiaSportowca;

    public Sportowiec(int id,
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

        this.id = id;
        this.poziomZaawansowania = poziomZaawansowania;
        this.wspolczynnikSpontanicznosci = wspolczynnikSpontanicznosci;
        this.wspolczynnikZnudzenia = wspolczynnikZnudzenia;
        this.wspolczynnikTrudnosci = wspolczynnikTrudnosci;
        this.wspolczynnikNawierzchni = wspolczynnikNawierzchni;
        this.wagaZnudzenia = wagaZnudzenia;
        this.sledzony = sledzony;
        this.wezelStartowy = wezelStartowy;
        this.momentStartu = momentStartu;
        this.maszynaLosujaca = maszynaLosujaca;
        this.trasy = trasy;
        znudzenieTrasa = new double[trasy.length];
        ostatniZjadzTrasa = new int[trasy.length];
        czyZaplanowaneDalszeKroki = false;
        licznikZjazdow = 0;

        if (sledzony) {
            historiaSportowca = new HistoriaSportowca(this);
        }
        else {
            historiaSportowca = null;
        }
    }

    public int id() {
        return id;
    }

    public int poziomZaawansowania() {
        return poziomZaawansowania;
    }

    public double wspolczynnikZnudzenia() {
        return wspolczynnikZnudzenia;
    }

    public double wagaZnudzenia() {
        return wagaZnudzenia;
    }

    public double wspolczynnikTrudnosci() {
        return wspolczynnikTrudnosci;
    }

    public double wspolczynnikNawierzchni() {
        return wspolczynnikNawierzchni;
    }

    public boolean sledzony() {
        return sledzony;
    }

    public Wezel wezelStartowy() {
        return wezelStartowy;
    }

    public Moment momentStartu() {
        return momentStartu;
    }

    public Trasa[] trasy() {
        return trasy;
    }

    public HistoriaSportowca historiaSportowca() {
        return historiaSportowca;
    }

    /**
     * Dla zadanego obecnego węzła daje wydarzenie opisujace następny
     * krok sportowca: dołączenie do kolejki na wyciągu lub rozpoczęcie zjazdu trasą.
     */
    public Zdarzenie nastepnyKrok(Moment moment, Wezel obecnyWezel) {
        if (czyZaplanowaneDalszeKroki) {
            return kontynuujZaplanowanaPodroz(moment, obecnyWezel);
        }

        if (maszynaLosujaca.losowyDouble(0, 1) < wspolczynnikSpontanicznosci) {
            return podejmijSpontanicznaDecyzje(moment, obecnyWezel);
        } else {
            return podejmijPrzemyslanaDecyzje(moment, obecnyWezel);
        }
    }

    /**
     * Aktualizuje wartość znudzenia podczas zjazdu trasa oraz historie (jesli śledzony).
     */
    public void zjedz(Trasa trasa) {
        licznikZjazdow++;
        ostatniZjadzTrasa[trasa.id()] = licznikZjazdow;
        znudzenieTrasa[trasa.id()] = wspolczynnikZnudzenia
                + (1 - wspolczynnikZnudzenia) * znudzenieTrasa[trasa.id()];
        if (historiaSportowca != null) {
            // historia != null /iff sledzony = true
            historiaSportowca.wydarzenie(trasa);
        }
    }

    /**
     * Aktualizuje historie sportowca (jeśli śledzony).
     * @param wyciag
     */
    public void wjedz(Wyciag wyciag) {
        if (historiaSportowca != null) {
            // historia != null /iff sledzony = true
            historiaSportowca.wydarzenie(wyciag);
        }
    }

    protected abstract Zdarzenie podejmijPrzemyslanaDecyzje(Moment moment, Wezel obecnyWezel);

    protected abstract Zdarzenie kontynuujZaplanowanaPodroz(Moment moment, Wezel obecnyWezel);

    /**
     * Spontaniczna decyzja polega na wylosowaniu następnej krawędzi jednostajnie
     * spośród wszystkich zaczynających się w obecnym wierzchołku.
     */
    protected Zdarzenie podejmijSpontanicznaDecyzje(Moment moment, Wezel obecnyWezel) {
        Trasa[] bezposrednieTrasy = obecnyWezel.wychodzaceTrasy();
        Wyciag[] wyciagi = obecnyWezel.wychodzaceWyciagi();

        int losowyWybor = maszynaLosujaca.losowyInt(0, bezposrednieTrasy.length + wyciagi.length);

        if (losowyWybor < bezposrednieTrasy.length) {
            return nastepnyKrokTrasa(moment, bezposrednieTrasy[losowyWybor]);
        } else {
            return nastepnyKrokWyciag(moment, wyciagi[losowyWybor - bezposrednieTrasy.length]);
        }
    }

    /**
     * Tworzy zdarzenie dołączenia do kolejki do wyciągu w nastepnym kroku.
     */
    protected DolaczenieDoKolejki nastepnyKrokWyciag(Moment moment, Wyciag wyciag) {
        return new DolaczenieDoKolejki(moment, wyciag, this);
    }

    /**
     * Tworzy zdarzenie zjazdu bezpośrednią trasą w następnym kroku.
     */
    protected RozpoczecieZjazdu nastepnyKrokTrasa(Moment moment, Trasa trasa) {
        return new RozpoczecieZjazdu(moment, trasa, this);
    }

    /**
     * Wylicza atrakcyjność trasy na podstawie wzoru z treści zadania.
     */
    protected double lacznaAtrakcyjnosc(Trasa trasa) {
        return wspolczynnikTrudnosci * atrakcyjnoscPoziomuTrudnosci(trasa)
            + wspolczynnikNawierzchni * trasa.wyrownanieNawierzchni()
            + wagaZnudzenia * (1 - poziomZnudzeniaTrasa(trasa));
    }

    /**
     * Wylicza poziom znudzenia trasą na podstawie wzoru z treści zadania.
     * @param trasa
     * @return
     */
    protected double poziomZnudzeniaTrasa(Trasa trasa) {
        int idT = trasa.id();
        return znudzenieTrasa[idT] * Math.pow(1 - wspolczynnikZnudzenia, licznikZjazdow - ostatniZjadzTrasa[idT]);
    }

    /**
     * Wylicza atrakcyjność pod względem poziomu trudności
     * na podstawie wzoru z treści zadania.
     * @param trasa
     * @return
     */
    protected double atrakcyjnoscPoziomuTrudnosci(Trasa trasa) {
        int poziomTrudnosci = trasa.poziomTrudnosci();

        double roznicaPoziomow = poziomTrudnosci - poziomZaawansowania;

        if (poziomTrudnosci >= poziomZaawansowania + WSPOLCZYNNIK_ROZNICY_POZIOMOW_TRUDNOSCI) {
            return 0;
        } else if (poziomZaawansowania + WSPOLCZYNNIK_ROZNICY_POZIOMOW_TRUDNOSCI > poziomTrudnosci
            && poziomTrudnosci >= poziomZaawansowania) {
            return 1.0 - roznicaPoziomow / (double) WSPOLCZYNNIK_ROZNICY_POZIOMOW_TRUDNOSCI;
        } else {
            return Math.max(DOMYSLNA_ATRAKCYJNOSC_LATWEJ_TRASY,
                1 - (-roznicaPoziomow) / (double) WSPOLCZYNNIK_ROZNICY_POZIOMOW_TRUDNOSCI_LATWEJ_TRASY);
        }
    }

    /**
     * Tworzy kopie sportowca z tymi samymi parametrami ale zwiekszonym id oraz momentem startu.
     * Uzywane na potrzeby tworzenia wielu sportowcow z jednej grupy sportowcow z wejscia.
     */
    public Sportowiec kopia(int przesuniecieId, Interwal przesuniecieMomentuStartu) {
        return TworcaSportowcow.tworzSportowca(id + przesuniecieId,
                poziomZaawansowania,
                wspolczynnikSpontanicznosci,
                wspolczynnikZnudzenia,
                wspolczynnikTrudnosci,
                wspolczynnikNawierzchni,
                rodzaj(),
                wagaZnudzenia,
                sledzony,
                wezelStartowy,
                momentStartu.dodajInterwal(przesuniecieMomentuStartu),
                maszynaLosujaca,
                trasy);
    }

    public abstract char rodzaj();

    protected void ustawCzyZaplanowane(boolean b) {
        czyZaplanowaneDalszeKroki = b;
    }

    @Override
    public String toString() {
        return String.format("Sportowiec nr %d", id);
    }
}
