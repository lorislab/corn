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

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

public class GzipObject {

    private static final Logger LOG = Logger.getLogger(GzipObject.class.getName());
    
    public static Path generate(Path directory, String input, String output) {
        
        Path inputFile = directory.resolve(input);
        if (!Files.exists(inputFile)) {
            throw new RuntimeException("File " + input + " for gzip does not exists!");
        }
        if (Files.isDirectory(inputFile)) {
            throw new RuntimeException("File " + input + " for gzip is directory!");
        }  
        Path outputFile = directory.resolve(output);

        try (GZIPOutputStream out = new GZIPOutputStream(Files.newOutputStream(outputFile))) {
            long written = Files.copy(inputFile, out);
            LOG.log(Level.FINE, "Stored {0} bytes to {1}", new Object[]{written, output});
        } catch (Exception ex) {
            throw new RuntimeException("Error gzip file " + output + " from file " + input, ex);
        }
        return outputFile;
    }

}
