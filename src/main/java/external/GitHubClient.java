package external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import com.monkeylearn.MonkeyLearnException;

import entity.Item;
import entity.Item.ItemBuilder;


public class GitHubClient {
	//% place holder
	private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
	private static final String DEFAULT_KEYWORD = "developer";
	public List<Item> search(double lat, double lon, String keyword) {
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = String.format(URL_TEMPLATE, keyword, lat, lon);
		//httpClient 接受对方的消息
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			//httpResponse 接收到的消息
			CloseableHttpResponse response = httpClient.execute(new HttpGet(url));
			if (response.getStatusLine().getStatusCode() != 200) {
				return new ArrayList<>();
			}
			//HttpEntity 接受的信息内容
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return new ArrayList<>();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			StringBuilder responseBody = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				responseBody.append(line);
			}
			JSONArray array = new JSONArray(responseBody.toString());
			try {
				return getItemList(array);
			} catch (MonkeyLearnException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	//json ---> List<Item>
	private List<Item> getItemList(JSONArray array) throws MonkeyLearnException {
		List<Item> result = new ArrayList<Item>();
		List<String> descriptionList = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			String description = getStringFieldOrEmpty(array.getJSONObject(i), "description");
			if (description.equals("") || description.equals("\n")) {
				descriptionList.add(getStringFieldOrEmpty(array.getJSONObject(i), "title"));
			}else {
				descriptionList.add(description);
			}
		}
		String[] strings = descriptionList.toArray(new String[descriptionList.size()]);
		List<List<String>> keywords = MonkeyLearnClient.extractKeyWords(strings);
		
//List<String> descriptionList = new ArrayList<>();
//		
//		for (int i = 0; i < array.length(); i++) {
//			// We need to extract keywords from description since GitHub API
//			// doesn't return keywords.
//			String description = getStringFieldOrEmpty(array.getJSONObject(i), "description");
//			if (description.equals("") || description.equals("\n")) {
//				descriptionList.add(getStringFieldOrEmpty(array.getJSONObject(i), "title"));
//			} else {
//				descriptionList.add(description);
//			}	
//		}
//
//		// We need to get keywords from multiple text in one request since
//		// MonkeyLearnAPI has limitation on request per minute.
//		List<List<String>> keywords = MonkeyLearnClient
//				.extractKeywords(descriptionList.toArray(new String[descriptionList.size()]));


		
		for (int i = 0; i < array.length(); i++) {
			ItemBuilder builder = new ItemBuilder();
			JSONObject object = array.getJSONObject(i);
			builder.setItemId(getStringFieldOrEmpty(object, "id"));
			builder.setName(getStringFieldOrEmpty(object, "title"));
			builder.setAddress(getStringFieldOrEmpty(object, "location"));
			builder.setUrl(getStringFieldOrEmpty(object, "url"));
			builder.setImageUrl(getStringFieldOrEmpty(object, "company_logo"));
			
			HashSet<String> set = new HashSet<>(keywords.get(i));
			builder.setKeywords(set);
			
			Item item = builder.build();
			result.add(item);	
		}	
			return result;
	}
		

//	private List<Item> getItemList(JSONArray array) {
//		List<Item> itemList = new ArrayList<>();
//		for (int i = 0; i < array.length(); ++i) {
//			JSONObject object = array.getJSONObject(i);
//			ItemBuilder builSystem.out.printn(w);der = new ItemBuilder();
//			
//			builder.setItemId(getStringFieldOrEmpty(object, "id"));
//			builder.setName(getStringFieldOrEmpty(object, "title"));
//			builder.setAddress(getStringFieldOrEmpty(object, "location"));
//			builder.setUrl(getStringFieldOrEmpty(object, "url"));
//			builder.setImageUrl(getStringFieldOrEmpty(object, "company_logo"));
//			
//			Item item = builder.build();
//			itemList.add(item);
//		}
//		
//		return itemList;
//	}
	
	//get specific field info from json object
	private String getStringFieldOrEmpty(JSONObject obj, String field) {
		return obj.isNull(field) ? "" : obj.getString(field);
	}
	//
	

}
