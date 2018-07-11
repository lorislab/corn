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
import java.util.UUID;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * The corn executor.
 *
 * @author andrej
 */
public class CornExecutor {

    public static void main(String[] args) throws Exception {
        String input = null;
        String target = UUID.randomUUID().toString();
        
        if (args != null && args.length > 0) {
            input = args[0];
        }
        
        if (input == null && input.isEmpty()) {
            throw new RuntimeException("Missing script file to run!");
        }
        
        if (args != null && args.length > 1) {
            target = args[1];
        }
        
        Gson gson = new Gson();
	CornRequest request = gson.fromJson(new FileReader(input), CornRequest.class);
        
        try {
            CornExecutor.execute(request, target);
        } catch (ScriptException ex) {
            System.err.println("------------------------------------------------------------------------");
            System.err.println("Script file: "  + ex.getFileName());
            System.err.println("Column : "  + ex.getColumnNumber());
            System.err.println("Line : "  + ex.getLineNumber());
            System.err.println("Message : "  + ex.getMessage());
            System.err.println("------------------------------------------------------------------------");
        }
    }
    
    public static void execute(CornRequest request, String target) throws Exception, ScriptException {

        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("nashorn");

        Corn corn = new Corn();
        corn.setEngine(engine);
        corn.setTarget(target);

        engine.put("corn", corn);
        engine.put("arguments", request.getArguments());
        if (request.getData() != null && !request.getData().isEmpty()) {
            request.getData().entrySet().forEach((entry) -> {
                engine.put(entry.getKey(), entry.getValue());
            });
        }
        engine.eval("load('" + request.getRun() + "')");

    }
}
