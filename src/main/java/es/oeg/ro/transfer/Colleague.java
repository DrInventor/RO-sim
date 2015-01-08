package es.oeg.ro.transfer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Colleague {
	
	@JsonProperty private String id;
	@JsonProperty private Integer num;
	@JsonProperty private String name;
	
	public String getId() {
		return id;
	}
	@Override
	public String toString() {
		return "Colleague [id=" + id + ", num=" + num + ", name=" + name + "]";
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
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
