package com.project.controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class for fetching and parsing weather data from a weather API
 */
public class Weather {
    private static final String WEATHER_API_URL = "http://api.weatherapi.com/v1/current.json";
    private static final String WEATHER_API_KEY = System.getenv("WeatherKey"); // Replace with your WeatherAPI key

    /**
     * Fetches weather data for the given city
     *
     * @param city the name of the city to fetch weather data for
     * @return a summary of the weather in the specified city
     */
    public static String getWeatherSummary(String city) {
        try {
            // Build request URL for WeatherAPI
            String requestURL = WEATHER_API_URL + "?key=" + WEATHER_API_KEY + "&q=" + city;

            HttpURLConnection connection = (HttpURLConnection) new URL(requestURL).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return parseWeatherData(response.toString(), city);
            } else {
                return "Unable to fetch weather data.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    /**
     *parse the API response
     *
     * @param response the response from the weather API
     * @param city the name of the city
     * @return a summary of the weather in the specified city
     */
    private static String parseWeatherData(String response, String city) {
        try {
            //extract the weather condition
            String condition = extractString(response, "\"text\":\"", "\"");

            //extract the temperature
            double temperature = extractDouble(response, "\"temp_c\":", ",");

            //categorize the temperature
            String temperatureCategory = (temperature > 20) ? "warm" : (temperature < 10) ? "cold" : "mild";
            return "The weather in " + city + " is " + condition.toLowerCase() + " and " + temperatureCategory +
                    " (current temperature: " + temperature + "°C).";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing weather data.";
        }
    }

    /**
     *helper method to extract a double value from a string using start and end markers
     * @param text the text to extract the double value from
     * @param startMarker the start marker
     * @param endMarker the end marker
     * @return the extracted double value
     */
    private static double extractDouble(String text, String startMarker, String endMarker) {
        try {
            int startIndex = text.indexOf(startMarker) + startMarker.length();
            int endIndex = text.indexOf(endMarker, startIndex);
            if (startIndex >= startMarker.length() && endIndex > startIndex) {
                return Double.parseDouble(text.substring(startIndex, endIndex).replace("\"", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Helper method to extract a string value from a string using start and end markers
     * @param text the text to extract the string value from
     * @param startMarker the start marker
     * @param endMarker the end marker
     * @return the extracted string value
     */
    private static String extractString(String text, String startMarker, String endMarker) {
        try {
            int startIndex = text.indexOf(startMarker) + startMarker.length();
            int endIndex = text.indexOf(endMarker, startIndex);
            if (startIndex >= startMarker.length() && endIndex > startIndex) {
                return text.substring(startIndex, endIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ""; //default value if extraction fails
    }

}
