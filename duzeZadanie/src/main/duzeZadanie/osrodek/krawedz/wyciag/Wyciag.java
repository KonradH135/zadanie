package duzeZadanie.osrodek.krawedz.wyciag;

import duzeZadanie.czas.Interwal;
import duzeZadanie.czas.Moment;
import duzeZadanie.dziennik.Dziennik;
import duzeZadanie.kolejkaZdarzen.zdarzenia.DotarcieDoWezla;
import duzeZadanie.kolejkaZdarzen.zdarzenia.OdjazdWyciagu;
import duzeZadanie.kolejkaZdarzen.zdarzenia.Zdarzenie;
import duzeZadanie.osrodek.Wezel;
import duzeZadanie.osrodek.krawedz.Krawedz;
import duzeZadanie.sportowcy.Sportowiec;

public class Wyciag extends Krawedz {

    private final Interwal odstepMiedzyOdjazdami;

    private final int ladownosc;

    private final KolejkaSportowcow obecnaKolejka;

    private int lacznaLiczbaPasazerow;

    private int maksymalnaDlugoscKolejki;

    private int sumaDlugosciKolejki;

    private Moment ostatniaAktualizacja;

    public Wyciag(int id,
        Wezel poczatek,
        Wezel koniec,
        Interwal odstepMiedzyOdjazdami,
        Interwal dlugoscPrzejazdu,
        int ladownosc) {
        super(id, poczatek, koniec, dlugoscPrzejazdu);

        assert poczatek.wysokosc() < koniec.wysokosc()
            : String.format("Wyciąg %d prowadzi w dół: %d -> %d", id, poczatek.wysokosc(), koniec.wysokosc());

        this.odstepMiedzyOdjazdami = odstepMiedzyOdjazdami;
        this.ladownosc = ladownosc;
        obecnaKolejka = new BuforCyklicznySportowcow();
        lacznaLiczbaPasazerow = 0;
        maksymalnaDlugoscKolejki = 0;
        sumaDlugosciKolejki = 0;
        ostatniaAktualizacja = new Moment(9,0,0);
    }

    /**
     * Aktualizuje Sume Dlugosci Kolejki z uwzglednieniem ewentualnych przesuniec.
     */
    private void aktualizujSumeKolejki(Moment moment, boolean czyDodac, int iloscDodatkowych) {
        sumaDlugosciKolejki += obecnaKolejka.rozmiar() * (moment.sekundy() - ostatniaAktualizacja.sekundy());
        ostatniaAktualizacja = moment;
        if (czyDodac) {
            sumaDlugosciKolejki += iloscDodatkowych;
        }
        else {
            sumaDlugosciKolejki -= iloscDodatkowych;
        }
    }

    /**
     * Należy wywołać przed wypisywaniem statystyk.
     * @param koniecSymulacji
     */
    public void koniecSymulacji(Moment koniecSymulacji) {
        aktualizujSumeKolejki(koniecSymulacji, true, 0);
    }

    /**
     * Dodaje sportowca do kolejki.
     * @param moment
     * @param sportowiec
     */
    public void dodajDoKolejki(Moment moment, Sportowiec sportowiec) {
        aktualizujSumeKolejki(moment, true, 1);

        obecnaKolejka.dodaj(sportowiec);

        maksymalnaDlugoscKolejki = Math.max(obecnaKolejka.rozmiar(), maksymalnaDlugoscKolejki);
    }

    /**
     * Symuluje odjazd następnego wagonika oraz daje nowo stworzone zdarzenia:
     * 1. Zdarzenie reprezentujące odjazd następnego wagonika.
     * 2. Zdarzenia reprezentujące dotarcie do końca wyciagu sportowców, którzy załapali się na obecny odjazd.
     */
    public Zdarzenie[] odjazd(Moment moment, Dziennik dziennik) {
        aktualizujSumeKolejki(moment, false, Math.min(obecnaKolejka.rozmiar(), ladownosc));

        Sportowiec[] odjezdzajacySportowcy = obecnaKolejka.zdejmij(Math.min(obecnaKolejka.rozmiar(), ladownosc));

        for (Sportowiec sportowiec : odjezdzajacySportowcy) {
            dziennik.dodajWpisZeSportowcem(moment, sportowiec, String.format("rozpoczął wjazd %s", toString()));
            sportowiec.wjedz(this);
        }

        Zdarzenie[] noweZdarzenia = new Zdarzenie[1 + odjezdzajacySportowcy.length];
        noweZdarzenia[0] = new OdjazdWyciagu(moment.dodajInterwal(odstepMiedzyOdjazdami), this);

        for (int i = 0; i < odjezdzajacySportowcy.length; i++) {
            noweZdarzenia[1 + i] = new DotarcieDoWezla(moment.dodajInterwal(dlugosc()),
                this,
                koniec(),
                odjezdzajacySportowcy[i]);
        }

        lacznaLiczbaPasazerow += odjezdzajacySportowcy.length;

        return noweZdarzenia;
    }

    private int sredniaDlugoscKolejki(Moment poczatekSymulacji) {
        return (int)Math.round(
                (double) sumaDlugosciKolejki / (ostatniaAktualizacja.sekundy() - poczatekSymulacji.sekundy()));
    }

    private int maksymalnaMożliwaLiczbaPasazerow(Moment poczatekSymulacji) {
        int maksymalnaLiczbaOdjazdow = (ostatniaAktualizacja.sekundy() - poczatekSymulacji.sekundy())
                / odstepMiedzyOdjazdami.sekundy();
        return maksymalnaLiczbaOdjazdow * ladownosc;
    }

    private int procentZajetychMiejsca(Moment poczatekSymulacji) {
        return (int) Math.round(
                (double) 100 * lacznaLiczbaPasazerow / maksymalnaMożliwaLiczbaPasazerow(poczatekSymulacji));
    }

    public int lacznaLiczbaPasazerow() {
        return lacznaLiczbaPasazerow;
    }

    public int maksymalnaDlugoscKolejki() {
        return maksymalnaDlugoscKolejki;
    }

    @Override
    public String pierwszaLiniaParametrow() {
        return String.format("w%d: %d os. co %ds",
                id(),
                ladownosc,
                odstepMiedzyOdjazdami.sekundy());
    }

    @Override
    public String drugaLiniaParametrow() {
        return String.format("czas: %ds", dlugosc().sekundy());
    }

    public String pierwszaLiniaStatystyk(Moment poczatekDnia) {
        return String.format("w%d: kol: %d(śr), %d(maks)",
                id(),
                sredniaDlugoscKolejki(poczatekDnia),
                maksymalnaDlugoscKolejki);
    }

    public String drugaLinaiStatystyk(Moment poczatekDnia) {
        return String.format("wjazdy: %d / %d (%d%%)",
                lacznaLiczbaPasazerow,
                maksymalnaMożliwaLiczbaPasazerow(poczatekDnia),
                procentZajetychMiejsca(poczatekDnia));
    }

    @Override
    public String wypiszStatystyki() {
        return String.format("%d pasażerów", lacznaLiczbaPasazerow);
    }

    @Override
    public String toString() {
        return String.format("Wyciąg nr %d", id());
    }
}
