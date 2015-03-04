
Example taken from:
http://msdn.microsoft.com/en-us/library/aa342335.aspx
JDBC driver downloaded from:
http://www.microsoft.com/en-us/download/details.aspx?displaylang=en&id=21599

Compilation:
javac -cp lib/sqljdbc4.jar -d classes src/*java

Running:
java -cp lib/sqljdbc4.jar:classes testXA
  params: 
    -DserverName=abc (has to be set before name of running class)
    -DportNumber=1433
    -DdatabaseName=crashrec
    -Duser=crashrec
    -Dpassword=crashrec
         
  examples:
    // mssql2014
    java -cp lib/sqljdbc4.jar:classes -DserverName=db18.mw.lab.eng.bos.redhat.com testXA
    // mssql2012
    java -cp lib/sqljdbc4.jar:classes -DserverName=db06.mw.lab.eng.bos.redhat.com testXA
    // mssql2008R2
    java -cp lib/sqljdbc4.jar:classes -DserverName=mssql01.mw.lab.eng.bos.redhat.com testXA
    // mssql2008R1
    java -cp lib/sqljdbc4.jar:classes -DserverName=vmg04.mw.lab.eng.bos.redhat.com testXA
