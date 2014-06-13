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

package org.apache.zab;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * The state machine interface. It contains a list
 * of callbacks which will be called by Zab. It
 * should be implemented by user.
 */
public interface StateMachine {
  /**
   * This method is called only on the leader after
   * the Zxid has been assigned but before proposing
   * the message to followers.
   *
   * @param zxid zxid of the message.
   * @param message the original message
   * @return an idempotent state update based on
   * the original message. This is what gets proposed
   * to followers.
   */
  byte[] preprocess(Zxid zxid, byte[] message);

  /**
   * Called after the state update has been committed.
   * This method is called from a single thread to ensure
   * that the state updates are applied in the same order
   * they arrived.
   *
   * @param zxid zxid of the message
   * @param stateUpdate the incremental state update
   */
  void deliver(Zxid zxid, byte[] stateUpdate);

  /**
   * Serializes state of the application to OutputStream.
   * Once this callback is called. The user should write
   * the state of their application to os. This can be
   * called from a different thread of the that calls deliver
   * to avoid blocking the delivery of the message.
   *
   * @param os the output stream
   */
  void getState(OutputStream os);

  /**
   * Deserializes the state of the application from the
   * IputStream. Once this callback is called. The user
   * could restore their application state from the input
   * stream. This can be only called from the same thread of
   * the one calls deliver to avoid ending up in inconsistent
   * state.
   *
   * @param is the input stream
   */
  void setState(InputStream is);
}
