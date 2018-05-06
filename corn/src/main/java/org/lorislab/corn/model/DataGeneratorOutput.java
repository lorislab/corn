package org.lorislab.corn.model;

import java.util.List;

public class DataGeneratorOutput {
 
    public String name;
    
    public String definition;
    
    public String file;
        
    public String precondition;
    
    public List<CsvGeneratorOutput> csv;
    
    public XmlGeneratorOutput xml;
    
    public List<DataGeneratorList> list;
}
