package cz.webarchiv.wayback;

/**
 *
 * @author xrosecky
 */
public class Wayback {

    public String host = "har.webarchiv.cz";
    public int port = 8080;
    public String prefix = "AP1";

    public Wayback(String host, int port, String prefix) {
        this.host = host;
        this.port = port;
        this.prefix = prefix;
    }

    public Wayback() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getURL(Page page) {
        return String.format("http://%s:%s/%s/%s/%s", host, port, prefix, page.getDate(), page.getUrl());
    }

    public String getURL() {
        return String.format("http://%s:%s/%s/", host, port, prefix);
    }

    public Page extractPage(String url) {
        Page page = new Page();
        String waybackUrl = this.getURL();
        if (url.startsWith(waybackUrl)) {
            page.setDate(url.substring(waybackUrl.length(), waybackUrl.length()+14));
            page.setUrl(url.substring(waybackUrl.length()+15, url.length()));
        } else {
            throw new IllegalArgumentException(url+"is not wayback URL!");
        }
        return page;
    }

}
