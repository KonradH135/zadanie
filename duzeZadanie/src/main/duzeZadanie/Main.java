package duzeZadanie;

import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Scanner;

import duzeZadanie.dziennik.DziennikStandardoweWyjscie;
import duzeZadanie.kolejkaZdarzen.zdarzenia.Zdarzenie;
import duzeZadanie.losowosc.DeterministycznaMaszynaLosujaca;
import duzeZadanie.symulacja.Symulacja;
import duzeZadanie.wczytywacz.DaneWejsciowe;
import duzeZadanie.wczytywacz.Wczytywacz;
import kadra.mapki.pliki.WyjatekSystemuPlikow;

public class Main {

    public static void main(String[] args) {
        DaneWejsciowe daneWejsciowe = wczytajWejscie();
        try {
            new Symulacja().przeprowadzSymulacje(new DziennikStandardoweWyjscie(),
                    new PriorityQueue<Zdarzenie>(),
                    daneWejsciowe.osrodek(),
                    daneWejsciowe.sportowcy(),
                    args[0]);
        }
        catch(WyjatekSystemuPlikow e) {
            System.err.println("Sprawdź poprawność podanej ścieżki, Drogi użytkowniku!");
        }
    }

    private static DaneWejsciowe wczytajWejscie() {
        Scanner scanner = new Scanner(System.in);

        // Ustawiamy region na angielski żeby Scanner parsował liczby
        // zmiennoprzecinkowe z '.' zamiast ','.
        scanner.useLocale(Locale.ENGLISH);

        Wczytywacz wczytywacz = new Wczytywacz(scanner, new DeterministycznaMaszynaLosujaca(0));
        return wczytywacz.wczytajWejscie();
    }
}
