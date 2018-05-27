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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * The main class.
 * 
 * @author andrej
 */
public class Main {
    
    public static void main(String[] args) {
        String run = null;
        if (args != null && args.length > 0) {
            run = args[0];
        }
        
        if (run == null && run.isEmpty()) {
            throw new RuntimeException("Missing script file to run!");
        }
        
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("nashorn");

        try {
            engine.put("arguments", args);
            engine.eval("load('" + run + "')");
        } catch (ScriptException ex) {
            System.err.println("ERROR ------------------------------------------------------------------");
            System.err.println("Script file: "  + ex.getFileName());
            System.err.println("Column : "  + ex.getColumnNumber());
            System.err.println("Line : "  + ex.getLineNumber());
            System.err.println("Message : "  + ex.getMessage());
            System.err.println("------------------------------------------------------------------------");
        }
    }

}
