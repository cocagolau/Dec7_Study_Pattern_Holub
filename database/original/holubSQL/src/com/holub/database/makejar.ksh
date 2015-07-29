STARTDIR=`pwd`

JAVA_SRC=/usr/src/com/holub
JAVA_BIN=/usr/bin/java/com/holub

TARGET=$JAVA_SRC/database/HolubSQL.jar

BIN=$TMP/HolubSQL
SRC=$BIN/src

DIRS='com com/holub com/holub/database com/holub/text com/holub/tools'

mkdir $BIN
mkdir $SRC
for i in $DIRS
do
	mkdir $BIN/$i
	mkdir $SRC/$i
done

echo "Manifest-Version: 1.0" 						>  $TMP/manifest.mf
echo "Created-By: 1.5.0-rc (Sun Microsystems Inc.)" >> $TMP/manifest.mf
echo "Main-Class: com.holub.database.jdbc.Console"  >> $TMP/manifest.mf

echo "HolubSQL.jar"						>  $BIN/README.TXT
echo "Version 1.0"						>> $BIN/README.TXT
echo "Released 27 Sept., 2004"			>> $BIN/README.TXT
echo ""									>> $BIN/README.TXT
cat  /etc/license.comment.txt			>> $BIN/README.TXT

CP -vr * 								$SRC/com/holub/database
CP -vr $JAVA_BIN/database/* 			$BIN/com/holub/database

CP -v $JAVA_SRC/text/ParseFailure.java  $SRC/com/holub/text
CP -v $JAVA_BIN/text/ParseFailure*		$BIN/com/holub/text

CP -v $JAVA_SRC/text/*Token*  			$SRC/com/holub/text
CP -v $JAVA_BIN/text/*Token*			$BIN/com/holub/text

CP -v $JAVA_SRC/text/Scanner* 			$SRC/com/holub/text
CP -v $JAVA_BIN/text/Scanner*			$BIN/com/holub/text

CP -v $JAVA_SRC/tools/ThrowableContainer.java	$SRC/com/holub/tools
CP -v $JAVA_BIN/tools/ThrowableContainer*		$BIN/com/holub/tools

CP -v $JAVA_SRC/tools/ArrayIterator.java	$SRC/com/holub/tools
CP -v $JAVA_BIN/tools/ArrayIterator* 		$BIN/com/holub/tools

./makedoc.ksh
mv docs $BIN

find $SRC -name '*.java' -exec /usr/bin/addcopyright.ksh {} \;
find $SRC -name '_*' -exec RM -rv {} \;
RM -v  $SRC/com/holub/database/*jar

cd $BIN
jar cvfm $TARGET $TMP/manifest.mf .

cd $STARTDIR
RM -rv $BIN
RM -r $TMP/manifest.mf
