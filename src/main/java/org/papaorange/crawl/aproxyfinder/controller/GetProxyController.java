package org.papaorange.crawl.aproxyfinder.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.papaorange.crawl.aproxyfinder.utils.DBAgent;
import org.papaorange.crawl.aproxyfinder.utils.DBMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "proxy")
public class GetProxyController
{
    private DBAgent agent = null;
    private final static Logger log = LoggerFactory.getLogger(GetProxyController.class);

    public GetProxyController()
    {
	agent = DBMgr.getDBAgent();
    }

    @CrossOrigin
    @RequestMapping(value = "all")
    public List<Document> getNextClusterByRateValue() throws IOException
    {
	List<Document> ret = new ArrayList<>();
	ret = agent.getAllDocuments("valid");
	log.info("总共获取" + ret.size() + "条有效代理.");
	return ret;
    }

}
