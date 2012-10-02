package file.server.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.naming.NoPermissionException;
import javax.ws.rs.WebApplicationException;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.filters.Caption;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.NotFoundException;

import file.server.model.bean.Metadata;
import file.server.model.bean.Revision;
import file.server.model.bean.UserPathPermission;
import file.server.model.dao.MetadataDAO;
import file.server.model.dao.RevisionDAO;
import file.server.model.dao.SessionDao;
import file.server.model.dao.UserPathPermissionDAO;

@Service
public class FileService {

	@Autowired
	private MetadataDAO metadataDAO;

	@Autowired
	private RevisionDAO revisionDAO;
	
	@Autowired
	private UserPathPermissionDAO userPathPermissionDAO;
	
	@Autowired
	private SessionDao sessionDao;
	
	private static List<String> canPutWatermark;

	
	public void validateSession(String sid) throws Exception {
		
		if(!sessionDao.validateSession(sid)) {
			throw new WebApplicationException(403);
		}
		
	}
	
	/**
	 * Upload file, if this file already exists and overwrite is true, then a
	 * new revision of file will be generated
	 * 
	 * @param file
	 * @param metadata
	 * @param overwrite2 
	 * @return
	 */
	public Metadata upload(InputStream file, Metadata metadata, boolean draft,  boolean overwrite)
			throws Exception {

		Metadata existsMeta = metadataDAO.findByPathAndName(metadata.getPath(),
				metadata.getName());

		if (existsMeta == null) {
			metadata.createRevision();
			metadataDAO.insert(metadata);
			revisionDAO.insert(metadata.createRevision());
			metadata.updateCurrentRevision();
			metadataDAO.update(metadata);
		} else if (overwrite) {
			revisionDAO.loadRevisions(existsMeta);
			revisionDAO.insert(existsMeta.createRevision());
			existsMeta.updateCurrentRevision();
			metadataDAO.update(existsMeta);

			metadata = existsMeta;
		} else {
			// nothing to do
			throw new FileNotFoundException();
		}
		
		
		// save file
		String pathToSaveFile = GedFileUtil.getPathById(metadata.getCurrentRevision());
		File fileToSave = new File(pathToSaveFile);
		writeToFile(file, fileToSave);
		
		if(draft && canPutWaterMark(metadata)) {
			putWaterMark(fileToSave,metadata);
		}

		return metadata;

	}

	private void putWaterMark(File file,Metadata metadata) throws IOException {
		
		BufferedImage bufferedImage = ImageIO.read(file);
		
		// Set up the caption properties
		String caption = "RASCUNHO";
		Font font = new Font("Monospaced", Font.PLAIN, 100);
		Color c = Color.GRAY;
		float opacity = 0.6f;
		Position[] positions = {Positions.TOP_LEFT,Positions.CENTER,Positions.BOTTOM_RIGHT};
		int insetPixels = 0;

		// Apply caption to the image
		Builder<BufferedImage> builder = Thumbnails.of(bufferedImage).size(bufferedImage.getHeight(),bufferedImage.getWidth()).outputFormat(metadata.getFileExtension());
		
		for(Position position: positions) {
			builder.addFilter(new Caption(caption, font, c, opacity, position, insetPixels));
		}
		BufferedImage result = builder.asBufferedImage();
		
		ImageIO.write(result, metadata.getFileExtension(), file);
	}

	private boolean canPutWaterMark(Metadata metadata) {
		return metadata.getMimeType().startsWith("image/");
	}

	/**
	 * Load file
	 * 
	 * @param id 
	 * @param revision
	 * @return
	 * @throws Exception 
	 */
	public Metadata load(Long id, Long revision) throws Exception {
		Metadata retVal = metadataDAO.get(id);
		Revision rev = null;
		File file = null;
		Long revisionId;
		
		if(retVal != null) {
			
			//if sent revision and thats not the current revision, then load that revision
			if(revision > 0 && retVal.getCurrentRevision() != revision) {
				rev = revisionDAO.get(revision);
				if(rev.getMetadataId() == retVal.getId()) {
					revisionId = rev.getId();
				} else {
					throw new NotFoundException("invalid revision");
				}
			
			} else {
				revisionId = retVal.getCurrentRevision();
			}
		
			file = new File(GedFileUtil.getPathById(revisionId));
			retVal.setFile(file);
		}

		return retVal;
	}

	public Metadata getRevisions(long id) throws Exception {
		
		Metadata metadata = metadataDAO.get(id);
		revisionDAO.loadRevisions(metadata);
				
		return metadata;
	}
	
	
	private void writeToFile(InputStream uploadedInputStream,
			File uploadedFileLocation) {

		try {
			uploadedFileLocation.getParentFile().mkdirs();
			
			OutputStream out = new FileOutputStream(uploadedFileLocation);
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(uploadedFileLocation);
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public List<Metadata> loadByPath(String path) throws Exception {
		return metadataDAO.findByPath(path);
	}
	
	public UserPathPermission share(long userId, String path,boolean read, boolean write) throws Exception {
		
		UserPathPermission perm = userPathPermissionDAO.findByUserAndPath(userId, path);
		if(perm != null) {
			perm.setRead(read);
			perm.setWrite(write);
		} else {
			perm = new UserPathPermission(userId,path,read,write);
		}
		
		userPathPermissionDAO.save(perm);
		
		return perm;
	}
	

}
