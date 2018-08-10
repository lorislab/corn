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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPathFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.lorislab.corn.csv.CSVObject;
import org.lorislab.corn.csv.CSVObjectInput;
import org.lorislab.corn.file.FileObject;
import org.lorislab.corn.file.FileObjectInput;
import org.lorislab.corn.gson.RequiredKeyAdapterFactory;
import org.lorislab.corn.gzip.GzipObject;
import org.lorislab.corn.xml.XmlObject;
import org.lorislab.corn.xml.XmlObjectInput;
import org.lorislab.corn.zip.ZipObject;

/**
 * The generator bean.
 *
 * @author andrej
 */
public class Corn {

    private static final Logger LOG = Logger.getLogger(Corn.class.getName());
    
    private Path target = Paths.get("target");

    private Map<String, Object> parameters = new HashMap<>();

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
        CSVObjectInput input = parseInput(value, CSVObjectInput.class);
        if (input != null) {
            CSVObject result = new CSVObject(input);
            Path path = result.generate(target);
            LOG.log(Level.INFO, "{0}", path);
            return result;
        }
        return null;
    }

    public XmlObject xml(Object value) {
        createTaget();
        XmlObjectInput input = parseInput(value, XmlObjectInput.class);
        if (input != null) {
            XmlObject result = new XmlObject(input, factory);
            Path path = result.generate(target);
            LOG.log(Level.INFO, "{0}", path);
            return result;
        }
        return null;
    }

    public FileObject file(Object value) {
        createTaget();
        FileObjectInput input = parseInput(value, FileObjectInput.class);
        if (input != null) {
            FileObject result = new FileObject(input);
            Path path = result.generate(target);
            LOG.log(Level.INFO, "{0}", path);
            return result;
        }
        return null;
    }

    public String gzip(String input, String output) {
        createTaget();
        if (input != null && output != null) {
            Path path = GzipObject.generate(target, input, output);
            LOG.log(Level.INFO, "{0}", path);
            return path.toString();
        }
        return null;
    }

    public String zip(String input, String output) {
        createTaget();
        if (input != null && output != null) {
            Path path = ZipObject.generate(target, input, output);
            LOG.log(Level.INFO, "{0}", path);
            return path.toString();
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

    public void println(String value) {
        if (log) {
            LOG.info(value);
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

    public boolean mkdir(String file) {
        boolean result = true;
        try {
            FileObject.mkdir(target, file);
            LOG.log(Level.INFO, "{0}", file);
        } catch (Exception ex) {
            result = false;
            LOG.log(Level.SEVERE, "Directory {0} could not be created. {1}", new Object[]{file, ex.getMessage()});
        }
        return result;
    }
    
    public boolean delete(String file) {
        boolean result = true;
        try {
            FileObject.delete(target, file);
            LOG.log(Level.INFO, "{0}", file);
        } catch (Exception ex) {
            result = false;
            LOG.log(Level.SEVERE, "File {0} could not be deleted. {1}", new Object[]{file, ex.getMessage()});
        }
        return result;
    }

    public boolean copy(String input, String output) {
        boolean result = true;
        try {
            FileObject.copy(target, input, output);
            LOG.log(Level.INFO, "{0} - {1}", new Object[]{input, output});
        } catch (Exception ex) {
            result = false;
            LOG.log(Level.SEVERE, "File {0} could not be copied to {1} error: {2}", new Object[]{input, output, ex.getMessage()});
        }
        return result;
    }

    private static <T> T parseInput(Object value, Class<T> clazz) {
        Map<String, Object> data = (Map<String, Object>) ScriptObjectMirror.wrapAsJSONCompatible(value, null);
        JsonElement e = GSON.toJsonTree(data);
        T result = GSON.fromJson(e, clazz);
        return result;
    }
}
