/******************************************************************
 * File:        UkGovId.java
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

package com.epimorphics.govData.URISets.intervalServer.ukgovcal;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.epimorphics.govData.URISets.intervalServer.BaseURI;


public class UkGovId extends UkGovCalURITemplate{
	protected @Context UriInfo ui;

	/**
	 * id->doc 303 Redirections for URI of the form /id/yyyy-mm-ddThh:mm:ss
	 */
	public Response redirector() {
		String base = (BaseURI.getBase()== null ? ui.getBaseUri() : BaseURI.getBase()).toString();
		String path = ui.getPath().replaceFirst(ID_STEM, DOC_STEM);

		ResponseBuilder resp = null;
		try {
			resp = Response.seeOther(new URI(base+path));
		} catch (URISyntaxException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return resp.build();
	}
}