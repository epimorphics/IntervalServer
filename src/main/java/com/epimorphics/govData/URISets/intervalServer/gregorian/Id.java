package com.epimorphics.govData.URISets.intervalServer.gregorian;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;


public class Id extends URITemplate{
	protected @Context UriInfo ui;

	/**
	 * id->doc 303 Redirections for URI of the form /id/yyyy-mm-ddThh:mm:ss
	 */
	public Response redirector() {
		String fullURI = ui.getAbsolutePath().toString();
		fullURI = fullURI.replaceFirst(ID_STEM, DOC_STEM);

		ResponseBuilder resp = null;
		try {
			resp = Response.seeOther(new URI(fullURI));
		} catch (URISyntaxException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return resp.build();
	}
}