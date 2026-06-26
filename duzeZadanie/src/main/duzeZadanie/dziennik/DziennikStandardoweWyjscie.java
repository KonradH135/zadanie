package duzeZadanie.dziennik;

import duzeZadanie.czas.Moment;
import duzeZadanie.sportowcy.Sportowiec;

public class DziennikStandardoweWyjscie implements Dziennik {

    @Override
    public void dodajWpis(Moment moment, String wpis) {
        System.out.printf("%s: %s%n", moment.toString(), wpis);
    }

    @Override
    public void dodajWpisZeSportowcem(Moment moment, Sportowiec sportowiec, String coZrobil) {
        if (sportowiec.sledzony()) {
            dodajWpis(moment, String.format("%s %s.", sportowiec, coZrobil));
        }
    }

    @Override
    public void dodajLinie(String linia) {
        System.out.println(linia);
    }
}
