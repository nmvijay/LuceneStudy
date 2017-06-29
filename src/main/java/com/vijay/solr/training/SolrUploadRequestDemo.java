package com.vijay.solr.training;

import java.io.File;
import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.util.NamedList;

public class SolrUploadRequestDemo {
	public static void main(String[] args) throws IOException, SolrServerException {
		SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/demo").build();
		ContentStreamUpdateRequest req = new ContentStreamUpdateRequest("/update/extract");
		File pdfFile = new File("/Users/vijay/PDF_FIles/what_is_opentaps.pdf");
		
		req.addFile(pdfFile, "application/pdf");

		NamedList<Object> result = client.request(req);
		System.out.println("Result: " + result);
	}
}
