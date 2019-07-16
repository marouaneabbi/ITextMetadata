package abm.com.sol.controllers;

import com.itextpdf.kernel.pdf.*;
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
import java.nio.charset.Charset;

@RestController
@RequestMapping("/XmpMetadata")

@Api(value = "Guidelines", tags = "Upload files")

public class EditXmpMetadataController {


    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    static PdfName PIECE_INFO = new PdfName("PieceInfo");
    static PdfName LAST_MODIFIED = new PdfName("LastModified");
    static PdfName PRIVATE = new PdfName("Private");
    PdfReader pdfReader = null;

    @GetMapping("/getMetadata")
    @ApiOperation(value = "Make a GET for getting XmpMetadata embedded on the pdf ",
            produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The POST call is Successful"),
            @ApiResponse(code = 500, message = "The POST call is Failed"),
            @ApiResponse(code = 404, message = "The API could not be found")
    })
    public ResponseEntity<String> readXmpMetadataFile(
            @ApiParam(name = "file", value = "Select the file to Upload", required = true)
            @RequestPart MultipartFile file) {

        log.info("starting uploading file name: {}", file.getName());

        File testFile = new File("PDD_metadata.pdf");
        try {
            FileUtils.writeByteArrayToFile(testFile, file.getBytes());
        } catch (IOException e) {
            log.warn("error when converting multipart to file: {} ", e);
            return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);

        }

        try {
            readXmpMetadata(testFile);
        } catch (IOException e) {
            log.warn("error when reading XmpMedataData from file: {} ", e);
        }
        return new ResponseEntity<String>("Done", HttpStatus.OK);
    }

    @PostMapping("/setMetadata")
    @ApiOperation(value = "Make a POST for setting XmpMetadata embedded on the pdf ",
            produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The POST call is Successful"),
            @ApiResponse(code = 500, message = "The POST call is Failed"),
            @ApiResponse(code = 404, message = "The API could not be found")
    })
    public ResponseEntity<String> updateXmpMetadataFile(
            @ApiParam(name = "file", value = "Select the file to Upload", required = true)
            @RequestPart MultipartFile file) {

        log.info("starting setting XmpMetadata file name: {}", file.getName());

        File testFile = new File("PDD_metadata.pdf");
        try {
            FileUtils.writeByteArrayToFile(testFile, file.getBytes());
        } catch (IOException e) {
            log.warn("error when converting multipart to file: {} ", e);
            return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);

        }

        try {
            writXmpMetadata(testFile);
        } catch (IOException e) {
            log.warn("error when converting multipart to file: {} ", e);
            return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info("End setting XmpMetadata file name: {}", file.getName());
        return new ResponseEntity<String>("Done", HttpStatus.OK);
    }


    /**
     * Extracts PDF Metadata and returns the meta data as byte array.
     */
     byte[] readXmpMetadata(File file) throws IOException {

        PdfReader pdfReader = new PdfReader(file);
        PdfDocument pdfDocument = new PdfDocument(pdfReader);
        byte[] b = pdfDocument.getXmpMetadata();
        System.out.println(new String(b));

        return b;
    }

    /**
     * Reads a metadata from a file specified in the src parameter and stores the extracted
     * metadata to a file specified in the dest parameter.
     */
    void writXmpMetadata(File file) throws IOException {

        // update xmp meta-data
        File destFile = new File("new_XMP_metadata.pdf");
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(file), new PdfWriter(destFile));

        pdfDoc.getFirstPage().setXmpMetadata(getXMPMetadata());
        pdfDoc.close();
    }

    /**
     * returns a hardcoded metadata XMP file. Name and value pairs are
     * added at the Document Metadata section.
     */
    private static byte[] getXMPMetadata() {
        String xmpMetada = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<?xpacket begin=\"?\" id='W5M0MpCehiHzreSzNTczkc9d'?>"
                + "<x:xmpmeta xmlns:x=\"adobe:ns:meta/\">"
                + "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
                + "<rdf:Description rdf:about=\"\" "
                + "xmlns:dc=\"http://purl.org/dc/elements/1.1/\">"
                + "<dc:format>application/pdf</dc:format>"
                + "<DOCUMENT_METADATA>"
                + "<rdf:Bag>"
                + "<rdf:li>Name=Brad Pitt</rdf:li>"
                + "<rdf:li>Movie=FightClub</rdf:li>"
                + "<rdf:li>Age=47</rdf:li>"
                + "</rdf:Bag>"
                + "</DOCUMENT_METADATA>"
                + "</rdf:Description>"
                + "</rdf:RDF>"
                + "</x:xmpmeta>"
                + "<?xpacket end=\"w\"?>";
        return xmpMetada.getBytes(Charset.forName("UTF-8"));

    }


    /**
     *
     * @param filesrc
     * @param dest
     * @throws IOException

    public static void writXmpMetadata(File filesrc, String dest) throws IOException {

    PdfReader pdfReader = new PdfReader(filesrc);
    FileOutputStream fos = new FileOutputStream(dest);
    PdfDocument pdfDocument = new PdfDocument(pdfReader);
    byte[] b = pdfDocument.getXmpMetadata();
    fos.write(b, 0, b.length);
    fos.flush();
    fos.close();
    pdfDocument.close();

    System.out.println("\n\nSuccessfully read metadata from PDF and found following:\n " + new String(b));


    // tetsing seting XmpMetadata
    // get and edit meta-data
    HashMap<String, String> info = new HashMap<>();
    info.put("Subject", "Hello World");
    info.put("Author", "your name");
    info.put("Keywords", "iText pdf");
    info.put("Title", "Hello World stamped");
    info.put("Creator", "your name");
    info.put("Producer", "sdfmlkqsdjflqsjf");

    // update xmp meta-data
    pdfDocument.getFirstPage().setXmpMetadata(getXMPMetadata());
    }
     */

}

