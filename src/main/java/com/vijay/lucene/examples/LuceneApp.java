package com.vijay.lucene.examples;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

/**
 * Hello world!
 *
 */
public class LuceneApp {

	public static void main(String[] args) throws Exception {
		LuceneApp app;
		try {
			app = new LuceneApp();
			app.createIndex();
			app.search("modi");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void createIndex() throws IOException {
		long startTime = System.currentTimeMillis();
		FileIndexer.indexFiles(Constants.DATA_DIR, Constants.INDEX_DIR, false);
		long endTime = System.currentTimeMillis();
		System.out.println("Indexing Took: " + (endTime - startTime) + " ms");
	}

	private void search(String searchQuery) throws Exception {

		long startTime = System.currentTimeMillis();

		FileSearcher.search(Constants.INDEX_DIR, searchQuery);

		long endTime = System.currentTimeMillis();

		System.out.println("Search Took: " + (endTime - startTime) + " ms");

	}
}
