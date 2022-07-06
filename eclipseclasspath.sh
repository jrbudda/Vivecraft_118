#!/bin/bash
# requires:
# coreutils https://www.gnu.org/software/coreutils/
# findutils https://www.gnu.org/software/findutils/
# sed https://www.gnu.org/software/sed/
if [ $# -lt 1 ]; then
	echo "No directory to update was provided in argument 1!"
	exit 0
fi
if [ -d "$1/jars/libraries" ] && [ -f "$1/eclipse/Client/.classpath" ]; then
	ECLIPSE_CLASSPATH="$1/eclipse/Client/.classpath"
else
	echo "Couldn't find $1/jars/libraries or $1/eclipse/Client/.classpath"
	echo "Skipping automatic eclipse classpath update."
	exit 0
fi
IFS=$'\n'
get_libraries(){
	cat <<-HDEOF
	<?xml version="1.0" encoding="UTF-8"?>
	<classpath>
	\t<classpathentry kind="src" path="src"/>
	\t<classpathentry kind="con"
	\t\tpath="org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-17"
	\t>\t<attributes>
	\t\t\t<attribute name="module" value="true"/>
	\t\t</attributes>
	\t</classpathentry>
	\t<classpathentry kind="lib" path="lib/1.18.2"/>
	\t<classpathentry kind="lib" path="jars/libraries"/>"
	HDEOF
	for library in $(
		find "$1/jars/libraries" \
		-type d -path "$1/jars/libraries/ca/weblite/java-objc-bridge/*" -prune -o \
		-type d -path "$1/jars/libraries/org/lwjgl/lwjgl/*" -prune -o \
		-type d -path "$1/jars/libraries/com/google/code/findbugs/*" -prune -o \
		-type f | sed "s/^$1\///"
		# find all libraries except select the directory of those which need natives and/or provide sources instead
	);	do
		local libloc="$1/$library"
		echo "$libloc" 1>&2
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
				echo "$(cat <<-HDEOF
				\t<classpathentry kind="lib" path="$jar">
				\t\t<attributes>
				\t\t\t<attribute
				\t\t\t\tname="org.eclipse.jdt.launching.CLASSPATH_ATTR_LIBRARY_PATH_ENTRY"
				\t\t\t\tvalue="lib/1.18.2/natives/linux/"
				\t\t\t/>
				\t\t</attributes>
				\t</classpathentry>
				HDEOF
				)"
			done
		fi
	done
	echo -e "\t<classpathentry kind=\"output\" path=\"bin\"/>"
	echo "</classpath>"
}
echo "INFO: Libraries Found:"
echo -e "$(get_libraries "$1")" > "$ECLIPSE_CLASSPATH"
# <classpathentry kind="lib" path="jars/libraries/org/lwjgl/lwjgl/3.2.2/lwjgl-3.2.2-natives-linux.jar">
# 	<attributes>
# 		<attribute
# 			name="org.eclipse.jdt.launching.CLASSPATH_ATTR_LIBRARY_PATH_ENTRY"
# 			value="lib/1.18.2/natives/linux/"
# 		/>
# 	</attributes>
# </classpathentry>
# <classpathentry kind="lib" path="jars/libraries/org/lwjgl/lwjgl/3.2.2/lwjgl-3.2.2.jar">
# 	<attributes>
# 		<attribute
# 			name="org.eclipse.jdt.launching.CLASSPATH_ATTR_LIBRARY_PATH_ENTRY"
# 			value="lib/1.18.2/natives/linux/"
# 		/>
# 	</attributes>
# </classpathentry>
# <classpathentry kind="lib"
# 	path="jars/libraries/ca/weblite/java-objc-bridge/1.0.0/java-objc-bridge-1.0.0.jar"
# 	sourcepath="jars/libraries/ca/weblite/java-objc-bridge/1.0.0/java-objc-bridge-1.0.0-sources.jar"
# />
# <classpathentry kind="lib"
# 	path="jars/libraries/com/google/code/findbugs/jsr305/3.0.1/jsr305-3.0.1.jar"
# 	sourcepath="jars/libraries/com/google/code/findbugs/jsr305/3.0.1/jsr305-3.0.1-sources.jar"
# />
# 	<classpathentry kind="output" path="bin"/>
# </classpath>