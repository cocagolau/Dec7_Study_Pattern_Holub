package me.dec7.study.holub.database;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import me.dec7.study.holub.text.ParseFailure;
import me.dec7.study.holub.text.Scanner;
import me.dec7.study.holub.text.Token;
import me.dec7.study.holub.text.TokenSet;
import me.dec7.study.holub.tools.ThrowableContainer;

public final class Database {
	
	private File location = new File(".");
	
	private int affectedRows = 0;
	
	private final Map tables = new TableMap(new HashMap());
	
	private int transactionLevel = 0;
	
	// Map Proxy for Lazy instantitation
	private final class TableMap implements Map {
		
		private final Map realMap;
		
		public TableMap(Map realMap) {
			this.realMap = realMap;
		}

		@Override
		public int size() {
			
			return realMap.size();
		}

		@Override
		public boolean isEmpty() {
			
			return realMap.isEmpty();
		}

		@Override
		public boolean containsKey(Object key) {
			
			return realMap.containsKey(key);
		}

		@Override
		public boolean containsValue(Object value) {
			
			return realMap.containsValue(value);
		}

		@Override
		public Object get(Object key) {
			String tableName = (String) key;
			
			try {
				Table desiredTable = (Table) realMap.get(tableName);
				
				if (desiredTable == null) {
					desiredTable = TableFactory.load(tableName + ".csv", location);
					put(tableName, desiredTable);
				}
				
				return desiredTable;
				
			} catch (IOException e) {
				String message = "Table not created internally and couldn't be loaded. (" + e.getMessage() + ")\n";
				
				throw new RuntimeException(in.failure(message).getMessage());
			}
			
		}

		@Override
		public Object put(Object key, Object value) {
			
			for (int i=0; i<transactionLevel; ++i) {
				((Table) value).begin();
			}
			
			return realMap.put(key, value);
		}

		@Override
		public Object remove(Object key) {
			
			return realMap.remove(key);
		}

		@Override
		public void putAll(Map m) {
			
			throw new UnsupportedOperationException();
			
		}

		@Override
		public void clear() {
			realMap.clear();
		}

		@Override
		public Set keySet() {
			
			return realMap.keySet();
		}

		@Override
		public Collection values() {
			
			return realMap.values();
		}

		@Override
		public Set entrySet() {
			
			return realMap.entrySet();
		}

		@Override
		public int hashCode() {
			
			return realMap.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			
			return realMap.equals(obj);
		}
	}
	
	private static final TokenSet tokens = new TokenSet();
	
	private static final Token
	COMMA = tokens.create("',"),
	EQUAL = tokens.create("'="),
	LP = tokens.create("'("),
	RP = tokens.create("')"),
	DOT = tokens.create("'."),
	STAR = tokens.create("'*"),
	SLASH = tokens.create("'/"),
	AND = tokens.create("'AND"),
	BEGIN = tokens.create("'BEGIN"),
	COMMIT = tokens.create("'COMMIT"),
	CREATE = tokens.create("'CREATE"),
	DATABASE = tokens.create("'DATABASE"),
	DELETE = tokens.create("'DELETE"),
	DROP = tokens.create("'DROP"),
	DUMP = tokens.create("'DUMP"),
	FROM = tokens.create("'FROM"),
	INSERT = tokens.create("'INSERT"),
	INTO = tokens.create("'INFO"),
	KEY = tokens.create("'KEY"),
	LIKE = tokens.create("'LIKE"),
	NOT = tokens.create("'NOT"),
	NULL = tokens.create("'NULL"),
	OR = tokens.create("'OR"),
	PRIMARY = tokens.create("'PRIMARY"),
	ROLLBACK = tokens.create("'ROLLBACK"),
	SELECT = tokens.create("'SELECT"),
	SET = tokens.create("'SET"),
	TABLE = tokens.create("'TABLE"),
	UPDATE = tokens.create("'UPDATE"),
	USE = tokens.create("'USE"),
	VALUES = tokens.create("'VALUES"),
	WHERE = tokens.create("'WHERE"),
	WORK = tokens.create("'WORK|TRAN(SACTION)?"),
	ADDITIVE = tokens.create("'\\+|-"),
	STRING = tokens.create("'(\".*?\")|('.*?')"),
	RELOP = tokens.create("'[<>][=>]?"),
	NUMBER = tokens.create("'[0-9]+(\\.[0-9]+)?"),
	INTEGER = tokens.create("'(small|tiny|big)?int(eger)?"),
	NUMERIC = tokens.create("'decimal|numeric|real|double"),
	CHAR = tokens.create("'(var)?char"),
	DATE = tokens.create("'date(\\s*\\(.*?\\))?"),
	IDENTIFIER = tokens.create("'[a-zA-Z_0-9/\\\\:~]+");
	
