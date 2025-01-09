package dev.prats.ragdemo;


import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;



/**
 *  Ingestion Service purpose:
 *  It reads the file and parse it
 *  then, It splits content into smaller chunks.
 *  then, Ingest the chunks into a VectorStore
 */

@Component
public class IngestionService implements CommandLineRunner {

    //    logging information, warnings and errors during Ingestion Process
    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);

    //     a storage mechanism to store the data into vector based and retrieve  vectorized represtion of the ingested data
    private final VectorStore vectorStore;

    //    we need to add reference and ingestion to Embedding model
    @Value("classpath:/docs/research_RAG.pdf")  // value annotation is used to injects a file using classpath
    private Resource resource;   //Resource can dynamically load the file to be ingested and used for different type of files

    //    Initialized the vectorized data through constructor
    public IngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    //    Ingestion Phase
    @Override
    public void run(String... args) throws Exception {
        try {
            System.err.println("Configured Ingestion Service");

//            multiple ways we can read resources

            var pdfReader = new ParagraphPdfDocumentReader(resource);
//        var pdfReader = new ParagraphPdfDocumentReader(resource.getInputStream();
//        var pdfReader = new ParagraphPdfDocumentReader(resource.getFile());
            TextSplitter textSplitter = new TokenTextSplitter();
            vectorStore.accept(textSplitter.apply(pdfReader.get()));
            log.info("VectorStore loaded with Data!!!");
        } catch (Exception e) {
//            throw new RuntimeException(e);
            log.error("Error During Ingestion : ", e);
        }

    }
}








//    @Override
//    public void run(String... args) throws Exception {
//        try {
//            System.out.println("Configured Ingestion Service");
//
//            // Determine file type
//            String fileName = resource.getFilename();
//
//            // Base case checks
//            if (fileName == null) {
//                throw new IllegalArgumentException("File name cannot be null");
//            }
//            if (!resource.exists()) {
//                throw new IllegalArgumentException("The file " + fileName + " does not exist.");
//            }
//
//            // Get the file extension
//            String fileExtension = getFileExtension(fileName);
//            log.info("Processing file: {} with extension: {}", fileName, fileExtension);
//
//            // Read file content based on the type
//            String content;
//            switch (fileExtension.toLowerCase()) { // Ensuring case-insensitive file extension matching
//                case "pdf" -> content = readPdf(resource);
//                case "xlsx" -> content = readExcel(resource);
////                case "csv" -> content = readCsv(resource);
////                case "txt" -> content = readText(resource);
////                case "docx" -> content = readDocx(resource);
//                default -> throw new UnsupportedOperationException("Unsupported file type: " + fileExtension);
//            }
//
//            // Split content into manageable chunks
//            TextSplitter textSplitter = new TokenTextSplitter();
//            var document = new org.springframework.ai.document.Document(content);
//            List<org.springframework.ai.document.Document> documentList = List.of(document);
//
//            // Pass the List<Document> to the TextSplitter
//            List<Document> chunks = textSplitter.apply(documentList);
//
//        // Convert chunks into Document objects
//            List<org.springframework.ai.document.Document> documents = chunks.stream()
//                    .map(chunk -> new org.springframework.ai.document.Document(chunk))
//                    .toList();
//
//            // Pass the documents to the VectorStore
//            vectorStore.accept(documents);
//            log.info("VectorStore loaded with data from file: {}", fileName);
//        } catch (Exception e) {
//            log.error("Error During Ingestion: ", e);
//        }
//    }
//
//
////    Get file using different extensions
//
//    private String getFileExtension(String fileName) {
//        int dotIndex = fileName.lastIndexOf('.');
//        if(dotIndex == -1){
//            return "";  //No Extension
//        }
//        return fileName.substring(dotIndex + 1);
//    }
//
////    readPDF
//    private String readPdf(Resource resource) throws Exception {
//        try {
//            var pdfReader = new ParagraphPdfDocumentReader(resource);
//            return pdfReader.get().toString();
//        } catch (Exception e) {
//            log.error("Error During PDF Ingestion: ", e);
//            return ""; // Return an empty string in case of an error
//        }
//    }
//
//
////    readExcel - xlsx
//
//    private String readExcel(Resource resource) throws Exception {
//        try( var inputStream = resource.getInputStream();
//            var workBook = WorkbookFactory.create(inputStream)){
//            StringBuilder content = new StringBuilder();
//            workBook.forEach(sheet -> sheet.forEach(row -> {
//                for(var cell : row){
//                    content.append(cell.toString()).append(" ");
//                }
//            }));
//            return content.toString();
//        }
//    }


////    read Word file
//    private String  readDocx(Resource resource) throws Exception {
//        try(var inputStream = resource.getInputStream();
//        var document = new XWPFDocument(inputStream)){
//            return document.getParagraphs().stream()
//                    .map(paragraph -> paragraph.getText() + "\n")
//                    .collect(Collectors.joining());
//        }
//    }
//
////    Read CSV file
//    private  String readCsv(Resource resource) throws Exception {
//        StringBuilder content = new StringBuilder();
//        try(var reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))){
//            var csvReader = new com.opencsv.CSVReader((reader));
//            String[] line;
//            while((line = csvReader.readNext()) != null){
//                content.append(String.join(" " + line)).append("\n");
//            }
//        }
//        return content.toString();
//    }
//
//
////    Read Text file
//    private String readText(Resource resource) throws Exception {
//        try (var inputStream = resource.getInputStream();
//             var reader = new BufferedReader(new InputStreamReader(inputStream))) {
//
//            return reader.lines().collect(Collectors.joining("\n"));
//        }
//    }
//}











