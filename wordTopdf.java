package egov2.word;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.SchemaType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.CTHdrFtrImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.CTHdrFtrRefImpl;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

public class WordTest {
	public static void main(String[] args) throws Exception {
		FileInputStream fis = new FileInputStream("C:/test/word/createdocument.doc");
		XWPFDocument docx = new XWPFDocument(fis);
//		XWPFHeaderFooterPolicy policy= docx.getHeaderFooterPolicy();
//		XWPFHeader header = policy.getHeader(0);
		
		//머리말 바꾸기
		docx = replacePOI(docx, "머리말1", "리플레이스머리말1");
		//꼬리말 바꾸기
		docx = replacePOI2(docx, "텍스트1", "리플레이스텍스트");
		
		//내용 바꾸기
		List<XWPFParagraph> paragrapgList = docx.getParagraphs();
		for(XWPFParagraph paragraph : paragrapgList) {
			List<XWPFRun> runs = paragraph.getRuns();
			if(runs != null) {
				for(XWPFRun r: runs) {
					String text = r.getText(0);
					if(text != null && text.contains("ㅅㅅㅅ")) {
						text = text.replace("ㅅㅅㅅ", "ㅋㅌㅊ");
						r.setText(text, 0);
					}
					if(text != null && text.contains("${ABC}")) {
						text = text.replace("${ABC}", "과제1");
						r.setText(text, 0);
					}
				}
			}
		}
		
		FileOutputStream out = new FileOutputStream(new File("C:/test/word/test2.doc"));
		docx.write(out);
		out.close();
		
		//오픈오피스
		SocketOpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
		connection.connect();
		//원본 디렉토리에 targetPdf 명칭지정
//		String valueFile = null; 
//		valueFile  = inputFile.getParent().replace('\\', FILE_SEPARATOR).replace('/', FILE_SEPARATOR);
		File inputFile = new File("C:/test/word/test2.doc");
		File outputFile = new File("C:/test/word/test.pdf");
		// convert
		DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
		converter.convert(inputFile, outputFile);
		// close the connection
		connection.disconnect();
	}
	
	public static XWPFDocument replacePOI2(XWPFDocument doc, String placeHolder, String replaceText){
		// REPLACE ALL HEADERS
//		XWPFHeaderFooterPolicy policy= doc.getHeaderFooterPolicy();
//		doc.getF
		for (XWPFFooter footer : doc.getFooterList()) 
			replaceAllBodyElements(footer.getBodyElements(), placeHolder, replaceText);
		replaceAllBodyElements(doc.getBodyElements(), placeHolder, replaceText);
		return doc;
	}
	
	 public static XWPFDocument replacePOI(XWPFDocument doc, String placeHolder, String replaceText){
		    // REPLACE ALL HEADERS
		    for (XWPFHeader header : doc.getHeaderList()) 
		        replaceAllBodyElements(header.getBodyElements(), placeHolder, replaceText);
		    	replaceAllBodyElements(doc.getBodyElements(), placeHolder, replaceText);
		    return doc;
	 }

	private static void replaceAllBodyElements(List<IBodyElement> bodyElements, String placeHolder, String replaceText){
	    for (IBodyElement bodyElement : bodyElements) {
	        if (bodyElement.getElementType().compareTo(BodyElementType.PARAGRAPH) == 0)
	            replaceParagraph((XWPFParagraph) bodyElement, placeHolder, replaceText);
	        if (bodyElement.getElementType().compareTo(BodyElementType.TABLE) == 0)
	            replaceTable((XWPFTable) bodyElement, placeHolder, replaceText);
	    }
	}

	private static void replaceTable(XWPFTable table, String placeHolder, String replaceText) {
	    for (XWPFTableRow row : table.getRows())
	        for (XWPFTableCell cell : row.getTableCells())
	            for (IBodyElement bodyElement : cell.getBodyElements()) {
	                if (bodyElement.getElementType().compareTo(BodyElementType.PARAGRAPH) == 0)
	                    replaceParagraph((XWPFParagraph) bodyElement, placeHolder, replaceText);
	                if (bodyElement.getElementType().compareTo(BodyElementType.TABLE) == 0)
	                    replaceTable((XWPFTable) bodyElement, placeHolder, replaceText);
	            }
	}

	private static void replaceParagraph(XWPFParagraph paragraph, String placeHolder, String replaceText) {
	    for (XWPFRun r : paragraph.getRuns()) {
	        String text = r.getText(r.getTextPosition());
	        if (text != null && text.contains(placeHolder)) {
	            text = text.replace(placeHolder, replaceText);
	            r.setText(text, 0);
	        }
	    }
	}
}
