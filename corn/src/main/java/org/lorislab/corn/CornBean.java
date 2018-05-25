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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
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
public class CornBean {

    public static final String NAME = "corn";
    
    private final Path target;

    private final static Gson GSON = new GsonBuilder()
            .registerTypeAdapterFactory(new RequiredKeyAdapterFactory())
            .create();

    public CornBean(String target) {
        this(Paths.get(target));
    }
    
    public CornBean(Path target) {
        this.target = target;
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
  
}
