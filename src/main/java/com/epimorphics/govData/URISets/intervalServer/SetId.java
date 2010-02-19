/******************************************************************
 * File:        SecId.java
 * Created by:  Stuart Williams
 * Created on:  13 Feb 2010
 * 
 * (c) Copyright 2010, Epimorphics Limited
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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

import com.epimorphics.govData.URISets.intervalServer.gregorian.GregorianURITemplate;
import com.epimorphics.govData.URISets.intervalServer.interval.SetDoc;
import com.epimorphics.govData.URISets.intervalServer.ukcal.BritishCalendarURITemplate;
import com.epimorphics.govData.URISets.intervalServer.ukgovcal.UkGovCalURITemplate;



@Path(GregorianURITemplate.SET_STEM)
public class SetId {
@Context UriInfo ui;

	final static String PATH_REGEX = SetDoc.PATH_REGEX + "|"+
									 GregorianURITemplate.PATH_REGEX +"|" + 
	                                 BritishCalendarURITemplate.PATH_REGEX + "|"+
	                                 UkGovCalURITemplate.PATH_REGEX;
	
	@GET
	@Path("{path:"+PATH_REGEX+"}" )
	public Response redirector(
			@PathParam("path")  String path){
		String fullURI = ui.getAbsolutePath().toString();
		fullURI = fullURI.replaceFirst(GregorianURITemplate.SET_STEM, GregorianURITemplate.DOC_STEM);

		ResponseBuilder resp = null;
		try {
			resp = Response.seeOther(new URI(fullURI));
		} catch (URISyntaxException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return resp.build();
		
	}
}