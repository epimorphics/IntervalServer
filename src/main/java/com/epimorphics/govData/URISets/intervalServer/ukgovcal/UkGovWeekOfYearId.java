/******************************************************************
 * File:        UkWeekOfYearId.java
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
 * $UkId:  $
 *****************************************************************/

package com.epimorphics.govData.URISets.intervalServer.ukgovcal;

import java.util.Calendar;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.epimorphics.govData.URISets.intervalServer.util.CalendarUtils;
import com.epimorphics.govData.URISets.intervalServer.util.BritishCalendar;


@Path(UkGovCalURITemplate.WEEK_ID_STEM+UkGovCalURITemplate.WOY_PATTERN)
public class UkGovWeekOfYearId extends UkGovId {


	/**
	 * id->doc 303 Redirections for URI of the form /id/yyyy-Www
	 */
	@GET
	public Response redirector(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(YEAR2_TOKEN) int year2,
			@PathParam(WEEK_TOKEN) int week) {
		
		if((year2-year)!= 1)
			throw new WebApplicationException(Status.NOT_FOUND);
		
		BritishCalendar nextYear = UkGovWeekOfYearDoc.getBritishCalAtGovWeekOne(year+1);
		BritishCalendar thisTime = UkGovWeekOfYearDoc.getBritishCalAtGovWeekOne(year);
		
		//Advance to the start of the requested Gov Week
		thisTime.add(Calendar.DATE, 7*(week-1));
		
		//We haven't gone into the folling year
		if(thisTime.getTimeInMillis()>=nextYear.getTimeInMillis())
			throw new WebApplicationException(Status.NOT_FOUND);
		
		return redirector();
	}
}