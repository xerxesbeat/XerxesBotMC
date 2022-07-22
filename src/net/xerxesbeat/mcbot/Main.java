package net.xerxesbeat.mcbot;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.time.Instant;
import java.util.LinkedList;

import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.auth.service.MojangAuthenticationService;
import com.github.steveice10.mc.auth.service.MsaAuthenticationService;
import com.github.steveice10.mc.auth.service.SessionService;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.ConnectedEvent;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.DisconnectingEvent;
import com.github.steveice10.packetlib.event.session.PacketErrorEvent;
import com.github.steveice10.packetlib.event.session.PacketSendingEvent;
import com.github.steveice10.packetlib.event.session.SessionListener;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import com.google.gson.Gson;

import net.kyori.adventure.text.Component;
import net.xerxesbeat.mcbot.exception.AuthenticationException;
import net.xerxesbeat.util.Util;

public class Main
{
	private static String HOST = "localhost";
	private static int PORT = 25565;

	private static final long TICK_MS = 1200; // outbound chat rate is tied to this
	private static final File CFG_ACCT = new File ( "account.json" );
	private static boolean LOG_PACKETS = false; // verbose packet classnames in stdout

	private enum STATE {
		LAUNCHED,
		LOGIN,
		INIT_CLIENT,
		CONNECT,
		CONNECTED,
		DISCONNECTING,
		DISCONNECTED,
		PUMP,
		ERROR
	}
	private static final int EXIT_SUCCESS = 0;
	private static final int EXIT_ERROR_UNKNOWN = 1;
	private static final int EXIT_ERROR_AUTH = 2;
	private static final int EXIT_ERROR_CFG = 3;
	private static final int EXIT_ERROR_DISCONNECT = 4;

	private static STATE state = STATE.LAUNCHED;
	private static Session client = null;
	private static Throwable disconnectCause = null;
	private static String disconnectReason = null;

	static {
		init();
		addChatListener( new ChatListener () {
			@Override
			protected void onChat ( Component content )
			{
				System.out.println( content.toString() );
			}
		});
	}

