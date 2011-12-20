package cz.webarchiv.WadminKonspekt.oai;

import ORG.oclc.oai.harvester2.verb.ListRecords;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

/**
 *
 * @author xrosecky
 */
public class OAIHarvester {

    protected String OAIProviderURL = "http://aleph.nkp.cz/OAI";
    protected String base = "WEBS";
    protected String metadataFormat = "marc21";

    public OAIHarvester(String provider, String base, String metadataFormat) {
        this.OAIProviderURL = provider;
        this.base = base;
        this.metadataFormat = metadataFormat;
    }

    static class MyNamespaceContext implements NamespaceContext {

        @Override
        public String getNamespaceURI(String arg0) {
            System.err.println("prefix:" + arg0);
            if (arg0.equals("marc")) {
                return "http://www.loc.gov/MARC21/slim";
            } else {
                return "http://www.openarchives.org/OAI/2.0/";
            }
        }

        @Override
        public String getPrefix(String arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Iterator getPrefixes(String arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public List<OAIRecord> getResources() throws Exception {
        List<OAIRecord> result = new ArrayList<OAIRecord>();
        XPath xpath = XPathFactory.newInstance().newXPath();
        ListRecords listRecords = new ListRecords(OAIProviderURL, null, null, base, metadataFormat);
        while (listRecords != null) {
            Document document = listRecords.getDocument();
            NodeList nodes = (NodeList) xpath.evaluate("/OAI-PMH/ListRecords/record",
                    document, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                Document doc = null;
                try {
                    doc = clone(nodes.item(i).cloneNode(true));
                    String alephId = xpath.evaluate("//identifier/text()", doc);
                    alephId = alephId.substring(alephId.lastIndexOf("-") + 1).trim();
                    String title = xpath.evaluate("//datafield[@tag='245']/subfield[@code='a']/text()", doc).trim();
                    String conspectusId = xpath.evaluate("//datafield[@tag='072' and @ind2='7']/subfield[@code='9']/text()", doc).trim();
                    String conspectusSubcategoryId = xpath.evaluate("//datafield[@tag='072' and @ind2='7']/subfield[@code='a']/text()", doc).trim();
                    String publisher = xpath.evaluate("//datafield[@tag='260']/subfield[@code='b']/text()", doc).trim();
                    String description = xpath.evaluate("//datafield[@tag='520']/subfield[@code='a']/text()", doc).trim();
                    String url = xpath.evaluate("//datafield[@tag='856']/subfield[@code='u' and not(contains(text(),'URN:'))]/text()", doc);
                    String cnb = xpath.evaluate("//datafield[@tag='015']/subfield[@code='a']/text()", doc);
                    OAIRecord resource = new OAIRecord();
                    resource.setId(alephId);
                    resource.setTitle(title);
                    if (conspectusId.equals("")) {
                        System.err.println("conspectusId is null for:" + alephId + " title:" + title);
                    } else {
                        resource.setConspectusId(Integer.parseInt(conspectusId));
                    }
                    resource.setConspectusSubcategoryId(conspectusSubcategoryId);
                    resource.setPublisher(publisher);
                    resource.setUrl(url);
                    resource.setDescription(description);
                    resource.setCNB(cnb);
                    List<String> keywords = new ArrayList<String>();
                    NodeList keys1 = (NodeList) xpath.evaluate("//datafield[@tag='650' and @ind2='7']/subfield[@code='a']/text()", doc, XPathConstants.NODESET);
                    add(keys1, keywords, false);
                    // NodeList keys2 = (NodeList) xpath.evaluate("//datafield[@tag='610' and @ind2='7']/subfield[@code='a' or @code='b']/text()", doc, XPathConstants.NODESET);
                    // NodeList keys2 = (NodeList) xpath.evaluate("//datafield[@tag='610' and @ind2='7' and subfield/@code='a' and subfield/@code='b']/concat(subfield/@code='a', ' . ', subfield/@code='b')", doc, XPathConstants.NODESET);
                    // add(keys2, keywords, true);
                    NodeList keys2 = (NodeList) xpath.evaluate("//datafield[@tag='610' and @ind2='7']", doc, XPathConstants.NODESET);
                    for (int j1 = 0, n1 = keys2.getLength(); j1 < n1; j1++) {
                        Element elm1 = (Element) keys2.item(j1);
                        // System.out.println(dump(elm1));
                        Document foo = clone(elm1.cloneNode(true));
                        String a = xpath.evaluate("//subfield[@code='a']/text()", foo);
                        String b = xpath.evaluate("//subfield[@code='b']/text()", foo);
                        if (notEmptyOrNull(a) && notEmptyOrNull(b)) {
                            String concated = fixValue(a) + ". " + fixValue(b);
                            // System.err.println(concated);
                            keywords.add(concated);
                        } else {
                            if (notEmptyOrNull(a)) {
                                keywords.add(fixValue(a));
                            }
                            if (notEmptyOrNull(b)) {
                                keywords.add(fixValue(b));
                            }
                        }
                    }
                    NodeList keys3 = (NodeList) xpath.evaluate("//datafield[@tag='600' and @ind2='7']/subfield[@code='a']/text()", doc, XPathConstants.NODESET);
                    add(keys3, keywords, true);
                    NodeList keys4 = (NodeList) xpath.evaluate("//datafield[@tag='651' and @ind2='7']/subfield[@code='a']/text()", doc, XPathConstants.NODESET);
                    add(keys4, keywords, false);
                    resource.setKeywords(keywords);
                    result.add(resource);
                    doc = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (doc != null) {
                        dump(doc);
                    }
                }
            }
            String resumptionToken = listRecords.getResumptionToken();
            if (resumptionToken == null || resumptionToken.length() == 0) {
                listRecords = null;
            } else {
                listRecords = new ListRecords(OAIProviderURL, resumptionToken);
            }
        }
        return result;
    }

    private boolean notEmptyOrNull(String str) {
        return (str != null && !str.trim().equals(""));
    }

    private void add(NodeList keys, List<String> keywords, boolean fix) {
        for (int j = 0, n = keys.getLength(); j < n; j++) {
            Node node = keys.item(j);
            String value = node.getTextContent();
            if (fix) {
                value = fixValue(value);
            }
            keywords.add(value);
        }
    }

    private String fixValue(String fixMe) {
        fixMe = fixMe.trim();
        StringBuilder publisher = new StringBuilder(fixMe);
        if (publisher != null) {
            while ((publisher.charAt(publisher.length() - 1) == ',')
                    || (publisher.charAt(publisher.length() - 1) == ']')
                    || (publisher.charAt(publisher.length() - 1) == ';')
                    || (publisher.charAt(publisher.length() - 1) == '.')) {
                publisher.deleteCharAt(publisher.length() - 1);
            }
            while ((publisher.charAt(0) == ',')
                    || (publisher.charAt(0) == ']')
                    || (publisher.charAt(0) == ';')) {
                publisher.deleteCharAt(0);
            }
        }
        return publisher.toString();
    }

    static String dump(Document doc) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
        String xmlString = result.getWriter().toString();
        return xmlString;
    }

    static String dump(Node node) throws Exception {
        Document document = node.getOwnerDocument();
        DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
        LSSerializer serializer = domImplLS.createLSSerializer();
        return serializer.writeToString(node);
    }

    /*
    public void resolve(List<Resource> resources) throws Exception {
    WAdmin wadmin = new WAdmin();
    Map<String, Resource> urls = new HashMap<String, Resource>();
    XPath xpath = XPathFactory.newInstance().newXPath();
    for (Resource res : resources) {
    urls.put(normalize(res.getUrl()), res);
    }
    // xpath.setNamespaceContext(new MyNamespaceContext());
    ListRecords listRecords = new ListRecords("http://sigma.nkp.cz/OAI", null, null, "WEBS", "marc21");
    while (listRecords != null) {
    Document document = listRecords.getDocument();
    NodeList nodes = (NodeList) xpath.evaluate("/OAI-PMH/ListRecords/record",
    document, XPathConstants.NODESET);
    for (int i = 0; i < nodes.getLength(); i++) {
    Document doc = clone(nodes.item(i).cloneNode(true));
    // String alephId = xpath.evaluate("//identifier", doc);
    // String alephId = xpath.evaluate("//controlfield[@tag='001']/text()", doc);
    // alephId = alephId.substring(4);
    String alephId = xpath.evaluate("//identifier/text()", doc);
    alephId = alephId.substring(alephId.lastIndexOf("-") + 1).trim();
    String title = xpath.evaluate("//datafield[@tag='245']/subfield[@code='a']/text()", doc);
    String conspectusId = xpath.evaluate("//datafield[@tag='072']/subfield[@code='a']/text()", doc);
    title = title.trim();
    String originalUrl = xpath.evaluate("//datafield[@tag='856']/subfield[@code='u' and not(contains(text(),'URN:'))]/text()", doc);
    String url = normalize(originalUrl);
    Resource res = urls.get(url);
    if (res == null) {
    for (Resource resource : resources) {
    if (resource.getTitle() == null) {
    break;
    }
    String title1 = resource.getTitle().toLowerCase().trim();
    String title2 = title.toLowerCase().trim();
    if (title1.contains(title2) || title2.contains(title1)) {
    res = resource;
    break;
    }
    }
    }
    if (res != null) {
    // System.out.println(res.getId() + " " + alephId + " " + res.getUrl() + " " + originalUrl);
    // + " " + res.getTitle() + " " + title);
    ConspectusSubcategory conspectus = wadmin.getConspectus(conspectusId);
    if (conspectus != null) {
    System.out.println(res.getId() + " --> " + conspectus.getSubCategoryId());
    } else {
    System.out.println(res.getId() + " --> " + conspectusId + "(not found)");
    }
    if (res.getAlephId() == null) {
    // System.out.println(res.getId() + "=>" + alephId);
    // res.setAlephId(alephId);
    // wadmin.updateResource(res);
    // System.exit(1);
    }
    if (res.getAlephId() != null && !res.getAlephId().trim().equals(alephId)) {
    System.err.println(res.getId() + " " + res.getAlephId() + " " + alephId + " " + originalUrl);
    }
    } else {
    System.err.println(alephId + " " + originalUrl + " not found.");
    }
    // urls.put(normalize(url), id);
    }
    String resumptionToken = listRecords.getResumptionToken();
    if (resumptionToken == null || resumptionToken.length() == 0) {
    listRecords = null;
    } else {
    listRecords = new ListRecords("http://sigma.nkp.cz/OAI", resumptionToken);
    }
    }
    for (Resource res : resources) {
    String url = normalize(res.getUrl());
    String alephId = urls.get(url);
    if (alephId == null) {
    System.err.println(res.getId() + "(" + url + ")" + " not found in aleph");
    } else {
    System.out.println(res.getId() + " " + alephId);
    }
    }
    }
     */
    public String normalize(String url) {
        String result = url.trim();
        if (result.endsWith("index.html")) {
            result = result.replace("index.html", "");
        }
        if (result.endsWith("index.htm")) {
            result = result.replace("index.htm", "");
        }
        if (result.endsWith("index.php")) {
            result = result.replace("index.php", "");
        }
        if (!result.endsWith("/")) {
            result += "/";
        }
        if (result.contains("www.")) {
            result = result.replace("www.", "");
        }
        if (!result.startsWith("http://")) {
            result = "http://" + result;
        }
        // System.err.println(url + "=>" + result);
        return result.trim();
    }

    public Document clone(Node doc) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
        String xmlString = result.getWriter().toString();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlString)));
    }
}
