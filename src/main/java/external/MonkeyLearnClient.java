package external;



import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.monkeylearn.ExtraParam;
import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;


public class MonkeyLearnClient {
	public static void main(String[] arg) throws MonkeyLearnException {
		String[] textList = {"I love minkang", "Elon Musk has shared a photo of the spacesuit designed by SpaceX. This is the second image shared of the new design and the first to feature the spacesuit’s full-body look"};
		List<List<String>> keywords = extractKeyWords(textList);
		for (List<String> wList : keywords) {
			for (String w : wList) {
				System.out.println(w);
			}
			System.out.println();
		}
	}
	private static final String API_KEY = "fd2f6604f933441f334c52ef905b8b7c6c7de444";
  
    //batch processing 
    public static List<List<String>> extractKeyWords(String[] data) throws MonkeyLearnException{
    	if (data == null || data.length == 0) {
    		return new ArrayList<>();
    	}
    	
    	MonkeyLearn ml = new MonkeyLearn(API_KEY);
        //extraParam[] ???
        String modelId = "ex_YCya9nrn";
        MonkeyLearnResponse res = ml.extractors.extract(modelId, data);
        return getKeywords(res.arrayResult);
        
    }
    
    //turn  JSONArray into List<List<String>>
    private static List<List<String>> getKeywords (JSONArray array){
    	List<List<String>> result = new ArrayList<>();
    	for (int i = 0; i < array.size(); i++) {
    		List<String> keywords = new ArrayList<>();
    		JSONArray keyWordArray = (JSONArray) array.get(i);
    		for (int j = 0; j < keyWordArray.size(); j++) {
    			// casting?
    			JSONObject object = (JSONObject) keyWordArray.get(j);
    			String keyword = (String) object.get("keyword");
    			keywords.add(keyword);
    		}
    		result.add(keywords);
    	}
    	return result;
    }
}