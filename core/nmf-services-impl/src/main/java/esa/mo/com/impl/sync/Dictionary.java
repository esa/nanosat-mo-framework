/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
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
 */
/**
 * Dictionary.
 */
public class Dictionary {

    private AtomicInteger uniqueId = new AtomicInteger(0);
    private HashMap<String, Integer> fastID;
    private HashMap<Integer, String> fastIDreverse;

    /**
     * Creates a new {@code Dictionary}.
     */
    public Dictionary() {
        this.fastID = new HashMap<>();
        this.fastIDreverse = new HashMap<>();
    }

    /**
     * Clears the dictionary.
     */
    public synchronized void reset() {
        this.fastID = new HashMap<>();
        this.fastIDreverse = new HashMap<>();
        uniqueId = new AtomicInteger(0);
    }

    /**
     * Returns whether the given word is in the dictionary.
     *
     * @param word the word
     * @return {@code true} if it exists
     */
    public synchronized boolean exists(final String word) {
        return (this.fastID.get(word) != null);
    }

    /**
     * Returns whether the given word id is in the dictionary.
     *
     * @param wordId the word id
     * @return {@code true} if it exists
     */
    public synchronized boolean exists(final Integer wordId) {
        return (this.fastIDreverse.get(wordId) != null);
    }

    /**
     * Maps the given word id to the given word.
     *
     * @param wordId the word id
     * @param word the word
     */
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

    /**
     * Returns the word id.
     *
     * @param word the word
     * @return the word id
     */
    public synchronized Integer getWordId(final String word) {
        final Integer id = this.fastID.get(word);
        return (id == null) ? this.addNewWord(word) : id;
    }

    /**
     * Returns the word.
     *
     * @param id the id
     * @return the word
     * @throws Exception if the operation fails
     */
    public synchronized String getWord(final Integer id) throws Exception {
        final String word = this.fastIDreverse.get(id);

        if (word == null) {
            throw new Exception("The word for the id: " + id + " is unknown!");
        }

        return word;
    }
}