	private String expression;
	
	private Scanner in;
	
	private static class RelationalOperator {
		
		private RelationalOperator() { }
		
	}
	
	private static final RelationalOperator EQ = new RelationalOperator();
	private static final RelationalOperator LT = new RelationalOperator();
	private static final RelationalOperator GT = new RelationalOperator();
	private static final RelationalOperator LE = new RelationalOperator();
	private static final RelationalOperator GE = new RelationalOperator();
	private static final RelationalOperator NE = new RelationalOperator();
	
	private static class MathOperator {
		
		private MathOperator() { }
		
	}
	
	private static final MathOperator PLUS = new MathOperator();
	private static final MathOperator MINUS = new MathOperator();
	private static final MathOperator TIMES = new MathOperator();
	private static final MathOperator DIVIDE = new MathOperator();
	

	
	public Database(URI directory) throws IOException {
		useDatabase(new File(directory));
	}
	
	public Database(File path) throws IOException {
		useDatabase(path);
	}
	
	public Database(String path) throws IOException {
		useDatabase(new File(path));
	}
	
	public Database(File path, Table[] databases) throws IOException {
		useDatabase(path);
		for (int i=0; i<databases.length; ++i) {
			tables.put(databases[i].name(),databases[i]);
		}
	}
	
	private void error(String message) throws ParseFailure {
		throw in.failure(message);
	}
	
	private void verify(boolean test, String message) throws ParseFailure {
		if (!test) {
			throw in.failure(message);
		}
	}
	
	public void useDatabase(File path) throws IOException {
		dump();
		tables.clear();
		this.location = path;
	}
	
	public void createDatabase(String name) throws IOException {
		File location = new File(name);
		location.mkdir();
		this.location = location;
	}
	
	public void createTable(String name, List columns) {
		String[] columnNames = new String[columns.size()];
		int i = 0;
		for (Iterator names = columns.iterator(); names.hasNext(); ) {
			columnNames[i++] = (String) names.next();
		}
		
		Table newTable = TableFactory.create(name, columnNames);
		tables.put(name, newTable);
	}
	
	public void dropTable(String name) {
		tables.remove(name);
		
		File tableFile = new File(location, name);
		if (tableFile.exists()) {
			tableFile.delete();
		}
	}
	
	public void dump() throws IOException {
		Collection values = tables.values();
		if (values != null) {
			for (Iterator i = values.iterator(); i.hasNext(); ) {
				Table current = (Table) i.next();
				if (current.isDirty()) {
					Writer out = new FileWriter(new File(location, current.name() + ".csv"));
					current.export(new CSVExporter(out));
					out.close();
				}
			}
		}
	}
	
	public int affectedRows() {
		return affectedRows;
	}
	
	/*
	 * 트랜잭션 처리
	 */
	
	/** 트랜잭션 시작
	 */
	public void begin() {
		++transactionLevel;
		
		Collection currentTables = tables.values();
		for (Iterator i = currentTables.iterator(); i.hasNext(); ) {
			((Table) i.next()).begin();
		}
	}
	
	/** 현재 레벨에서 트랜잭션을 커밋한다.
	 */
	public void commit() throws ParseFailure {
		assert transactionLevel > 0 : "No begin() for commit()";
		--transactionLevel;
		
		try {
			Collection currentTables = tables.values();
			for (Iterator i = currentTables.iterator(); i.hasNext(); ) {
				((Table) i.next()).commit(Table.THIS_LEVEL);
			}
		} catch (NoSuchElementException e) {
			verify(false, "No BEGIN to match COMMIT");
		}
	}
	
