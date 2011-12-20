package cz.webarchiv.WadminKonspekt;

import cz.webarchiv.WadminKonspekt.oai.OAIHarvester;
import cz.webarchiv.WadminKonspekt.oai.OAIRecord;
import cz.webarchiv.WadminKonspekt.wadmin.Resource;
import cz.webarchiv.WadminKonspekt.wadmin.WAdmin;
import cz.webarchiv.WadminKonspekt.wayback.Drawer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Vaclav Rosecky
 */
public class Main {

    /*
    public static void main(String args[]) throws Exception {
    int[] array = new int[]{1, 5, 5, 3, 2, 1, 4, 6, 2, 2};
    Drawer.draw(array, new File("output.png"));
    }
     */
    public static void main(String args[]) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage java -jar WadminKonspekt.jar file_with_properties");
            System.exit(1);
        }
        Properties properties = null;
        String file = args[0];
        try {
            properties = loadProperties(new File(file));
        } catch (IOException ioe) {
            System.err.format("Error reading properties from file %s.", file);
            System.exit(2);
        }
        Map<String, String> subCategoriesMapping = Helper.loadSubCategories();
        String OAIProvider = properties.getProperty("oai.provider");
        String OAIBase = properties.getProperty("oai.base");
        String OAIMetadataFormat = properties.getProperty("oai.metadataformat");
        OAIHarvester harvester = new OAIHarvester(OAIProvider, OAIBase, OAIMetadataFormat);
        String dbURL = properties.getProperty("wadmin.db.url");
        String dbUser = properties.getProperty("wadmin.db.user");
        String dbPassword = properties.getProperty("wadmin.db.password");
        WAdmin wadmin = new WAdmin(dbURL, dbUser, dbPassword);
        List<String> hosts = new ArrayList<String>();

        // vygenerovani seznamu povolenych hostname pro wayback
        try {
            File contractFile = new File(properties.getProperty("contract.file"));
            PrintStream cs = new PrintStream(new FileOutputStream(contractFile));
            for (Resource res : wadmin.getContractedResources()) {
                try {
                    hosts.addAll(getHosts(new URL(res.getUrl())));
                } catch (MalformedURLException me) {
                    System.err.format("WARN: resource with id=%s has invalid url:", res.getId(), res.getUrl());
                }
            }
            for (String host : hosts) {
                cs.println(host);
            }
        } catch (IOException ioe) {
            System.err.println("ERROR: error occured when generating contract file");
            System.exit(3);
        }

        // vygenerovani seznamu URL
        try {
            PrintStream cs = new PrintStream(new FileOutputStream(new File(properties.getProperty("urls.file"))));
            for (Resource res : wadmin.getContractedResources()) {
                if (res.getAlephId() != null) {
                   cs.println(res.getId() + " " + res.getAlephId() + " " +res.getUrl());
                }
            }
            cs.close();
        } catch (IOException ioe) {
            System.err.println("ERROR: error occured when generating contract file");
            System.exit(3);
        }

        // System.exit(3);

        File dumpFile = new File(properties.getProperty("dump.file"));
        PrintStream ds = new PrintStream(new FileOutputStream(dumpFile));
        List<OAIRecord> records = harvester.getResources();
        // Drawer drawer = new Drawer();
        for (OAIRecord record : records) {
            System.err.println("Sysno:"+record.getId());
            Resource resource = wadmin.getResourceByAlephId(record.getId());
            if (resource == null) {
                System.err.println("Resource with sysno=" + record.getId() + " does not exist in WA Admin.");
                continue;
            }
            resource.setDescription(record.getDescription());
            resource.setConspectusId(record.getConspectusId());
	    // System.out.println(record.getConspectusId());
            String subcategory = record.getConspectusSubcategoryId();
            if (subCategoriesMapping.containsKey(subcategory.trim())) {
                subcategory = subCategoriesMapping.get(subcategory.trim());
            }
            int subcategoryId = wadmin.getConspectusSubcategoryId(subcategory);
            if (subcategoryId == -1) {
                System.err.println("Subcategory " + subcategory + " does not exists in WA Admin. Resource has aleph no " + record.getId() + ".");
            } else {
                resource.setConspectusSubcategoryId(subcategoryId);
                try {
                    wadmin.updateResource(resource, record.getKeywords());
                } catch (Exception ex) {
                    System.err.println("Error when processing record"+record.getId());
                }
            }
            // drawer.draw(resource.getUrl(), new File("graphs/stat_" + resource.getId() + ".png"));
            // System.out.format("%s\t%s\t%s\n", resource.getId(), record.getCNB(), resource.getUrl());
        }
    }
    /*
    List<OAIRecord> records = harvester.getResources();
    for (OAIRecord record : records) {
    Resource resource = wadmin.getResourceByAlephId(record.getId());
    try {
    URL url = new URL(resource.getUrl());
    String host = url.getHost();
    System.out.println(host);
    if (host.contains("www")) {
    System.out.println(host.replace("www.", ""));
    } else {
    System.out.println("www"+"."+host);
    }
    } catch (Exception ex) {
    ex.printStackTrace();
    }
    }
     */
    private static String PREFIX_WWW = "www.";

    private static List<String> getHosts(URL url) {
        List<String> hosts = new ArrayList<String>();
        String host = url.getHost();
        hosts.add(host);
        if (host.startsWith(PREFIX_WWW)) {
            hosts.add(host.substring(4));
        } else {
            hosts.add(PREFIX_WWW + host);
        }
        return hosts;
    }

    public static Properties loadProperties(File file) throws IOException {
        Properties properties = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            properties.load(fis);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return properties;
    }

    public static void tmain(String args[]) throws Exception {
        Drawer drawer = new Drawer();
        Map<String, String> subCategoriesMapping = Helper.loadSubCategories();
        OAIHarvester harvester = null; // new OAIHarvester();
        WAdmin wadmin = new WAdmin();
        List<OAIRecord> records = harvester.getResources();
        for (OAIRecord record : records) {
            Resource resource = wadmin.getResourceByAlephId(record.getId());
            if (resource == null) {
                System.err.println("Resource with aleph no =" + record.getId() + " and title='" + record.getTitle() + "' does not exist");
                continue;
            }
            resource.setDescription(record.getDescription());
            resource.setConspectusId(record.getConspectusId());
            String subcategory = record.getConspectusSubcategoryId();
            if (subCategoriesMapping.containsKey(subcategory.trim())) {
                subcategory = subCategoriesMapping.get(subcategory.trim());
            }
            int subcategoryId = wadmin.getConspectusSubcategoryId(subcategory);
            if (subcategoryId == -1) {
                System.err.println("Subcategory " + subcategory + " does not exists in WA Admin. Resource has aleph no " + record.getId() + ".");
            } else {
                resource.setConspectusSubcategoryId(subcategoryId);
                wadmin.updateResource(resource, record.getKeywords());
            }
            drawer.draw(resource.getUrl(), new File("graphs/stat_" + resource.getId() + ".png"));
            System.out.format("%s\t%s\t%s\n", resource.getId(), record.getCNB(), resource.getUrl());
        }
    }
    /*
    WAdmin admin = new WAdmin();
    List<Resource> resources = admin.getResources();
    AlephConnector conn = new AlephConnector();
    conn.resolve(resources);
     */
    /*
    OAIHarvester alephCon = new OAIHarvester();
    WAdmin wadmin = new WAdmin();
    Set<String> notFound = new HashSet<String>();

    List<OAIRecord> records = alephCon.getResources();
    for (OAIRecord record : records) {
    try {
    for (String word : record.getKeywords()) {
    int id = wadmin.getKeywordIdAddIfNotExists(word);
    Resource res = wadmin.getResourceByAlephId(record.getId());
    try {
    wadmin.getConspectusSubcategoryId(record.getConspectusSubcategoryId());
    } catch (Exception ex) {
    notFound.add(record.getConspectusSubcategoryId());
    // System.err.println(record.getConspectusSubcategoryId());
    }
    // res.setDescription(record.getDescription());
    // res.setConspectusId(record.getConspectusId());
    // wadmin.updateResource(res);
    // wadmin.addKeyword(res, word);
    }
    } catch (Exception ex) {
    ex.printStackTrace();
    }
    System.out.println("================================");
    for (String str : notFound) {
    System.out.println(str);
    }
    System.out.println("================================");
     */
    /*
    try {
    Resource resource = wadmin.getResourceByAlephId(record.getId());
    int subcategoryId = wadmin.getConspectusSubcategoryId(record.getConspectusSubcategoryId());
    if (resource.getConspectusSubcategoryId() == 0) {
    System.out.format("%s -> %s\n", record.getId(), subcategoryId);
    resource.setConspectusSubcategoryId(subcategoryId);
    wadmin.updateResource(resource);
    }
    } catch (Exception ex) {
    }
     */
    // System.out.format("%s has id=%s(MDT is %s)\n", resource.getId(), subcategoryId, record.getConspectusSubcategoryId());
    // }
    // }
}
