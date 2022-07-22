package net.xerxesbeat.mcbot;

import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;

public class ChatUtil
{
	public static enum TYPE {
		INVALID,
		SYSTEM,
		PLAYER
	}

	public static String plaintextSystemChat ( Component component )
	{
		if ( !( component instanceof TranslatableComponent ) )
			return null;
		TranslatableComponent trans = (TranslatableComponent) component;
		if ( trans.key().equals( "chat.type.text" ) )
		{
			List<Component> texts = (List<Component>) trans.args();
			TextComponent [] text = texts.toArray( new TextComponent [0] );
			return "<" + text[0].content() + "> " + text[1].content();
		}
		else if ( trans.key().equals( "chat.type.admin" ) )
		{
			System.err.println( trans.compact().toString() );
		}
		return null;
	}
}
