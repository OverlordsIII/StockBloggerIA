package io.github.overlordsiii.stockblogger.api;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Article {

    private final String title;

    private final URI url;

    private List<String> summarizedBulletPoints = new ArrayList<>();

    private final String desc;

    private final LocalDateTime time;

    private String htmlContent;

    public Article(String title, URI url, String desc, LocalDateTime time) {
        this.title = title;
        this.url = url;
        this.desc = desc;
        this.time = time;
    }

    public List<String> getSummarizedBulletPoints() {
        return summarizedBulletPoints;
    }

    public String getTitle() {
        return title;
    }

    public URI getUrl() {
        return url;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }


    public void setBulletPoints(List<String> bps) {
        this.summarizedBulletPoints = bps;
    }
}
