package com.aol.pulsar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;

import scala.actors.threadpool.Arrays;

import com.textteaser.summarizer.*;



/**
 * Servlet implementation class Pulsar
 */

public class Pulsar extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SITE_URL_AOL = "http://www.aol.com";
	private static final String SITE_URL_HUFFINGTON_POST = "http://www.huffingtonpost.com";
	private static final String SITE_URL_TECHCRUNCH = "http://techcrunch.com";
	private static final String SITE_NAME_AOL = "AOL";
	private static final String SITE_NAME_HUFFINGTON_POST = "Huffington Post";
	private static final String SITE_NAME_TECHCRUNCH = "TechCrunch";
	private static final String SITE_TEXT_SELECTOR_AOL = "div.article-content";
	private static final String SITE_TEXT_SELECTOR_HUFFINGTON_POST = "#mainentrycontent p";
	private static final String SITE_TEXT_SELECTOR_TECHCRUNCH = "div.article-entry p:not(.wp-caption-text)";

	private static final Map<String, List<String>> SUPPORTED_SITES = intializeSupportedSites();

	private static Map<String, List<String>> intializeSupportedSites(){
		try{
			Map<String, List<String>> map = new HashMap<String, List<String>>();
			map.put(SITE_URL_AOL, Collections.unmodifiableList(Arrays.
					asList(new String[]{SITE_NAME_AOL, SITE_TEXT_SELECTOR_AOL})));
			map.put(SITE_URL_HUFFINGTON_POST, Collections.unmodifiableList(Arrays
					.asList(new String[]{SITE_NAME_HUFFINGTON_POST, SITE_TEXT_SELECTOR_HUFFINGTON_POST})));
			map.put(SITE_URL_TECHCRUNCH, Collections.unmodifiableList(Arrays
					.asList(new String[]{SITE_NAME_TECHCRUNCH, SITE_TEXT_SELECTOR_TECHCRUNCH})));
			return Collections.unmodifiableMap(map);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Pulsar() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		String title, url = "";
		try{
			StringBuilder htmlResponse = new StringBuilder();
			String text = request.getParameter("text").trim();
			url = request.getParameter("url").trim();

			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");

			PrintWriter writer = response.getWriter();

			if(!url.equals("")){
				if(!url.startsWith("https://") && !url.startsWith("http://")){
					url = "http://" + url;
				}

				Document doc = Jsoup.connect(url).get();
				title = doc.select("title").text();

				// TODO MG: Consider revising to parse url and use hostname as 
				//   key to access entry in map.  This will eliminate the need 
				//   for the loop.
				for(Map.Entry<String, List<String>> entry : 
					SUPPORTED_SITES.entrySet()){
					if(url.indexOf(entry.getKey()) == 0){
						text = doc.select(entry.getValue().get(1)).text();
						if(text.equals("")){
							text = noSynopsisMsg(entry.getValue().get(0));
						}
						htmlResponse.append("<h5>" + title + " (SUMMARY)</h5>"
								+ "<p>" + new Run().execute(text, title) + "</p>");
						break;
					}
				}
			}else if(text != null && !text.equals("")){
				htmlResponse.append("<h5>SUMMARY</h5><p>" 
						+ new Run().execute(text, "Article") + "</p>");
			}
			
			writer.println(htmlResponse.toString());
		}catch(Exception e){
			System.out.println("Exception: " + url);
			e.printStackTrace();
		}

	}

	private String noSynopsisMsg(String name) {
		return "<i>***Pulsar is unable to generate a synopsis for the selected "
				+ "article from " + name + ".***</i>";
	}
}
