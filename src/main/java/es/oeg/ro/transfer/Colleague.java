package es.oeg.ro.transfer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Colleague {
	
	@JsonProperty private String id;
	@JsonProperty private Integer num;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
}
