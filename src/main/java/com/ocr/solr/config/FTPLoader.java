package com.ocr.solr.config;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.sourceforge.tess4j.TesseractException;

import com.ocr.solr.helper.FTPHelper;


public class FTPLoader{

	private String userName;
	private String password;
	private String host;
	private String remoteFolder;
	
	public FTPLoader(){}
	
	public FTPLoader(String userName,String password,String host,String remoteFolder) throws IOException, TesseractException{
		
		this.userName = userName;
		this.password = password;
		this.host = host;
		this.remoteFolder = remoteFolder;
		
		ftpIntilize();
		System.out.println("FTPLoader called..................000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"+userName);
	}
	

	private void ftpIntilize()throws UnsupportedEncodingException {
		
		FTPHelper ftpHelper = new FTPHelper(userName, password); //Initialized username and password for FTP 
		ftpHelper.setHost(host); //must required in order to download files
		ftpHelper.setRemoteFolder(remoteFolder); //must required in order to download files
		ftpHelper.downloadFiles();
	}

	public void init() {
		// TODO Auto-generated method stub
		System.out.println("init");

	}
}
