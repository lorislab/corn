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
package org.lorislab.corn.xml;

import java.util.Map;

/**
 *
 * @author andrej
 */
public class StackItem {

    private Object data;

    private StackItem parent;

    private String name;

    private boolean first;

    private int defLevel;

    private int level;

    private StackItem parentElement;

    private Map<String, Object> definition;
    
    private Object value;
    
    public StackItem(Object data) {
        this.data = data;
        this.name = null;
        this.parent = null;
        this.level = 0;
        this.defLevel = 0;
        this.parentElement = null;
        this.first = false;
    }
    
    public StackItem(Object data, String name,Map<String, Object> definition) {
        this(data);
        this.name = name;
        this.definition = definition;
    }

    public StackItem(Object data, StackItem parent) {
        this(data);
        this.parent = parent;
        this.level = parent.level;
        this.defLevel = parent.defLevel + 1;
        if (parent.name != null) {
            this.parentElement = parent;
        } else {
            this.parentElement = parent.parentElement;
        }
    }

    public StackItem(Object data, StackItem parent, String name, Map<String,Object> definition, Object value) {
        this(data, parent);        
        this.level = parent.level + 1;
        this.name = name;
        this.value = value;
        this.definition = definition;
    }

    public String getParentElementName() {
        if (isParentElemet()) {
            return parentElement.name;
        }
        return null;
    }

    public boolean isParentElemet() {
        return parentElement != null;
    }
    
    public StackItem getParentElement() {
        return parentElement;
    }

    public Object getData() {
        return data;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public int getDefinitionLevel() {
        return defLevel;
    }

    public StackItem getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public Map<String, Object> getDefinition() {
        return definition;
    }

    public void setDefinition(Map<String, Object> definition) {
        this.definition = definition;
    }   

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
    
    
}
