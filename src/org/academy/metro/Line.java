package org.academy.metro;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private String color;
    private List<Station> stations = new ArrayList<>();
    private Metro metro;

    public Line(String color, Metro metro) {
        this.color = color;
        this.metro = metro;
    }

    public String getColor() {
        return color;
    }

    public List<Station> getStations() {
        return stations;
    }

    @Override
    public String toString() {
        return "Line{" +
                "color='" + color + '\'' +
                ", stations=" + stations +
                '}';
    }
}
