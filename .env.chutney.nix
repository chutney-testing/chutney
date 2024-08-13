#SPDX-FileCopyrightText: 2017-2024 Enedis
#SPDX-License-Identifier: Apache-2.0

{ nixpkgs ? import <nixpkgs> {} }:
with nixpkgs;
let
  unstable = import (fetchTarball https://nixos.org/channels/nixos-unstable/nixexprs.tar.xz) { };
  jdk = openjdk17;
  mvn = maven.override { jdk  = jdk; };
in
mkShell {

  buildInputs = [
    unstable.nodejs_20
    unstable.act
    chromium
    geckodriver
    jdk
    mvn
  ];

  NPM_CONFIG_PROXY = builtins.getEnv "http_proxy";
  CHROME_BIN = "${chromium}/bin/chromium";
  JAVA_HOME="${jdk}/lib/openjdk";
  M2_HOME="${mvn}/maven";
}
