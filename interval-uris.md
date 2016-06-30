# Using Interval URIs in Statistical Data

Statistical publications generally include at least one dimension comprised of the time intervals over which data has been aggregated. Linked data vocabularies for representing statistical information, such as [SCOVO](http://sw.joanneum.at/scovo/schema.html) and current [SDMX/Linked-Data collaborative activities](http://groups.google.com/group/publishing-statistical-data) refer to time intervals as 'things' or 'objects' rather than as literal values. This will typically involves the assignment of a URI so that multiple references can be made to the same interval. In the absense of a common set of intervals to refer to, each data publisher will have a tendancy to manufacture their own set of interval names. Here we present sets of URI named calendar aligned intervals with durations of a year, a half-year, a quarter year, a month... down to a second. Adoption and use of these interval names in statistical data will better enable the alignment of the time dimensions of otherwise independent statistical data sets. 

We illustrate with the following example from the SCOVO website which presents on-time-arrival statistics for airports in the first quarter of 2006.

    @prefix ex: <http://example.org/on-time-flight#> .
    @prefix scv: <http://purl.org/NET/scovo#> .
    @prefix dc: <http://purl.org/dc/elements/1.1/> .
    @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
    @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
    @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
    
    # domain schema definitions
 
    ex:TimePeriod rdfs:subClassOf scv:Dimension ;
       dc:title "time period" .
     
    ex:Q12006 rdf:type ex:TimePeriod ;
       dc:title "2006 Q1" ;
       scv:min "2006-01-01"^^xsd:date ;
       scv:max "2006-03-31"^^xsd:date .
     
    ex:OnTime rdfs:subClassOf scv:Dimension ; 
       dc:title "on-time arrivals or departures" .
    
    ex:ota rdf:type ex:OnTime ;
       dc:title "on-time arrivals" .
     
    ex:Airport rdfs:subClassOf scv:Dimension ;
       dc:title "airport" .
    
    ex:AtlantaHartsfield rdf:type ex:Airport ;
       dc:title "Atlanta, Hartsfield" .
     
    
    # dataset and data items instances
    
    ex:ontime-flights rdf:type scv:Dataset ;
       dc:title "On-time Flight Arrivals and Departures at Major U.S. Airports: 2006" ;
       scv:datasetOf ex:AtlantaHartsfield-ota-2006-q1 .
    
    ex:AtlantaHartsfield-ota-2006-q1 rdf:type scv:Item ;
       rdf:value 74 ;
       scv:dataset ex:ontime-flights ;
       scv:dimension ex:Q12006 ;
       scv:dimension ex:ota ;
       scv:dimension ex:AtlantaHartsfield .
    
This extract presents a single data item for just one airport, <code>ex:AtlantaHartsfield-ota-2006-q1</code>, but a fuller publication will contain data for many airports and for more than just a single quarter. In this extract the time interval coordinate for the data item <code>ex:Q12006</code> is set up by the following 4 RDF statements:

    ex:Q12006 rdf:type ex:TimePeriod ;
       dc:title "2006 Q1" ;
       scv:min "2006-01-01"^^xsd:date ;
       scv:max "2006-03-31"^^xsd:date .

This establishes an interval that runs from 1st January 2006 to 31st March 2006. Note that in this case the <code>scv:min</code> and <code>scv:max</code> statements have literal values which each designate one day long intervals, rather than time instants. 

It would be really 'handy' to have a common set of intervals that could be referred to using the same URI for the same interval across muliple statistical publications. This would make it much easier to aligned statstical data covering similar intervals. As well as being useful for expressing the time coordinate of a data item (<code>scv:Item</code>) in a datasets, a common set of URIs for intervals may also be used in describing the temporal extent of the data set as a whole.

Interval URI Sets
-----------------

[Three families of URI sets](http://reference.data.gov.uk/id/interval) are now available to use in the publication of statistical data. Each contains subset URI sets for calendar aligned intervals of varying sizes: one year, one half year, one quarter year, one month, one week, one day, one hour, one minute and one second. Each family of URI sets is aligned with one of the following three timelines or calendars:

* The [UK/British calendar](http://reference.data.gov.uk/id/uk-calendar) which transitions from the Julian calendar to the Gregorian calendar ofor dates on or after 14th September 1752. 

* The [Gregorian calendar](http://reference.data.gov.uk/id/gregorian-calendar) which follows the Gregorian calendar for all dates and acts as a reference calendar to which all other intervals are referred using a <code>time:intervalEquals</code> statement.

* The [modern UK/British government business calendar](http://reference.data.gov.uk/id/government-calendar) which starts annually on 1st April each year. This set contains subsets for [year](http://reference.data.gov.uk/id/government-year), [half-year](http://reference.data.gov.uk/id/government-half), [quarter-year](http://reference.data.gov.uk/id/government-quarter) and [week](http://reference.data.gov.uk/id/government-week) long intervals. Month long, day long and smaller intervals are taken from the UK/British calendar.

There are two further subsets of the [Gregorian URI set](http://reference.data.gov.uk/id/gregorian-calendar). There are URI sets for [unzoned arbitrary length intervals](http://reference.data.gov.uk/id/gregorian-interval) and [unzoned arbitary time instants](http://reference.data.gov.uk/id/gregorian-instant) (one second precision). These operate in a manner similar to the zoned Gregorian interval and instant URI available at [placetime.com](http://www.placetime.com). However, for statistical aggregations, zoned temporal references may be overly precise. For a statistical quarter spanning an interval of 3 months the precise time at which the quarter begins and ends may be of little significance. Having said that there may be a case for adding zoned URI sets for cases where zoning is important - eg. for recording events in calendaring applications in a multinational context.

In the SCOVO example above, the first quarter of 2006 can be designated by the URI [<http://reference.data.gov.uk/id/quarter/2006-Q1>](http://reference.data.gov.uk/id/quarter/2006-Q1). Performing an HTTP retrieval (GET) operation using this URI, will return reference data about that interval. This includes an enumeration of the months long intervals enclosed with in the quarter; references to the calendar aligned year and half-year intervals within which the quarter is contained; and references to the neigbouring quarter year intervals, as illustrate in the annotated turtle below:

    # Subject interval
    <http://reference.data.gov.uk/id/quarter/2006-Q1>
    
    # Labels and comments for people and UIs
          rdfs:comment "The first quarter of the British calendar year 2006"@en ;
          rdfs:label "British Quarter:2006-Q1"@en ;
          skos:prefLabel "British Quarter:2006-Q1"@en ;

    # Linkage to containing URI Set.
          dgu:uriSet <http://reference.data.gov.uk/id/quarter> ;

    # RDF Typing infomation
          a       interval:Q1 , 
                  interval:Quarter , 
                  interval:CalendarQuarter , 
                  scv:Dimension ;

    # SCOVO upper and lower bounds
          scv:max "2006-03-31"^^xsd:date ;
          scv:min "2006-01-01"^^xsd:date ;

    # Interval limits as instants on the gregorian timeline.
          time:hasBeginning 
                 <http://reference.data.gov.uk/id/gregorian-instant/2006-01-01T00:00:00> ;
          time:hasEnd 
                 <http://reference.data.gov.uk/id/gregorian-instant/2006-04-01T00:00:00> ;

    # Interval Duration as an an XML Schema datatype literal
          interval:hasXsdDurationDescription
              "P3M"^^xsd:duration ;

    # Interval duration as an instance.
          time:hasDurationDescription
              interval:one-quarter ;

    # Neigbouring intervals of the same duration (next/prev)
          interval:nextInterval
              <http://reference.data.gov.uk/id/quarter/2006-Q2>;

          interval:previousInterval
              <http://reference.data.gov.uk/id/quarter/2005-Q4>;

          time:intervalMeets 
              <http://reference.data.gov.uk/id/quarter/2006-Q2>;
 
          time:intervalMetBy 
              <http://reference.data.gov.uk/id/quarter/2005-Q4>;

    # Contained next level finer grained intervals as multivalued and as list
          interval:intervalContainsMonth
              <http://reference.data.gov.uk/id/month/2006-02> , 
              <http://reference.data.gov.uk/id/month/2006-03> , 
              <http://reference.data.gov.uk/id/month/2006-01> ;
          interval:intervalContainsMonths
              (<http://reference.data.gov.uk/id/month/2006-01> 
               <http://reference.data.gov.uk/id/month/2006-02> 
               <http://reference.data.gov.uk/id/month/2006-03>);

    # Superproperty closure for interval:intervalContainsMonth
          time:intervalContains
              <http://reference.data.gov.uk/id/month/2006-02> , 
              <http://reference.data.gov.uk/id/month/2006-03> , 
              <http://reference.data.gov.uk/id/month/2006-01> ;

    # Linkage to containing intervals.
          time:intervalDuring 
              <http://reference.data.gov.uk/id/year/2006> , 
              <http://reference.data.gov.uk/id/half/2006-H1> ;

    # Linkage arbitrary gergorian interval. ALL URI Set intervals are referred in this way
    # to the gregorian timeline.
          time:intervalEquals 
              <http://reference.data.gov.uk/id/gregorian-interval/2006-01-01T00:00:00/P3M> ;

    # Significant ordinal values
          interval:ordinalHalfOfYear              1 ;
          interval:ordinalQuarterOfYear           1 ;
          interval:ordinalYear                 2006 ;

    # Linkage between the interval and its describing document.
          foaf:isPrimaryTopicOf
              <http://reference.data.gov.uk/doc/quarter/2006-Q1.ttl> .


Document representations for each interval are available in RDF/XML, Turtle or N3, N-Triple and as JSON ([linked-data-api](http://code.google.com/p/linked-data-api/)) formats. The selection of a particular format can be accomplished either by Accept header based content-negotiation or by the use of specific URI for each available document variant. 

We follow data.gov.uk established convention for naming reference documents. They are best located by attempting to dereference the interval URI and following the intervening 303 redirection. Document representations of our example quarter year are available at:

<pre>
http://reference.data.gov.uk/doc/quarter/2006-Q1	//Generic document</pre>

with format specific versions available at:

<pre>
http://reference.data.gov.uk/doc/quarter/2006-Q1.rdf	//RDF/XML format (application/rdf+xml)
http://reference.data.gov.uk/doc/quarter/2006-Q1.ttl	//Turtle format (text/turtle)
http://reference.data.gov.uk/doc/quarter/2006-Q1.n3		//N3 format (text/n3)
http://reference.data.gov.uk/doc/quarter/2006-Q1.nt		//N-Triple format (text/plain)
http://reference.data.gov.uk/doc/quarter/2006-Q1.json	//JSON (application/json) [linked-data-api format](http://code.google.com/p/linked-data-api/)
</pre>

Note that the .extensions only work with the .../doc/... URI.

URI Set and Dataset Descriptions
--------------------------------

This section section serves as a reference for the URI patterns associated with each URI set.

### Common patterns

The syntactic patterns of the 'tail' of the URI sets are intended to follow the syntax of [ISO 8601(http://en.wikipedia.org/wiki/ISO_8601). The pattern for modern government business intervals differs from ISO 8601 syntax because the year size business interval spans two 'normal' calendar years. To avoid confusion, the URI assigned to modern government business intervals embeds two consecutive 'normal' years. 


| *Pattern Tag* | *Syntactic Regex* | *Description* |
|---------------|-------------------|---------------|
| {year}, {year1} and {year2} | ((0-9){4}) or ([1-9][0-9]{4,})) | A decimal calendar year indicator of at least 4 digits.  |
| {half} | H[1-2] | A half year designator, where H1 designates the first half year and H2 designates the second. |
| {quarter} | Q[1-4] | A quarter year designator, where Q1 designates the first quarter year and Q4 designates the forth. |
| {month} | [0-1][0-9] | A two digit decimal month number. 01 designates the month of January while 12 designates the month of December. |
| {day} | [0-3][0-9] | A two digit decimal day of the month indicator. 01 designates the first day of a given month. |
| {hour} | [0-2][0-9] | A two digit decimal hour of the day indicator. 00 designates the first hour of a given day |
| {minute} | [0-5][0-9] | A two digit decimal minute of the hour indicator. 00 designates the first minute of a given hour. |
| {sec} | [0-6][0-9] | A two digit decimal second of the minute indicator. 00 designates the first second of a given minute. |
| {week} | W[0-5][0-9] | A two digit decimal week of the year indicator. W01 designates the first week of a given year |
| {dateTime} | {year}-{month}-{day}T{hour}:{minute}:{second} | A composite indicator that designates an instant which is the starting moment of a given second of a given day. |
| {duration} | | A time duration in "ISO 8601 duration syntax":http://en.wikipedia.org/wiki/ISO_8601#Durations PnYnMnDTnHnMnS |

The URI patterns given below are given as relative URI with respect to a base URI of <http://reference.data.gov.uk>. 

### British Calendar Intervals

| *URI Pattern* | *Description* | *URI Set URI* |
|---------------|---------------|---------------|
| /id/year/{year} | A UK calendar aligned interval of one year starting on 1st Jan of the designated year | /id/year |
| /id/half/{year}-{half} | A UK calendar aligned interval of one-half year, 6 calendar months, starting on 1st Jan or 1st July of the designated year | /id/half |
| /id/quarter/{year}-{quarter} | A UK calendar aligned interval of one quarter, 3 months, starting in 1st Jan, 1st Apr, 1st July or 1st Oct of the designated year | /id/quarter |
| /id/month/{year}-{month} | A UK calendar interval of one calendar month starting on the first day of the designated month | /id/month |
| /id/day/{year}-{month}-{day} | A UK calendar aligned interval of one day starting at midnight on the designated day | /id/day |
| /id/hour/{year}-{month}-{day}T{hour}| A UK calendar aligned interval of one hour | /id/hour |
| /id/hour/{year}-{month}-{day}T{hour}:{min}| A UK calendar aligned interval of one minute | /id/minute |
| /id/hour/{year}-{month}-{day}T{hour}:{min}:{sec}| A UK calendar aligned interval of one second | /id/second |
| /id/week/{year}-{week}| An interval of 7 days starting on a Monday, numbered in accordance with ISO 8601 such that week one contains, equivalently, 4th January or the 1st Thursday of the designated year | /id/week |

### Gregorian Calendar Intervals and Instants

| *URI Pattern* | *Description* | *URI Set URI* |
|---------------|---------------|---------------|
| /id/gregorian-year/{year} | A Gregorian calendar aligned interval of one year starting on 1st Jan of the designated year | /id/gregorian-year |
| /id/gregorian-half/{year}-{half} | A Gregorian calendar aligned interval of one-half year, 6 calendar months, starting on 1st Jan or 1st July of the designated year | /id/gregorian-half |
| /id/gregorian-quarter/{year}-{quarter} | A Gregorian calendar aligned interval of one quarter, 3 months, starting in 1st Jan, 1st Apr, 1st July or 1st Oct of the designated year | /id/gregorian-quarter |
| /id/gregorian-month/{year}-{month} | A Gregorian calendar interval of one calendar month starting on the first day of the designated month | /id/gregorian-month |
| /id/gregorian-day/{year}-{month}-{day} | A Gregorian calendar aligned interval of one day starting at midnight on the designated day | /id/gregorian-day |
| /id/gregorian-hour/{year}-{month}-{day}T{hour}| A Gregorian calendar aligned interval of one hour | /id/gregorian-hour |
| /id/gregorian-hour/{year}-{month}-{day}T{hour}:{min}| A Gregorian calendar aligned interval of one minute | /id/gregorian-minute |
| /id/gregorian-hour/{year}-{month}-{day}T{hour}:{min}:{sec}| A Gregorian calendar aligned interval of one second | /id/gregorian-hour |
| /id/gregorian-week/{year}-{week}| An interval of 7 days starting on a Monday, numbered in accordance with ISO 8601 such that week one contains, equivalently, 4th January or the 1st Thursday of the designated year | /id/gregorian-week |

In addition under there are two further URI sets that provide for arbitrary unzoned intervals and instants with respect to the Gregorian calendar.

| * URI Pattern* | *Description* | *URI Set URI* |
|----------------|---------------|---------------|
| /id/gregorian-instant/{dateTime} | An arbitrary unzoned instant on the Gregorian timeline. | /id/gregorian-instant |
| /id/gregorian-interval/{dateTime}/{duration} | An arbitrary duration unzoned interval on the Gregorian timeline.  | /id/gregorian-interval |

### Modern Government Business Intervals

These intervals are described as modern in the sense that they reflect the modern pattern of the UK government's business year. However, the government year has not always followed this pattern. Prior to the 1750 Calendar Act and the Gregorian changeover of 1752, the government year for England and Wales started on annually 25th March with quarter days of 24th June, 29th September and 25th December. The quarter days in Scotland were different. With the Gregorian changeover, the UK tax year shifted from beginning on 25th March (on the Julian calendar) initially to 5th April and then subsequently to the 6th April as a further leap day present in the Julian calendar was suppressed from the Gregorian calendar in 1800. Since then the UK tax year has continue to start on 6th April. However, the government financial reporting year has run from 1st April since xxxx.

| *URI Pattern* | *Description* | *URI Set URI* |
|---------------|---------------|---------------|
| /id/government-year/{year1}-{year2} | A modern UK government calendar aligned interval of one year starting on 1st April of the UK calendar year designated by {year1} | /id/government-year |
| /id/government-half/{year1}-{year2}/{half} | A modern UK government calendar aligned interval of one-half year, 6 calendar months, starting on 1st April or 1st October of the designated year | /id/government-half |
| /id/government-quarter/{year1}-{year2}/{quarter} | A modern UK government  calendar aligned interval of one quarter, 3 months, starting in 1st Apr, 1st July, 1st Oct or 1st Janof the designated year | /id/government-quarter |
| /id/government-week/{year1}-{year2}/{week}| An interval of 7 days starting on a Monday, numbered in accordance with ISO 8601 such that week one contains, equivalently, 4th April or the 1st Thursday of the designated year | /id/government-week |

Acknowledgement
---------------

The 'pioneering' work in the area of URI for intervals and instants was done by Ian Davis with the [placetime.com](http://www.placetime.com) service that he deployed a several years ago. 

The generic Gregorian interval and instant URI sets follow the pattern of the URIs that placetime creates, however unlike the zoned placetime intervals and instants, the URI assigned here are assigned to families of unzoned time intervals of predetermined - and hopefully useful - duration and calendar alignment.
