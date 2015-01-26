import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ocr.solr.SolrBean;
import com.ocr.solr.helper.FTPHelper;
import com.ocr.solr.helper.FileHelper;
import com.ocr.solr.helper.SolrHelper;


public class AllSolrUtilities {

	public static void main(String[] args) {
		
	try{
		
		/*------------------------------ Download files from FTP Server ---------------------------*/
		
		//FTPHelper1.createServer("D:/hardik/").startServer();
		
		FTPHelper ftpHelper = new FTPHelper("java", "shiva.dave"); //Initialized username and password for FTP 
		
		ftpHelper.setHost("192.168.1.115"); //must required in order to download files
		ftpHelper.setRemoteFolder("/hardik/"); //must required in order to download files
		
		ftpHelper.downloadFiles();
		
		/*------------------------------ SOLR Initialize ----------------------------------------------*/
		SolrHelper solrHelper = SolrHelper.getInstance();
		
		solrHelper.intialize(new File("solr/index")); // use to store indexing procedure  
		
		/*------------------------------ OCR Process ----------------------------------------------*/
		FileHelper fileHelper = FileHelper.getInstance();
		fileHelper.setFileDirectory("FTPDownloadFiles"); // set file directory for OCR process 
		//fileHelper.doProcess(); // process for fetching document data and prepared collection 
		
		ArrayList<SolrBean> solrBeanList = fileHelper.doProcess(); // process for fetching document data and prepared collection 
		for (SolrBean solrBean : solrBeanList) {
			solrHelper.addInfoToSolr(solrBean);
		}
		
		/*-------------------------------------Add static Data-----------------------------------*/
		
		solrHelper.addInfoToSolr("file8","95270693001");
		solrHelper.addInfoToSolr("file7","95270693002");
		
		solrHelper.addInfoToSolr(new SolrBean("file6", "95270693033", "hardik","R","Lotiya"));
		
		solrHelper.addInfoToSolr("file1","95270693004","Shakti","V","Vamja");
		solrHelper.addInfoToSolr("file2","95270693005","Nirav","D","Lotiya");
		solrHelper.addInfoToSolr("file3","95270693005","Ronak","D","Selarka");
		solrHelper.addInfoToSolr("file4","95270693007","Chirag","p","panara");
		solrHelper.addInfoToSolr("file5","95270693008","Virendra","L","Limbad");
		
		solrHelper.closeIndexWriter();
		/*-------------------------------------Add static Data-----------------------------------END-*/
		
		/*-------------------------------------Now search the index:-----------------------------------*/
		
		solrHelper.openIndexSearcher();
		List<String> fileNameList = solrHelper.getDocumentNameByNumeroId("80889257");
		for (String string : fileNameList) {
			System.out.println("Available File Name "+string);
		}
		
		System.out.print("\n");
		fileNameList = solrHelper.getDocumentNameByNumeroId("95270693001");
		for (String string : fileNameList) {
			System.out.println("Available File Name "+string);
		}
		
		System.out.print("\n");
		fileNameList = solrHelper.getDocumentNameByFirstNameORMiddleNameORLastName("Lotiya");
		for (String string : fileNameList) {
			System.out.println("Available File Name "+string);
		}
		
		solrHelper.closeDirectoryReader();
		solrHelper.closeDirectory();
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	}
}

