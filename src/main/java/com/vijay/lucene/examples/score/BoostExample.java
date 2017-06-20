package com.vijay.lucene.examples.score;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class BoostExample {

	private static IndexWriter iwriter = null;
	static Directory directory = new RAMDirectory();
	static Analyzer analyzer = new StandardAnalyzer();
	static boolean printExplanation = true;

	public static void main(String arg[]) throws Exception {

		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		iwriter = new IndexWriter(directory, config);

		boostPerType("Lada Niva", "Brown", "2000000", "Russia", "SUV");
		boostPerType("Tata Aria", "Red", "1600000", "India", "SUV");
		boostPerType("Nissan Terrano", "Blue", "2000000", "Japan", "SUV");
		boostPerType("Mahindra XUV500", "Black", "1600000", "India", "SUV");
		boostPerType("Ford Ecosport", "White", "1000000", "USA", "SUV");
		boostPerType("Mahindra Thar", "White", "1200000", "India", "SUV");
		iwriter.close();

		// ^2
		String searchText = "itemColour:white OR itemType:suv";
		searchAndPrintResults(searchText);

	}

	private static void boostPerType(String itemName, String itemColour, String itemPrice, String originOfItem,
			String itemType) throws IOException {
		Document docToAdd = new Document();
		docToAdd.add(new StringField("itemName", itemName, Field.Store.YES));
		docToAdd.add(new StringField("itemColour", itemColour, Field.Store.YES));
		docToAdd.add(new StringField("itemPrice", itemPrice, Field.Store.YES));
		docToAdd.add(new StringField("originOfItem", originOfItem, Field.Store.YES));

		TextField itemTypeField = new TextField("itemType", itemType, Field.Store.YES);
		docToAdd.add(itemTypeField);

		// Boost items made in India
		if ("India".equalsIgnoreCase(originOfItem)) {
			itemTypeField.setBoost(2.0f);
		}
		iwriter.addDocument(docToAdd);
	}

	private static void searchAndPrintResults(String searchText) {
		try {
			IndexReader idxReader = DirectoryReader.open(directory);
			IndexSearcher idxSearcher = new IndexSearcher(idxReader);
			Query queryToSearch = new QueryParser("itemType", analyzer).parse(searchText);

			System.out.println(queryToSearch);

			ScoreDoc[] hitsTop = idxSearcher.search(queryToSearch, 10).scoreDocs;

			System.out.println("Search produced " + hitsTop.length + " hits.");
			System.out.println("----------");
			for (int i = 0; i < hitsTop.length; ++i) {
				int docId = hitsTop[i].doc;
				Document docAtHand = idxSearcher.doc(docId);

				System.out.println(docAtHand.get("itemName") + "\t" + docAtHand.get("originOfItem") + "\t"
						+ docAtHand.get("itemColour") + "\t" + docAtHand.get("itemPrice") + "\t"
						+ docAtHand.get("itemType") + "\t" + hitsTop[i].score);

				if (printExplanation) {
					Explanation explanation = idxSearcher.explain(queryToSearch, hitsTop[i].doc);
					System.out.println("----------");
					System.out.println(explanation.toString());
					System.out.println("----------");
				}
			}
		} catch (IOException | ParseException ex) {
			System.out.println("Something went wrong in this sample code -- " + ex.getLocalizedMessage());
		}
	}
}
