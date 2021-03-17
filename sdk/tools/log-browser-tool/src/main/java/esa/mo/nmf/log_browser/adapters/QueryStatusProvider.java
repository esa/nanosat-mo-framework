// (C) 2021 European Space Agency
// European Space Operations Centre
// Darmstadt, Germany

package esa.mo.nmf.log_browser.adapters;

/**
 * Interface providing the status of an on-going archive query.
 *
 * @author Tanguy Soto
 */
public interface QueryStatusProvider {

  /**
   * @return true if the query is finished ((response or any error message received), false
   *         otherwise.
   */
  boolean isQueryOver();

}
