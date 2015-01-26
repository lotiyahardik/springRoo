package com.ocr.solr.repository;
import com.ocr.solr.SolrBean;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = SolrBean.class)
public interface SolrRepository {
}
