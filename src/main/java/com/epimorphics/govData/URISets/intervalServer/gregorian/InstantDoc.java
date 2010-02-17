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
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.epimorphics.govData.URISets.intervalServer.BaseURI;
import com.epimorphics.govData.URISets.intervalServer.util.CalendarUtils;
import com.epimorphics.govData.URISets.intervalServer.util.GregorianOnlyCalendar;
import com.epimorphics.govData.URISets.intervalServer.util.MediaTypeUtils;
import com.epimorphics.govData.vocabulary.DGU;
import com.epimorphics.govData.vocabulary.FOAF;
import com.epimorphics.govData.vocabulary.INTERVALS;
import com.epimorphics.govData.vocabulary.SKOS;
import com.epimorphics.govData.vocabulary.TIME;
import com.epimorphics.govData.vocabulary.VOID;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

@Path(URITemplate.DOC_STEM+URITemplate.INSTANT_SEGMENT+URITemplate.SET_EXT_PATTERN)

public class InstantDoc extends Doc {
	
	protected void populateModel(int year, int month, int day, int hour, int min, int sec) {
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
		super.populateModel();

	}
	
	@GET
	@Path(INSTANT_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN) 		int year,
			@PathParam(MONTH_TOKEN) 	int month,
			@PathParam(DAY_TOKEN)   	int day,
			@PathParam(HOUR_TOKEN)		int hour,
 			@PathParam(MINUTE_TOKEN) 	int min,
 			@PathParam(SECOND_TOKEN) 	int sec,
			@PathParam(DURATION_TOKEN)  String duration,
			@PathParam(EXT_TOKEN)  		String ext2 ) {
		if(ext2.equals(""))
			throw new WebApplicationException(Status.NOT_FOUND);
		
		MediaType mt = MediaTypeUtils.extToMediaType(ext2);
		ext = ext2;
		return respond(year,month, day, hour, min, sec, mt, false);
	}
	
	@GET
	@Path(INSTANT_PATTERN)
	public Response getResponse2(
			@PathParam(YEAR_TOKEN) 		int year, 
			@PathParam(MONTH_TOKEN) 	int month,
			@PathParam(DAY_TOKEN)   	int day,
			@PathParam(HOUR_TOKEN)		int hour,
			@PathParam(MINUTE_TOKEN) 	int min,
			@PathParam(SECOND_TOKEN) 	int sec) {

		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		ext = MediaTypeUtils.getExtOfMediaType(mt);
		return respond(year, month, day, hour, min, sec, mt, true);
	}

	private Response respond(int year, int month, int day, int hour, int min, int sec, MediaType mt, boolean addExtent) {
		base = getBaseUri();
		try {
				loc = new URI(base + ui.getPath());
				contentURI = new URI(loc.toString() + (addExtent? "."+ ext :""));
				setURI = new URI(base + SET_STEM + YEAR_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		populateModel(year, month, day, hour, min, sec);

		if (lang.equals("JSON"))
			return doGetJson().type(mt).contentLocation(contentURI).build();
 
		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
	
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
				setURI = new URI(base + SET_STEM + YEAR_SEGMENT);
			} else {
				mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
				ext = MediaTypeUtils.getExtOfMediaType(mt);
				loc = new URI(base + ui.getPath());
				contentURI = new URI(loc.toString()+ "."+ ext);
				setURI = new URI(base + SET_STEM + YEAR_SEGMENT);
			}
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		populateInstantSet();

		if (lang.equals("JSON"))
			return doGetJson().type(mt).contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
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
				+ " of the "+CALENDAR_NAME+" calendar year " + year, "en");
				                    
		model.add(r_inst, RDFS.label, ""+CALENDAR_NAME+" Instant:"+s_relPart, "en");
		model.add(r_inst, SKOS.prefLabel, ""+CALENDAR_NAME+" Instant:"+s_relPart, "en");
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
	
	private void populateInstantSet() {
		Resource r_set = createSecSet();
		Resource r_doc = model.createResource(loc.toString(), FOAF.Document);
		initSetModel(r_set, r_doc, INSTANT_SET_LABEL);
		
		model.add(r_set, RDFS.comment, "A dataset of time instant on the "+CALENDAR_NAME+" time line.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.Interval);
		model.add(r_set, VOID.uriRegexPattern, base_reg+INSTANT_ID_STEM+INSTANT_PATTERN_PLAIN);
		
		model.add(r_set, VOID.exampleResource, InstantDoc.createResource(base, model, new GregorianOnlyCalendar(1977, 10, 1, 12, 22, 45)));

		addGregorianSourceRef(r_set);	
		
		Resource r_yearSet, r_halfSet, r_quarterSet, r_monthSet, r_weekSet, r_daySet, r_hourSet, r_minSet, r_secSet, r_intervalSet, r_instantSet;
		
		r_yearSet=createYearSet();
		r_halfSet=createHalfSet();
		r_quarterSet=createQuarterSet();
		r_monthSet=createMonthSet();
		r_weekSet=createWeekSet();
		r_daySet=createDaySet();
		r_hourSet=createHourSet();
		r_minSet=createMinSet();
		r_secSet=createSecSet();
		r_intervalSet=createIntervalSet();
//		r_instantSet=createInstantSet();
		
		addLinkset(r_set, r_yearSet, r_set, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar year to starting instant links.",
				"Links between "+CALENDAR_NAME+" calendar years and their corresponding starting instants.");

		addLinkset(r_set, r_halfSet, r_set, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar half year to starting instant links.",
				"Links between "+CALENDAR_NAME+" calendar half years and their corresponding starting instants.");
		
		addLinkset(r_set, r_quarterSet, r_set, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar quarter year to starting instant links.",
				"Links between "+CALENDAR_NAME+" calendar quarter years and their corresponding starting instants.");
		
		addLinkset(r_set, r_monthSet, r_set, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar month to starting instant links.",
				"Links between "+CALENDAR_NAME+" calendar months and their corresponding starting instants.");
		
		addLinkset(r_set, r_weekSet, r_set, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar week to starting instant links.",
				"Links between ISO 8610 numbered "+CALENDAR_NAME+" calendar weeks and their corresponding starting instants.");
		
		addLinkset(r_set, r_daySet, r_set, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar day to starting instant links.",
				"Links between "+CALENDAR_NAME+" calendar days and their corresponding starting instants.");
		
		addLinkset(r_set, r_hourSet, r_set, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar hour to starting instant links.",
				"Links between "+CALENDAR_NAME+" calendar hours and their corresponding starting instants.");
		
		addLinkset(r_set, r_minSet, r_set, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar minute to starting instant links.",
				"Links between "+CALENDAR_NAME+" calendar minutes and their corresponding starting instants.");
		
		addLinkset(r_set, r_secSet, r_set, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar second to starting instant links.",
				"Links between "+CALENDAR_NAME+" calendar seconds and their corresponding starting instants.");
		
		addLinkset(r_set, r_intervalSet, r_set, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar generic intervals to starting instant links.",
				"Links between "+CALENDAR_NAME+" calendar generic intervals and their corresponding starting instants.");
		
		// Now the hadEnd links

		addLinkset(r_set, r_yearSet, r_set, TIME.hasEnd, 
				""+CALENDAR_NAME+" calendar year to ending instant links.",
				"Links between "+CALENDAR_NAME+" calendar years and their corresponding ending instants.");

		addLinkset(r_set, r_halfSet, r_set, TIME.hasEnd, 
				""+CALENDAR_NAME+" calendar half year to ending instant links.",
				"Links between "+CALENDAR_NAME+" calendar half years and their corresponding ending instants.");
		
		addLinkset(r_set, r_quarterSet, r_set, TIME.hasEnd, 
				""+CALENDAR_NAME+" calendar quarter year to ending instant links.",
				"Links between "+CALENDAR_NAME+" calendar quarter years and their corresponding ending instants.");
		
		addLinkset(r_set, r_monthSet, r_set, TIME.hasEnd, 
				""+CALENDAR_NAME+" calendar month to ending instant links.",
				"Links between "+CALENDAR_NAME+" calendar months and their corresponding ending instants.");
		
		addLinkset(r_set, r_weekSet, r_set, TIME.hasEnd, 
				""+CALENDAR_NAME+" calendar week to ending instant links.",
				"Links between ISO 8610 numbered "+CALENDAR_NAME+" calendar weeks and their corresponding ending instants.");
		
		addLinkset(r_set, r_daySet, r_set, TIME.hasEnd, 
				""+CALENDAR_NAME+" calendar day to ending instant links.",
				"Links between "+CALENDAR_NAME+" calendar days and their corresponding ending instants.");
		
		addLinkset(r_set, r_hourSet, r_set, TIME.hasEnd, 
				""+CALENDAR_NAME+" calendar hour to ending instant links.",
				"Links between "+CALENDAR_NAME+" calendar hours and their corresponding ending instants.");
		
		addLinkset(r_set, r_minSet, r_set, TIME.hasEnd, 
				""+CALENDAR_NAME+" calendar minute to ending instant links.",
				"Links between "+CALENDAR_NAME+" calendar minutes and their corresponding ending instants.");
		
		addLinkset(r_set, r_secSet, r_set, TIME.hasEnd, 
				""+CALENDAR_NAME+" calendar second to ending instant links.",
				"Links between "+CALENDAR_NAME+" calendar seconds and their corresponding ending instants.");
		
		addLinkset(r_set, r_intervalSet, r_set, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar generic intervals to ending instant links.",
				"Links between "+CALENDAR_NAME+" calendar generic intervals and their corresponding ending instants.");
		
	}	
}