package cz.webarchiv.WadminKonspekt.wadmin;

/**
 *
 * @author xrosecky
 */
public class ConspectusSubcategory {

    private int id; // id in database
    private int conspectusId; // NK, same as in db
    private String conspectusSubcategoryId; // NK

    public ConspectusSubcategory() {
    }

    public int getConspectusId() {
        return conspectusId;
    }

    public void setConspectusId(int conspectusId) {
        this.conspectusId = conspectusId;
    }

    public String getConspectusSubcategoryId() {
        return conspectusSubcategoryId;
    }

    public void setConspectusSubcategoryId(String conspectusSubcategoryId) {
        this.conspectusSubcategoryId = conspectusSubcategoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
}
