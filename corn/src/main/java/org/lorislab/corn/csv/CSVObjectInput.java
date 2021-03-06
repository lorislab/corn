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
import org.lorislab.corn.gson.Required;

public class CSVObjectInput {
    
    @Required
    public String file;
    
    @Required
    public CSVDefinition definition;
    
    public List<Map<String, Object>> data;
    
    public static class CSVDefinition {
        
        public String version;
        
        public boolean header;
        
        public String charset;
        
        public boolean newLine = true;
        
        @Required
        public List<String> columns;
        
        @Required
        public String separator;
    }
}
