package com.ocr.solr;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooJpaEntity(table = "SolrBean")
public class SolrBean {

	private int id;
	private String numeroId;
	private String fileName;
	private String fname;
	private String mname;
	private String lname;
	
	public SolrBean(String fileName,
			String numeroId,
			String fname,
			String mname,
			String lname) {
		this.fileName = fileName;
		this.numeroId = numeroId;
		this.fname = fname;
		this.mname = mname;
		this.lname = lname;
	}
	
}
