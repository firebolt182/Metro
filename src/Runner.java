import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import org.academy.metro.Line;
import org.academy.metro.Metro;
import org.academy.metro.Station;
import org.academy.metro.exceptions.*;

public class Runner {

    public void init() {
        Metro permSubway = new Metro("Пермь");
        try {
            //Создание линий
            Line red = permSubway.createLine("Красная");
            Line blue = permSubway.createLine("Синяя");
            permSubway.getLines().add(red);
            permSubway.getLines().add(blue);

            //Создание первой станции красной линии
            Station sportivnayaStation = permSubway.createFirstStation("Красная", "Спортивная");
            //Создание остальных станций красной линии
            Station medvedkovskayaStation = permSubway.createLastStation("Красная",
                    "Медведковская", Duration.ofSeconds(2 * 60 + 21));
            Station molodezhnayaStation = permSubway.createLastStation("Красная",
                    "Молодежная", Duration.ofSeconds(60 + 58));
            Station perm1Station = permSubway.createLastStation("Красная",
                    "Пермь 1", Duration.ofSeconds(3 * 60));
            Station perm2Station = permSubway.createLastStation("Красная",
                    "Пермь 2", Duration.ofSeconds(2 * 60 + 10));
            Station dvoretsKulturyStation = permSubway.createLastStation("Красная",
                    "Дворец культуры", Duration.ofSeconds(4 * 60 + 26));

            //Создание первой станции синей линии
            Station pacanskayaStation = permSubway.createFirstStation("Синяя", "Пацанская");
            //Создание остальных станций синей линии
            Station kirovaStreetStation = permSubway.createLastStation("Синяя",
                    "Улица Кирова", Duration.ofSeconds(60 + 30));

            Station tyazhmashStation = permSubway.createLastStation("Синяя",
                    "Тяжмаш", Duration.ofSeconds(60 + 47));
            //Создание пересадочных узлов
            perm1Station.setChangeLines(new ArrayList<>());
            perm1Station.getChangeLines().add(tyazhmashStation);
            tyazhmashStation.setChangeLines(new ArrayList<>());
            tyazhmashStation.getChangeLines().add(perm1Station);

            Station nizhnekamskayaStation = permSubway.createLastStation("Синяя",
                    "Нижнекамская", Duration.ofSeconds(3 * 60 + 19));
            Station sobornayaStation = permSubway.createLastStation("Синяя",
                    "Соборная", Duration.ofSeconds(60 + 48));

            //Продажа билетов
            sobornayaStation.sellTicket(LocalDate.of(2022, Month.JANUARY, 21),
                    sobornayaStation, pacanskayaStation);
            sobornayaStation.sellTicket(LocalDate.of(2022, Month.JANUARY, 21),
                    sobornayaStation, pacanskayaStation);
            perm2Station.sellTicket(LocalDate.of(2022, Month.NOVEMBER, 13),
                    perm2Station, medvedkovskayaStation);
            //Продажа проездного
            sportivnayaStation.getCashier().sellTravelCard(sportivnayaStation,
                    LocalDate.of(2022, Month.NOVEMBER, 13));
            tyazhmashStation.getCashier().sellTravelCard(tyazhmashStation,
                    LocalDate.of(2022, Month.NOVEMBER, 13));
            perm1Station.getCashier().sellTravelCard(perm1Station,
                    LocalDate.of(2022, Month.NOVEMBER, 23));
            //Продление проездного
            perm1Station.getCashier().travelCardProlongation(permSubway, "a0001",
                    LocalDate.of(2022, 12, 20));

            //Вывод прибыли на конкретной станции
            System.out.println(Arrays.asList(sobornayaStation.getCashier().getIncome()));
            System.out.println(Arrays.asList(sportivnayaStation.getCashier().getIncome()));
            //Вывод прибыли на всех станциях
            System.out.println("print all profit:");
            permSubway.printProfitFromStations();
            try {
                //Вывод станций на пересадку
                Station redTransfer = permSubway.transferStation(red, blue);
                System.out.println("red line transfer station: ");
                System.out.println(redTransfer.getName());
                Station blueTransfer = permSubway.transferStation(blue, red);
                System.out.println("blue line transfer station: ");
                System.out.println(blueTransfer.getName());
            } catch (StationNotFoundException e) {
                System.out.println(e.getMessage());
            }

            //Проверка количества перегонов между станциями
            System.out.println("Check stages between stations");
            System.out.println(permSubway.countStations(perm2Station, pacanskayaStation));

            //Вывод номеров реализованных абонементов
            Set<String> cardNumbers = permSubway.getTravelCardContainer().keySet();
            System.out.println(cardNumbers);
            //Проверка действительности абонемента
            System.out.println("Check active travel card: "
                    + permSubway.isTravelCardActive("a0001",
                    LocalDate.of(2023, Month.JANUARY, 19)));

        } catch (LineAlreadyExistsException | LineHasStationsException | LastStationException
                 | NoWayException | StationExistsException | CheckSameStationException
                 | StationNotFoundException | LineNameException | StationCreateException
                 | DurationException | StationNameException | PreviousStationException e) {
            System.out.println(e.getMessage());
        } catch (CheckTravelCardException e) {
            throw new RuntimeException(e);
        }
        //Вывод структуры метро по ТЗ
        System.out.println(permSubway);
    }

    public static void main(String[] args) {
        new Runner().init();
    }
}