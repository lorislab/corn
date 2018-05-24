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
package org.lorislab.corn;

import java.nio.file.Path;

/**
 *
 * @author andrej
 */
public abstract class AbstractObject {

    public Path generate(Path output) {
        createData();
        Path path = writeToFile(output);
        System.out.println("File: " + path);
        validation(path);       
        return path;
    }

    protected abstract Path writeToFile(Path directory);

    
    protected void validation(Path path) {
        // empty
    }

    protected void createData() {
        
    }

}
