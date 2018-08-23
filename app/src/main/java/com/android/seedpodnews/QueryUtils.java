package com.android.seedpodnews;

import android.util.Log;
import org.json.JSONException;
import android.text.TextUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;


/**
 * Created by sangeetha_gsk on 8/12/18.
 * Helper methods related to requesting and receiving News data from USGS.
 */
public final class QueryUtils {

    private static final String LOG_TAG = "QueryUtils";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        //Extract relevant fields from the JSON response and create a list of {@link News}s
        List<News> newsList = extractFeatureFromJson(jsonResponse);

        // Return the list of news
        return newsList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);

            } else {
                Log.e(LOG_TAG, "Error while connection,url connection response code: "
                        + urlConnection.getResponseCode());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            Log.i(LOG_TAG,"TEST: newsJSON is empty ...");
            return null;
        }
        // Create an empty ArrayList that we can start adding news to the newsList
        List<News> newsList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // Extract the JSONArray associated with the key called "response",
            // which represents a list of response (or news).
            JSONObject responseJSONObject = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of news stories
            JSONArray newsArray = responseJSONObject.getJSONArray("results");
            // For each news in the newsArray, create an {@link News} object
            for (int i = 0; i < newsArray.length(); i++) {
                // Get a single news at position i within the list of news
                JSONObject currentNews = newsArray.getJSONObject(i);

                // Extract the value for the key called "webTitle"
                String title = currentNews.getString("webTitle");

                // Extract the value for the key called "sectionName"
                String sectionName = currentNews.getString("sectionName");

                // Extract the value for the key called "webPublicationDate"
                String webPublicationDate = currentNews.getString("webPublicationDate");

                String formattedDate = convertDate(webPublicationDate);

                // Extract the value for the key called "url"
                String url = currentNews.getString("webUrl");


                String authorName = "";
                //Check if the JSON object has the key "tags"
                if(currentNews.has("tags")) {
                    // Extract the JSONArray associated with the key called "tags",
                    // which represents a list of news stories
                    JSONArray tagArray = currentNews.getJSONArray("tags");
                   if (tagArray != null && tagArray.length() > 0) {
                       JSONObject authorTag = (JSONObject) tagArray.get(0);
                       // Extract the value for the key called "url"
                       authorName = authorTag.getString("webTitle");
                   }
               }else if(currentNews.has("fields")) {
                    // Extract the JSONObject associated with the key called "fields"
                    JSONObject fieldsObject = currentNews.getJSONObject("fields");
                    if (fieldsObject != null && fieldsObject.has("byline")) {
                        // Extract the value for the key called "byline"
                        authorName = fieldsObject.getString("byline");
                    }
                }
                // Create a new {@link News} object with the magnitude, location, time,
                // and url from the JSON response.
                News news = new News(title, sectionName, formattedDate, url,authorName);
                // Add the new {@link News} to the list of news.
                newsList.add(news);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }
        // Return the list of news
        return newsList;
    }

    /**
     * This method parses the webPublicationDate.
     * @param webPublicationDate
     * @return the date in the pattern d MMM yyyy.
     */
    private static String convertDate(String webPublicationDate){
        Date date1 = null;
        SimpleDateFormat sdfSource = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            date1 = sdfSource.parse(webPublicationDate);
        }
        catch (ParseException e)
        {
            Log.e("QueryUtils", "Problem parsing the date", e);
        }
        SimpleDateFormat sdfDestination = new SimpleDateFormat("d MMM yyyy");
        String formattedDate = sdfDestination.format(date1);
        return formattedDate;

    }
}