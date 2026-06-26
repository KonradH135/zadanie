package duzeZadanie.osrodek.krawedz;

import duzeZadanie.czas.Interwal;
import duzeZadanie.czas.Moment;
import duzeZadanie.osrodek.Wezel;

public class Trasa extends Krawedz {

    private final int poziomTrudnosci; // {0, 1, ..., 10}

    private final double bazowaAtrakcyjnosc; // [0, 1]

    private final double odpornoscNaNierownosci; // [0, 1]

    private int liczbaZjazdow;

    public Trasa(int id,
        Wezel poczatek,
        Wezel koniec,
        Interwal dlugoscZjazdu,
        int poziomTrudnosci,
        double bazowaAtrakcyjnosc,
        double odpornoscNaNierownosci) {
        super(id, poczatek, koniec, dlugoscZjazdu);

        assert poczatek.wysokosc() > koniec.wysokosc()
            : String.format("Trasa %d prowadzi w górę: %d -> %d", id, poczatek.wysokosc(), koniec.wysokosc());

        this.poziomTrudnosci = poziomTrudnosci;
        this.bazowaAtrakcyjnosc = bazowaAtrakcyjnosc;
        this.odpornoscNaNierownosci = odpornoscNaNierownosci;
        liczbaZjazdow = 0;
    }

    public int poziomTrudnosci() {
        return poziomTrudnosci;
    }

    public double bazowaAtrakcyjnosc() {
        return bazowaAtrakcyjnosc;
    }

    public double odpornoscNaNierownosci() {
        return odpornoscNaNierownosci;
    }

    public int liczbaZjazdow() {
        return liczbaZjazdow;
    }

    public Moment przemierz(Moment start) {
        liczbaZjazdow++;
        return start.dodajInterwal(dlugosc());
    }

    public double wyrownanieNawierzchni() {
        return (bazowaAtrakcyjnosc + (1 - bazowaAtrakcyjnosc) * Math.pow(odpornoscNaNierownosci, liczbaZjazdow));
    }

    @Override
    public String wypiszStatystyki() {
        return String.format("%d zjazdów", liczbaZjazdow);
    }

    @Override
    public String pierwszaLiniaParametrow() {
        return String.format("t%d: poziom: %d, czas: %ds",
                id(),
                poziomTrudnosci,
                dlugosc().sekundy());
    }

    @Override
    public String drugaLiniaParametrow() {
        return String.format("odpornosc: %.2f, %.5f",
                bazowaAtrakcyjnosc,
                odpornoscNaNierownosci);
    }

    public String pierwszaLiniaStatystyk() {
        return String.format("t%d: śnieg: %.2f", id(), wyrownanieNawierzchni());
    }

    public String drugaLinaiStatystyk() {
        return String.format("zjazdy: %d", liczbaZjazdow);
    }

    @Override
    public String toString() {
        return String.format("Trasa nr %d", id());
    }
}
