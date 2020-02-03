package com.plawx;

import com.google.gson.JsonObject;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TimeZone;

public class WeatherDAO {

    public static void main(String[] args) {
        WeatherDAO dao = new WeatherDAO();
        if (args.length == 1 && args[0].equals("writeCurrentConditions")) {
            dao.writeCurrentConditions();
            System.exit(0);
        }
        if (false) {
            String message = dao.getHistoryMessage();
            System.out.format("%s\n", message);
            System.exit(0);
        }
        if (true) {
            dao.writeCurrentConditions();
            System.exit(0);
        }
    }

    private void createTable() {
        if (System.currentTimeMillis() > 0) {
            return;
        }
        Connection connection = null;
        Statement statement = null;
        try {
            connection = Utils.getConnection();
            statement = connection.createStatement();
            statement.execute("drop table if exists weather");
            statement.execute("create table weather ( " +
                    "date_milliseconds bigint, " +
                    "date_string char(30), " +
                    "timezone int, " +
                    "city_name varchar(80), " +
                    "latitude decimal(12,9), " +
                    "longitude decimal(12,9), " +
                    "temperature decimal(5,2), " +
                    "feels_like decimal(5,2), " +
                    "temperature_minimum decimal(5,2), " +
                    "temperature_maximum decimal(5,2), " +
                    "pressure decimal(7,2), " +
                    "sea_level int, " +
                    "ground_level int, " +
                    "humidity int, " +
                    "wind_speed decimal(5,2), " +
                    "wind_degrees int, " +
                    "rain_one_hour decimal(5,2),  " +
                    "rain_three_hour decimal(5,2),  " +
                    "rain_six_hour decimal(5,2),  " +
                    "rain_twelve_hour decimal(5,2),  " +
                    "rain_one_day decimal(5,2),  " +
                    "rain_today decimal(5,2),  " +
                    "snow_one_hour decimal(5,2),  " +
                    "snow_three_hour decimal(5,2),  " +
                    "snow_six_hour decimal(5,2),  " +
                    "snow_twelve_hour decimal(5,2),  " +
                    "snow_one_day decimal(5,2),  " +
                    "show_today decimal(5,2),  " +
                    "clouds_all int, " +
                    "weather_id int, " +
                    "weather_main varchar(80), " +
                    "weather_description text, " +
                    "weather_icon varchar(10) " +
                    ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeCurrentConditions() {
        CityDAO cityDAO = new CityDAO();
        ArrayList<City> cities = cityDAO.get();
        for (City city : cities) {
            writeCurrentConditions(city.getCityId(), city.getCityName());
        }
    }

    private void writeCurrentConditions(int cityId, String cityName) {
        Properties properties = Utils.getProperties();
        String urlString = String.format("https://api.openweathermap.org/data/2.5/weather?id=%d&APPID=%s&units=imperial",
                cityId, properties.get("apikey"));
        JsonObject jsonObject = Utils.get(urlString);
        Utils.print(jsonObject);
        Connection connection = null;
        PreparedStatement ps = null;
        System.out.format("%s %s\n", cityName, urlString);
        try {
            connection = Utils.getConnection();
            ps = connection.prepareStatement("insert into weather " +
                    "(date_milliseconds, " +
                    "date_string, " +
                    "timezone, " +
                    "city_name, " +
                    "latitude, " +
                    "longitude, " +
                    "temperature, " +
                    "feels_like, " +
                    "temperature_minimum, " +
                    "temperature_maximum, " +
                    "pressure, " +
                    "humidity, " +
                    "wind_speed, " +
                    "wind_degrees, " +
                    "weather_main, " +
                    "weather_description, " +
                    "weather_id, " +
                    "clouds_all, " +
                    "weather_icon, " +
                    "date_timestamp) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) " +
                    "");
            int i = 1;
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            ps.setLong(i++, (timestamp.getTime() / 1000));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss +0000");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            ps.setString(i++, String.format("%s UTC", dateFormat.format(timestamp)));
            ps.setInt(i++, -18000);
            ps.setString(i++, cityName);
            ps.setDouble(i++, jsonObject.get("coord").getAsJsonObject().get("lat").getAsDouble());
            ps.setDouble(i++, jsonObject.get("coord").getAsJsonObject().get("lon").getAsDouble());
            ps.setDouble(i++, jsonObject.get("main").getAsJsonObject().get("temp").getAsDouble());
            ps.setDouble(i++, jsonObject.get("main").getAsJsonObject().get("feels_like").getAsDouble());
            ps.setDouble(i++, jsonObject.get("main").getAsJsonObject().get("temp_min").getAsDouble());
            ps.setDouble(i++, jsonObject.get("main").getAsJsonObject().get("temp_max").getAsDouble());
            ps.setDouble(i++, jsonObject.get("main").getAsJsonObject().get("pressure").getAsDouble());
            ps.setDouble(i++, jsonObject.get("main").getAsJsonObject().get("humidity").getAsDouble());
            JsonObject wind = jsonObject.get("wind").getAsJsonObject();
            ps.setDouble(i++, Utils.getDouble(wind, "speed"));
            ps.setDouble(i++, Utils.getDouble(wind, "deg"));
            ps.setString(i++, jsonObject.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("main").getAsString());
            ps.setString(i++, jsonObject.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("description").getAsString());
            ps.setInt(i++, jsonObject.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsInt());
            ps.setInt(i++, jsonObject.get("clouds").getAsJsonObject().get("all").getAsInt());
            ps.setString(i++, jsonObject.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("icon").getAsString());
            ps.setTimestamp(i++, timestamp);
            int rowsInserted = ps.executeUpdate();
            System.out.format("%d rows inserted for %s\n", rowsInserted, cityName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(ps, connection);
        }
    }

    public String getHistoryMessage() {
        Connection connection = null;
        PreparedStatement ps = null;
        Statement statement = null;
        ResultSet rs = null;
        double temperatureLow = 0;
        double temperatureHigh = 0;
        Timestamp timestampLow = null;
        Timestamp timestampHigh = null;
        try {
            connection = Utils.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery("select date_timestamp, temperature " +
                    "from weather " +
                    "where extract(month from date_timestamp) = extract(month from current_date) " +
                    "and extract(day from date_timestamp) = extract(day from current_date) " +
                    "order by temperature " +
                    "fetch first 1 row only");

            if (rs.next()) {
                temperatureLow = rs.getDouble("temperature");
                timestampLow = rs.getTimestamp("date_timestamp");

            }
            rs = statement.executeQuery("select date_timestamp, temperature " +
                    "from weather " +
                    "where extract(month from date_timestamp) = extract(month from current_date) " +
                    "and extract(day from date_timestamp) = extract(day from current_date) " +
                    "order by temperature desc " +
                    "fetch first 1 row only");

            if (rs.next()) {
                temperatureHigh = rs.getDouble("temperature");
                timestampHigh = rs.getTimestamp("date_timestamp");
            }
            if (temperatureHigh != 0 && temperatureLow != 0) {
                return String.format("On %s the low was %.2f. On %s the high was %.2f.",
                        Utils.getFullDate(timestampLow), temperatureLow, Utils.getFullDate(timestampHigh), temperatureHigh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(rs, ps, connection);
        }
        return null;
    }

}
