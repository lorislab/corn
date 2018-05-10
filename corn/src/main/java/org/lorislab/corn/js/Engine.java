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
package org.lorislab.corn.js;

import java.io.FileReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 *
 * @author andrej
 */
public class Engine {

    public final static Pattern PATTERN = Pattern.compile("#\\{(.+?)\\}");

    private static ScriptEngineManager mgr = new ScriptEngineManager();

    private ScriptEngine getEngine() {
        return mgr.getEngineByName("nashorn");
    }
    
    public void add(String name, Object value) {
        mgr.put(name, value);
    }

    public Object evalVar(Object value) throws Exception {
        if (value instanceof String) {
            return evalAll((String) value);
        }
        return value;
    }

    public int evalInt(Object value) throws Exception {
        if (value instanceof Integer) {
            return (int) value;
        }
        if (value instanceof String) {
            Object tmp = evalString((String) value);
            if (tmp instanceof Integer) {
                return (int) tmp;
            }
        }
        throw new RuntimeException("The value " + value + " is not int or Integer!");
    }

    public Object evalString(final String s) throws Exception {
        final Matcher matcher = PATTERN.matcher(s);
        if (matcher.find()) {
            return getEngine().eval(matcher.group(1));
        }
        return getEngine().eval(s);
    }

    public String evalAll(final String s) throws Exception {
        ScriptEngine e = getEngine();
        final StringBuffer sb = new StringBuffer();
        final Matcher matcher = PATTERN.matcher(s);
        while (matcher.find()) {
            final String expression = matcher.group(1);
            final Object result = e.eval(expression);
            matcher.appendReplacement(sb, result != null ? result.toString() : "");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public Map<String, Object> evalFile(String fileName) throws Exception {
        Object tmp = getEngine().eval(new FileReader(fileName));        
        return (Map<String, Object>) ScriptObjectMirror.wrapAsJSONCompatible(tmp, null);
    }
}
