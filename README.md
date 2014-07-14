YEP FILE SERVER
====================

First of all, Don't Panic!

This is a simple JAVA project to be used as a File Server using REST API.



You can use to store your files on another place, and consume everything from a HTTP API.
Upload files specifying a path, create new revisions, list files on a path, download files or download revisions.

Its simple, but usefull if you want to move your storage to another place. I know, I know... "But why not Amazon S3?" you say... Well... there you cannot read the code and learn from that, and with this code you can have yout own File Server.. thats not awsome? :p

# PUBLISHING

## Requirements

To run this project as is, you will need to setup:

- JVM Java 6+
- SGBD Mysql 5+
- Servlet Container - TOMCAT 6 (or another)

## Setup

- Import schema.sql to your database
- Do deploy of .war to your tomcat

# APIs Doc

## Returning errors
All failed requisitions will have your equivalent HTTP Response Code, and on the body a JSON explaing the problem.

* 400: Invalid parameters. More details on body as JSON
* 404: File or path not found
* 405: Unexpected request method. Usually GET or POST
* 5xx: Server error. More details on body as JSON

## GET files
Donwload a file.

### URL structure
http://base_path/files/{id}
*{id}* is the id of file
### Method
GET
### Version
1.0
### Parameters
**rev** - (integer) the number of file revision to be returned. As default the last revision is returned
### Return
The file with defined id
### Errors
* **404**: When the file or revision is not found

## POST files
Upload a file or a new revision of file

### URL structure
http://base_path/files/
### Method
POST
### Version
1.0

### Parameters
**file** - required (multipart/form-data) the content of file sent from a html form with enctype="multipart/form-data"
**overwrite** - (boolean) when true, if the file on the same path with the same name already exists, create a new revision for that file
**path** - the path where this file should be saved

### Return
All file metadata.

#### Example

```
{
"id":"0",
"isDir":"false",
"mimeType":"image/png",
"name":"image.png",
"path":"path/where/is/file",
"currentRevision":"1"
}
```

### Errors
* **400**: Error to read file, or some required params was not sent

## GET revisions
Return the metadata from all file revisions

### URL structure
http://base_path/revisions/{id}
*{id}* is the id of file
### Method
GET
### Version
1.0

### Parameters
No one needed

### Return
File metadata and the list of revisions.

#### Example

```
{
  “metadata”:
{
    "id":"0",
    "isDir":"false",
    "mimeType":"image/png",
    "name":"image.png",
    "path":"path/where/is/file",
    "currentRevision":"1"
},
  “revisions” :
{
   “revision”: 0,
   “created_at”: “2012-01-01 00:00:00”
},
{
    “revision”: 1,
   “created_at”: “2012-01-01 01:34:22”
}
}
```

### Errors
* **404**: File not found


## GET paths
Return the metadata from all files on the specified path

### URL structure
http://base_path/paths

### Method
GET
### Version
1.0

### Parameters
**path** required (String) path to be checked

### Return
File metadata of the files with the specified path.

#### Example

```
{
	“metadatas”: [
	{
    	"id":"0",
    	"isDir":"false",
    	"mimeType":"image/png",
	    "name":"image.png",
	    "path":"path/where/is/file",
    	"currentRevision":"1"
	},
	{
	    "id":"1",
	    "isDir":"false",
    	"mimeType":"image/png",
   		"name":"image1.png",
	    "path":"path/where/is/file",
    	"currentRevision":"0"
	}
	]
}
```

### Errors
none


# DEVELOPMENT


## Requirements

- JAVA 6+ SDK
- Eclipse (http://eclipse.org)
-- I'm using the plugin m2eclipse (http://maven.apache.org/eclipse-plugin.html)
(If you want to user another IDE, be free to do that. :) But all my instructions are to run on Eclipse... )


## How to run

1. Create a Tomcat server on Eclipse. (do some search on goole)
2. Import the project to eclipse
3. Run maven install to dondload all dependencias. (you can use the plugin m2eclipse)
	* Right button on the project -> Run as -> Maven Install or ...
	* on project dir, exec the command "mvn install"
4. Config the database credentials on file  src/main/webapp/WEB-INF/config/db.xml
5. Import tables from schema.sql to the configured database
4. Publish the project on Tomcat
5. Run tomcat

## Frameworks

* Jersey (jersey.java.net) RESTFUL API
* Spring - For IC and Configurations

## Talking about the dir structure

src/main/java - Java classes
src/main/config - configuration files (log4j)
src/main/webapp - Sprint and ServletContainer configuration files

## Talking about the packages of the project

file.server.model.bean - the beans of the project, every class that load data
file.server.model.dao - class to manage beans on database
file.server.rest - "servlets" jersey, represents the communication with external clients
file.server.service - service classes, the link between rest and dao, here I put all bussiness logical
file.server.util - util classes

## How to generate the fucking WAR?

* Change the config of the database to our production server on src/main/webapp/WEB-INF/config/db.xml (I know, its not the best approach... sorry about that)
* On eclipse, click with the right button of yout mouse on the project and select the option Export -> WAR File
* NNF on the wizard and be happy!
