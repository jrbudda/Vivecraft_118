@echo off
python getchanges.py -m mcp_vr -v VR
python build.py  -m mcp_vr -v VR %*
rem python getchanges.py -m mcp_nonvr -v NONVR
rem python build.py  -m mcp_nonvr -v NONVR %*

