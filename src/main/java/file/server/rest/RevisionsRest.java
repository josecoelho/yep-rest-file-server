package file.server.rest;

import javax.ws.rs.GET;
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

import file.server.model.bean.Metadata;
import file.server.service.FileService;

@Component
@Produces(MediaType.APPLICATION_JSON)
@Path("/revisions")
public class RevisionsRest {

	@Autowired
	private FileService fileService;
	
	@GET
	@Path("/{id}")
	public Response gett(
			@PathParam("id") Long id) {
		
		try {
			Metadata metadata = fileService.getRevisions(id);
			
			if(metadata == null) {
				throw new NotFoundException();
			}
			
			return Response.ok(metadata).build();
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			throw new WebApplicationException(e);
		} 
		
		
	}
	
}
