package file.server.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import file.server.model.bean.Metadata;
import file.server.service.FileService;

@Component
@Path("/files")
@Produces(MediaType.APPLICATION_JSON)
public class FilesRest {

	@Autowired
	private FileService fileService;

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response gett(
			@PathParam("id") Long id,
			@QueryParam("revision") @DefaultValue("0") Long revision
			) throws Exception {
			
			Metadata metadata = fileService.load(id,revision);
			if(metadata == null) {
				throw new NotFoundException(); 
			}
			String header = String.format("attachment;filename=\"%s\"",metadata.getName());
			
			return Response.ok(metadata.getFile(),metadata.getMimeType()).header("Content-Disposition",header).build();
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response post(@FormDataParam("file") InputStream file,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@FormDataParam("file") FormDataBodyPart bodyPart,
			@FormDataParam("path") String path,
			@FormDataParam("overwrite") @DefaultValue("true") Boolean overwrite
			) throws Exception {

			
			Metadata fMeta = new Metadata();
			fMeta.setMimeType(bodyPart.getMediaType().toString());
			fMeta.setIsDir(false);
			fMeta.setName(fileDetail.getFileName());
			fMeta.setPath(path);

			fMeta = fileService.upload(file,fMeta,overwrite);
			
			return Response.ok(fMeta).build();
		
	}
	

}
