# EspressoViews-
Slim SQL-based reporting tool for highly complex data structure. 

Express your own view. 

## **What is this tool used for?**

EspressoViews is a reporting and data analysis environment. 
It generates fast, simple and efficient reports from a relational database (RDBMS). 

In a nutshell, Espresso View merges the results of many SQL queries to one hierarchically structured result set as specified by the user in a defintion file and displays it graphically in a tree structure (comparable with a file folder structure), which can be manually expanded and collapsed:

![](https://github.com/RaduMarcel/EspressoViews-/blob/master/DocImg/EspressoViewsImg1.png)

The result of the first query is always shown in the “expanded” mode, and under each line the results of the subordinated queries can be expanded manually. Like this you follow only the stream of data you are interested in.

Currently (December 2017) it supports "only" the Oracle and MySQL database servers.

## **What are the special "skills" of EspressoViews?**

If you have one monolithic SQL query which retrieves everything you need or if you plan to build such a query and you are looking forward to create a report around it, then this reporting tool will not be useful for you.

This tool is best suitable for use cases in which the data retrieval logic is made of a multitude of SQL queries which are regularly changing, where new queries need to be included and old queries have to be removed and/or where a high amount of data needs to be built up but without showing the users hundreds of report pages. 

A core feature is the level of modularization of the report definition.
The smallest unit or module is built around a single SQL query. A report is made of at least two such SQL query units. The above example is made of three SQL query units. Any report definition, can be on his side a reporting unit in another report definition, thus allowing the user to easily bundle and maintain very complex data retrieval (circular references between report modules are of course not allowed since they would cause infinite loops). 
more details about this modularized design approach you can find in the chapters [The main ideas behind this tool][Ideas] and [Defining an EspressoViews report][ReportDef]

[Ideas]: https://github.com/RaduMarcel/EspressoViews-/wiki/2.-The-main-ideas-behind-this-tool
[ReportDef]: https://github.com/RaduMarcel/EspressoViews-/wiki/3.-Defining-an-EspressoViews-report

The driving force to implement this report is in the first place the attempt to increase the human readability and usability of the data retrieved with SQL without reducing its complexity and/or the amount of information. 

This tool is a dwarf standing on the shoulders of RDBMS giants.


## What should I do to give it a try?

You need first to have Java installed on your machine (version 1.6 upwards).
Then download the EspressoViews file `EspressoViews.zip`, unpack it in a new folder and start the application with the Java runnable file `EspressoViews.jar`. You should be able to see this database log-in dialog:

![](https://github.com/RaduMarcel/EspressoViews-/blob/master/DocImg/EspressoViewsInstall.png)



Now, to use and generate an EspressoViews report you need further:

**1. An XML report definition file.**

This definition file is made of your SQLs queries and of instructions, which specify how to organize and display the data retrieved with these SQL queries. To see how such an EspressoViews definition is created please read first [these introductory words on how to define an Espresso Views report][ReportDef] and then [the XML report definition syntax][ReportSyntax]

[ReportDef]: https://github.com/RaduMarcel/EspressoViews-/wiki/3.-Defining-an-EspressoViews-report
[ReportSyntax]: https://github.com/RaduMarcel/EspressoViews-/wiki/4.-The-XML-report-definition-syntax

**2. The connection credentials for the (Oracle or MySQL) database server where the report definition file should be ran.**

The `EspressoViews.zip` file contains the JDBC driver packages for the suported database server. They are placed in the folder `EspressoViews_lib`. The provided JDBC diver for Oracle databases works with the Oracle Versions 11.2 and 12.1 and the JDBC driver for MySQL databases works at least with the MySQL version 5.1.42.
These drivers can be also downloaded in internet and you should do so if the version provided is not compatible with the database system version you try to access.
When you replace the JDBC driver packages then make sure the new driver file name is also referenced in the path defintion in MANIFEST.MF file of the `EspressoViews.jar` package.  


**3. Press the OK Button on the bottom to generate the report**




## Credits: 
Espresso Views, Version 0.5

Copyright © Radu-Marcel Dumitru

This program is free software; you can redistribute it and/or modify it under the terms of the GNU GENERAL PUBLIC LICENSE, Version 3 as published by the Free Software Foundation on 29 June 2007
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
See the GNU General Public License for more details.


