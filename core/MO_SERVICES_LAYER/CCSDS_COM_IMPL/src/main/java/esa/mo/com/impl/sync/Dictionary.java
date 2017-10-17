/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * ----------------------------------------------------------------------------
 */
package esa.mo.com.impl.sync;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 *
 */
public class Dictionary {

    private AtomicInteger uniqueId = new AtomicInteger(0);
    private HashMap<String, Integer> fastID;
    private HashMap<Integer, String> fastIDreverse;

    public Dictionary() {
        this.fastID = new HashMap<String, Integer>();
        this.fastIDreverse = new HashMap<Integer, String>();
    }

    public synchronized void resetFastNetwork() {
        this.fastID = new HashMap<String, Integer>();
        this.fastIDreverse = new HashMap<Integer, String>();
        uniqueId = new AtomicInteger(0);
    }

    public synchronized boolean exists(final String word) {
        return (this.fastID.get(word) != null);
    }

    public synchronized boolean exists(final Integer wordId) {
        return (this.fastIDreverse.get(wordId) != null);
    }

    public synchronized void defineWord(final Integer wordId, final String word) {
        this.fastID.put(word, wordId);
        this.fastIDreverse.put(wordId, word);
    }

    private Integer addNewWord(final String word) {
        final int wordId = uniqueId.incrementAndGet();
        this.fastID.put(word, wordId);
        this.fastIDreverse.put(wordId, word);
        return wordId;
    }

    public synchronized Integer getWordId(final String word) {
        final Integer id = this.fastID.get(word);
        return (id == null) ? this.addNewWord(word) : id;
    }

    public synchronized String getWord(final Integer id) throws Exception {
        final String word = this.fastIDreverse.get(id);

        if (word == null) {
            throw new Exception("The word for the id: " + id + "is unknown!");
        }

        return word;
    }
}
