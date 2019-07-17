package abm.com.sol.controllers;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.*;
import io.swagger.annotations.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/edit")

@Api(value = "Guidelines", tags = "Filling out file", description = "Filling out a pdf file")
public class AddContentToPdfController {


    @Autowired
    ResourceLoader resourceLoader;

    private static final Logger log = LoggerFactory.getLogger(AddContentToPdfController.class);

      public String FONT = "/Users/abm/sol/src/main/resources/fonts/FreeSans.ttf";

    PdfReader pdfReader = null;

    @PostMapping("/filling-out")
    @ApiOperation(value = "Make a Post for filling out forms one the pdf ",
            produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The POST call is Successful"),
            @ApiResponse(code = 500, message = "The POST call is Failed"),
            @ApiResponse(code = 404, message = "The API could not be found")
    })
    public ResponseEntity<String> fillingOutFile(
            @ApiParam(name = "file", value = "Select the file to Upload", required = true)
            @RequestPart MultipartFile file) {

        log.info("starting fillingOutFile file name, {}", file.getName());

        File testFile = new File("TemplateForm.pdf");
        try {
            FileUtils.writeByteArrayToFile(testFile, file.getBytes());
        } catch (IOException e) {
            log.warn("error when converting multipart to file: {} ", e);
            return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);

        }

        try {
            manipulatePdf(testFile);
        } catch (Exception e) {
            e.printStackTrace();log.warn("error when manipulating file: {} ", e);
            return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info("End fillingOutFile file name, {}", file.getName());

        return new ResponseEntity<String>("Done", HttpStatus.OK);
    }

    protected void manipulatePdf(File file) throws Exception {
        // License file is loaded because open type font is used and typography module is in classpath:
        // typography module is utilized and requires license.
        //LicenseKey.loadLicenseFile(System.getenv("ITEXT7_LICENSEKEY") + "/itextkey-typography.xml");

        File destFile = new File("fillingOutDoc.pdf");
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(file), new PdfWriter(destFile));


        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true);
        form.setGenerateAppearance(true);


        PdfFont font = PdfFontFactory.createFont(FONT,
                PdfEncodings.IDENTITY_H);

        form.getField("Country").setValue("France", font, 16f);
        form.getField("Name").setValue("Philippe DUPONT", font, 16f);
        form.getField("Company").setValue("BUISINESS IT GLOBAL", font, 16f);

        pdfDocument.close();
    }
}
