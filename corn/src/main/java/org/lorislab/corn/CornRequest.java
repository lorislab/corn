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

import java.util.List;
import java.util.Map;

/**
 * The executor request.
 * 
 * @author andrej
 */
public class CornRequest {
    
    private String run;
    
    private List<String> arguments;
    
    private Map<String, Object> data;

    /**
     * @return the run
     */
    public String getRun() {
        return run;
    }

    /**
     * @param run the run to set
     */
    public void setRun(String run) {
        this.run = run;
    }

    /**
     * @return the arguments
     */
    public List<String> getArguments() {
        return arguments;
    }

    /**
     * @param arguments the arguments to set
     */
    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    /**
     * @return the data
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    
}
