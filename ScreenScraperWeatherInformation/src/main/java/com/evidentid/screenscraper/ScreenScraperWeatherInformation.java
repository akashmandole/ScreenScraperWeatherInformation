package com.evidentid.screenscraper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * @autor Akash Mandole
 */
public class ScreenScraperWeatherInformation {

	private final static String baseURL = "https://www.wunderground.com";

	private final static String tableName = "historyTable";
	private final static String className = "wx-value";

	private final static String Mean_Temperature_Actual = "Mean Temperature Actual";
	private final static String Mean_Temperature_Average = "Mean Temperature Average";

	private final static String Max_Temperature_Actual = "Max Temperature Actual";
	private final static String Max_Temperature_Average = "Max Temperature Average";
	private final static String Max_Temperature_Record = "Max Temperature Record";

	private final static String Min_Temperature_Actual = "Min Temperature Actual";
	private final static String Min_Temperature_Average = "Min Temperature Average";
	private final static String Min_Temperature_Record = "Min Temperature Record";

	/*
	 * The function takes input as location and date and outputs a JSON
	 * representation of Temperature data.
	 */
	public JSONObject screenScraper(String location, String date) {

		// create a json object
		JSONObject jsonObject = null;

		String[] parts = date.split("/");
		String day = parts[1];
		String month = parts[0];
		String year = parts[2];
		try {

			HttpURLConnection conn = getConnectionObject(location, day, month,
					year);

			StringBuffer html = getHTML(conn);

			jsonObject = parseHTML(html.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	/*
	 * The function parses HTML document and creates a json object
	 */
	@SuppressWarnings("unchecked")
	private static JSONObject parseHTML(String html) {
		JSONObject jsonObject = new JSONObject();
		Document doc = Jsoup.parse(html.toString());

		// get all temperatures
		Element table = doc.getElementById(tableName);
		Elements rows = table.getElementsByClass(className);

		Element meanTemperatureActual = rows.get(0);
		Element meanTemperatureAverage = rows.get(1);

		Element maxTemperatureActual = rows.get(2);
		Element maxTemperatureAverage = rows.get(3);
		Element maxTemperatureRecord = rows.get(4);

		Element minTemperatureActual = rows.get(5);
		Element minTemperatureAverage = rows.get(6);
		Element minTemperatureRecord = rows.get(7);

		jsonObject.put(Mean_Temperature_Actual, meanTemperatureActual.text());
		jsonObject.put(Mean_Temperature_Average, meanTemperatureAverage.text());

		jsonObject.put(Max_Temperature_Actual, maxTemperatureActual.text());
		jsonObject.put(Max_Temperature_Average, maxTemperatureAverage.text());
		jsonObject.put(Max_Temperature_Record, maxTemperatureRecord.text());

		jsonObject.put(Min_Temperature_Actual, minTemperatureActual.text());
		jsonObject.put(Min_Temperature_Average, minTemperatureAverage.text());
		jsonObject.put(Min_Temperature_Record, minTemperatureRecord.text());

		return jsonObject;
	}

	/*
	 * The function reads the connection stream and returns a HTML document;
	 */
	private static StringBuffer getHTML(HttpURLConnection conn)
			throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String inputLine;
		StringBuffer html = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			html.append(inputLine);
		}
		in.close();
		return html;
	}

	/*
	 * The function creates HttpURLConnection object using location and date
	 */
	public HttpURLConnection getConnectionObject(String location, String day,
			String month, String year) throws IOException {

		StringBuilder forecastUrl = new StringBuilder(baseURL);
		forecastUrl.append("/cgi-bin/findweather/getForecast");

		URL obj = new URL(forecastUrl.toString());
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setReadTimeout(5000);
		conn.addRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.addRequestProperty("Accept-Encoding", "gzip, deflate, br");
		conn.addRequestProperty("Accept-Language", "en-US,en;q=0.5");
		conn.addRequestProperty("Connection", "keep-alive");
		conn.addRequestProperty("Host", "www.wunderground.com");
		conn.addRequestProperty("Referer",
				"https://www.wunderground.com/history/");

		conn.addRequestProperty("Upgrade-Insecure-Requests", "1");
		conn.addRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64; rv:53.0) Gecko/20100101 Firefox/53.0");

		// Adding params
		StringBuilder urlParameters = new StringBuilder(
				"airportorwmo=query&backurl=/history/index.html");
		urlParameters.append("&code=");
		urlParameters.append(URLEncoder.encode(location, "UTF-8"));
		urlParameters.append("&day=");
		urlParameters.append(URLEncoder.encode(day, "UTF-8"));
		urlParameters.append("&historytype=DailyHistory");
		urlParameters.append("&month=");
		urlParameters.append(URLEncoder.encode(month, "UTF-8"));
		urlParameters.append("&year=");
		urlParameters.append(URLEncoder.encode(year, "UTF-8"));

		conn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(urlParameters.toString());
		wr.flush();
		wr.close();

		boolean redirect = false;

		// normally, 3xx is redirect
		int status = conn.getResponseCode();
		if (status != HttpURLConnection.HTTP_OK) {
			if (status == HttpURLConnection.HTTP_MOVED_TEMP
					|| status == HttpURLConnection.HTTP_MOVED_PERM
					|| status == HttpURLConnection.HTTP_SEE_OTHER)
				redirect = true;
		}

		if (redirect) {

			// get redirect url from "location" header field
			StringBuilder redirectUrl = new StringBuilder(baseURL);
			redirectUrl.append(conn.getHeaderField("Location"));

			// get the cookie if need, for login
			String cookies = conn.getHeaderField("Set-Cookie");

			// open the new connnection again
			conn = (HttpURLConnection) new URL(redirectUrl.toString())
					.openConnection();
			conn.setRequestProperty("Cookie", cookies);
			conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			conn.addRequestProperty("User-Agent", "Mozilla");
			conn.addRequestProperty("Referer", "google.com");

		}
		return conn;
	}

	/*
	 * Input : argument 1 : location(example - Atlanta, Georgia), argument 2 :
	 * date mm/dd/yyyy (example - 5/17/2017)
	 */
	public static void main(String[] args) throws Exception {

		String location = null, date = null;

		if (args.length != 0) {
			location = args[0];
			date = args[1];
		}

		if (location == null || date == null) {
			throw new Exception("Input parameters missing");
		}

		ScreenScraperWeatherInformation screenScraperWeatherInformation = new ScreenScraperWeatherInformation();
		screenScraperWeatherInformation.screenScraper(location, date);
	}

	public static String getBaseurl() {
		return baseURL;
	}

	public static String getTablename() {
		return tableName;
	}

	public static String getClassname() {
		return className;
	}

	public static String getMeanTemperatureActual() {
		return Mean_Temperature_Actual;
	}

	public static String getMeanTemperatureAverage() {
		return Mean_Temperature_Average;
	}

	public static String getMaxTemperatureActual() {
		return Max_Temperature_Actual;
	}

	public static String getMaxTemperatureAverage() {
		return Max_Temperature_Average;
	}

	public static String getMaxTemperatureRecord() {
		return Max_Temperature_Record;
	}

	public static String getMinTemperatureActual() {
		return Min_Temperature_Actual;
	}

	public static String getMinTemperatureAverage() {
		return Min_Temperature_Average;
	}

	public static String getMinTemperatureRecord() {
		return Min_Temperature_Record;
	}

}
