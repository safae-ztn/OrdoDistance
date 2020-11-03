package pfa.ordodistance.services;

import com.itextpdf.html2pdf.HtmlConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

@Service
public class Html2PdfServiceImpl implements Html2PdfService {

    private final TemplateEngine templateEngine = new TemplateEngine();

    @Override
    public InputStreamResource html2PdfGenerator(Map<String, Object> data) {
        Context context = new Context();
        context.setVariables(data);
        final String html = templateEngine.process("invoice", context);
        final String DEST = "target/FA-2018-09-04-0001.pdf";
        try {
            HtmlConverter.convertToPdf(html, new FileOutputStream(DEST));
            return new InputStreamResource(new FileInputStream(DEST));

        } catch (IOException e) {
            return null;
        }
    }

}
