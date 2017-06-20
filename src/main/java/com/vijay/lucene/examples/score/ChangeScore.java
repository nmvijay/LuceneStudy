package com.vijay.lucene.examples.score;

import java.io.File;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;

public class ChangeScore {

	public static void main(String[] args) throws Exception {
		ChangeScore its = new ChangeScore();
		IndexWriter iw = its.buildIndexWriter("/Users/Vijay/index");
	}

	public IndexWriter buildIndexWriter(String indexPath) throws Exception {
		Directory dir = MMapDirectory.open(new File(indexPath).toPath());
		IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
		iwc.setSimilarity(new BM25Similarity()); 
		IndexWriter iw = new IndexWriter(dir, iwc);
		return iw;
	}

	public IndexSearcher buildIndexSearcher(String indexPath) throws Exception {
		IndexReader dr = DirectoryReader.open(FSDirectory.open(new File(indexPath).toPath()));
		IndexSearcher is = new IndexSearcher(dr);
		is.setSimilarity(new BM25Similarity()); 
		return is;
	}
}
