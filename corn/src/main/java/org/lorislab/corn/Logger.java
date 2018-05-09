package org.lorislab.corn;

public class Logger {
    
    public static boolean DEBUG = false;
    
    public static void info(Object message) {
        System.out.println(message);
    }
    
    public static void debug(Object message) {
        if (DEBUG) {
            System.out.println(message);
        }
    }
    
    public static void error(Object message) {
        System.err.println(message);
    }    
}
