package org.lorislab.corn;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static org.lorislab.corn.log.Logger.debug;
import org.lorislab.corn.js.Engine;
import org.lorislab.corn.model.DataDefinition;
import org.lorislab.corn.model.DataGenerator;
import org.lorislab.corn.model.DataGeneratorData;
import org.lorislab.corn.model.DataLoader;
import static org.lorislab.corn.log.Logger.info;
import org.lorislab.corn.csv.CSVObject;
import org.lorislab.corn.csv.CSVWritter;
import org.lorislab.corn.model.CornConfig;
import org.lorislab.corn.model.CsvDefinition;
import org.lorislab.corn.model.DataGeneratorItem;
import org.lorislab.corn.model.XmlDefinition;
import org.lorislab.corn.xml.XmlObject;
import org.lorislab.corn.xml.XmlWritter;

public class Main {

    private static final String LEVEL_PREFIX = "    ";

    private static final String SUBLEVEL_PREFIX = "  ";

    public static void main(String[] args) throws Exception {
        
        CornConfig config = DataLoader.loadConfig("def/corn.json");
        
        Path target = Paths.get(config.target);

        Engine engine = new Engine();
        
        info("Beans {");
        if (config.beans != null && !config.beans.isEmpty()) {
            for (Entry<String, String> e : config.beans.entrySet()) {
                Object obj = Class.forName(e.getValue()).newInstance();
                engine.add(e.getKey(), obj);
                info(SUBLEVEL_PREFIX + e.getKey() + " : " + e.getValue());
            }
        }
        info("}");
        
        engine.add("parameters", config.parameters);
        info("Parameters {");
        if (config.parameters != null && !config.parameters.isEmpty()) {
            for (Entry<String, Object> k : config.parameters.entrySet()) {
                engine.add(k.getKey(), k.getValue());
                info(SUBLEVEL_PREFIX + k.getKey() + " : " + k.getValue());
            }
        }
        info("}");
        
        DataDefinition def = DataLoader.loadDefinitions(config.definition);
        engine.add("def", def);
        DataGenerator gen = DataLoader.loadDataGenerator(config.generator);
        engine.add("gen", gen);
        
        info("Variables {");
        for (Entry<String, Object> e : gen.variable.entrySet()) {
            Object value = engine.evalVar(e.getValue());
            info(SUBLEVEL_PREFIX + e.getKey() + " : " + value);
            engine.add(e.getKey(), value);
        }
        info("}");

        generate(engine, "", target, def, gen.data);
    }

    private static void generate(Engine engine, String prefix, Path target, DataDefinition def, DataGeneratorData data) throws Exception {
        int size = engine.evalInt(data.size);
        debug("Generate the size " + size);

        for (int i = 0; i < size; i++) {

            for (DataGeneratorItem item : data.items) {

                engine.add(data.index, i);
                info(prefix + "[" + i + "] " + item.name + " {" );
                
                CsvDefinition cf = def.csv.get(item.definition);
                if (cf != null) {
                    CSVObject obj = new CSVObject(item, cf);
                    engine.add(item.name, obj);

                    Map<String, Object> tmp = engine.evalFile(item.js);
                    obj.setFileName((String) tmp.get("file"));
                    obj.setData((List<Map<String, Object>>) tmp.get("data"));

                    Path path = CSVWritter.writeToFile(target, obj);
                    info(prefix + SUBLEVEL_PREFIX + "file :" + path);
                    
                } else {
                    XmlDefinition xcf = def.xml.get(item.definition);

                    XmlObject xm = new XmlObject(item, xcf);
                    engine.add(item.name, xm);

                    Map<String, Object> tmp = engine.evalFile(item.js);
                    if (tmp != null && !tmp.isEmpty()) {
                        xm.setFileName((String) tmp.get("file"));
                        xm.setRoot((String) tmp.get("root"));
                        xm.setNamespace((String) tmp.get("namespace"));
                        xm.setData((Map<String, Object>) tmp.get("data"));
                        xm.generate();
                        Path path = XmlWritter.writeToFile(target, xm);
                        
                        info(prefix + SUBLEVEL_PREFIX + "file : " + path);
                    }
                }

                if (item.data != null) {
                    info(prefix + SUBLEVEL_PREFIX + "items : [");
                    generate(engine, prefix + LEVEL_PREFIX, target, def, item.data);
                    info(prefix + SUBLEVEL_PREFIX + "]");
                }
                
                info(prefix + "}" ); 
            }
        }
    }

}
