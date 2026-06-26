package duzeZadanie.sportowcy;


import duzeZadanie.czas.Moment;
import duzeZadanie.losowosc.MaszynaLosujaca;
import duzeZadanie.osrodek.Wezel;
import duzeZadanie.osrodek.krawedz.Trasa;

public class TworcaSportowcow {
    /**
     * Inaczej fabryka sportowców, odpowiada za tworzenie odpowiedniego sportowca.
     * @param id
     * @param poziomZaawansowania
     * @param wspolczynnikSpontanicznosci
     * @param wspolczynnikZnudzenia
     * @param wspolczynnikTrudnosci
     * @param wspolczynnikNawierzchni
     * @param rodzaj
     * @param wagaZnudzenia
     * @param sledzony
     * @param wezelStartowy
     * @param momentStartu
     * @param maszynaLosujaca
     * @param trasy
     * @return
     */
    public static Sportowiec tworzSportowca(int id,
        int poziomZaawansowania,
        double wspolczynnikSpontanicznosci,
        double wspolczynnikZnudzenia,
        double wspolczynnikTrudnosci,
        double wspolczynnikNawierzchni,
        char rodzaj,
        double wagaZnudzenia,
        boolean sledzony,
        Wezel wezelStartowy,
        Moment momentStartu,
        MaszynaLosujaca maszynaLosujaca,
        Trasa[] trasy) {
        
        if (rodzaj == 'L') {
            return new SportowiecLokalny(id,
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
        if (rodzaj == 'Z') {
            return new SportowiecZachlanny(id,
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
        if (rodzaj == 'K') {
            return new SportowiecKolekcjoner(id,
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
        assert true : "Nieistniejący typ sportowca w symulacji!";
        return null; // niepowinno się wykonać
    }
}