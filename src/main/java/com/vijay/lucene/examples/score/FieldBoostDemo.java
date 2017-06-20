package com.vijay.lucene.examples.score;

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
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.DoubleValuesSource;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class FieldBoostDemo {

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

		doc = new Document();

		text = "Lucene is simple yet powerful java based search library.";
		doc.add(new Field("title", "Apache Elasticseach Lucene", TextField.TYPE_STORED));
		doc.add(new Field("content", text, TextField.TYPE_STORED));
		iwriter.addDocument(doc);

		iwriter.close();

		// Now search the index:
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		// Parse a simple query that searches for "text":
		QueryParser parser = new QueryParser("title", analyzer);
		Query query = parser.parse("lucene");
		ScoreDoc[] hits = isearcher.search(query, 10, Sort.RELEVANCE).scoreDocs;

		// Iterate through the results:
		for (ScoreDoc docs : hits) {
			Document hitDoc = isearcher.doc(docs.doc);
			System.out.println(docs);
			System.out.println("Scoring Expl: " + isearcher.explain(query, docs.doc));
			System.out.println("Content: " + hitDoc.get("fieldname"));
		}

		isearcher.setSimilarity(new ClassicSimilarity());
		// Parse a simple query that searches for "text":
		parser = new QueryParser("title", analyzer);
		query = parser.parse("lucene");
		hits = isearcher.search(query, 10, Sort.RELEVANCE).scoreDocs;

		// Iterate through the results:
		for (ScoreDoc docs : hits) {
			Document hitDoc = isearcher.doc(docs.doc);
			System.out.println(docs);
			System.out.println("Scoring Expl: " + isearcher.explain(query, docs.doc));
			System.out.println("Content: " + hitDoc.get("fieldname"));
		}

		ireader.close();
		directory.close();
	}

	public static void main1(String[] args) throws IOException {

		Query query = new TermQuery(new Term("title", "fieldValue"));
		FunctionScoreQuery fsq = new FunctionScoreQuery(query, DoubleValuesSource.constant(5));

		IndexSearcher searcher = null;
		searcher.setSimilarity(new BM25Similarity());

		searcher.search(fsq, 0);
	}

}
