/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.engine.wikipedia;

import com.engine.wikipedia.model.WikiPage;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ludovic Modified version of the code from:
 * https://github.com/delip/wikixmlj/blob/master/src/main/java/edu/jhu/nlp/wikipedia/WikiTextParser.java
 */
public class WikiTextParser {

    private final Pattern ptnDisambiguation = Pattern.compile("\\{disambiguation\\}", Pattern.CASE_INSENSITIVE);
    private final Pattern ptnRedirect = Pattern.compile("#redirect\\s*\\[\\[(.*?)\\]\\]", Pattern.CASE_INSENSITIVE);
    private final Pattern ptnCategories = Pattern.compile("\\[\\[category:(.*?)\\]\\]", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static Pattern stylesPattern = Pattern.compile("\\{\\|.*?\\|\\}$", Pattern.MULTILINE | Pattern.DOTALL);
    private static Pattern infoboxCleanupPattern = Pattern.compile("\\{\\{infobox.*?\\}\\}$", Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static Pattern curlyCleanupPattern0 = Pattern.compile("^\\{\\{.*?\\}\\}$", Pattern.MULTILINE | Pattern.DOTALL);
    private static Pattern curlyCleanupPattern1 = Pattern.compile("\\{\\{.*?\\}\\}", Pattern.MULTILINE | Pattern.DOTALL);
    private static Pattern cleanupPattern0 = Pattern.compile("^\\[\\[.*?:.*?\\]\\]$", Pattern.MULTILINE | Pattern.DOTALL);
    private static Pattern cleanupPattern1 = Pattern.compile("\\[\\[(.*?)\\]\\]", Pattern.MULTILINE | Pattern.DOTALL);
    private static Pattern refCleanupPattern = Pattern.compile("<ref>.*?</ref>", Pattern.MULTILINE | Pattern.DOTALL);
    private static Pattern commentsCleanupPattern = Pattern.compile("<!--.*?-->", Pattern.MULTILINE | Pattern.DOTALL);

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public WikiTextParser() {
    }

    /**
     * method to parse the page content and retrieve the categories and page
     * main body
     *
     * @param page
     */
    public void parse(WikiPage page) {

        String pageFullText = page.getRevision().getText();
        if (pageFullText != null) {
            //extract categories
            Matcher catMatcher = ptnCategories.matcher(pageFullText);
            HashSet<String> categories = new HashSet();
            while (catMatcher.find()) {
                String[] temp = catMatcher.group(1).split("\\|");
                categories.add(temp[0]);
            }
            page.setCategories(categories);

            //extract clean page body
            String articleBody = getTextBody(pageFullText);
            page.setTextBody(articleBody);
        }

    }

    public String getPlainText(String wikiText) {
        String text = wikiText.replaceAll("&gt;", ">");
        text = text.replaceAll("&lt;", "<");
        text = infoboxCleanupPattern.matcher(text).replaceAll(" ");
        text = commentsCleanupPattern.matcher(text).replaceAll(" ");
        text = stylesPattern.matcher(text).replaceAll(" ");
        text = refCleanupPattern.matcher(text).replaceAll(" ");
        text = text.replaceAll("</?.*?>", " ");
        text = curlyCleanupPattern0.matcher(text).replaceAll(" ");
        text = curlyCleanupPattern1.matcher(text).replaceAll(" ");
        text = cleanupPattern0.matcher(text).replaceAll(" ");

        Matcher m = cleanupPattern1.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            // For example: transform match to upper case
            int i = m.group().lastIndexOf('|');
            String replacement;
            if (i > 0) {
                replacement = m.group(1).substring(i - 1);
            } else {
                replacement = m.group(1);
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        text = sb.toString();

        text = text.replaceAll("'{2,}", "");

        //add additional text to clean up 
        return text.trim();
    }

    /**
     * Return only the unformatted text body. Heading markers are omitted.
     *
     * @return the unformatted text body
     */
    public String getTextBody(String pageText) {
        String text = getPlainText(pageText);
        text = stripBottomInfo(text, "seealso");
        text = stripBottomInfo(text, "further");
        text = stripBottomInfo(text, "references");
        text = stripBottomInfo(text, "notes");
        text = cleanHeadings(text);
        return text;
    }

    /**
     * Strips any content following a specific heading, e.g. "See also",
     * "References", "Notes", etc. Everything following this heading (including
     * the heading) is cut from the text.
     *
     * @param text The wiki page text
     * @param label the heading label to cut
     * @return the processed wiki text
     */
    private String stripBottomInfo(String text, String label) {
        Pattern bottomPattern = Pattern.compile("^=*\\s?" + label + "\\s?=*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = bottomPattern.matcher(text);
        if (matcher.find()) {
            text = text.substring(0, matcher.start());
        }
        return text;
    }

    /**
     * Cleans the surrounding annotations on headings (e.g. "==" or "===").
     * Leaves the heading word intact.
     *
     * @param text the wiki text
     * @return the processed text
     */
    private String cleanHeadings(String text) {
        Pattern startHeadingPattern = Pattern.compile("^=*", Pattern.MULTILINE);
        Pattern endHeadingPattern = Pattern.compile("=*$", Pattern.MULTILINE);
        text = startHeadingPattern.matcher(text).replaceAll("");
        text = endHeadingPattern.matcher(text).replaceAll("");
        return text;
    }

}
