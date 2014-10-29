/*
 *=BEGIN CLOSED LICENSE
 *
 * Copyright(c) 2012 András Csizmadia.
 * http://www.vpmedia.eu
 *
 * For information about the licensing and copyright please 
 * contact András Csizmadia at andras@vpmedia.eu.
 *
 *=END CLOSED LICENSE
 */
 
package hu.vpmedia.media.red5.dev;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.red5.server.api.Red5;
import org.red5.server.api.IScope;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.IBandwidthConfigure;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.scheduling.IScheduledJob;
import org.red5.server.api.scheduling.ISchedulingService;
import org.red5.server.api.stream.support.SimpleConnectionBWConfig;
import org.red5.server.api.stream.ISubscriberStream;
import org.red5.server.api.stream.IStreamCapableConnection;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.server.api.service.ServiceUtils;

import hu.vpmedia.media.red5.bwcheck.BandwidthDetection;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class Application extends MultiThreadedApplicationAdapter
{    
    private static final Log log = LogFactory.getLog( Application.class );
    //public static Logger log = LoggerFactory.getLogger(Application.class);
    
    /**
     * Red5 Level
     */
    
    @Override
    public synchronized boolean start(IScope scope) {
        log.info( "Main.start" );
        return super.start(scope);
    }  
    
    @Override
    public synchronized void stop ( IScope scope )    
    {        
        log.info( "Main.stop" );
        super.stop(scope);
    }      
          
    @Override
    public boolean connect(IConnection conn, IScope scope, Object[] params) {
        log.info( "Main.connect " + conn.getClient().getId() );
        return super.connect(conn, scope, params);
    }
    
    @Override
    public void disconnect(IConnection conn, IScope scope) {
        log.info( "Main.disconnect " + conn.getClient().getId() );
        super.disconnect(conn, scope);
    }
    
    @Override
    public synchronized boolean join(IClient client, IScope scope) {
        log.info( "Main.join " + client.getId() );
        return super.join(client, scope);
    }
    
    @Override
    public synchronized void leave(IClient client, IScope scope) {
        log.info( "Main.leave " + client.getId() );
        super.leave(client, scope);
    }
    
    /**
     * App level
     */
    
    @Override
    public boolean appStart ( IScope scope )    
    {        
        log.info( "Main.appStart" );
        return super.appStart(scope);
    }   
     
    @Override
    public void appStop ( IScope scope )    
    {        
        log.info( "Main.appStop" );
        super.appStop(scope);
    } 
    
    @Override        
    public boolean appConnect( IConnection conn , Object[] params )    
    {        
        log.info( "Main.appConnect " + conn.getClient().getId() );

        return super.appConnect(conn, params);
    }
        
    @Override
    public void appDisconnect( IConnection conn )    
    {        
        log.info( "Main.appDisconnect " + conn.getClient().getId() );
        super.appDisconnect(conn);
    } 
    
    @Override   
    public boolean appJoin(IClient client, IScope scope) {
        log.info( "Main.appJoin " + client.getId() );
        return super.appJoin(client, scope);
    } 
    
    @Override    
    public void appLeave(IClient client, IScope room) {
        log.info( "Main.appLeave " + client.getId() );
        super.appLeave(client, room);
    }
        
    /**
     * Room level
     */
    @Override    
    public boolean roomStart ( IScope scope )    
    {        
        log.info( "Main.roomStart" );
        return super.roomStart(scope);
    } 

    @Override    
    public void roomStop ( IScope scope )    
    {        
        log.info( "Main.roomStop" );
        super.roomStop(scope);
    } 
    
    @Override   
    public boolean roomConnect( IConnection conn , Object[] params )    
    {        
        log.info( "Main.roomConnect " + conn.getClient().getId() );
        //boolean accept = (Boolean)params[0];
        // if ( !accept ) rejectClient( "you passed false..." );
        // getting client parameters        
        Map properties = conn.getConnectParams();
        log.info( "Connection properties " + properties.toString() );
        //connection time
        Long stamp = System.currentTimeMillis( );
        // client ip        
        String ip = (String)conn.getRemoteAddress( );
        // agent        
        String agent = (String)properties.get( "flashVer" );
        // referrer        
        String referrer = (String)properties.get( "swfUrl" );
        // this will be our stream list        
        Object[ ] streamList = { };
                                                     
        // Trigger calling of "onBWDone", required for some FLV players
        ServiceUtils.invokeOnConnection(conn, "onBWDone", new Object[] {0,0,0,0});
        //BandwidthDetection detect = new BandwidthDetection();
		//detect.checkBandwidth(conn);
        /*
        if (conn instanceof IStreamCapableConnection) {
           IStreamCapableConnection streamConn = (IStreamCapableConnection) conn;
           SimpleConnectionBWConfig bwConfig = new SimpleConnectionBWConfig();
           bwConfig.getChannelBandwidth()[IBandwidthConfigure.OVERALL_CHANNEL] = 1024 * 1024;
           bwConfig.getChannelInitialBurst()[IBandwidthConfigure.OVERALL_CHANNEL] = 128 * 1024;
           streamConn.setBandwidthConfigure(bwConfig);
        } */       
        ServiceUtils.invokeOnConnection(conn, "onRoomLogin", new Object[] {"guest"+conn.getClient().getId()});
        onUserListChange( conn );    
        return super.roomConnect(conn, params);
    } 

    @Override
    public void roomDisconnect( IConnection conn )    
    {        
        log.info( "Main.roomDisconnect " + conn.getClient().getId() );
        super.roomDisconnect(conn);
    } 
    
    @Override   
    public boolean roomJoin( IClient client , IScope scope ) {
        log.info( "Main.roomJoin " + client.getId() ); 
        // If you need the connecion object you can access it via.
		// IConnection conn = Red5.getConnectionLocal(); 		                 
        return super.roomJoin(client, scope);
    } 
       
    @Override    
    public void roomLeave(IClient client, IScope room) {
        log.info( "Main.roomLeave " + client.getId() );
        super.roomLeave(client, room);
    }
    
    /* see IStreamAwareScopeHandler */
    public void streamSubscriberStart ( ISubscriberStream stream )    
    {        
        log.info( "Main.streamSubscriberStart" );        
        // somebody started a stream  
        try {            
            IConnection conn = stream.getConnection( );        
            IClient client = conn.getClient( );   
        } catch ( Exception e ) {
            log.fatal ( "Fatal error!" , e );
            throw new RuntimeException ( e );
        }
        // Long stamp = (Long)client.getAttribute( "stamp" );        
        // if client isn't already watching, setting stamp        
        // if ( stamp == 0 ) client.setAttribute( "stamp" , System.currentTimeMillis( ) );  
    }   
    
    public void streamPublishStart ( ISubscriberStream stream )    
    {        
        log.info( "Main.streamPublishStart" );
    }
    
    public void streamSubscriberStop ( ISubscriberStream stream )    
    {        
        log.info( "Main.streamSubscriberStop" );
    }
    
    public void streamRecordStart ( ISubscriberStream stream )    
    {        
        log.info( "Main.streamRecordStart" );
    }
    
    public void streamRecordStop ( ISubscriberStream stream )    
    {        
        log.info( "Main.streamRecordStop" );
    }
    
    /* Server API */
    
    public void onUserListChange( IConnection conn ) {
        log.info( "onUserListChange" );              
        try {            
            //IConnection conn = Red5.getConnectionLocal();  
            IScope scope = conn.getScope();
            ArrayList<HashMap> resultList = new ArrayList();
            
            HashMap<String, String> result = new HashMap<String, String>();
            result.put("loginName", "guest"+conn.getClient().getId() );
            result.put("userType", "ADMIN");            
            
            resultList.add( result );

            ServiceUtils.invokeOnAllConnections("onUserListChange", new Object[]{resultList}); 
        } catch ( Exception e ) {
            log.fatal ( "Fatal error!" , e );
            throw new RuntimeException ( e );
        }        
    }
    
    /* Client API */
        
    public void chatMessage(String message) {
        log.info( "chatMessage" );
        try {            
            ServiceUtils.invokeOnAllConnections("onChatMessage", new Object[]{message}); 
        } catch ( Exception e ) {
            log.fatal ( "Fatal error!" , e );
            throw new RuntimeException ( e );
        }     
    }
    
    public void logoutRoom() {
        log.info( "logoutRoom" );
        try {            
            IConnection conn = Red5.getConnectionLocal();
            IClient client = conn.getClient();
            client.disconnect();
        } catch ( Exception e ) {
            log.fatal ( "Fatal error!" , e );
            throw new RuntimeException ( e );
        }
    }
    
    public String getHost(String extId) {
        log.info( "getHost" );
        return "localhost";
    }

       
}