package org.academy.metro;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import org.academy.metro.exceptions.CheckSameStationException;
import org.academy.metro.exceptions.NoWayException;
import org.academy.metro.exceptions.StationExistsException;
import org.academy.metro.exceptions.StationNotFoundException;

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

    public Station(String name, Line line, Metro metro) {
        this.name = name;
        this.before = null;
        this.after = null;
        this.durationToNext = null;
        this.line = line;
        this.changeLines = null;
        this.metro = metro;
    }

    public Station(String name, Station before, Line line, Metro metro) {
        this.name = name;
        this.before = before;
        this.after = null;
        this.durationToNext = null;
        this.line = line;
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

    public void sellTicket(LocalDate date, Station start, Station finish) throws NoWayException,
            StationNotFoundException, CheckSameStationException, StationExistsException {
        cashier.sellTicket(date, start, finish);
    }

    private String showChangeLines() {
        if (changeLines == null) {
            return null;
        }
        StringBuilder transfer = new StringBuilder();
        changeLines.forEach(station -> transfer.append(station.getLine().getColor()).append(", "));
        return transfer.substring(0, transfer.length() - 2);
    }

    @Override
    public String toString() {
        return "Station{"
                + "name='" + name + '\''
                + ", changeLines=" + showChangeLines()
                + '}';
    }
}