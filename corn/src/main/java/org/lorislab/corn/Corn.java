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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import static org.lorislab.corn.log.Logger.info;
import org.lorislab.corn.model.CornConfig;

/**
 *
 * @author andrej
 */
public class Corn {

    private CornConfig config;

    private static Path target;

    public Corn(String config) {
        this(loadConfig(config));
    }

    public Corn(CornConfig config) {
        this.config = config;
        target = Paths.get(config.target);
    }

    public static Path getTarget() {
        return target;
    }

    public void generate() throws Exception {

        if (!Files.exists(target)) {
            Files.createDirectories(target);
        }

        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("nashorn");

        engine.put("parameters", config.parameters);
        info("Parameters {");
        if (config.parameters != null && !config.parameters.isEmpty()) {
            config.parameters.entrySet().stream().map((k) -> {
                engine.put(k.getKey(), k.getValue());
                return k;
            }).forEachOrdered((k) -> {
                info(k.getKey() + " : " + k.getValue());
            });
        }
        info("}");

        try {
            engine.eval("load('" + config.run + "')");
        } catch (ScriptException ex) {
            throw new RuntimeException("Error executing the script for the step " + config.run, ex);
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
