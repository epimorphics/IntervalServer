/******************************************************************
 * File:        UkSecId.java
 * Created by:  Stuart Williams
 * Created on:  13 Feb 2010
 * 
 * (c) Copyright 2010, Epimorphics Limited
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 * $Id:  $
 *****************************************************************/

package com.epimorphics.govData.URISets.intervalServer;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.epimorphics.govData.URISets.intervalServer.gregorian.GregorianCalURITemplate;
import com.epimorphics.govData.URISets.intervalServer.interval.SetDoc;
import com.epimorphics.govData.URISets.intervalServer.ukcal.UkCalURITemplate;
import com.epimorphics.govData.URISets.intervalServer.ukgovcal.UkGovCalURITemplate;



@Path(GregorianCalURITemplate.SET_STEM)
public class SetId {
@Context UriInfo ui;

	final static String PATH_REGEX = SetDoc.PATH_REGEX + "|"+
									 GregorianCalURITemplate.PATH_REGEX +"|" + 
	                                 UkCalURITemplate.PATH_REGEX + "|"+
	                                 UkGovCalURITemplate.PATH_REGEX;
	
	@GET
	@Path("{path:"+PATH_REGEX+"}" )
	public Response redirector(
			@PathParam("path")  String path){
		
		String base = (BaseURI.getBase()== null ? ui.getBaseUri() : BaseURI.getBase()).toString();
		String pathUri = ui.getPath().replaceFirst(GregorianCalURITemplate.SET_STEM, GregorianCalURITemplate.DOC_STEM);

		ResponseBuilder resp = null;
		try {
			resp = Response.seeOther(new URI(base+pathUri));
		} catch (URISyntaxException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return resp.build();
		
	}
}