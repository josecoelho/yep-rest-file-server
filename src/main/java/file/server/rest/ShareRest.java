package file.server.rest;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import file.server.model.bean.UserPathPermission;
import file.server.service.FileService;

@Component
@Path("/share")
@Produces(MediaType.APPLICATION_JSON)
public class ShareRest {

	@Autowired
	private FileService fileService;

	@GET
	public Response gett(
			@QueryParam("sid") String sid,
			@QueryParam("path") String path,
			@QueryParam("userId") Long userId, 
			@QueryParam("read") @DefaultValue("true") Boolean read, 
			@QueryParam("write") @DefaultValue("true") Boolean write) throws Exception {

		fileService.validateSession(sid);
		
		UserPathPermission perm = fileService.share(userId, path, read, write);
		
		return Response.ok(perm).build();
	}
	


}
