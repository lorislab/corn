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

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import static org.lorislab.corn.CommonTest.TARGET;

/**
 *
 * @author apetras
 */
public class ScriptTests extends CommonTest {

    private static final Logger LOG = Logger.getLogger(ScriptTests.class.getName());

    @TestFactory
    public Iterable<DynamicTest> scriptTests() throws Exception {
        DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("src/test/resources/corn"));

        List<DynamicTest> tests = new ArrayList<>();
        stream.forEach(file -> tests.add(DynamicTest.dynamicTest(file.getFileName().toString(),
                () -> {
                    try {
                        LOG.log(Level.INFO, "Start the test case for the script file: {0}", file.toString());
                        String tmp = file.toString();
                        Assertions.assertTrue(tmp.endsWith(".js"));
                        tmp = tmp.replaceAll("\\\\", "/");
                        CornRequest request = new CornRequest();
                        request.setRun(tmp);
                        CornExecutor.execute(request, TARGET);
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, "Error executing the script test for the file {0}", file.toString());
                        ex.printStackTrace();
                        Assertions.assertNull(ex, "Error executing the script test for the file " + file.toString());
                    }
                }
        )));
        return tests;
    }
}