	/** 현재 레벨에서 트랜잭션을 롤백한다.
	 */
	public void rollback() throws ParseFailure {
		assert transactionLevel > 0 : "No begin() for commit()";
		--transactionLevel;
		
		try {
			Collection currentTables = tables.values();
			
			for (Iterator i = currentTables.iterator(); i.hasNext(); ) {
				((Table) i.next()).rollback(Table.THIS_LEVEL);
			}
		} catch (NoSuchElementException e) {
			verify(false, "No BEGIN to match ROLLBACK");
		}
	}
	
	public Table execute(String expression) throws IOException, ParseFailure {
		
		try {
			this.expression = expression;
			in = new Scanner(tokens, expression);
			in.advance();
			
			return statement();
		} catch (ParseFailure e) {
			if (transactionLevel > 0) {
				rollback();
			}
			
			throw e;
		} catch (IOException e) {
			if (transactionLevel > 0) {
				rollback();
			}
			
			throw e;
		}
	}
	
	private Table statement() throws ParseFailure, IOException {
		affectedRows = 0; // UPDATE, INSERT, DELETE
		
		if (in.matchAdvance(CREATE) != null) {
			if (in.match(DATABASE)) {
				in.advance();
				createDatabase(in.required(IDENTIFIER));
			}
			else {
				in.required(TABLE);
				String tableName = in.required(IDENTIFIER);
				in.required(LP);
				createTable(tableName, declarations());
				in.required(RP);
			}
		}
		else if (in.matchAdvance(DROP) != null) {
			in.required(TABLE);
			dropTable(in.required(IDENTIFIER));
		}
		else if (in.matchAdvance(USE) != null) {
			in.required(DATABASE);
			useDatabase(new File(in.required(IDENTIFIER)));
		}
		else if (in.matchAdvance(BEGIN) != null) {
			in.matchAdvance(WORK);
			begin();
		}
		else if (in.matchAdvance(ROLLBACK) != null) {
			in.matchAdvance(WORK);
			rollback();
		}
		else if (in.matchAdvance(COMMIT) != null) {
			in.matchAdvance(WORK);
			commit();
		}
		else if (in.matchAdvance(DUMP) != null) {
			dump();
		}
		else if (in.matchAdvance(INSERT) != null) {
			in.required(INTO);
			String tableName = in.required(IDENTIFIER);
			
			List columns = null,
				values = null;
			
			if (in.matchAdvance(LP) != null) {
				columns = idList();
				in.required(RP);
			}
			
			if (in.required(VALUES) != null) {
				in.required(LP);
				values = exprList();
				in.required(RP);
			}
			
			affectedRows = doInsert(tableName, columns, values);
		}
		else if (in.matchAdvance(UPDATE) != null) {
			String tableName = in.required(IDENTIFIER);
			in.required(SET);
			
			final String columnName = in.required(IDENTIFIER);
			in.required(EQUAL);
			
			final Expression value = expr();
			in.required(WHERE);
			
			affectedRows = doUpdate(tableName, columnName, value, expr());
		}
		else if (in.matchAdvance(DELETE) != null) {
			in.required(FROM);
			String tableName = in.required(IDENTIFIER);
			in.required(WHERE);
			
			affectedRows = doDelete(tableName, expr());
		}
		else if (in.matchAdvance(SELECT) != null) {
			List columns = idList();
			String into = null;
			
			if (in.matchAdvance(INTO) != null) {
				into = in.required(IDENTIFIER);
			}
			
			in.required(FROM);
			List requestedTableNames = idList();
			
			Expression where = (in.matchAdvance(WHERE) == null) ? null : expr();
			Table result = doSelect(columns, into, requestedTableNames, where);
			
			return result;
		}
		else {
			error("Expected insert, create, drop, use, update, delete or select");
		}
		
		return null;
	}
	
