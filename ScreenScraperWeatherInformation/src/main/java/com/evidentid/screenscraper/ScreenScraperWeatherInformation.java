package com.evidentid.screenscraper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * @autor Akash Mandole
 */
public class ScreenScraperWeatherInformation {

	String baseURL = "https://www.wunderground.com";

	/*
	 * The function takes input as location and date and outputs a JSON representation of Temperature data.
	 */
	public JSONObject screenScraper(String location, String date) {

		// create a json object
		JSONObject jsonObject = null;

		String[] parts = date.split("/");
		String day = parts[1];
		String month = parts[0];
		String year = parts[2];
		try {

			HttpURLConnection conn = getConnectionObject(location,day,month,year);

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
	public static JSONObject parseHTML(String html) {
		JSONObject jsonObject = new JSONObject();
		Document doc = Jsoup.parse(html.toString());
		
		//get title 
		 System.out.println("title : " + doc.title());

		// get all temperatures
		Element table = doc.getElementById("historyTable");
		Elements rows = table.getElementsByClass("wx-value");

		Element meanTemperatureActual = rows.get(0);
		Element meanTemperatureAverage = rows.get(1);

		Element maxTemperatureActual = rows.get(2);
		Element maxTemperatureAverage = rows.get(3);
		Element maxTemperatureRecord = rows.get(4);

		Element minTemperatureActual = rows.get(5);
		Element minTemperatureAverage = rows.get(6);
		Element minTemperatureRecord = rows.get(7);

		jsonObject.put("Mean Temperature Actual",
				meanTemperatureActual.text());
		jsonObject.put("Mean Temperature Average",
				meanTemperatureAverage.text());

		jsonObject.put("Max Temperature Actual",
				maxTemperatureActual.text());
		jsonObject.put("Max Temperature Average",
				maxTemperatureAverage.text());
		jsonObject.put("Max Temperature Record",
				maxTemperatureRecord.text());

		jsonObject.put("Min Temperature Actual",
				minTemperatureActual.text());
		jsonObject.put("Min Temperature Average",
				minTemperatureAverage.text());
		jsonObject.put("Min Temperature Record",
				minTemperatureRecord.text());

		return jsonObject;
	}

	/*
	 * The function reads the connection stream and returns a HTML document;
	 */
	public static StringBuffer getHTML(HttpURLConnection conn) throws IOException {
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
	public HttpURLConnection getConnectionObject(String location,
			String day, String month, String year) throws IOException {
		
		URL obj = new URL(baseURL + "/cgi-bin/findweather/getForecast");
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
		String urlParameters = "airportorwmo=query&backurl=/history/index.html&code="
				+ location
				+ "&day="
				+ day
				+ "&historytype=DailyHistory&month="
				+ month
				+ "&year="
				+ year;
		conn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		System.out.println("Request URL ... " + baseURL);

		boolean redirect = false;

		// normally, 3xx is redirect
		int status = conn.getResponseCode();
		if (status != HttpURLConnection.HTTP_OK) {
			if (status == HttpURLConnection.HTTP_MOVED_TEMP
					|| status == HttpURLConnection.HTTP_MOVED_PERM
					|| status == HttpURLConnection.HTTP_SEE_OTHER)
				redirect = true;
		}

		System.out.println("Response Code ... " + status);

		if (redirect) {

			// get redirect url from "location" header field
			String newUrl = conn.getHeaderField("Location");

			// get the cookie if need, for login
			String cookies = conn.getHeaderField("Set-Cookie");

			// open the new connnection again
			conn = (HttpURLConnection) new URL(baseURL + newUrl).openConnection();
			conn.setRequestProperty("Cookie", cookies);
			conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			conn.addRequestProperty("User-Agent", "Mozilla");
			conn.addRequestProperty("Referer", "google.com");

			System.out.println("Redirect to URL : " + newUrl);

		}
		return conn;
	}

	/*
	 * Input : argument 1 : location(example - Atlanta, Georgia), argument 2 : date mm/dd/yyyy (example - 5/17/2017) 
	 */
	public static void main(String[] args) {
		
		String location = "Atlanta, Georgia";
		String date = "5/17/2017";
		if(args.length!=0){
			location = args[0];
			date = args[1];
		}
	
		ScreenScraperWeatherInformation screenScraperWeatherInformation = new ScreenScraperWeatherInformation();
		screenScraperWeatherInformation.screenScraper(location, date);
	}

}