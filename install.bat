@echo off
python install.py -m mcp_vr %*
xcopy /s /y /q ".\mcp_vr\" ".\mcp_nonvr\"
python applychanges.py -m mcp_nonvr -v NONVR

