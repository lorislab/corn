package org.lorislab.corn;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static org.lorislab.corn.Logger.debug;
import org.lorislab.corn.js.Engine;
import org.lorislab.corn.model.DataDefinition;
import org.lorislab.corn.model.DataGenerator;
import org.lorislab.corn.model.DataGeneratorData;
import org.lorislab.corn.model.ServiceDataLoader;
import static org.lorislab.corn.Logger.info;
import org.lorislab.corn.csv.CSVObject;
import org.lorislab.corn.csv.CSVWritter;
import org.lorislab.corn.model.CsvDefinition;
import org.lorislab.corn.model.DataGeneratorItem;
import org.lorislab.corn.model.XmlDefinition;
import org.lorislab.corn.xml.XmlObject;
import org.lorislab.corn.xml.XmlWritter;

public class Main {

    private static final String LEVEL_PREFIX = "    ";

    private static final String SUBLEVEL_PREFIX = "  ";

    public static void main(String[] args) throws Exception {
        Path parent = Paths.get("output");

        Engine engine = new Engine();
        engine.add("SYS", new SystemBean());

        DataDefinition def = ServiceDataLoader.loadDefinitions("def/definitions.json");
        engine.add("def", def);
        DataGenerator gen = ServiceDataLoader.loadDataGenerator("def/generator4.json");
        engine.add("gen", gen);

        Map<String, Object> inputs = ServiceDataLoader.loadInputs("def/inputs.json");
        engine.add("input", inputs);
        for (Entry<String, Object> k : inputs.entrySet()) {
            engine.add(k.getKey(), k.getValue());
        }

        for (String key : gen.input) {
            if (!inputs.containsKey(key)) {
                throw new RuntimeException("Missing the input parameter: " + key);
            }
        }

        info("Inputs {");
        for (Entry<String, Object> e : inputs.entrySet()) {
            engine.add(e.getKey(), e.getValue());
            info("  " + e.getKey() + " : " + e.getValue());
        }
        info("}");

        info("Variables {");
        for (Entry<String, Object> e : gen.variable.entrySet()) {
            Object value = engine.evalVar(e.getValue());
            info("  " + e.getKey() + " : " + value);
            engine.add(e.getKey(), value);
        }
        info("}");

        generate(engine, "", parent, def, gen.data);
    }

    private static void generate(Engine engine, String prefix, Path parent, DataDefinition def, DataGeneratorData data) throws Exception {
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

                    Path path = CSVWritter.writeToFile(parent, obj);
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
                        Path path = XmlWritter.writeToFile(parent, xm);
                        
                        info(prefix + SUBLEVEL_PREFIX + "file : " + path);
                    }
                }

                if (item.data != null) {
                    info(prefix + SUBLEVEL_PREFIX + "items : [");
                    generate(engine, prefix + LEVEL_PREFIX, parent, def, item.data);
                    info(prefix + SUBLEVEL_PREFIX + "]");
                }
                
                info(prefix + "}" ); 
            }
        }
//        for (DataGeneratorData item : data) {
//
//        }
    }

//        for (DataGeneratorData list : data) {
//            int size = number(expression, list.size, "1");
//            for (int i = 0; i < size; i++) {
//                expression.addVariableValue(list.index, i);
//
//                for (DataGeneratorItem out : list.data) {
//
//                    if (precondition(expression, out.precondition, true)) {
//
//                        info(prefix + out.name + " {" );
//                        if (out.csv != null) {                            
//                            CsvDefinition csvDef = def.csv.get(out.definition);
//                            CSVObject csv = new CSVObject(out, csvDef);
//
//                            expression.addBean((String) expression.evaluate(out.name), csv);
//                            csv.generate(expression);
//
//                            Path path = CSVWritter.writeToFile(parent, csv);
//                            info(prefix + SUBLEVEL_PREFIX + "file :" + path);
//                            
//                        } else if (out.xml != null) {
//                            XmlDefinition xmlDef = def.xml.get(out.definition);
//                            XmlObject xml = new XmlObject(out, xmlDef);
//
//                            expression.addBean((String) expression.evaluate(out.name), xml);
//                            xml.generate(expression);
//
//                            Path path = XmlWritter.writeToFile(parent, xml);
//                            info(prefix + SUBLEVEL_PREFIX + "file : " + path);
//                            
//                            if (out.xml.validate) {
//                                XmlValidator.validate(path, xmlDef.xsds);
//                            }
//                        }
//                                                
//                        if (out.list != null && !out.list.isEmpty()) {
//                            info(prefix + SUBLEVEL_PREFIX + "items : [");
//                            generate(prefix + LEVEL_PREFIX, parent, expression, def, out.list);
//                            info(prefix + SUBLEVEL_PREFIX + "]");
//                        }                        
//                        info(prefix + "}" );                        
//                    }
//                }
//
//            }
//        }
//    }
}
