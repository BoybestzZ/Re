/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea05;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.itext.ChartWriterToPDF;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;

/**
 *
 * @author 75496
 */
public class CreateChartPDF {
    
    
    public static void main(String[] args) {
        
        try {
            
System.out.println(            PageSize.A4); // 595 x 842 
            String pdffile="C:\\Users\\75496\\Documents\\pdf/CreatePDF.pdf";
            ChartWriterToPDF writer=new ChartWriterToPDF(pdffile);
            XYPlot plot=new XYPlot();
            
            
            
            
            JFreeChart chart=new JFreeChart(plot);

            writer.add(chart, new Rectangle2D.Double(0, 0, 100, 100));
            writer.add(chart, new Rectangle2D.Double(100, 100, 100, 100));
            writer.add(chart, new Rectangle2D.Double(0, 300, 100, 100));
            writer.add(chart, new Rectangle2D.Double(100, 300, 100, 100));

            writer.newpage();
            writer.add(chart, new Rectangle2D.Double(50, 50, 120, 80));
            
            writer.finish();
            
            
            
            
            

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CreateChartPDF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(CreateChartPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
}
