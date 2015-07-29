/*  (c) 2004 Allen I. Holub. All rights reserved.
 *
 *  This code may be used freely by yourself with the following
 *  restrictions:
 *
 *  o Your splash screen, about box, or equivalent, must include
 *    Allen Holub's name, copyright, and URL. For example:
 *
 *      This program contains Allen Holub's SQL package.<br>
 *      (c) 2005 Allen I. Holub. All Rights Reserved.<br>
 *              http://www.holub.com<br>
 *
 *    If your program does not run interactively, then the foregoing
 *    notice must appear in your documentation.
 *
 *  o You may not redistribute (or mirror) the source code.
 *
 *  o You must report any bugs that you find to me. Use the form at
 *    http://www.holub.com/company/contact.html or send email to
 *    allen@Holub.com.
 *
 *  o The software is supplied <em>as is</em>. Neither Allen Holub nor
 *    Holub Associates are responsible for any bugs (or any problems
 *    caused by bugs, including lost productivity or data)
 *    in any of this code.
 */
package com.holub.text;

import java.util.Iterator;
import java.io.*;
import com.holub.text.ParseFailure;

/***
 *  A Scanner lets you read a file as a set of input tokens.
 *	<p>
 *  See the source code for {@link com.holub.database.Database}
 *  (in the distribution jar) for an example of how a token set
 *  is used in conjunction with a Scanner. Here's a stripped-down
 *  version:
 *  <p>First create a token set:
 *  <PRE>
	private static final TokenSet tokens = new TokenSet();

	private static final Token
		COMMA		= tokens.create( "'," 		),
		EQUAL		= tokens.create( "'=" 		),
		LP			= tokens.create( "'(" 		),
		RP 			= tokens.create( "')" 		),
		DOT			= tokens.create( "'." 		),
		STAR		= tokens.create( "'*" 		),
		SLASH		= tokens.create( "'/" 		),
		AND			= tokens.create( "'AND"		),
		BEGIN		= tokens.create( "'BEGIN"	),
		CREATE		= tokens.create( "'CREATE"	),
		//...
		INTEGER		= tokens.create( "(small|tiny|big)?int(eger)?"),
		IDENTIFIER	= tokens.create( "[a-zA-Z_0-9/\\\\:~]+"		);
	</PRE>
	Then create and initialize the scanner. The following method
	scans input from a string (as compared to a file):
	<PRE>
	private Scanner in;

	public Table execute( String expression ) throws IOException, ParseFailure
	{	try
		{	this.expression   = expression;
			<b>in				  = new Scanner(tokens, expression);</b>
			<b>in.advance();	// advance to the first token.</b>
			return statement();
		}
		catch( ParseFailure e )
		{	if( transactionLevel > 0 )
				rollback();
		}
		//...
	}
	</PRE>
	The Scanner uses a "match/advance" strategy. Rather than read tokens that you
	might have to push back, you first check if the next token is the one you
	want, and then advance past it if so.
	<PRE>
	// statement
	//      ::= CREATE  DATABASE IDENTIFIER
	//      |   CREATE  TABLE    IDENTIFIER LP idList RP
	//
	void statement()
	{
		// The matchAdvance(CREATE) call skips past (and returns)
		// the CREATE token if it's the next input token, otherwise
		// it returns null.

		if( <b>in.matchAdvance(CREATE) != null</b> )
		{
			// Here, I'm doing match and advance as two separate
			// operations.

			if( <b>in.match( DATABASE )</b> )
			{	<b>in.advance();</b>
				createDatabase( in.required( IDENTIFIER ) );
			}
			else // must be CREATE TABLE
			{	
				// This required() call throws an exception
				// if the next input token isn't a TABLE. If
				// a TABLE token is found, then we'll advance past
				// it automatically.

				<b>in.required( TABLE );</b>
				String tableName = in.required( IDENTIFIER );
				in.required( LP );
				createTable( tableName, declarations() );
				in.required( RP );
			}
		}
		//...
	}
	</PRE>
 *
 *	@include /etc/license.txt
 */

public class Scanner
{	private Token			currentToken	= new BeginToken();
	private BufferedReader	inputReader 	= null;
	private int		 		inputLineNumber = 0;
	private String	 		inputLine     	= null;
	private int				inputPosition 	= 0;

	private TokenSet tokens;

	/** Create a Scanner for the indicated token set, which
	 *  will get input from the indicated string.
	 */
	public Scanner( TokenSet tokens, String input )
	{	this( tokens, new StringReader(input) );	
	}

	/** Create a Scanner for the indicated token set, which
	 *  will get input from the indicated Reader.
	 */
	public Scanner( TokenSet tokens, Reader inputReader )
	{	this.tokens 	 = tokens;
		this.inputReader =
			(inputReader instanceof BufferedReader)
				? (BufferedReader) inputReader
				: new BufferedReader( inputReader )
				;
		loadLine();
	}

