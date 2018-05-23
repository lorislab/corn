/*
 * Copyright 2018 lorislab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE_2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lorislab.corn.xml;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

/**
 *
 * @author andrej
 */
public class XmlObjectInput {

    public Map<String, Object> data;
    
    public String file;

    public String root;

    public String namespace;

    public XmlConfig config;

    public XmlDefinition definition;
    
    public static class XmlDefinition {
        
        public String version;
        
        public List<String> xsds;
    }
    
    public static class XmlConfig {

        @SerializedName("xml_date_format")
        public String dateFormat;

        @SerializedName("xml_time_format")
        public String timeFormat;

        @SerializedName("xml_all_choices")
        public Boolean generateAllChoices;

        @SerializedName("xml_optional_attributes")
        public Boolean generateOptionalAttributes;

        @SerializedName("xml_fixed_attributes")
        public Boolean generateFixedAttributes;

        @SerializedName("xml_default_attributes")
        public Boolean generateDefaultAttributes;

        @SerializedName("xml_default_elements_value")
        public Boolean generateDefaultElementValues;

        @SerializedName("xml_optional_elements")
        public Boolean generateOptionalElements;

        @SerializedName("xml_max_elements")
        public Integer maximumElementsGenerated;

        @SerializedName("xml_min_elements")
        public Integer minimumElementsGenerated;

        @SerializedName("xml_min_list_items")
        public Integer minimumListItemsGenerated;

        @SerializedName("xml_max_list_items")
        public Integer maximumListItemsGenerated;

        @SerializedName("xml_max_recursion_depth")
        public Integer maximumRecursionDepth;
    }

}
