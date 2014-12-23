package es.oeg.ro.transfer;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthorBSON {
	@JsonProperty private String id;
	
	@JsonProperty private String name;
	
	@JsonProperty private List<Colleague> coll;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Colleague> getColl() {
		return coll;
	}
	public void setColl(List<Colleague> coll) {
		this.coll = coll;
	}

}
