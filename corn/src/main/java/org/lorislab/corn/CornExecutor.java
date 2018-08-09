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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * The corn executor.
 *
 * @author andrej
 */
public class CornExecutor {

    private static final Logger LOG = Logger.getLogger(CornExecutor.class.getName());
    
    public static void main(String[] args) throws Exception {
        String input = null;
        String target = UUID.randomUUID().toString();
        
        if (args != null && args.length > 0) {
            input = args[0];
        }
        
        if (input == null || input.isEmpty()) {
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
            LOG.log(Level.INFO, "\n------------------------------------------------------------------------\n"
                    + "Script file: {0}\n"
                    + "Column : {1}\n"
                    + "Line : {2}\n"
                    + "Message : {3}\n"
                    + "------------------------------------------------------------------------", 
                    new Object[]{ex.getFileName(), ex.getColumnNumber(), ex.getLineNumber(), ex.getMessage()});
        }
    }
    
    public static void execute(CornRequest request, String target) throws Exception, ScriptException {

        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("nashorn");

        Corn corn = new Corn();
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
