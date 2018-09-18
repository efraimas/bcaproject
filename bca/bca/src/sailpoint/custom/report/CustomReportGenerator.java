package sailpoint.custom.report;

import java.awt.Color;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class CustomReportGenerator {
	
public PdfPTable getReportHeader(String headerText, String retensi, String laporan, String cabang, String frek, String tanggal) throws DocumentException{
		
		PdfPTable header = new PdfPTable(7);
		float[] widths = new float[] { 5f, 1f, 5f, 5f, 5f, 1f, 5f };
		header.setWidths(widths);
		header.setWidthPercentage(100);
		
		int borderStyle = Rectangle.NO_BORDER;
		
		PdfPCell cellHeader = new PdfPCell(new Phrase(headerText, new Font(Font.TIMES_ROMAN, 18, Font.BOLD)));
		cellHeader.setColspan(7);
		cellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
		cellHeader.setBorder(0);
		
		
		PdfPCell cellNewLine = new PdfPCell(new Phrase(""));
		cellNewLine.setColspan(7);
		cellNewLine.setBorder(0);
		
		PdfPCell cellRetensiText = new PdfPCell(new Phrase("Retensi"));
		cellRetensiText.setHorizontalAlignment(Element.ALIGN_LEFT);
		cellRetensiText.setBorder(borderStyle);
		
		PdfPCell cellRetensi = new PdfPCell(new Phrase(retensi));
		cellRetensi.setHorizontalAlignment(Element.ALIGN_LEFT);
		cellRetensi.setBorder(borderStyle);
		
		PdfPCell cellLaporanText = new PdfPCell(new Phrase("Laporan"));
		cellLaporanText.setHorizontalAlignment(Element.ALIGN_LEFT);
		cellLaporanText.setBorder(borderStyle);
		
		PdfPCell cellLaporan = new PdfPCell(new Phrase(laporan));
		cellLaporan.setHorizontalAlignment(Element.ALIGN_LEFT);
		cellLaporan.setBorder(borderStyle);
		
		PdfPCell cellCabangText = new PdfPCell(new Phrase("Cabang"));
		cellCabangText.setHorizontalAlignment(Element.ALIGN_LEFT);
		cellCabangText.setBorder(borderStyle);
		
		PdfPCell cellCabang = new PdfPCell(new Phrase(cabang));
		cellCabang.setHorizontalAlignment(Element.ALIGN_LEFT);
		cellCabang.setBorder(borderStyle);
		
		PdfPCell separator = new PdfPCell(new Phrase(":"));
		separator.setHorizontalAlignment(Element.ALIGN_CENTER);
		separator.setBorder(borderStyle);
		
		PdfPCell blankCell = new PdfPCell(new Phrase(""));
		blankCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		blankCell.setBorder(borderStyle);
		
		PdfPCell cellFrekText = new PdfPCell(new Phrase("Frekuensi"));
		cellFrekText.setHorizontalAlignment(Element.ALIGN_LEFT);
		cellFrekText.setBorder(borderStyle);
		
		PdfPCell cellFrek = new PdfPCell(new Phrase(frek));
		cellFrek.setHorizontalAlignment(Element.ALIGN_LEFT);
		cellFrek.setBorder(borderStyle);
		
		PdfPCell cellTanggalText = new PdfPCell(new Phrase("Tanggal"));
		cellTanggalText.setHorizontalAlignment(Element.ALIGN_LEFT);
		cellTanggalText.setBorder(borderStyle);
		
		PdfPCell cellTanggal = new PdfPCell(new Phrase(tanggal));
		cellTanggal.setHorizontalAlignment(Element.ALIGN_LEFT);
		cellTanggal.setBorder(borderStyle);
		
		PdfPCell cellHalamanText = new PdfPCell(new Phrase("Halaman"));
		cellHalamanText.setHorizontalAlignment(Element.ALIGN_LEFT);
		cellHalamanText.setBorder(borderStyle);
		
		PdfPCell cellHalaman = new PdfPCell(new Phrase("1"));
		cellHalaman.setHorizontalAlignment(Element.ALIGN_LEFT);
		cellHalaman.setBorder(borderStyle);
		
		
		//add header
		header.addCell(cellHeader);
		header.addCell(cellNewLine);
		header.addCell(cellNewLine);
		header.addCell(cellNewLine);
		
		//add row 1
		header.addCell(cellRetensiText);
		header.addCell(separator);
		header.addCell(cellRetensi);
		header.addCell(blankCell);
		header.addCell(cellFrekText);
		header.addCell(separator);
		header.addCell(cellFrek);
		
		//add row 2
		header.addCell(cellLaporanText);
		header.addCell(separator);
		header.addCell(cellLaporan);
		header.addCell(blankCell);
		header.addCell(cellTanggalText);
		header.addCell(separator);
		header.addCell(cellTanggal);
		
		//add row 3
		header.addCell(cellCabangText);
		header.addCell(separator);
		header.addCell(cellCabang);
		header.addCell(blankCell);
		header.addCell(cellHalamanText);
		header.addCell(separator);
		header.addCell(cellHalaman);
		
		header.addCell(cellNewLine);
		header.addCell(cellNewLine);
		header.addCell(cellNewLine);
		
		return header;
		
	}

public PdfPTable getTableHeader(String []headers) throws DocumentException{
	
	PdfPTable header = new PdfPTable(headers.length);
	float[] widths = new float[headers.length];
	
	for(int i=0; i<headers.length; i++){
		widths[i] = 1f;
	}
	
	header.setWidths(widths);
	header.setWidthPercentage(102);
	
	int borderStyle = Rectangle.BOX;
	
	Font fontHeader = new Font(Font.TIMES_ROMAN, 10, Font.NORMAL);
	
	for(int i=0; i<headers.length; i++){
		
		PdfPCell cellTableHeader = new PdfPCell(new Phrase(headers[i], fontHeader));
		cellTableHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
		cellTableHeader.setBorder(borderStyle);
		cellTableHeader.setBackgroundColor(new Color(176, 224, 230));
		 
		header.addCell(cellTableHeader);
	}
	
	
	return header;
	
	}

}
