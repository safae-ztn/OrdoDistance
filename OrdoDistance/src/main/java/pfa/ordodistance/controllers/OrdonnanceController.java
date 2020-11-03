package pfa.ordodistance.controllers;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import pfa.ordodistance.models.Medicament;
import pfa.ordodistance.models.Ordonnance;
import pfa.ordodistance.models.Patient;
import pfa.ordodistance.models.UserPrincipal;
import pfa.ordodistance.repositories.MedicamentRepository;
import pfa.ordodistance.repositories.PatientRepository;
import pfa.ordodistance.services.MedicamentService;
import pfa.ordodistance.services.OrdonnanceService;
import pfa.ordodistance.services.SendingMailService;

@Controller
@ComponentScan("pfa.ordodistance")
public class OrdonnanceController {
	
	int Reqid;
	int idPatient;
	Ordonnance clonedOrdo;
	Patient Pa ;
	@Autowired
	private OrdonnanceService ordonnanceService;
	@Autowired
	private MedicamentService medicamentService;
	@Autowired
	private ResourceLoader resourceLoader;
	
	@Autowired
	private MedicamentRepository medicaR;
	
	@Autowired(required = true)
	private PatientRepository patientRepository;
	
	@Autowired
	private ServletContext context;
	
	@Autowired 
	private SendingMailService sendingMailService;
	
	@GetMapping("/ordonnances")
	public String getOrdonnancesByUser(Model model,String keyword) {	
		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(keyword!=null) {
			if(keyword.equalsIgnoreCase("archive")){
				model.addAttribute("ordonnances",ordonnanceService.getOrdonnancesByUserAndIsArchived(userPrincipal.getUser(),true));
			}
			else{
				if (keyword.equalsIgnoreCase("envoye")||keyword.equalsIgnoreCase("envoyé")){
					model.addAttribute("ordonnances",ordonnanceService.getOrdonnancesByUserAndIsSent(userPrincipal.getUser(),true));
				}
				else{
					if(keyword.equalsIgnoreCase("en attente")){
						model.addAttribute("ordonnances",ordonnanceService.getOrdonnancesByUserAndIsSent(userPrincipal.getUser(),false));

					}else{
						model.addAttribute("ordonnances",ordonnanceService.getOrdonnancesByUserAndKeyword(userPrincipal.getUser(),keyword));

					}
				}
			}
		}
		else {
					model.addAttribute("ordonnances", ordonnanceService.getOrdonnancesByUserAndIsArchived(userPrincipal.getUser(),false));
		}
		return "ordonnances";
	}
	
	@GetMapping("/patients")
	public String getPatientsByUser(Model model,String keyword) {	
		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(keyword!=null) {
			model.addAttribute("patients",ordonnanceService.getPatientsByUserAndKeyword(userPrincipal.getUser(),keyword));
		}
		else {
			model.addAttribute("patients", ordonnanceService.getPatientsByUser(userPrincipal.getUser()));	
		}
		return "patients";
	}
	
	@GetMapping("/ordonnances/{id}")
	public String getOrdonnance(@PathVariable int id, Model model) {
		model.addAttribute("ordonnance", ordonnanceService.getOrdonnance(id));
		return "ordonnanceDetails";
	}

	@GetMapping("/ordonnances/edit/{id}")
	public String getEditOrdonnance(@PathVariable int id, Model model) {
		model.addAttribute("ordonnance", ordonnanceService.getOrdonnance(id));
		return "ordonnanceEdit";
	}

	@GetMapping("/ordonnances/archive/{id}")
	public String archiveOrdonnance(@PathVariable int id, Model model) {
		Ordonnance ordoArchived=ordonnanceService.getOrdonnance(id);
		ordoArchived.setArchived(true);
		ordonnanceService.save(ordoArchived);
		return "redirect:/ordonnances";
	}

	@GetMapping("/patients/archive/{id}")
	public String archivePatient(@PathVariable int id, Model model) {
		Patient patientArchived=patientRepository.findById(id).get();
		patientArchived.setArchived(true);
		patientRepository.save(patientArchived);
		return "redirect:/patients";
	}

