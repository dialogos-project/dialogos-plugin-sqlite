# DialogOS plugin to work with SQLite databases
Author: Timo Baumann

**Note:** If you encounter problems with this plugin, please submit a bug report on the [main DialogOS issue tracker](https://github.com/dialogos-project/dialogos/issues) with the label "plugin: SQLite".

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
(The example uses an expression which integrates the value of the variable "level"
so that questions of increasing complexity are selected.)

The plugin always attempts evaluation of an expression first and then falls back
to using the statement verbatim. Consider the following cases:
```
1. select * from questions order by RANDOM() limit 1;
2. "select * from questions order by RANDOM() limit 1;"
3. select * from questions where level = level order by RANDOM() limit 1;
4. select * from questions where level = " + level + " order by RANDOM() limit 1;
5. select * from questions where level = + level + order by RANDOM() limit 1;
6. "select * from questions where level = " + level + " order by RANDOM() limit 1;"
```
1. will be passed to SQL verbatim because it does not evaluate to an expression
   (`select` probably isn't defined, even if it were and could be multiplied `*`,
   there's no operator between `from` and `questions`).
2. evaluates to the same expression as 1 (because it's just a string definition)
   and is then passed on to SQL.
3. is passed on to SQL and works exactly like 1 and 2. Notice that there's no
   effective condition (because level == level is always true).
4. is passed on to SQL (it doesn't evaluate to an expression) and then probably
   yields no result (unless your database contains a level `" + level + "`; the
   default database has int levels). In other words: this is well-formed SQL but
   it doesn't make any sense in our domain.
5. can't be evaluated as an expression (see 1) and then throws an SQL error:
   `near "order": syntax error: select * from questions where level = + level + order`
6. works as intended: is evaluated to a string into which the value of level is
   included (e.g. "select * from questions where level = 1 order by RANDOM() limit 1;"
   and the condition is well-formed and meaningful SQL.

Example database content courtesy of the jQuizshow project:
http://quizshow.sourceforge.net/ (reformated as a SQLite database).