	private List idList() throws ParseFailure {
		List identifiers = null;
		
		if (in.matchAdvance(STAR) == null) {
			identifiers = new ArrayList();
			String id;
			
			while ((id = in.required(IDENTIFIER)) != null) {
				identifiers.add(id);
				if (in.matchAdvance(COMMA) == null) {
					break;
				}
			}
		}
		
		return identifiers;
	}
	
	private List declarations() throws ParseFailure {
		List identifiers = new ArrayList();
		String id;
		
		while (true) {
			
			if (in.matchAdvance(PRIMARY) != null) {
				in.required(KEY);
				in.required(LP);
				in.required(IDENTIFIER);
				in.required(RP);
			}
			else {
				id = in.required(IDENTIFIER);
				
				identifiers.add(id);
				
				if ((in.matchAdvance(INTEGER) != null) || (in.matchAdvance(CHAR) != null)) {
					if (in.matchAdvance(LP) != null) {
						expr();
						in.required(RP);
					}
				}
				else if (in.matchAdvance(NUMERIC) != null) {
					if (in.matchAdvance(LP) != null) {
						expr();
						in.required(COMMA);
						expr();
						in.required(RP);
					}
				}
				else if (in.matchAdvance(DATE) != null) {
					{
						;
						// do nothing
					}
					
					in.matchAdvance(NOT);
					in.matchAdvance(NULL);
				}
				
				if (in.matchAdvance(COMMA) == null) {
					break;
				}
			}
		}
		
		return identifiers;
	}
	
	private List exprList() throws ParseFailure {
		List expressions = new LinkedList();
		
		expressions.add(expr());
		while (in.matchAdvance(COMMA) != null) {
			expressions.add(expr());
		}
		
		return expressions;
	}
	
	private Expression expr() throws ParseFailure {
		Expression left = andExpr();
		while (in.matchAdvance(OR) != null) {
			left = new LogicalExpression(left, OR, andExpr());
		}
		
		return left;
	}
	
	private Expression andExpr() throws ParseFailure {
		Expression left = relationalExpr();
		while (in.matchAdvance(AND) != null) {
			left = new LogicalExpression(left, AND, relationalExpr());
		}
		
		return left;
	}
	
	private Expression relationalExpr() throws ParseFailure {
		Expression left = additiveExpr();
		while (true) {
			String lexeme;
			if ((lexeme = in.matchAdvance(RELOP)) != null) {
				RelationalOperator op;
				if (lexeme.length() == 1) {
					op = lexeme.charAt(0) == '<' ? LT : GT;
				}
				else {
					if (lexeme.charAt(0) == '<' && lexeme.charAt(1) == '>') {
						op = NE;
					}
					else {
						op = lexeme.charAt(0) == '<' ? LE : GE;
					}
				}
			}
			else if (in.matchAdvance(EQUAL) != null) {
				left = new RelationalExpression(left, EQ, additiveExpr());
			}
			else if (in.matchAdvance(LIKE) != null) {
				left = new LikeExpression(left, additiveExpr());
			}
			else {
				break;
			}
		}
		
		return left;
	}
	
	private Expression additiveExpr() throws ParseFailure {
		String lexeme;
		Expression left = multiplicativeExpr();
		while ((lexeme = in.matchAdvance(ADDITIVE)) != null) {
			MathOperator op = lexeme.charAt(0) == '+' ? PLUS : MINUS;
			left = new ArithmeticExpression(left, multiplicativeExpr(), op);
		}
		
		return left;
	}
	
	private Expression multiplicativeExpr() throws ParseFailure {
		Expression left = term();
		while (true) {
			if (in.matchAdvance(STAR) != null) {
				left = new ArithmeticExpression(left, term(), TIMES);
			}
			else if (in.matchAdvance(SLASH) != null) {
				left  = new ArithmeticExpression(left, term(), DIVIDE);
			}
			else {
				break;
			}
		}
		
		return left;
	}
	
