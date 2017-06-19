package com.vijay.lucene.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class FileIndexer {

	private FileIndexer() {
	}

	/** Index all text files under a directory. */

	public static void indexFiles(String docsPath, String indexPath, boolean create) {

		final Path docDir = Paths.get(docsPath);

		Long start = System.currentTimeMillis();

		try {
			System.out.println("Indexing to directory '" + indexPath + "'...");

			Directory dir = FSDirectory.open(Paths.get(indexPath));

			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			if (create) {
				// Create a new index in the directory, removing any
				// previously indexed documents:
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// Add new documents to an existing index:
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}

			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir);

			writer.close();

			System.out.println("Index Create took: " + (System.currentTimeMillis() - start) + " ms");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void indexDocs(final IndexWriter writer, Path path) throws IOException {

		if (Files.isDirectory(path)) {

			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					try {
						indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
						System.out.println(attrs.lastModifiedTime());
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
			System.out.println(Files.getLastModifiedTime(path));
		}
	}

	/** Indexes a single document */
	static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {

		try (InputStream stream = Files.newInputStream(file)) {
			// make a new, empty document
			Document doc = new Document();

			// Add the path of the file as a field named "path". Use a
			// field that is indexed (i.e. searchable), but don't tokenize
			// the field into separate words and don't index term frequency
			// or positional information:
			Field pathField = new StringField("path", file.toString(), Field.Store.YES);

			doc.add(pathField);

			// Add the last modified date of the file a field named "modified".
			// Use a LongField that is indexed (i.e. efficiently filterable with
			// NumericRangeFilter). This indexes to milli-second resolution,
			// which is often too fine. You could instead create a number based
			// on year/month/day/hour/minutes/seconds, down the resolution you
			// require.For example the long value 2011021714 would mean February
			// 17, 2011, 2-3 PM. doc.add(new LongPoint("modified",
			// lastModified));
			Field modify = new StringField("modified", Long.toString(lastModified), Field.Store.YES);
			doc.add(modify);

			// Add the contents of the file to a field named "contents". Specify
			// a Reader,so that the text of the file is tokenized and indexed,
			// but not stored. Note that FileReader expects the file to be in
			// UTF-8 encoding. If that's not the case searching for special
			// characters will fail.
			doc.add(new TextField("contents",
					new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {

				// New index, so we just add the document (no old document can
				// be there):
				System.out.println("adding " + file);
				writer.addDocument(doc);
			} else {
				// Existing index (an old copy of this document may have been
				// indexed) so we use updateDocument instead to replace the old
				// one matching the exact path, if present:
				System.out.println("updating " + file);
				writer.updateDocument(new Term("path", file.toString()), doc);
			}
		}
	}

	static void deleteIndex(IndexWriter writer, Path file) throws IOException {

		try (InputStream stream = Files.newInputStream(file)) {
			// make a new, empty document
			Document doc = new Document();

			// Add the path of the file as a field named "path". Use a
			// field that is indexed (i.e. searchable), but don't tokenize
			// the field into separate words and don't index term frequency
			// or positional information:
			Field pathField = new StringField("path", file.toString(), Field.Store.YES);

			doc.add(pathField);

			System.out.println("Deleting index for " + file);
			writer.deleteDocuments(new Term("path", file.toString()));
		}
	}
}