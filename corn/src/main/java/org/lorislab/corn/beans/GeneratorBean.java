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
package org.lorislab.corn.beans;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.lorislab.corn.CornConfig;
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
public class GeneratorBean {

    private final Path target;

    private final static Gson GSON = new GsonBuilder()
            .registerTypeAdapterFactory(new RequiredKeyAdapterFactory())
            .create();

    public GeneratorBean(NashornScriptEngine engine) {
        CornConfig config = (CornConfig) engine.get(CornConfig.NAME);
        this.target = Paths.get(config.target);
    }

    public CSVObject csv(Object value) {
        Map<String, Object> data = (Map<String, Object>) ScriptObjectMirror.wrapAsJSONCompatible(value, null);
        JsonElement e = GSON.toJsonTree(data);
        CSVObjectInput input = GSON.fromJson(e, CSVObjectInput.class);
        if (input != null) {
            CSVObject result = new CSVObject(input);
            result.generate(target);
            return result;
        }
        return null;
    }

    public XmlObject xml(Object value) {
        Map<String, Object> data = (Map<String, Object>) ScriptObjectMirror.wrapAsJSONCompatible(value, null);
        JsonElement e = GSON.toJsonTree(data);
        XmlObjectInput input = GSON.fromJson(e, XmlObjectInput.class);
        if (input != null) {
            XmlObject result = new XmlObject(input);
            result.generate(target);
            return result;
        }
        return null;
    }
    
    public String currentDateFormat(String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(new Date());
    }
    
    public String dateFormat(Date date, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(date);
    }
    
    public String currentDateFormat(String format, String language, String country) {
        SimpleDateFormat sf = new SimpleDateFormat(format, new Locale(language, country));
        return sf.format(new Date());
    }
    
    public String dateFormat(Date date, String format, String language, String country) {
        SimpleDateFormat sf = new SimpleDateFormat(format, new Locale(language, country));
        return sf.format(date);
    }
    
    public Date currentDate() {
        return new Date();
    }

    public String uuidRandom() {
        return UUID.randomUUID().toString();
    }
    
    public String uuidRandom(int length) {
        String result = uuidRandom().substring(0, length);
        return result;
    }
    
    public String uuidToString(int length) {
        String result = uuidRandom().substring(0, length);
        return result;
    }    
}
