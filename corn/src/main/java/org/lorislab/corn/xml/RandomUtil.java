package org.lorislab.corn.xml;

public class RandomUtil {
    
    public static boolean randomBoolean(Boolean bool){
        if(Boolean.TRUE.equals(bool))
            return true;
        else if(Boolean.FALSE.equals(bool))
            return false;
        else // random either 0 or 1
            return randomBoolean();
    }
    
    public static int random(int min, int max){
        return (int)Math.round(min+Math.random()*(max-min));
    }
    
    public static boolean randomBoolean(){
        return Math.random()<0.5d;
    }
    
    public static double random(double min, double max){
        return Math.random()<0.5
                ? ((1-Math.random()) * (max-min) + min)
                : (Math.random() * (max-min) + min);
    }    
}
