package com.ocr.solr.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;

import com.ocr.solr.SolrBean;
import com.ocr.solr.repository.SolrRepository;
import com.ocr.solr.service.SolrService;

public class SolrHelper {
	
	@Autowired
	SolrService solrService;
	
	private final Logger logger = Logger.getLogger(FileHelper.class.getName());
	
	public static Analyzer analyzer;
	public IndexWriter indexWriter = null;
	public Directory directory = null;
	public IndexSearcher indexSearcher = null; 
	public QueryParser parser = null;
	public DirectoryReader directoryReader = null;

	public static final SolrHelper INSTANCE = new SolrHelper();
	
	public static List<String> fileNameList =null;
	
	IndexWriterConfig indexWriterConfig =null;

	// Don't let anyone else instantiate this class
	private SolrHelper(){}

	/** Don't let anyone else instantiate this class */
	public static SolrHelper getInstance() {
		return INSTANCE;
	}

	/**
	 * required to analyzer initialization using this block  
	 * when @link getInstance() method is called before call this Initializer block
	 * 
	 */
	static{
		analyzer = new StandardAnalyzer();
	}

	/**
	 * This method is used to initialize  three main classes
	 * <br>Directory ,
	 * <br>IndexWriterConfig and 
	 * <br>indexWriter.
	 * <br>with this method default set Directory to "solr/index"
	 * @see 
	 * {@link #intialize(File file)} 
	 */
	public void initialize() {
		intialize(new File("solr/index"));
	}

	/**
	 * This method is used to initialize  three main classes
	 * <br>Directory ,
	 * <br>IndexWriterConfig and 
	 * <br>indexWriter.
	 * @param 
	 * {@link java.io.File} to create a file Directory to store Indexes
	 * @see 
	 * {@link #initialize()} 
	 */
	public void intialize(File file) {
		logger.log(Level.INFO,"--- intialize the Solr ---");
		try {
			directory = FSDirectory.open(file);
			  // Store the index in memory:
		    //Directory directory = new RAMDirectory();
			removeUnusedIndex(file);
			indexWriterConfig = new IndexWriterConfig(Version.LATEST, analyzer);
			indexWriter = new IndexWriter(directory, indexWriterConfig);
		}catch (IOException e) {
			logger.log(Level.INFO,"--- Solr Intialize IOException  ---\n");
		} catch (Exception e) {
			logger.log(Level.INFO,"--- Solr Intialize Exception  ---\n");
		}
	}

