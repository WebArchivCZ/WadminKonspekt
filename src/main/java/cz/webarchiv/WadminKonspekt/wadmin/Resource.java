package cz.webarchiv.WadminKonspekt.wadmin;

/**
 *
 * @author Vaclav Rosecky
 */
public class Resource {

    private int id;
    private String alephId;
    private String url;
    private String title;
    private String description;
    private int conspectusId;
    private int conspectusSubcategoryId;
    
    public Resource() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlephId() {
        return alephId;
    }

    public void setAlephId(String alephId) {
        this.alephId = alephId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getConspectusId() {
        return conspectusId;
    }

    public void setConspectusId(int conspectusId) {
        this.conspectusId = conspectusId;
    }

    public int getConspectusSubcategoryId() {
        return conspectusSubcategoryId;
    }

    public void setConspectusSubcategoryId(int conspectusSubcategoryId) {
        this.conspectusSubcategoryId = conspectusSubcategoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}

