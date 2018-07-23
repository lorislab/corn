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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.script.ScriptEngine;
import javax.xml.xpath.XPathFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.lorislab.corn.csv.CSVObject;
import org.lorislab.corn.csv.CSVObjectInput;
import org.lorislab.corn.gson.RequiredKeyAdapterFactory;
import org.lorislab.corn.xml.XmlObject;
import org.lorislab.corn.xml.XmlObjectInput;

/**
 * The generator bean.
 *
 * @author andrej
 */
public class Corn {

    private Path target = Paths.get("target");

    private Map<String, Object> parameters = new HashMap<>();

    private ScriptEngine engine;

    private static final Object LOCK = new Object();
        
    private final XPathFactory factory;

    private boolean log = true;
    
    public Corn() {
        synchronized (LOCK) {
            factory = XPathFactory.newInstance();
        }
    }
    
    private final static Gson GSON = new GsonBuilder()
            .registerTypeAdapterFactory(new RequiredKeyAdapterFactory())
            .create();
    
    public void setTarget(String target) {
        if (target != null && !target.isEmpty()) {
            this.target = Paths.get(target);
        }
    }
    
    public void loadParameters(String name) {
        parameters = loadJson(name);
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public void setEngine(ScriptEngine engine) {
        this.engine = engine;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public boolean isLog() {
        return log;
    }
    
    public CSVObject csv(Object value) {
        createTaget();
        Map<String, Object> data = (Map<String, Object>) ScriptObjectMirror.wrapAsJSONCompatible(value, null);
        JsonElement e = GSON.toJsonTree(data);
        CSVObjectInput input = GSON.fromJson(e, CSVObjectInput.class);
        if (input != null) {
            CSVObject result = new CSVObject(input);
            Path path = result.generate(target);
            println("File: " + path + "\n");
            return result;
        }
        return null;
    }

    public XmlObject xml(Object value) {
        createTaget();
        Map<String, Object> data = (Map<String, Object>) ScriptObjectMirror.wrapAsJSONCompatible(value, null);
        JsonElement e = GSON.toJsonTree(data);
        XmlObjectInput input = GSON.fromJson(e, XmlObjectInput.class);
        if (input != null) {
            XmlObject result = new XmlObject(input, factory);
            Path path = result.generate(target);
            println("File: " + path + "\n");
            return result;
        }
        return null;
    }

    public String date(String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(new Date());
    }

    public String date(Date date, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(date);
    }

    public String date(String format, String language, String country) {
        SimpleDateFormat sf = new SimpleDateFormat(format, new Locale(language, country));
        return sf.format(new Date());
    }

    public String date(Date date, String format, String language, String country) {
        SimpleDateFormat sf = new SimpleDateFormat(format, new Locale(language, country));
        return sf.format(date);
    }

    public Date date() {
        return new Date();
    }

    public String uuid() {
        return UUID.randomUUID().toString();
    }

    public String uuid(int length) {
        String result = uuid().substring(0, length);
        return result;
    }

    private void createTaget() {
        try {
            if (!Files.exists(this.target)) {
                Files.createDirectories(this.target);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error creating the target directory: " + target, ex);
        }        
    }
    
    private void println(String value) {
        try {
            if (log && engine != null) {
                engine.getContext().getWriter().write(value);
                engine.getContext().getWriter().flush();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error write the log message " + value, ex);
        }
    }
    
    /**
     * Loads the corn configuration file.
     *
     * @param file the file name.
     * @return the corresponding corn configuration.
     */
    public Map<String, Object> loadJson(String file) {
        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            return new Gson().fromJson(reader, type);
        } catch (Exception ex) {
            throw new RuntimeException("Error loading the configuration file " + file, ex);
        }
    }
}
