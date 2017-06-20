package com.vijay.lucene.examples.search;

import java.io.IOException;

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
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class MultiFieldSearch {

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
		doc.add(new Field("title", "Apache Lucene", TextField.TYPE_STORED));
		doc.add(new Field("content", text, TextField.TYPE_STORED));
		iwriter.addDocument(doc);

		iwriter.close();

		// Now search the index:
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);

		String searchText = "Apache";

		Query query1 = new QueryParser("title", analyzer).parse(searchText);
		Query query2 = new QueryParser("content", analyzer).parse(searchText);

		BooleanQuery booleanQuery = new BooleanQuery.Builder().add(query1, BooleanClause.Occur.MUST)
				.add(query2, BooleanClause.Occur.SHOULD).build();

		TopDocs tdocs = null;

		/**
		QueryParser queryParser = new QueryParser("title", analyzer);
		String special = "title:" + searchText + " OR content:" + searchText;
		tdocs = isearcher.search(queryParser.parse(special), 10);
		*/
		
		tdocs = isearcher.search(booleanQuery, 10);

		for (ScoreDoc docs : tdocs.scoreDocs) {
			Document hitDoc = isearcher.doc(docs.doc);
			System.out.println(docs);
			System.out.println("Scoring Expl: " + isearcher.explain(booleanQuery, docs.doc));
			// System.out.println("Content: " + hitDoc.get("content"));
		}
		ireader.close();
		directory.close();
	}
}