!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/function/NetworkFunctions.java){:target="_blank"}"

Following functions help you to :

* Generate IP
* Generate network mask
* Find available UDP and TCP ports

# HostIpMatching

!!! note "String hostIpMatching(String regex)"

    Return a local ip matching `regex` input or else `InetAddress.getLocalHost().getHostAddress()` if no ip matching the regex found

    **Returns** :

    * Return a String representing an IP address matching the `regex`. For example `"127.0.0.1"`

    **Examples** :

    SpEL : `${#hostIpMatching("127.0.*")}`

# RandomNetworkMask

!!! note "String randomNetworkMask()"

    Constructs a random network mask matching regex `^\d{1,3}\.\d{1,3}\.\d{1,3}$`.

    **Returns** :

    * Returns a String representing a network mask. For example `"145.242.119"`

    **Examples** :

    SpEL : `${#randomNetworkMask()}`

# TcpPort

!!! note "int tcpPort()"

    Find an available TCP port randomly selected from the range [1024, 65535].

    **Returns** :

    * Returns an available TCP Port number

    **Examples** :

    SpEL : `${#tcpPort()}`

!!! note "SortedSet<Integer> tcpPorts(int num)"

    Find the requested number of available TCP ports, each randomly selected from the range [1024, 65535].

    **Returns** :

    * Returns a sorted set of available TCP port numbers

    **Examples** :

    SpEL : `${#tcpPorts(2000)}`

!!! note "int tcpPortMin(int minPort)"

    Find an available TCP port randomly selected from the range [minPort, 65535].

    **Returns** :

    * Returns an available TCP port number

    **Examples** :

    SpEL : `${#tcpPortMin(4455)}`

!!! note "int tcpPortMinMax(int minPort, int maxPort)"

    Find an available TCP port randomly selected from the range [minPort, maxPort].

    **Returns** :

    * Returns an available TCP port number

    **Examples** :

    SpEL : `${#tcpPortMinMax(1400, 62335)}`

!!! note "SortedSet<Integer> tcpPortsMinMax(int num, int minPort, int maxPort)"

    Find the requested number of available TCP ports, each randomly selected from the range [minPort, maxPort].

    **Returns** :

    * Returns a sorted set of available TCP port numbers

    **Examples** :

    SpEL : `${#tcpPortsMinMax(420, 500, 1000)}`

!!! note "int tcpPortRandomRange(int range)"

    Find an available TCP port randomly selected from the range [minPort, maxPort].

    **Returns** :

    * Returns an available TCP port number 

    **Examples** :

    SpEL : `${#tcpPortRandomRange(120)}`

!!! note "SortedSet<Integer> tcpPortsRandomRange(int num, int range)"

    Find the requested number of available TCP ports, each randomly selected from the range [minPort, maxPort].

    **Returns** :

    * Returns a sorted set of available TCP port numbers

    **Examples** :

    SpEL : `${#tcpPortsRandomRange(120, 230)}`

# UdpPort

!!! note "int udpPort()"

    Find an available UDP port randomly selected from the range [1024, 65535].

    **Returns** :

    * Returns an available UDP port number

    **Examples** :

    SpEL : `${#udpPort()}`

!!! note "SortedSet<Integer> udpPorts(int num)"

    Find the requested number of available UDP ports, each randomly selected from the range [1024, 65535].

    **Returns** :

    * Returns a sorted set of available UDP port numbers

    **Examples** :

    SpEL : `${#udpPorts(2500)}`

!!! note "int udpPortMin(int minPort)"

    Find an available UDP port randomly selected from the range [minPort, 65535].

    **Returns** :

    * Returns an available UDP port number

    **Examples** :

    SpEL : `${#udpPortMin(2331)}`

!!! note "int udpPortMinMax(int minPort, int maxPort)"

    Find an available UDP port randomly selected from the range [minPort, maxPort].

    **Returns** :

    * Returns an available UDP port number

    **Examples** :

    SpEL : `${#udpPortMinMax(250, 1544)}`

!!! note "SortedSet<Integer> udpPortsMinMax(int num, int minPort, int maxPort)"

    Find the requested number of available UDP ports, each randomly selected from the range [minPort, maxPort].

    **Returns** :

    * Returns a sorted set of available UDP port numbers

    **Examples** :

    SpEL : `${#udpPortsMinMax(342, 250, 1544)}`

!!! note "int udpPortRandomRange(int range)"

    Find an available UDP port randomly selected from the range [minPort, maxPort].

    **Returns** :

    * Returns an available UDP port number

    **Examples** :

    SpEL : `${#udpPortRandomRange(152)}`

!!! note "SortedSet<Integer> udpPortsRandomRange(int num, int range)"

    Find the requested number of available UDP ports, each randomly selected from the range [minPort, maxPort].

    **Returns** :

    * Returns a sorted set of available UDP port numbers

    **Examples** :

    SpEL : `${#udpPortsRandomRange(152, 12)}`
