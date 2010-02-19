/******************************************************************
 * File:        UkYearId.java
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

import com.epimorphics.govData.URISets.intervalServer.util.BritishCalendar;

import java.util.Calendar;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


@Path(UkGovCalURITemplate.YEAR_ID_STEM+UkGovCalURITemplate.YEAR_PATTERN)
public class UkGovYearId extends UkGovId {
	@GET
	public Response redirector(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(YEAR2_TOKEN)int year2 ) {
		// Make sure the years are consequtive.
		if((year2-year)!= 1)
			throw new WebApplicationException(Status.NOT_FOUND);
		
		//Check that the date is in the "+CALENDAR_NAME+" Calendar
		BritishCalendar cal = new BritishCalendar(Locale.UK);
		cal.setLenient(false);
		cal.set(year, Calendar.APRIL, 1);
		try {
			cal.getTimeInMillis();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		return redirector();
	}
}