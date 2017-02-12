package org.papaorange.crawl.aproxyfinder.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils
{

    private final static Logger log = LoggerFactory.getLogger(Utils.class);

    public static Map<String, String> getCookies(String url) throws IOException
    {
	Map<String, String> cookies = new HashMap<>();

	URL getUrl = new URL(url);
	// 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，
	// 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
	HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();

	System.out.println(connection.getResponseCode());
	System.out.println(connection.getHeaderField("Set-Cookie"));
	// InputStream stream = connection.getInputStream();
	// String responseCookie = connection.getHeaderField("Set-Cookie");//
	// 取到所用的Cookie

	// stream.close();
	System.out.println(connection);
	// 断开连接
	connection.disconnect();

	return cookies;
    }

    public static Document httpGetWithCookies(String url, Map<String, String> cookies) throws IOException
    {
	log.debug("Download URL:" + url);
	try
	{
	    Response res = Jsoup.connect(url).timeout(3000).execute();
	}
	catch (Exception e)
	{
	    // TODO: handle exception
	}

	return Jsoup.connect(url).cookies(cookies).ignoreContentType(true).header("Accept", "text/html").header("Accept-Charset", "utf-8").header("Accept-Encoding", "gzip")
		.header("Accept-Language", "zh-cn").header("User-Agent", UserAgents.getRandomUA()).timeout(0).get();

    }

    public static Document httpGet(String url) throws IOException
    {
	log.debug("Download URL:" + url);

	return Jsoup.connect(url).ignoreContentType(true).header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8").header("Accept-Language", "zh-CN,zh;q=0.8")
		.header("Accept-Encoding", "gzip, deflate, sdch").header("Accept-Language", "zh-cn").header("User-Agent", UserAgents.getRandomUA()).timeout(0).get();

    }

    public static Document httpPost(String url, Map<String, String> map, String cookie) throws IOException
    {
	// 获取请求连接
	Connection con = Jsoup.connect(url);
	con.ignoreContentType(true).header("Accept", "text/html").header("Accept-Charset", "utf-8").header("Accept-Encoding", "gzip").header("Accept-Language", "en-US,en")
		.header("User-Agent", UserAgents.getRandomUA()).timeout(0);
	// 遍历生成参数
	if (map != null)
	{
	    for (Entry<String, String> entry : map.entrySet())
	    {
		// 添加参数
		con.data(entry.getKey(), entry.getValue());
	    }
	}
	// 插入cookie（头文件形式）
	if (cookie != null)
	{
	    con.header("Cookie", cookie);
	}
	Document doc = con.post();
	return doc;
    }

    public static InputStream getInputStreamFromHttpPost(String url, Map<String, String> map, String cookie) throws IOException
    {
	// 获取请求连接

	Connection con = Jsoup.connect(url);
	con.ignoreContentType(true).header("Accept", "text/html").header("Accept-Charset", "utf-8").header("Accept-Encoding", "gzip").header("Accept-Language", "en-US,en")
		.header("User-Agent", UserAgents.getRandomUA()).timeout(0);
	// 遍历生成参数
	if (map != null)
	{
	    for (Entry<String, String> entry : map.entrySet())
	    {
		// 添加参数
		con.data(entry.getKey(), entry.getValue());
	    }
	}
	// 插入cookie（头文件形式）
	if (cookie != null)
	{
	    con.header("Cookie", cookie);
	}

	return null;
    }

    // eg:129
    public static String matchRuntime(String str)
    {
	String runtime = "";
	try
	{
	    Pattern p = Pattern.compile("[0-9]+");
	    Matcher matcher = p.matcher(str);
	    while (matcher.find())
	    {
		runtime = matcher.group();
	    }
	}
	catch (Exception e)
	{
	}
	return runtime;
    }

    // eg:2016
    public static String matchYear(String str)
    {
	String year = "";
	try
	{
	    Pattern p = Pattern.compile("[0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}");
	    Matcher matcher = p.matcher(str);
	    while (matcher.find())
	    {
		year = matcher.group();
	    }
	}
	catch (Exception e)
	{
	}
	return year;
    }

    // eg:2016-01-23
    public static String matchDate(String dateStr)
    {
	String date = "";
	try
	{
	    Pattern p = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
	    Matcher matcher = p.matcher(dateStr);
	    while (matcher.find())
	    {
		date = matcher.group();
	    }
	}
	catch (Exception e)
	{
	}
	return date;
    }

    // public static void main(String[] args)
    // {
    // System.out.println(matchYear("5652-12-12"));
    // }
}
