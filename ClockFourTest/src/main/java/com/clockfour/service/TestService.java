package com.clockfour.service;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.clockfour.model.Employee;
import com.clockfour.model.EmployeesHolder;
import com.clockfour.model.Response;

@Service("TestService")
public class TestService {
	
	static final Logger log = Logger.getLogger(TestService.class);

	@Value("${image.folder}")
	private String imageFolder;

	@Value("${archive.folder}")
	private String archiveFolder;

	@Value("${xls.name}")
	private String xlsName;
	
	@Value("${output.file}")
	private String outputFile;
	
    private static String[] xlColumns = {"First Name", "Last Name", "DOB"};

	public Response process(MultipartFile image, EmployeesHolder employees) {
		Response response = new Response();
		log.info("Initiating process");
		if (image!= null && !image.isEmpty() && processImage(image)) {
			response.setImageName(image.getOriginalFilename());
			response.setImageFileSize(image.getSize());
			log.info("Image processing successful:" + response);
		}
		if (processEmployees(employees)) {
			response.setEmployees(employees.getEmployees());
			log.info("Employees processing successful:" + response);
		}
		
		return response;
	}

	public boolean processImage(MultipartFile image) {
        try {
            Path path = Paths.get(imageFolder + File.separator + image.getOriginalFilename());
            if(path.toFile().exists()) {
            	log.info("Image file with same name exists, archiving");
            	Path archive = Paths.get(archiveFolder + File.separator + image.getOriginalFilename() + "." + System.currentTimeMillis());
            	Files.move(path, archive, REPLACE_EXISTING);
            	log.info("Image file with same name archived");
            }
            Files.write(path, image.getBytes());
        	log.info("Image file saved");
        } catch (Exception e) {
            log.info(e);
            return false;
        }
		return true;
	}

	public boolean processEmployees(EmployeesHolder employees) {
    	log.info("Starting employee processing");

		FutureTask<Boolean> ft1 = new FutureTask<>(() -> writeExcel(employees));
		FutureTask<Boolean> ft2 = new FutureTask<>(() -> writePDF(employees));
		 	 
    	new Thread(ft1).start();
    	log.info("Excel generation started");
		new Thread(ft2).start();
    	log.info("PDF generation started");
		
		try {
			if(ft1.get() && ft2.get()) {
		    	log.info("Excel & PDF generation completed");
				return true;
			}
		} catch (Exception e) {
			log.error(e);
			return false;
		}

    	log.info("Excel & PDF generation failed");
		return false;
	}

	public boolean writePDF(EmployeesHolder employees) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(new FileWriter(outputFile));
			pw.println(String.join("|", xlColumns));
	        for(Employee employee: employees.getEmployees()) {
	        	pw.println(employee.toPDString());
	        }
	        pw.close();
		} catch (IOException e) {
			log.error(e);
			return false;
		}
        return true;		
	}
	
	// Used https://www.callicoder.com/java-write-excel-file-apache-poi/
	public boolean writeExcel(EmployeesHolder employees) {
		// Write the output to a file
		try (FileOutputStream fileOut = new FileOutputStream(xlsName); Workbook workbook = new HSSFWorkbook();) {
			CreationHelper createHelper = workbook.getCreationHelper();

			// Create a Sheet
			Sheet sheet = workbook.createSheet("Employees");

			// Create a Font for styling header cells
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 12);
			headerFont.setColor(IndexedColors.BLACK.getIndex());

			// Create a CellStyle with the font
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);

			// Create a Row
			Row headerRow = sheet.createRow(0);

			// Create cells
			for (int i = 0; i < xlColumns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(xlColumns[i]);
				cell.setCellStyle(headerCellStyle);
			}

			// Create Cell Style for formatting Date
			CellStyle dateCellStyle = workbook.createCellStyle();
			dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MM/dd/yyyy"));

			// Create Other rows and cells with employees data
			int rowNum = 1;
			for (Employee employee : employees.getEmployees()) {
				Row row = sheet.createRow(rowNum++);

				row.createCell(0).setCellValue(employee.getFirstname());
				row.createCell(1).setCellValue(employee.getLastname());
				Cell dateOfBirthCell = row.createCell(2);
				dateOfBirthCell.setCellValue(employee.getDob());
				dateOfBirthCell.setCellStyle(dateCellStyle);
			}

			// Resize all columns to fit the content size
			for (int i = 0; i < xlColumns.length; i++) {
				sheet.autoSizeColumn(i);
			}

			workbook.write(fileOut);
		} catch (IOException e) {
			log.error(e);
			return false;
		}
		return true;
	}
}