	/**
	 * Remove Unused Indexes
	 * */
	private void removeUnusedIndex(File file) {
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for (File file2 : files) {
				file2.delete();
			}
		}
	}

	/**
	 * 
	 * closed IndexWriter if unlocked(does not in use)
	 * generally method called before use {@link #openIndexSercher}
	 * because in that method open directoryReader and indexSercher
	 * @throws IOException if there is a low-level IO error
	 * @see
	 * {@link #openIndexSearcher()}
	 */
	private void forceClosedIndexWriter() throws IOException {
		if(IndexWriter.isLocked(directory)) indexWriter.close(); 
	}

	/**
	 * openIndexSearcher() is used to Initialized with before created Indexes
	 * using DirectoryReader and IndexSearcher
	 * <br>and indexSearcher will be used to fetch data from indexes using queries.
	 * @throws IOException if there not exist file or a directory for indexSercher
	 * */	
	public void openIndexSearcher() {
		logger.log(Level.INFO,"--- creating or opening Solr Indexes ---");
		try {
			forceClosedIndexWriter();
			directoryReader = DirectoryReader.open(directory);
			indexSearcher = new IndexSearcher(directoryReader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<String> getDocumentNameByNumeroId(String NumeroId) throws IOException {
		
		//openIndexSearcher();
		fileNameList = new ArrayList<String>();
		try {
			System.out.println("Search Query :"+NumeroId+"\n");
			parser = new QueryParser("numeroId", analyzer);
			//Query query = parser.parse(NumeroId+"~");
			Query query = parser.parse(NumeroId);
			ScoreDoc[] hits = indexSearcher.search(query, null, 1000).scoreDocs;
			for (ScoreDoc scoreDoc : hits) {
				Document hitDoc = indexSearcher.doc(scoreDoc.doc);
				fileNameList.add(hitDoc.get("fileName"));
				//System.out.println(hitDoc.get("fileName"));
			}
			return fileNameList;
		} catch (IOException e) {
			// TODO Auto-generated catch block for indexSearcher   
			e.printStackTrace();
		}catch (ParseException e) {
			// TODO Auto-generated catch block misplayed for query
			e.printStackTrace();
		}
		/*closeDirectoryReader();
		closeDirectory();*/
		return null;
	}

	public List<String> getDocumentNameByFirstNameORMiddleNameORLastName(String strQuery) throws IOException, ParseException {
		
		fileNameList = new ArrayList<String>();
		
		System.out.println("Search Query :"+strQuery+"\n");
		Query query = parse(Version.LATEST,
							strQuery,
							new String[]{"fname","mname","lname"},
							analyzer);

		TopDocs topDocs = indexSearcher.search(query,10);
		ScoreDoc[] scoreDosArray = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDosArray) {
			//Retrieve the matched document and show relevant details
			Document doc = indexSearcher.doc(scoreDoc.doc);
			//System.out.println(doc.get("fileName"));
			fileNameList.add(doc.get("fileName"));
		}

		return fileNameList.size()>0 ? fileNameList : null;
	}

	/**
	 * Closes files associated with this index.
	 * Also saves any new deletions to disk.
	 * No other methods should be called after this has been called.
	 * @throws IOException if there is a low-level IO error
	 */
	public void closeDirectoryReader() throws IOException{
		directoryReader.close();
	}

	/** 
	 * Closes the Directory. 
	 * @throws IOException if there is a low-level IO error
	 * */
	public void closeDirectory() throws IOException {
		directory.close();
	}

	/**
	 * Commits all changes to an index, waits for pending merges
	 * to complete, closes all associated files and releases the
	 * write lock.  
	 * @throws IOException if there is a low-level IO error
	 */
	public void closeIndexWriter()throws IOException {
		indexWriter.close();
	}
	
	/*----------------START------------------------------------------- ADD INFORAMTION TO DOCUMENT -----------------------------------------------START----------*/
	
	public SolrBean addInfoToSolr(String fileName,String numeroId,String fname, String mname,String lname) throws IOException {
		//System.out.println(solrService == null);
		//solrService.saveSolrBean(new SolrBean(fileName, numeroId, fname,mname,lname));
		/*if(!HibernateUtil.save(new SolrBean(fileName, numeroId, fname,mname,lname))){
			logger.log(Level.INFO,"--- fail to insert inforamtion to Database ---");
		}*/
		
		logger.log(Level.INFO,"--- insert inforamtion to Solr ---");
		
		Document doc = new Document();
		doc.add(new Field("fileName", fileName, TextField.TYPE_STORED));
		doc.add(new Field("fname", fname, TextField.TYPE_STORED));
		doc.add(new Field("name", mname, TextField.TYPE_STORED));
		doc.add(new Field("lname", lname, TextField.TYPE_STORED));
		doc.add(new Field("numeroId", numeroId, TextField.TYPE_STORED));

		try {
			indexWriter.addDocument(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new SolrBean(fileName, numeroId, fname,mname,lname);
	}

	public SolrBean addInfoToSolr(SolrBean solrBean) throws IOException{

		return addInfoToSolr(solrBean.getFileName(),solrBean.getNumeroId(),solrBean.getFname(), solrBean.getMname(),solrBean.getLname());
		
	}
	
	public SolrBean addInfoToSolr(String fileName,String numeroId) throws IOException {
		
		return addInfoToSolr(fileName,numeroId,"","","");
		
	}

	/*----------------END------------------------------------------- ADD INFORAMTION TO DOCUMENT -----------------------------------------------END----------*/
	
	/**
	 * This method is copy from the MultiFieldQueryParser.parse method 
	 * and custom it for fetching data from document  
	 * 
	 * */
	public static Query parse(Version matchVersion, String queries, String[] fields, Analyzer analyzer) throws ParseException
	{
		
		/*
		 * here match version is used for matching Version 
		 * query variable is used for finding the data and 
		 * its compare with array of fields
		 * 
		 * */
		
		/*
		 * This is old style to fetch data 
		 * It's Recommended queries field and compare field length are same 
		 * like queries.length == fields.length
		 * Query query = MultiFieldQueryParser.parse(new String[]{lname,lname},
		        new String[]{"fname", "lname"},
		        new SimpleAnalyzer())
		 *       
		 */
		
		/*if (queries.length != fields.length)
			throw new IllegalArgumentException("queries.length != fields.length");*/
		BooleanQuery bQuery = new BooleanQuery();
		for (int i = 0; i < fields.length; i++)
		{
			QueryParser qp = new QueryParser(matchVersion, fields[i], analyzer);
			Query q = qp.parse(queries);
			if (q!=null && // q never null, just being defensive
					(!(q instanceof BooleanQuery) || ((BooleanQuery)q).getClauses().length>0)) {
				bQuery.add(q, BooleanClause.Occur.SHOULD);
			}
		}
		return bQuery;
	}

	public boolean isIntialize(){
		return directory==null;
	}

}
