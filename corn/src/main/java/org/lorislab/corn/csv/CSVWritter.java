package org.lorislab.corn.csv;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class CSVWritter {

    public static Path writeToFile(Path parent, CSVObject csv) {
        Path path = parent.resolve(csv.getFileName());
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {                        
            for (Map<String, Object> row : csv.getData()) {                
                for (String col : csv.getDefinition().columns) {
                    Object item = row.get(col);
                    if (item != null) {
                        writer.write(item.toString());
                    }
                    writer.write(csv.getDefinition().separator);
                }          
                writer.write('\n');
            }
            
        } catch (Exception ex) {
            throw new RuntimeException("Error write CSV ", ex);
        }
        return path;
    }
}
