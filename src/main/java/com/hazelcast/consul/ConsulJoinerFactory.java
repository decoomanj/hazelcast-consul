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

import com.hazelcast.cluster.Joiner;
import com.hazelcast.config.spi.CustomJoinerConfig;
import com.hazelcast.config.spi.CustomJoinerFactory;
import com.hazelcast.instance.Node;

/**
 * @author: Jan De Cooman, Guiquan Weng
 */
public class ConsulJoinerFactory implements CustomJoinerFactory {

    /**
     * The tag must match the type in order to be activated.
     * 
     * @return 
     */
    @Override
    public String getType() {
        return ConsulJoiner.JOINER_TYPE;
    }

    /**
     * Creates a joiner. SPI doesn't allow parameters in constructors, so
     * we have to do it this way. However, it adds some complexity.
     * 
     * @param node
     * @param config
     * @return 
     */
    @Override
    public Joiner createJoiner(final Node node, final CustomJoinerConfig config) {
        return new ConsulJoiner(node, new ConsulConfig(config));
    }
}
