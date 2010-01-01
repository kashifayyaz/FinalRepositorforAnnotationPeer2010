

your Derby database should be running and you have to create
testDB with following configurations
jdbc:derby://localhost:1527/testDB
username: "kashif"
password: "kashif"

connect the testDB and the execute the PeerInterface.java class

in the "Add Annotation Data" tab you can enter new annotation data 
but before that you have to press the "Create Database" button as
you are running this application for the first time. later when it will
be executed there is no need to create database. 
caution, if you press the "Create Database " button again later, your previous
tables and data will be erased.

you will only press the ReadRDF button if you have created some RDF file which will be 
explained shortly.

now coming towards the second tab which is "search"

you can enter your search query in the text area provided and press you desired button, you will see 
results in result text area.

going towards advanced search there are alot of options which will be explained one by one.

Search Multiple Fields: will provide you facility to search from multiple fields of DB.
You have to enter the query and select the fields by selecting relevent check box. remember 
try to enter as many queries as you have selected text boxes separated by ":"

wild card query is simple you can enter for example
* for multiple characters and ? for single character
example to find kashif you can enter "k?s*" and so on

Phrase Query will help you to find a phrase from the fields. 
if you have something like "this is the description " in description field of your data
you can select description check box and search words like this, the, or description e.t.c


Prefix Query: as the name suggests this query will help you to find words whose similar prefix
string has been entered.

Term Query:is no more same like phrase query to search a term

Range Query: the most critical one, before i was storing creation time of query as a String in DB, due to which
searching was efficiet with respect to single field search. but when i started range search like time between
[12:34:45 TO 12:45:45] the query didnt work. thats why i made changes in all database and now the CreationTime field
is stored in TIMESTAMP type in DB 
due to which all the time what ever time java provides to DB it will store "12:00:00", i think i will cover this bug in 
coming days.



now lastly the CreateRDF button, when you will find your search item, you can create respective RDF on the rood of your "C" drive 
with the name of "test.n3"

once the RDF file is created now you can read the RDF [the read RDF button is in previous tab]. but if you try to read the 
same RDF as you created it will not successfull store it in you database as the primary key will be same. so you can edit you RDF
and the read it by system. upon reading the data will be saved in to you DB as well as printed on prompt.



