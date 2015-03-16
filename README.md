# hazelcast-consul
A hazelcast resolver for Consul

(more info to come)

# Example

Update your cluster.xml like this:


```
   <join>
            <multicast enabled="false">

            </multicast>
            <tcp-ip enabled="false">                

            </tcp-ip>
            <consul enabled="true">
                <name>hazelcast-module</name>
            </consul>
    </join>
```