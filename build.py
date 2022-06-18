import os, os.path, sys, json, datetime, StringIO
import shutil, tempfile,zipfile, fnmatch, install
from xml.dom.minidom import parse
from optparse import OptionParser
import subprocess, shlex
from tempfile import mkstemp
from shutil import move
from os import remove, close
from install import download_deps, download_native, download_file, mkdir_p, replacelineinfile
from minecriftversion import *

try:
    WindowsError
except NameError:
    WindowsError = OSError

base_dir = os.path.dirname(os.path.abspath(__file__))

def cmdsplit(args):
    if os.sep == '\\':
        args = args.replace('\\', '\\\\')
    return shlex.split(args)

def zipmerge( target_file, source_file ):
    out_file, out_filename = tempfile.mkstemp()
    out = zipfile.ZipFile(out_filename,'a')
    target = zipfile.ZipFile( target_file, 'r')
    source = zipfile.ZipFile( source_file, 'r' )

    #source supersedes target
    source_files = set( source.namelist() )
    target_files = set( target.namelist() ) - source_files

    for file in source_files:
        out.writestr( file, source.open( file ).read() )

    for file in target_files:
        out.writestr( file, target.open( file ).read() )

    source.close()
    target.close()
    out.close()
    #os.remove( target_file )
    shutil.copy( out_filename, target_file )

def process_json(dir, installer_json_id, json_id, lib_id, version, mcversion, forgeversion, ofversion):
    time = datetime.datetime(1979,6,1).strftime("%Y-%m-%dT%H:%M:%S-05:00")
    with  open(os.path.join(dir,installer_json_id),"rb") as f:
        s=f.read()
        s=s.replace("$MCVERSION", mcversion)
        s=s.replace("$FORGEVERSION", forgeversion)
        s=s.replace("$OFVERSION", ofversion)
        s=s.replace("$VERSION", version)
        json_obj = json.loads(s)
        json_obj["id"] = json_id
        json_obj["time"] = time
        json_obj["releaseTime"] = time
        json_obj["libraries"].insert(0,{"name":lib_id, "MMC-hint":"local"}) #Insert at beginning
        #json_obj["libraries"].append({"name":"net.minecraft:Minecraft:"+mc_version}) #Insert at end
        return json.dumps( json_obj, indent=1 )

def parse_srg_classnames(srgfile):
    classnames = {}
    with open(srgfile) as f:
        for line in f:
            split = line.split(" ")
            if split[0] == "CL:":
                classnames["%s.class" % (split[1])] = split[2].strip()
    return classnames
    
def parse_tsrg_classnames(srgfile):
    classnames = {}
    with open(srgfile) as f:
        for line in f:
            split = line.split(" ")
            if not line.startswith('\t'):
                classnames["%s.class" % (split[0])] = split[1].strip()
    return classnames

