package cz.webarchiv.WadminKonspekt.wayback;

import java.util.Calendar;

/**
 *
 * @author xrosecky
 */
public class WaybackResource {

    private String url;
    private Calendar date;
    private String mimeType;
    private String httpResponseCode;

    public WaybackResource(String url, Calendar date, String mimeType, String httpResponseCode) {
        this.url = url;
        this.date = date;
        this.mimeType = mimeType;
        this.httpResponseCode = httpResponseCode;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getHttpResponseCode() {
        return httpResponseCode;
    }

    public void setHttpResponseCode(String httpResponseCode) {
        this.httpResponseCode = httpResponseCode;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
