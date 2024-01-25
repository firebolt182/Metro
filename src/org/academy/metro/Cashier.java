package org.academy.metro;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import org.academy.metro.exceptions.CheckSameStationException;
import org.academy.metro.exceptions.NoWayException;
import org.academy.metro.exceptions.StationExistsException;
import org.academy.metro.exceptions.StationNotFoundException;

public class Cashier {
    private static final int MULTIPLIER = 3;
    private static final int EXTRA_PRICE = 20;
    private static final long MONTH_CARD = 3000;
    private Map<LocalDate, Long> income = new TreeMap<>();

    public Map<LocalDate, Long> getIncome() {
        return income;
    }

    /*
     * 2.7 Функция продажи билетов. Проверки в тз не делал явно,
     *  тк они выполняются в методе countStations
     */
    public void sellTicket(LocalDate date, Station start, Station finish) throws NoWayException,
            StationNotFoundException, CheckSameStationException, StationExistsException {
        int stages = start.getMetro().countStations(start, finish);
        long price = MULTIPLIER * stages + EXTRA_PRICE;
        if (this.getIncome().containsKey(date)) {
            this.getIncome().put(date, this.getIncome().get(date) + price);
        } else {
            this.getIncome().put(date, price);
        }
    }

    /* 3.1 Продажа Абонемента */
    public void sellTravelCard(Station seller, LocalDate date) {
        seller.getMetro().getTravelCardContainer()
                .put(seller.getMetro().generateTravelCardNumber(), date.plusMonths(1));
        if (seller.getCashier().getIncome().containsKey(date)) {
            long money = seller.getCashier().getIncome().get(date);
            seller.getCashier().getIncome().put(date, money + MONTH_CARD);
        } else {
            seller.getCashier().getIncome().put(date, MONTH_CARD);
        }
    }

    /* 3.3 Продление абонемента на месяц */
    public void travelCardProlongation(Metro metro,
                                       String travelCardNumber, LocalDate purchaseDate) {
        metro.getTravelCardContainer().put(travelCardNumber, purchaseDate.plusMonths(1));
        if (this.getIncome().containsKey(purchaseDate)) {
            long money = this.getIncome().get(purchaseDate);
            this.getIncome().put(purchaseDate, money + MONTH_CARD);
        } else {
            this.getIncome().put(purchaseDate, MONTH_CARD);
        }
    }
}
