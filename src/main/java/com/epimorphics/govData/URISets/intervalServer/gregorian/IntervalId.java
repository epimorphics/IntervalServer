/******************************************************************
 * File:        IntervalId.java
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

import com.epimorphics.govData.URISets.intervalServer.util.GregorianOnlyCalendar;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;


@Path(GregorianURITemplate.INTERVAL_ID_STEM+GregorianURITemplate.INTERVAL_PATTERN)
//@Path(GregorianURITemplate.INTERVAL_ID_STEM+GregorianURITemplate.SECOND_PATTERN+"/{duration : P([0-9]+Y)?([0-9]+M)?([0-9]+D)?(T([0-9]+H)?([0-9]+M)?([0-9]+S)?)?}")
public class IntervalId extends Id{

	@GET
	public Response redirector(
			@PathParam(YEAR_TOKEN)     int year, 
			@PathParam(MONTH_TOKEN)    int month,
			@PathParam(DAY_TOKEN)      int day,
			@PathParam(HOUR_TOKEN)     int hour,
			@PathParam(MINUTE_TOKEN)   int min,
			@PathParam(SECOND_TOKEN)   int sec,
			@PathParam(DURATION_TOKEN) String duration){
		
		//Check that the date is in the Calendar
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
		cal.setLenient(false);
		cal.set(year, month-1, day);
		try {
			cal.getTimeInMillis();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return redirector();	
	}
}