	@GetMapping("/ordonnances/clone/{id}")
	public String getCloneOrdonnance(@PathVariable int id, Model model) {
		clonedOrdo=ordonnanceService.getOrdonnance(id);
		model.addAttribute("ordonnance",clonedOrdo);
		return "PatientPageClone";
	}

	@GetMapping("/ordonnances/delete/{id}")
	public String deleteOrdonnance(@PathVariable int id, Model model) {
		ordonnanceService.delete(ordonnanceService.getOrdonnance(id));
		return "redirect:/ordonnances";
	}

	@GetMapping("/patients/delete/{id}")
	public String deletePatient(@PathVariable int id, Model model) {
		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		for(Ordonnance o:ordonnanceService.getOrdonnancesByPatient(userPrincipal.getUser(),patientRepository.findById(id).get().getCIN())){
			ordonnanceService.delete(o);
		}
		patientRepository.delete(patientRepository.findById(id).get());
		return "redirect:/patients";
	}

	@PostMapping("/ordonnances/edit/{id}")
	public String editOrdonnance(@PathVariable int id, Model model ,Ordonnance ord,  @ModelAttribute("request") Medicament med) {
		Ordonnance ordonnance=ordonnanceService.getOrdonnance(id);
		ordonnance.setDateo(new Date());
		ordonnance.setMaladie(ord.getMaladie());
		ordonnanceService.save(ordonnance);
		med.setIdO(ordonnance);
		medicamentService.save(med);
		List<Medicament> meds=ordonnance.getMedicaments();
		for(Medicament m:meds){
			if(m==meds.get(meds.size()-1));
			else{
				medicamentService.delete(m.getId());
			}
		}
		return "redirect:/ordonnances";
	}

	@PostMapping("/patients/edit/{id}")
	public String editPatient(@PathVariable int id,@ModelAttribute("request")Patient P, Model model) {
		Patient editedPatient=patientRepository.findById(id).get();
		editedPatient.setCIN(P.getCIN());
		editedPatient.setDateNaissance(P.getDateNaissance());
		editedPatient.setNom(P.getNom());
		editedPatient.setPrenom(P.getPrenom());
		patientRepository.save(editedPatient);
		return "redirect:/patients";
	}

	@GetMapping("/patients/edit/{id}")
	public String getEditPatient(@PathVariable int id, Model model){
		model.addAttribute("patient",patientRepository.findById(id).get());
		return "patientEdit";
	}

	@GetMapping("/patients/{id}")
	public String getPatient(@PathVariable int id, Model model) {
		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		model.addAttribute("patient", patientRepository.findById(id).get());
		model.addAttribute("ordonnances", ordonnanceService.getOrdonnancesByPatient(userPrincipal.getUser(),patientRepository.findById(id).get().getCIN()));
		return "patientDetails";
	}

	@GetMapping("/cancelOnCreation")
	public String cancelOnCreation(){
		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ordonnanceService.cancelOnCreation(userPrincipal.getUser());
		return "firstPage";
	}

	@GetMapping("/sendOnCreation")
	public String sendOnCreation(){
		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ordonnanceService.sendOnCreation(userPrincipal.getUser());
		return "SendingWaysPage";
	}

