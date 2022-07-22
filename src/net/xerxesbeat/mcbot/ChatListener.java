package net.xerxesbeat.mcbot;

import net.kyori.adventure.text.Component;

public abstract class ChatListener
{
	protected abstract void onChat ( Component content );
}
