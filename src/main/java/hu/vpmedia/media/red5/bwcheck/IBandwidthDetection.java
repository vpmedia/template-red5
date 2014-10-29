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
 
package hu.vpmedia.media.red5.bwcheck;

import org.red5.server.api.IConnection;

public interface IBandwidthDetection {
	public void checkBandwidth(IConnection p_client);
	public void calculateClientBw(IConnection p_client);
}