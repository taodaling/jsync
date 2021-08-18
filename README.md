# jsync

A simple tool to transfer directory fast by network.

It's based on rsync algorithm and implemented as a java server end and client end. Server work to distribute file to client, and the transfered file is protected by AES encryption.

Download the [latest release](https://github.com/taodaling/jsync/releases), unzip it. Then execute follow commands in server:

```sh
# bin/server.cmd
```

Corresponding for client:

```sh
# bin/client.cmd
```

The conf/setting.properties contains all the configuration required, you can override them by jvm argument, for example `-Dport=8080`.

| field | explanation |
| - | - |
| root | the root directory |
| host | server's host |
| port | server's port |
| remote | client root should sync with server root/remote|
| block | keep it default | 
| maxOpenedFile | keep it default |
| delete | whether delete the old directory | 
| pwd | to encrypt or decrypt file |

