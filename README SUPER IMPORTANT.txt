**********************************************************************
READ THIS BEFORE DESTROYING THE GIT
**********************************************************************
Our git root folder contains the following:
- "db" folder
- "java src" folder
- "FabulousBeeAnn" folder

*** DB FOLDER
	- this contains the sql files to be used in DBMS like MySQL
	  or SQLserver.
	- this is not needed for tomcat server.

*** JAVA SRC FOLDER
	- this contains all the source code of our java servlet.
	- you can edit these code.
	- these codes have to be compiled into .class files using eclipse
	  before they can be used in tomcat server.
	- move the .class files into the following path:
	  (root tomcat folder)> webapps> FabulousBeeAnn> WEB-INF> classes>

*** FABULOUSBEEANN FOLDER
	- this is a folder that we have to create in tomcat
	- it is the root of our own website. create it in the following:
	  (root tomcat folder)> webapps>


**********************************************************************
SUMMARY OF THE INNER WORKINGS OF TOMCAT
**********************************************************************
if you had completed apache-tomcat tutorial
you would be able to understand the following:

- Within apache-tomcat root folder
 - Exist a "webapps" folder to hold all the web files
  - Within "webapps" folder, we create our own folder "FabulousBeeAnn"

- "FabulousBeeAnn" folder is the root of our website. it contains:
 - all HTML files
 - an "IMG" folder
 - a "CSS" folder
 - a "JS" folder
 - a "WEB-INF" folder
  - all the html files will reference from these resources to display
    our website

- "WEB-INF" folder contains the following:
 - web.xml, which maps html files to java functions
 - "classes" folder, holds the compiled java codes (in .class format)
   to perform all our java functions


**********************************************************************
TO TEST OUR WEBSITE
**********************************************************************
- pull the "FabulousBeeAnn" folder from git, copy it to your own
  tomcat> webapps folder.

- to be able to query from the database, TBC.....


**********************************************************************
MISC TBC
**********************************************************************
I haven't think of a better way but currently we will have to manually change the IP address on these files:
	register.html
	... more coming up


