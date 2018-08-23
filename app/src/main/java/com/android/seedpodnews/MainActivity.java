package com.android.seedpodnews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>  {

    public static final int SEED_POD_NEWS_LOADER_ID = 1;
    private SeedPodNewsAdapter seedPodNewsAdapter= null;
    TextView mEmptyStateTextView = null;
    /** URL for NEWs data from the USGS dataset */
    private static final String URL_BASE = "https://content.guardianapis.com/search";
    // API_KEY to access GUARDIAN API's
    private static final String API_KEY = "68ad6700-0653-4684-a223-c86f2f985c5c";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find a reference to the {@link ListView} in the layout
        ListView seedPodNewsView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        seedPodNewsView.setEmptyView(mEmptyStateTextView);

        NetworkInfo networkInfo = getActiveNetworkInfo();
        if( networkInfo != null && networkInfo.isConnected()){
            // Create a new adapter that takes an empty list of news as input
            seedPodNewsAdapter = new SeedPodNewsAdapter(this, new ArrayList<News>());
            //set the adapter value
            seedPodNewsView.setAdapter(seedPodNewsAdapter);
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(SEED_POD_NEWS_LOADER_ID, null, this);

        } else {
            // Set empty state text to display "No news found."
            mEmptyStateTextView.setText(R.string.no_internet_connection);
            mEmptyStateTextView.setVisibility(View.VISIBLE);

            // Hide loading indicator because the data has been loaded
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
        }

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news.
        seedPodNewsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news that was clicked on
                News currentNews = seedPodNewsAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getUrl());

                // Create a new intent to view the news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }

    /**
     * Checks the status of a network interface.
     * @return NetworkInfo object
     */
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        // Create a new loader for the given URL
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String newsSectionType = sharedPrefs.getString(
                getString(R.string.settings_news_section_type_key),
                getString(R.string.settings_news_section_type_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(URL_BASE);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String orderBy  = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        // Append query parameter and its value. For example, the section=games
        uriBuilder.appendQueryParameter("section", newsSectionType);
        uriBuilder.appendQueryParameter("show-fields", "byline");
        uriBuilder.appendQueryParameter("api-key",API_KEY);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        Log.i("main activity", "onCreateLoader: " +uriBuilder.toString());
        // Create a new loader for the given URL
        return new SeedPodNewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        // Clear the adapter of previous news data
        seedPodNewsAdapter.clear();

        // If there is a valid list of {@link New}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
           seedPodNewsAdapter.addAll(news);
        }else {
            // Set empty state text to display "No news found."
            mEmptyStateTextView.setText(R.string.no_news_found);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        seedPodNewsAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}