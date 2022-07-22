package net.xerxesbeat.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.UUID;

public class Util
{
	public static void writeFileAtOnce ( File filename, String content ) throws IOException
	{
		FileOutputStream fout = new FileOutputStream ( filename );
		fout.write( content.getBytes() );
		fout.close();
	}

	public static String readFileAtOnce ( File filename ) throws IOException
	{
		FileInputStream fin = new FileInputStream ( filename );
		byte [] bytes = new byte [fin.available()];
		fin.read( bytes );
		fin.close();
		return new String ( bytes );
	}

	public static UUID stringToUUID ( String id )
	{
		id = id.replaceAll( "[ \\-\\r\\n\\t]*", "" );
		long upper, lower;
		if ( id.length() >= 16 )
		{
			lower = new BigInteger ( id.substring( id.length() - 16 ), 16 ).longValue();
			upper = new BigInteger ( "0" + id.substring( 0, id.length() - 16 ), 16 ).longValue();
		}
		else
		{
			lower = new BigInteger ( id, 16 ).longValue();
			upper = 0l;
		}
		return new UUID ( upper, lower );
	}

	public static LinkedList<String> tokenize ( String string )
	{
		boolean doublequote = false, singlequote = false;
		String token = "";
		LinkedList<String> tokens = new LinkedList<String> ();
		for ( int i = 0; i < string.length(); i++ )
		{
			char c = string.charAt( i );
			if ( c == '\\' )
			{
				if ( i == string.length() - 1 )
					return null; // invalid escape sequence
				char c2 = string.charAt( i + 1 );
				switch ( c2 )
				{
	// TODO handle other escape sequences (\\([nrt]|x[0-9a-fA-F]{2}))
				default: // handles "\\", "\'" and "\"" (also, all non-escape sequence characters as themselves)
					token += c2;
					i++;
				}
			}
			else if ( !( doublequote || singlequote ) && " \t\r\n".indexOf( c ) >= 0 )
			{
				if ( !token.equals( "" ) )
				{
					tokens.addLast( token );
					token = "";
				}
			}
			else if ( !singlequote && c == '"' )
				if ( doublequote )
				{
					token += c;
					doublequote = false;
					tokens.addLast( token );
					token = "";
				}
				else
				{
					if ( !token.equals( "" ) )
					{
						// split tokens as if whitespace were present (technically bad syntax?)
						tokens.addLast( token );
						token = "";
					}
					doublequote = true;
					token += c;
				}
			else if ( !doublequote && c == '\'' )
				if ( singlequote )
				{
					token += c;
					singlequote = false;
					tokens.addLast( token );
					token = "";
				}
				else
				{
					if ( !token.equals( "" ) )
					{
						// split tokens as if whitespace were present (technically bad syntax?)
						tokens.addLast( token );
						token = "";
					}
					singlequote = true;
					token += c;
				}
			else if ( c == ';' )
			{
				if ( !token.equals( "" ) )
				{
					tokens.addLast( token );
					token = "";
				}
				tokens.addLast( new String ( ";" ) ); // string literals are same object without String constructor
			}
			else
			{
				token += c;
			}
		}
		if ( doublequote || singlequote )
			return null; // unfinished quotes
		if ( !token.equals( "" ) )
			tokens.addLast( token );
		return tokens;
	}

	public static String SHA1 ( String message )
	{
	    MessageDigest md = null;
	    try
	    {
	        md = MessageDigest.getInstance( "SHA-1" );
	    }
	    catch ( NoSuchAlgorithmException e )
	    {
	        e.printStackTrace();
	    } 
	    return new BigInteger ( md.digest( message.getBytes() ) ).toString( 16 );
	}
}
