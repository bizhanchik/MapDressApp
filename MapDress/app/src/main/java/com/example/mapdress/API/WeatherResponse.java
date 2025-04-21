package com.example.mapdress.API;

import java.util.List;

public class WeatherResponse {
    public Main main;
    public Wind wind;
    public List<Weather> weather;
    public Clouds clouds;
    public Rain rain;
    public Snow snow;
    public Integer visibility;

    public static class Main {
        public double temp;
        public double feels_like;
        public double temp_min;
        public double temp_max;
        public int humidity;
    }

    public static class Wind {
        public double speed;
        public Integer deg;
    }

    public static class Weather {
        public String main;
        public String description;
    }

    public static class Clouds {
        public int all;
    }

    public static class Rain {
        public Double oneH;

        // В API это поле: "1h", его нужно аннотировать для Gson
        @com.google.gson.annotations.SerializedName("1h")
        public void setOneH(Double oneH) {
            this.oneH = oneH;
        }
    }

    public static class Snow {
        public Double oneH;

        @com.google.gson.annotations.SerializedName("1h")
        public void setOneH(Double oneH) {
            this.oneH = oneH;
        }
    }
}
