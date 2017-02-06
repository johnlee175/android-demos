// Test Event Record Language
grammar Ter;

file: packageDeclaration* statement+ mainRun
;
packageDeclaration: PACKAGE QUALIFIED_NAME ';'
;
statement
    : baseStatement #labBaseStatement
    | blockStatement #labBlockStatement
    | swtichStatement #labSwitchStatement
;
swtichStatement: IF condition ifStatement (ELSE elseStatement)?
;
condition: EXE_TEXT
;
ifStatement: statement
;
elseStatement: statement
;
blockStatement : '{' statement* '}'
;
baseStatement
    : triggerStatement #labTriggerStatement
    | caseStatement #labCaseStatement
;
triggerStatement: triggerId '=' trigger ';'
;
triggerId: ID
;
caseStatement: caseId '=' testCase ';'
;
caseId: ID
;
trigger: event
;
testCase: '[' idRef ',' caseItem (',' caseItem)* (',')? ']'
;
caseItem
    : idRef #labIdRef
    | verifier #labVerifier
;
verifier: event
;
event: '{' fields '}'
;
fields: field+
;
field: VARIABLE '=' (EXE_TEXT | TEXT) ';'
;
mainRun: MAIN ':' idRef ('=>' idRef)* ';'
;
idRef: '@' ID
;
ID: [A-Z][a-zA-Z0-9]+
;
EXE_TEXT: '@' TEXT
;
TEXT:  '"' ('\\' ["\\/bfnrt] | ~["\\])* '"'
;
VARIABLE
    : 'type'
    | 'code'
    | 'when'
    | 'duration'
    | 'data'
    | 'id'
    | 'fingerprint'
;
WS: [ \t\n\r]+ -> skip
;
COMMENT: '/*' .*? '*/' -> skip
;
IF: 'if'
;
ELSE: 'else'
;
MAIN: 'main'
;
PACKAGE: 'package'
;
QUALIFIED_NAME: IDENTIFIER ('.' IDENTIFIER)*
;
IDENTIFIER: JavaLetter JavaLetterOrDigit*
;

fragment
JavaLetter
    :   [a-zA-Z$_] // these are the "java letters" below 0x7F
    |   // covers all characters above 0x7F which are not a surrogate
        ~[\u0000-\u007F\uD800-\uDBFF]
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
    ;
fragment
JavaLetterOrDigit
    :   [a-zA-Z0-9$_] // these are the "java letters or digits" below 0x7F
    |   // covers all characters above 0x7F which are not a surrogate
        ~[\u0000-\u007F\uD800-\uDBFF]
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
    ;