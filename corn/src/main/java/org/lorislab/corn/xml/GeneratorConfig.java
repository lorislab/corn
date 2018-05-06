/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lorislab.corn.xml;

/**
 *
 * @author andrej
 */
public class GeneratorConfig {
    
    public String XSD_DATE_FORMAT = "yyyy-MM-dd";
    
    public String XSD_TIME_FORMAT = "HH:mm:ss";
    
    public boolean generateAllChoices = false;
    
    public Boolean generateOptionalAttributes = Boolean.TRUE;

    public Boolean generateFixedAttributes = Boolean.TRUE;

    public Boolean generateDefaultAttributes = Boolean.TRUE;

    public Boolean generateDefaultElementValues = Boolean.TRUE;

    public Boolean generateOptionalElements = Boolean.FALSE;

    public int maximumElementsGenerated = 0;

    public int minimumElementsGenerated = 1;
    
    public int minimumListItemsGenerated = 1;

    public int maximumListItemsGenerated = 0;

    public int maximumRecursionDepth = 30;    
}
