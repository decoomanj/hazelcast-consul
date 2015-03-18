# hazelcast-consul
A hazelcast resolver for Consul

(more info to come)

# Example

Update your cluster.xml like this:

The consul configuration takes the following parameters:
- host: the host of the consul api. You can add a port too. This is optional. 
- name: the service with which you want to connect.

When you want to connect to the local agent, you can omit the host entry.

Connect to a Consul server:

```
   <join>
            <consul enabled="true">
                <host>consul:8500</host>
                <name>my-service</name>
            </consul>
    </join>
```

or locally:

```
   <join>
            <consul enabled="true">
                <name>my-service</name>
            </consul>
    </join>
```
