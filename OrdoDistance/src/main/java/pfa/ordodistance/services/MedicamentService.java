package pfa.ordodistance.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventDispatcher;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import pfa.ordodistance.models.Medicament;
import pfa.ordodistance.models.Ordonnance;
import pfa.ordodistance.repositories.MedicamentRepository;

@Service
public class MedicamentService {
	
	@Autowired(required = true)
	private MedicamentRepository medicamentRepository;

	@Autowired
	private OrdonnanceService ordonnanceService;
	
	public String[] sarr;
	public String[] sarrFront;
	public Document document;
	
    //Save new ordonnance
	public void save(Medicament m) {
		medicamentRepository.save(m);
	}

	public void delete(int id){
		medicamentRepository.deleteById(id);
	}
	
	public 	boolean createPdf(List<Medicament> Medicaments, ServletContext context,int ido, HttpServletRequest request,
			HttpServletResponse response) {
		
		document= new Document();
		try {
			 PdfWriter.getInstance(document, new FileOutputStream("./src/main/resources/static/Ordonnance-"+ido+".pdf"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.open();
		Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
		PdfPTable table = new PdfPTable(1);
		PdfPTable table2 = new PdfPTable(2);
		try {
			table2.setWidths(new float[] { 1, 5 });
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		PdfPTable table3 = new PdfPTable(1);
		addTableHeader(table);
		addRows(table,Medicaments.get(0),ido);
		addRows2(table2,Medicaments);
		addRows3(table3);
		try {
			document.add(table);
			document.add(table2);
			document.add(table3);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		document.close();
		File file = new File("./src/main/resources/static/Ordonnance-"+ido+".pdf");
		String extension= "png";
		try {
			PDDocument doc = PDDocument.load(file);
			 PDFRenderer pdfRenderer = new PDFRenderer(doc);
			   for (int page = 0; page < doc.getNumberOfPages(); ++page) {
			        BufferedImage bim = pdfRenderer.renderImageWithDPI(
			          page, 300, ImageType.RGB);
			        ImageIOUtil.writeImage(
			          bim, String.format("./src/main/resources/static/ordonnance-%d.%s", ido, extension), 300);
			    }
		doc.close();
		/**********API - Config********/ 
		   HttpPost post = new HttpPost("https://api.imgbb.com/1/upload?key=73d866a57b3067c76e15cb6ceb40b60a");
		   /*The image to upload */
		   File file1 = new File("./src/main/resources/static/Ordonnance-"+ido+".png");
		   try (FileInputStream imageInFile = new FileInputStream(file1)) {
			   
		     // Reading a Image file from file system
		     byte imageData[] = new byte[(int) file1.length()];
		     imageInFile.read(imageData);
		     String base64Image = Base64.getEncoder().encodeToString(imageData);
	         List<NameValuePair> urlParameters = new ArrayList<>();
	         urlParameters.add(new BasicNameValuePair("image", base64Image));
	         post.setEntity(new UrlEncodedFormEntity(urlParameters));
	         try (CloseableHttpClient httpClient1 = HttpClients.createDefault();
	             CloseableHttpResponse response1 = httpClient1.execute(post)) {
	        	 String s=EntityUtils.toString(response1.getEntity());
	             //To get the image address :
	             sarr=s.split(",")[2].split("\"");
	             //To get the front image :
				 sarrFront=s.split(",")[3].split("\"");
				 }
			   } catch (FileNotFoundException e) {
			     System.out.println("Image not found" + e);
			   } catch (IOException ioe) {
			     System.out.println("Exception while reading the Image " + ioe);
			   }
		/********End of the API config*********/	
		} catch (InvalidPasswordException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	   return true;
	}

	private void addRows(PdfPTable table, Medicament M, int ido) {
		// TODO Auto-generated method stub
		PdfPCell cellv= new PdfPCell(new Paragraph(
		"Veuillez présenter ce document à votre pharmacien pour scanner "
				+ "le QR-code et vous délivrer les médicaments prescrits "));
        cellv.setFixedHeight(32);
        cellv.setPaddingLeft(11);
		table.addCell(cellv);
		PdfPCell cell = new PdfPCell(new Paragraph("Docteur:                    "+
				ordonnanceService.getOrdonnance(ido).getUser().getLastname()+"  "+M.getIdO().getUser().getFirstname()+
				
				    "                                                                              "
				    + "PPR:                          "
             +  ordonnanceService.getOrdonnance(ido).getUser().getPpt()  ))	;
		cell.setColspan(4);
		cell.setHorizontalAlignment(Element.ALIGN_BOTTOM);
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		cell.setFixedHeight(35);
		cell.setPaddingLeft(15);
		table.addCell(cell);
		PdfPCell cell2 = new PdfPCell(new Paragraph(
		
		"Patient:                     "+ ordonnanceService.getOrdonnance(ido).getPatient().getNom()+" "+M.getIdO().getPatient().getPrenom()
			    + "                                                                                                    "
			    + "Date de naissance:"+ordonnanceService.getOrdonnance(ido).getPatient().getDateNaissance()
				));
		
		cell2.setPaddingTop(6);
		cell2.setPaddingLeft(15);
		cell2.setPaddingBottom(9);
		table.addCell(cell2);
		PdfPCell cell3 = new PdfPCell(new Paragraph(
		"                             Contenu de la prescription électronique     "));
		cell3.setFixedHeight(20);
		table.addCell(cell3);
	}
	
	private void addRows2(PdfPTable table,List<Medicament> Medicaments) {
	    for( Medicament M : Medicaments) {
	    	final String Separateur=",";
	    	String Noms[]=M.getNom().split(Separateur);
	    	String Nbr []=M.getNbrefois().split(Separateur);
	    	String Pend []=M.getPendant().split(Separateur); 
	    	for(int i=0;i<Noms.length;i++) {
	    		PdfPCell cell=new PdfPCell(new Paragraph(String.format("%d", i+1)));	
	    		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				cell.setFixedHeight(20);
				table.addCell(cell);
				PdfPCell cell1 = new PdfPCell(new Paragraph(
            	  Noms[i]+"                                                                                               "
  						+" - Nbr de fois par Jour : "+ Nbr[i]+"                                                                  "
  						+" - Pendant : " +Pend[i]));
				cell1.setFixedHeight(45);
				cell1.setPaddingLeft(15);
				table.addCell(cell1);
	    	}
	    }
	}
	
	private void addRows3(PdfPTable table) {
    	 PdfPCell cell=new PdfPCell();
    	 cell.setFixedHeight(50);
    	 table.addCell(cell);
    	 DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    	 Date date = new Date();
    	 System.out.println(format.format(date));
    	 Paragraph p= new Paragraph("Attention : Aucun ajout manuscrit à ce document ne sera pris en compte");
    	 p.setAlignment(Element.ALIGN_CENTER);
    	 PdfPCell cell1 = new PdfPCell(p);
    	 cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
    	 cell1.setFixedHeight(20);
    	 table.addCell(cell1);
    	 PdfPCell cell2 = new PdfPCell(new Paragraph("Date:"+format.format(date)));
    	 cell2.setFixedHeight(20);
    	 cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
    	 table.addCell(cell2);
	}
	
	private void addTableHeader(PdfPTable table) {
		  Stream.of("                     PREUVE DE PRESCRIPTION ELECTRONIQUE ")
	      .forEach(columnTitle -> {
	        PdfPCell header = new PdfPCell();
	        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
	        header.setBorderWidth(1);
	        header.setPhrase(new Phrase(columnTitle));
	        
	        header.setFixedHeight(20);
	        table.addCell(header);
	    });
	}
}
