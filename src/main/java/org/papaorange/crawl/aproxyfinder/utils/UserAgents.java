package org.papaorange.crawl.aproxyfinder.utils;

public class UserAgents
{
    private final static String[] userAgents = new String[] { "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36" };

    public static String getRandomUA()
    {
	int index = (int) (Math.random() * userAgents.length);
	return userAgents[index];
    }
}
