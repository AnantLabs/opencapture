/*
Copyright 2008 Filter Logic

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package net.filterlogic.OpenCapture;

import net.filterlogic.util.xml.XMLParser;
import net.filterlogic.util.NamedValueList;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Darron Nesbitt
 */
public class IndexFields 
{
    private NamedValueList <String,IndexField> indexFields = new NamedValueList<String,IndexField>();

    /**
     * Default constructor.
     */
    public IndexFields()
    {
    }
    
    /**
     * Constructor using IndexFields object
     * 
     * @param indexFields Object containing all index fields
     */
    public IndexFields(IndexFields indexFields)
    {
        
        for(int i=0;i<indexFields.Count();i++)
        {
            IndexField ndxField = (IndexField)indexFields.get(i);
            
            addIndexField(new IndexField(ndxField.getName(), ndxField.getType(), ndxField.getValue(), ndxField.isStickey()));
        }
    }
    
    /**
     * Create a new instance of IndexFields object (clone)
     * 
     * @param indexFields
     * 
     * @return IndexFields object.
     */
    public static IndexFields newInstanceOf(IndexFields indexFields)
    {
        return new IndexFields(indexFields);        
    }
    
    /**
     * Constructor 
     * @param batch XMLParser object.
     * @param xPath Path to index fields.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public IndexFields(XMLParser batch,String xPath) throws OpenCaptureException
    {
        try
        {
            List list = batch.getNodeList(xPath);
            
            for(int i=0;i<list.size();i++)
            {
                HashMap map = (HashMap)list.get(i);
                
                String ndxName = (String)map.get("Name");
                String type = map.get("Type") != null ? (String)map.get("Type") : "";
                String value = (String)map.get("Value");
                String s = ((String)map.get("Stickey")).toUpperCase();
                
                boolean stickey = s.toUpperCase().equals("N") || s.length()<1 ? false : true;
                
                // create and fill ndx field object
                IndexField ndxField = new IndexField(ndxName,type,value,stickey);

                // add ndx field to hash
                indexFields.put(ndxName, ndxField);
            }
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }
    }
  
    /**List
     * Get index field.
     * @param indexFieldName Index field name.
     * @return
     */
    public IndexField getIndexField(String indexFieldName)
    {
        IndexField ndxField = (IndexField)indexFields.get(indexFieldName);
        
        if(ndxField == null)
            ndxField = new IndexField();
        
        return ndxField;
    }
    
    /**
     * Set index field.
     * @param indexField IndexField object to save.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public void setIndexField(IndexField indexField) throws OpenCaptureException
    {
        String indexFieldName = indexField.getName();

        if(indexFieldName.trim().length()<1)
            throw new OpenCaptureException("IndexField name not set!");

        indexFields.put(indexFieldName, indexField);
    }
    
    /**
     * Return batch xml.
     * @return String containing XML.
     */
    public String getXML()
    {
        String xml = "";
        
        List list = indexFields.getOrderedNameList();
        
        for(int i=0;i<list.size();i++)
        {
            String name = (String)list.get(i);
            IndexField ndxField = (IndexField)indexFields.get(name);
            String stickey = !ndxField.isStickey() ? "N" : "Y";
            
            xml += "<IndexField Name=\"" + ndxField.getName() + "\" Type=\"" + ndxField.getType() + 
                    "\" Value=\"" + ndxField.getValue() + "\" Stickey=\"" + stickey + "\" />\n";
        }
        
        return xml;
    }
    
    /**
     * Get count of IndexFields.
     * @return Returns count.
     */
    public int Count()
    {
        return indexFields.size();
    }
    
    /**
     * Add index field to index field collection.
     * @param indexField IndexField to add.
     */
    public void addIndexField(IndexField indexField)
    {
        indexFields.put(indexField.getName(), indexField);
    }

    /**
     * Delete index field.
     * @param indexFieldName Name of index field to delete.
     * @return IndexField being deleted.  Returns empty index field if index field name doesn't exist.
     */
    public IndexField deleteIndexField(String indexFieldName)
    {
        return indexFields.containsKey(indexFieldName) ? (IndexField)indexFields.remove(indexFieldName) : new IndexField();
    }
    
    public IndexField get(int index)
    {
        List list = indexFields.getOrderedNameList();
        
        return index >=0 ? index < list.size() ? (IndexField)indexFields.get((String)list.get(index)) : new IndexField() : new IndexField();
    }
    
    /**
     * Get a list of index field names.
     * 
     * @return List containing IndexField names.
     */
    public List getNameList()
    {
        if(this.indexFields!=null)
            return this.indexFields.getOrderedNameList();
        else
            return new ArrayList<String>();
    }
}
