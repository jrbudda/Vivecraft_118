@echo off
python getchanges.py -m mcp_nonvr -v NONVR
python build.py  -m mcp_nonvr -v NONVR %*

