package es.oeg.ro.transfer;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Authors {
	
	List<Author> list;
	
	public Authors(){
		list = new ArrayList<Author>();
	}
	
	@Override
	public String toString() {
		return "Results [_authors=" + list + "]";
	}

	@JsonProperty("list")
	public List<Author> getList() {
		return list;
	}

	public void setList(List<Author> _docs) {
		this.list = _docs;
	}
	
	public void add(Author auth){
		if (list == null)
			list = new ArrayList<>();
		list.add(auth);
	}

	public Author search( String location) {
	    for(Author o : list) {
	        if(o != null && o.getName().equals(location)) {
	            return o;
	        }
	    }
	    return null;
	}
}
