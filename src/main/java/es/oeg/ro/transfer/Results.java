package es.oeg.ro.transfer;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Results{
	private List<Paper> _docs;

	@Override
	public String toString() {
		return "Results [_docs=" + _docs + "]";
	}

	@JsonProperty("docs")
	public List<Paper> get_docs() {
		return _docs;
	}

	public void set_docs(List<Paper> _docs) {
		this._docs = _docs;
	}
}