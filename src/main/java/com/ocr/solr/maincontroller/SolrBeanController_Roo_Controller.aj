// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ocr.solr.maincontroller;

import com.ocr.solr.SolrBean;
import com.ocr.solr.maincontroller.SolrBeanController;
import com.ocr.solr.service.SolrService;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect SolrBeanController_Roo_Controller {
    
    @Autowired
    SolrService SolrBeanController.solrService;
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String SolrBeanController.create(@Valid SolrBean solrBean, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, solrBean);
            return "solrbeans/create";
        }
        uiModel.asMap().clear();
        solrService.saveSolrBean(solrBean);
        return "redirect:/solrbeans/" + encodeUrlPathSegment(solrBean.getId_().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", produces = "text/html")
    public String SolrBeanController.createForm(Model uiModel) {
        populateEditForm(uiModel, new SolrBean());
        return "solrbeans/create";
    }
    
    @RequestMapping(value = "/{id_}", produces = "text/html")
    public String SolrBeanController.show(@PathVariable("id_") Long id_, Model uiModel) {
        uiModel.addAttribute("solrbean", solrService.findSolrBean(id_));
        uiModel.addAttribute("itemId", id_);
        return "solrbeans/show";
    }
    
    @RequestMapping(produces = "text/html")
    public String SolrBeanController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("solrbeans", SolrBean.findSolrBeanEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) solrService.countAllSolrBeans() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("solrbeans", SolrBean.findAllSolrBeans(sortFieldName, sortOrder));
        }
        return "solrbeans/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String SolrBeanController.update(@Valid SolrBean solrBean, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, solrBean);
            return "solrbeans/update";
        }
        uiModel.asMap().clear();
        solrService.updateSolrBean(solrBean);
        return "redirect:/solrbeans/" + encodeUrlPathSegment(solrBean.getId_().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id_}", params = "form", produces = "text/html")
    public String SolrBeanController.updateForm(@PathVariable("id_") Long id_, Model uiModel) {
        populateEditForm(uiModel, solrService.findSolrBean(id_));
        return "solrbeans/update";
    }
    
    @RequestMapping(value = "/{id_}", method = RequestMethod.DELETE, produces = "text/html")
    public String SolrBeanController.delete(@PathVariable("id_") Long id_, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        SolrBean solrBean = solrService.findSolrBean(id_);
        solrService.deleteSolrBean(solrBean);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/solrbeans";
    }
    
    void SolrBeanController.populateEditForm(Model uiModel, SolrBean solrBean) {
        uiModel.addAttribute("solrBean", solrBean);
    }
    
    String SolrBeanController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}
