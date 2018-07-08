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

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class CSVObject implements List {

    private final CSVObjectInput input;
    
    public CSVObject(CSVObjectInput input) {
        this.input = input;
    }
    
    public List<Map<String, Object>> getData() {
        return input.data;
    }

    public Path generate(Path directory) {
        Path path = directory.resolve(input.file);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            if (input.definition.header) {
                for (String col : input.definition.columns) {
                    writer.write(col);
                    writer.write(input.definition.separator);
                }
                writer.write('\n');
            }
            
            for (Map<String, Object> row : input.data) {
                for (String col : input.definition.columns) {
                    Object item = row.get(col);
                    if (item != null) {
                        writer.write(item.toString());
                    }
                    writer.write(input.definition.separator);
                }
                writer.write('\n');
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error write CSV ", ex);
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

}
