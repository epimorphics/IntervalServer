package com.epimorphics.govData.URISets.intervalServer.govcalendar;


public class URITemplate {

	protected static final String Y1_TOKEN 			= "year1";
	protected static final String Y2_TOKEN 			= "year2";
	protected static final String HALF_TOKEN 		= "half";
	protected static final String QUARTER_TOKEN 	= "quarter";
	protected static final String MONTH_TOKEN 		= "month";
	protected static final String DAY_TOKEN 		= "day";
	protected static final String HOUR_TOKEN 		= "hour";
	protected static final String MINUTE_TOKEN 		= "minute";
	protected static final String SECOND_TOKEN 		= "second";
	protected static final String EXT_TOKEN 		= "ext";

	protected static final String ID_STEM 			= "id/";
	protected static final String DOC_STEM 			= "doc/";
	
	
	protected static final String CALENDAR_STEM 	= "government";
	protected static final String CALENDAR_ID_STEM 	= ID_STEM + CALENDAR_STEM;

	
	protected static final String YEAR_ID_STEM 		= CALENDAR_ID_STEM + "-year/";
	protected static final String HALF_ID_STEM 		= CALENDAR_ID_STEM + "-half/";
	protected static final String QUARTER_ID_STEM 	= CALENDAR_ID_STEM + "-quarter/";
	protected static final String MONTH_ID_STEM 	= CALENDAR_ID_STEM + "-month/";
	protected static final String DAY_ID_STEM 		= CALENDAR_ID_STEM + "-day/";
	protected static final String HOUR_ID_STEM 		= CALENDAR_ID_STEM + "-hour/";
	protected static final String MINUTE_ID_STEM 	= CALENDAR_ID_STEM + "-minute/";
	protected static final String SECOND_ID_STEM	= CALENDAR_ID_STEM + "-second/";
	protected static final String INSTANT_ID_STEM	= CALENDAR_ID_STEM + "-instant/";

	protected static final String CALENDAR_DOC_STEM = DOC_STEM + CALENDAR_STEM;
	protected static final String YEAR_DOC_STEM 	= CALENDAR_DOC_STEM + "-year/";
	protected static final String HALF_DOC_STEM 	= CALENDAR_DOC_STEM + "-half/";
	protected static final String QUARTER_DOC_STEM 	= CALENDAR_DOC_STEM + "-quarter/";
	protected static final String MONTH_DOC_STEM 	= CALENDAR_DOC_STEM + "-month/";
	protected static final String DAY_DOC_STEM 		= CALENDAR_DOC_STEM + "-day/";
	protected static final String HOUR_DOC_STEM 	= CALENDAR_DOC_STEM + "-hour/";
	protected static final String MINUTE_DOC_STEM 	= CALENDAR_DOC_STEM + "-minute/";
	protected static final String SECOND_DOC_STEM 	= CALENDAR_DOC_STEM + "-second/";
	protected static final String INSTANT_DOC_STEM	= CALENDAR_DOC_STEM + "-instant/";

	protected static final String HALF_PREFIX		= "-H";
	protected static final String QUARTER_PREFIX	= "-Q";
	protected static final String MONTH_PREFIX	= "-";
	protected static final String DAY_PREFIX		= "-";
	protected static final String HOUR_PREFIX		= "T";
	protected static final String MINUTE_PREFIX	= ":";
	protected static final String SECOND_PREFIX	= ":";
	
	protected static final String YEAR_PATTERN 	=                                     "{" + Y1_TOKEN    + ":[1-9][0-9]+}"+"/{" + Y2_TOKEN    + ":[1-9][0-9]+}";
	protected static final String HALF_PATTERN 	= YEAR_PATTERN 	   + HALF_PREFIX    + "{" + HALF_TOKEN	  + ":[12]}";
	protected static final String QUARTER_PATTERN = YEAR_PATTERN 	   + QUARTER_PREFIX + "{" + QUARTER_TOKEN + ":[12345]}";
	protected static final String MONTH_PATTERN 	= YEAR_PATTERN 	   + MONTH_PREFIX   + "{" + MONTH_TOKEN	  + ":[0-1][0-9]}";
	protected static final String DAY_PATTERN 	= MONTH_PATTERN    + DAY_PREFIX     + "{" + DAY_TOKEN     + ":[0-3][0-9]}";
	protected static final String HOUR_PATTERN 	= DAY_PATTERN 	   + HOUR_PREFIX    + "{" + HOUR_TOKEN    + ":[0-2][0-9]}";
	protected static final String MINUTE_PATTERN 	= HOUR_PATTERN 	   + MINUTE_PREFIX  + "{" + MINUTE_TOKEN  + ":[0-5][0-9]}";
	protected static final String SECOND_PATTERN 	= MINUTE_PATTERN   + SECOND_PREFIX  + "{" + SECOND_TOKEN  + ":[0-5][0-9]}";
	protected static final String INSTANT_PATTERN = SECOND_PATTERN;
	
	protected static final String EXT_RDF = "rdf";
	protected static final String EXT_NT  = "nt";
	protected static final String EXT_TTL = "ttl";
	protected static final String EXT_JSON = "json";
	protected static final String EXT_N3 = "n3";
	protected static final String EXT_PATTERN =".{"+EXT_TOKEN+": (("+EXT_RDF+")|("+EXT_NT+")|("+EXT_TTL+")|("+EXT_N3+")||("+EXT_JSON+"))}";
}