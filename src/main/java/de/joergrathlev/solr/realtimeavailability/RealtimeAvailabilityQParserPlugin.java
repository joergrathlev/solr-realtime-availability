package de.joergrathlev.solr.realtimeavailability;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BitsFilteredDocIdSet;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.DocIdBitSet;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.SolrConstantScoreQuery;

public class RealtimeAvailabilityQParserPlugin extends QParserPlugin {

	public void init(NamedList arg0) {
	}

	@Override
	public QParser createParser(String qstr, SolrParams localParams,
			SolrParams params, SolrQueryRequest req) {
		return new QParser(qstr, localParams, params, req) {
			@Override
			public Query parse() throws ParseException {
				return new SolrConstantScoreQuery(new Filter() {
					@Override
					public DocIdSet getDocIdSet(AtomicReaderContext context,
							Bits acceptDocs) throws IOException {
						int maxDoc = context.reader().maxDoc();
						
						System.out.println("numDocs: " + context.reader().numDocs());
						System.out.println("maxDoc:  " + maxDoc);
						for (int i = 0; i < maxDoc; i++) {
							System.out.format("docId: %2d  [%b]  (id: %s)%n",
									i + context.docBase,
									acceptDocs == null ? true : acceptDocs.get(i),
									context.reader().document(i).get("id"));
						}
						
						req.getSearcher().cacheLookup("availabilityCache", "bitset");
						OpenBitSet set = (OpenBitSet) req.getSearcher().cacheLookup("availabilityCache", "bitset");
						if (set == null) {
							set = new OpenBitSet(req.getSearcher().maxDoc());
							set.set(0, req.getSearcher().maxDoc());
							req.getSearcher().cacheInsert("availabilityCache", "bitset", set);
						}
						return BitsFilteredDocIdSet.wrap(set, acceptDocs);
					}
				});
			}
		};
	}

}
