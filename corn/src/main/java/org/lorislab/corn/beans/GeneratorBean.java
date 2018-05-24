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
import java.util.Map;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.lorislab.corn.CornConfig;
import org.lorislab.corn.csv.CSVObject;
import org.lorislab.corn.csv.CSVObjectInput;
import org.lorislab.corn.gson.RequiredKeyAdapterFactory;
import org.lorislab.corn.xml.XmlObject;
import org.lorislab.corn.xml.XmlObjectInput;

/**
 *
 * @author andrej
 */
public class GeneratorBean {

    private final Path target;

    private final static Gson GSON = new GsonBuilder()
            .registerTypeAdapterFactory(new RequiredKeyAdapterFactory())
            .create();

    public GeneratorBean(CornConfig config) {
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
}
