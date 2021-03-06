/*
 * Copyright (c) 2015, Person Chao Liu. All rights reserved.
 */

package me.chaoliu.learning.solr4_X.cursor;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import me.chaoliu.learning.solr4_X.solrserver.HttpSolrServerFactory;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CursorMarkParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Solr Cursor Mark Demo
 * 
 * @author Chao Liu
 * @since SolrDemo 1.0
 */
public class CursorMark {

	private SolrServer solrServer = HttpSolrServerFactory
			.getSolrServerInstance();
	private static Logger log = LoggerFactory.getLogger(CursorMark.class);

	public void cursorMark() throws SolrServerException, IOException {
		SolrQuery q = (new SolrQuery("*:*")).setRows(10).setSort(
				SortClause.asc("id"));
		String cursorMark = CursorMarkParams.CURSOR_MARK_START;
		boolean done = false;
		while (!done) {
			q.set(CursorMarkParams.CURSOR_MARK_PARAM, cursorMark);
			QueryResponse rsp = solrServer.query(q);
			String nextCursorMark = rsp.getNextCursorMark();
			doCustomProcessingOfResults(rsp);
			if (cursorMark.equals(nextCursorMark)) {
				done = true;
			}
			cursorMark = nextCursorMark;
		}
	}

	public void doCustomProcessingOfResults(QueryResponse rsp)
			throws IOException {

		SolrDocumentList results = rsp.getResults();

		for (int i = 0; i < results.size(); i++) {
			SolrDocument solrDocument = results.get(i);
			Iterator<Entry<String, Object>> iterator = solrDocument.iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> next = iterator.next();
				log.info(next.getKey() + ":" + next.getValue() + "\t");
			}
		}
	}

	public static void main(String[] args) throws SolrServerException,
			IOException {
		CursorMark cmd = new CursorMark();
		long start = System.currentTimeMillis();
		cmd.cursorMark();
		long end = System.currentTimeMillis();
		log.info("runing time: " + (end - start) / 1000 + "s");
	}
}