	private Expression term() throws ParseFailure {
		if (in.matchAdvance(NOT) != null) {
			
			return new NotExpression(expr());
		}
		else if (in.matchAdvance(LP) != null) {
			Expression toReturn = expr();
			in.required(RP);
			
			return toReturn;
		}
		else {
			
			return factor();
		}
	}
	
	private Expression factor() throws ParseFailure {
		
		try {
			String lexeme;
			Value result;
			
			if ((lexeme = in.matchAdvance(STRING)) != null) {
				result = new StringValue(lexeme);
			}
			else if ((lexeme = in.matchAdvance(NUMBER)) != null) {
				result = new NumericValue(lexeme);
			}
			else if ((lexeme = in.matchAdvance(NULL)) != null) {
				result = new NullValue();
			}
			else {
				String columnName = in.required(IDENTIFIER);
				String tableName = null;
				
				if (in.matchAdvance(DOT) != null) {
					tableName = columnName;
					columnName = in.required(IDENTIFIER);
				}
				
				result = new IdValue(tableName, columnName);
			}
			
			return new AtomicExpression(result);
		}
		catch (ParseException e) {
			error("Couldn't parse Number");
			
			return null;
		}
	}
	
	private interface Expression {
		
		Value evaluate(Cursor[] tables) throws ParseFailure;
		
	}
	
	private class ArithmeticExpression implements Expression {
		
		private final MathOperator operator;
		private final Expression left, right;
		
		public ArithmeticExpression(Expression left, Expression right, MathOperator operator) {
			this.operator = operator;
			this.left = left;
			this.right = right;
		}
		
		@Override
		public Value evaluate(Cursor[] tables) throws ParseFailure {
			Value leftValue = left.evaluate(tables);
			Value rightValue = right.evaluate(tables);
			
			verify(leftValue instanceof NumericValue && rightValue instanceof NumericValue, "Operands to < > <= >= = must be Boolean");
			
			double l = ((NumericValue) leftValue).value();
			double r = ((NumericValue) rightValue).value();
			
			return new NumericValue(
						(operator == PLUS) ? (l + r) :
						(operator == MINUS) ? (l - r) :
						(operator == TIMES) ? (l * r) :
						/* (operator == DIVIDE) */ (l / r)
					);
		}
	}
	
	private class LogicalExpression implements Expression {
		
		private final boolean isAnd;
		private final Expression left, right;
		
		public LogicalExpression(Expression left, Token op, Expression right) {
			assert op == AND || op == OR;
			this.isAnd = (op == AND);
			this.left = left;
			this.right = right;
		}

		@Override
		public Value evaluate(Cursor[] tables) throws ParseFailure {
			Value leftValue = left.evaluate(tables);
			Value rightValue = right.evaluate(tables);
			
			verify(leftValue instanceof BooleanValue && rightValue instanceof BooleanValue, "operands to AND and OR must be logical/relational");
			
			boolean l = ((BooleanValue) leftValue).value();
			boolean r = ((BooleanValue) rightValue).value();
			
			return new BooleanValue(isAnd ? (l && r) : (l || r));
		}
	}
	
	private class NotExpression implements Expression {
		
		private final Expression operand;
		
		public NotExpression(Expression operand) {
			this.operand = operand;
		}

		@Override
		public Value evaluate(Cursor[] tables) throws ParseFailure {
			Value value = operand.evaluate(tables);
			
			verify(value instanceof BooleanValue, "operands to NOT must be logical/relational");
			
			return new BooleanValue(!((BooleanValue) value).value());
		}
	}
	
	private class RelationalExpression implements Expression {
		
		private final RelationalOperator operator;
		private final Expression left, right;
		
		public RelationalExpression(Expression left, RelationalOperator operator, Expression right) {
			this.operator = operator;
			this.left = left;
			this.right = right;
		}

