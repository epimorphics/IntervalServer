/******************************************************************
 * File:        SetDoc.java
 * Created by:  skw
 * Created on:  16 Feb 2010
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
package com.epimorphics.govData.URISets.intervalServer.ukcal;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.epimorphics.govData.URISets.intervalServer.gregorian.InstantDoc;
import com.epimorphics.govData.URISets.intervalServer.util.BritishCalendar;
import com.epimorphics.govData.URISets.intervalServer.util.Duration;
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
@Path(BritishCalendarURITemplate.DOC_STEM+BritishCalendarURITemplate.CALENDAR_SET+BritishCalendarURITemplate.SET_EXT_PATTERN)
public class SetDoc extends Doc {
	
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
				setURI = new URI(base + SET_STEM + CALENDAR_SET);
			} else {
				mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
				ext = MediaTypeUtils.getExtOfMediaType(mt);
				loc = new URI(base + ui.getPath());
				contentURI = new URI(loc.toString()+ "."+ ext);
				setURI = new URI(base + SET_STEM + CALENDAR_SET);
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
	/* (non-Javadoc)
	 * @see com.epimorphics.govData.URISets.intervalServer.gregorian.Doc#addContainedIntervals()
	 */
	@Override
	void addContainedIntervals() {

	}

	/* (non-Javadoc)
	 * @see com.epimorphics.govData.URISets.intervalServer.gregorian.Doc#addContainingIntervals()
	 */
	@Override
	void addContainingIntervals() {

	}

	/* (non-Javadoc)
	 * @see com.epimorphics.govData.URISets.intervalServer.gregorian.Doc#addNeighboringIntervals()
	 */
	@Override
	void addNeighboringIntervals() {

	}

	/* (non-Javadoc)
	 * @see com.epimorphics.govData.URISets.intervalServer.gregorian.Doc#addThisTemporalEntity()
	 */
	@Override
	void addThisTemporalEntity() {

	}
	
	private void populateCalSet() {
		Resource r_set = model.createResource(setURI.toString(),VOID.Dataset);
		Resource r_doc = model.createResource(loc.toString(), FOAF.Document);
		initSetModel(r_set, r_doc, CALENDAR_SET_LABEL);
		
		model.add(r_set, RDFS.label, CALENDAR_SET_LABEL, "en");
		model.add(r_set, SKOS.prefLabel, CALENDAR_SET_LABEL,"en");
		
		model.add(r_set, RDFS.comment, "A dataset of "+CALENDAR_NAME+" Calendar aligned time intervals formed from the union" +
				                     " of datasets that contain calendar aligned intervals one year, one half year," +
				                     " one quarter, one month, one day, one hour, one minute or one second.", "en");
		model.add(r_set, RDF.type, VOID.Dataset);

		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
//		model.add(r_set, VOID.uriRegexPattern, base_reg+YEAR_ID_STEM+YEAR_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+HALF_ID_STEM+HALF_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+QUARTER_ID_STEM+QUARTER_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+MONTH_ID_STEM+MONTH_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+DAY_ID_STEM+DAY_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+HOUR_ID_STEM+HOUR_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+MINUTE_ID_STEM+MIN_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+SECOND_ID_STEM+SEC_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+INSTANT_ID_STEM+INSTANT_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+INTERVAL_ID_STEM+INTERVAL_PATTERN_PLAIN);
		
		model.add(r_set, VOID.uriRegexPattern, base_reg+ID_STEM+CALENDAR_STEM+SUMMARY_PATTERN_PLAIN);
		
		model.add(r_set, VOID.exampleResource, YearDoc.createResourceAndLabels(base, model, 1752));
		model.add(r_set, VOID.exampleResource, HalfDoc.createResourceAndLabels(base, model, 2010, 1));
		model.add(r_set, VOID.exampleResource, QuarterDoc.createResourceAndLabels(base, model, 1644, 3));
		model.add(r_set, VOID.exampleResource, MonthDoc.createResourceAndLabels(base, model, 1958, 11));
		model.add(r_set, VOID.exampleResource, WeekOfYearDoc.createResourceAndLabels(base, model, 2009, 52 ));
		model.add(r_set, VOID.exampleResource, DayDoc.createResourceAndLabels(base, model, 1960, 3, 12));
		model.add(r_set, VOID.exampleResource, HourDoc.createResourceAndLabels(base, model, 1752, 9, 2, 23));
		model.add(r_set, VOID.exampleResource, MinuteDoc.createResourceAndLabels(base, model, 1752, 9, 2, 23, 59));
		model.add(r_set, VOID.exampleResource, SecDoc.createResourceAndLabels(base, model, 1234, 4, 1, 22, 35, 41));
//		model.add(r_set, VOID.exampleResource, InstantDoc.createResource(base, model, new BritishCalendar(1977, 10, 1, 12, 22, 45)));
//		model.add(r_set, VOID.exampleResource, IntervalDoc.createResourceAndLabels(base, model, new BritishCalendar(1977, 10, 1, 12, 22, 45), new Duration("P2Y1MT1H6S") ));
		

		addCalendarActRef(r_set);
		
		Resource r_yearSet, r_halfSet, r_quarterSet, r_monthSet, r_weekSet, r_daySet, r_hourSet, r_minSet, r_secSet, r_intervalSet, r_instantSet;
		
		model.add(r_set, VOID.subset, r_yearSet=createYearSet());
		model.add(r_set, VOID.subset, r_halfSet=createHalfSet());
		model.add(r_set, VOID.subset, r_quarterSet=createQuarterSet());
		model.add(r_set, VOID.subset, r_monthSet=createMonthSet());
		model.add(r_set, VOID.subset, r_weekSet=createWeekSet());
		model.add(r_set, VOID.subset, r_daySet=createDaySet());
		model.add(r_set, VOID.subset, r_hourSet=createHourSet());
		model.add(r_set, VOID.subset, r_minSet=createMinSet());
		model.add(r_set, VOID.subset, r_secSet=createSecSet());
//		model.add(r_set, VOID.subset, r_intervalSet=createIntervalSet());
//		model.add(r_set, VOID.subset, r_instantSet=createInstantSet());
			
	}
}
