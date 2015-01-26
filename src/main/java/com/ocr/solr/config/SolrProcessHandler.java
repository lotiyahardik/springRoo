package com.ocr.solr.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.sourceforge.tess4j.TesseractException;

import com.ocr.solr.SolrBean;
import com.ocr.solr.helper.FileHelper;
import com.ocr.solr.helper.SolrHelper;

public class SolrProcessHandler {

	public SolrProcessHandler(){}

	public SolrProcessHandler(String solrIndexDirectory,String downloadFileDirectory) throws IOException, TesseractException{
		solrIntializeAndOcrDocuments(solrIndexDirectory,downloadFileDirectory);
	}

	private void solrIntializeAndOcrDocuments(String solrIndexDirectory,String downloadFileDirectory) throws IOException, TesseractException {

		/*------------------------------ SOLR Initialize ----------------------------------------------*/
		SolrHelper solrHelper = SolrHelper.getInstance();

		solrHelper.intialize(new File(solrIndexDirectory)); // use to store indexing procedure  

		/*------------------------------ OCR Process ----------------------------------------------*/
		
		FileHelper fileHelper = FileHelper.getInstance();
		fileHelper.setFileDirectory(downloadFileDirectory); // set file directory for OCR process 
		//fileHelper.doProcess(); // process for fetching document data and prepared collection 

		ArrayList<SolrBean> solrBeanList = fileHelper.doProcess(); // process for fetching document data and prepared collection 
		for (SolrBean solrBean : solrBeanList) {
			solrHelper.addInfoToSolr(solrBean);
		}

	}
}
