package org.papaorange.crawl.aproxyfinder.service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.papaorange.crawl.aproxyfinder.model.FreeProxy;
import org.papaorange.crawl.aproxyfinder.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KuaidailiProxyCollector
{

    class ProxyValidator extends Thread
    {
	// 从待验证队列中取出一个代理,如果验证ok,则保留代理列表,否则从列表中删除
	private void ValidateProxy()
	{
	    FreeProxy toValidate = null;
	    synchronized (this)
	    {
		toValidate = freeProxies.remove();
		log.debug("剩余未验证数量:" + freeProxies.size());
	    }

	    Connection connection = Jsoup.connect("http://www.baidu.com").proxy(toValidate.getIp(), Integer.parseInt(toValidate.getPort()));

	    try
	    {
		connection.timeout(10000).get();
		synchronized (toValidate)
		{
		    validProxies.add(toValidate);
		}
	    }
	    catch (IOException e)
	    {
		synchronized (this)
		{
		    freeProxies.remove(toValidate);
		    log.debug("代理失效,删除:" + toValidate.getIp() + ":" + toValidate.getPort());
		}
	    }
	}

	@Override
	public void run()
	{
	    while (true)
	    {
		synchronized (freeProxies)
		{
		    if (freeProxies.isEmpty())
		    {
			synchronized (threadCount)
			{
			    threadCount--;
			}
			break;
		    }
		}
		ValidateProxy();
	    }
	}
    }

    private final static Logger log = LoggerFactory.getLogger(KuaidailiProxyCollector.class);

    private static String baseUrl = "http://www.kuaidaili.com/proxylist/";

    private static LinkedList<FreeProxy> freeProxies = new LinkedList<>();

    private static LinkedList<FreeProxy> validProxies = new LinkedList<>();

    private static Integer threadCount = 100;

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

    public static void validate()
    {
	for (int i = 0; i < 100; i++)
	{
	    ProxyValidator validator = new KuaidailiProxyCollector().new ProxyValidator();
	    Thread thread = new Thread(validator);
	    thread.start();
	}
    }

    public static void waitForValidationComplete()
    {
	while (true)
	{
	    synchronized (threadCount)
	    {
		if (threadCount == 0)
		{
		    break;
		}
	    }
	    try
	    {
		Thread.sleep(10);
	    }
	    catch (InterruptedException e)
	    {
		e.printStackTrace();
	    }
	}
    }

    public static LinkedList<FreeProxy> getAllProxies()
    {
	return freeProxies;
    }

    public static LinkedList<FreeProxy> getAllValidProxies()
    {
	return validProxies;
    }

    public static void main(String[] args)
    {
	refresh();
	validate();
	waitForValidationComplete();
	for (FreeProxy proxy : getAllValidProxies())
	{
	    System.out.println(proxy.getIp() + ":" + proxy.getPort() + ":" + proxy.getLocation());
	}
    }
}
