import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RestaurantMashup {

	private static final String outputFileName = "RestaurantMashup.txt";
	private static final String filePath = "keys.json";

	private static String CITY_NAME = null;
	private static String YELP_API_KEY = null;
	private static String YELP_API_SECRET = null;
	private static String YELP_TOKEN = null;
	private static String YELP_TOKEN_SECRET = null;
	private static String TWITTER_API_KEY = null;
	private static String TWITTER_API_SECRET = null;
	private static String TWITTER_TOKEN = null;
	private static String TWITTER_TOKEN_SECRET = null;
	private static int limit = 15;

	public String JSONExtract(String key) {
		String value = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(filePath));
			JSONObject jsonObject = (JSONObject) obj;

			value = (String) jsonObject.get(key);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return value;
	}

	public static void main(String[] args) {

		RestaurantMashup rm = new RestaurantMashup();

		CITY_NAME = rm.JSONExtract("city");
		YELP_API_KEY = rm.JSONExtract("yelpconsumerkey");
		YELP_API_SECRET = rm.JSONExtract("yelpconsumersecret");
		YELP_TOKEN = rm.JSONExtract("yelptoken");
		YELP_TOKEN_SECRET = rm.JSONExtract("yelptokensecret");
		TWITTER_API_KEY = rm.JSONExtract("twitterapikey");
		TWITTER_API_SECRET = rm.JSONExtract("twitterapisecret");
		TWITTER_TOKEN = rm.JSONExtract("twittertoken");
		TWITTER_TOKEN_SECRET = rm.JSONExtract("twittertokensecret");

		// search restaurants in yelp
		YelpSearcher ys = new YelpSearcher(YELP_API_KEY, YELP_API_SECRET,
				YELP_TOKEN, YELP_TOKEN_SECRET, limit);
		String yelpSearchResult = ys.searchForBusinessesByLocation(
				"restaurants", CITY_NAME);

		// extract restaurants names and ratings from yelp's search result
		JsonParser yelp_JsonParser = new JsonParser(yelpSearchResult);
		JSONObject yelp_response = yelp_JsonParser.parserBegin();
		JSONArray restaurants = (JSONArray) yelp_response.get("businesses");
		
		// get twitter review and then output the search result to a local file
		FileParser fp = new FileParser();
		fp.outputFile(restaurants, CITY_NAME, outputFileName, TWITTER_API_KEY,
				TWITTER_API_SECRET, TWITTER_TOKEN, TWITTER_TOKEN_SECRET);
	}
}