package com.ebay.firstEbay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

@SpringBootApplication
@RestController
public class FirstEbayApplication {

	public static void main(String[] args) {

		SpringApplication.run(FirstEbayApplication.class, args);
	}
	@GetMapping("/browse_api/search")
	public String search() throws URISyntaxException {
		String url = "https://api.sandbox.ebay.com/buy/browse/v1/item_summary/search?q=drone&limit=3";
		String accessToken = "";
		try {
			accessToken = generateAccessToken();
		} catch (IOException e) {
			System.out.println("ERROR Access Token");
			e.printStackTrace();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-EBAY-C-MARKETPLACE-ID", "EBAY_US");
		headers.set("X-EBAY-C-ENDUSERCTX", "affiliateCampaignId=<ePNCampaignId>,affiliateReferenceId=<referenceId>");

		RestTemplate restTemplate = new RestTemplate();
		RequestEntity requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI(url));
		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			String responseBody = responseEntity.getBody();
			System.out.println("Response: " + responseBody);
			return responseBody;

		} else {
			return "Error: " + responseEntity.getStatusCode();
		}
	}
//
//	@PostMapping("/browse_api/inventory_task")
//	public String inventoryTask() throws URISyntaxException {
//		String url = "https://api.sandbox.ebay.com/sell/feed/v1/inventory_task";
//		String accessToken = "";
//		try {
//			accessToken = generateAccessToken();
//		} catch (IOException e) {
//			System.out.println("ERROR Access Token");
//			e.printStackTrace();
//		}
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.set("Authorization", "Bearer " + accessToken);
//		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		headers.set("X-EBAY-C-MARKETPLACE-ID", "EBAY_US");
//
//		String requestBody = "{\"schemaVersion\": \"1.0\", \"feedType\": \"LMS_ACTIVE_INVENTORY_REPORT\"}";
//
//		RestTemplate restTemplate = new RestTemplate();
//		RequestEntity<String> requestEntity = new RequestEntity<>(requestBody, headers, HttpMethod.POST, new URI(url));
//		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
//
//		if (responseEntity.getStatusCode().is2xxSuccessful()) {
//			String responseBody = responseEntity.getBody();
//			System.out.println("Response: " + responseBody);
//			return responseBody;
//		} else {
//			return "Error: " + responseEntity.getStatusCode();
//		}
//	}
	public static String generateAccessToken() throws IOException {
		String endpoint = "https://api.sandbox.ebay.com/identity/v1/oauth2/token";

		// Set your client ID and client secret
		String clientId = "Zadorozh-Zadorapp-SBX-513fa3adc-cf525d98";
		String clientSecret = "SBX-076c9b06ab46-a800-4228-8f02-e0cd";

		// Set the scopes
		String scope = "https://api.ebay.com/oauth/api_scope https://api.ebay.com/oauth/api_scope/buy.guest.order " +
				"https://api.ebay.com/oauth/api_scope/buy.item.feed https://api.ebay.com/oauth/api_scope/buy.marketing " +
				"https://api.ebay.com/oauth/api_scope/buy.product.feed https://api.ebay.com/oauth/api_scope/buy.marketplace.insights " +
				"https://api.ebay.com/oauth/api_scope/buy.proxy.guest.order https://api.ebay.com/oauth/api_scope/buy.item.bulk " +
				"https://api.ebay.com/oauth/api_scope/buy.deal ";

		// Encode the client ID and client secret as Base64
		String credentials = clientId + ":" + clientSecret;
		String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

		// Create the request payload
		String payload = "grant_type=client_credentials&scope=" + scope;

		// Create the HTTP connection and configure headers
		URL url = new URL(endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
		connection.setDoOutput(true);

		// Send the request payload
		connection.getOutputStream().write(payload.getBytes(StandardCharsets.UTF_8));
		System.out.println(connection.getInputStream());

		// Read the response
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder response = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			response.append(line);
		}
		reader.close();

		// Parse the JSON response and extract the access token
		String accessToken = response.toString().split("\"access_token\":\"")[1].split("\"")[0];

		// Return the access token
		return accessToken;
	}
}
