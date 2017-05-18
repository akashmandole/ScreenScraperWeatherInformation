package com.evidentid.screenscraper.test;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.json.simple.JSONObject;

import com.evidentid.screenscraper.ScreenScraperWeatherInformation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for ScreenScrapper
 */
public class ScreenScraperWeatherInformationTest extends TestCase{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ScreenScraperWeatherInformationTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ScreenScraperWeatherInformationTest.class );
    }

    /*
     * Check JSON object validity
     */
    public void testScreenScraper()
    {
    	ScreenScraperWeatherInformation screenScraperWeatherInformation = new ScreenScraperWeatherInformation();
    	JSONObject obj = screenScraperWeatherInformation.screenScraper("Atlanta, Georgia", "5/17/2017");
    	
    	assertNotNull("JSON data successfully fetched");
    	
    	assertTrue(obj.containsKey("Mean Temperature Actual"));
    	assertTrue(obj.containsKey("Mean Temperature Average"));
    	
    	assertTrue(obj.containsKey("Max Temperature Actual"));
    	assertTrue(obj.containsKey("Max Temperature Average"));
    	assertTrue(obj.containsKey("Max Temperature Record"));
    	
    	assertTrue(obj.containsKey("Min Temperature Actual"));
    	assertTrue(obj.containsKey("Min Temperature Average"));
    	assertTrue(obj.containsKey("Min Temperature Record"));
    	
    }
    
    /*
     * Test Connection
     */
    public void testConnectionObject()
    {
    	try {
    		ScreenScraperWeatherInformation screenScraperWeatherInformation = new ScreenScraperWeatherInformation();
			assertEquals("Connection successfull",screenScraperWeatherInformation.getConnectionObject("Atlanta, Georgia", "17","5","2017").getResponseCode(),HttpURLConnection.HTTP_OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}

