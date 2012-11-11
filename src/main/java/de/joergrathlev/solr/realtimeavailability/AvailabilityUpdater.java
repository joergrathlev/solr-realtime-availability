package de.joergrathlev.solr.realtimeavailability;

import java.io.IOException;
import java.net.URL;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.OpenBitSet;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.request.SolrRequestHandler;
import org.apache.solr.response.SolrQueryResponse;

public class AvailabilityUpdater implements SolrRequestHandler {

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public Category getCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSource() {
		// TODO Auto-generated method stub
		return null;
	}

	public URL[] getDocs() {
		// TODO Auto-generated method stub
		return null;
	}

	public NamedList getStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	public void init(NamedList args) {
		// TODO Auto-generated method stub

	}

	public void handleRequest(SolrQueryRequest req, SolrQueryResponse rsp) {
		OpenBitSet set = (OpenBitSet) req.getSearcher().cacheLookup("availabilityCache", "bitset");
		if (set == null) {
			set = new OpenBitSet(req.getSearcher().maxDoc());
			set.set(0, req.getSearcher().maxDoc());
		}
		String id = req.getParams().get("id");
		try {
			long docId = req.getSearcher().lookupId(new BytesRef(id));
			set.clear(docId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rsp.setHttpCaching(false);
	}

}
