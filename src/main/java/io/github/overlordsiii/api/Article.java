package io.github.overlordsiii.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Article {

    private final String title;

    private final URI url;

    private final List<String> summarizedBulletPoints = new ArrayList<>();

    public Article(String title, URI url) {
        this.title = title;
        this.url = url;
    }

    public void addBulletPoint(String bp) {
        summarizedBulletPoints.add(bp);
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
}
