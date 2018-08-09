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
package org.lorislab.corn.zip;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipObject {

    private static final Logger LOG = Logger.getLogger(ZipObject.class.getName());
    
    private final ZipObjectInput input;

    public ZipObject(ZipObjectInput input) {
        this.input = input;
    }

    public Path generate(Path directory) {
        Path result = directory.resolve(input.output);
        Path path = directory.resolve(input.input);
        if (!Files.exists(path)) {
            throw new RuntimeException("File " + input.input + " for zip does not exists!");
        }
        if (Files.exists(result)) {
            throw new RuntimeException("Result file " + input.output + " exists! ");
        }        
        if (Files.isDirectory(path)) {
            createDiretoryZip(result, path, directory);
        } else {
            createFileZip(result, path, directory);
        }
        return result;
    }

    public static void createDiretoryZip(Path result, Path input, Path directory) {
        try (ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(result.toFile()))) {
            Stream<Path> dirStream = Files.walk(input);
            dirStream.filter(f -> !Files.isDirectory(f)).forEach(path -> addToZipFile(directory, path, zipStream));
            LOG.log(Level.FINE, "Zip file created in {0}", result.toFile().getPath());
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("Error while zipping." + e.getMessage(), e);
        }        
    }
    
    public static void createFileZip(Path result, Path file, Path directory) {
        try (ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(result.toFile()))) {
            addToZipFile(directory, file, zipStream);
            LOG.log(Level.FINE, "Zip file created in {0}", result.toFile().getPath());
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("Error while zipping." + e.getMessage(), e);
        } 
    }
    
    /**
     * Adds the file to the zip package.
     *
     * @param file the file.
     * @param zipStream the zip stream.
     */
    private static void addToZipFile(Path directory, Path file, ZipOutputStream zipStream) {
        String inputFileName = file.toFile().getPath();
        String name = directory.relativize(file).toString();
        try (FileInputStream inputStream = new FileInputStream(inputFileName)) {

            ZipEntry entry = new ZipEntry(name);
            entry.setCreationTime(FileTime.fromMillis(file.toFile().lastModified()));
            entry.setComment("Created by TheCodersCorner");
            zipStream.putNextEntry(entry);

            LOG.log(Level.FINE, "Generated new entry for: {0}", inputFileName);

            byte[] readBuffer = new byte[2048];
            int amountRead;
            int written = 0;
            while ((amountRead = inputStream.read(readBuffer)) > 0) {
                zipStream.write(readBuffer, 0, amountRead);
                written += amountRead;
            }
            LOG.log(Level.FINE, "Stored {0} bytes to {1}", new Object[]{written, inputFileName});

        } catch (IOException e) {
            throw new RuntimeException("Unable to process " + inputFileName, e);
        }
    }    
}
