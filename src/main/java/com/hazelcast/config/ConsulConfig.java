/*
 * Copyright (c) 2008-2015, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.config;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import static com.hazelcast.util.ValidationUtil.hasText;

public class ConsulConfig extends AbstractXmlConfigHelper{

    private static final int CONNECTION_TIMEOUT = 5;
    private static final int DEFAULT_VALUE = 5;

    private String name;
    private String host;
    private boolean enabled;
    private String type;
    private Node node;

    private int connectionTimeoutSeconds = CONNECTION_TIMEOUT;

    public ConsulConfig(SpiJoinerConfig joinerConfig) {
        this.enabled = joinerConfig.isEnabled();
        this.type = joinerConfig.getType();
        this.node = joinerConfig.getNode();
        handleConsul(node);
    }

    /**
     * Returns the connection timeout.
     *
     * @return the connectionTimeoutSeconds
     * @see #setConnectionTimeoutSeconds(int)
     */
    public int getConnectionTimeoutSeconds() {
        return connectionTimeoutSeconds;
    }

    /**
     * Sets the connection timeout. This is the maximum amount of time Hazelcast
     * will try to connect to a well known member before giving up. Setting it
     * to a too low value could mean that a member is not able to connect to a
     * cluster. Setting it as too high a value means that member startup could
     * slow down because of longer timeouts (e.g. when a well known member is
     * not up).
     *
     * @param connectionTimeoutSeconds the connection timeout in seconds.
     * @return the updated TcpIpConfig
     * @throws IllegalArgumentException if connectionTimeoutSeconds is smaller
     *                                  than 0.
     * @see #getConnectionTimeoutSeconds()
     */
    public ConsulConfig setConnectionTimeoutSeconds(final int connectionTimeoutSeconds) {
        if (connectionTimeoutSeconds < 0) {
            throw new IllegalArgumentException("connection timeout can't be smaller than 0");
        }
        this.connectionTimeoutSeconds = connectionTimeoutSeconds;
        return this;
    }

    /**
     * Checks if the Consul join mechanism is enabled.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables or disables the Consul join mechanism.
     *
     * @param enabled true to enable the Consul join mechanism, false to disable
     * @return ConsulConfig the updated ConsulConfig config.
     */
    public ConsulConfig setEnabled(final boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Set the serviceName
     *
     * @param name the service name to add.
     * @return the updated configuration.
     * @throws IllegalArgumentException if name is null or empty.
     */
    public ConsulConfig setName(String name) {
        String nameText = hasText(name, "name");
        this.name = nameText.trim();
        return this;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Set the consul hostname
     *
     * @param host the host, including optional port
     * @return the updated configuration.
     * @throws IllegalArgumentException if host is null or empty.
     */
    public ConsulConfig setHost(String host) {
        String nameText = hasText(host, "host");
        this.host = nameText.trim();
        return this;
    }

    public String getHost() {
        return this.host;
    }

    @Override
    public String toString() {
        return "Consul [enabled=" + enabled
                + ", connectionTimeoutSeconds=" + connectionTimeoutSeconds
                + ", name=" + this.name
                + "]";
    }

    private void handleConsul(final org.w3c.dom.Node node) {
        final NamedNodeMap atts = node.getAttributes();
        for (int a = 0; a < atts.getLength(); a++) {
            final org.w3c.dom.Node att = atts.item(a);
            final String value = getTextContent(att).trim();

            if (att.getNodeName().equals("connection-timeout-seconds")) {
                setConnectionTimeoutSeconds(getIntegerValue("connection-timeout-seconds", value, DEFAULT_VALUE));
            }
        }
        for (org.w3c.dom.Node n : new AbstractXmlConfigHelper.IterableNodeList(node.getChildNodes())) {
            final String value = getTextContent(n).trim();
            if ("name".equals(cleanNodeName(n.getNodeName()))) {
                setName(value);
            } else if ("host".equals(cleanNodeName(n.getNodeName()))) {
                setHost(value);
            }
        }
    }
}
