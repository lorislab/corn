package org.lorislab.corn.js;

import java.io.FileReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 *
 * @author andrej
 */
public class Engine {

    public final static Pattern PATTERN = Pattern.compile("#\\{(.+?)\\}");

    private static ScriptEngineManager mgr = new ScriptEngineManager();

    private ScriptEngine engine;

    public Engine() {
        engine = mgr.getEngineByName("nashorn");
    }

    public void add(String name, Object value) {
        engine.put(name, value);
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
            return engine.eval(matcher.group(1));
        }
        return engine.eval(s);
    }

    public String evalAll(final String s) throws Exception {
        final StringBuffer sb = new StringBuffer();
        final Matcher matcher = PATTERN.matcher(s);
        while (matcher.find()) {
            final String expression = matcher.group(1);
            final Object result = engine.eval(expression);
            matcher.appendReplacement(sb, result != null ? result.toString() : "");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public Map<String, Object> evalFile(String fileName) throws Exception {
        Object tmp = engine.eval(new FileReader(fileName));        
        return (Map<String, Object>) ScriptObjectMirror.wrapAsJSONCompatible(tmp, null);
    }
}
