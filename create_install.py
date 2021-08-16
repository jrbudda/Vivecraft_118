#!/usr/bin/env python
from build import create_install
from install import mcp_version
from optparse import OptionParser
if __name__ == '__main__':
    parser = OptionParser()
    parser.add_option('-m', '--mcp-dir', action='store', dest='mcp_dir', help='Path to MCP to use', default=mcp_version)
    parser.add_option('-v', '--version', action='store', dest='version', help='VR or NONVR', default='VR')
    options, _ = parser.parse_args()
    create_install(options.mcp_dir, options.version)