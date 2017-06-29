# DBPoolModule
Garage payment system - connection pool module

1. DBPoolModule Summary

The DBPoolModule library consists in a “database connection pool” in Java programming language. The use of this “database connection pool” improves considerably the performance of  applications. Since, it is not necessary to obtain connections directly from the database whenever a client wants to use a java.sql.Connection object type.So connections are acquired from a  previously initialized connection pool. This connection are return to the pool when the clients complete their use. 

The library provides the necessary features to obtain the connection pool, starting from the Database Connection Pool Builder (DBCPBuilder). In addition, to instantiation a pool of  connections it is necessary to create a configuration object (DBPoolConfiguration) where the database resources(user, password, url, driver). Also, it is necessary to establish a “minimium amount of connections to cache at any time" and a "maximium amount of outstanding connections
“.

	2.	DBPoolModuleBehavior

The connection pool is governed under the following behavior: The stack is initialized from an N number, minimum connections to keep in cache at any time. After the total available connections in the stack are requested, N connections to the stack must be added. The connections can be returned to the stack and are added from N to N as needed until reaching the number M, maximum connections that can be created in the stack. If maximum stack connections are reached then no further connections can be obtained until N connections are returned to the stack. 

	3.	DBPoolModule Dependencies

The project use Maven(3.3.9v); the code is developed under the rules of the Maven checkstyle. Thus, this includes dependencies for the development of the unit tests such as unit, org.mockito, com.googlecod.catch-exception.

	4.	DBPoolModule Set up
	5.	DBPoolModule How to use?
	6.	DBPoolModule
	7.	License 

DBPoolModule is available under the license Apache License 2.0. More information read  [license](https://choosealicense.com/licenses/)
