/******************************************************************
 * File:        InstantDoc.java
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

package com.epimorphics.govData.URISets.intervalServer.gregorian;

import java.net.URI;
import java.util.Calendar;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.epimorphics.govData.URISets.intervalServer.util.CalendarUtils;
import com.epimorphics.govData.URISets.intervalServer.util.GregorianOnlyCalendar;
import com.epimorphics.govData.vocabulary.INTERVALS;
import com.epimorphics.govData.vocabulary.SKOS;
import com.epimorphics.govData.vocabulary.TIME;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

@Path(URITemplate.INSTANT_DOC_STEM)
public class InstantDoc extends Doc {
	
	protected void reset(int year, int month, int day, int hour, int min, int sec) {
		reset();
		this.year  = year;
		this.month = month;
		this.half =((month-1)/6)+1;
		this.quarter = ((month-1)/3)+1;
		this.day = day;
		this.hour = hour;
		this.min = min;
		this.sec = sec;
		
		startTime = new GregorianOnlyCalendar(Locale.UK);
		startTime.setLenient(false);
		startTime.set(year, month-1, day, hour, min, sec);
	}

	@GET
	@Path(INSTANT_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN)  int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day,
			@PathParam(HOUR_TOKEN)	int hour,
 			@PathParam(MINUTE_TOKEN) int min,
 			@PathParam(SECOND_TOKEN) int sec,
			@PathParam(EXT_TOKEN)  String ext ) {
		reset(year, month, day, hour, min, sec);
		this.ext   = ext;
		return doGet();
	}

	@GET
	@Path(INSTANT_PATTERN)
	@Produces("text/plain")
	public Response getNTriple(
			@PathParam(YEAR_TOKEN)   int year,
			@PathParam(MONTH_TOKEN)  int month,
			@PathParam(DAY_TOKEN)    int day,
			@PathParam(HOUR_TOKEN)   int hour,
			@PathParam(MINUTE_TOKEN) int min,
			@PathParam(SECOND_TOKEN) int sec) {
		reset(year, month, day, hour, min, sec);

		return doGetNTriple().contentLocation(URI.create(ui.getPath()+"."+EXT_NT)).build();
	}

	@GET
	@Path(INSTANT_PATTERN)
	@Produces("application/rdf+xml")
	public Response getRDF(
			@PathParam(YEAR_TOKEN)   int year,
			@PathParam(MONTH_TOKEN)  int month,
			@PathParam(DAY_TOKEN)    int day,
			@PathParam(HOUR_TOKEN)   int hour,
			@PathParam(MINUTE_TOKEN) int min,
			@PathParam(SECOND_TOKEN) int sec) {
		reset(year, month, day, hour, min, sec);
		return doGetRDF().contentLocation(URI.create(ui.getPath()+"."+EXT_RDF)).build();

	}
	@GET
	@Path(INSTANT_PATTERN)
	@Produces("application/json")
	public Response getJson(
			@PathParam(YEAR_TOKEN)   int year,
			@PathParam(MONTH_TOKEN)  int month,
			@PathParam(DAY_TOKEN)    int day,
			@PathParam(HOUR_TOKEN)   int hour,
			@PathParam(MINUTE_TOKEN) int min,
			@PathParam(SECOND_TOKEN) int sec) {
		reset(year, month, day, hour, min, sec);
		return doGetJson().contentLocation(URI.create(ui.getPath()+"."+EXT_JSON)).build();
	}
	
	@GET
	@Path(INSTANT_PATTERN)
	@Produces( { "text/turtle", "application/x-turtle", "text/n3" })
	public Response getTurtle(
			@PathParam(YEAR_TOKEN)   int year,
			@PathParam(MONTH_TOKEN)  int month,
			@PathParam(DAY_TOKEN)    int day,
			@PathParam(HOUR_TOKEN)   int hour,
			@PathParam(MINUTE_TOKEN) int min,
			@PathParam(SECOND_TOKEN) int sec) {
		reset(year, month, day, hour, min, sec);
		return doGetTurtle().contentLocation(URI.create(ui.getPath()+"."+EXT_TTL)).build();
	}

	public static Resource createResource(URI base, Model model, Calendar cal) {
		String s_relPart = CalendarUtils.toXsdDateTime(cal);

		String s_instURI = base + INSTANT_ID_STEM + s_relPart;
		Resource r_inst = model.createResource(s_instURI, INTERVALS.CalendarInstant);
		Literal l_dateTime = ResourceFactory.createTypedLiteral(s_relPart, XSDDatatype.XSDdateTime);
		
		int year = cal.get(Calendar.YEAR);
		int moy = cal.get(Calendar.MONTH);
		int dom = cal.get(Calendar.DATE);
		int hod = cal.get(Calendar.HOUR);
		int moh = cal.get(Calendar.MINUTE);
		int som = cal.get(Calendar.SECOND);
		
		String s_month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK);
		String s_dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK);
		String s_domSuffix = getDecimalSuffix(dom);
		String s_hodSuffix = getDecimalSuffix(hod+1);
		String s_mohSuffix = getDecimalSuffix(moh+1);
		String s_somSuffix = getDecimalSuffix(som+1);
	
		model.add(r_inst, RDFS.comment, "The instant at start of the " + (som+1) + s_somSuffix + " second of " + (moh+1)
				+ s_mohSuffix + " minute of " + (hod+1) + s_hodSuffix + " hour of "
				+ s_dayOfWeek + " the " + dom + s_domSuffix + " " + s_month
				+ " of the Gregorian calendar year " + year, "en");
				                    
		model.add(r_inst, RDFS.label, s_relPart, "en");
		model.add(r_inst, SKOS.prefLabel, s_relPart, "en");
		model.add(r_inst, TIME.inXSDDateTime, l_dateTime);
		
		return r_inst;
	}
	
	@Override
	void addContainedIntervals() {
		// Instants don't contain smaller instants - do nothing.
	}

	@Override
	void addContainingIntervals() {
		//Instants are not contained by bigger instants... do nothing.
	}

	@Override
	void addNeighboringIntervals() {
		// Instants don't have neighbours... do nothing 
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, startTime);
		
//		addPlaceTimeInstantLink(model, startTime.getTime());
//		
//		// Link to www.placetime.com
//		String s_placeTimeURI = "http://www.placetime.com/instant/gregorian/" + year + 
//								"-"+String.format("%02d", month)+
//								"-"+String.format("%02d", day)+
//								"T"+String.format("%02d", hour)+
//								":"+String.format("%02d", min)+
//								":"+String.format("%02d", sec)+
//								"Z";
//		Resource r_placeTimeInterval = model.createResource(s_placeTimeURI,TIME.Interval);
//		model.add(r_thisTemporalEntity, TIME.hasBeginning, r_placeTimeInterval);
	}
}