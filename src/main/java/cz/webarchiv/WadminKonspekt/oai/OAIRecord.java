package cz.webarchiv.WadminKonspekt.oai;

import java.util.List;

/**
 *
 * @author xrosecky
 */
public class OAIRecord {
    private List<String> keywords;
    private String id;
    private int conspectusId;
    private String conspectusSubcategoryId;
    private String url;
    private String title;
    private String description;
    private String publisher;
    private String CNB;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCNB() {
        return CNB;
    }

    public void setCNB(String CNB) {
        this.CNB = CNB;
    }

    @Override
    public String toString() {
        StringBuilder words = new StringBuilder();
        String sep = "";
        for (String keyword : keywords) {
            words.append(sep+keyword);
            sep=",";
        }
        return String.format("[id:%s, url:%s, title:%s, description:%s, conspect:%s, conspectus_subcategory:%s, publisher:%s, keywords:%s]", id, url, title, description, conspectusId, conspectusSubcategoryId, publisher, words);
    }

}
