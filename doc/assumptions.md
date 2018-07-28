#### Assumptions

* A request from a channel contains only 3 fields
	- country (String)
	- channel (String)
	- language (String)

* Categories:
	- unique
	- may be hierarchical
	- associated to channels

* Channels
	- unique
	- may have categories associated with it

* Adverts
	- unique
	- have constraints associated with them.
	- have a global view limit
	- have a start and end date
	- may have country or channel specific limits

* Rules / Constraints
	- can be either simple expressions which evaluate to true | false
	- combined using either conjuctions or disjunctions.

* API
	- returns an advertisement or null given a request
	- returns an advertisement given an advertisement id

* On an average of 40 to 50 advertisements can be live at any point

* Data stored in memory
