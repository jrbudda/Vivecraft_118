#!/bin/bash
python2 getchanges.py -m mcp_nonvr -v NONVR
python2 build.py  -m mcp_nonvr -v NONVR $@

