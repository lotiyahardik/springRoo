package com.ocr.solr.helper;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.PdfUtilities;

import com.ocr.solr.SolrBean;

public final class FileHelper {

	private final Logger logger = Logger.getLogger(FileHelper.class.getName());

	private static final FileHelper INSTANCE = new FileHelper();

	private String fileDirectory = "samples/";
	private String allowExtension = ".pdf .png .jpg .jpeg .bmp .gif";

	private HashMap<String, String> fileName_NumeroId = new HashMap<String, String>();

	// Tesseract instance is used for OCR functionality and IT's provided by Tess4j API 
	private static Tesseract instance;

	private FileHelper(){}

	static{
		// this Instance is in this block for only instance overall in app.
		instance = Tesseract.getInstance(); // JNA Interface
		instance.setLanguage("eng");
	}

	public static FileHelper getInstance() {
		return INSTANCE;
	}

	public ArrayList<SolrBean> doProcess() throws TesseractException, IOException  {
		File[] files =  getFiles();
		if(files.length>0)
			logger.log(Level.SEVERE,"--- Total Available Files "+files.length+" ---");

		ArrayList<SolrBean> solrBeans = new ArrayList<SolrBean>();
		
		for (File file : files) {
			SolrBean bean = ocrFile(file);
			if(bean!=null)
				solrBeans.add(bean);
		}
		//loadFiles(fileName_NumeroId);
		return solrBeans;
	}

	private SolrBean ocrFile(File file) throws TesseractException, IOException {
		if(!file.getName().startsWith("workingimage")){
			//SolrHelper solrHelper = SolrHelper.getInstance();
			String result = getFileContent(file);
			String numeroID = getNumero(result);
			
			if(numeroID!=null){
				fileName_NumeroId.put(file.getName(), numeroID);
				
				return new SolrBean(file.getName(), numeroID, "", "", "");
			}
		}
		return null;
	}

	/*private void loadFiles(HashMap<String, String> fileName_NumeroId2) {
		Iterator it = fileName_NumeroId2.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
		}
	}*/

	private String getNumero(String result) {
		if(!result.contains("Numero")){
			return null;
		}else{
			int start = result.indexOf("Numero")+7;
			int end = start+8;
			return result.substring(start, end);
		}
	}

	private String getFileContent(File file) throws TesseractException{

		String ext = getFileExtension(file);
		String result = "";
		logger.log(Level.INFO   ,"--- OCR OF "+file.getName()+" ---");

		if(ext!=""){
			if(ext.equals(".pdf")){
				File[] pdfFiles = PdfUtilities.convertPdf2Png(file);
				for (File pdfFile : pdfFiles) {
					result += doOCR(pdfFile);
				}
			}else{
				//.png .gpg .gpeg .bmp
				result = doOCR(file);
			} 
		}
		return result;
	}

	private String doOCR(File file) throws TesseractException{
		String result ="";
		try {
			String path = this.getClass().getClassLoader().getResource("").getPath();
			String fullPath = URLDecoder.decode(path, "UTF-8");
			fullPath = removeSlashIf(fullPath);
			System.out.println("doOCR path:"+fullPath);
			System.out.println(new File(fullPath+"//tessdata").listFiles().length);
			instance.setDatapath(fullPath+"/tessdata");
			result = instance.doOCR(file);
		} catch (Exception e) {
			logger.log(Level.INFO, e.getMessage(), e);
			throw new TesseractException(e);
		}
		return result;
	}

	private String removeSlashIf(String fullPath) {
        System.out.println(fullPath);
        if(fullPath.charAt(0) == '/'){
        	return fullPath.substring(1, fullPath.length());
           //String str[] = fullPath.split("/", fullPath.length());
           //return str[1];
        }
        return fullPath;
    }


	private String getFileExtension(File file) {
		String name = file.getName();
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return ""; // empty extension
		}if(!isExtAllow(name.substring(lastIndexOf))){
			logger.log(Level.INFO,"--- Ignore "+name+" ---");
			return ""; // ignore unknown files i.e. .svn etc
		}
		return name.substring(lastIndexOf);
	}

	private  boolean isExtAllow(String ext) {
		return getAllowExtension().contains(ext);
	}

	private  File[] getFiles() {
		return new File(getFileDirectory()).listFiles(); 
	}

	private  String getFileDirectory() {
		return fileDirectory;
	}

	/**
	 * Set fileDirectory to OCR and 
	 * default fileDirectory to samples/ as root path 
	 * @param fileDirectory as String , Its take up to folder and OCR Only this file It is allowExtension <br>
	 * default allowExtension .pdf .png .jpg .jpeg .bmp <br> If you want to change then use 
	 * {@link #setAllowExtension(String allowExtension)}
	 * @throws UnsupportedEncodingException 
	 * 
	 */
	public  void setFileDirectory(String fileDirectory) throws UnsupportedEncodingException {
		String path = FileHelper.class.getClassLoader().getResource("").getPath();
		String fullPath = URLDecoder.decode(path, "UTF-8");
		
		this.fileDirectory = fullPath+fileDirectory;
		System.out.println("setFileDirectory:::::::::::-"+this.fileDirectory);
	}

	private  String getAllowExtension() {
		return allowExtension;
	}

	/**
	 * Set Files Extensions to allow OCR <br>
	 * default allowExtension .pdf .png .jpg .jpeg .bmp .gif<br>
	 * set value as space separated
	 * @param allowExtension
	 */
	public  void setAllowExtension(String allowExtension) {
		this.allowExtension = allowExtension;
	}

	public HashMap<String, String> getFileName_NumeroId() {
		return fileName_NumeroId;
	}

	public void setFileName_NumeroId(HashMap<String, String> fileName_NumeroId) {
		this.fileName_NumeroId = fileName_NumeroId;
	}

}
