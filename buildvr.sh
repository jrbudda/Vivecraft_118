#!/bin/bash
python2 getchanges.py -m mcp_vr -v VR
python2 build.py  -m mcp_vr -v VR $@

