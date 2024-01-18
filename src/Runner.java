import org.academy.metro.Cashier;
import org.academy.metro.exceptions.LastStationException;
import org.academy.metro.Line;
import org.academy.metro.Metro;
import org.academy.metro.Station;
import org.academy.metro.exceptions.LineExistsException;
import org.academy.metro.exceptions.LineHasStationsException;
import org.academy.metro.exceptions.StationNotFoundException;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Runner {

    public void init() {
        Metro permSubway = new Metro("Пермь");
        try {
            Line red = permSubway.createLine("Красная");
            Line blue = permSubway.createLine("Синяя");
            permSubway.getLines().add(red);
            permSubway.getLines().add(blue);
            Station sportivnayaStation = permSubway.createFirstStation("Красная", "Спортивная", null);
            red.getStations().add(sportivnayaStation);
            Station medvedkovskayaStation = permSubway.createLastStation("Красная",
                    "Медведковская", Duration.ofSeconds(2 * 60 + 21), null);
            red.getStations().add(medvedkovskayaStation);
            Station molodezhnayaStation = permSubway.createLastStation("Красная",
                    "Молодежная", Duration.ofSeconds(60 + 58), null);
            red.getStations().add(molodezhnayaStation);
            Station perm1Station = permSubway.createLastStation("Красная",
                    "Пермь 1", Duration.ofSeconds(3 * 60), null);
            red.getStations().add(perm1Station);
            Station perm2Station = permSubway.createLastStation("Красная",
                    "Пермь 2", Duration.ofSeconds(2 * 60 + 10), null);
            red.getStations().add(perm2Station);
            Station dvoretsKulturyStation = permSubway.createLastStation("Красная",
                    "Дворец культуры", Duration.ofSeconds(4 * 60 + 26), null);
            red.getStations().add(dvoretsKulturyStation);
            Station pacanskayaStation = permSubway.createFirstStation("Синяя", "Пацанская", null);
            blue.getStations().add(pacanskayaStation);
            Station kirovaStreetStation = permSubway.createLastStation("Синяя",
                    "Улица Кирова", Duration.ofSeconds(60 + 30), null);
            blue.getStations().add(kirovaStreetStation);
            Station tazhmazhStation = permSubway.createLastStation("Синяя",
                    "Тяжмаш", Duration.ofSeconds(60 + 47), null);
            perm1Station.setChangeLines(new ArrayList<>());
            perm1Station.getChangeLines().add(tazhmazhStation);
            tazhmazhStation.setChangeLines(new ArrayList<>());
            tazhmazhStation.getChangeLines().add(perm1Station);
            blue.getStations().add(tazhmazhStation);
            Station nizhnekamskayaStation = permSubway.createLastStation("Синяя",
                    "Нижнекамская", Duration.ofSeconds(3 * 60 + 19), null);
            blue.getStations().add(nizhnekamskayaStation);
            Station sobornayaStation = permSubway.createLastStation("Синяя",
                    "Соборная", Duration.ofSeconds(60 + 48), null);
            blue.getStations().add(sobornayaStation);
            System.out.println("ZZZZZZZZZZZZZZZ");
            sobornayaStation.sellTicket("11.01.2022", sobornayaStation, pacanskayaStation);
            sobornayaStation.sellTicket("21.01.2022", sobornayaStation, pacanskayaStation);
            sportivnayaStation.sellTravelCard(sportivnayaStation, LocalDate.of(2022, 11, 13));
            System.out.println(Arrays.asList(sobornayaStation.getCashier().getIncome()));
            System.out.println(Arrays.asList(sportivnayaStation.getCashier().getIncome()));
            permSubway.printProfitFromStations();
            System.out.println("ZZZZZZZZZZZZZZZ");
            System.out.println("=================");
            for (Station station : red.getStations()) {
                System.out.println(station.getName());
            }
            System.out.println("=================");
            Station transfer = null;
            try {
                transfer = permSubway.transferStation(blue, red);
                System.out.println("transfer station: ");
                System.out.println(transfer.getName());
            } catch (StationNotFoundException e) {
                System.out.println(e.getMessage());
            }
            System.out.println("--------");
            System.out.println("Check stages between stations");
            System.out.println(permSubway.countStations(kirovaStreetStation, perm1Station));
            System.out.println("--------");
        } catch (LineExistsException | LineHasStationsException | LastStationException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(permSubway);

    }
    public static void main(String[] args) {
        new Runner().init();
    }
}