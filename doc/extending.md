#### Missing / Improvement areas

* Error handling

* Test scenarios and data generation (Exhaustive branch coverage)

* Ad-ranking:
  - Ad-classes => platinum | gold | silver | bronze
  - expr weightage => (and expr weight) => (and (isa? ::ktm ::bike) 10)
  - closest to expiry | exhaustion
  - hit frequency

* Categories can be hierarchies | graphs
  - ancestors, descendants, isa? operators

* core.spec
  - represent rule engine using spec
  - spec all fns
  - property testing reusing spec

* Logging
  - json logging to make the log searchable

* Persistent data store

* Containerization

* Prometheus/metrics end-points
  - application metrics for analytics
  - jvm metrics

* signal handling
  - graceful termination of process
  - config reload
  - control log level
