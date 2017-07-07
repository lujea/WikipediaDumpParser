/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.engine.wikipedia;

import com.engine.wikipedia.model.WikiPage;
import java.io.File;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ludovic
 */
public class WikiReaderTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public WikiReaderTest() {
    }

    /**
     * Test of processWikipedia method, of class WikiReader.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testProcessWikipedia() throws Exception {
        logger.info("Testing processWikipedia");
        File sampleFile = new File("src/test/resouces/sample/wikisample.xml.bz2");
        ArrayList<WikiPage> pageList = new ArrayList<>();
        WikiReaderCallback callback = new WikiReaderCallback() {
            @Override
            public void call(WikiPage page) {                
                pageList.add(page);
            }
        };

        String wikiDumpFileBz2 = sampleFile.getAbsolutePath();
        WikiReader instance = new WikiReader();
        instance.processWikipedia(wikiDumpFileBz2, callback);

        assertEquals(18, pageList.size());

    }

}
