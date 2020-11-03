package pfa.ordodistance.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import pfa.ordodistance.services.Html2PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@Log()
public class Html2PdfRestController {

    private final Html2PdfService documentGeneratorService = null;

    @RequestMapping(value = "/html2pdf", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity html2pdf(@RequestBody Map<String, Object> data) {
        InputStreamResource resource = documentGeneratorService.html2PdfGenerator(data);
        if (resource != null) {
            return ResponseEntity
                    .ok()
                    .body(resource);
        } else {
            return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