	/** Load the next input line and adjust the line number
	 *  and inputPosition offset.
	 */
	private boolean loadLine()
	{	try
		{	inputLine = inputReader.readLine();
			if( inputLine != null )
			{	++inputLineNumber;
				inputPosition = 0;
			}
			return inputLine != null;
		}
		catch( IOException e )
		{	return false;
		}
	}

	/** Return true if the current token matches the
	 *  candidate token.
	 */
	public boolean match( Token candidate )
	{	return currentToken == candidate;
	}

	/** Advance the input to the next token and return the current
	 *  token (the one in the input before the advance).
	 *  This returned token is valid only until the
	 *  next <code>advance()</code> call (at which time the
	 *  lexeme may change, for example).
	 */
	public Token advance() throws ParseFailure
	{	try
		{	
			if( currentToken != null )	// not at end of file
			{	
				inputPosition += currentToken.lexeme().length();
				currentToken   = null;

				if( inputPosition == inputLine.length() )
					if( !loadLine() )
						return null;

				while( Character.isWhitespace(
								inputLine.charAt(inputPosition)) )
					if( ++inputPosition == inputLine.length() )
						if( !loadLine() )
							return null;

				for( Iterator i = tokens.iterator(); i.hasNext(); ) //{=Scanner.search}
				{	Token t = (Token)(i.next());
					if( t.match(inputLine, inputPosition) )
					{	currentToken = t;
						break;
					}
				}

				if( currentToken == null )
					throw failure("Unrecognized Input");
			}
		}
		catch( IndexOutOfBoundsException e ){ /* nothing to do */ }
		return currentToken;
	}

	/**
	 * Throw a {@link ParseFailure} object initialized for the current
	 * input position. This method lets a parser that's using the
	 * current scanner report an error in a way that identifies
	 * where in the input the error occurred.
	 * @param message the "message" (as returned by
	 * 			{@link java.lang.Throwable.getMessage}) to attach
	 * 			to the thrown <code>RuntimeException</code> object.
	 * @throws	ParseFailure always.
	 */
	public ParseFailure failure( String message )
	{ 	return new ParseFailure(message,
						inputLine, inputPosition, inputLineNumber);
	}

	/** Combines the match and advance operations. Advance automatically
	 *  if the match occurs.
	 *  @return the lexeme if there was a match and the input was advanced,
	 *  	null if there was no match (the input is not advanced).
	 */
	public String matchAdvance( Token candidate ) throws ParseFailure
	{	if( match(candidate) )
		{	String lexeme = currentToken.lexeme();
			advance();
			return lexeme;
		}
		return null;
	}

	/**  If the specified candidate is the current token,
	 *   advance past it and return the lexeme; otherwise,
	 *   throw an exception with the rror message
	 *   "XXX Expected".
	 *   @throws ParseFailure if the required token isn't the
	 *   			current token.
	 */
	public final String required( Token candidate ) throws ParseFailure
	{	String lexeme =	matchAdvance(candidate);
		if( lexeme == null )
			throw failure(
					"\"" + candidate.toString() + "\" expected.");
		return lexeme;
	}

	/*--------------------------------------------------------------*/
	public static class Test
	{
		private static TokenSet tokens = new TokenSet();

		private static final Token
			COMMA		= tokens.create( "'," 			 	),
			IN			= tokens.create( "'IN'" 		 	),
			INPUT		= tokens.create( "INPUT"		 	),
			IDENTIFIER	= tokens.create( "[a-z_][a-z_0-9]*" );

		public static void main(String[] args) throws ParseFailure
		{
			assert COMMA 	  instanceof SimpleToken: "Factory Failure 1";
			assert IN 	  	  instanceof WordToken  : "Factory Failure 2";
			assert INPUT 	  instanceof WordToken  : "Factory Failure 3";
			assert IDENTIFIER instanceof RegexToken : "Factory Failure 4";

			Scanner analyzer = new Scanner( tokens, ",aBc In input inputted" );

			assert analyzer.advance() == COMMA 	 	: "COMMA unrecognized";
			assert analyzer.advance() == IDENTIFIER : "ID unrecognized";
			assert analyzer.advance() == IN 		: "IN unrecognized";
			assert analyzer.advance() == INPUT 	 	: "INPUT unrecognized";
			assert analyzer.advance() == IDENTIFIER : "ID unrecognized 1";

			analyzer = new Scanner(tokens, "Abc IN\nCde");
			analyzer.advance(); // advance to first token.

			assert( analyzer.matchAdvance(IDENTIFIER).equals("Abc") );
			assert( analyzer.matchAdvance(IN).equals("in")  );
			assert( analyzer.matchAdvance(IDENTIFIER).equals("Cde") );

			// Deliberately force an exception toss
			analyzer = new Scanner(tokens, "xyz\nabc + def");
			analyzer.advance();
			analyzer.advance();
			try
			{	analyzer.advance(); // should throw an exception
				assert false : "Error Detection Failure";
			}
			catch( ParseFailure e )
			{	assert e.getErrorReport().equals(
											  "Line 2:\n"
											+ "abc + def\n"
											+ "____^\n"	);
			}

			System.out.println("Scanner PASSED");

			System.exit(0);
		}
	}
}
