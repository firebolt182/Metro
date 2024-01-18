package org.academy.metro;

import org.academy.metro.exceptions.NoWayException;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cashier {
    private List<String> income = new ArrayList<>();

    public List<String> getIncome() {
        return income;
    }
}
