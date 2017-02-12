package org.papaorange.crawl.aproxyfinder.service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.papaorange.crawl.aproxyfinder.model.FreeProxy;
import org.papaorange.crawl.aproxyfinder.utils.Utils;

public class KuaidailiProxyCollector
{
    private static String baseUrl = "http://www.kuaidaili.com/proxylist/";

    private static List<FreeProxy> freeProxies = new LinkedList<>();

    private static Document loadDocFromUrl(String url)
    {

	Document document = null;
	try
	{
	    document = Utils.httpGet(url);
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
	return document;
    }

    /**
     * 从当前dom中解析出所有匿名proxy,透明代理忽略 IP PORT 匿名度 类型 get/post支持 位置 响应速度 最后验证时间
     * 117.90.2.31 9000 高匿名 HTTP GET, POST 中国 江苏省 镇江市 电信 0.9秒 31分钟前
     */
    private static List<FreeProxy> parseProxyFromDocument(Document doc)
    {
	LinkedList<FreeProxy> ret = new LinkedList<>();

	int trSize = doc.select("tr").size();

	for (int i = 1; i < trSize; i++)
	{
	    FreeProxy fProxy = new FreeProxy();

	    Elements ip = doc.select("#index_free_list > table > tbody > tr:nth-child(" + i + ") > td:nth-child(1)");

	    Elements port = doc.select("#index_free_list > table > tbody > tr:nth-child(" + i + ") > td:nth-child(2)");

	    Elements anonymous = doc.select("#index_free_list > table > tbody > tr:nth-child(" + i + ") > td:nth-child(3)");

	    Elements supportSSL = doc.select("#index_free_list > table > tbody > tr:nth-child(" + i + ") > td:nth-child(4)");

	    Elements location = doc.select("#index_free_list > table > tbody > tr:nth-child(" + i + ") > td:nth-child(6)");

	    fProxy.setIp(ip.size() > 0 ? ip.get(0).text() : "");
	    fProxy.setPort(port.size() > 0 ? port.get(0).text() : "");
	    fProxy.setLocation(location.size() > 0 ? location.get(0).text() : "");

	    fProxy.setHighAnonymous(false);// 默认非高匿名
	    if (anonymous.size() > 0)
	    {
		String text = anonymous.get(0).text();
		if (text.contains("透明"))
		{
		    // 透明代理直接忽略
		    continue;
		}
		if (text.contains("高"))
		{
		    fProxy.setHighAnonymous(true);
		}
	    }

	    fProxy.setSupportSSL(false);// 默认不支持SSL
	    if (supportSSL.size() > 0)
	    {
		String text = supportSSL.text();

		if (text.contains("HTTPS"))
		{
		    fProxy.setSupportSSL(true);
		}
	    }
	    ret.add(fProxy);
	}
	return ret;
    }

    public static void refresh()
    {
	// 扫描前十页
	for (int i = 0; i < 10; i++)
	{
	    List<FreeProxy> proxiesInOnePage = parseProxyFromDocument(loadDocFromUrl(baseUrl + (i + 1) + "/"));
	    freeProxies.addAll(proxiesInOnePage);
	}
    }

    public static List<FreeProxy> getAllProxies()
    {
	return freeProxies;
    }

    public static void main(String[] args)
    {

	refresh();
	List<FreeProxy> freeProxies = getAllProxies();

	for (FreeProxy freeProxy : freeProxies)
	{
	    System.out.println(freeProxy.getIp() + ":" + freeProxy.getPort() + ":" + freeProxy.getLocation());
	}
    }
}
