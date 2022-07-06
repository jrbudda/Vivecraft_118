#!/bin/bash
python2 install.py -m mcp_vr $@ # runs installer
rmdir mcp_vr/lib # delete empty directory
mkdir -p mcp_nonvr # create nonvr directory
ln -f -s -r -t mcp_vr lib # linking jars to vr for eclipse
ln -f -s -r -t mcp_nonvr lib # linking jars to nonvr for eclipse
./eclipseclasspath.sh mcp_vr # jar finding and classpath building
cp -R -f mcp_vr/* mcp_nonvr # make nonvr
python2 applychanges.py -m mcp_nonvr -v NONVR # build
