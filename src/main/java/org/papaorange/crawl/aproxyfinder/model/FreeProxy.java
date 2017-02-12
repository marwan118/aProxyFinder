package org.papaorange.crawl.aproxyfinder.model;

public class FreeProxy
{
    private String ip;
    private String port;
    private boolean supportSSL;
    private boolean highAnonymous;
    private String location;
    private double delay;// 单位秒

    public String getIp()
    {
	return ip;
    }

    public void setIp(String ip)
    {
	this.ip = ip;
    }

    public String getPort()
    {
	return port;
    }

    public void setPort(String port)
    {
	this.port = port;
    }

    public boolean isSupportSSL()
    {
	return supportSSL;
    }

    public void setSupportSSL(boolean supportSSL)
    {
	this.supportSSL = supportSSL;
    }

    public boolean isHighAnonymous()
    {
	return highAnonymous;
    }

    public void setHighAnonymous(boolean highAnonymous)
    {
	this.highAnonymous = highAnonymous;
    }

    public String getLocation()
    {
	return location;
    }

    public void setLocation(String location)
    {
	this.location = location;
    }

    public double getDelay()
    {
	return delay;
    }

    public void setDelay(double delay)
    {
	this.delay = delay;
    }

}
