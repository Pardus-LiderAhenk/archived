package tr.org.liderahenk.backup.model;

import java.io.Serializable;

/**
 * Model class for backup parameter items.
 * 
 * @author <a href="mailto:seren.unal@agem.com.tr">Seren Ünal</a>
 *
 */
public class BackupParametersListItem implements Serializable {

	private static final long serialVersionUID = -1215191189845829199L;

	private String  sourcePath;
	private String  excludePattern;
	private String  logicalVolume;
	private String  virtualGroup;
	private String  logicalVolumeSize;
	private boolean recursive;
	private boolean preserveGroup;
	private boolean preserveOwner;
	private boolean preservePermissions;
	private boolean archive;
	private boolean compress;
	private boolean existingOnly;

	public BackupParametersListItem() {
		super();
	}

	public BackupParametersListItem(String sourcePath, String excludePattern, String logicalVolume, String virtualGroup,
			String logicalVolumeSize, boolean recursive, boolean preserveGroup, boolean preserveOwner,
			boolean preservePermissions, boolean archive, boolean compress, boolean existingOnly) {
		super();
		this.sourcePath = sourcePath;
		this.excludePattern = excludePattern;
		this.logicalVolume = logicalVolume;
		this.virtualGroup = virtualGroup;
		this.logicalVolumeSize = logicalVolumeSize;
		this.recursive = recursive;
		this.preserveGroup = preserveGroup;
		this.preserveOwner = preserveOwner;
		this.preservePermissions = preservePermissions;
		this.archive = archive;
		this.compress = compress;
		this.existingOnly = existingOnly;
	}


	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getExcludePattern() {
		return excludePattern;
	}

	public void setExcludePattern(String excludePattern) {
		this.excludePattern = excludePattern;
	}

	public String getLogicalVolume() {
		return logicalVolume;
	}

	public void setLogicalVolume(String logicalVolume) {
		this.logicalVolume = logicalVolume;
	}

	public String getVirtualGroup() {
		return virtualGroup;
	}

	public void setVirtualGroup(String virtualGroup) {
		this.virtualGroup = virtualGroup;
	}

	public String getLogicalVolumeSize() {
		return logicalVolumeSize;
	}

	public void setLogicalVolumeSize(String logicalVolumeSize) {
		this.logicalVolumeSize = logicalVolumeSize;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public boolean isPreserveGroup() {
		return preserveGroup;
	}

	public void setPreserveGroup(boolean preserveGroup) {
		this.preserveGroup = preserveGroup;
	}

	public boolean isPreserveOwner() {
		return preserveOwner;
	}

	public void setPreserveOwner(boolean preserveOwner) {
		this.preserveOwner = preserveOwner;
	}

	public boolean isPreservePermissions() {
		return preservePermissions;
	}

	public void setPreservePermissions(boolean preservePermissions) {
		this.preservePermissions = preservePermissions;
	}

	public boolean isArchive() {
		return archive;
	}

	public void setArchive(boolean archive) {
		this.archive = archive;
	}

	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public boolean isExistingOnly() {
		return existingOnly;
	}

	public void setExistingOnly(boolean existingOnly) {
		this.existingOnly = existingOnly;
	}

}
