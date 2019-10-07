/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esa.mo.com.impl.util;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This class represents a simple quota mechanism. You can either right directly to the underlying
 * data structure or use the provided wrapper methods.
 *
 * @author yannick
 */
public class Quota extends HashMap<Long, Integer> {

  /**
   * This method resets the quota to 0 for all provided IDs.
   * @param ids The entities for which to reset the quotas.
   */
  public void clean(HashSet<Long> ids){
    for(Long id : ids){
      this.put(id, 0);
    }
  }

  /**
   * Increases the used quota for the provided entity.
   * @param id The entity for which to increase the utilization.
   * @param increment The value by which the utilization should be incremented.
   */
  public void increase(Long id, int increment){
    int current = retrieve(id);
    this.put(id, current + increment);
  }

  /**
   * Retrieves the used quota for the provided ID or 0 if the ID is not used.
   * @param id The ID for which the quota shall be retrieved.
   * @return The quota iff id is already a key in the quota and 0 otherwise.
   */
  public int retrieve(Long id){
    return this.containsKey(id) ? this.get(id) : 0;
  }
}
