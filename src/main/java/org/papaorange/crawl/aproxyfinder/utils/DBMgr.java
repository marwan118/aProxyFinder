package org.papaorange.crawl.aproxyfinder.utils;

public class DBMgr
{

    private static DBAgent agent = null;

    public static DBAgent getDBAgent()
    {

	if (agent != null)
	{
	    return agent;
	}
	else
	{
	    agent = new DBAgent("papaorange.org", 27017, "proxy");
	    agent.connect();
	    return agent;
	}
    }

}
