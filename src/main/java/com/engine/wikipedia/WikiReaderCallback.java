/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.engine.wikipedia;

import com.engine.wikipedia.model.WikiPage;

/**
 *
 * @author ludovic
 */
public interface WikiReaderCallback {

    public void call(WikiPage page);

}
