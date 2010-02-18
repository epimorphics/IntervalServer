/******************************************************************
 * File:        WeekOfYearId.java
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

package com.epimorphics.govData.URISets.intervalServer.ukcal;

import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.epimorphics.govData.URISets.intervalServer.util.CalendarUtils;
import com.epimorphics.govData.URISets.intervalServer.util.BritishCalendar;


@Path(BritishCalendarURITemplate.WEEK_ID_STEM+BritishCalendarURITemplate.WOY_PATTERN)
public class WeekOfYearId extends Id {


	/**
	 * id->doc 303 Redirections for URI of the form /id/yyyy-Www
	 */
	@GET
	public Response redirector(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(WEEK_TOKEN) int week) {
		
		//Check that the date is in the Calendar
		BritishCalendar cal = new BritishCalendar(Locale.UK);
		try {
			CalendarUtils.setWeekOfYear(year,  week , cal);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return redirector();
	}
}