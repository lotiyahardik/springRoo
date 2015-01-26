package com.ocr.solr.maincontroller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ocr.solr.SolrBean;
import com.ocr.solr.helper.FTPHelper;
import com.ocr.solr.helper.FileHelper;
import com.ocr.solr.helper.SolrHelper;
import com.ocr.solr.service.SolrService;

@Controller
public class CustomController {

	@Autowired SolrService solrService;
	SolrHelper solrHelper = SolrHelper.getInstance();
	

	@RequestMapping(value = "/ocrsolr",method=RequestMethod.GET)
	public String ocrsolr(Model uiModel){
		//start();
		 uiModel.addAttribute("solrBean", new SolrBean());
		return "ocrsolr";
	}
	
	@RequestMapping(value="/searchOcrDocument",method=RequestMethod.POST)
	public String searchOcrDocument(Model uiModel,
			@RequestParam(value = "numeroId", required = false) String numeroId) throws IOException{
		
		System.out.println(numeroId);
		uiModel.addAttribute("solrBean", new SolrBean());
		 
		 List<String> stringsList = new ArrayList<String>();
		 
		// solrHelper.intialize(new File("solr/index"));
		 solrHelper.openIndexSearcher();
		 List<String> fileNameList = solrHelper.getDocumentNameByNumeroId(numeroId);
		 for (String string : fileNameList) {
			System.out.println("Available File Name "+string);
			stringsList.add(string);
		 }
		 
		 solrHelper.closeDirectoryReader();
		 //solrHelper.closeDirectory();
		 
		 uiModel.addAttribute("stringsList", stringsList);
		 return "ocrsolr";
	}

	void start(){
		try{

			/*------------------------------ Download files from FTP Server ---------------------------*/

			FTPHelper ftpHelper = new FTPHelper("java", "shiva.dave"); //Initialized username and password for FTP 

			ftpHelper.setHost("192.168.1.115"); //must required in order to download files
			ftpHelper.setRemoteFolder("/hardik/"); //must required in order to download files

			ftpHelper.downloadFiles();

			/*------------------------------ SOLR Initialize ----------------------------------------------*/
			//if(solrHelper.isIntialize())
			solrHelper.intialize(new File("solr/index")); // use to store indexing procedure  

			/*------------------------------ OCR Process ----------------------------------------------*/
			FileHelper fileHelper = FileHelper.getInstance();
			fileHelper.setFileDirectory("SHIVAGOD/"); // set file directory for OCR process 
			ArrayList<SolrBean> solrBeanList = fileHelper.doProcess(); // process for fetching document data and prepared collection 
			for (SolrBean solrBean : solrBeanList) {
				solrService.saveSolrBean(solrHelper.addInfoToSolr(solrBean));
			}

			/*-------------------------------------Add static Data-----------------------------------*/

			solrService.saveSolrBean(solrHelper.addInfoToSolr("file8","95270693001"));
			solrService.saveSolrBean(solrHelper.addInfoToSolr("file7","95270693002"));

			solrService.saveSolrBean(solrHelper.addInfoToSolr(new SolrBean("file6", "95270693033", "hardik","R","Lotiya")));

			solrService.saveSolrBean(solrHelper.addInfoToSolr("file1","95270693004","Shakti","V","Vamja"));
			solrService.saveSolrBean(solrHelper.addInfoToSolr("file2","95270693005","Nirav","D","Lotiya"));
			solrService.saveSolrBean(solrHelper.addInfoToSolr("file3","95270693005","Ronak","D","Selarka"));
			solrService.saveSolrBean(solrHelper.addInfoToSolr("file4","95270693007","Chirag","p","panara"));
			solrService.saveSolrBean(solrHelper.addInfoToSolr("file5","95270693008","Virendra","L","Limbad"));

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
			//solrHelper.closeDirectory();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
