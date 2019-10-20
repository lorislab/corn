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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class FileObject implements List {

    private final FileObjectInput input;

    public FileObject(FileObjectInput input) {
        this.input = input;
    }

    public List<String> getData() {
        return input.data;
    }

    public Path generate(Path directory) {
        Charset charset = StandardCharsets.UTF_8;
        String tmp = input.definition.charset;
        if (tmp != null && !tmp.isEmpty()) {
            charset = Charset.forName(tmp);
        }
        
        boolean first = false;
        
        Path path = directory.resolve(input.file);
        if (input.data != null && !input.data.isEmpty()) {
            try (BufferedWriter writer = Files.newBufferedWriter(path, charset)) {
                for (String line : input.data) {
                    if (first) {
                        writer.write(input.definition.lineSeparator);
                    }
                    writer.write(line);
                    first = true;
                }
            } catch (Exception ex) {
                throw new RuntimeException("Error write file " + input.file, ex);
            }
        }
        return path;
    }

    public int length() {
        return size();
    }

    public int getLength() {
        return size();
    }

    public int getSize() {
        return size();
    }

    @Override
    public int size() {
        return input.data.size();
    }

    @Override
    public boolean isEmpty() {
        return input.data.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return input.data.contains(o);
    }

    @Override
    public Iterator iterator() {
        return input.data.iterator();
    }

    @Override
    public Object[] toArray() {
        return input.data.toArray();
    }

    @Override
    public Object[] toArray(Object[] a) {
        return input.data.toArray(a);
    }

    @Override
    public boolean add(Object e) {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public boolean containsAll(Collection c) {
        return input.data.containsAll(c);
    }

    @Override
    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public Object get(int index) {
        return input.data.get(index);
    }

    @Override
    public Object set(int index, Object element) {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public void add(int index, Object element) {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public Object remove(int index) {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public int indexOf(Object o) {
        return input.data.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return input.data.lastIndexOf(o);
    }

    @Override
    public ListIterator listIterator() {
        return input.data.listIterator();
    }

    @Override
    public ListIterator listIterator(int index) {
        return input.data.listIterator(index);
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        return input.data.subList(fromIndex, toIndex);
    }

    public static Path copy(Path directory, String file, String output) {
        Path result = directory.resolve(output);
        Path input = directory.resolve(file);
        if (!Files.exists(input)) {
            throw new RuntimeException("File " + file + " does not exists! ");
        }
        if (Files.isDirectory(result)) {
            throw new RuntimeException("Result file " + output + " is directory!");
        }
        if (Files.exists(result)) {
            throw new RuntimeException("Result file " + output + " exists! ");
        }
        try {
            Files.copy(input, result);
        } catch (Exception ex) {
            throw new RuntimeException("Error delete directory " + result + " error: " + ex.getMessage(), ex);
        }
        return result;
    }

    public static Path mkdir(Path directory, String file) {
        Path result = null;
        Path path = directory.resolve(file);
        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                throw new RuntimeException("Directory " + path + " already exists!");
            }
            throw new RuntimeException("File " + path + " already exists!");
        }   
        
        try {
            result = Files.createDirectories(path);
        } catch (Exception ex) {
            throw new RuntimeException("Error create directory " + result + " error: " + ex.getMessage(), ex);
        }        
        return result;
    }    

    public static Path delete(Path directory, String file) {
        Path result = directory.resolve(file);
        return delete(result);
    }
    
    public static Path delete(Path path) {
        if (!Files.exists(path)) {
            throw new RuntimeException("File " + path + " does not exists!");
        }
        if (Files.isDirectory(path)) {
            try {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }

                });
            } catch (Exception ex) {
                throw new RuntimeException("Error delete directory " + path + " error: " + ex.getMessage(), ex);
            }
        } else {
            try {
                Files.deleteIfExists(path);
            } catch (Exception ex) {
                throw new RuntimeException("Error delete file " + path + " error: " + ex.getMessage(), ex);
            }
        }
        return path;
    }
}