def create_install(mcp_dir, vrversion = "VR"):
    print "Creating Installer..."
    srg = os.path.join(mcp_dir,'class','srg')
    obf = os.path.join(mcp_dir,'class','obf')
    resources = os.path.join(base_dir,"resources", vrversion)
    patches = os.path.join(base_dir,'patches', vrversion)
    
    #use the java that mcp uses for compiling the installer and invoking launch4j
    dir = os.path.abspath(mcp_dir)
    sys.path.append(dir)
    os.chdir(dir)
    from runtime.commands import Commands  
    commands = Commands(None, verify=True)
    print "java is " + commands.cmdjavac
    os.chdir("..")
    #
    vanilla = parse_tsrg_classnames(os.path.join(mcp_dir, "conf", "joined.tsrg"))
    
    in_mem_zip_obf = StringIO.StringIO()
    in_mem_zip_srg = StringIO.StringIO()
    in_mem_zip_res = StringIO.StringIO()
   
    print "Packing OBF..."
    with zipfile.ZipFile( in_mem_zip_obf,'w', zipfile.ZIP_DEFLATED) as zipout:
        for abs_path, _, filelist in os.walk(obf, followlinks=True):
            arc_path = os.path.relpath( abs_path, obf ).replace('\\','/').replace('.','') + '/'
            for cur_file in fnmatch.filter(filelist, '*.class'):
                #print arc_path + cur_file
                flg = False
                if not 'vivecraft' in (arc_path+cur_file).lower() and not 'jopenvr' in arc_path and not 'minecraftforge' in arc_path and not 'VR' in cur_file: #these misbehave when loaded in this jar, do some magic.
                    flg = True
                    ok = False
                    v = (arc_path + cur_file).replace('/','\\').split('$')[0].replace('.class', '')
                    cur_file_parent = cur_file.split('$')[0].replace('.class','') + '.class'
                    if cur_file_parent in vanilla:
                        v = vanilla[cur_file_parent].replace('/','\\')               
                    for patch_path, _, patchlist in os.walk(patches, followlinks=True):
                        for patch in fnmatch.filter(patchlist, '*.patch'):
                            p = patch_path + '\\' + patch
                            if v in p:
                                print 'Found ' + v + ' ' + p
                                ok = True
                                break
                    if not ok:
                        print "WARNING: Skipping unexpected file with no patch " + arc_path + cur_file_parent + ' (' + v + ')'
                        continue
                if "blaze3d" in arc_path:
                    flg = True
                in_file= os.path.join(abs_path,cur_file)
                arcname =  arc_path + cur_file
                if flg:
                    arcname =  arc_path.replace('/','.') + cur_file.replace('.class', '.clazz')
                zipout.write(in_file, arcname.strip('.'))
    
    print "Packing SRG..."
    with zipfile.ZipFile( in_mem_zip_srg,'w', zipfile.ZIP_DEFLATED) as zipout:
        metaroot = os.path.join(base_dir, "installer", "META-INF")
        for root, _, filelist in os.walk(metaroot, followlinks=True):
            for file in filelist:
                print root + " ************************************************* " + file
                filename = os.path.join(root, file)
                zipout.write(filename, "META-INF/" + os.path.relpath(filename,metaroot))

        for abs_path, _, filelist in os.walk(srg, followlinks=True):
            arc_path = os.path.relpath(abs_path, srg ).replace('\\','/').replace('.','') + '/'
            for cur_file in fnmatch.filter(filelist, '*.class'):
                if 'minecraftforge' in arc_path: continue
                #print arc_path + cur_file
                flg = False
                if not 'vivecraft' in (arc_path+cur_file).lower() and not 'jopenvr' in arc_path and not 'VR' in cur_file: #these misbehave when loaded in this jar, do some magic.
                    flg = True
                    ok = False
                    v = (arc_path + cur_file).replace('/','\\').split('$')[0].replace('.class', '')
                    cur_file_parent = cur_file.split('$')[0].replace('.class','') + '.class'
                    if cur_file_parent in vanilla:
                        v = vanilla[cur_file_parent].replace('/','\\')               
                    for patch_path, _, patchlist in os.walk(patches, followlinks=True):
                        for patch in fnmatch.filter(patchlist, '*.patch'):
                            p = patch_path + '\\' + patch
                            if v in p:
                                #print 'Found ' + v + ' ' + p
                                ok = True
                                break
                    if not ok:
                        print "WARNING: Skipping unexpected file with no patch " + arc_path + cur_file_parent + ' (' + v + ')'
                        continue
                if "blaze3d" in arc_path:
                    flg = True
                in_file= os.path.join(abs_path,cur_file)
                arcname = arc_path + cur_file
                if not "/tweaker" in arcname:
                    arcname = "vcsrg/" + arcname
                if flg:
                    arcname = arcname.replace('.class', '.clsrg')
                zipout.write(in_file, arcname.strip('.'))
                
    print "Packing Resources..."
    with zipfile.ZipFile( in_mem_zip_res,'w', zipfile.ZIP_DEFLATED) as zipout:      
        zipout.write(os.path.join(mcp_dir, "conf", "joined.srg"), "mappings/vivecraft/joined.srg")
        for a, b, c in os.walk(resources):
            print a
            arc_path = os.path.relpath(a,resources).replace('\\','/').replace('.','')+'/'
            for cur_file in c:
                print "Adding resource %s..." % cur_file
                in_file= os.path.join(a,cur_file) 
                arcname =  arc_path + cur_file
                zipout.write(in_file, arcname)
                
    os.chdir( base_dir )
    
    in_mem_zip_obf.seek(0)
    in_mem_zip_srg.seek(0)
    in_mem_zip_res.seek(0)
    if os.getenv("RELEASE_VERSION"):
        version = os.getenv("RELEASE_VERSION")
    elif os.getenv("BUILD_NUMBER"):
        version = "b"+os.getenv("BUILD_NUMBER")
    else:
        if vrversion == "VR":
            version = vr_build
        else:
            version = nonvr_build

    version = minecrift_version_num+"-"+version
    
    # Replace version info in installer.java
    print "Updating installer versions..."
    installer_java_file = os.path.join("installer","Installer.java")
    replacelineinfile( installer_java_file, "private static final String PROJECT_NAME",          "    private static final String PROJECT_NAME          = \"%s\";\n" % (project_name) );
    replacelineinfile( installer_java_file, "private static final String MINECRAFT_VERSION",     "    private static final String MINECRAFT_VERSION     = \"%s\";\n" % mc_version );
    replacelineinfile( installer_java_file, "private static final String MC_VERSION",            "    private static final String MC_VERSION            = \"%s\";\n" % minecrift_version_num );
    replacelineinfile( installer_java_file, "private static final String MC_MD5",                "    private static final String MC_MD5                = \"%s\";\n" % mc_file_md5 );
    replacelineinfile( installer_java_file, "private static final String OF_FILE_NAME",          "    private static final String OF_FILE_NAME          = \"%s\";\n" % of_file_name );
    replacelineinfile( installer_java_file, "private static final String OF_MD5",                "    private static final String OF_MD5                = \"%s\";\n" % of_file_md5 );
    replacelineinfile( installer_java_file, "private static final String OF_VERSION_EXT",        "    private static final String OF_VERSION_EXT        = \"%s\";\n" % of_file_extension );
    replacelineinfile( installer_java_file, "private static String FORGE_VERSION",               "    private static String FORGE_VERSION               = \"%s\";\n" % forge_version );
    replacelineinfile( installer_java_file, "private static final String HOMEPAGE_LINK",         "    private static final String HOMEPAGE_LINK         = \"%s\";\n" % homepage );
    replacelineinfile( installer_java_file, "private static final String DONATION_LINK",         "    private static final String DONATION_LINK         = \"%s\";\n" % donation );

    replacelineinfile( installer_java_file, "private static final boolean ALLOW_FORGE_INSTALL",  "    private static final boolean ALLOW_FORGE_INSTALL  = %s;\n" % str(allow_forge).lower() );
    replacelineinfile( installer_java_file, "private static final boolean DEFAULT_FORGE_INSTALL","    private static final boolean DEFAULT_FORGE_INSTALL= %s;\n" % str(forge_default).lower() );
    replacelineinfile( installer_java_file, "private static final boolean ALLOW_KATVR_INSTALL",  "    private static final boolean ALLOW_KATVR_INSTALL  = %s;\n" % str(allow_katvr).lower() );
    replacelineinfile( installer_java_file, "private static final boolean ALLOW_KIOSK_INSTALL",  "    private static final boolean ALLOW_KIOSK_INSTALL  = %s;\n" % str(allow_kiosk).lower() );
    replacelineinfile( installer_java_file, "private static final boolean ALLOW_HRTF_INSTALL",   "    private static final boolean ALLOW_HRTF_INSTALL   = %s;\n" % str(allow_hrtf).lower() );
    replacelineinfile( installer_java_file, "private static final boolean PROMPT_REMOVE_HRTF",   "    private static final boolean PROMPT_REMOVE_HRTF   = %s;\n" % str(allow_remove_hrtf).lower() );

    # Build installer.java
    print "Recompiling Installer.java..."
    subprocess.Popen( 
        cmdsplit(commands.cmdjavac + " -source 1.8 -target 1.8 \"%s\"" % os.path.join(base_dir,installer_java_file)), 
            cwd=os.path.join(base_dir,"installer"),
            bufsize=-1).communicate()
	
    artifact_id = "vivecraft-"+version
    installer_id = artifact_id + "-installer"  
    installer = os.path.join( installer_id+".jar" ) 
    shutil.copy( os.path.join("installer","installer.jar"), installer )
    with zipfile.ZipFile( installer,'a', zipfile.ZIP_DEFLATED) as install_out: #append to installer.jar
    
        # Add newly compiled class files
        for dirName, subdirList, fileList in os.walk("installer"):
            for afile in fileList:
                if os.path.isfile(os.path.join(dirName,afile)) and afile.endswith('.class'):
                    relpath = os.path.relpath(dirName, "installer")
                    print "Adding %s..." % os.path.join(relpath,afile)
                    install_out.write(os.path.join(dirName,afile), os.path.join(relpath,afile))

        # Add json files
        json_dir =  os.path.join(base_dir, "json", vrversion)
        
        install_out.writestr("version.json",                process_json(json_dir, "vivecraft.json",                "vivecraft-"+version+"",       "com.mtbs3d:minecrift:"+version,            version, minecrift_version_num,"",of_file_name + "_LIB"))
        install_out.writestr("version-forge.json",          process_json(json_dir, "vivecraft-forge.json",          "vivecraft-"+version+"-forge", "com.mtbs3d:minecrift:"+version+"-forge",   version, minecrift_version_num,forge_version,of_file_name + "_LIB"))
        install_out.writestr("version-multimc.json",        process_json(json_dir, "vivecraft-multimc.json",        "vivecraft-"+version+"",       "com.mtbs3d:minecrift:"+version,            version, minecrift_version_num,"",of_file_name + "_LIB"))
        install_out.writestr("version-multimc-forge.json",  process_json(json_dir, "vivecraft-multimc-forge.json",  "vivecraft-"+version+"-forge", "com.mtbs3d:minecrift:"+version+"-forge",   version, minecrift_version_num,"",of_file_name + "_LIB"))
              
        # Add version jar - this contains all the changed files (effectively minecrift.jar). A mix
        # of obfuscated and non-obfuscated files.
        install_out.writestr( "version.jar", in_mem_zip_obf.read() )
        install_out.writestr( "version-forge.jar", in_mem_zip_srg.read() )       
        install_out.writestr( "resources.jar", in_mem_zip_res.read() )      
        
        # Add the version info
        install_out.writestr( "version", artifact_id+":"+version )

    print("Creating Installer exe...")
    with open( os.path.join("installer","launch4j.xml"),"r" ) as inlaunch:
        with open( os.path.join("installer","launch4j","launch4j.xml"), "w" ) as outlaunch:
            outlaunch.write( inlaunch.read().replace("installer",installer_id))
    
    print("Invoking launch4j...")
    subprocess.Popen( 
        cmdsplit(commands.cmdjava + " -jar \"%s\" \"%s\""% (
                os.path.join( base_dir,"installer","launch4j","launch4j.jar"),
                os.path.join( base_dir,"installer","launch4j","launch4j.xml"))), 
            cwd=os.path.join(base_dir,"installer","launch4j"),
            bufsize=-1).communicate()
            
    os.unlink( os.path.join( base_dir,"installer","launch4j","launch4j.xml") )
  
