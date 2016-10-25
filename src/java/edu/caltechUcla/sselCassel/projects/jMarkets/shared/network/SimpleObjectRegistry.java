/*
 * Copyright (C) 2005-2006, <a href="http://www.ssel.caltech.edu">SSEL</a>
 * <a href="http://www.cassel.ucla.edu">CASSEL</a>, Caltech/UCLA
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
 * USA.
 *
 * Project Authors: Raj Advani, Walter M. Yuan, and Peter Bossaerts
 * Email: jmarkets@ssel.caltech.edu
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.network;

import java.rmi.*;
import java.rmi.registry.*;

/** A kind of registry that provides a very simple means of exchanging remote
 *  objects by name.  One can create a registry, allowing the constructor 
 *  to select an available port.  One can find a bound object by name over
 *  a sequence of port numbers and hosts. */
public class SimpleObjectRegistry {

  /** The default values for LOWPORT and HIGHPORT arguments. */
  static final int 
    DEFAULT_LOW_PORT = Registry.REGISTRY_PORT,
    DEFAULT_HIGH_PORT = DEFAULT_LOW_PORT + 9;

  /** Find an remote object named NAME in some registry on HOST on 
   *  some port number between LOWPORT and HIGHPORT.  Throws 
   *  NotBoundException if no such object is found for whatever reason 
   *  (i.e., either because no registry is found or because NAME
   *  is unbound on all such registries.) */
  public static Remote findObject (String name, String host, int lowPort, int highPort) throws NotBoundException {
    boolean aRegistryFound = false;
 
    for (int rport = lowPort; rport <= highPort; rport += 1) 
      try {
	return LocateRegistry.getRegistry (host, rport).lookup (name);
      }catch (NotBoundException e) {
          aRegistryFound = true; 
          e.printStackTrace(); 
      }catch (AccessException e) {
          System.out.println("*** Access Denied!" + host + " : " + rport); 
          e.printStackTrace(); 
      }catch (RemoteException e) {
          System.out.println("*** Remote exception!" + e.getClass().getName() + " : " +  host + " : " + rport); 
          e.printStackTrace(); 
      }
    
    if (aRegistryFound) 
      throw new NotBoundException ("name not bound in any registry on " + host + ": " + name);
    else
      throw new NotBoundException ("no registries found on " + host + ", ports " + lowPort + ".." + highPort);
  }

  /** Same as findObject (NAME, HOST, DEFAULT_LOW_PORT, DEFAULT_HIGH_PORT).
   */
  public static Remote findObject (String name, String host) throws NotBoundException {
    return findObject (name, host, DEFAULT_LOW_PORT, DEFAULT_HIGH_PORT);
  }

  /*  Find an remote object named NAME in some registry on one of the 
   *  hosts in HOSTS on some port number between LOWPORT and HIGHPORT.
   *  Throws NotBoundException if no such object is found for 
   *  whatever reason (i.e., either because no registry is found or 
   *  because NAME is unbound on all such registries.) */
  public static Remote findObject (String name, String[] hosts, int lowPort, int highPort) throws NotBoundException {
    boolean aRegistryFound;
    aRegistryFound = false;
    for (int i = 0; i < hosts.length; i += 1) 
      try {
	return findObject (name, hosts[i], lowPort, highPort);
      } catch (NotBoundException e) { 
	if (! e.getMessage ().startsWith ("name not bound"))
	  aRegistryFound = true;
      }
    if (aRegistryFound)
      throw new NotBoundException ("name not bound on any host: " + name);
    else
      throw new NotBoundException ("registry not found on any host");
  }

  /** Same as findObject (NAME, HOSTS, DEFAULT_LOW_PORT, DEFAULT_HIGH_PORT).
   */
  public static Remote findObject (String name, String[] hosts) 
    throws NotBoundException {
    return findObject (name, hosts, DEFAULT_LOW_PORT, DEFAULT_HIGH_PORT);
  }
				
  /** Create a new SimpleObjectRegistry on one of the ports LOWPORT to 	
   *  HIGHPORT, if possible.  Throws RemoteException if no registry can be
   *  created on any of these ports. */
  public SimpleObjectRegistry (int lowPort, int highPort)
    throws RemoteException {
    registry = null;
   
    for (port = lowPort; port <= highPort; port += 1)
      try {  
	registry = LocateRegistry.createRegistry (port);
	return;
      } catch (RemoteException e) { }
    for (port = lowPort; port <= highPort; port += 1)
      try {  
	registry = LocateRegistry.getRegistry (port);
	return;
      } catch (RemoteException e) { }
    throw new RemoteException ("no port available");
  }

  /** Same as SimpleObjectRegistry (PORT, PORT). */
  public SimpleObjectRegistry (int port) throws RemoteException {
    this (port, port);
  }

  /** Same as SimpleObjectRegistry (DEFAULT_LOW_PORT, DEFAULT_HIGH_PORT). */
  public SimpleObjectRegistry () throws RemoteException {
    this (DEFAULT_LOW_PORT, DEFAULT_HIGH_PORT);
  }

  /** The port number on which this registry is exported. */
  public int port () {
    return port;
  }

  /** Bind NAME to VALUE in this registry, replacing any existing binding. */
  public synchronized void rebind (String name, Remote value) {
    if (registry == null)
      throw new IllegalStateException ("registry not active");
    if (value == null)
      throw new IllegalArgumentException ("null value");
    try {
      registry.rebind (name, value);
    } catch (Exception e) {
      if (! (e instanceof RuntimeException))
	e = new RuntimeException ("unexpected exception in "
				  + "SimpleObjectRegistry.rebind: " + e);
      throw (RuntimeException) e;
    }
  }			 			

  /** Remove any binding of NAME in this registry. */
  public synchronized void unbind (String name) {
    if (registry == null)
      throw new IllegalStateException ("registry not active");
    try {
      registry.unbind (name);
    } catch (NotBoundException e) {
    } catch (Exception e) {
      if (! (e instanceof RuntimeException))
	e = new RuntimeException ("unexpected exception in "
				  + "SimpleObjectRegistry.unbind: " + e);
      throw (RuntimeException) e;
    }
  }

  /** Remove all bindings from this registry, and disable it from further 
   *  use.  */
  public synchronized void close () {
    if (registry == null)
      return;

    try {
      String[] names;
      names = null;

      names = registry.list ();

      for (int i = 0; i < names.length; i += 1)
	unbind (names[i]);
    } catch (Exception e) {
      if (e instanceof RuntimeException)
	throw (RuntimeException) e;
    } finally {
      registry = null;
      port = -1;
    }
  }

  private Registry registry;
  private int port;
}