	@PostMapping("PatientClonePage")
	public String addPatientClone( @ModelAttribute("request") Patient P, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException, WriterException {
		boolean ancien = false;
		List<Patient> Patients =patientRepository.findAll();
		for(Patient patient : Patients) {
			if(patient.getCIN().equals(P.getCIN())) {
				Pa=patient;
				ancien=true;
			}
		}
		if(ancien==false) {
			patientRepository.save(P);
			Pa=P;
		}
		 //construction de la nouvelle ordo :
		Ordonnance newOrdo = new Ordonnance();
		newOrdo.setPatient(Pa);
		newOrdo.setSent(false);
		newOrdo.setMaladie(clonedOrdo.getMaladie());
		newOrdo.setDateo(new Date());
		newOrdo.setUser(clonedOrdo.getUser());
		newOrdo.setMedicaments(clonedOrdo.getMedicaments());
		ordonnanceService.save(newOrdo);
		List<Ordonnance>ordosUser=ordonnanceService.getOrdonnancesByUser(clonedOrdo.getUser());
		List<Medicament> Medica =ordosUser.get(ordosUser.size()-1).getMedicaments();
		boolean isFile = medicamentService.createPdf(Medica, context,ordosUser.get(ordosUser.size()-1).getId(), request, response);
		String qrCodePath = writeQR(newOrdo,medicamentService.sarrFront[3]);
		File file1 = new File("./src/main/resources/static/"+newOrdo.getId()+"-QrCode.png");
		File file2 = new File("./src/main/resources/static/Ordonnance-"+newOrdo.getId()+".png");
		FileInputStream imageInFile1 = new FileInputStream(file1);
		FileInputStream imageInFile2 = new FileInputStream(file2);

		// Reading a Image file from file system
		BufferedImage Qr = ImageIO.read(imageInFile1 );
		BufferedImage tab = ImageIO.read(imageInFile2 );

		// create the new image, canvas size is the max. of both image sizes
		int w = Math.max(Qr.getWidth(), tab.getWidth());
		int h = Math.max(Qr.getHeight(),tab.getHeight());
		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		int x=tab.getWidth()/2-100;
		int y=Qr.getHeight()-140;

		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(tab, 0, y+140, null);
		g.drawImage(Qr, x,140, null);
		g.dispose();
		
		// Save as new image
		ImageIOUtil.writeImage(
				combined, String.format("./src/main/resources/static/ordonnance-fin-%d.%s", newOrdo.getId(), "png"), 300);
		Reqid=newOrdo.getId();
		model.addAttribute("ordoId",medicamentService.sarrFront[3]);
		return "ConfirmOrdoPage";
	}
	
	@PostMapping("PatientPage")
	public String addPatient( @ModelAttribute("request") Patient P, Model model) {
		boolean ancien = false;
		List<Patient> Patients =patientRepository.findAll();
		for(Patient patient : Patients) {
			if(patient.getCIN().equals(P.getCIN())) {
				System.out.println("anc Pnew");
				Pa=patient;
				ancien=true;
			}
		}
		if(ancien==false) {
			System.out.println("Save P ");
			patientRepository.save(P);
			Pa=P;
		}
		return "prescriptionPage";
	}

	@GetMapping("/patients/addOrdonnance/{id}")
	public String addOrdonnancePatient(@PathVariable int id, Model model) {
		Pa=patientRepository.findById(id).get();
		return "redirect:/prescriptionPage";
	}
	
	@PostMapping("prescriptionPage")
	public String addNew(Ordonnance ord,  @ModelAttribute("request") Medicament med, Model model,HttpServletRequest request, HttpServletResponse response ) throws WriterException, IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ord.setUser(user.getUser());
		ord.setPatient(Pa);
		ordonnanceService.save(ord);
		med.setIdO(ord);
		medicamentService.save(med);
		List<Medicament> Medica = medicaR.findByIdO(ord);
		String qrCodePath = writeQR(ord,medicamentService.sarrFront[3]);		
		File file1 = new File("./src/main/resources/static/"+ord.getId()+"-QrCode.png");
		File file2 = new File("./src/main/resources/static/Ordonnance-"+ord.getId()+".png");
		FileInputStream imageInFile1 = new FileInputStream(file1);
		FileInputStream imageInFile2 = new FileInputStream(file2);
		   
		 // Reading a Image file from file system
		BufferedImage Qr = ImageIO.read(imageInFile1 );
		BufferedImage tab = ImageIO.read(imageInFile2 );

		// create the new image, canvas size is the max. of both image sizes
		int w = Math.max(Qr.getWidth(), tab.getWidth());
		int h = Math.max(Qr.getHeight(),tab.getHeight());
		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		int x=tab.getWidth()/2-100;
		int y=Qr.getHeight()-140;
		
		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(tab, 0, y+140, null);
		g.drawImage(Qr, x,140, null);
		g.dispose();

		// Save as new image
		ImageIOUtil.writeImage(
		          combined, String.format("./src/main/resources/static/ordonnance-fin-%d.%s", ord.getId(), "png"), 300);
		Reqid=ord.getId();
		model.addAttribute("ordoId",medicamentService.sarrFront[3]);
		return "ConfirmOrdoPage";;
	}
	
