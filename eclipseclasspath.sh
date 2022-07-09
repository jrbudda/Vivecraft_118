#!/bin/bash
# requires:
# coreutils https://www.gnu.org/software/coreutils/
# findutils https://www.gnu.org/software/findutils/
# sed https://www.gnu.org/software/sed/
if [ $# -lt 1 ]; then
	echo "No directory to update was provided in argument 1!"
	exit 1
fi
ECLIPSE_CLASSPATH="$1/eclipse/Client/.classpath"
ECLIPSE_COREPREFS="$1/eclipse/Client/.settings/org.eclipse.jdt.core.prefs"
ECLIPSE_LIBRARIES="$1/jars/libraries"
if [ -d "$ECLIPSE_LIBRARIES" ] && [ -f "$ECLIPSE_CLASSPATH" ] && [ -f "$ECLIPSE_COREPREFS" ]; then
	MC_VERSION=$(find -L "$1/jars/versions/" -maxdepth 1 -mindepth 1 -name "of" -prune -o \( -type d -print \) | sed "s/^$1\/jars\/versions\///")
	if [ "$MC_VERSION" == "" ]; then
		echo "Couldn't find a minecraft version linked at $1/jars/versions/"
		exit 2
	fi
	echo "Automatically Updating Eclipse .classpath and compiler settings."
else
	echo "Couldn't find $ECLIPSE_LIBRARIES or $ECLIPSE_CLASSPATH or $ECLIPSE_COREPREFS"
	exit 2
fi
IFS=$'\n'
get_libraries(){
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
		find "$ECLIPSE_LIBRARIES" \
		-type d -path "$ECLIPSE_LIBRARIES/ca/weblite/java-objc-bridge/*" -prune -o \
		-type d -path "$ECLIPSE_LIBRARIES/org/lwjgl/lwjgl/*" -prune -o \
		-type d -path "$ECLIPSE_LIBRARIES/com/google/code/findbugs/*" -prune -o \
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
update_prefs(){
	cat <<-HDEOF
	eclipse.preferences.version=1
	org.eclipse.jdt.core.compiler.codegen.inlineJsrBytecode=enabled
	org.eclipse.jdt.core.compiler.codegen.methodParameters=do not generate
	org.eclipse.jdt.core.compiler.codegen.targetPlatform=17
	org.eclipse.jdt.core.compiler.codegen.unusedLocal=preserve
	org.eclipse.jdt.core.compiler.compliance=17
	org.eclipse.jdt.core.compiler.debug.lineNumber=generate
	org.eclipse.jdt.core.compiler.debug.localVariable=generate
	org.eclipse.jdt.core.compiler.debug.sourceFile=generate
	org.eclipse.jdt.core.compiler.problem.assertIdentifier=error
	org.eclipse.jdt.core.compiler.problem.enablePreviewFeatures=disabled
	org.eclipse.jdt.core.compiler.problem.enumIdentifier=error
	org.eclipse.jdt.core.compiler.problem.reportPreviewFeatures=warning
	org.eclipse.jdt.core.compiler.release=disabled
	org.eclipse.jdt.core.compiler.source=17
	HDEOF
}
echo "INFO: Libraries Found:"
echo -e "$(get_libraries "$1")" > "$ECLIPSE_CLASSPATH"
echo "$(update_prefs)" > "$ECLIPSE_COREPREFS"
exit 0
