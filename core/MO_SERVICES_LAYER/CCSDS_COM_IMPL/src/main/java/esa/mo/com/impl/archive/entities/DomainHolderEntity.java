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
package esa.mo.com.impl.archive.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author Cesar Coelho
 */
@Entity
public class DomainHolderEntity implements Serializable{

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "domainString")
    private String domainString;
  
    protected DomainHolderEntity() {
    }

    public DomainHolderEntity (final int id, final String domainString){
        this.id = id;
        this.domainString = domainString;
    }
    
    public int getId(){
        return this.id;
    }

    public String getDomainString(){
        return this.domainString;
    }
  
}
