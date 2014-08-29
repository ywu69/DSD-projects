import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FileParser {

	// output result as requested format to a local file
	public void outputFile(JSONArray restaurants, String location,
			String outputFileName, String TWITTER_API_KEY,
			String TWITTER_API_SECRET, String TWITTER_TOKEN,
			String TWITTER_TOKEN_SECRET) {

		// built twitter views results
		String viewResults[] = new String[restaurants.size()];

		for (int i = 0; i < viewResults.length; i++) {
			JSONObject restaurant = (JSONObject) restaurants.get(i);
			String nameBeforeFormat = restaurant.get("name").toString();
			String restaurantName = nameBeforeFormat.replace(" ", "%20");
			String citt_nameString = location.replace(" ", "%20");
			StringBuffer twitterUrl = new StringBuffer(
					"https://api.twitter.com/1.1/search/tweets.json?q=");
			twitterUrl.insert(49, restaurantName + "%20" + citt_nameString);
			TwitterSearcher ts = new TwitterSearcher(twitterUrl.toString(),
					TWITTER_API_KEY, TWITTER_API_SECRET, TWITTER_TOKEN,
					TWITTER_TOKEN_SECRET);
			viewResults[i] = ts.searchInTwitter();
		}

		// file output
		File file = new File(outputFileName);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.getMessage();
			}
		}

		FileWriter fw = null;
		PrintWriter pw = null;

		try {
			fw = new FileWriter(file);
			pw = new PrintWriter(fw);

			// print out the result
			for (int i = 0; i < restaurants.size(); i++) {
				JSONObject restaurant = (JSONObject) restaurants.get(i);
				String restaurantName = restaurant.get("name").toString();
				String restaurantRating = restaurant.get("rating").toString();
				pw.println(restaurantName);
				pw.println(String.format("  rating: %s", restaurantRating));
				pw.println(String.format("  what people are saying: "));

				// extract user's text from JSON
				JsonParser twitter_JsonParser = new JsonParser(viewResults[i]);
				JSONObject twitter_response = twitter_JsonParser.parserBegin();
				JSONArray userArray = null;
				userArray = (JSONArray) twitter_response.get("statuses");
				if (userArray == null || userArray.size() == 0) {
					pw.println("  There is no twitter about this restaurant.");
				}
				else {
					for (int j = 0; j < userArray.size(); j++) {
						JSONObject userStatus = (JSONObject) userArray.get(j);
						String userText = (String) userStatus.get("text");
						String finalTextString = userText.replace("\n", " ");
						pw.println("    " + finalTextString);
					}
				}
				pw.println();
				pw.flush();
				// if (i == 3) {
				// System.out.println(viewResults[i]);
				// }
			}
		} catch (IOException e) {
			e.getMessage();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}
}