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
