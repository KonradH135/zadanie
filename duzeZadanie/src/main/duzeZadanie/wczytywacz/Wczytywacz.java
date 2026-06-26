package duzeZadanie.wczytywacz;

import java.util.Scanner;

import duzeZadanie.czas.Interwal;
import duzeZadanie.czas.Moment;
import duzeZadanie.losowosc.MaszynaLosujaca;
import duzeZadanie.osrodek.Wezel;
import duzeZadanie.osrodek.krawedz.Trasa;
import duzeZadanie.osrodek.krawedz.wyciag.Wyciag;
import duzeZadanie.sportowcy.GrupaSportowcow;
import duzeZadanie.sportowcy.Sportowiec;
import duzeZadanie.sportowcy.TworcaSportowcow;

public class Wczytywacz {

    private final Scanner scanner;

    private final MaszynaLosujaca maszynaLosujaca;

    public Wczytywacz(Scanner scanner, MaszynaLosujaca maszynaLosujaca) {
        this.scanner = scanner;
        this.maszynaLosujaca = maszynaLosujaca;
    }

    public DaneWejsciowe wczytajWejscie() {
        Wezel[] wezly = wczytajWezly();
        Wyciag[] wyciagi = wczytajWyciagi(wezly);
        Trasa[] trasy = wczytajTrasy(wezly);
        GrupaSportowcow[] grupySportowcow = wczytajGrupySportowcow(trasy, wezly);
        return new DaneWejsciowe(wezly, trasy, wyciagi, grupySportowcow);
    }

    private Wezel[] wczytajWezly() {
        int liczbaWezlow = scanner.nextInt();

        Wezel[] wezly = new Wezel[liczbaWezlow];

        for (int id = 0; id < liczbaWezlow; id++) {
            int wysokosc = scanner.nextInt();
            int wspolrzednaX = scanner.nextInt();
            int wspolrzednaY = scanner.nextInt();
            boolean czyStartowy = scanner.findInLine("s") != null;
            wezly[id] = new Wezel(id, wysokosc, wspolrzednaX, wspolrzednaY, czyStartowy);
        }

        return wezly;
    }

    private Wyciag[] wczytajWyciagi(Wezel[] wezly) {
        int liczbaWyciagow = scanner.nextInt();

        Wyciag[] wyciagi = new Wyciag[liczbaWyciagow];

        for (int id = 0; id < liczbaWyciagow; id++) {
            int poczatek = scanner.nextInt();
            int koniec = scanner.nextInt();
            int odstep = scanner.nextInt();
            int maksymalnaWielkoscGrupy = scanner.nextInt();
            int czasPrzejazdu = scanner.nextInt();

            Wyciag wyciag = new Wyciag(id,
                wezly[poczatek],
                wezly[koniec],
                new Interwal(odstep),
                new Interwal(czasPrzejazdu),
                maksymalnaWielkoscGrupy);

            wyciagi[id] = wyciag;
        }

        return wyciagi;
    }

    private Trasa[] wczytajTrasy(Wezel[] wezly) {
        int liczbaTras = scanner.nextInt();

        Trasa[] trasy = new Trasa[liczbaTras];

        for (int id = 0; id < liczbaTras; id++) {
            int poczatek = scanner.nextInt();
            int koniec = scanner.nextInt();
            int poziomTrudnosci = scanner.nextInt();
            int czasPrzejazdu = scanner.nextInt();
            Interwal dlugosc = new Interwal(czasPrzejazdu);
            double bazowaAtrakcyjnosc = scanner.nextDouble();
            double odpornoscNaNierownosci = scanner.nextDouble();

            Trasa trasa = new Trasa(id,
                wezly[poczatek],
                wezly[koniec],
                dlugosc,
                poziomTrudnosci,
                bazowaAtrakcyjnosc,
                odpornoscNaNierownosci);

            trasy[id] = trasa;
        }

        return trasy;
    }

    private GrupaSportowcow[] wczytajGrupySportowcow(Trasa[] trasy, Wezel[] wezly) {
        int nastepneId = 0;
        int liczbaGrup = scanner.nextInt();

        GrupaSportowcow[] grupySportowcow = new GrupaSportowcow[liczbaGrup];

        for (int grupa = 0; grupa < liczbaGrup; grupa++) {
            grupySportowcow[grupa] = wczytajGrupeSportowcow(nastepneId, trasy, wezly);
            nastepneId += grupySportowcow[grupa].krotnosc();
        }

        return grupySportowcow;
    }

    private GrupaSportowcow wczytajGrupeSportowcow(int nastepneId, Trasa[] trasy, Wezel[] wezly) {
        int liczbaSportowcowWGrupie = scanner.nextInt();
        int poziomZaawansowania = scanner.nextInt();
        double wspolczynnikSpontanicznosci = scanner.nextDouble();
        double wspolczynnikZnudzenia = scanner.nextDouble();
        char rodzaj = scanner.next().charAt(0);
        boolean czySledzeni = scanner.findInLine("s") != null;

        double wagaDopasowania = scanner.nextDouble();
        double wagaJakosciNawierzchni = scanner.nextDouble();
        double wagaZnudzenia = scanner.nextDouble();

        int idPoczatkowegoWezla = scanner.nextInt();

        Moment start = wczytajMoment();

        Interwal odstepCzasowy = new Interwal(0);

        if (liczbaSportowcowWGrupie > 1) {
            odstepCzasowy = new Interwal(scanner.nextInt());
        }

        Sportowiec pierwszySportowiec = TworcaSportowcow.tworzSportowca(nastepneId,
            poziomZaawansowania,
            wspolczynnikSpontanicznosci,
            wspolczynnikZnudzenia,
            wagaDopasowania,
            wagaJakosciNawierzchni,
            rodzaj,
            wagaZnudzenia,
            czySledzeni,
            wezly[idPoczatkowegoWezla],
            start,
            maszynaLosujaca,
            trasy);

        return new GrupaSportowcow(pierwszySportowiec, liczbaSportowcowWGrupie, odstepCzasowy);
    }

    private Moment wczytajMoment() {
        String napis = scanner.next();
        String[] napisy = napis.split(":");
        int[] liczby = new int[napisy.length];

        for (int i = 0; i < liczby.length; i++) {
            liczby[i] = Integer.parseInt(napisy[i]);
        }

        return new Moment(liczby[0], liczby[1], liczby[2]);
    }
}
