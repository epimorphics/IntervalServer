/******************************************************************
 * File:        SetDoc.java
 * Created by:  skw
 * Created on:  16 Feb 2010
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
package com.epimorphics.govData.URISets.intervalServer.interval;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.epimorphics.govData.URISets.intervalServer.URITemplate;
import com.epimorphics.govData.URISets.intervalServer.gregorian.GregorianCalSetDoc;
import com.epimorphics.govData.URISets.intervalServer.gregorian.GregorianCalURITemplate;
import com.epimorphics.govData.URISets.intervalServer.ukcal.UkCalSetDoc;
import com.epimorphics.govData.URISets.intervalServer.ukcal.UkCalURITemplate;
import com.epimorphics.govData.URISets.intervalServer.ukgovcal.UkGovCalSetDoc;
import com.epimorphics.govData.URISets.intervalServer.ukgovcal.UkGovCalURITemplate;
import com.epimorphics.govData.URISets.intervalServer.util.MediaTypeUtils;
import com.epimorphics.govData.vocabulary.FOAF;
import com.epimorphics.govData.vocabulary.SKOS;
import com.epimorphics.govData.vocabulary.VOID;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author skw
 *
 */
@Path(URITemplate.DOC_STEM+SetDoc.INTERVAL_SET+URITemplate.SET_EXT_PATTERN)
public class SetDoc extends Doc {
		public static final String INTERVAL_SET = "interval";
		public static final String PATH_REGEX = "("+INTERVAL_SET+")";
		public static final String INTERVAL_SET_LABEL = "Combined Interval and Instances Dataset";
		
	//URI setURI;
	
	
	@GET
	public Response getSetResponse(@PathParam(EXT2_TOKEN) String ext2) {
		MediaType mt;
		base = getBaseUri();
		//Remove leading .
		ext2 = (ext2!=null && !ext2.equals("")) ? ext2.substring(1) : null ; //skip the '.'
		try {
			// Sort out media type from extent or pick media type and ext.
			if (ext2 != null && !ext2.equals("")) {
				mt = MediaTypeUtils.extToMediaType(ext2);
				loc = new URI(base + ui.getPath());
				ext = ext2;
				contentURI = new URI(loc.toString());
				setURI = new URI(base + SET_STEM + INTERVAL_SET);
			} else {
				mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
				ext = MediaTypeUtils.getExtOfMediaType(mt);
				loc = new URI(base + ui.getPath());
				contentURI = new URI(loc.toString()+ "."+ ext);
				setURI = new URI(base + SET_STEM + INTERVAL_SET);
			}
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		populateCalSet();

		if (lang.equals("JSON"))
			return doGetJson().type(mt).contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
	
	private void populateCalSet() {
		Resource r_set = model.createResource(setURI.toString(),VOID.Dataset);
		Resource r_doc = model.createResource(loc.toString(), FOAF.Document);
		initSetModel(r_set, r_doc, INTERVAL_SET_LABEL);
		
		r_thisTemporalEntity = r_set;
		
		model.add(r_set, RDFS.label, INTERVAL_SET_LABEL, "en");
		model.add(r_set, SKOS.prefLabel, INTERVAL_SET_LABEL,"en");
		
		model.add(r_set, RDFS.comment, "A dataset of Unzoned Gregorian Calendar, British Calendar and UK Government Buisness Calendar aligned " +
									   "time intervals.", "en");
		model.add(r_set, RDF.type, VOID.Dataset);

		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
								
		model.add(r_set, VOID.subset,        UkCalSetDoc.createCalSet(model, base+SET_STEM+UkCalURITemplate.CALENDAR_SET));
		model.add(r_set, VOID.subset,     UkGovCalSetDoc.createCalSet(model, base+SET_STEM+UkGovCalURITemplate.CALENDAR_SET));
		model.add(r_set, VOID.subset, GregorianCalSetDoc.createCalSet(model, base+SET_STEM+GregorianCalURITemplate.CALENDAR_SET));
	}
}
