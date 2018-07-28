#### Environment

* Leiningen 2.8.1
* Java 9 (OpenJDK)

#### Cloning and installing the application

	$ git clone git@github.com:abhijith/ad-rule-engine.git rpm
	$ cd rpm
	$ lein deps

#### Running the code

* Starting the application

		$ cd rpm
		$ lein run -m rpm.core
		$ curl -X GET http://localhost:5000/ # hello

* Tests

		$ lein test

* [API](api.md)
