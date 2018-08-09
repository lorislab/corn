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

import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

public class GzipObject {

    private final GzipObjectInput input;

    public GzipObject(GzipObjectInput input) {
        this.input = input;
    }

    public Path generate(Path directory) {
        
        Path p = directory.resolve(input.input);
        if (!Files.exists(p)) {
            throw new RuntimeException("File " + input.input + " for gzip does not exists!");
        }
        if (Files.isDirectory(p)) {
            throw new RuntimeException("File " + input.input + " for gzip is directory!");
        }  
        
        Charset charsetInput = StandardCharsets.UTF_8;
        Charset charsetOutput = StandardCharsets.UTF_8;

        if (input.definition.inputCharset != null && !input.definition.inputCharset.isEmpty()) {
            charsetInput = Charset.forName(input.definition.inputCharset);
        }
        if (input.definition.outputCharset != null && !input.definition.outputCharset.isEmpty()) {
            charsetOutput = Charset.forName(input.definition.outputCharset);
        }

        Path inputFile = directory.resolve(input.input);
        Path outputFile = directory.resolve(input.output);

        try (OutputStreamWriter writer = new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(outputFile)), charsetOutput)) {
            for (String line : Files.readAllLines(inputFile, charsetInput)) {
                  writer.write(line);
                  writer.write(input.definition.lineSeparator);
            }           
        } catch (Exception ex) {
            throw new RuntimeException("Error gzip file " + input.output + " from file " + input.input, ex);
        }
        return outputFile;
    }

}
