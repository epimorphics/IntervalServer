/******************************************************************
 * File:        UkMonthId.java
 * Created by:  Stuart Williams
 * Created on:  13 Feb 2010
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

package com.epimorphics.govData.URISets.intervalServer.ukcal;

import com.epimorphics.govData.URISets.intervalServer.util.BritishCalendar;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;


@Path(UkCalURITemplate.MONTH_ID_STEM+UkCalURITemplate.MONTH_PATTERN)
public class UkMonthId extends UkId {


	/**
	 * id->doc 303 Redirections for URI of the form /id/yyyy-mm
	 */
	@GET
	public Response redirector(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month) {
		
		//Check that the date is in the Calendar
		BritishCalendar cal = new BritishCalendar(Locale.UK);
		cal.setLenient(false);
		cal.set(year, month-1, 1);
		try {
			cal.getTimeInMillis();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return redirector();
	}
}