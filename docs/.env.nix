{ pkgs ? import <nixpkgs> {} }:
let
  pypkg = p: with p; [
    mkdocs
    mkdocs-material
    mkdocs-redirects
    (
      buildPythonPackage rec {
        pname = "mkdocs-git-revision-date-localized-plugin";
        version = "1.1.0";
        src = fetchPypi {
          inherit pname version;
          sha256 = "38517e2084229da1a1b9460e846c2748d238c2d79efd405d1b9174a87bd81d79";
        };
        doCheck = false;
        propagatedBuildInputs = [
          pkgs.python310Packages.GitPython
          pkgs.python310Packages.babel
          pkgs.python310Packages.mkdocs
        ];
      }
    )
  ];
  chutney-doc = pkgs.python310.withPackages pypkg;
in chutney-doc.env
