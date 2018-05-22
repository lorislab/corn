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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.lorislab.corn.Corn;
import org.lorislab.corn.CornConfig;
import org.lorislab.corn.csv.CSVObject;
import org.lorislab.corn.xml.XSDDefinition;
import org.lorislab.corn.xml.XmlObject;

/**
 *
 * @author andrej
 */
public class GeneratorBean {

    private static final Map<Integer, XSDDefinition> XSD_DEFINITIONS = new HashMap<>();

    private final Path target;
    
    public GeneratorBean(CornConfig config) {
        this.target = Paths.get(config.target);
    }

    public CSVObject csv(Object value) {

        Map<String, Object> data = (Map<String, Object>) ScriptObjectMirror.wrapAsJSONCompatible(value, null);

        Map<String, Object> tmp = (Map<String, Object>) data.get("definition");
        List<String> columns = (List<String>) tmp.get("columns");
        String separator = (String) tmp.get("separator");
        CSVObject result = new CSVObject(columns, separator);
        
        result.generate(target, data);
        return result;
    }

    public XmlObject xml(Object value) {

        Map<String, Object> data = (Map<String, Object>) ScriptObjectMirror.wrapAsJSONCompatible(value, null);

        Map<String, Object> tmp = (Map<String, Object>) data.get("definition");
        List<String> xsds = (List<String>) tmp.get("xsds");

        int code = 10;
        for (String s : xsds) {
            code = code * 31 + s.hashCode();
        }
        XSDDefinition xsdDef = XSD_DEFINITIONS.get(code);
        if (xsdDef == null) {
            xsdDef = new XSDDefinition(xsds);
            XSD_DEFINITIONS.put(code, xsdDef);
        }

        XmlObject result = new XmlObject(xsdDef);
        result.generate(target, data);
        return result;
    }
}
