package cz.webarchiv.WadminKonspekt;

import cz.webarchiv.WadminKonspekt.oai.OAIRecord;
import cz.webarchiv.wayback.Page;
import cz.webarchiv.wayback.WaybackSearcher;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;

/**
 *
 * @author xrosecky
 */
public class WaybackChecker {

    public static void main(String[] args) throws Exception {
        /*
        OAIHarvester harvester = new OAIHarvester();
        List<OAIRecord> records = harvester.getResources();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("foo.data"));
        oos.writeObject(records);
        oos.close();
         */
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("foo.data"));
        List<OAIRecord> records = (List<OAIRecord>) ois.readObject();
        WaybackSearcher ws = new WaybackSearcher();
        for (OAIRecord record : records) {
            List<Page> pages = ws.search(record.getUrl());
            // System.out.println("checking:"+record.getUrl());
            if (pages.size() == 0) {
                System.err.println(record.getUrl()+" does not exist.");
            }
        }
    }

}
