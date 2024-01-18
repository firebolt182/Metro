package org.academy.metro;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Station {
    private String name;
    private Station before;
    private Station after;
    private Duration durationToNext;
    private Line line;
    private List<Station> changeLines;
    private Metro metro;
    private Cashier cashier = new Cashier();

    public Station(String name, Station before, Station after,
                   Duration durationToNext, Line line, List<Station> changeLines, Metro metro) {
        this.name = name;
        this.before = before;
        this.after = after;
        this.durationToNext = durationToNext;
        this.line = line;
        this.changeLines = changeLines;
        this.metro = metro;
    }

    public String getName() {
        return name;
    }

    public Line getLine() {
        return line;
    }

    public Station getAfter() {
        return after;
    }

    public Station getBefore() {
        return before;
    }

    public void setAfter(Station after) {
        this.after = after;
    }

    public void setDurationToNext(Duration durationToNext) {
        this.durationToNext = durationToNext;
    }

    public List<Station> getChangeLines() {
        return changeLines;
    }

    public void setChangeLines(List<Station> changeLines) {
        this.changeLines = changeLines;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public Metro getMetro() {
        return metro;
    }

    /*
    2.7 Функция продажи билетов. Проверки в тз не делал явно, тк они выполняются в методе countStations
     */
    public void sellTicket(String date, Station start, Station finish) {
        int stages = start.getMetro().countStations(start, finish);
        int price = 3 * stages + 20;
        for (String income : cashier.getIncome()) {
            String arrDate = income.split(" - ")[0];
            int arrPrice = Integer.parseInt(income.split(" - ")[1]);
            if (arrDate.equals(date) && arrPrice == price) {
                cashier.getIncome().set(cashier.getIncome().indexOf(income), arrDate + " - " + (arrPrice + price));
                return;
            }
        }
        cashier.getIncome().add(date + " - " + price);
    }

    /* 3.1 Продажа Абонемента */
    public void sellTravelCard(Station seller, LocalDate date) {
        seller.getMetro().getTravelCardContainer()
                .put(seller.getMetro().generateTravelCardNumber(), date.plusMonths(1));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        seller.getCashier().getIncome().add(date.format(formatter) + " - " + 3000);
    }

    /* 3.3 Продление абонемента на месяц */
    public void travelCardProlongation(String travelCardNumber, LocalDate purchaseDate) {
        metro.getTravelCardContainer().put(travelCardNumber, purchaseDate.plusMonths(1));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        this.getCashier().getIncome().add(purchaseDate.format(formatter) + " - " + 3000);
    }

    @Override
    public String toString() {
        if (changeLines == null) {
            return "Station{" +
                    "name='" + name + '\'' +
                    ", changeLines=" + null +
                    '}';
        } else {
            StringBuilder transfer = new StringBuilder();
            for (Station station : changeLines) {
                transfer.append(station.getLine().getColor()).append(", ");
            }
            String result = transfer.substring(0, transfer.length()-2);
            return "Station{" +
                    "name='" + name + '\'' +
                    ", changeLines=" + result +
                    '}';
        }
    }
}
