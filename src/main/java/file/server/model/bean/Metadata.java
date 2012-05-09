package file.server.model.bean;

import java.io.File;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import javolution.util.FastList;

@XmlRootElement
public class Metadata implements DBObj {

	private long id;
	private String name;
	private Boolean isDir;
	private String mimeType;
	private String path;
	private long currentRevision;

	private List<Revision> revisions;
	
	private File file;
	
	
	/**
	 * Create a new revision on this metadata,
	 * 
	 * after save that on database and pick the id, the method updateCurrentRevision must be called.
	 * @return created revision
	 */
	public Revision createRevision() {
		Revision revision = new Revision(this.id);
		
		if(revisions == null) {
			revisions = new FastList<Revision>();
		}
		
		revisions.add(revision);
		
		return revision;
	}
	
	/**
	 * Change the currentRevision id to the last revision created. 
	 * @return the id of last revision
	 */
	public long updateCurrentRevision() {
		if(revisions != null) {
			this.currentRevision = revisions.get(revisions.size()-1).getId();
		}
		
		return currentRevision;
	}
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	public Boolean getIsDir() {
		return isDir;
	}

	public void setIsDir(Boolean isDir) {
		this.isDir = isDir;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}





	public List<Revision> getRevisions() {
		return revisions;
	}

	public void setRevisions(List<Revision> revisions) {
		this.revisions = revisions;
	}

	@XmlTransient
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public long getCurrentRevision() {
		return currentRevision;
	}

	public void setCurrentRevision(long currentRevision) {
		this.currentRevision = currentRevision;
	}
}
