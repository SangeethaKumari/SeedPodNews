package com.android.seedpodnews;

/**
 * Created by sangeetha_gsk on 8/12/18.
 */

public class News {
    private String title;
    private String sectionName;
    private String webPublicationDate;
    private String url;
    private String author;


    /**
     * Constructs a new {@link News} object.
     *
     * @param title is the title of the news.
     * @param sectionName is the category of the news.
     * @param webPublicationDate is the date of the news.
     * @param url is the web url(link) of the news.
     * @param author is the author of the news.
     */
    public News(String title, String sectionName, String webPublicationDate, String url, String author) {
        this.title = title;
        this.sectionName = sectionName;
        this.webPublicationDate = webPublicationDate;
        this.url = url;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getWebPublicationDate() {
        return webPublicationDate;
    }

    public String getUrl() {
        return url;
    }

    public String getAuthor() {
        return author;
    }
}
