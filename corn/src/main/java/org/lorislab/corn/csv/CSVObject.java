package org.lorislab.corn.csv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import org.lorislab.corn.el.Expressions;
import org.lorislab.corn.model.CsvDefinition;
import org.lorislab.corn.model.CsvGeneratorOutput;
import org.lorislab.corn.model.DataGeneratorOutput;

public class CSVObject implements List<CSVRow> {

    private String fileName;
    
    private final DataGeneratorOutput output;
    
    private final CsvDefinition definition;
    
    private final Map<String, Integer> header;
    
    private final List<List<Object>> data = new ArrayList<>();

    public CSVObject(DataGeneratorOutput output, CsvDefinition definition) {
        this.output = output;
        this.definition = definition;
        header = new HashMap<>(this.definition.columns.size());
        for (int i=0; i<this.definition.columns.size(); i++) {
            header.put(this.definition.columns.get(i), i);
        }
    }
    
    public void generate(Expressions expresion) {
        for (CsvGeneratorOutput out : output.csv) {
            String tmp = expresion.evaluateAllValueExpressions(out.rows);
            int rows = Integer.parseInt(tmp);
            for (int i=0; i<rows; i++) {
                expresion.addVariableValue(out.index, i);
                CSVRow row = addRow();
                for (Entry<String, Object> e : out.data.entrySet()) {
                    Object value = expresion.evaluate(e.getValue());                    
                    row.setColumn(e.getKey(), value);
                }
            }
        }
        fileName = expresion.evaluateAllValueExpressions(output.file);
    }

    public CsvDefinition getDefinition() {
        return definition;
    }
    
    public String getFileName() {
        return fileName;
    }
        
    public CSVRow addRow() {
        data.add(new ArrayList<>(Collections.nCopies(definition.columns.size(), null)));
        return getRow(data.size() - 1);
    }
    
    public Map<String, Integer> getHeader() {
        return header;
    }

    public List<List<Object>> getData() {
        return data;
    }
        
    public int getSize() {
        return data.size();
    }
    
    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }
    
    public CSVRow getRow(int row) {
        return new CSVRow(this, row);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object[] toArray(Object[] a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean add(CSVRow e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsAll(Collection c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CSVRow set(int index, CSVRow element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add(int index, CSVRow element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CSVRow remove(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ListIterator listIterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ListIterator listIterator(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public CSVRow get(int index) {
        return new CSVRow(this, index);
    }

}
