package org.lorislab.corn.xml;

import org.lorislab.corn.gson.Required;

public class XmlMergeObject {

    @Required
    public String file;

    @Required
    public String include;

    @Required
    public String replace;
}
