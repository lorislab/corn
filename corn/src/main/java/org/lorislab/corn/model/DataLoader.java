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

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

/**
 * The data loader class.
 *
 * @author andrej
 */
public final class DataLoader {

    /**
     * Loads the corn configuration file.
     *
     * @param file the file name.
     * @return the corresponding corn configuration.
     */
    public static CornConfig loadConfig(String file) {
        try (FileInputStream​ in = new FileInputStream​(file)) {
            Jsonb jsonb = JsonbBuilder.create();
            return jsonb.fromJson(in, CornConfig.class);
        } catch (Exception ex) {
            throw new RuntimeException("Error loading the configuration file " + file, ex);
        }
    }

    /**
     * Loads the corn definitions file.
     *
     * @param file the file name.
     * @return the corresponding corn definitions.
     */
    public static Map<String, DataDefinition> loadDefinitions(String file) {
        Map<String, DataDefinition> result = null;
        try (FileInputStream​ in = new FileInputStream​(file)) {
            Jsonb jsonb = JsonbBuilder.create();
            List<DataDefinition> tmp = jsonb.fromJson(in, new ArrayList<DataDefinition>(){}.getClass().getGenericSuperclass());
            if (tmp != null) {
                result = new HashMap<>(tmp.size());
                for (DataDefinition item : tmp) {
                    result.put(item.name, item);
                }
            }
            return result;
        } catch (Exception ex) {
            throw new RuntimeException("Error loading the definitions file " + file, ex);
        }
    }

    /**
     * Loads the corn generator file.
     *
     * @param file the file name.
     * @return the corresponding corn generator.
     */
    public static DataGenerator loadDataGenerator(String file) {
        try (FileInputStream​ in = new FileInputStream​(file)) {
            Jsonb jsonb = JsonbBuilder.create();
            return jsonb.fromJson(in, DataGenerator.class);
        } catch (Exception ex) {
            throw new RuntimeException("Error loading the generator file " + file, ex);
        }
    }

}
