package com.plawx;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class CityDAO {
    public static void main(String[] args) {
        CityDAO dao = new CityDAO();
        dao.createTable();
    }

    public ArrayList<City> get() {
        ArrayList<City> list = new ArrayList<>(0);
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            connection = Utils.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery("select * from city");
            while (rs.next()) {
                City city = new City();
                city.setId(rs.getInt("id"));
                city.setCityId(rs.getInt("city_id"));
                city.setCityName(rs.getString("city_name"));
                list.add(city);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(rs, statement, connection);
        }
        return list;
    }

    private void createTable() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = Utils.getConnection();
            statement = connection.createStatement();
            statement.execute("drop table if exists city");
            statement.execute("create table city (" +
                    "id serial, " +
                    "city_name varchar(80), " +
                    "city_id int " +
                    ")");
            statement.execute("insert into city (city_name, city_id) values('Charleston',4574324)");
            statement.execute("insert into city (city_name, city_id) values('Summerville',4597919)");
            statement.execute("insert into city (city_name, city_id) values('Moncks Corner',4587511)");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(statement, connection);
        }
    }
}

