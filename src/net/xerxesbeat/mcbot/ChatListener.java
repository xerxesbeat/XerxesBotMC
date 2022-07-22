package net.xerxesbeat.mcbot;

import net.kyori.adventure.text.TranslatableComponent;
import net.xerxesbeat.mcbot.ChatUtil.TYPE;

public abstract class ChatListener
{
	protected abstract void onChat ( TYPE type, TranslatableComponent content );
}
