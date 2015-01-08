package es.oeg.ro.transfer;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthorBSON {
	@JsonProperty private String _id;
	
	@JsonProperty private String name;
	
	@JsonProperty private List<Colleague> coll;
	
	public String getId() {
		return _id;
	}
	public void setId(String id) {
		this._id = id;
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
	@Override
	public String toString() {
		return "AuthorBSON [_id=" + _id + ", name=" + name + ", coll=" + coll
				+ "]";
	}

}
