package org.papaorange.crawl.aproxyfinder.service;

import java.util.HashMap;
import java.util.Map;

import org.papaorange.crawl.aproxyfinder.model.FreeProxy;
import org.papaorange.crawl.aproxyfinder.utils.DBAgent;
import org.papaorange.crawl.aproxyfinder.utils.DBMgr;
import org.springframework.scheduling.annotation.Scheduled;

public class RefreshService
{

    @Scheduled(fixedRate = 1000 * 60 * 3)
    public void refresh()
    {
	DBAgent agent = DBMgr.getDBAgent();

	KuaidailiProxyCollector.refresh();
	KuaidailiProxyCollector.validate();
	KuaidailiProxyCollector.waitForValidationComplete();
	agent.drop("valid");

	for (FreeProxy proxy : KuaidailiProxyCollector.getAllValidProxies())
	{
	    Map<String, Object> proxiesMap = new HashMap<>();
	    proxiesMap.put("proxy", proxy.getIp() + ":" + proxy.getPort());
	    proxiesMap.put("location", proxy.getLocation());
	    proxiesMap.put("ssl", proxy.isSupportSSL());
	    proxiesMap.put("HighAnonymous", proxy.isHighAnonymous());
	    agent.addOneDocument(proxiesMap, "valid");
	    System.out.println(proxy.getIp() + ":" + proxy.getPort() + ":" + proxy.getLocation());
	}
    }

}
