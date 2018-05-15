package com.loxodon.crawler.facebook.groupmembers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Loay
 *
 */
public class FacebookJsonParser {

	private Logger log = LoggerFactory.getLogger(getClass());

	private final String accessToken;
	private String graphURL = "https://graph.facebook.com/"; // Facebook Graph API URL

	private String groupID[] = {};
	private String userID[] = {};
	private String fields[] = {};

	/**
	 * 
	 * @param accessToken
	 * @param fields
	 * @param groupIDs
	 */
	public FacebookJsonParser(String accessToken, String[] fields, String[] groupIDs) {
		this.accessToken = accessToken;

		this.fields = fields;
		this.groupID = groupIDs;
	}

	private void crawlGroup() throws IOException, JSONException, SQLException {
		log.info("Start crawling group");
		
		String memberListAppend = "/members?access_token=" + this.accessToken;

		for (int index = 0; index < groupID.length; index++) {
			String groupURL = this.graphURL;

			groupURL = groupURL.concat(groupID[index]);
			groupURL = groupURL.concat(memberListAppend); // Group members list & id
			log.info(String.format("Crawling group URL %s", groupURL));

			JSONObject GroupJSONObject = Parser.readJsonFromUrl(groupURL);
			JSONArray GroupMemberListJSONArray = GroupJSONObject.getJSONArray("data");

			for (int MemberCount = 0; MemberCount < GroupMemberListJSONArray.length(); MemberCount++) {
				this.crawlUser(GroupMemberListJSONArray.getJSONObject(MemberCount), index);
			}
		}
	}

	private void crawlUsers() throws IOException, JSONException, SQLException {
		String accessTokenAppend = "?access_token=" + accessToken;

		userID = new String[] {"751800947"};
		if (userID.length <= 0) {
			return;
		}

		for (int index = 0; index < userID.length; index++) {
			String UserURL = this.graphURL;

			UserURL = UserURL.concat(userID[index]);
			UserURL = UserURL.concat(accessTokenAppend);

			JSONObject UserObject = new JSONObject();
			UserObject.put("id", userID[index]);
			this.crawlUser(UserObject, -1);
		}
	}

	private void crawlUser(JSONObject GroupMemberJSONObject, int index)
			throws IOException, JSONException, SQLException {

		final String accessTokenAppend = "?access_token=" + accessToken;
		final int fieldsSize = 19;

		final String[] fieldsWithCommonStructure = { "id", "name", "first_name", "middle_name", "last_name", "link",
				"username", "birthday", "gender", "relationship_status", "bio", "quotes", "email", "religion" };

		String memberURL;
		JSONObject groupMemberDataJSONObject;

		memberURL = graphURL;

		String userId = GroupMemberJSONObject.getString("id");
		memberURL = memberURL.concat(userId);
		memberURL = memberURL.concat(accessTokenAppend);

		groupMemberDataJSONObject = Parser.readJsonFromUrl(memberURL); // Member info

		Object username = groupMemberDataJSONObject.get("name");
		log.info(String.format("Crawling user id %s with name %s", userId, username));

		String memberDataFieldsArray[] = new String[fieldsSize];
		String currentField = "";
		JSONArray memberDataFieldNames = groupMemberDataJSONObject.names();

		for (int fieldIndex = 0; fieldIndex < memberDataFieldNames.length(); fieldIndex++) {
			String check = memberDataFieldNames.getString(fieldIndex);

			if (StringUtils.startsWithAny(check, fieldsWithCommonStructure)) {
				currentField = groupMemberDataJSONObject.getString(memberDataFieldNames.getString(fieldIndex));
			}

			switch (memberDataFieldNames.getString(fieldIndex)) {

			case "location":
				JSONObject location = groupMemberDataJSONObject.getJSONObject("location");

				// String locarr[] =
				// JSONObject..getNames(groupMemberDataJSONObject.getJSONObject("location"));
				// for (int z = 0; z < locarr.length; z++)
				// if (locarr[z].equals("name"))
				// currentField = GroupMemberDataJSONObject.getString(locarr[z]);
				break;

			case "hometown":
				JSONObject homeTown = groupMemberDataJSONObject.getJSONObject("hometown");
				// String homearr[] = JSONObject.getNames(homeTown);
				// for (int z = 0; z < homearr.length; z++)
				// if (homearr[z].equals("name"))
				// currentField = GroupMemberDataJSONObject.getString(homearr[z]);
				break;

			case "languages":
				String langs = "";
				JSONArray langarry = groupMemberDataJSONObject.getJSONArray("languages");
				JSONObject langnames;

				for (int y = 0; y < langarry.length(); y++) {
					langnames = langarry.getJSONObject(y);
					langs = langs.concat(" " + langnames.getString("name") + " ");
				}
				currentField = langs;
				break;

			case "education":
				String education = "";
				JSONArray eduarry = groupMemberDataJSONObject.getJSONArray("education");
				JSONObject eduname;
				JSONObject educationObject = groupMemberDataJSONObject.getJSONObject("education");
				// String scharry[] = JSONObject.getNames(educationObject);
				//
				// for (int x = 0; x < eduarry.length(); x++) {
				// if (scharry[x].equals("school")) {
				// eduname = eduarry.getJSONObject(x);
				// education = education.concat(" " + eduname.getString("name") + " ");
				// }
				// }
				currentField = education;
				break;

			case "work":
				String work = "";
				JSONArray workarry = groupMemberDataJSONObject.getJSONArray("work");
				JSONObject workname;
				JSONObject workObject = groupMemberDataJSONObject.getJSONObject("work");
				// String wrkarry[] = JSONObject.getNames(workObject);
				//
				// for (int w = 0; w < workarry.length(); w++) {
				// if (wrkarry[w].equals("employer")) {
				// workname = workarry.getJSONObject(w);
				// work = work.concat(" " + workname.getString("name") + " ");
				// }
				//
				// }
				currentField = work;
				break;
			}

			String tempcheck = memberDataFieldNames.getString(fieldIndex);

			for (int x = 0; x < fields.length; x++) {
				if (tempcheck.equals(fields[x])) {

					// currentField = FacebookDatabase.trimQuery(currentField);
					// if (currentField != null)
					// MemberDataFieldsArray[x] = currentField;
					//
					// else
					// MemberDataFieldsArray[x] = "null";
				}
			}
			// ID = GroupMemberDataJSONObject.getString("id");

		}

		// this.db.executeInsertQuery(ID, MemberDataFieldsArray, GroupID, IDIndex);
	}

	public void crawlJson(CrawlOptions op) throws IOException, JSONException, SQLException {
		// If Group Crawl
		if (op.toString().equals("GROUP") && groupID.length > 0) {
			this.crawlGroup();
		}
		
		// If Users Crawl
		else if (op.toString().equalsIgnoreCase("USER")) {
			this.crawlUsers();
		}

		else {
			log.error("Parameters missing.");
		}
	}
}
