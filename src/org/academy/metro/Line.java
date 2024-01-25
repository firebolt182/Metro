package org.academy.metro;

import java.util.LinkedHashSet;
import java.util.Set;

public class Line {
    private String color;
    private Set<Station> stations = new LinkedHashSet<>();
    private Metro metro;

    public Line(String color, Metro metro) {
        this.color = color;
        this.metro = metro;
    }

    public Station createFirstStation(String name) {
        return new Station(name, this, this.metro);
    }

    public Station createLastStation(String name, Station before, Line line, Metro metro) {
        return new Station(name, before, line, metro);
    }

    public String getColor() {
        return color;
    }

    public Set<Station> getStations() {
        return stations;
    }

    @Override
    public String toString() {
        return "Line{"
                + "color='" + color + '\''
                + ", stations=" + stations
                + '}';
    }
}