	@GetMapping("/readQR")
	public String verifyQR(@RequestParam("qrImage") String qrImage, Model model) throws Exception {
		model.addAttribute("content", readQR(qrImage, model));
		model.addAttribute("code", qrImage);
		return "QRlire";
	}
	
	private String writeQR(Ordonnance request,String content) throws WriterException, IOException {
		String qcodePath = "./src/main/resources/static/" + request.getId() + "-QRCode.png";
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300);
		Path path = FileSystems.getDefault().getPath(qcodePath);
		MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
		return "" + request.getId() + "-QRCode.png";
		
	}
	
	private String readQR(String qrImage, Model model) throws Exception {
		final Resource fileResource = resourceLoader.getResource("classpath:static" + qrImage);
		File QRfile = fileResource.getFile();
		BufferedImage bufferedImg = ImageIO.read(QRfile);
		LuminanceSource source = new BufferedImageLuminanceSource(bufferedImg);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Result result = new MultiFormatReader().decode(bitmap);
		model.addAttribute("lire", result.getText());
		return "QRlire";
	}
	
	@GetMapping("/test-whatsapp")
	public String showIndexPage() {
		String line = "patient";
		System.setProperty("webdriver.chrome.driver","chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("useAutomationExtension", false);
		WebDriver driver = new ChromeDriver(options);
		String baseUrl = "https://web.whatsapp.com/";
		driver.get(baseUrl);
		try {
		Thread.sleep(15000);
				driver.findElement(By.xpath("//label[@class='_3xpD_']")).click();
				//WebElement ser = driver.findElement(By.xpath("//input[@title='Rechercher ou démarrer une nouvelle discussion']"));
				WebElement ser = driver.findElement(By.xpath("//div[@class='_3FRCZ copyable-text selectable-text']"));
				ser.sendKeys(line + "\n");
				Thread.sleep(10000);
				driver.findElement(By.xpath("//span[@title='" + line + "']")).click();
				driver.findElement(By.xpath("//div[@title='Joindre']")).click();
				WebElement uploadElement = driver.findElement(By.xpath("//li[1]//button//input"));
				uploadElement.sendKeys("C:\\Users\\HP\\Downloads\\OrdooDistance (1)\\OrdoDistance\\src\\main\\resources\\static\\ordonnance-fin-"+Reqid+".png");
				Thread.sleep(2000);
				driver.findElement(By.cssSelector(
						"div.SwXX- div._3W-fv.QRMJR div._2ySBn div._2FVVk._3WjMU._1C-hz > div._3FRCZ.copyable-text.selectable-text"))
						.click();

				WebElement msg = driver.findElement(By.cssSelector(
						"div.SwXX- div._3W-fv.QRMJR div._2ySBn div._2FVVk._3WjMU._1C-hz > div._3FRCZ.copyable-text.selectable-text"));
				msg.sendKeys("Bon rétablissement :) ! \n");
				Thread.sleep(2000);

			}catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
		return "SendingSuccessPage";  
	}

	@GetMapping("/test-email")
	private String getEmail(Model model) {
		model.addAttribute("email", new String());
		return "Email";
	}
	
	@PostMapping("/test-email")
	private String sendEmail(String email) {
		String ordoPath="./src/main/resources/static/ordonnance-fin-"+Reqid+".png";
		boolean isSent=sendingMailService.sendMail(email,ordoPath );
		return "SendingSuccessPage";
	}

	@GetMapping("/print")
	private String print(Model model){
		String ordoPath="http://localhost:8080/ordonnance-fin-"+Reqid+".png";
		model.addAttribute("ordsrc", ordoPath);
		return "print";
	}
}
