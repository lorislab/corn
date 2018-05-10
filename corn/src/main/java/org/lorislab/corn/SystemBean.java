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

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class SystemBean {
    
    public Date getCURRENT_DATE() {
        return new Date();
    }
    
    public String getUUID() {
        return UUID.randomUUID().toString();
    }
    
    public String string(int length) {
        return UUID.randomUUID().toString().substring(length);
    }
    
    public Object random(List data) {
        int index = ThreadLocalRandom.current().nextInt(0, data.size());
        return data.get(index);
    }
}
