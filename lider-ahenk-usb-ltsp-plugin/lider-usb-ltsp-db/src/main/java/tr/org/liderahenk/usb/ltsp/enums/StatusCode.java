package tr.org.liderahenk.usb.ltsp.enums;

public enum StatusCode {

	PRIVILEGED(1), UNPRIVILEGED(0), ERR_NO_FUSE_GROUP(2), ERR_NO_USER(3), ERR_UNKNOWN(4);

	private int id;

	private StatusCode(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	/**
	 * Provide mapping enums with a fixed ID in JPA (a more robust alternative
	 * to EnumType.String and EnumType.Ordinal)
	 * 
	 * @param id
	 * @return related StateValues enum
	 * @see http://blog.chris-ritchie.com/2013/09/mapping-enums-with-fixed-id-in
	 *      -jpa.html
	 * 
	 */
	public static StatusCode getType(Integer id) {
		if (id == null) {
			return null;
		}
		for (StatusCode position : StatusCode.values()) {
			if (id.equals(position.getId())) {
				return position;
			}
		}
		throw new IllegalArgumentException("No matching type for id: " + id);
	}

}
