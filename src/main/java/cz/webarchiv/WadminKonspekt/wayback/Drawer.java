package cz.webarchiv.WadminKonspekt.wayback;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @author xrosecky
 */
public class Drawer {

    WaybackConnector conn = new WaybackConnector("http://war.webarchiv.cz/wayback/");

    public void draw(String url, File output) throws Exception {
        List<WaybackResource> result = conn.query(url);
        int[] stat = new int[11];
        for (WaybackResource res : result) {
            stat[res.getDate().get(Calendar.YEAR) - 2001]++;
        }
        draw(stat, output);
    }

    /*
    public static void draw(int[] array, File output) throws Exception {
        String[] xAxisLabels = {"1998", "1999", "2000", "2001", "2002", "2003", "2004"};
        String xAxisTitle = " ";
        String yAxisTitle = " ";
        String title = " ";
        DataSeries dataSeries = new DataSeries(xAxisLabels, xAxisTitle, yAxisTitle, title);
        double[][] data = new double[][]{{250, 45, 0, 66, 145, 80, 55}};
        // String[] legendLabels = {"Bugs"};
        String[] legendLabels = null;
        Paint[] paints = new Paint[]{Color.blue.darker()};
        BarChartProperties barChartProperties = new BarChartProperties();
        AxisChartDataSet axisChartDataSet = new AxisChartDataSet(data, legendLabels, paints, ChartType.BAR, barChartProperties);
        dataSeries.addIAxisPlotDataSet(axisChartDataSet);
        ChartProperties chartProperties = new ChartProperties();
        AxisProperties axisProperties = new AxisProperties();
        LegendProperties legendProperties = new LegendProperties();
        AxisChart axisChart = new AxisChart(dataSeries, chartProperties, axisProperties, legendProperties, 640, 480);
        axisChart.renderWithImageMap();
        BufferedImage bimage = axisChart.getBufferedImage();
        ImageIO.write(bimage, "png", output);
    }
    */

    public static void draw(int[] array, File output) throws IOException {
        int width = 120; // 30
        int height = 80; // 30
        int barWidth = 6; // 3
        int shiftWidth = 12;
        int barHeight = 8; // 3
        int lineSize = 2;
        int rectSize = 20;
        BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); //vytvoreni obrazku s danou sirkou a vyskou
        Graphics g = bimage.getGraphics();
        Graphics2D g2d = bimage.createGraphics();
        g2d.setColor(Color.WHITE); //nastaveni barvy kresleni
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        /*
        g2d.fillRect(0, height - barHeight, width, axesXHeight);
        g2d.fillRect(0, 0, axesXHeight, height - barHeight);
        */
        g2d.fillRect(0, height - lineSize, width, lineSize);
        g2d.fillRect(0, 0, lineSize, height);
        g2d.setColor(Color.GRAY);
        int x = 0;
        int y = 0;
        while (x < width) {
            while (y < height) {
                g2d.drawRect(x, y, rectSize, rectSize);
                y+= rectSize;
            }
            y = 0;
            x += rectSize;
        }
        g2d.fillRect(width - 1, 0, 1, height);
        // g2d.fillRect(0, height - barHeight, width, axesXHeight); //
        g2d.setColor(Color.GREEN);
        int index = lineSize * 2;
        for (int count : array) {
            // g2d.fillRect(index, (height - barHeight - count * barHeight), barWidth, count * barHeight);
            g2d.fillRect(index, (height - lineSize - count * barHeight), barWidth, count * barHeight);
            index += shiftWidth;
        }
        ImageIO.write(bimage, "png", output);
    }
}
