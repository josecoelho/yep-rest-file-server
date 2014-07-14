package file.server.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import file.server.model.bean.Metadata;
import file.server.service.FileService;

@Component
@Path("/paths")
@Produces(MediaType.APPLICATION_JSON)
public class PathRest {

	@Autowired
	private FileService fileService;

	@GET
	public Response gett(@QueryParam("path") String path) throws Exception {
		
		
		List<Metadata> list = fileService.loadByPath(path);

		return Response.ok(list.toArray(new Metadata[list.size()])).build();

	}
	


}
