package tz.go.mof.trab.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class FileDataExtractor {

	public static HashMap<Integer, ArrayList<String>> storedFileData;
	public static ArrayList<String> storedFile;
	public static int rowCounted = 0;

	public static HashMap<Integer, ArrayList<String>> parseExcelNew(String file) {
		try {
			@SuppressWarnings("deprecation")
			XSSFWorkbook wb = new XSSFWorkbook(file);
			XSSFSheet sheet = wb.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();

			// initializing collection variable
			storedFileData = new HashMap<>();
			ArrayList<String> rowData = new ArrayList<>();
			int rowCount = 0;
			// Create a DataFormatter to format and get each cell's value as
			// String
			DataFormatter dataFormatter = new DataFormatter();

			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();

				if (rowCount > 0) {
					//System.out.println("\n");
					Iterator<Cell> cells = row.cellIterator();

					while (cells.hasNext()) {
						Cell cell = cells.next();
						String cellValue = dataFormatter.formatCellValue(cell);
						rowData.add(cellValue);
						//System.out.println(cellValue);
					}
					rowCounted = rowCount;
					storedFileData.put(rowCount, rowData);
					rowData = new ArrayList<>();
				}
				rowCount++;
			}
			// System.out.println("Stored File : " + storedFileData.toString());
			return storedFileData;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static boolean writeXLSXFile(File file, HashMap<Integer, ArrayList<String>> getDataToWriteExcell) {
		String sheetName = "Arreas_to_Verify";// name of sheet
		file.setWritable(true);
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName);

		XSSFRow row1 = sheet.createRow(0);
		// iterating through column names
		ArrayList<String> rowData = getDataToWriteExcell.get(0);
		int count = 0;
		for (String values : rowData) {
			XSSFCell cell = row1.createCell(count);
			cell.setCellValue(values);
			count++;
		}
		for (int row = 1; row < getDataToWriteExcell.size(); row++) {
			XSSFRow rowcell = sheet.createRow(row);
			ArrayList<String> rowData1 = getDataToWriteExcell.get(row);
			for (int rows = 0; rows < getDataToWriteExcell.get(row).size(); rows++) {
				XSSFCell cell = rowcell.createCell(rows);
				cell.setCellValue(rowData1.get(rows));
			}
		}

		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		try {
			wb.write(fileOut);
			fileOut.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}
}
