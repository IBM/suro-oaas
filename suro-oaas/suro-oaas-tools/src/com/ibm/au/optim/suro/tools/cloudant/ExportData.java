package com.ibm.au.optim.suro.tools.cloudant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.StringBuilder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This tool will export all the data from the Cloudant instance configured
 * below. The tool has 2 configuration variables, CLOUDANT_ROOT_URL and
 * ENDPOINT_ALL_DOCS. The CLOUDANT_ROOT_URL also needs to contain username and
 * password in the structure https://<username>:<password>@<url>/<database>
 * 
 * @author Peter Ilfrich
 *
 */
public class ExportData {

	/*
	 * remote database url (containing username and password) and the endpoint
	 * for the document list
	 */
	public static final String CLOUDANT_ROOT_URL = "https://81e591e3-b955-4000-91d4-3baa0ce3f8c4-bluemix:2618a60e3d418eee51ff08056d87a1998462ec8a4f6472ff11abc23917ce3628@81e591e3-b955-4000-91d4-3baa0ce3f8c4-bluemix.cloudant.com/suro/";
	// the endpoint for all documents
	public static final String ENDPOINT_ALL_DOCS = "_all_docs";

	// to be set at the beginning of the main method.
	public static String FOLDER_NAME = "";

	// run variables to capture total documents
	public static int TOTAL_DOCS = 0;
	// run variables to capture processed documents
	public static int PROCESSED_DOCS = 0;

	/**
	 * Starts the program. Will first read the index of all documents and then
	 * iterate over the documents and save each document to disk.
	 * 
	 * @param args
	 *            - none
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// determine the folder name for the current export
		DateFormat df = new SimpleDateFormat("YYYY-MM-dd-HHmm");
		FOLDER_NAME = df.format(new Date()) + "-cloudant-data";

		// make sure the folder exists
		File folder = new File("data/" + FOLDER_NAME);
		if (!folder.isDirectory()) {
			folder.mkdirs();
		}
		
		// read the index of all documents and parse it
		String allDocs = returnHttpGetResult(CLOUDANT_ROOT_URL
				+ ENDPOINT_ALL_DOCS);
		JsonObject docs = (JsonObject) new JsonParser().parse(allDocs);

		// find out how many documents there are in total
		JsonArray docList = docs.get("rows").getAsJsonArray();
		TOTAL_DOCS = docList.size();

		// iterate through the document IDs
		for (int i = 0; i < docList.size(); i++) {
			String docId = ((JsonObject) docList.get(i)).get("id")
					.getAsString();
			// save a single document into a single file
			saveDocument(docId);
		}

		// write the list of documents into an index document
		System.out.println("Writing Index");
		FileWriter fw = new FileWriter(new File(new File("data/" + FOLDER_NAME),
				"index.json"));
		fw.write(allDocs);
		fw.close();

		// finished export
		System.out.println("Done");
	}

	/**
	 * Requests the provided URL via HTTP get and returns the result as a String
	 * 
	 * @param url
	 *            - the URL to request
	 * @return - the content returned (as text)
	 * @throws IOException
	 */
	public static String returnHttpGetResult(String url) throws IOException {
		// create the client and request
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet allDocsRequest = new HttpGet(url);

		// execute the request
		HttpResponse response = client.execute(allDocsRequest);

		// read the response into a string buffer
		StringBuilder result = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = "";
		while ((line = br.readLine()) != null) {
			result.append(line);
		}
		br.close();

		// return the content as String
		return result.toString();
	}

	/**
	 * Saves a document with the provided ID. The method will request the
	 * document from the server and store it in a file with the same name as the
	 * document ID. If the documentID is a design document, the documentID will
	 * be split into tokens to generate the path. The method makes sure the
	 * sub-directory (e.g. /design/ exists)
	 * 
	 * @param documentId
	 *            - the id field of the document from the index
	 * @throws IOException
	 */
	public static void saveDocument(String documentId) throws IOException {
		if (documentId.contains("_design")) {
		// open the URL and return the response as String (the JSON document)
		String doc = returnHttpGetResult(CLOUDANT_ROOT_URL + documentId);

		// handle sub folders (e.g. for design documents)
		if (documentId.contains("/")) {
			// e.g. design/abc
			File dir = new File("data/" + FOLDER_NAME);
			String[] path = documentId.split("/");
			// ignore the last token (which is the file name)
			for (int i = 0; i < path.length - 1; i++) {
				// recursively make sure all sub-folders exist
				dir = new File(dir, path[i]);
				if (!dir.isDirectory()) {
					// create the directory if it doesn't exist yet
					dir.mkdirs();
				}
			}
		}

		// write the content of the document into the file
		FileWriter fw = new FileWriter(new File(new File("data/" + FOLDER_NAME),
				documentId + ".json"));
		// if document is a design document remove _id and _rev to allow re-importing them
		if (documentId.contains("/") && documentId.contains("_design")) {
			JsonObject design = (JsonObject) new JsonParser().parse(doc);
			design.remove("_id");
			design.remove("_rev");
			doc = new Gson().toJson(design);
		}
		fw.write(doc);
		fw.close();
		}
		// show debug information for processed docs
		PROCESSED_DOCS++;
		if (PROCESSED_DOCS % 5 == 0) {
			System.out.println("Processed " + PROCESSED_DOCS + " / "
					+ TOTAL_DOCS + " documents.");
		}
		
	}

}