	public static void main ( String [] args )
	{
		state = STATE.LOGIN;
		try
		{
			login();
		}
		catch ( AuthenticationException e )
		{
			System.err.println( "Authentication Failure" );
			System.exit( EXIT_ERROR_AUTH );
		}
		catch ( IOException e )
		{
			System.err.println( "Account Configuration Missing" );
			System.exit( EXIT_ERROR_CFG );
		}
		if ( protocol != null )
		{
			state = STATE.INIT_CLIENT;
			SessionService sessionService = new SessionService ();
			sessionService.setProxy( Proxy.NO_PROXY );
			client = new TcpClientSession ( HOST, PORT, protocol, null );
			client.setFlag( MinecraftConstants.SESSION_SERVICE_KEY, sessionService );
			client.addListener( new SessionListener () {
	
				@Override
				public void packetReceived ( Session session, Packet packet )
				{
					if ( session != client )
					{
						state = STATE.ERROR;
						System.err.println( "ERROR: Multiple Client Sessions Detected" );
						return;
					}
					if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundEntityEventPacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundMoveEntityPosPacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundMoveEntityPosRotPacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundRemoveEntitiesPacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundRotateHeadPacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundSetEntityDataPacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundSetEntityMotionPacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundSetEquipmentPacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundTeleportEntityPacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundUpdateAttributesPacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundUpdateMobEffectPacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.spawn.ClientboundAddEntityPacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundBlockUpdatePacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundLevelChunkWithLightPacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundLevelEventPacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundLightUpdatePacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundSectionBlocksUpdatePacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundSetTimePacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundMoveEntityRotPacket );
					else if ( packet instanceof com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundKeepAlivePacket );
					else
					{
						if ( LOG_PACKETS )
							System.out.println( "[RCV] " + packet.getClass().getName() );
						if ( packet instanceof ClientboundSystemChatPacket )
						{
							ClientboundSystemChatPacket chat = (ClientboundSystemChatPacket) packet;
							Component content = chat.getContent();
							for ( ChatListener listener : chatListeners )
								listener.onChat( content );
						}
					}
				}
	
				@Override
				public void packetSending ( PacketSendingEvent event )
				{
					if ( LOG_PACKETS )
						System.out.println( "[SND] " + event.getPacket().getClass().getName() );
				}
	
				@Override
				public void packetSent ( Session session, Packet packet )
				{
	//				[SND] com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundKeepAlivePacket
					if ( session != client )
					{
						state = STATE.ERROR;
						System.err.println( "ERROR: Multiple Client Sessions Detected" );
						return;
					}
					if ( LOG_PACKETS )
						System.out.println( "[SNT] " + packet.getClass().getName() );
				}
	
				@Override
				public void packetError ( PacketErrorEvent event )
				{
					state = STATE.ERROR;
					System.err.println( "ERROR: Packet Error (" + event.getCause().toString() + ")" );
					return;
				}
	
				@Override
				public void connected ( ConnectedEvent event )
				{
					System.out.println( "Connection Established" );
				}
	
				@Override
				public void disconnecting ( DisconnectingEvent event )
				{
					state = STATE.DISCONNECTING;
					System.out.println( "Disconnecting..." );
				}
	
				@Override
				public void disconnected ( DisconnectedEvent event )
				{
					state = STATE.DISCONNECTED;
					System.out.println( "Disconnected." );
					disconnectCause = event.getCause();
					disconnectReason = event.getReason();
					
				}
			});
			state = STATE.CONNECT;
			client.connect();
		}
		else
			System.err.println( "[ERR] Unknown Authentication Failure" );
// MAIN LOOP
		if ( client != null && client.isConnected() )
			state = STATE.CONNECTED;
		else
		{
			state = STATE.ERROR;
			System.exit( EXIT_ERROR_UNKNOWN );
		}
run:	while ( true )
		{
			long frameStart = Instant.now().toEpochMilli();
			switch ( state )
			{
			case ERROR:
				if ( client.isConnected() )
					client.disconnect( "Internal Error" );
				System.exit( EXIT_ERROR_UNKNOWN );
				return; // this should never happen, but if it does, return immediately is what should happen
			case PUMP:
				if ( !chatQueue.isEmpty() )
					client.send( new ServerboundChatPacket ( chatQueue.pop(), Instant.now().toEpochMilli(), 0, new byte [0], false ) );
				break;
			case DISCONNECTED:
				if ( disconnectCause != null )
				{
					disconnectCause.printStackTrace();
					System.err.println( "[ERR] Disconnected: " + disconnectReason );
					System.exit( EXIT_ERROR_DISCONNECT );
				}
				System.out.println( "Disconnected: " + disconnectReason );
				System.exit( EXIT_SUCCESS );
				return; // this should never happen, but if it does, return immediately is what should happen
			default:
				long frameEnd = Instant.now().toEpochMilli();
				try
				{
					Thread.sleep( TICK_MS - ( frameEnd - frameStart ) );
				}
				catch ( InterruptedException e )
				{
					break run;
				}
			}
		}
		System.err.println( "Program Exited Unexpectedly" );
		System.exit( EXIT_ERROR_UNKNOWN );
	}

	private static MinecraftProtocol protocol = null;
	private static void login () throws AuthenticationException, IOException
	{
		String acctCfg = Util.readFileAtOnce( CFG_ACCT );
		Account acct = Account.fromJson( acctCfg );
		AuthenticationService authService;
		if ( acct.type == Account.TYPE.MICROSOFT )
			authService = new MsaAuthenticationService ( acct.remoteId, acct.profileId );
		else
			authService = new MojangAuthenticationService ();
		authService.setUsername( acct.username );
		authService.setPassword( acct.password );
		authService.setProxy( Proxy.NO_PROXY );
		try
		{
			authService.login();
		}
		catch ( RequestException e )
		{
			throw new AuthenticationException ( authService );
		}
		protocol = new MinecraftProtocol ( authService.getSelectedProfile(), authService.getAccessToken() );
	}

// Operability

	private static void init ()
	{
		// idiot proofing
		if ( HOST == null || HOST.isBlank() )
			HOST = "localhost";
		if ( PORT <= 0 || PORT > 65535 )
			PORT = 25565;
		// end idiot proofing

		chatListeners = new LinkedList<ChatListener> ();
		chatQueue = new LinkedList<String> ();
	}

	private static LinkedList<ChatListener> chatListeners;
	private static void addChatListener ( ChatListener listener )
	{
		chatListeners.add( listener );
	}

	private static LinkedList<String> chatQueue;
	public static void chat ( String message )
	{
		chatQueue.add( message );
	}

// Account Login Stuffs
	private static class Account
	{
		enum TYPE {
			MICROSOFT,
			MOJANG
		};

		String username, password, remoteId, profileId;
		TYPE type;

		Account ( String username, String password )
		{
			this.type = TYPE.MOJANG;
			this.username = username;
			this.password = password;
		}

		Account ( String username, String password, String remoteId, String profileId )
		{
			this.type = TYPE.MICROSOFT;
			this.username = username;
			this.password = password;
			this.remoteId = remoteId;
			this.profileId = profileId;
		}

		static Account fromJson ( String json )
		{
			Gson gson = new Gson ();
			return gson.fromJson( json, Account.class );
		}
	}
}
