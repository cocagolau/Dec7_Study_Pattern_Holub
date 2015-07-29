set -v
DOCS=docs
RM -r $DOCS
$JAVA_HOME/bin/javadoc.exe \
				-source 1.5 \
				-breakiterator \
				-classpath  /usr/bin/java \
				-sourcepath /usr/src\;/usr/src/apps \
				-taglet 	IncludeTag \
				-tagletpath c:\\usr\\bin\\java \
				-public \
				-d $DOCS \
				-link http://java.sun.com/j2se/1.5.0/docs/api \
					com.holub.database \
					com.holub.database.jdbc \
					com.holub.database.jdbc.adapters \
					/usr/src/com/holub/text/BeginToken.java \
					/usr/src/com/holub/text/ParseFailure.java \
					/usr/src/com/holub/text/RegexToken.java \
					/usr/src/com/holub/text/Scanner.java \
					/usr/src/com/holub/text/SimpleToken.java \
					/usr/src/com/holub/text/Token.java \
					/usr/src/com/holub/text/TokenSet.java \
					/usr/src/com/holub/text/WordToken.java \
					/usr/src/com/holub/tools/ArrayIterator.java \
					/usr/src/com/holub/tools/ThrowableContainer.java
