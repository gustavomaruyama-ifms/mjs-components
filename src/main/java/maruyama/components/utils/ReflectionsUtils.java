/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maruyama.components.utils;

import java.lang.reflect.Field;

/**
 *
 * @author gustavo
 */
public class ReflectionsUtils {
    public static String[] getAtributesFromFields(Field fields[]){
        String[] stringFields = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
           stringFields[i] = fields[i].getName();
        }
        return stringFields;
    }
}
