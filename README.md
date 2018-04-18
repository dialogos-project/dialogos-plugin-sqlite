# DialogOS plugin to work with SQLite databases
Author: Timo Baumann

This repository serves two purposes:

 - implement a simple SQLiteNode which enables SQL queries towards a SQLite database,
 - demonstrate how to set up gradle's dependency management when implementing a plugin.

## Example:
Run ```./gradlew run```, wait for compilation to complete, select "Open File..."
and open the file ```millionaire.dos```. Have fun!

## Usage:

The database to be used is set in the plugin settings. 

In the SQLiteNode, you enter a SQL statement and determine what script variable 
should be used to store the result. You _must_ select a list variable. 

SQL statements can be entered either verbatim, or evaluated from an expression.
(The example uses an expression into which evaluates "level" so that questions 
of increasing complexity are selected.

Example database content courtesy of the jQuizshow project: 
http://quizshow.sourceforge.net/ (reformated as a SQLite database).
