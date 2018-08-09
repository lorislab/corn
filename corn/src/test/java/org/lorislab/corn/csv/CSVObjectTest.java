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
package org.lorislab.corn.csv;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lorislab.corn.CommonTest;

public class CSVObjectTest extends CommonTest {

    @Test
    public void testUTF() throws Exception {
        CSVObjectInput input = new CSVObjectInput();
        input.file = "testUTF.csv";
        input.definition = new CSVObjectInput.CSVDefinition();
        input.definition.columns = Arrays.asList("A", "B", "V");
        input.definition.separator = "#";
        input.definition.version = "1";

        input.data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, Object> tmp = new HashMap<>();
            tmp.put("A", "ÐA" + i);
            tmp.put("B", "BÐ" + i);
            input.data.add(tmp);
        }
        CSVObject object = new CSVObject(input);
        object.generate(TARGET_PATH);
        Assertions.assertTrue(Files.exists(Paths.get(TARGET + input.file)));
    }

    @Test
    public void testISO() throws Exception {
        CSVObjectInput input = new CSVObjectInput();
        input.file = "testISO.csv";
        input.definition = new CSVObjectInput.CSVDefinition();
        input.definition.columns = Arrays.asList("A", "B", "V");
        input.definition.separator = "#";
        input.definition.version = "1";
        input.definition.charset = "ISO-8859-1";

        input.data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, Object> tmp = new HashMap<>();
            tmp.put("A", "ÐA" + i);
            tmp.put("B", "BÐ" + i);
            input.data.add(tmp);
        }
        CSVObject object = new CSVObject(input);
        object.generate(TARGET_PATH);
        Assertions.assertTrue(Files.exists(Paths.get(TARGET + input.file)));
    }

    @Test
    public void testISOError() throws Exception {
        Exception exception = null;
        try {
            CSVObjectInput input = new CSVObjectInput();
            input.file = "testISOError.csv";
            input.definition = new CSVObjectInput.CSVDefinition();
            input.definition.columns = Arrays.asList("A", "B", "V");
            input.definition.separator = "#";
            input.definition.version = "1";
            input.definition.charset = "ISO-8859-1";

            input.data = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                HashMap<String, Object> tmp = new HashMap<>();
                tmp.put("A", "ŠA" + i);
                tmp.put("B", "BÐ" + i);
                input.data.add(tmp);
            }
            CSVObject object = new CSVObject(input);
            object.generate(TARGET_PATH);
        } catch (Exception ex) {
            exception = ex;
        }
        Assertions.assertNotNull(exception);
    }
}
