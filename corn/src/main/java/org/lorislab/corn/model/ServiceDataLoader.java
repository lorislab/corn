package org.lorislab.corn.model;

import java.io.FileInputStream;
import java.util.Map;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

public class ServiceDataLoader {
    
    public static DataDefinition loadDefinitions(String file) {
        try (FileInputStream​ in = new FileInputStream​(file)) {
            Jsonb jsonb = JsonbBuilder.create();
            return jsonb.fromJson(in, DataDefinition.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public static DataGenerator loadDataGenerator(String file) {
        try (FileInputStream​ in = new FileInputStream​(file)) {
            Jsonb jsonb = JsonbBuilder.create();
            return jsonb.fromJson(in, DataGenerator.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    } 
    
    public static Map<String, Object> loadInputs(String file) {
        try (FileInputStream​ in = new FileInputStream​(file)) {
            Jsonb jsonb = JsonbBuilder.create();
            return jsonb.fromJson(in, Map.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }    
}
