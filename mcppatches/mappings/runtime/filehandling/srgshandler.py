# -*- coding: utf-8 -*-
"""
Created on Fri Apr  8 12:50:02 2011

@author: ProfMobius
@version : v0.1
"""


def parse_srg(srg_filename):
    """Reads a SeargeRG file and returns a dictionary of lists for packages, classes, methods and fields"""
    srg_types = {'PK:': ['obf_name', 'deobf_name'],
                 'CL:': ['obf_name', 'deobf_name'],
                 'FD:': ['obf_name', 'deobf_name'],
                 'MD:': ['obf_name', 'obf_desc', 'deobf_name', 'deobf_desc']}
    parsed_dict = {'PK': [],
                   'CL': [],
                   'FD': [],
                   'MD': []}

    def get_parsed_line(keyword, buf):
        return dict(zip(srg_types[keyword], [i.strip() for i in buf]))

    with open(srg_filename, 'r') as srg_file:
        for buf in srg_file:
            if 'tsrg2' in buf:
                return parse_tsrg2(srg_filename)
            buf = buf.strip()
            if buf == '' or buf[0] == '#':
                continue
            buf = buf.split()
            parsed_dict[buf[0][:2]].append(get_parsed_line(buf[0], buf[1:]))
    return parsed_dict

def parse_tsrg2(file):  
    srg_types = {'PK': ['obf_name', 'deobf_name'],
                 'CL': ['obf_name', 'deobf_name'],
                 'FD': ['obf_name', 'deobf_name'],
                 'MD': ['obf_name', 'obf_desc', 'deobf_name', 'deobf_desc']}
    parsed_dict = {'PK': [], 'CL': [], 'FD': [], 'MD': []}

    with open(file, 'r') as srg_file:
        cclass = ['','']
        for buf in srg_file:
            key = ''
            if 'tsrg' in buf:
                continue
            if buf.startswith('\t\t'):
                #params, ignore
                continue
            elif buf.startswith('\t'):
                if cclass[0] == '':
                    print 'invalid srg ' + buf + ' has no class '
                    continue
                #f or m
                buf = buf.split()
                if len(buf) == 3:
                    #fd
                    key = 'FD'
                    parsed_dict[key].append(dict(zip(srg_types[key],[cclass[0] + "/" + buf[0], cclass[1] + '/' + buf[1]])))
                elif len(buf) == 4:
                    #md
                    key = 'MD'
                    parsed_dict[key].append(dict(zip(srg_types[key],[cclass[0] + "/" + buf[0], buf[1], cclass[1] + '/' + buf[2], buf[1]])))
                else:
                    key = 'wut'
                    #what
            else:
                #class
                key = 'CL'
                buf = buf.split()
                cclass = [buf[0], buf[1]]
                parsed_dict[key].append(dict(zip(srg_types[key],[buf[0], buf[1]])))
    return parsed_dict
        
def writesrgs(filename, data):
    """Writes a srgs file based on data. Data is formatted similar to the output of readsrgs (dict of lists)"""
    if not 'PK' in data or not 'CL' in data or not 'FD' in data or not 'MD' in data:
        raise Exception("Malformed data for writesrgs. Keys should be in ['PK', 'CL', 'FD', 'MD']")

    with open(filename, 'w') as srgsout:
        # HINT: We write all the entries for a given key in order
        for key in ['PK', 'CL', 'FD', 'MD']:
            for entry in data[key]:
                srgsout.write('%s: %s %s\n' % (key, entry[0], entry[1]))
