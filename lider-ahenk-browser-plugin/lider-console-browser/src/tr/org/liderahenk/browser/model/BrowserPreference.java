package tr.org.liderahenk.browser.model;

import java.io.Serializable;

public class BrowserPreference implements Serializable {

	private static final long serialVersionUID = 2671227394653786584L;

	private String preferenceName;

	private String value;

	public BrowserPreference(String preferenceName, String value) {
		this.preferenceName = preferenceName;
		this.value = value;
	}

	public BrowserPreference() {
	}

	public String getPreferenceName() {
		return preferenceName;
	}

	public void setPreferenceName(String preferenceName) {
		this.preferenceName = preferenceName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((preferenceName == null) ? 0 : preferenceName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BrowserPreference other = (BrowserPreference) obj;
		if (preferenceName == null) {
			if (other.preferenceName != null)
				return false;
		} else if (!preferenceName.equals(other.preferenceName))
			return false;
		return true;
	}

}

