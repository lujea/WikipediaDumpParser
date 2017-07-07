/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.engine.wikipedia;

import com.engine.wikipedia.model.WikiPage;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ludovic
 */
public class WikiReader {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final XmlMapper xmlMapper;
    private final Pattern ptnDisambiguation = Pattern.compile("\\{disambiguation\\}", Pattern.CASE_INSENSITIVE);
    private final Pattern ptnRedirect = Pattern.compile("#redirect\\s*\\[\\[(.*?)\\]\\]", Pattern.CASE_INSENSITIVE);
    private final Pattern ptnCategories = Pattern.compile("\\[\\[category:(.*?)\\]\\]", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private final WikiTextParser wikiParser;

    public WikiReader() {
        xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        wikiParser = new WikiTextParser();
    }

    public void processWikipedia(String wikiDumpFileBz2) throws IOException {
        this.processWikipedia(wikiDumpFileBz2, null);
    }

    /**
     * method to process the Wikipedia dump file
     *
     * @param wikiDumpFileBz2
     * @param callback
     * @throws java.io.IOException
     */
    public void processWikipedia(String wikiDumpFileBz2, WikiReaderCallback callback) throws IOException {

        InputStream fin = Files.newInputStream(Paths.get(wikiDumpFileBz2));
        BufferedInputStream in = new BufferedInputStream(fin);
        int buffersize = 8 * 1024;
        BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);
        long pageCount = 0;
        StringBuilder pageContent = new StringBuilder();

        String startPageTag = "<page>";
        String endPageTag = "</page>";
        long errorCount = 0;
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bzIn), buffersize);
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            String content = new String(line.getBytes());
            pageContent.append(content + "\n");

            if (content.contains(endPageTag)) {
                pageCount++;
                String cnt = pageContent.toString();
                int start = cnt.indexOf(startPageTag);
                int end = cnt.indexOf(endPageTag) + endPageTag.length();
                String xmlPageStr = cnt.substring(start, end);
                WikiPage page = null;
                boolean deletedPage = xmlPageStr.contains("<comment deleted=\"deleted\" />");

                boolean isArticlePage = xmlPageStr.contains("<ns>0</ns>");
                //skip page that are not part of the "Main/Article" namespace
                if (isArticlePage && deletedPage == false) {
                    try {
                        page = xmlMapper.readValue(xmlPageStr, WikiPage.class);

                        boolean isDisambiguationPage = ptnDisambiguation.matcher(xmlPageStr).find();
                        page.setDisambiguationPage(isDisambiguationPage);

                        boolean isRedirect = page.getRedirect() != null || ptnDisambiguation.matcher(xmlPageStr).find();
                        page.setRedirectPage(isRedirect);
                        if (page.getRedirect() != null) {
                            page.setRedirectPageTitle(page.getRedirect().getTitle());
                        }

                        if (page.getRevision() != null && isRedirect == false) {
                            // retrieve categories and page body
                            wikiParser.parse(page);
                        }

                        if (callback != null) {
                            callback.call(page);
                        }
                    } catch (Exception e) {
                        logger.error("Fail processing document {}", xmlPageStr, e);
                        errorCount++;
                    }
                }

                //reset old buffer
                pageContent.delete(0, pageContent.capacity());

                if ((pageCount % 10000) == 0) {
                    logger.info("Processed {} pages (nb errors: {})", pageCount, errorCount);
                }
            }

        }

        logger.info("Total pages processed {}", pageCount);
        bzIn.close();

    }

}
