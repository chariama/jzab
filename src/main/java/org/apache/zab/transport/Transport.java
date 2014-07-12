/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zab.transport;

import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * Abstract transport class. Used for communication between different
 * Zab instances. It will handle connections to different peers underneath.
 * The transport will also handle reconnections. Before it reconnects to
 * the disconnected peer, it will first call onDisconnected callback of
 * receiver.
 */
public abstract class Transport {

  protected Receiver receiver;

  public Transport(Receiver r) {
    this.receiver = r;
  }

  /**
   * Sends a message to a specific server. The channel delivers
   * the message in FIFO order. Transport establishes a connection to the
   * destination implicitly the first time this method is called with a given
   * destination.
   *
   * @param destination the id of the message destination
   * @param message the message to be sent
   */
  public abstract void send(String destination, ByteBuffer message);

  /**
   * Closes the connection to the destination. If there is no connection to the
   * destination, this method does nothing. This method clears any pending
   * outgoing messages. Transport reestablishes the connection on the next
   * send().
   */
  public abstract void disconnect(String destination);

  /**
   * Broadcasts a message to a set of peers.
   *
   * @param peers the set of destination peers.
   * @param message the message to be broadcasted.
   */
  public void broadcast(Iterator<String> peers, ByteBuffer message) {
    while (peers.hasNext()) {
      send(peers.next(), message);
    }
  }

  /**
   * Shutdown the transport.
   *
   * @throws InterruptedException if it's interrupted.
   */
  public abstract void shutdown() throws InterruptedException;

  /**
   * Interface of receiver class. Transport will notify the receiver of
   * arrived messages.
   */
  public interface Receiver {
    /**
     * Callback that will be called by Transport class once the message
     * is arrived. The message is guaranteed to be received in FIFO order,
     * which means if message m1 is sent before m2 from sender s, then m1
     * is guaranteed to be received first.
     *
     * @param source the id of the server who sent the message
     * @param message the message
     */
    void onReceived(String source, ByteBuffer message);

    /**
     * Callback that notifies the the connection to peer is disconnected.
     * Note that this callback is invoked even when the connection was closed
     * explicitly by the user via the disconnect() method.
     *
     * @param destination the ID of the peer from which the transport got
     *                    disconnected
     */
    void onDisconnected(String destination);
  }
}
