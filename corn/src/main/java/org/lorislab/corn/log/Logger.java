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
package org.lorislab.corn.log;

public class Logger {
    
    public static boolean DEBUG = false;
    
    public static void info(Object message) {
        System.out.println(message);
    }

    public static void info() {
        System.out.println();
    }
    
    public static void debug(Object message) {
        if (DEBUG) {
            System.out.println(message);
        }
    }
    
    public static void error(Object message) {
        System.err.println(message);
    }    
    
    public static void error() {
        System.err.println();
    }    
}
