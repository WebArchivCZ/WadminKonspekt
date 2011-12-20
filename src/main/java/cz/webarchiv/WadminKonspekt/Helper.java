package cz.webarchiv.WadminKonspekt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author xrosecky
 */
public class Helper {

    public static Map<String, String> loadSubCategories() {
        Map<String, String> result = new HashMap<String, String>();
        InputStream is = Helper.class.getResourceAsStream("/subcategories_mapping.csv");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = null;
            while ((line = in.readLine()) != null) {
                String[] array = line.split(",");
                result.put(array[1].trim(), array[0].trim());
            }
            return result;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