def readpomversion(pomFile):
	if not os.path.exists(pomFile):
		return ''
		
	dom = parse(pomFile)
	project = dom.getElementsByTagName("project")[0]
	version = str(project.getElementsByTagName("version")[0].firstChild.nodeValue)
	return version
  
def main(mcp_dir, version = "VR"):
    print 'Using mcp dir: %s' % mcp_dir
    print 'Using base dir: %s' % base_dir
    
    print("Refreshing dependencies...")
    download_deps( mcp_dir, False )
    
    sys.path.append(mcp_dir)
    os.chdir(mcp_dir)

    reobf = os.path.join(mcp_dir,'reobf','minecraft')
    srg = os.path.join(mcp_dir,'class','srg')
    obf = os.path.join(mcp_dir,'class','obf')
    
    from runtime.commands import reallyrmtree

    reallyrmtree(reobf)
    reallyrmtree(srg)
    reallyrmtree(obf)
		       
    minecrift_build = vr_build if version == "VR" else nonvr_build 
    
    # Update Minecrift version
    minecraft_java_file = os.path.join(mcp_dir,'src','minecraft','net','minecraft','client','Minecraft.java')
    if os.path.exists(minecraft_java_file):
        print "Updating Minecraft.java with Vivecraft version: [Vivecraft %s %s %s] %s" % ( minecrift_version_num, "", minecrift_build, minecraft_java_file ) 
        replacelineinfile( minecraft_java_file, "public final String minecriftVerString",     "    public final String minecriftVerString = \"Vivecraft %s %s %s\";\n" % (minecrift_version_num, "", minecrift_build) );        

    print("Recompiling...")
    from runtime.mcp import recompile_side, reobfuscate_side
    from runtime.commands import Commands, CLIENT
    commands = Commands(None, verify=True)
    recompile_side( commands, CLIENT)

    print("Reobfuscating...")

    #commands.creatergcfg(reobf=True, keep_lvt=True, keep_generics=True, srg_names=False)
    reobfuscate_side( commands, CLIENT )
    
    try:   
        pass
        shutil.move(reobf, obf)
    except OSError:
        quit
            
    #commands.creatergcfg(reobf=True, keep_lvt=True, keep_generics=True, srg_names=True)
    reobfuscate_side( commands, CLIENT , srg_names=True)
    try:   
        pass
        shutil.move(reobf, srg)
    except OSError:
        quit

    create_install( mcp_dir, version)
    
if __name__ == '__main__':
    parser = OptionParser()
    parser.add_option('-m', '--mcp-dir', action='store', dest='mcp_dir', help='Path to MCP to use', default=None)
    parser.add_option('-o', '--no-optifine', dest='nomerge', default=False, action='store_true', help='If specified, no optifine merge will be carried out')
    parser.add_option('-v', '--version', action='store', dest='version', help='VR or NONVR', default='VR')

    options, _ = parser.parse_args()
  
    install.nomerge = options.nomerge

    if not options.mcp_dir is None:
        main(os.path.abspath(options.mcp_dir), options.version)
    elif os.path.isfile(os.path.join('..', 'runtime', 'commands.py')):
        main(os.path.abspath('..'), options.version)
    else:
        main(os.path.abspath(mcp_version), options.version)	
