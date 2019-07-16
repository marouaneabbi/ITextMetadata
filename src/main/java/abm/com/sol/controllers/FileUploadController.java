package abm.com.sol.controllers;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.PdfDictionary;
import io.swagger.annotations.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/upload")

@Api(value = "Guidelines", tags = "Upload files")
/*

FileUploadController
 */
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    static PdfName PIECE_INFO = new PdfName("PieceInfo");
    static PdfName LAST_MODIFIED = new PdfName("LastModified");
    static PdfName PRIVATE = new PdfName("Private");
    PdfReader pdfReader = null;
    PdfDocument pdfDoc = null;

    @PostMapping
    @ApiOperation(value = "Make a Post for setting PieceInfoDictionary embedded on the pdf ",
            produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The POST call is Successful"),
            @ApiResponse(code = 500, message = "The POST call is Failed"),
            @ApiResponse(code = 404, message = "The API could not be found")
    })
    public ResponseEntity<String> uploadFile(
            @ApiParam(name = "file", value = "Select the file to Upload", required = true)
            @RequestPart MultipartFile file) {

        log.info("starting uploading file name, {}", file.getName());

        PdfName appName = new PdfName("IBM-ODIndexes");
        PdfName dataName1 = new PdfName("Cname");
        PdfName dataName2 = new PdfName("BankNumber");
        PdfName dataName3 = new PdfName("AcctNumber");
        PdfName dataName4 = new PdfName("StmtDate");

        File testFile = new File("PDD_metadata.pdf");
        try {
            FileUtils.writeByteArrayToFile(testFile, file.getBytes());
        } catch (IOException e) {
            log.warn("error when converting multipart to file: {} ", e);
            return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);

        }

        File destFile = new File("new_PDD_metadata.pdf");
        try {
            pdfDoc = new PdfDocument(new PdfReader(testFile), new PdfWriter(destFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int pages = pdfDoc.getNumberOfPages();

        for (int p = 1; p <= pages; p++) {
            System.out.println("Page " + p + " Inserted");
            addPieceInfo(pdfDoc, appName, dataName1, dataName2, dataName3, dataName4, new PdfString("ABM-Bud1"), new PdfString("ABM-0002"), new PdfString("ABM-123678"), new PdfString("ABM-20120507"), p);
        }

        pdfDoc.close();
        log.info("End uploading file name, {}", file.getName());

        return new ResponseEntity<String>("Done", HttpStatus.OK);
    }

    @PostMapping("/getPieceInfoDictionary")
    @ApiOperation(value = "Make a Post for getting PieceInfoDictionary embedded on the pdf",
            produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The POST call is Successful"),
            @ApiResponse(code = 500, message = "The POST call is Failed"),
            @ApiResponse(code = 404, message = "The API could not be found")
    })
    public ResponseEntity<String> getInfoPieceDictionnary(
            @ApiParam(name = "file", value = "Select the file to Upload", required = true)
            @RequestPart MultipartFile file) {

        log.info("starting uploading file name, {} ", file.getName());

        File testFile = new File("PDD_metadata.pdf");
        try {
            FileUtils.writeByteArrayToFile(testFile, file.getBytes());
        } catch (IOException e) {
            log.warn("error when converting multipart to file: {} ", e);
            return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);

        }

        try {
            pdfReader = new PdfReader(testFile);
        } catch (IOException e) {
            log.warn("error when converting file to PdfReader: {} ", e);
            return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);

        }

        //int pages = pdfReader.getNumberOfPages();
        PdfDocument pdfDocument = new PdfDocument(pdfReader);
        int pages = pdfDocument.getNumberOfPages();

        PdfDictionary pieceInfo = pdfDocument.getPage(1).getPdfObject().getAsDictionary(PIECE_INFO);
        log.info("END uploading file name, {} ", file.getName());

        return new ResponseEntity<String>(String.valueOf(pieceInfo), HttpStatus.OK);
    }

    void addPieceInfo(PdfDocument pdfDocument, PdfName app, PdfName name1, PdfName name2, PdfName name3, PdfName name4, PdfObject value1, PdfObject value2, PdfObject value3, PdfObject value4, int p) {

        PdfDictionary pieceInfo = pdfDocument.getPage(p).getPdfObject().getAsDictionary(PIECE_INFO);
        if (pieceInfo == null) {
            pieceInfo = new PdfDictionary();
            pdfDocument.getTrailer().put(PIECE_INFO, pieceInfo);
        }

        PdfDictionary appData = pieceInfo.getAsDictionary(app);
        if (appData == null) {
            appData = new PdfDictionary();
            pieceInfo.put(app, appData);
        }
        //appData.put(LAST_MODIFIED, new PdfDate());
        PdfDictionary privateData = pieceInfo.getAsDictionary(PRIVATE);

        if (privateData == null) {
            privateData = new PdfDictionary();
            appData.put(PRIVATE, privateData);
        }

        privateData.put(name1, value1);
        privateData.put(name2, value2);
        privateData.put(name3, value3);
        privateData.put(name4, value4);
    }

}
