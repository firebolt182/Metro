package org.academy.metro;

import org.academy.metro.exceptions.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Metro {
    private String city;
    private List<Line> lines = new ArrayList<>();
    private String travelCardNumber = "a0000";
    private Map<String, LocalDate> travelCardContainer = new HashMap<>();

    public Metro(String city) {
        this.city = city;
    }

    public List<Line> getLines() {
        return lines;
    }

    public Map<String, LocalDate> getTravelCardContainer() {
        return travelCardContainer;
    }

    public Line createLine(String color) throws LineExistsException {
        for (Line line : lines) {
            if (line.getColor().equals(color)) {
                throw new LineExistsException("Линия с таким цветом уже существует");
            }
        }
        return new Line(color, this);
    }

    public Station createFirstStation(String color, String name, List<Station> transfer) throws LineHasStationsException {
        Line line;
        try {
            checkStationName(name);
            line = lineColorExists(color);
            if (emptyLine(line)) {
                return new Station(name, null, null, null,
                        line, transfer, this);
            }
        } catch (StationNameException | StationCreateException e) {
            System.out.println(e.getMessage());
        }
        throw new LineHasStationsException("Линия уже имеет станции");
    }

    /* Проверяем, что такой станции не существует во всех линиях */
    private void checkStationName(String name) throws StationNameException {
        for (Line line : lines) {
            for (Station station : line.getStations()) {
                if (station.getName().equals(name)) {
                    throw new StationNameException("Станция с таким именем уже существует!");
                }
            }
        }
    }

    /* Проверяем,что линия с указанным цветом существует */
    private Line lineColorExists(String color) throws StationCreateException {
        for (Line line : lines) {
            if (line.getColor().equalsIgnoreCase(color)) {
                return line;
            }
        }
        throw new StationCreateException("Линия с цветом: " + color + " не существует");
    }

    /* Проверяем-пустая линия или нет */
    private boolean emptyLine(Line line) {
        return line.getStations().isEmpty();
    }

    public Station createLastStation(String lineColor, String stationName,
                                     Duration duration, List<Station> transfer) throws LastStationException {
        Line line;
        Station previousStation;
        try {
            checkStationName(stationName);
            line = lineNameExists(lineColor);
            checkDurationTime(duration);
            previousStation = checkPreviousStation(line);
            previousStation.setDurationToNext(duration);
            Station station = new Station(stationName, previousStation, null, null,
                    line, null, this);
            previousStation.setAfter(station);
            return station;
        } catch (StationNameException | LineNameException | DurationException
                 | PreviousStationException e) {
            System.out.println(e.getMessage());
        }
        throw new LastStationException("Ошибка добавления конечной станции");
    }

    private Line lineNameExists(String lineColor) throws LineNameException {
        for (Line line : lines) {
            if (line.getColor().equalsIgnoreCase(lineColor)) {
                return line;
            }
        }
        throw new LineNameException("Линия с таким цветом не существует");
    }

    /*
        -Проверка на существование предыдущей станции.
        -Предыдущая станция должна не иметь следующей станции.
    */
    private Station checkPreviousStation(Line line) throws PreviousStationException {
        Station station;
        if (!line.getStations().isEmpty()) {
            station = line.getStations().get(line.getStations().size() - 1);
            if (station.getAfter() == null) {
                return station;
            }
        }
        throw new PreviousStationException("предыдущей станция не существует или"
                + " имеет следующую станцию");
    }

    private void checkDurationTime(Duration duration) throws DurationException {
        if (duration.isZero() || duration.isNegative()) {
            throw new DurationException("Время перегона меньше или равно нулю");
        }
    }

    /* 2.1 Определение станции на пересадку */
    public Station transferStation(Line from, Line to) throws StationNotFoundException {
        for (Station station : from.getStations()) {
            if (station.getChangeLines() != null) {
                for (Station transfer : station.getChangeLines()) {
                    if (transfer.getLine().getColor().equals(to.getColor())) {
                        return station;
                    }
                }
            }
        }
        throw new StationNotFoundException("Станция на пересадку не найдена");
    }

    /* 2.2 Подсчет перегонов по следующим станциям */
    public int countStagesUp(Station start, Station finish) {
        int count = 1;
        if (start.getAfter() == null) {
            return -1;
        }
        if (start.getName().equals(finish.getName())) {
            return 0;
        }
        Station next = start.getAfter();
        while (true) {
            if (next.getAfter() == null) {
                return count;
            } else {
                if (next.getName().equals(finish.getName())) {
                    return count;
                }
                next = next.getAfter();
                count++;
            }
        }
    }

    /* 2.3 Подсчет перегонов по предыдущим станциям */
    public int countStagesDown(Station start, Station finish) {
        int count = 1;
        if (start.getBefore() == null) {
            return -1;
        }
        if (start.getName().equals(finish.getName())) {
            return 0;
        }
        Station before = start.getBefore();
        while (true) {
            if (before.getBefore() == null) {
                return count;
            } else {
                if (before.getName().equals(finish.getName())) {
                    return count;
                }
                before = before.getBefore();
                count++;
            }
        }
    }

    /* 2.4 Подсчет перегонов вверх или вниз на одной линии */
    public int countStages(Station start, Station finish) throws NoWayException {
        int countUp = countStagesUp(start, finish);
        if (countUp != -1) {
            return countUp;
        } else {
            int countDown = countStagesDown(start, finish);
            if (countDown != -1) {
                return countDown;
            } else {
                throw new NoWayException("нет пути из станции " + start.getName() + " к "
                        + finish.getName());
            }
        }
    }

    /* 2.5 подсчет кличества станций */
    public int countStations(Station start, Station finish) {
        try {
            stationExists(start);
            stationExists(finish);
            checkSameStations(start, finish);
            /* Проверяем совпадение линий */
            if (start.getLine().getColor().equals(finish.getLine().getColor())) {
                return countStages(start, finish);
            } else {
                Station transferStation = transferStation(start.getLine(), finish.getLine());
                int beforeTransfer = countStages(start, transferStation);
                for (Station station : transferStation.getChangeLines()) {
                    if (station.getLine().getColor().equals(finish.getLine().getColor())) {
                        transferStation = station;

                    }
                }
                int afterTransfer = countStages(transferStation, finish);
                return beforeTransfer + afterTransfer;
            }
        } catch (StationExistsException | CheckSameStationException | NoWayException |
                 StationNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    /* Проверяем существование станции */
    public boolean stationExists(Station station) throws StationExistsException {
        for (Line line : lines) {
            for (Station station1 : line.getStations()) {
                if (station1.getName().equals(station.getName())) {
                    return true;
                }
            }
        }
        throw new StationExistsException("Станции " + station.getName() + "не существует");
    }

    /* Проверка начальная станция не равна конечной */
    public void checkSameStations(Station first, Station second) throws CheckSameStationException {
        if (first.getName().equals(second.getName())) {
            throw new CheckSameStationException("Станция начала совпадает со станцией конца");
        }
    }

    /* Генерируем номер проездного билета */
    public String generateTravelCardNumber() {
        if (!travelCardNumber.equals("a0000")) {
            String letter = travelCardNumber.substring(0, 1);
            String number = travelCardNumber.substring(1);
            int num = Integer.parseInt(number);
            num++;
            String sub = "0000" + num;
            travelCardNumber = letter + sub.substring(sub.length() - 4);
        }
        return travelCardNumber;
    }

    /* 3.2 Проверка действительности абонемента */
    public boolean isTravelCardActive(String travelCardNumber, LocalDate checkDate) throws CheckTravelCardException {
        LocalDate travelCardDate = null;
        for (Map.Entry<String, LocalDate> entry : this.getTravelCardContainer().entrySet()) {
            if (entry.getKey().equals(travelCardNumber)) {
                //travelCardDate = entry.getValue();
                return entry.getValue().isAfter(checkDate);
            }
        }
        throw new CheckTravelCardException("Неверный номер абонемента");
    }

    /* 3.4 Добавляем функцию печати доходов касс всех станций метро по дням в которые были продажи */
    public void printProfitFromStations() {
        List<String> profit = new ArrayList<>();
        for (String incomeString : getIncomeFromAllStations()) {
            for (String profitString : profit) {
                if (profit.isEmpty()) {
                    profit.add(incomeString);
                }
                if (profitString.split(" - ")[0].equals(incomeString.split(" - ")[0])) {
                    profit.set(profit.indexOf(profitString), profitString.split(" - ")[0] + " - "
                            + (Integer.parseInt(profitString.split(" - ")[1])
                            + Integer.parseInt(incomeString.split(" - ")[1])));
                }
            }
        }
        profit.stream().forEach(System.out::println);
    }

    public List<String> getIncomeFromAllStations() {
        List<String> incomeFromAllStations = new ArrayList<>();
        for (Line line : this.getLines()) {
            for (Station station : line.getStations()) {
                incomeFromAllStations.addAll(station.getCashier().getIncome());
            }
        }
        return incomeFromAllStations;
    }

    @Override
    public String toString() {
        return "Metro{" +
                "city='" + city + '\'' +
                ", lines=" + lines +
                '}';
    }
}

