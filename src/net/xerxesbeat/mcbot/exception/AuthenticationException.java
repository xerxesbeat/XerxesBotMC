package net.xerxesbeat.mcbot.exception;

import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.auth.service.MojangAuthenticationService;
import com.github.steveice10.mc.auth.service.MsaAuthenticationService;
import com.github.steveice10.mc.protocol.MinecraftProtocol;

public class AuthenticationException extends Exception
{
	public AuthenticationException ( AuthenticationService auth, Throwable cause )
	{
		super( "Authentication Failure for " + auth.getUsername() + ":" + auth.getPassword().replaceAll( ".", "\\*" ) + ( ( auth instanceof MojangAuthenticationService )? " (Mojang)" : ( auth instanceof MsaAuthenticationService )? " (Microsoft)" : " (Unknown)" ), cause );
	}
}
