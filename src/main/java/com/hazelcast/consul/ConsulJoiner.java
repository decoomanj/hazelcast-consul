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
package com.hazelcast.consul;

import com.hazelcast.cluster.impl.TcpIpJoiner;
import com.hazelcast.instance.Node;
import com.hazelcast.util.AddressUtil;
import com.hazelcast.util.ExceptionUtil;
import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.catalog.CatalogService;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ConsulJoiner extends TcpIpJoiner {

    public static final String JOINER_TYPE = "consul";

    private CatalogClient agentClient;
    
    final private ConsulConfig consulConfig;

    public ConsulJoiner(final Node node, final ConsulConfig consulConfig) {
        super(node);
        this.consulConfig = consulConfig;
        
        // autoconnect to consul
        this.connect();
    }

    /**
     * Connect to the consul
     */
    private void connect() {
        String host = this.consulConfig.getHost();
        if (host != null && !host.trim().isEmpty()) {
            AddressUtil.AddressHolder addressHolder = AddressUtil.getAddressHolder(host, 5800);
            logger.finest("Connecting to consul at: " + addressHolder.toString());
            this.agentClient = Consul.newClient(addressHolder.getAddress(), addressHolder.getPort()).catalogClient();
        } else {
            logger.finest("Connecting to local consul agent");
            this.agentClient = Consul.newClient().catalogClient();
        } 
    }

    /**
     * Get a list of all members.
     * 
     * @return a collection with all members
     */
    @Override
    protected Collection<String> getMembers() {

        Collection<String> list = new LinkedList<>();
        String name = consulConfig.getName();
        try {

            ConsulResponse<List<CatalogService>> service = this.agentClient.getService(name);

            logger.info("Resolving service: " + name);

            for (CatalogService s : service.getResponse()) {
                if (logger.isFinestEnabled()) {
                    logger.finest("Found service at: " + s.getAddress());
                }
                list.add(s.getAddress());
            }

            if (list.isEmpty()) {
                logger.info("No services found!");
            }
            return list;
        } catch (Exception e) {
            logger.warning(e);
            throw ExceptionUtil.rethrow(e);
        }
    }

    @Override
    protected int getConnTimeoutSeconds() {
        return this.consulConfig.getConnectionTimeoutSeconds();
    }

    @Override
    public String getType() {
        return JOINER_TYPE;
    }

}
