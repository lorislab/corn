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

/**
 *
 * @author andrej
 */
public class GeneratorConfig {
    
    public String dateFormat = "yyyy-MM-dd";
    
    public String timeFormat = "HH:mm:ss";
    
    public boolean generateAllChoices = false;

    public boolean omitXmlDeclaration = false;

    public Boolean generateOptionalAttributes = Boolean.TRUE;

    public Boolean generateFixedAttributes = Boolean.TRUE;

    public Boolean generateDefaultAttributes = Boolean.TRUE;

    public Boolean generateDefaultElementValues = Boolean.TRUE;

    public Boolean generateOptionalElements = Boolean.FALSE;

    public Boolean generateDefaultAnyElement = Boolean.TRUE;

    public int maximumElementsGenerated = 0;

    public int minimumElementsGenerated = 1;
    
    public int minimumListItemsGenerated = 1;

    public int maximumListItemsGenerated = 0;

    public int maximumRecursionDepth = 30;    
}
