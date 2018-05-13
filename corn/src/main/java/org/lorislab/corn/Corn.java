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
package org.lorislab.corn;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.lorislab.corn.csv.CSVObject;
import org.lorislab.corn.js.Engine;
import static org.lorislab.corn.log.Logger.debug;
import static org.lorislab.corn.log.Logger.info;
import org.lorislab.corn.model.AbstractDataObject;
import org.lorislab.corn.model.CornConfig;
import org.lorislab.corn.model.DataDefinition;
import org.lorislab.corn.model.DataGenerator;
import org.lorislab.corn.model.DataGeneratorData;
import org.lorislab.corn.model.DataGeneratorItem;
import org.lorislab.corn.model.DataLoader;
import org.lorislab.corn.xml.XSDDefinition;
import org.lorislab.corn.xml.XmlObject;

/**
 *
 * @author andrej
 */
public class Corn {

    private static final String LEVEL_PREFIX = "    ";

    private static final String SUBLEVEL_PREFIX = "  ";

    private final Map<String, XSDDefinition> XSD_DEFINITIONS = new HashMap<>();

    private final CornConfig config;

    private final Path target;
    
    private final Map<String, DataDefinition> definitions;
    
    private final Engine engine;
    
    private final DataGenerator generator;
    
    public Corn(String config) {
        this(DataLoader.loadConfig(config));
    }
    
    public Corn(CornConfig config) {
        this.config = config;
        engine = new Engine();
        target = Paths.get(config.target);
        definitions = DataLoader.loadDefinitions(config.definition);
        generator = DataLoader.loadDataGenerator(config.generator);
    }

    public void generate() throws Exception {
        if (!Files.exists(target)) {
             Files.createDirectories(target);
        }

        info("Beans {");
        if (config.beans != null && !config.beans.isEmpty()) {
            for (Map.Entry<String, String> e : config.beans.entrySet()) {
                Object obj = Class.forName(e.getValue()).newInstance();
                engine.add(e.getKey(), obj);
                info(SUBLEVEL_PREFIX + e.getKey() + " : " + e.getValue());
            }
        }
        info("}");

        engine.add("parameters", config.parameters);
        info("Parameters {");
        if (config.parameters != null && !config.parameters.isEmpty()) {
            for (Map.Entry<String, Object> k : config.parameters.entrySet()) {
                engine.add(k.getKey(), k.getValue());
                info(SUBLEVEL_PREFIX + k.getKey() + " : " + k.getValue());
            }
        }
        info("}");

        engine.add("def", definitions);
        engine.add("gen", generator);

        info("Variables {");
        for (Map.Entry<String, Object> e : generator.variable.entrySet()) {
            Object value = engine.evalVar(e.getValue());
            info(SUBLEVEL_PREFIX + e.getKey() + " : " + value);
            engine.add(e.getKey(), value);
        }
        info("}");

        generate("", generator.data);
    }

    private void generate(String prefix, DataGeneratorData data) throws Exception {
        int size = engine.evalInt(data.size);
        debug("Generate the size " + size);

        for (int i = 0; i < size; i++) {

            for (DataGeneratorItem item : data.items) {

                engine.add(data.index, i);
                info(prefix + "[" + data.index + ":" + i + "] " + item.name + " {");

                DataDefinition def = definitions.get(item.definition);
                if (def != null) {

                    AbstractDataObject object = null;
                    if (def.xsds != null && !def.xsds.isEmpty()) {

                        XSDDefinition xsdDef = XSD_DEFINITIONS.get(def.name);
                        if (xsdDef == null) {
                            xsdDef = new XSDDefinition(def);
                            XSD_DEFINITIONS.put(def.name, xsdDef);
                        }

                        object = new XmlObject(xsdDef, item);
                    } else if (def.columns != null && !def.columns.isEmpty()) {
                        object = new CSVObject(def, item);
                    } else {
                        info("Could not found the definition type xml or csv base on the attributes [xsds | columns] for the name " + item.definition);
                    }

                    if (object != null) {
                        engine.add(object.getOutput().name, object);
                        Map<String, Object> tmp = null;
                        try {
                            tmp = engine.evalFile(object.getOutput().js);
                        } catch (Exception ex) {
                            throw new RuntimeException("Error executing the script for the step " + item.name, ex);
                        }

                        Path path = null;
                        if (tmp != null && !tmp.isEmpty()) {
                            path = object.generate(target, tmp);
                        }
                        info(prefix + SUBLEVEL_PREFIX + "file : " + path);
                    }

                } else {
                    info("Could not found the definition for the name " + item.definition);
                }

                if (item.data != null) {
                    info(prefix + SUBLEVEL_PREFIX + "items : [");
                    generate(prefix + LEVEL_PREFIX, item.data);
                    info(prefix + SUBLEVEL_PREFIX + "]");
                }

                info(prefix + "}");
            }
        }
    }
}
