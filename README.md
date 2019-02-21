# EspressoViews


- EspressoViews is a standalone Java tool for reporting and analysis of data stored on relational databases (RDBMS).

- EspressoViews merges the results of meaningfull interconnected SQL statements and displays them in a data tree structure, which can be expanded and collapsed by the user. 

- Its human friendly [approach][Ideas] allows you to connect a, theoretically, unlimited number SQLs to a report as simple as adding phrases to a text.  

- More About? Check the [Wiki][Wiki] pages!

![](https://github.com/RaduMarcel/EspressoViews-/blob/master/DocImg/EspressoViewsImg1.png)


### How to generate an Espresso Views Report 

You should first [read how to define an EspressoViews report][ReportDef] and then follow [the few syntax rules needed to create a report definiton XML file][ReportSyntax]. And finally you connect to a database server using EspressoViews and you let the tool process your report defintion file. 

- A report is made of at least two report units 

- A report unit is built around a single SQL query. The above example is made of three units, but theoretically there is no limitation as long as the whole structure is a tree structure. 

- Any report definition file can be on his side embedded in another report definition file. Also here there is not  is no limitation as long as the linked files are defining a tree structure


[Ideas]: https://github.com/RaduMarcel/EspressoViews-/wiki/2.-The-main-ideas-behind-this-tool
[Reportref]: https://github.com/RaduMarcel/EspressoViews-/wiki/3.-Defining-an-EspressoViews-report
[Wiki]: https://github.com/RaduMarcel/EspressoViews/wiki

### Maintain the human readability without reducing the complexity and/or the amount of data.

EspressoViews first runs all SQL queries as specified in the report definition file without prior filtering by the user. 
The first data shown to the user is the result of the root query. This first query should or can be used as a summary or as an introduction for the rest of the data tree. The user then decides which meaningful "data path" to follow further down.

Though, a graphical user interface for filtering prior to and after the data retrieval is in some cases still necessary and will be supported in the future as optional features.


There is a lot of space to improve funtionality and user experience. You are welcome [to contribute][Contributing] with your feedback, ideas and/or coding experience and help this project to evolve.
But the way, this tool supports only Oracle and MySQL DBMS.


### What is required to give it a try? 

You need first to have a Java Runtime version 1.7 or higher installed installed on your machine.
To do a quick check type in the command line:
```
java -version
```
If the version is lower than 1.7 or your system does not have Java then please download and install a newer Java version (https://java.com/de/download/). 
If you know that you have the right version but the Java executable was not found from the command line, then make sure the Java PATH system variable is set correctly (see more: https://www.java.com/en/download/help/path.xml).


Then download the EspressoViews file `EspressoViews.zip` and unpack it in an own folder. The application is started either by double-klicking Java runnable file `EspressoViews.jar` or by typing in the command line 
```
java -jar EspressoViews.jar
```

If the application has started correctly, then you should be able to see this database log-in dialog:

![](https://github.com/RaduMarcel/EspressoViews-/blob/master/DocImg/EspressoViewsInstall.png)



Now, to use and generate an EspressoViews report you need:

**1. An XML report definition file.**

This definition file is made of your SQLs queries and of instructions, which specify how to organize and display the data retrieved with these SQL queries. To see how such an EspressoViews definition is created please read first [these introductory words on how to define an Espresso Views report][ReportDef] and then [the XML report definition syntax][ReportSyntax]

[ReportDef]: https://github.com/RaduMarcel/EspressoViews-/wiki/3.-Defining-an-EspressoViews-report
[ReportSyntax]: https://github.com/RaduMarcel/EspressoViews-/wiki/4.-The-XML-report-definition-syntax

**2. The connection credentials for the (Oracle or MySQL) database server where the report definition file should be ran.**

The `EspressoViews.zip` file contains the JDBC driver packages for the suported database server. They are placed in the folder `EspressoViews_lib`. The provided JDBC diver for Oracle databases works with the Oracle Versions 11.2 and 12.1 and the JDBC driver for MySQL databases works at least with the MySQL versions from 5.1 to 5.7
These drivers can be also downloaded in internet and you should do so if the version provided is not compatible with the database system version you try to access.
If you replace the JDBC driver packages then make sure the new driver file name is also referenced in the path defintion in MANIFEST.MF file of the `EspressoViews.jar` package.  


**3. Press the OK Button on the bottom to generate the report**


## Contributing

There are many ways to [contribute][Contributing] to this open source project. 

[Contributing]: https://github.com/RaduMarcel/EspressoViews/blob/master/CONTRIBUTING.md

## Credits: 
Espresso Views, Version 0.51

Copyright © Radu-Marcel Dumitru

This program is free software; you can redistribute it and/or modify it under the terms of the GNU GENERAL PUBLIC LICENSE, Version 3 as published by the Free Software Foundation on 29 June 2007
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
See the GNU General Public License for more details.


