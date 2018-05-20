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
package org.lorislab.corn.model;

import java.nio.file.Path;
import java.util.Map;

/**
 *
 * @author andrej
 */
public abstract class AbstractDataObject {
 
    protected String fileName;
    
    protected final DataDefinition definition;

    protected final DataGeneratorItem output;
    
    public AbstractDataObject(DataDefinition definition, DataGeneratorItem output) {
        this.definition = definition;
        this.output = output;
    }

    public abstract Path generate(Path directory, Map<String, Object> data);
    
    public DataGeneratorItem getOutput() {
        return output;
    }

    public DataDefinition getDefinition() {
        return definition;
    }     

    public String getFileName() {
        return fileName;
    }
        
}
