package com.android.seedpodnews;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;


/**
 * Created by sangeetha_gsk on 8/12/18.
 */

public class SeedPodNewsLoader extends AsyncTaskLoader<List<News>>  {

    private String mUrl;

    public SeedPodNewsLoader(Context context, String url){
        super(context);
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<News> newsList = QueryUtils.fetchNewsData(mUrl);
        return newsList;
    }
}