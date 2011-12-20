package cz.webarchiv.WadminKonspekt.wayback;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author xrosecky
 */
public class WaybackConnector {

    private final String waybackURL;

    public WaybackConnector(String url) {
        this.waybackURL = url;
    }

    public List<WaybackResource> query(String url) {
        List<WaybackResource> result = new ArrayList<WaybackResource>();
        DateFormat formatter = new SimpleDateFormat("yyyymmddHHmmss");
        try {
            URL conn = new URL(waybackURL + "xmlquery?type=urlquery&url="+java.net.URLEncoder.encode(url.trim(), "UTF-8"));
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(conn.openConnection().getInputStream());
            if (doc.getElementsByTagName("error").getLength() > 0) {
                return Collections.emptyList();
            }
            NodeList nodes = ((Element) doc.getElementsByTagName("results").item(0)).getElementsByTagName("result");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                Calendar cal = new GregorianCalendar();
                cal.setTime(formatter.parse(element.getElementsByTagName("capturedate").item(0).getTextContent()));
                String resourceUrl = element.getElementsByTagName("url").item(0).getTextContent();
                String mimeType = element.getElementsByTagName("mimetype").item(0).getTextContent();
                String httpResponseCode = element.getElementsByTagName("httpresponsecode").item(0).getTextContent();
                WaybackResource resource = new WaybackResource(resourceUrl, cal, mimeType, httpResponseCode);
                result.add(resource);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