		@Override
		public Value evaluate(Cursor[] tables) throws ParseFailure {
			Value leftValue = left.evaluate(tables);
			Value rightValue = right.evaluate(tables);
			
			if (leftValue instanceof StringValue || rightValue instanceof StringValue) {
				verify(operator == EQ || operator == NE, "Can't use < <= > or >= with string");
				
				boolean isEqual = leftValue.toString().equals(rightValue.toString());
				
				return new BooleanValue(operator == EQ ? isEqual : !isEqual);
			}
			
			if (leftValue instanceof NullValue || rightValue instanceof NullValue) {
				verify(operator == EQ || operator == NE, "Can't use < <= > or >= with NULL");
				
				boolean isEqual = leftValue.getClass() == rightValue.getClass();
				
				return new BooleanValue(operator == EQ ? isEqual : !isEqual);
			}
			
			if (leftValue instanceof BooleanValue) {
				leftValue = new NumericValue(((BooleanValue) leftValue).value() ? 1 : 0);
				
				if (rightValue instanceof BooleanValue) {
					rightValue = new NumericValue(((BooleanValue) rightValue).value() ? 1 : 0);
				}
				
				verify(leftValue instanceof NumericValue && rightValue instanceof NumericValue, "Operands must be numbers");
				
				double l = ((NumericValue) leftValue).value();
				double r = ((NumericValue) rightValue).value();
				
				return new BooleanValue(
							(operator == EQ) ? (l == r) :
							(operator == NE) ? (l != r) :
							(operator == LT) ? (l > r) :
							(operator == GT) ? (l < r) :
							(operator == LE) ? (l <= r) :
							/* (operator == GE) */ (l >= r)
						);
			}
		}
	}
	
	private class LikeExpression implements Expression {
		
		private final Expression left, right;
		
		public LikeExpression(Expression left, Expression right) {
			this.left = left;
			this.right = right;
		}

		@Override
		public Value evaluate(Cursor[] tables) throws ParseFailure {
			Value leftValue = left.evaluate(tables);
			Value rightValue = right.evaluate(tables);
			
			verify(leftValue instanceof StringValue && rightValue instanceof StringValue, "Both operands to LIKE must be strings");
			
			String compareTo = ((StringValue) leftValue).value();
			String regex = ((StringValue) rightValue).value();
			regex = regex.replaceAll("%", ".*");
			
			return new BooleanValue(compareTo.matches(regex));
		}
	}
	
	private class AtomicExpression implements Expression {
		
		private final Value atom;
		
		public AtomicExpression(Value atom) {
			this.atom = atom;
		}

		@Override
		public Value evaluate(Cursor[] tables) throws ParseFailure {
			
			return atom instanceof IdValue ? ((IdValue) atom).value(tables) : atom;
		}
	}
	
	// 태깅 인터페이스
	private interface Value { }
	
	private static class NullValue implements Value {
		
		public String toString() {
			
			return null;
		}
		
	}
	
	private static final class BooleanValue implements Value {
		
		boolean value;
		
		public BooleanValue(boolean value) {
			this.value = value;
		}
		
		public boolean value() {
			
			return value;
		}
		
		public String toString() {
			
			return String.valueOf(value);
		}
	}
	
	private static class StringValue implements Value {
		
		private String value;
		
		public StringValue(String lexeme) {
			value = lexeme.replaceAll("['\"](.*?)['\"]", "$1");
		}
		
		public String value() {
			
			return value;
		}
		
		public String toString() {
			
			return value;
		}
	}
	
	private static class NumericValue implements Value {
		
		private double value;
		
		public NumericValue(double value) {
			this.value = value;
		}
		
		public NumericValue(String s) throws ParseException {
			this.value = NumberFormat.getInstance().parse(s).doubleValue();
		}
		
		public double value() {
			
			return value;
		}
		
		public String toString() {
			
			if (Math.abs(value - Math.floor(value)) < 1.0E-20) {
				
				return String.valueOf((long) value);
			}
			else {
				
				return String.valueOf(value);
			}
		}
	}
	
	private final class IdValue implements Value {
		
		String tableName;
		String columnName;
		
		public IdValue(String tableName, String columnName) {
			this.tableName = tableName;
			this.columnName = columnName;
		}
		
