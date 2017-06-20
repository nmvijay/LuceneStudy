package com.vijay.lucene.examples;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneCRUDApplication {

	static String KEY_COLUMN = "DocID";
	static String KEY = "100";
	static String CONTENT_COLUMN = "myContent";

	Analyzer analyzer = new StandardAnalyzer();
	Directory directory = null;

	public static void main(String[] args) throws Exception {
		LuceneCRUDApplication app = new LuceneCRUDApplication();
		app.clearAllIndex();
		app.search("Lucene");
		app.createIndex();
		app.search("Lucene");
		app.updateIndex();
		app.search("Lucene");
		app.deleteIndex();
		app.search("Lucene");
	}

	private void createIndex() throws IOException {
		System.out.println("Creating Index");
		directory = FSDirectory.open(Paths.get("/tmp/testindex"));

		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter iwriter = new IndexWriter(directory, config);
		Document doc = new Document();
		String text = "Lucene is simple yet powerful java based search library.";

		doc.add(new Field(KEY_COLUMN, KEY, TextField.TYPE_STORED));
		doc.add(new Field(CONTENT_COLUMN, text, TextField.TYPE_STORED));

		iwriter.addDocument(doc);
		iwriter.commit();
		iwriter.close();
	}

	private void updateIndex() throws IOException {
		System.out.println("Updating Index");
		directory = FSDirectory.open(Paths.get("/tmp/testindex"));

		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter iwriter = new IndexWriter(directory, config);
		Document doc = new Document();
		String text = "Lucene is simple yet powerful java based search library, used by Solr and Elasticseearch";

		doc.add(new Field(KEY_COLUMN, KEY, TextField.TYPE_STORED));
		doc.add(new Field(CONTENT_COLUMN, text, TextField.TYPE_STORED));

		iwriter.updateDocument(new Term(KEY_COLUMN, KEY), doc);
		iwriter.commit();
		iwriter.close();
	}

	private void deleteIndex() throws IOException {
		System.out.println("Deleting Index");
		directory = FSDirectory.open(Paths.get("/tmp/testindex"));

		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter iwriter = new IndexWriter(directory, config);

		iwriter.deleteDocuments(new Term(KEY_COLUMN, KEY));
		iwriter.commit();
		iwriter.close();
	}

	private void clearAllIndex() throws IOException {
		System.out.println("Clearing All Indexes");
		directory = FSDirectory.open(Paths.get("/tmp/testindex"));

		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter iwriter = new IndexWriter(directory, config);
		iwriter.deleteAll();
		iwriter.commit();
		iwriter.close();
	}

	private List<String> search(String text) throws Exception {
		System.out.println("Searching Index: " + text);
		directory = FSDirectory.open(Paths.get("/tmp/testindex"));

		// Now search the index:
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		// Parse a simple query that searches for "text":
		QueryParser parser = new QueryParser(CONTENT_COLUMN, analyzer);
		Query query = parser.parse(text);
		ScoreDoc[] hits = isearcher.search(query, 10, Sort.RELEVANCE).scoreDocs;
		List<String> result = new ArrayList<>();
		// Iterate through the results:
		for (ScoreDoc docs : hits) {
			Document hitDoc = isearcher.doc(docs.doc);
			result.add(hitDoc.get(CONTENT_COLUMN));
		}
		ireader.close();
		directory.close();
		System.out.println("Results found: " + result);
		return result;
	}
}