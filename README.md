# IntervalServer

**Deprecated**
GDS no longer run the reference data server, nor the interval URI resolver so this code is no longer in use. Anyone forking this repo should review dependencies for any vulnerabilities before depolying it.


IntervalServer repo contains the source code for the Servlet that provides the Interval service at reference.data.gov.uk. e.g.

   - `http://reference.data.gov.uk/id/quarter/2016-Q1` [.rdf](http://reference.data.gov.uk/doc/quarter/2016-Q1) [.ttl](http://reference.data.gov.uk/doc/quarter/2016-Q1.ttl) [.json](http://reference.data.gov.uk/doc/quarter/2016-Q1.json)
   - `http://reference.data.gov.uk/id/gregorian-interval/2016-06-30T00:00:00/P3M` [.rdf](http://reference.data.gov.uk/id/gregorian-interval/2016-06-30T00:00:00/P3M) [.ttl](http://reference.data.gov.uk/id/gregorian-interval/2016-06-30T00:00:00/P3M.ttl) [.json](http://reference.data.gov.uk/id/gregorian-interval/2016-06-30T00:00:00/P3M.json)

This source code is made available under an [Apache 2](http://www.apache.org/licenses/LICENSE-2.0) open source license.

[Interval URIs](interval-uris.md) provides more information about the interval service and the use of the interval URIs that it supports in statistical data publications.

