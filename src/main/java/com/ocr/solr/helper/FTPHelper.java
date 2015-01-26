package com.ocr.solr.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

public class FTPHelper{

	private final static Logger logger = Logger.getLogger(FTPHelper.class.getName());

	private String host;
	private final String username;
	private final String password;
	
	private String remoteFolder = "/hardik/"; //download files remote folder
	
	private static String DOWNLOAD_FTP_DOLDER = "/SHIVAGOD/";
	private static final String UPLOAD_FTP_DOLDER = "FTPUploadFiles\\";

	private static final int PORT = 21;
	static FtpServerFactory serverFactory ;
	static PropertiesUserManagerFactory userManagerFactory ;
	static FtpServer server ;

	@SuppressWarnings("unused")
	private FTPHelper(){
		this(null, null);
	}
	
	public FTPHelper(String username,String password) {
		this.username = username;
		this.password = password;
	}

	static FTPClient ftpClient = null;

	public static void main(String[] args) {
		//FTPHelper.uploadFiles();
		//FTPHelper.downloadFiles();
	}

	public void downloadFiles() throws UnsupportedEncodingException {
		
		if(validHost(getHost())){
			List<String> nameList   = getNameList();
			try {
				ftpClient = new FTPClient();
				ftpClient.connect(host, PORT);
				if (ftpClient.login(username, password)) {
					
					ftpClient.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
					ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
					
					/*
					 * above two methods are required after sucessfull login named like 
					 * setFileType() and  setFileTransferMode()
					 * 
					 * */
					
					FTPFile[] names =  ftpClient.listFiles(remoteFolder);
					for (FTPFile ftpFile : names) {
						String fileName = ftpFile.getName();
						if(!nameList.contains(fileName)){
							FileOutputStream foss = new FileOutputStream(DOWNLOAD_FTP_DOLDER+fileName);
							boolean downloads = ftpClient.retrieveFile(remoteFolder+fileName, foss);
							if (downloads) 
								logger.log(Level.INFO,"--- File '"+fileName+"' downloaded successfully ! ---");
							else 
								logger.log(Level.INFO,"--- Error in downloading '"+fileName+"' file ! ---");
						}else{
							//System.out.println(fileName +" File exist");
						}
					}
				}else{
					logger.log(Level.INFO,"--- FTP Connection Problem ! ---");
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					ftpClient.logout();
					ftpClient.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			logger.log(Level.INFO,"--- Host Can not be null ! ---");
		}
		
		DOWNLOAD_FTP_DOLDER = null;
	}

	public synchronized void uploadFiles() {
		
		if(validHost(getHost())){
			try {
				ftpClient = new FTPClient();
				ftpClient.connect(getHost(), PORT);
				if (ftpClient.login(username, password)) {
					File[]  uploadFiles = new File(UPLOAD_FTP_DOLDER).listFiles();
					for (File file : uploadFiles) {
						synchronized (file) {
							InputStream inputStream = new FileInputStream(UPLOAD_FTP_DOLDER+file.getName());
							System.out.println("Start uploading first file");
							boolean done = ftpClient.storeFile("/timepass/"+file.getName(), inputStream);
							inputStream.close();
							if (done) {
								System.out.println("Uploaded successfully.");
							}
						}	
					}
				}else{
					logger.log(Level.INFO,"--- FTP Connection Problem ! ---");
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					ftpClient.logout();
					ftpClient.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			logger.log(Level.INFO,"--- Host Can not be null ! ---");
		}
	}

	private boolean validHost(String host2) {
		
		try {
			InetAddress.getByName(host2);
		} catch (UnknownHostException e) {
			logger.log(Level.INFO,"--- '"+e+"' ! ---");
			return false;
		}
		
		return host2!=null;
	}

	private static List<String> getNameList() throws UnsupportedEncodingException {
		List<String> nameList = new ArrayList<String>();
//		System.out.println(FTPHelper.class.getClassLoader().getResource("/").get);
		String path = FTPHelper.class.getClassLoader().getResource("").getPath();
		String fullPath = URLDecoder.decode(path, "UTF-8");
		/*String pathArr[] = fullPath.split("/WEB-INF/classes/"+DOWNLOAD_FTP_DOLDER);
		System.out.println(fullPath);
		System.out.println(pathArr[0]);*/
		System.out.println(fullPath+DOWNLOAD_FTP_DOLDER);
		
		DOWNLOAD_FTP_DOLDER = fullPath+DOWNLOAD_FTP_DOLDER;
		
		System.out.println("FTPDownloadFiles :"+DOWNLOAD_FTP_DOLDER);
		
		if(! new File(DOWNLOAD_FTP_DOLDER).isDirectory()){
			boolean a = new File(DOWNLOAD_FTP_DOLDER).mkdir();
			System.out.println(a);
		}
		File[] files = new File(DOWNLOAD_FTP_DOLDER).listFiles();
		for (File file : files) {
			nameList.add(file.getName());
		}
		System.out.println("File Size : "+nameList.size());
		return nameList;
	}

	private String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setRemoteFolder(String remoteFolder) {
		this.remoteFolder = remoteFolder;
	}

}
