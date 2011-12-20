package cz.webarchiv.wayback;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author xrosecky
 */
public class WaybackSearcher {

    protected Wayback wayback = new Wayback();

    public WaybackSearcher() {
    }

    public WaybackSearcher(Wayback wayback) {
        this.wayback = wayback;
    }

    public static Map<String, String> loadSubCategories() {
        Map<String, String> result = new HashMap<String, String>();
        InputStream is = WaybackSearcher.class.getResourceAsStream("/subcategories_mapping.csv");
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

    public List<Page> search(String pageUrl) {
        try {
            String waybackURL = String.format("http://%s:%s/%s/xmlquery?type=urlquery&url=%s", wayback.getHost(),
                    wayback.getPort(), wayback.getPrefix(), URLEncoder.encode(pageUrl, "UTF-8"));
            URL url = new URL(waybackURL);
            URLConnection urlConnection = url.openConnection();
            if (urlConnection instanceof HttpURLConnection) {
                HttpURLConnection httpCon = (HttpURLConnection) urlConnection;
                if (httpCon.getResponseCode() == 200) {
                    return parseResponse(httpCon.getInputStream());
                } else {
                    throw new Exception("Error fetching pages, wayback response code is "+httpCon.getResponseCode());
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

    public List<Page> parseResponse(InputStream is) throws Exception {
        List<Page> pages = new ArrayList<Page>();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        if (doc.getElementsByTagName("results").getLength() == 0) {
            return Collections.emptyList();
        }
        NodeList results = ((Element) doc.getElementsByTagName("results").item(0)).getElementsByTagName("result");
        for (int i = 0; i != results.getLength(); i++) {
            Element elm = (Element) results.item(i);
            String url = elm.getElementsByTagName("url").item(0).getTextContent();
            String date = elm.getElementsByTagName("capturedate").item(0).getTextContent();
            Page page = new Page();
            page.setUrl(url);
            page.setDate(date);
            pages.add(page);
        }
        return pages;
    }

    public static void main(String[] args) {
        WaybackSearcher wayback = new WaybackSearcher();
        List<Page> pages = wayback.search("http://www.domena.ktera.neexistuje.cz/");
        for (Page page : pages) {
            System.out.println(page.getDate()+"/"+page.getUrl());
        }
    }
}
