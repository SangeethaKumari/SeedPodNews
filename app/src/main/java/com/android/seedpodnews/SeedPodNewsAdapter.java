package com.android.seedpodnews;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sangeetha_gsk on 8/12/18.
 */

public class SeedPodNewsAdapter extends ArrayAdapter<News> {

    public SeedPodNewsAdapter(Context context, List<News> newsList) {
        super(context, 0, newsList);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list, parent, false);
        }

        // Find the news at the given position in the list of news
        News currentNews = getItem(position);

        // Find the TextView with view ID title
        TextView title = (TextView) listItemView.findViewById(R.id.title);
        title.setText(currentNews.getTitle());

        // Find the TextView with view ID sectionName
        TextView sectionName = (TextView) listItemView.findViewById(R.id.sectionName);
        sectionName.setText(currentNews.getSectionName());

        // Find the TextView with view ID sectionId
        TextView sectionId = (TextView) listItemView.findViewById(R.id.sectionId);

        // Set the proper background color on the sectionId circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable sectionIdBackground = (GradientDrawable) sectionId.getBackground();

        String sectionIdValues[] = getSectionId(currentNews.getSectionName());
        // Set the color on the sectionId circle
        sectionIdBackground.setColor(ContextCompat.getColor(getContext(), Integer.parseInt(sectionIdValues[0])));
        // Set the text on the sectionId TextView
        sectionId.setText(sectionIdValues[1]);

        // Find the TextView with view ID webPublicationDate
        TextView webPublicationDate = (TextView) listItemView.findViewById(R.id.webPublicationDate);
        webPublicationDate.setText(currentNews.getWebPublicationDate());

        // Find the TextView with view ID author
        TextView author = (TextView) listItemView.findViewById(R.id.author);
        if(currentNews.getAuthor() == null || currentNews.getAuthor().equals("")) {
            author.setVisibility(View.GONE);
        }else{
            author.setText(currentNews.getAuthor());
        }
        return listItemView;

    }

    /**
     * This method sets the color and text value of the
     * TextView sectionId based on the input value of sectionName.
     *
     * @param sectionName
     * @return arrray of String containing the color and text for the
     *  sectionId.
     */
    private String[] getSectionId(String sectionName){
        String sectionIdValues[] = new String[2];
        int sectionIdColorResourceId = 0;
        String sectionIdText = null;

        String caseValue = sectionName.toLowerCase();
        switch (caseValue) {
            case "technology":
                sectionIdColorResourceId = R.color.technology;
                sectionIdText = "T";
                break;
            case "games":
                sectionIdColorResourceId = R.color.games;
                sectionIdText = "G";
                break;
            case "sport":
                sectionIdColorResourceId = R.color.games;
                sectionIdText = "G";
                break;
            case "football":
                sectionIdColorResourceId = R.color.games;
                sectionIdText = "G";
                break;
            case "opinion":
                sectionIdColorResourceId = R.color.opinion;
                sectionIdText = "R";
                break;
            case "lifestyle":
                sectionIdColorResourceId = R.color.lifestyle;
                sectionIdText = "L";
                break;
            case "help":
                sectionIdColorResourceId = R.color.help;
                sectionIdText = "H";
                break;
            case "music":
                sectionIdColorResourceId = R.color.music;
                sectionIdText = "M";
                break;
            case "business":
                sectionIdColorResourceId = R.color.business;
                sectionIdText = "B";
                break;
            case "culture":
                sectionIdColorResourceId = R.color.culture;
                sectionIdText = "C";
                break;
            case "books":
                sectionIdColorResourceId = R.color.books;
                sectionIdText = "B";
                break;
            case "science":
                sectionIdColorResourceId = R.color.science;
                sectionIdText = "S";
                break;
            default :
                sectionIdColorResourceId = R.color.others;
                sectionIdText = "O";
                break;
        }
        sectionIdValues[0]= String.valueOf(sectionIdColorResourceId);
        sectionIdValues[1]= sectionIdText;

        return sectionIdValues;
    }
}