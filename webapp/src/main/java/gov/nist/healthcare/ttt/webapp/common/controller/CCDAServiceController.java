package gov.nist.healthcare.ttt.webapp.common.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger; 
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
@RequestMapping("/api/ccdaservice")
public class CCDAServiceController {
	private static Logger logger = LogManager.getLogger(CCDAServiceController.class.getName());

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody HashMap<String, Object> getCcdaDocument(@RequestParam(value = "ccdatype") String ccdatype, 
		@RequestParam(value = "sutrole")  String sutrole,
		@RequestParam(value = "filename")  String filename) throws Exception {
		  String v1SenderGitHubUrl = "https://api.github.com/repos/onc-healthit/2015-edition-cures-update-data/contents/Cures Update Sender SUT Test Data";
		  String v1ReceiverGitHubUrl = "https://api.github.com/repos/onc-healthit/2015-edition-cures-update-data/contents/Cures Update Receiver SUT Test Data";
		  String v2SenderGitHubUrl = "https://api.github.com/repos/onc-healthit/2015-edition-cures-update-uscdi-v2-testdata/contents/Cures Update Svap Uscdiv2 Sender SUT Test Data";
		  String v2ReceiverGitHubUrl = "https://api.github.com/repos/onc-healthit/2015-edition-cures-update-uscdi-v2-testdata/contents/Cures Update Svap Uscdiv2 Receiver SUT Test Data";
		  String v3SenderGitHubUrl = "https://api.github.com/repos/onc-healthit/2015-edition-cures-update-uscdi-v3-testdata/contents/Cures Update Svap Uscdiv3 Sender SUT Test Data";
		  String v3ReceiverGitHubUrl = "https://api.github.com/repos/onc-healthit/2015-edition-cures-update-uscdi-v3-testdata/contents/Cures Update Svap Uscdiv3 Receiver SUT Test Data";
		  String gtHubUrl = v3ReceiverGitHubUrl;

		String returnVal ="";
		if (ccdatype.equalsIgnoreCase("Uscdiv3")){
			if (sutrole.equalsIgnoreCase("Receiver")){
				gtHubUrl = v3ReceiverGitHubUrl;
			}else{
				gtHubUrl = v3SenderGitHubUrl;
			}
		}else if (ccdatype.equalsIgnoreCase("Uscdiv2")){
			if (sutrole.equalsIgnoreCase("Receiver")){
				gtHubUrl = v2ReceiverGitHubUrl;
			}else{
				gtHubUrl = v2SenderGitHubUrl;
			}
		}else if (ccdatype.equalsIgnoreCase("Uscdiv1")){
			if (sutrole.equalsIgnoreCase("Receiver")){
				gtHubUrl = v1ReceiverGitHubUrl;
			}else{
				gtHubUrl = v1SenderGitHubUrl;
			}			
		}

		 returnVal =  getHtmlContent(gtHubUrl,filename);
         HashMap<String, Object> resultSenderReceiver = new HashMap<>();
         resultSenderReceiver.put("ccdadata", returnVal);         
		 return resultSenderReceiver;
	}


	public static String getHtmlContent(String urlToRead, String filename) throws Exception {
		if (StringUtils.isNotBlank(filename) && filename.length() > 1){
		 	urlToRead = urlToRead+"/"+filename+"?ref=master";
		 }
		 logger.info("CCDAServiceController urlToRead:::::"+urlToRead);
		return getHtmlContent(urlToRead);
	}

	public static String getHtmlContent(String urlToRead) throws Exception {

		String ettApiToken = System.getenv("ETT_API_TOKEN");

		  if(urlToRead.contains(" "))
			  urlToRead = urlToRead.replace(" ", "%20");	

		StringBuilder result = new StringBuilder();
		URL url = new URL(urlToRead);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Authorization","Bearer "+ettApiToken);
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return prettyPrintJsonUsingDefaultPrettyPrinter(result.toString());
	}

	public static String prettyPrintJsonUsingDefaultPrettyPrinter(String uglyJsonString) {
	    ObjectMapper objectMapper = new ObjectMapper();
	    Object jsonObject;
	    String prettyJson ="";
		try {
			jsonObject = objectMapper.readValue(uglyJsonString, Object.class);
		     prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
		} catch (Exception e) {
			logger.error("Json Pretty conversion error: "+e.getMessage());
			e.printStackTrace();
		}		
	    return prettyJson;
	}	

}