<!--
  ~ SPDX-FileCopyrightText: 2017-2024 Enedis
  ~
  ~ SPDX-License-Identifier: Apache-2.0
  ~
  -->

# Local DEV packaging

This section describe the default configuration of this packaging.  
See all configuration files in [resources directory](./src/main/resources).

## Server

The server listens http requests on port 8080, redirected on https port 8443.  
It includes an Angular frontend.  
SSL and SpringBoot Actuator are activated.

## Persistence

The database used by default is SQLite.  
Its data file is located under the `.chutney/data` directory, relative to the root execution directory.  

Embedded H2 server or PostgreSQL could be activated by Spring profiles.  

All persistent Chutney repositories' files are located under the `.chutney/conf` directory, relative to the root execution directory.

## Authentication

LDAP authentication is activated by default.  
An embedded LDAP declares two users : `john` and `jahn`, the latter being an administrator.  

Memory authentication is activated by default.  
Two users are declared : `user` and `admin`, the latter being an administrator.
