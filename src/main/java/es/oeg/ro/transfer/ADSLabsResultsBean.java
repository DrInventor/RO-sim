package es.oeg.ro.transfer;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ADSLabsResultsBean {
	
	@JsonProperty("_id")
	private String _id;
	private Meta meta;
	private Results results;
	
	public Results getResults() {
		return results;
	}

	public void setResults(Results results) {
		this.results = results;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	@Override
	public String toString() {
		return "ADSLabsResultsBean [_id=" + _id + ", meta=" + meta
				+ ", results=" + results + "]";
	}

	
	

}
