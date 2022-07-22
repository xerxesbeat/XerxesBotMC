package net.xerxesbeat.mcbot;

import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;

public class ChatUtil
{
	public static String plaintext ( Component component )
	{
		String ret = "";
		if ( component instanceof TranslatableComponent )
		{
			TranslatableComponent tc = (TranslatableComponent) component;
			if ( tc.key().equals( "chat.type.text" ) )
			{
				List<Component> contents = tc.args();
				int i = 0;
				for ( Component content : contents )
				{
					if ( i == 0 )
						ret = "<" + plaintext( content ) + ">";
					else
						ret += " " + plaintext( content );
					i++;
				}
				if ( i > 2 )
					System.err.println( "[WRN] Unknown Chat Format" );
			}
			else if ( tc.key().equals( "chat.type.admin" ) )
			{
				System.out.println( tc );
			}
			else
				System.err.println( "[WRN] Unhandled Chat Type '" + tc.key() + "'" );
		}
		else if ( component instanceof TextComponent )
		{
			ret += ( (TextComponent) component ).content();
		}
		else
			System.err.println( component.getClass() );
		return ret;
	}
}
