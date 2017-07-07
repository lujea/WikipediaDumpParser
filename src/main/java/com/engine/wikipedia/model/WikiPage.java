/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.engine.wikipedia.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.HashSet;

/**
 *
 * @author ludovic
 */
@JacksonXmlRootElement(localName = "page")
public class WikiPage {

    @JacksonXmlProperty(localName = "id", isAttribute = false)
    private int id;
    @JacksonXmlProperty(localName = "ns", isAttribute = false)
    private int ns;
    @JacksonXmlProperty(localName = "title", isAttribute = false)
    private String title;
    @JacksonXmlProperty(localName = "revision")
    private Revision revision;

    @JacksonXmlProperty(localName = "redirect", isAttribute = false)
    private Redirect redirect;
    @JacksonXmlProperty(localName = "restrictions")
    private String restrictions;
    //private String[] categories;

    private boolean disambiguationPage;
    private boolean redirectPage;
    private String textBody;
    private String redirectPageTitle;
    private HashSet<String> categories;

    public WikiPage() {
    }

//    public WikiPage(String title, String[] categories) {
//        this.title = title;
//        this.categories = categories;
//    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public String[] getCategories() {
//        return categories;
//    }
//
//    public void setCategories(String[] categories) {
//        this.categories = categories;
//    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNs() {
        return ns;
    }

    public void setNs(int ns) {
        this.ns = ns;
    }

    public Redirect getRedirect() {
        return redirect;
    }

    public void setRedirect(Redirect redirect) {
        this.redirect = redirect;
    }

    public Revision getRevision() {
        return revision;
    }

    public void setRevision(Revision revision) {
        this.revision = revision;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public boolean isDisambiguationPage() {
        return disambiguationPage;
    }

    public void setDisambiguationPage(boolean disambiguationPage) {
        this.disambiguationPage = disambiguationPage;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    public boolean isRedirectPage() {
        return redirectPage;
    }

    public void setRedirectPage(boolean redirectPage) {
        this.redirectPage = redirectPage;
    }

    public String getRedirectPageTitle() {
        return redirectPageTitle;
    }

    public void setRedirectPageTitle(String redirectPageTitle) {
        this.redirectPageTitle = redirectPageTitle;
    }

    public HashSet<String> getCategories() {
        return categories;
    }

    public void setCategories(HashSet<String> categories) {
        this.categories = categories;
    }

}
