#### APIs

| url         | type   | params                     | desc                                      |
|:------------|:------:|:--------------------------:|:-----------------------------------------:|
| /           | POST   | None                       | Initializes sample data
| /           | DELETE | None                       | Clears the sample data
| /ads/:label | GET    | label                      | Gets the advertisement matching the label
| /ads/       | GET    | channel, country, language | returns a matching ad (if any)

* [Sample Data](sample-data.md)
