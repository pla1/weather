package com.plawx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Scanner;

public class Utils {

    public static void main(String[] args) throws Exception {
        Connection connection = Utils.getConnection();
        DatabaseMetaData dbmd =  connection.getMetaData();
    }

    public static Properties getProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("/etc/com.plawx.properties"));
            return properties;
        } catch (IOException e) {
            System.out.format("Properties files not loaded. %s\n", e.getLocalizedMessage());
            e.printStackTrace();
        }
        return null;
    }
    public static Connection getConnection() {
        Properties properties = Utils.getProperties();
        String url = String.format("jdbc:postgresql://%s/%s?user=%s&password=%s",
                properties.get("dbHost"), properties.get("dbName"), properties.get("dbUser"), properties.get("dbPassword"));
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url, "postgres", "Hahvai8rohbeethaefung0aeroGh6s");
        } catch (Exception e) {
            return null;
        }
    }

    public static void close(Object... objects) {
        for (Object object : objects) {
            if (object != null) {
                try {
                    boolean closed = false;
                    if (object instanceof RandomAccessFile) {
                        RandomAccessFile randomAccessFile = (RandomAccessFile) object;
                        randomAccessFile.close();
                        closed = true;
                    }
                    if (object instanceof AudioInputStream) {
                        AudioInputStream audioInputStream = (AudioInputStream) object;
                        audioInputStream.close();
                        closed = true;
                    }
                    if (object instanceof Clip) {
                        Clip clip = (Clip) object;
                        clip.close();
                        closed = true;
                    }
                    if (object instanceof FileChannel) {
                        FileChannel fileChannel = (FileChannel) object;
                        fileChannel.close();
                        closed = true;
                    }
                    if (object instanceof java.io.BufferedOutputStream) {
                        BufferedOutputStream bufferedOutputStream = (BufferedOutputStream) object;
                        bufferedOutputStream.close();
                        closed = true;
                    }
                    if (object instanceof java.io.StringWriter) {
                        StringWriter stringWriter = (StringWriter) object;
                        stringWriter.close();
                        closed = true;
                    }
                    if (object instanceof java.sql.Statement) {
                        Statement statement = (Statement) object;
                        statement.close();
                        closed = true;
                    }
                    if (object instanceof java.io.FileReader) {
                        FileReader fileReader = (FileReader) object;
                        fileReader.close();
                        closed = true;
                    }
                    if (object instanceof java.sql.ResultSet) {
                        ResultSet rs = (ResultSet) object;
                        rs.close();
                        closed = true;
                    }
                    if (object instanceof java.sql.PreparedStatement) {
                        PreparedStatement ps = (PreparedStatement) object;
                        ps.close();
                        closed = true;
                    }
                    if (object instanceof java.sql.Connection) {
                        Connection connection = (Connection) object;
                        connection.close();
                        closed = true;
                    }
                    if (object instanceof java.io.BufferedReader) {
                        BufferedReader br = (BufferedReader) object;
                        br.close();
                        closed = true;
                    }
                    if (object instanceof Socket) {
                        Socket socket = (Socket) object;
                        socket.close();
                        closed = true;
                    }
                    if (object instanceof PrintStream) {
                        PrintStream printStream = (PrintStream) object;
                        printStream.close();
                        closed = true;
                    }
                    if (object instanceof ServerSocket) {
                        ServerSocket serverSocket = (ServerSocket) object;
                        serverSocket.close();
                        closed = true;
                    }
                    if (object instanceof Scanner) {
                        Scanner scanner = (Scanner) object;
                        scanner.close();
                        closed = true;
                    }
                    if (object instanceof InputStream) {
                        InputStream inputStream = (InputStream) object;
                        inputStream.close();
                        closed = true;
                    }
                    if (object instanceof OutputStream) {
                        OutputStream outputStream = (OutputStream) object;
                        outputStream.close();
                        closed = true;
                    }
                    if (object instanceof Socket) {
                        Socket socket = (Socket) object;
                        socket.close();
                        closed = true;
                    }
                    if (object instanceof PrintWriter) {
                        PrintWriter pw = (PrintWriter) object;
                        pw.close();
                        closed = true;
                    }
                    if (!closed) {
                        System.out.format("Object not closed. Object type not defined in this close method. Name: %s Stack: %s\n", object.getClass().getName(), getClassNames());
                    }
                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }
    }

    public static String getFullDateAndTime(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd, yyyy hh:mm:ss a");
        return sdf.format(timestamp);
    }

    public static String getFullDate(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd, yyyy");
        return sdf.format(timestamp);
    }

    public static String getClassNames() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StringBuilder classNames = new StringBuilder();
        for (StackTraceElement e : stackTraceElements) {
            classNames.append(e.getClassName()).append(", ");
        }
        if (classNames.toString().endsWith(", ")) {
            classNames.delete(classNames.length() - 2, classNames.length());
        }
        return classNames.toString();
    }

    public static long getLong(String s) {
        if (s == null) {
            return 0;
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    public static boolean isBlank(String s) {
        return (s == null || s.trim().length() == 0);
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }
    public static double getDouble(JsonObject jsonObject, String propertyName) {
        if (jsonObject.has(propertyName)) {
            return jsonObject.get(propertyName).getAsDouble();
        } else {
            return 0;
        }
    }
    public static JsonObject get(String urlString) {
        HttpsURLConnection urlConnection;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        JsonObject jsonObject;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Cache-Control", "no-cache");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", "com.plawx");
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("GET");
            urlConnection.setInstanceFollowRedirects(true);
            inputStream = urlConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            jsonObject = gson.fromJson(isr, JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonObject();
        } finally {
            Utils.close(inputStream, outputStream);
        }
        return jsonObject;
    }

   public static void print(JsonElement jsonElement) {
       Gson gson = new GsonBuilder().setPrettyPrinting().create();
       System.out.println(gson.toJson(jsonElement));
   }

    public static int getInt(Object object) {
        if (object == null) {
            return 0;
        }
        try {
            return (int) Double.parseDouble(object.toString());
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }


}
