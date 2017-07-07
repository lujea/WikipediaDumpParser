/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.engine.wikipedia;

import com.engine.wikipedia.WikiReader;
import com.engine.wikipedia.WikiReaderCallback;
import com.engine.wikipedia.model.WikiPage;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ludovic
 */
public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException {
        String wikiFile = "/mnt/dataset/wikipedia/dataset/enwiki-latest-pages-articles.xml.bz2";
        WikiReader reader = new WikiReader();
        WikiReaderCallback callback = new WikiReaderCallback() {
            @Override
            public void call(WikiPage page) {
                logger.info("page id: {}, title: {}, disambiguation: {}", page.getId(), page.getTitle(), page.isDisambiguationPage());
            }
        };
        reader.processWikipedia(wikiFile, callback);
    }

}
