package com.example.mapdress.API;

public class WeatherResponse {
    public Main main;
    public Weather[] weather;

    public static class Main {
        public double temp;
    }

    public static class Weather {
        public String description;
    }
}