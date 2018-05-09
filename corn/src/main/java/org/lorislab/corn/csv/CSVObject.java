package org.lorislab.corn.csv;

import java.util.List;
import java.util.Map;
import org.lorislab.corn.model.CsvDefinition;
import org.lorislab.corn.model.DataGeneratorItem;

public class CSVObject {

    private String fileName;
    
    private final DataGeneratorItem output;
    
    private final CsvDefinition definition;
    
    private List<Map<String, Object>> data;

    public CSVObject(DataGeneratorItem output, CsvDefinition definition) {
        this.output = output;
        this.definition = definition;
    }
    
    public int getRows() {
        return data.size();
    }
    
    public void generate(Object expresion) {
//        for (CsvGeneratorOutput out : output.csv) {
//            String tmp = expresion.evaluateAllValueExpressions(out.rows);
//            int rows = Integer.parseInt(tmp);
//            for (int i=0; i<rows; i++) {
//                expresion.addVariableValue(out.index, i);
//                CSVRow row = addRow();
//                for (Entry<String, Object> e : out.data.entrySet()) {
//                    Object value = expresion.evaluate(e.getValue());                    
//                    row.setColumn(e.getKey(), value);
//                }
//            }
//        }
//        fileName = expresion.evaluateAllValueExpressions(output.file);
    }

    public CsvDefinition getDefinition() {
        return definition;
    }
    
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
            
    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
            
    public int getSize() {
        return data.size();
    }
    
}
