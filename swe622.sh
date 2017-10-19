#!/usr/bin/sh

port=9876

export PA1_SERVER="localhost:$port"

export CLASSPATH="$CLASSPATH;C:/Users/jmr/IdeaProjects/SWE622_FSS/out/artifacts/SWE622_FSS_jar/pa1.jar"

alias fss-startserver="java server start $port &"

alias fss-upload="java client upload './swe622.sh' '/' "
alias fss-rm="java client rm 'swe622.sh' "
alias fss-rmdir="java client rmdir 'test' "
alias fss-download="java client download 'swe622.sh' "
alias fss-dir="java client dir 'test' "
