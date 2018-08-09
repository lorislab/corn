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
package org.lorislab.corn.gzip;

import org.lorislab.corn.gson.Required;

public class GzipObjectInput {
    
    @Required
    public String input;
    
    @Required
    public String output;

    @Required
    public GzipDefinition definition;
    
    public static class GzipDefinition {
        
        public String lineSeparator = "\n";
    
        public String inputCharset;
    
        public String outputCharset;
        
    }
    
}
