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

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author andrej
 */
public class Corn {

    public void generate(String config) throws Exception {
        generate(loadConfig(config));        
    }
    
    public void generate(CornConfig config) throws Exception {

        Path target = Paths.get(config.target);
        if (!Files.exists(target)) {
            Files.createDirectories(target);
        }

        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("nashorn");

        engine.put(CornBean.NAME, new CornBean(target));
        engine.put("parameters", config.parameters);
        System.out.println("Parameters {");
        if (config.parameters != null && !config.parameters.isEmpty()) {
            config.parameters.entrySet().stream().map((k) -> {
                engine.put(k.getKey(), k.getValue());
                return k;
            }).forEachOrdered((k) -> {
                System.out.println(k.getKey() + " : " + k.getValue());
            });
        }
        System.out.println("}");

        try {
            engine.eval("load('" + config.run + "')");
        } catch (ScriptException ex) {
            System.err.println("ERROR ------------------------------------------------------------------");
            System.err.println("Script file: "  + ex.getFileName());
            System.err.println("Column : "  + ex.getColumnNumber());
            System.err.println("Line : "  + ex.getLineNumber());
            System.err.println("Message : "  + ex.getMessage());
            System.err.println("------------------------------------------------------------------------");
        }
    }

    /**
     * Loads the corn configuration file.
     *
     * @param file the file name.
     * @return the corresponding corn configuration.
     */
    private static CornConfig loadConfig(String file) {
        try (FileReader reader = new FileReader(file)) {
            return new Gson().fromJson(reader, CornConfig.class);
        } catch (Exception ex) {
            throw new RuntimeException("Error loading the configuration file " + file, ex);
        }
    }
}
