package cz.webarchiv.wayback;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author Vašek
 */
public class PageLinkChecker {

    protected FirefoxDriver driver = new FirefoxDriver();
    protected Page page;
    protected Wayback wayback;
    protected WaybackSearcher searcher;
    protected Set<String> visited = new HashSet<String>();
    protected Stack<String> waitList = new Stack<String>();

    public PageLinkChecker(Wayback wayback, Page page) {
        this.page = page;
        this.wayback = wayback;
        this.searcher = new WaybackSearcher(wayback);
    }

    public void check() {
        waitList.add(wayback.getURL(page));
        while (!waitList.empty()) {
            String url = waitList.pop();
            // System.err.println("visiting:" + url);
            driver.get(url);
            List<WebElement> elements = driver.findElementsByTagName("a");
            for (WebElement element : elements) {
                String src = element.getAttribute("href");
                // System.err.println("consider:" + src);
                if (visit(src)) {
                    String tempUrl = wayback.extractPage(src).getUrl();
                    if (!visited.contains(tempUrl)) {
                        waitList.add(src);
                        visited.add(tempUrl);
                    }
                }
            }
        }
        driver.close();
    }

    public boolean visit(String url) {
        if (url == null) {
            return false;
        }
        if (!url.startsWith(wayback.getURL())) {
            System.err.println("Escape to live web:" + url);
            return false;
        }
        try {
            URL srcURL = new URL(page.getUrl());
            URL destURL = new URL(wayback.extractPage(url).getUrl());
            if (srcURL.getHost().equals(destURL.getHost())) {
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        /*
        String url = "http://har.webarchiv.cz:8080/AP1/20100224152922/http://www.ping-pong.cz/cast.php?sekce=listy";
        Wayback wayback = new Wayback();
        Page page = wayback.extractPage(url);
        System.out.println(page.getDate()+"/"+page.getUrl());
         */
        Page page = new Page();
        page.setUrl("http://sysifos.cz");
        page.setDate("20100224155405");
        PageLinkChecker checker = new PageLinkChecker(new Wayback(), page);
        checker.check();
    }
}
