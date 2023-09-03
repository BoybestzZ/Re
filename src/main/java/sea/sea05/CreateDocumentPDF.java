/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea05;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 75496
 */
public class CreateDocumentPDF {
    public static void main(String[] args) {
        
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document,
                    new FileOutputStream("C:\\Users\\75496\\Documents\\pdf/CreateDocumentPDF.pdf"));
            document.open(); 
            document.add(new Paragraph("SVG Example"));
            
            
            
            
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(CreateDocumentPDF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CreateDocumentPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
}
