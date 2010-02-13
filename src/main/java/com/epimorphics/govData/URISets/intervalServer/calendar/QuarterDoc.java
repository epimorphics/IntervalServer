/******************************************************************
 * File:        QuarterDoc.java
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
package com.epimorphics.govData.URISets.intervalServer.calendar;

import java.net.URI;
import java.util.Calendar;
import java.util.Locale;

import com.epimorphics.govData.URISets.intervalServer.gregorian.InstantDoc;
import com.epimorphics.govData.URISets.intervalServer.util.CalendarUtils;
import com.epimorphics.govData.URISets.intervalServer.util.BritishCalendar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.epimorphics.govData.vocabulary.INTERVALS;
import com.epimorphics.govData.vocabulary.SCOVO;
import com.epimorphics.govData.vocabulary.SKOS;
import com.epimorphics.govData.vocabulary.TIME;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

@Path(URITemplate.QUARTER_DOC_STEM)
public class QuarterDoc extends Doc {


	private void reset(int year, int quarter) {
		reset();
		this.year = year;
		this.quarter = quarter;
		this.half = ((quarter-1)/2)+1;
		this.month = ((quarter-1)*3)+1;
		
		startTime = new BritishCalendar(Locale.UK);
		startTime.setLenient(false);
		startTime.set(year, month-1, day, hour, min, sec);
		startTime.getTimeInMillis();
	}

	@GET
	@Path(QUARTER_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(QUARTER_TOKEN) int quarter,
			@PathParam(EXT_TOKEN)  String ext ) {
		reset(year, quarter);
		this.ext = ext;
		return doGet() ;
	}

	@GET
	@Path(QUARTER_PATTERN)
	@Produces("text/plain")
	public Response getNTriple(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(QUARTER_TOKEN) int quarter) {
		reset(year, quarter);
		return doGetNTriple().contentLocation(URI.create(ui.getPath()+"."+EXT_NT)).build();
	}

	@GET
	@Path(QUARTER_PATTERN)
	@Produces("application/rdf+xml")
	public Response getRDF(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(QUARTER_TOKEN) int quarter) {
		reset(year, quarter);
		return doGetRDF().contentLocation(URI.create(ui.getPath()+"."+EXT_RDF)).build();
	}

	@GET
	@Path(QUARTER_PATTERN)
	@Produces("application/json")
	public Response getJson(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(QUARTER_TOKEN) int quarter) {
		reset(year, quarter);
		return doGetJson()
		       .contentLocation(URI.create(ui.getPath()+"."+EXT_JSON))
		       .build();
	}
	
	@GET
	@Path(QUARTER_PATTERN)
	@Produces( { "text/turtle", "application/x-turtle", "text/n3" })
	public Response getTurtle(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(QUARTER_TOKEN) int quarter) {
		reset(year, quarter);
		return doGetTurtle().contentLocation(URI.create(ui.getPath()+"."+EXT_TTL)).build();
	}

	
	static protected Resource createResourceAndLabels(URI base, Model m, int year, int quarter) {
		String relPart = year + QUARTER_PREFIX + quarter;

		String s_quarterURI = base + QUARTER_ID_STEM + relPart;
		Resource r_quarter = m.createResource(s_quarterURI,	INTERVALS.CalendarQuarter);
		
		if(quarter>0 && quarter<=4) {
			Resource r_quarterType = quarter == 1 ? INTERVALS.Q1 :
				                     quarter == 2 ? INTERVALS.Q2 :
				                     quarter == 3 ? INTERVALS.Q3 : INTERVALS.Q4;
			r_quarter.addProperty(RDF.type, r_quarterType);
		}
		
		String s_label = "Calendar Quarter:" + relPart;
		m.add(r_quarter, SKOS.prefLabel, s_label, "en");
		m.add(r_quarter, RDFS.label, s_label, "en");
		m.add(r_quarter, RDFS.comment, "The "
				+ ((quarter == 1) ? "first" 
				 : (quarter == 2) ? "second"
				 : (quarter == 3) ? "third" : "forth")
				+ " quarter of the British calendar year " + year, "en");
		return r_quarter;
	}

	static protected Resource createResource(URI base, Model m, int year, int quarter) {
		Resource r_quarter = createResourceAndLabels(base, m,  year, quarter);
		r_quarter.addProperty(RDF.type, SCOVO.Dimension);
		r_quarter.addProperty(RDF.type, INTERVALS.Quarter);
		
		if(quarter>0 && quarter<=4 ) {
			r_quarter.addProperty(RDF.type, (quarter==1 ? INTERVALS.Q1 :
											 quarter==2 ? INTERVALS.Q2 :
											 quarter==3 ? INTERVALS.Q3 :
											 			  INTERVALS.Q4));
		}

		BritishCalendar cal = new BritishCalendar( year, ((quarter-1)*3), 1, 0, 0, 0);
		cal.setLenient(false);

		m.add(r_quarter, INTERVALS.hasXsdDurationDescription, oneQuarter);
		m.add(r_quarter, TIME.hasDurationDescription, INTERVALS.one_quarter);
		
		
		Resource r_instant = InstantDoc.createResource(base, m, cal);	
		m.add(r_quarter, TIME.hasBeginning, r_instant);
		m.add(r_quarter, SCOVO.min, CalendarUtils.formatScvDate(cal, CalendarUtils.iso8601dateformat), XSDDatatype.XSDdate);
		
		cal.add(Calendar.MONTH, 3);
		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);
		m.add(r_quarter, TIME.hasEnd, r_EndInstant);

		cal.add(Calendar.SECOND, -1);
		m.add(r_quarter, SCOVO.max, CalendarUtils.formatScvDate(cal, CalendarUtils.iso8601dateformat), XSDDatatype.XSDdate);

		return r_quarter;
	}

	@Override
	void addContainedIntervals() {
		Resource months[] = new Resource[3];
		
		for (int month = 0; month < 3; month++) {
			int i_moy = ((quarter - 1) * 3) + month + 1;
			Resource r_month = MonthDoc.createResourceAndLabels(base, model, year, i_moy);
			
			connectToContainingInterval(model, r_thisTemporalEntity, r_month);
			months[month] = r_month;
		}
		RDFList r_months = model.createList(months);
		model.add(r_thisTemporalEntity, INTERVALS.intervalContainsMonths, r_months);
		
	}

	@Override
	void addContainingIntervals() {
		Resource r_thisYear = YearDoc.createResourceAndLabels(base, model, year);
		Resource r_thisHalf = HalfDoc.createResourceAndLabels(base, model, year, half);
		connectToContainingInterval(model, r_thisYear, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisHalf, r_thisTemporalEntity);		
	}

	@Override
	void addNeighboringIntervals() {
		BritishCalendar cal;
		Resource r_nextQuarter;
		Resource r_prevQuarter;
		
		try{
			cal = (BritishCalendar) startTime.clone();
			cal.add(Calendar.MONTH,3);
			r_nextQuarter = createResourceAndLabels(base, model, cal.get(Calendar.YEAR), (cal.get(Calendar.MONTH)/3)+1);
			cal = (BritishCalendar) startTime.clone();
			cal.add(Calendar.MONTH,-3);
			r_prevQuarter = createResourceAndLabels(base, model, cal.get(Calendar.YEAR), (cal.get(Calendar.MONTH)/3)+1);	
		} catch (IllegalArgumentException e){
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		connectToNeigbours(model, r_thisTemporalEntity, r_nextQuarter, r_prevQuarter);
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year, quarter);	
		
		addGeneralIntervalTimeLink(model, startTime, oneQuarter);
	}

}