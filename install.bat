@echo off
python install.py -m mcp_vr %*
rem xcopy /s /y /q ".\mcp_vr\" ".\mcp_nonvr\"
rem python applychanges.py -m mcp_nonvr -v NONVR

