# DBPoolModule
Garage payment system - connection pool module

# Summary

The DBPoolModule library consists in a “database connection pool” in Java programming language. The use of this “database connection pool” improves considerably the performance of  applications. Since, it is not necessary to obtain connections directly from the database whenever a client wants to use a java.sql.Connection object type.So connections are acquired from a  previously initialized connection pool. This connection are return to the pool when the clients complete their use. 

The library provides the necessary features to obtain the connection pool, starting from the Database Connection Pool Builder (DBCPBuilder). In addition, to instantiation a pool of  connections it is necessary to create a configuration object (DBPoolConfiguration) where the database resources(user, password, url, driver). Also, it is necessary to establish a “minimium amount of connections to cache at any time" and a "maximium amount of outstanding connections
“.

# DBPoolModuleBehavior

The connection pool is governed under the following behavior: The stack is initialized from an N number, minimum connections to keep in cache at any time. After the total available connections in the stack are requested, N connections to the stack must be added. The connections can be returned to the stack and are added from N to N as needed until reaching the number M, maximum connections that can be created in the stack. If maximum stack connections are reached then no further connections can be obtained until N connections are returned to the stack. 

# DBPoolModule Dependencies

The project use Maven(3.3.9v); the code is developed under the rules of the Maven checkstyle. Thus, this includes dependencies for the development of the unit tests such as [junit](https://mvnrepository.com/artifact/junit/junit/4.11), [org.mockito](https://mvnrepository.com/artifact/org.mockito/mockito-core/1.9.5), [com.googlecod.catch-exception](https://mvnrepository.com/artifact/com.googlecode.catch-exception/catch-exception) and [org.assertj](https://mvnrepository.com/artifact/org.assertj/assertj-core/1.5.0)

# Set up
It's recommended to use Maven for the use of this library. Use ```mvn install``` to build the project and run the unit tests.

# How to use?

Using the DBCPBuilder class is possible to obtain a connection pool object (DBConnectionPool). The recommended usage is the following:

``` 
public DBConnectionPool getDBConnectionPool() throws SQLException, DBPoolException {
      DBPoolConfiguration dbPoolConfiguration = new DBPoolConfiguration();
      dbPoolConfiguration.setDbusername(USERNAME);
      dbPoolConfiguration.setDbpassword(PASSWORD);
      dbPoolConfiguration.setDburl(URL);
      dbPoolConfiguration.setDbdriver(DRIVER);
      dbPoolConfiguration.setMinPoolCache(MIN_POOL_CACHE);
      dbPoolConfiguration.setMaxPoolSize(MAX_POOL_SIZE);
      
      DBCPBuilder dbcpBuilder = new DBCPBuilder();
      dbcpBuilder.setConfiguration(dbPoolConfiguration);
      
      return dbcpBuilder.build();
}
    
public void managePool() throws SQLException, DBPoolException {
      
      try (Connection con = getDBConnectionPool().getConnection();
           Statement st = con.createStatement();
           ResultSet res = st.executeQuery("…"))
      {
          // Connection usage
        
      }
      
}
    
```
# License 

DBPoolModule is available under the license Apache License 2.0. More information read  [license](https://choosealicense.com/licenses/)
