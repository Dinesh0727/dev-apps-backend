package com.opdinna.error_vault.backend.model.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class ImageLinkElement {
    private String link;
    private String text;

    public ImageLinkElement(String link, String text) {
        this.link = link;
        this.text = text;
    }

    public ImageLinkElement() {
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "ImageLinkElement [link=" + link + ", text=" + text + "]";
    }

}
