package io.github.overlordsiii.api;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Article {

    private final String title;

    private final URI url;

    private final List<String> summarizedBulletPoints = new ArrayList<>();

    private final String desc;

    private final LocalDateTime time;

    public Article(String title, URI url, String desc, LocalDateTime time) {
        this.title = title;
        this.url = url;
        this.desc = desc;
        this.time = time;
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
