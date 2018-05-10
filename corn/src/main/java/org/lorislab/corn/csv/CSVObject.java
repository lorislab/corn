/*
 * Copyright 2018 lorislab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lorislab.corn.csv;

import java.util.List;
import java.util.Map;
import org.lorislab.corn.model.CsvDefinition;
import org.lorislab.corn.model.DataGeneratorItem;

public class CSVObject {

    private String fileName;
    
    private final DataGeneratorItem output;
    
    private final CsvDefinition definition;
    
    private List<Map<String, Object>> data;

    public CSVObject(DataGeneratorItem output, CsvDefinition definition) {
        this.output = output;
        this.definition = definition;
    }
    
    public int getRows() {
        return data.size();
    }
    
    public CsvDefinition getDefinition() {
        return definition;
    }
    
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
            
    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
            
    public int getSize() {
        return data.size();
    }
    
}
