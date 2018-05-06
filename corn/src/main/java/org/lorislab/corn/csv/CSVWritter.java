package org.lorislab.corn.csv;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSVWritter {

    public static Path writeToFile(Path parent, CSVObject csv) {
        Path path = parent.resolve(csv.getFileName());
//        Path path = Paths.get(csv.getFileName());
        
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            
            if (csv.getDefinition().showHeader) {
                Map<String, Integer> h = csv.getHeader();
                List<String> header = new ArrayList<>(h.size());
                h.entrySet().forEach((e) -> {
                    header.set(e.getValue(), e.getKey());
                });
                for (String name : header) {
                    writer.write(name);
                    writer.write(csv.getDefinition().separator);                    
                }
                writer.write('\n');
            }
            
            for (List<Object> row : csv.getData()) {
                for (Object item : row) {
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
