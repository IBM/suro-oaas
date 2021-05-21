/**
 * This grammar is a simplified version of the syntax of the CPLEX
 * DAT file for the purpose of parsing specific DAT files that
 * are used within the Surgical Unit Resource Optimisation project.
 */
grammar CplexDatFile;


declarations 
	: 	Declaration + EOF 
	;


declaration 
	: 	IDENTIFIER EQUAL body SEMICOLON 
	;

body 
	: 	array | set | tuple | value 
	;

value 
	: 	STRING | number
	;

number 
	: 	FLOAT | INTEGER
	;


array 
	: 	OPEN_BRACKET body + CLOSE_BRACKET 
	;

tuple 
	: 	OPEN_ANGLE_BRACKET body + CLOSE_ANGLE_BRACKET 
	;

set   
	: 	OPEN_BRACE body + CLOSE_BRACE 
	;


OPEN_BRACKET
	:	'['
	;

CLOSE_BRACKET
	:	']'
	;

OPEN_BRACE
	:	'{'
	;

CLOSE_BRACE
	:	'{'
	;

OPEN_ANGLE_BRACKET
	:	'<'
	;

CLOSE_ANGLE_BRACKET
	:	'<'
	;

EQUAL
	:	'='
	;

COMMA	
	:	','
	;

SEMICOLON
	:	';'
	;

/**
 * Maybe replace with: '"'​ .*? ​'"'​ ;
 */
STRING 
	: 	'"' ~ ["\r\n]* '"' 
	;


IDENTIFIER	
	: 	('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')+	
	;


fragment DIGIT
	: ('0'..'9')
	;

fragment DIGIT_NOT_ZERO	
	: ('1'..'9')
	;

FLOAT
   	: 	DIGIT* '.' DIGIT+ 
   	;



INTEGER
	: DIGIT | (DIGIT_NOT_ZERO DIGIT+)
	;  



WS
   : [ \r\n\t] + -> channel (HIDDEN)
   ;
