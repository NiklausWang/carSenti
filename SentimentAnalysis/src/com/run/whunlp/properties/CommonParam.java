package com.run.whunlp.properties;
 
import java.util.MissingResourceException;
import java.util.ResourceBundle;
 
/**
 * @author Henry_zp
 */
public class CommonParam {
    private String propertyFileName;
    private ResourceBundle resourceBundle;
    public CommonParam(String resourceFilePath) {
        propertyFileName = resourceFilePath;
        resourceBundle = ResourceBundle.getBundle(propertyFileName);
    }
    public String getString(String key) {
        if (key == null || key.equals("") || key.equals("null")) {
            return "";
        }
        String result = "";
        try {
            result = resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
        return result;
    }
}