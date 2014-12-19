package es.oeg.ro.transfer;

public class Author {
	private String name;
	private int publications;
	
	public int getPublications() {
		return publications;
	}
	public void setPublications(int publications) {
		this.publications = publications;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Author(String name, int publications) {
		super();
		this.name = name;
		this.publications = publications;
	}
	public Author(String name) {
		super();
		this.name = name;
	}
	public int incrementPublication(){
		publications++;
		return publications;
	}
}
