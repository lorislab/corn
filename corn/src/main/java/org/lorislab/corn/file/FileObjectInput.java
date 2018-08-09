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
package org.lorislab.corn.file;

import java.util.List;
import org.lorislab.corn.gson.Required;

public class FileObjectInput {
    
    @Required
    public String file;
    
    @Required
    public FileDefinition definition;
    
    public List<String> data;
    
    public static class FileDefinition {
        
        public String lineSeparator = "\n";
        
        public String version;
        
        public String charset;
        
    }
}
