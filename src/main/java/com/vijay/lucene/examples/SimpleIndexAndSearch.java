package com.vijay.lucene.examples;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class SimpleIndexAndSearch {

	public static void main(String[] args) throws Exception {
		Analyzer analyzer = new StandardAnalyzer();

		// Store the index in memory:
		Directory directory = new RAMDirectory();
		// To store an index on disk, use this instead:
		// Directory directory = FSDirectory.open(Paths.get("/tmp/testindex"));
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter iwriter = new IndexWriter(directory, config);
		Document doc = new Document();
		String text = "Lucene is simple yet powerful java based search library.";
		doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
		iwriter.addDocument(doc);
		iwriter.close();

		// Now search the index:
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		// Parse a simple query that searches for "text":
		QueryParser parser = new QueryParser("fieldname", analyzer);
		Query query = parser.parse("lucene");
		ScoreDoc[] hits = isearcher.search(query, 10, Sort.RELEVANCE).scoreDocs;

		// Iterate through the results:
		for (ScoreDoc docs : hits) {
			Document hitDoc = isearcher.doc(docs.doc);
			System.out.println(docs);
			System.out.println("Content: " + hitDoc.get("fieldname"));
		}
		ireader.close();
		directory.close();
	}
}
