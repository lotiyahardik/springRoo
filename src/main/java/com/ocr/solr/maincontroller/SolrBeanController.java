package com.ocr.solr.maincontroller;
import com.ocr.solr.SolrBean;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/solrbeans")
@Controller
@RooWebScaffold(path = "solrbeans", formBackingObject = SolrBean.class)
public class SolrBeanController {
}
