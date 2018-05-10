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