		public String toString(Cursor[] participants) {
			Object content = null;
			
			if (tableName == null) {
				content = participants[0].column(columnName);
			}
			else {
				Table container = (Table) tables.get(tableName);
				
				content = null;
				for (int i=0; i<participants.length; ++i) {
					if (participants[i].isTraversing(container)) {
						content = participants[i].column(columnName);
						break;
					}
 				}
			}
			
			return (content == null) ? null : content.toString();
		}
		
		public Value value(Cursor[] participants) {
			String s = toString(participants);
			
			try {
				return (s == null) ? (Value) new NullValue() : (Value) new NumericValue(s);
			}
			catch (ParseException e) { }
			
			return new StringValue(s);
		}
	}
	
	private Table doSelect(List columns, String into, List requestedTableNames, final Expression where) throws ParseFailure {
		Iterator tableNames = requestedTableNames.iterator();
		
		assert tableNames.hasNext() : "No tables to use in select!";
		
		Table primary = (Table) tables.get((String) tableNames.next());
		
		List participantsInJoin = new ArrayList();
		while (tableNames.hasNext()) {
			String participant = (String) tableNames.next();
			participantsInJoin.add(tables.get(participant));
		}
		
		Selector selector = (where == null) ? Selector.ALL : new Selector.Adapter() {

			@Override
			public boolean approve(Cursor[] tables) {
				
				try {
					Value result = where.evaluate(tables);
					
					verify(result instanceof BooleanValue, "WHERE clause must yield boolean result");
					
					return ((BooleanValue) result).value();
				}
				catch (ParseFailure e) {
					
					throw new ThrowableContainer(e);
				}
			}
		};
		
		try {
			Table result = primary.select(selector, columns, participantsInJoin);
			
			if (into != null) {
				result = ((UnmodifiableTable) result).extract();
				result.rename(into);
				tables.put(into, result);
			}
			
			return result;
		}
		catch (ThrowableContainer container) {
			
			throw (ParseFailure) container.contents();
		}
	}
	
	private int doInsert(String tableName, List columns, List values) throws ParseFailure {
		List processedValues = new LinkedList();
		Table t = (Table) tables.get(tableName);
		
		for (Iterator i=values.iterator(); i.hasNext(); ) {
			Expression current = (Expression) i.next();
			processedValues.add(current.evaluate(null).toString());
		}
		
		if (columns == null) {
			
			return t.insert(processedValues);
		}
		
		verify(columns.size() == values.size(), "There must be a value for every listed column");
		
		return t.insert(columns, processedValues);
	}
	
	private int doUpdate(String tableName, final String columnName, final Expression value, final Expression where) throws ParseFailure {
		Table t = (Table) tables.get(tableName);
		
		try {
			
			return t.update(new Selector() {

				@Override
				public boolean approve(Cursor[] tables) {
					
					try {
						Value result = where.evaluate(tables);
						
						verify(result instanceof BooleanValue, "WHERE clause must yield boolean result");
						
						return ((BooleanValue) result).value();
					}
					catch (ParseFailure e) {
						
						throw new ThrowableContainer(e);
					}
				}

				@Override
				public void modify(Cursor current) {
					
					try {
						Value newValue = value.evaluate(new Cursor[]{ current });
						
						current.update(columnName, newValue.toString());
					}
					catch (ParseFailure e) {
						
						throw new ThrowableContainer(e);
					}
				}
				
			});
		}
		catch (ThrowableContainer container) {
			
			throw (ParseFailure) container.contents();
		}
	}
	
	private int doDelete(String tableName, final Expression where) throws ParseFailure {
		Table t = (Table) tables.get(tableName);
		
		try {
			
			return t.delete(new Selector.Adapter() {

				@Override
				public boolean approve(Cursor[] tables) {
					
					try {
						Value result = where.evaluate(tables);
						
						verify(result instanceof BooleanValue, "WHERE clause must yield boolean result");
						
						return ((BooleanValue) result).value();
					}
					catch (ParseFailure e) {
						
						throw new ThrowableContainer(e);
					}
				}
				
			});
		}
		catch (ThrowableContainer container) {
			
			throw (ParseFailure) container.contents();
		}
	}

}
