package org.academy.metro;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import org.academy.metro.exceptions.*;

public class Metro {
    private String city;
    private Set<Line> lines = new HashSet<>();
    private String travelCardNumber = "a%s";
    private Map<String, LocalDate> travelCardContainer = new HashMap<>();
    private int count = 0;

    public Metro(String city) {
        this.city = city;
    }

    public Set<Line> getLines() {
        return lines;
    }

    public Map<String, LocalDate> getTravelCardContainer() {
        return travelCardContainer;
    }

    public Line createLine(String color) throws LineAlreadyExistsException {
        for (Line line : lines) {
            if (line.getColor().equals(color)) {
                throw new LineAlreadyExistsException("Линия с таким цветом уже существует");
            }
        }
        return new Line(color, this);
    }

    public Station createFirstStation(String lineColor, String stationName) throws
            LineHasStationsException, StationNameException, StationCreateException {
        checkStationName(stationName);
        Line line = lineColorExists(lineColor);
        if (emptyLine(line)) {
            Station station = line.createFirstStation(stationName);
            line.getStations().add(station);
            return station;
        }
        throw new LineHasStationsException("Линия уже имеет станции");
    }

    /* Проверяем, что такой станции не существует во всех линиях */
    private void checkStationName(String name) throws StationNameException {
        for (Station station : allStationsList(lines)) {
            if (station.getName().equals(name)) {
                throw new StationNameException("Станция с таким именем уже существует!");
            }
        }
    }

