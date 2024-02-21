{ nixpkgs ? import <nixpkgs> {} }:
with nixpkgs;
let
  jdk = openjdk17;
in
mkShell {

  buildInputs = [
    jdk
  ];

  JAVA_HOME="${jdk}/lib/openjdk";
}
