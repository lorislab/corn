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
import static org.lorislab.corn.log.Logger.info;

/**
 *
 * @author andrej
 */
public abstract class AbstractDataObject {

    protected String fileName;

    public Path generate(Path directory, Map<String, Object> data) {
        fileName = (String) data.get("file");
        if (fileName == null || fileName.isEmpty()) {
            missingAttribute("file");
        }

        createData(data);

        Path path = writeToFile(directory);
        
        validation(path);
        info("Create a file : " + path);
        return path;
    }

    protected abstract void createData(Map<String, Object> data);

    protected abstract Path writeToFile(Path directory);

    protected abstract void addCustomAttribute();

    protected void missingAttribute(String attribute) {
        info("Missing '" + attribute + "' attribute in the script object!");
        info("The script object for the " + this.getClass().getSimpleName() + " muss have this format: ");
        info("result = {");
        info("   \"file\": \"output_file_name is mandatory\",");
        info("   \"parameters\": \"parameters is optional\",");
        addCustomAttribute();
        info("}");
        throw new RuntimeException("Wrong script object!");
    }
    
    protected void validation(Path path) {
        // empty
    }

    public String getFileName() {
        return fileName;
    }

}
