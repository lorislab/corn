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
import static org.lorislab.corn.log.Logger.info;
import org.lorislab.corn.model.AbstractDataObject;

public class CSVObject extends AbstractDataObject implements List {

    private List<Map<String, Object>> data;

    private final List<String> columns;
    
    private final String separator;
    
    public CSVObject(List<String> columns, String separator) {
        this.columns = columns;
        this.separator = separator;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    @Override
    protected Path writeToFile(Path directory) {
        Path path = directory.resolve(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Map<String, Object> row : data) {
                for (String col : columns) {
                    Object item = row.get(col);
                    if (item != null) {
                        writer.write(item.toString());
                    }
                    writer.write(separator);
                }
                writer.write('\n');
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error write CSV ", ex);
        }
        return path;
    }

    @Override
    protected void addCustomAttribute() {        
        info("   \"data\": \"list_of_objects is mandatory\",");
    }

    @Override
    protected void createData(Map<String, Object> tmp) {
        data = (List<Map<String, Object>>) tmp.get("data");
        if (data == null || data.isEmpty()) {
            missingAttribute("data");
        }
    }

    public int getSize() {
        return size();
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return data.contains(o);
    }

    @Override
    public Iterator iterator() {
        return data.iterator();
    }

    @Override
    public Object[] toArray() {
        return data.toArray();
    }

    @Override
    public Object[] toArray(Object[] a) {
        return data.toArray(a);
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
        return data.containsAll(c);
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
        return data.get(index);
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
        return data.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return data.lastIndexOf(o);
    }

    @Override
    public ListIterator listIterator() {
        return data.listIterator();
    }

    @Override
    public ListIterator listIterator(int index) {
        return data.listIterator(index);
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        return data.subList(fromIndex, toIndex);
    }

}
