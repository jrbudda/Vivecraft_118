#!/bin/bash
# requires:
# coreutils https://www.gnu.org/software/coreutils/
# findutils https://www.gnu.org/software/findutils/
# sed https://www.gnu.org/software/sed/
if [ $# -lt 1 ]; then
	echo "No directory to update was provided in argument 1!"
	exit 1
fi
if [ -d "$1/jars/libraries" ] && [ -f "$1/eclipse/Client/.classpath" ]; then
	ECLIPSE_CLASSPATH="$1/eclipse/Client/.classpath"
else
	echo "Couldn't find $1/jars/libraries or $1/eclipse/Client/.classpath"
	exit 2
fi
IFS=$'\n'
get_libraries(){
	MC_VERSION=$(find -L "$1/lib" -maxdepth 1 -mindepth 1 -name "of" -prune -o \( -type d -print \) | sed "s/^$1\/lib\///")
	if [ "$MC_VERSION" == "" ]; then
		echo "Couldn't find a minecraft version linked at $1/lib/"
		exit 2
	fi
	echo \
	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"\
	"<classpath>\n"\
	"\t<classpathentry kind=\"src\" path=\"src\"/>\n"\
	"\t<classpathentry kind=\"con\"\n"\
	"\t\tpath=\"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/java-17-openjdk\"\n"\
	"\t>\t<attributes>\n"\
	"\t\t\t<attribute name=\"module\" value=\"true\"/>\n"\
	"\t\t</attributes>\n"\
	"\t</classpathentry>\n"\
	"\t<classpathentry kind=\"lib\" path=\"lib/$MC_VERSION\"/>\n"\
	"\t<classpathentry kind=\"lib\" path=\"jars/versions/$MC_VERSION/$MC_VERSION.jar\"/>\n"\
	"\t<classpathentry kind=\"lib\" path=\"jars/libraries\"/>"
	for library in $(
		find "$1/jars/libraries" \
		-type d -path "$1/jars/libraries/ca/weblite/java-objc-bridge/*" -prune -o \
		-type d -path "$1/jars/libraries/org/lwjgl/lwjgl/*" -prune -o \
		-type d -path "$1/jars/libraries/com/google/code/findbugs/*" -prune -o \
		-type f | sed "s/^$1\///"
		# find all libraries except select the directory of those which need natives and/or provide sources instead
	);	do
		local libloc="$1/$library"
		if [ -f "$libloc" ]; then
			echo "$library" 1>&2
			echo "\t<classpathentry kind=\"lib\" path=\"$library\"/>"
		elif [ -d "$libloc" ]; then
			echo "\t<classpathentry kind=\"lib\""
			for jar in $(
				find "$libloc" \
					-name "*-natives*.jar" -prune -o \( \
					-name "*-sources.jar" -prune -o \( \
					-type f -print \) \) \
				| sed "s/^$1\///"
			);	do
				echo "$jar" 1>&2
				echo "\t\tpath=\"$jar\""
			done
			for jar in $(
				find "$libloc" -name "*-sources.jar" -type f | sed "s/^$1\///"
			);	do
				echo "$jar" 1>&2
				echo "\t\tsourcepath=\"$jar\""
			done
			echo "\t/>"
			for jar in $(
				find "$libloc" -name "*-natives*.jar" -type f | sed "s/^$1\///"
			);	do
				echo "$jar" 1>&2
				echo \
				"\t<classpathentry kind=\"lib\" path=\"$jar\">\n"\
				"\t\t<attributes>\n"\
				"\t\t\t<attribute\n"\
				"\t\t\t\tname=\"org.eclipse.jdt.launching.CLASSPATH_ATTR_LIBRARY_PATH_ENTRY\"\n"\
				"\t\t\t\tvalue=\"lib/1.18.2/natives/linux/\"\n"\
				"\t\t\t/>\n"\
				"\t\t</attributes>\n"\
				"\t</classpathentry>"
			done
		fi
	done
	echo "\t<classpathentry kind=\"output\" path=\"bin\"/>"
	echo "</classpath>"
}
echo "INFO: Libraries Found:"
echo -e "$(get_libraries "$1")" > "$ECLIPSE_CLASSPATH"
exit 0