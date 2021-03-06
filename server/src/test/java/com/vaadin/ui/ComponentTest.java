/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.ui;

import java.lang.reflect.Method;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.ServerRpcManager;
import com.vaadin.shared.communication.ServerRpc;

/**
 * Base class for component unit tests, providing helper methods for e.g.
 * invoking RPC and updating diff state.
 */
public class ComponentTest {

    /**
     * Perform operations on the component similar to what would be done when
     * the component state is communicated to the client, e.g. update diff state
     * and mark as clean.
     *
     * @param component
     *            the component to update
     */
    public static void syncToClient(AbstractComponent component) {
        updateDiffState(component);
        component.getUI().getConnectorTracker().markClean(component);
    }

    /**
     * Checks if the connector has been marked dirty.
     *
     * @param connector
     *            the connector to check
     * @return <code>true</code> if the connector has been marked dirty,
     *         <code>false</code> otherwise
     */
    public static boolean isDirty(ClientConnector connector) {
        return connector.getUI().getConnectorTracker().isDirty(connector);
    }

    /**
     * Updates the stored diff state from the current component state.
     *
     * @param rta
     *            the component to update
     */
    public static void updateDiffState(AbstractComponent component) {
        component.getUI().getSession().getCommunicationManager()
                .encodeState(component, component.getState());

    }

    /**
     * Gets the server rpc handler registered for a component.
     *
     * @param component
     *            the component which listens to the RPC
     * @param serverRpcClass
     *            the server RPC class
     * @return the server RPC handler
     */
    public static <T extends ServerRpc> T getRpcProxy(Component component,
            Class<T> serverRpcClass) {
        try {
            ServerRpcManager<?> rpcManager = component
                    .getRpcManager(serverRpcClass.getName());
            Method method = ServerRpcManager.class
                    .getDeclaredMethod("getImplementation");
            method.setAccessible(true);
            return serverRpcClass.cast(method.invoke(rpcManager));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

}
