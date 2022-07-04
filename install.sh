#!/bin/bash
python2 install.py -m mcp_vr $@ # runs installer
mkdir -p mcp_nonvr # create nonvr directory
ln -f -s -r -t mcp_vr lib # linking jars to vr for eclipse
ln -f -s -r -t mcp_nonvr lib # linking jars to nonvr for eclipse
cp -R -f mcp_vr/* mcp_nonvr # make nonvr
python2 applychanges.py -m mcp_nonvr -v NONVR # build
