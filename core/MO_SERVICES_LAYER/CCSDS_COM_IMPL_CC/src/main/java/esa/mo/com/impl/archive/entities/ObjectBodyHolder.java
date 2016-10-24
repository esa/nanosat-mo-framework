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
import org.ccsds.moims.mo.mal.structures.Element;

/**
 *
 * @author Cesar Coelho
 */
@Entity
public class ObjectBodyHolder implements Serializable{

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(name = "objectTypeId")
    private Long objectTypeId;
    
    @Column(name = "asElement")
    private Element asElement;

    @Column(name = "asForeignTable")
    private Long asForeignTable;
        
    public Element getObjectBody(){ 
        return asElement;
    }
    
    protected ObjectBodyHolder() {
    }

    public ObjectBodyHolder (Element objectBody, Long objectTypeId){
        this.objectTypeId = objectTypeId;


        // Based on the object type, select if it should be stored as pure element or in a different table
        if(this.isForeignTableStore(objectTypeId)){
            // check if the table for the mentioned objTypeId already exists
            
            // If yes, add it
            // If not, create the table and add the entry
            
        }else{
            this.asElement = objectBody;
        }
        
    }
    
    private boolean isForeignTableStore(Long objectTypeId){
        return false;
    }
    


}
