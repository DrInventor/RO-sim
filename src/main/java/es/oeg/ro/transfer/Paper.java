package es.oeg.ro.transfer;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Paper {
	
	private String _bibcode;
	private String _pubdate;
	private List<String> _keyword;
	private List<String> _author;
	private List<String> _property;
	private String _abstract;
	private List<String> _bibstem;
	private Number _citation_count;
	private String _pub;
	// FIXME ver cómo parsear esto, en el json viene como :
//	"[citations]":
//    {
//        "num_citations": 0,
//        "num_references": 0
//    },
	private List<String> _citations;
	private String _volume;
	private List<String> _database;
	private List<String> _keyword_norm;
	private List<String> _doi;
	private String _year;
	private List<String> _title;
	private List<String> _aff;
	private List<String> _identifier;
	private List<String> _keyword_schema;
	private String _id;
	private List<String> _page;
	
	@JsonProperty("abstract")
	public String get_abstract() {
		return _abstract;
	}
	
	@JsonProperty("aff")
	public List<String> get_aff() {
		return _aff;
	}
	@JsonProperty("author")
	public List<String> get_author() {
		return _author;
	}
	
	@JsonProperty("bibcode")
	public String get_bibcode() {
		return _bibcode;
	}
	@JsonProperty("bibstem")
	public List<String> get_bibstem() {
		return _bibstem;
	}
	
	@JsonProperty("citation_count")
	public Number get_citation_count() {
		return _citation_count;
	}
	@JsonProperty("database")
	public List<String> get_database() {
		return _database;
	}
	
	@JsonProperty("doi")
	public List<String> get_doi() {
		return _doi;
	}
	@JsonProperty("id")
	public String get_id() {
		return _id;
	}
	
	@JsonProperty("identifier")
	public List<String> get_identifier() {
		return _identifier;
	}
	@JsonProperty("keyword")
	public List<String> get_keyword() {
		return _keyword;
	}
	
	@JsonProperty("keyword_norm")
	public List<String> get_keyword_norm() {
		return _keyword_norm;
	}
	@JsonProperty("keyword_schema")
	public List<String> get_keyword_schema() {
		return _keyword_schema;
	}
	
	@JsonProperty("page")
	public List<String> get_page() {
		return _page;
	}
	@JsonProperty("property")
	public List<String> get_property() {
		return _property;
	}
	
	@JsonProperty("pub")
	public String get_pub() {
		return _pub;
	}
	@JsonProperty("pubdate")
	public String get_pubdate() {
		return _pubdate;
	}
	
	@JsonProperty("title")
	public List<String> get_title() {
		return _title;
	}
	@JsonProperty("volume")
	public String get_volume() {
		return _volume;
	}
	
//	@JsonProperty("citations")
//	public List<String> get_citations() {
//		return _citations;
//	}
//	public void set_citations(List<String> _citations) {
//		this._citations = _citations;
//	}
	
	@JsonProperty("year")
	public String get_year() {
		return _year;
	}
	public void set_abstract(String _abstract) {
		this._abstract = _abstract;
	}
	
	public void set_aff(List<String> _aff) {
		this._aff = _aff;
	}
	public void set_author(List<String> _author) {
		this._author = _author;
	}
	
	public void set_bibcode(String _bibcode) {
		this._bibcode = _bibcode;
	}
	public void set_bibstem(List<String> _bibstem) {
		this._bibstem = _bibstem;
	}
	
	public void set_citation_count(Number _citation_count) {
		this._citation_count = _citation_count;
	}
	public void set_database(List<String> _database) {
		this._database = _database;
	}
	
	public void set_doi(List<String> _doi) {
		this._doi = _doi;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	
	public void set_identifier(List<String> _identifier) {
		this._identifier = _identifier;
	}
	public void set_keyword(List<String> _keyword) {
		this._keyword = _keyword;
	}
	
	public void set_keyword_norm(List<String> _keyword_norm) {
		this._keyword_norm = _keyword_norm;
	}
	public void set_keyword_schema(List<String> _keyword_schema) {
		this._keyword_schema = _keyword_schema;
	}
	
	public void set_page(List<String> _page) {
		this._page = _page;
	}
	public void set_property(List<String> _property) {
		this._property = _property;
	}
	
	public void set_pub(String _pub) {
		this._pub = _pub;
	}
	public void set_pubdate(String _pubdate) {
		this._pubdate = _pubdate;
	}
	
	public void set_title(List<String> _title) {
		this._title = _title;
	}
	public void set_volume(String _volume) {
		this._volume = _volume;
	}
	
	public void set_year(String _year) {
		this._year = _year;
	}
	@Override
	public String toString() {
		return "Paper [_bibcode=" + _bibcode + ", _author=" + _author
				+ ", _abstract=" + _abstract + ", _doi=" + _doi + ", _title="
				+ _title + ", _identifier=" + _identifier + ", _id=" + _id
				+ "]";
	}

}
