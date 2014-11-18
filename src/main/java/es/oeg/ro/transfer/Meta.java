package es.oeg.ro.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//@JsonIgnoreProperties(value="api-version")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meta{
	
	private Object _api_version;
	private Number _count;
	private Number _hits;
	private String _qtime;
	private String _query;
	
	
//	@JsonProperty("api-version")
//	public Object get_api_version() {
//		return _api_version;
//	}
//	public void set_api_version(Object _api_version) {
//		this._api_version = _api_version;
//	}
	
	@JsonProperty("count")
	public Number get_count() {
		return _count;
	}
	public void set_count(Number _count) {
		this._count = _count;
	}
	
	@JsonProperty("hits")
	public Number get_hits() {
		return _hits;
	}
	public void set_hits(Number _hits) {
		this._hits = _hits;
	}
	
	@JsonProperty("qtime")
	public String get_qtime() {
		return _qtime;
	}
	public void set_qtime(String _qtime) {
		this._qtime = _qtime;
	}
	
	@JsonProperty("query")
	public String get_query() {
		return _query;
	}
	public void set_query(String _query) {
		this._query = _query;
	}
	
}