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
import com.hazelcast.config.ConsulConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.ExceptionUtil;
import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.catalog.CatalogService;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class TcpIpJoinerOverConsul extends TcpIpJoiner {

    final CatalogClient agentClient;
    final ConsulConfig consulConfig;
    final ILogger logger;

    public TcpIpJoinerOverConsul(Node node) {
        super(node);
        this.agentClient = Consul.newClient().catalogClient();
        logger = node.getLogger(getClass());
        consulConfig = node.getConfig().getNetworkConfig().getJoin().getConsulConfig();
    }

    @Override
    protected Collection<String> getMembers() {

        Collection<String> list = new LinkedList<>();
        String name = consulConfig.getName();
        try {

            ConsulResponse<List<CatalogService>> service = this.agentClient.getService(name);

            logger.warning("Resolving service: " + name);

            for (CatalogService s : service.getResponse()) {
                if (logger.isFinestEnabled()) {
                    logger.finest("Found service at: " + s.getAddress());
                }
                list.add(s.getAddress());
            }

            if (list.isEmpty()) {
                logger.warning("No consul instances found!");
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
        return "consul";
    }
}