    private List<Station> allStationsList(Set<Line> lines) {
        List<Station> stations = new ArrayList<>();
        for (Line line : lines) {
            stations.addAll(line.getStations());
        }
        return stations;
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
                                     Duration duration) throws LastStationException,
            StationNameException, LineNameException, DurationException, PreviousStationException {
        checkStationName(stationName);
        Line line = lineNameExists(lineColor);
        checkDurationTime(duration);
        Station previousStation = checkPreviousStation(line);
        previousStation.setDurationToNext(duration);
        Station station = line.createLastStation(stationName, previousStation, line, this);
        previousStation.setAfter(station);
        line.getStations().add(station);
        return station;
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
     *  -Проверка на существование предыдущей станции.
     *  -Предыдущая станция должна не иметь следующей станции.
     */
    private Station checkPreviousStation(Line line) throws PreviousStationException {
        if (!line.getStations().isEmpty()) {
            Station station = lastStation(line);
            if (station.getAfter() == null) {
                return station;
            }
        }
        throw new PreviousStationException("предыдущей станция не существует или"
                + " имеет следующую станцию");
    }

    //Забираем последнюю станцию из сета, которая после добавления новой - станет предыдущей
    private Station lastStation(Line line) {
        Station result = null;
        for (Station station : line.getStations()) {
            result = station;
        }
        return result;
    }

    private void checkDurationTime(Duration duration) throws DurationException {
        if (duration.isZero() || duration.isNegative()) {
            throw new DurationException("Время перегона меньше или равно нулю");
        }
    }

    /* 2.1 Определение станции на пересадку */
    public Station transferStation(Line from, Line to) throws StationNotFoundException {
        for (Station station : changeStationsList(to)) {
            if (station.getLine().getColor().equals(from.getColor())) {
                return station;
            }
        }
        throw new StationNotFoundException("Станция на пересадку не найдена");
    }

    /* Вытаскиваем из линии станции с пересадками */
    private List<Station> stationsWithTransfer(Line line) {
        List<Station> stationsWithTransfer = new ArrayList<>();
        for (Station station : line.getStations()) {
            if (station.getChangeLines() != null) {
                stationsWithTransfer.add(station);
            }
        }
        return stationsWithTransfer;
    }

    /* Получаем список станций на пересадку */
    private List<Station> changeStationsList(Line to) {
        List<Station> changeStations =  new ArrayList<>();
        for (Station station : stationsWithTransfer(to)) {
            changeStations.addAll(station.getChangeLines());
        }
        return changeStations;
    }

    /* 2.2 Подсчет перегонов по следующим станциям */
    public int countStagesUp(Station start, Station finish) {
        int count = 1;
        if (start.getName().equals(finish.getName())) {
            return 0;
        }
        while (true) {
            if (start.getAfter() != null && start.getAfter().getName().equals(finish.getName())) {
                return count;
            } else {
                if (start.getAfter() == null) {
                    return -1;
                }
                start = start.getAfter();
                count++;
            }
        }
    }

    /* 2.3 Подсчет перегонов по предыдущим станциям */
    private int countStagesDown(Station start, Station finish) {
        int count = 1;
        if (start.getName().equals(finish.getName())) {
            return 0;
        }
        while (true) {
            if (start.getBefore() != null && start.getBefore().getName().equals(finish.getName())) {
                return count;
            } else {
                if (start.getBefore() == null) {
                    return -1;
                }
                start = start.getBefore();
                count++;
            }
        }
    }

    /* 2.4 Подсчет перегонов вверх или вниз на одной линии */
    private int countStages(Station start, Station finish) throws NoWayException {
        int countUp = countStagesUp(start, finish);
        if (countUp != -1) {
            return countUp;
        }
        int countDown = countStagesDown(start, finish);
        if (countDown != -1) {
            return countDown;
        }
        throw new NoWayException("нет пути из станции " + start.getName() + " к "
                + finish.getName());
    }

    /* 2.5 подсчет кличества станций */
    public int countStations(Station start, Station finish) throws StationExistsException,
            CheckSameStationException, NoWayException, StationNotFoundException {
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
    }

    /* Проверяем существование станции */
    private boolean stationExists(Station station) throws StationExistsException {
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
    private void checkSameStations(Station first, Station second) throws CheckSameStationException {
        if (first.getName().equals(second.getName())) {
            throw new CheckSameStationException("Станция начала совпадает со станцией конца");
        }
    }

    /* Генерируем номер проездного билета */
    public String generateTravelCardNumber() {
        String sub = "0000" + count;
        count++;
        return String.format(travelCardNumber, sub.substring(sub.length() - 4));
    }

    /* 3.2 Проверка действительности абонемента */
    public boolean isTravelCardActive(String travelCardNumber, LocalDate checkDate)
            throws CheckTravelCardException {
        for (Map.Entry<String, LocalDate> entry : this.getTravelCardContainer().entrySet()) {
            if (entry.getKey().equals(travelCardNumber)) {
                return entry.getValue().isAfter(checkDate);
            }
        }
        throw new CheckTravelCardException("Неверный номер абонемента");
    }

    /* 3.4 Добавляем функцию печати доходов касс всех станций по дням, в которые были продажи */
    public void printProfitFromStations() {
        Map<LocalDate, Long> totalResult = calculateIncome();
        for (Map.Entry<LocalDate, Long> income : totalResult.entrySet()) {
            System.out.println(income.getKey() + " - " + income.getValue());
        }
    }

    /*
     * Юр, 2 прохода, вложенность пока эту пока мне не победить
     * по моей логике я итерируюсь по всем станциям и у каждой станции прохожу по прибыли
     */
    private Map<LocalDate, Long> calculateIncome() {
        Map<LocalDate, Long> totalResult = new TreeMap<>();
        List<Station> stations = allStationsList(lines);
        for (Station station : stations) {
            for (Map.Entry<LocalDate, Long> income : station.getCashier().getIncome().entrySet()) {
                if (totalResult.containsKey(income.getKey())) {
                    totalResult.put(income.getKey(),
                            totalResult.get(income.getKey()) + income.getValue());
                } else {
                    totalResult.put(income.getKey(), income.getValue());
                }
            }
        }
        return totalResult;
    }

    @Override
    public String toString() {
        return "Metro{"
                + "city='" + city + '\''
                + ", lines=" + lines
                + '}';
    }
}