package duzeZadanie.osrodek.krawedz.wyciag;

import duzeZadanie.czas.Interwal;
import duzeZadanie.czas.Moment;
import duzeZadanie.dziennik.Dziennik;
import duzeZadanie.dziennik.DziennikStandardoweWyjscie;
import duzeZadanie.losowosc.DeterministycznaMaszynaLosujaca;
import duzeZadanie.losowosc.MaszynaLosujaca;
import duzeZadanie.osrodek.Wezel;
import duzeZadanie.osrodek.krawedz.Trasa;
import duzeZadanie.sportowcy.Sportowiec;
import duzeZadanie.sportowcy.TworcaSportowcow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class WyciagTests {

    private Dziennik dziennik;
    private Wezel poczatek, koniec;
    private Wyciag wyciag;
    private MaszynaLosujaca maszynaLosujaca;


    @BeforeEach
    void przygotujSrodowisko() {
        dziennik = new DziennikStandardoweWyjscie();
        poczatek = new Wezel(1, 1, 1, 1, true);
        koniec = new Wezel(2, 2, 2, 2, true);
        // Tworzymy wyciąg o ładowności 3
        wyciag = new Wyciag(1, poczatek, koniec, new Interwal(1), new Interwal(1), 3);
        maszynaLosujaca = new DeterministycznaMaszynaLosujaca(10);
    }

    // Metoda pomocnicza tworzaca sportowca
    private Sportowiec stworzTestowegoSportowca(int id) {
        return TworcaSportowcow.tworzSportowca(
                id,
                1,
                1,
                1,
                1,
                1,
                'L',
                1,
                true,
                poczatek,
                new Moment(9, 0, 0),
                maszynaLosujaca,
                new Trasa[]{});
    }

    @Test
    public void testDlugaKolejka() {
        Sportowiec s1 = stworzTestowegoSportowca(1);
        Sportowiec s2 = stworzTestowegoSportowca(2);
        Sportowiec s3 = stworzTestowegoSportowca(3);
        Sportowiec s4 = stworzTestowegoSportowca(4);

        // dodanie do kolejki
        wyciag.dodajDoKolejki(new Moment(9, 0, 0), s1);
        wyciag.dodajDoKolejki(new Moment(9, 0, 0), s2);
        wyciag.dodajDoKolejki(new Moment(9, 0, 0), s3);
        wyciag.dodajDoKolejki(new Moment(9, 0, 0), s4);

        // akcja odjazdu wyciagu
        wyciag.odjazd(new Moment(9, 0, 1), dziennik);

        assertEquals(3, wyciag.lacznaLiczbaPasazerow());
    }

    @Test
    public void testKrotkaKolejka() {
        Sportowiec s1 = stworzTestowegoSportowca(1);
        Sportowiec s2 = stworzTestowegoSportowca(2);

        // dodanie do kolejki
        wyciag.dodajDoKolejki(new Moment(9, 0, 0), s1);
        wyciag.dodajDoKolejki(new Moment(9, 0, 0), s2);

        // akcja odjazdu wyciagu
        wyciag.odjazd(new Moment(9, 0, 1), dziennik);

        assertEquals(2, wyciag.lacznaLiczbaPasazerow());
    }

    @Test
    public void testPrzyjsciePoOdjezdzie() {
        Sportowiec s1 = stworzTestowegoSportowca(1);
        Sportowiec s2 = stworzTestowegoSportowca(2);
        Sportowiec s3 = stworzTestowegoSportowca(3);
        Sportowiec s4 = stworzTestowegoSportowca(4);
        Sportowiec s5 = stworzTestowegoSportowca(5);

        // dodanie do kolejki
        wyciag.dodajDoKolejki(new Moment(9, 0, 0), s1);
        wyciag.dodajDoKolejki(new Moment(9, 0, 0), s2);
        wyciag.dodajDoKolejki(new Moment(9, 0, 0), s3);
        wyciag.dodajDoKolejki(new Moment(9, 0, 0), s4);

        // akcja odjazdu wyciagu
        wyciag.odjazd(new Moment(9, 0, 1), dziennik);

        // pozniejsze przyjscie
        wyciag.dodajDoKolejki(new Moment(9, 0, 5), s5);

        assertEquals(4, wyciag.maksymalnaDlugoscKolejki());
    }
}
