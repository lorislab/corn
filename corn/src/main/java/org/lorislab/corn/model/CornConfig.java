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
package org.lorislab.corn.model;

import java.util.Map;

/**
 * The corn configuration.
 * 
 * @author andrej
 */
public class CornConfig {
   
    /**
     * The definition file
     */
    public String definition;
    
    /**
     * The generator file
     */
    public String generator;
    
    /**
     * The target output directory
     */
    public String target;
    
    /**
     * The input parameters.
     */
    public Map<String, Object> parameters;
    
    /**
     * The external beans
     */
    public Map<String, String> beans;

}
