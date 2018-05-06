package org.lorislab.corn.csv;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author andrej
 */
public class CSVRow implements Map<String, Object> {
    
    private CSVObject parent;
    
    private int row;

    public CSVRow(CSVObject parent, int row) {
        this.parent = parent;
        this.row = row;
    }
    
    public List<Object> getData() {
        return parent.getData().get(row);
    }
    
    public Object setColumn(String key, Object value) {
        List<Object> data = getData();
        return data.set(parent.getHeader().get(key), value);        
    }
    
    public Object getColumn(String key) {
        List<Object> data = getData();
        return data.get(parent.getHeader().get(key));
    }
    
    public int getSize() {
        return getData().size();
    }
    
    public boolean isEmpty() {
        return getData().isEmpty();
    }

    public int getRow() {
        return row;
    }

    @Override
    public int size() {
        return getData().size();
    }

    @Override
    public boolean containsKey(Object key) {
        return parent.getHeader().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getData().contains(value);
    }

    @Override
    public Object get(Object key) {
        return getColumn((String) key);
    }

    @Override
    public Object put(String key, Object value) {
        return setColumn(key, value);
    }

    @Override
    public Object remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        
    }

    @Override
    public void clear() {
        
    }

    @Override
    public Set<String> keySet() {
        return parent.getHeader().keySet();
    }

    @Override
    public Collection<Object> values() {
        return getData();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
