# This is a korn-shell script that tests the database code. You can run
# it under windows if you've installed the MKS toolkit (or the Cygwin
# UNIX tools, though I haven't tried that.)

function jtest {

	redirect="1>$TMP/stdout 2>$TMP/stderr"

	case `pwd` in
	${SRC}/com/holub*) 
		dir=$(pwd | sed 's/[a-zA-Z]:.*\/src\///' | sed 's/\//\./g')
		java $dir.$1\$Test $2 $3 $4 $5 $6 $redriect
		;;
	*)
		java $(pwd | sed "s/^.*\///" | sed "s/\//\./g").$1\$Test $2 $3 $4 $5 $6 $redirect
		;;
	esac
}

RM Dbase/address.csv
RM Dbase/name.csv
RM Dbase/existing_copy.csv

jtest ConcreteTable > $TMP/ConcreteTable.test.tmp

diff $TMP/ConcreteTable.test.tmp ConcreteTable.test.out
case $? in
(0)	print ConcreteTable PASSED
	;;
(1) print ConcreteTable FAIL
	;;
(*) print Unknown Diff failure
	;;
esac

jtest Database > $TMP/Database.test.tmp
diff $TMP/Database.test.tmp Database.test.out
case $? in
(0)	print Database PASSED
	;;
(1) print Database FAIL
	;;
(*) print Unknown Diff failure
	;;
esac

cat Dbase/address.csv Dbase/name.csv > $TMP/Databases.tmp
diff $TMP/Databases.tmp Database.testdatabases.out
case $? in
(0)	print Database Output PASSED
	;;
(1) print Database Output FAIL
	;;
(*) print Unknown Diff failure
	;;
esac

diff Dbase/existing_copy.csv Dbase/existing_copy.template
case $? in
(0)	print Database Copy PASSED
	;;
(1) print Database Copy FAIL
	;;
(*) print Unknown Diff failure
	;;
esac
