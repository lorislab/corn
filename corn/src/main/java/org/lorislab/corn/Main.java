package org.lorislab.corn;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.lorislab.corn.csv.CSVObject;
import org.lorislab.corn.csv.CSVWritter;
import org.lorislab.corn.el.Expressions;
import org.lorislab.corn.model.CsvDefinition;
import org.lorislab.corn.model.DataDefinition;
import org.lorislab.corn.model.DataGenerator;
import org.lorislab.corn.model.DataGeneratorList;
import org.lorislab.corn.model.DataGeneratorOutput;
import org.lorislab.corn.model.ServiceDataLoader;
import org.lorislab.corn.model.XmlDefinition;
import org.lorislab.corn.xml.XmlObject;
import org.lorislab.corn.xml.XmlWritter;
import org.lorislab.corn.xml.validator.XmlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String LEVEL_PREFIX = "    ";
    
    private static final String SUBLEVEL_PREFIX = "  ";
    
    public static void main(String[] args) throws Exception {
        Expressions expression = new Expressions();
        expression.addBean("SYS", new SystemBean());

        DataDefinition def = ServiceDataLoader.loadDefinitions("def/definitions.json");
        expression.addBean("def", def);
        DataGenerator gen = ServiceDataLoader.loadDataGenerator("def/generator2.json");
        expression.addBean("gen", gen);

        Properties prop = new Properties();
        try (InputStream ins = new FileInputStream("def/inputs.properties")) {
            prop.load(ins);
        }
        
        Path parent = Paths.get("output");
        Map<String, Object> inputs = new HashMap<>();
        for (String name: prop.stringPropertyNames()) {
            inputs.put(name, prop.getProperty(name));
        }
        
        for (String key : gen.input) {
            if (!inputs.containsKey(key)) {
                throw new RuntimeException("Missing the input parameter: " + key);
            }            
        }
        
        LOGGER.info("Inputs {");        
        for (Entry<String, Object> e : inputs.entrySet()) {
            expression.addVariableValue(e.getKey(), e.getValue());
            LOGGER.info("  " + e.getKey() + " : " + e.getValue());
        }
        LOGGER.info("}");
        
        LOGGER.info("Variables {");        
        for (Entry<String, Object> e : gen.variable.entrySet()) {
            Object value = e.getValue();
            if (value instanceof String) {
                value = expression.evaluateAllValueExpressions((String) value);
            }
            LOGGER.info("  " + e.getKey() + " : " + value);
            expression.addVariableValue(e.getKey(), value);
        }
        LOGGER.info("}");

        generate("", parent, expression, def, gen.list);
    }

    private static int number(Expressions expression, String value, String defaultValue) {
        String tmp = expression.evaluateString(value, defaultValue);
        return Integer.parseInt(tmp);
    }

    private static boolean precondition(Expressions expression, String value, boolean defaultValue) {
        if (value != null && !value.isEmpty()) {
            return expression.evaluateValueExpression(value);
        }
        return defaultValue;
    }

    private static void generate(String prefix, Path parent, Expressions expression, DataDefinition def, List<DataGeneratorList> data) {
        for (DataGeneratorList list : data) {
            int size = number(expression, list.size, "1");
            for (int i = 0; i < size; i++) {
                expression.addVariableValue(list.index, i);

                for (DataGeneratorOutput out : list.data) {

                    if (precondition(expression, out.precondition, true)) {

                        LOGGER.info(prefix + out.name + " {" );
                        if (out.csv != null) {                            
                            CsvDefinition csvDef = def.csv.get(out.definition);
                            CSVObject csv = new CSVObject(out, csvDef);

                            expression.addBean((String) expression.evaluate(out.name), csv);
                            csv.generate(expression);

                            Path path = CSVWritter.writeToFile(parent, csv);
                            LOGGER.info(prefix + SUBLEVEL_PREFIX + "file :" + path);
                            
                        } else if (out.xml != null) {
                            XmlDefinition xmlDef = def.xml.get(out.definition);
                            XmlObject xml = new XmlObject(out, xmlDef);

                            expression.addBean((String) expression.evaluate(out.name), xml);
                            xml.generate(expression);

                            Path path = XmlWritter.writeToFile(parent, xml);
                            LOGGER.info(prefix + SUBLEVEL_PREFIX + "file : " + path);
                            
                            if (out.xml.validate) {
                                XmlValidator.validate(path, xmlDef.xsds);
                            }
                        }
                                                
                        if (out.list != null && !out.list.isEmpty()) {
                            LOGGER.info(prefix + SUBLEVEL_PREFIX + "items : [");
                            generate(prefix + LEVEL_PREFIX, parent, expression, def, out.list);
                            LOGGER.info(prefix + SUBLEVEL_PREFIX + "]");
                        }                        
                        LOGGER.info(prefix + "}" );                        
                    }
                }

            }
        }
    }
}
