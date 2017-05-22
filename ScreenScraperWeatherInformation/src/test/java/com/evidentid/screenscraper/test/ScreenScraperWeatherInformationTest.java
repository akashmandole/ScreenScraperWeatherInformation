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
public class ScreenScraperWeatherInformationTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public ScreenScraperWeatherInformationTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(ScreenScraperWeatherInformationTest.class);
	}

	/*
	 * Check JSON object validity
	 */
	public void testScreenScraper() {
		ScreenScraperWeatherInformation screenScraperWeatherInformation = new ScreenScraperWeatherInformation();
		JSONObject obj = screenScraperWeatherInformation.screenScraper(
				"Atlanta, Georgia", "5/18/2017");

		String meanTemperatureActual = "79";
		String meanTemperatureAverage = "70";

		String maxTemperatureActual = "89";
		String maxTemperatureAverage = "81";
		String maxTemperatureRecord = "90";

		String minTemperatureActual = "68";
		String minTemperatureAverage = "59";
		String minTemperatureRecord = "44";

		assertNotNull("JSON data successfully fetched");

		if (obj != null) {

			assertEquals(obj.get(ScreenScraperWeatherInformation
					.getMeanTemperatureActual()), meanTemperatureActual);
			assertEquals(obj.get(ScreenScraperWeatherInformation
					.getMeanTemperatureAverage()), meanTemperatureAverage);

			assertEquals(obj.get(ScreenScraperWeatherInformation
					.getMaxTemperatureActual()), maxTemperatureActual);
			assertEquals(obj.get(ScreenScraperWeatherInformation
					.getMaxTemperatureAverage()), maxTemperatureAverage);
			assertEquals(obj.get(ScreenScraperWeatherInformation
					.getMaxTemperatureRecord()), maxTemperatureRecord);

			assertEquals(obj.get(ScreenScraperWeatherInformation
					.getMinTemperatureActual()), minTemperatureActual);
			assertEquals(obj.get(ScreenScraperWeatherInformation
					.getMinTemperatureAverage()), minTemperatureAverage);
			assertEquals(obj.get(ScreenScraperWeatherInformation
					.getMinTemperatureRecord()), minTemperatureRecord);

		}

	}

	/*
	 * Test Connection
	 */
	public void testConnectionObject() {
		try {
			ScreenScraperWeatherInformation screenScraperWeatherInformation = new ScreenScraperWeatherInformation();
			assertEquals(
					"Connection successfull",
					screenScraperWeatherInformation.getConnectionObject(
							"Atlanta, Georgia", "17", "5", "2017")
							.getResponseCode(), HttpURLConnection.HTTP_OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
