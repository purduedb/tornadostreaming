package sqliteparser;

// Generated from SQLite.g4 by ANTLR 4.6
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SQLiteParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, SCOL=2, DOT=3, OPEN_PAR=4, CLOSE_PAR=5, COMMA=6, ASSIGN=7, STAR=8, 
		PLUS=9, MINUS=10, TILDE=11, PIPE2=12, DIV=13, MOD=14, LT2=15, GT2=16, 
		AMP=17, PIPE=18, LT=19, LT_EQ=20, GT=21, GT_EQ=22, EQ=23, NOT_EQ1=24, 
		NOT_EQ2=25, K_ABORT=26, K_ACTION=27, K_ADD=28, K_AFTER=29, K_ALL=30, K_ALTER=31, 
		K_ANALYZE=32, K_AND=33, K_AS=34, K_ASC=35, K_ATTACH=36, K_AUTOINCREMENT=37, 
		K_BEFORE=38, K_BEGIN=39, K_BETWEEN=40, K_BY=41, K_CASCADE=42, K_CASE=43, 
		K_CAST=44, K_CHECK=45, K_COLLATE=46, K_COLUMN=47, K_COMMIT=48, K_CONFLICT=49, 
		K_CONSTRAINT=50, K_CREATE=51, K_CROSS=52, K_CURRENT_DATE=53, K_CURRENT_TIME=54, 
		K_CURRENT_TIMESTAMP=55, K_DATABASE=56, K_DEFAULT=57, K_DEFERRABLE=58, 
		K_DEFERRED=59, K_DELETE=60, K_DESC=61, K_DETACH=62, K_DISTINCT=63, K_DROP=64, 
		K_EACH=65, K_ELSE=66, K_END=67, K_ESCAPE=68, K_EXCEPT=69, K_EXCLUSIVE=70, 
		K_EXISTS=71, K_EXPLAIN=72, K_FAIL=73, K_FOR=74, K_FOREIGN=75, K_FROM=76, 
		K_FULL=77, K_GLOB=78, K_GROUP=79, K_HAVING=80, K_IF=81, K_IGNORE=82, K_IMMEDIATE=83, 
		K_IN=84, K_INDEX=85, K_INDEXED=86, K_INITIALLY=87, K_INNER=88, K_INSERT=89, 
		K_INSTEAD=90, K_INTERSECT=91, K_INTO=92, K_IS=93, K_ISNULL=94, K_JOIN=95, 
		K_KEY=96, K_LEFT=97, K_LIKE=98, K_LIMIT=99, K_MATCH=100, K_NATURAL=101, 
		K_NO=102, K_NOT=103, K_NOTNULL=104, K_NULL=105, K_OF=106, K_OFFSET=107, 
		K_ON=108, K_OR=109, K_ORDER=110, K_OUTER=111, K_PLAN=112, K_PRAGMA=113, 
		K_PRIMARY=114, K_QUERY=115, K_RAISE=116, K_RECURSIVE=117, K_REFERENCES=118, 
		K_REGEXP=119, K_REINDEX=120, K_RELEASE=121, K_RENAME=122, K_REPLACE=123, 
		K_RESTRICT=124, K_RIGHT=125, K_ROLLBACK=126, K_ROW=127, K_SAVEPOINT=128, 
		K_SELECT=129, K_SET=130, K_TABLE=131, K_TEMP=132, K_TEMPORARY=133, K_THEN=134, 
		K_TO=135, K_TRANSACTION=136, K_TRIGGER=137, K_UNION=138, K_UNIQUE=139, 
		K_UPDATE=140, K_USING=141, K_VACUUM=142, K_VALUES=143, K_VIEW=144, K_VIRTUAL=145, 
		K_WHEN=146, K_WHERE=147, K_WITH=148, K_WITHOUT=149, K_PARTITION=150, K_SKIP=151, 
		K_OVER=152, K_COLLECTIVE=153, K_WITHIN=154, K_DISTANCE=155, K_CONTAINS=156, 
		K_COLLECT=157, K_OVERLAP=158, IDENTIFIER=159, NUMERIC_LITERAL=160, BIND_PARAMETER=161, 
		STRING_LITERAL=162, BLOB_LITERAL=163, SINGLE_LINE_COMMENT=164, MULTILINE_COMMENT=165, 
		SPACES=166, UNEXPECTED_CHAR=167;
	public static final int
		RULE_parse = 0, RULE_error = 1, RULE_sql_stmt_list = 2, RULE_sql_stmt = 3, 
		RULE_alter_table_stmt = 4, RULE_analyze_stmt = 5, RULE_attach_stmt = 6, 
		RULE_begin_stmt = 7, RULE_commit_stmt = 8, RULE_compound_select_stmt = 9, 
		RULE_create_index_stmt = 10, RULE_create_table_stmt = 11, RULE_create_trigger_stmt = 12, 
		RULE_create_view_stmt = 13, RULE_create_virtual_table_stmt = 14, RULE_delete_stmt = 15, 
		RULE_delete_stmt_limited = 16, RULE_detach_stmt = 17, RULE_drop_index_stmt = 18, 
		RULE_drop_table_stmt = 19, RULE_drop_trigger_stmt = 20, RULE_drop_view_stmt = 21, 
		RULE_factored_select_stmt = 22, RULE_insert_stmt = 23, RULE_pragma_stmt = 24, 
		RULE_reindex_stmt = 25, RULE_release_stmt = 26, RULE_rollback_stmt = 27, 
		RULE_savepoint_stmt = 28, RULE_simple_select_stmt = 29, RULE_select_stmt = 30, 
		RULE_select_or_values = 31, RULE_partition_term = 32, RULE_update_stmt = 33, 
		RULE_update_stmt_limited = 34, RULE_vacuum_stmt = 35, RULE_column_def = 36, 
		RULE_type_name = 37, RULE_column_constraint = 38, RULE_conflict_clause = 39, 
		RULE_limit = 40, RULE_contain = 41, RULE_skip = 42, RULE_file_name = 43, 
		RULE_read_name = 44, RULE_expr = 45, RULE_foreign_key_clause = 46, RULE_raise_function = 47, 
		RULE_indexed_column = 48, RULE_table_constraint = 49, RULE_with_clause = 50, 
		RULE_qualified_table_name = 51, RULE_ordering_term = 52, RULE_pragma_value = 53, 
		RULE_common_table_expression = 54, RULE_result_column = 55, RULE_table_or_subquery = 56, 
		RULE_join_clause = 57, RULE_join_operator = 58, RULE_join_constraint = 59, 
		RULE_select_core = 60, RULE_compound_operator = 61, RULE_signed_number = 62, 
		RULE_literal_value = 63, RULE_unary_operator = 64, RULE_error_message = 65, 
		RULE_module_argument = 66, RULE_column_alias = 67, RULE_keyword = 68, 
		RULE_name = 69, RULE_function_name = 70, RULE_database_name = 71, RULE_schema_name = 72, 
		RULE_table_function_name = 73, RULE_table_name = 74, RULE_table_or_index_name = 75, 
		RULE_new_table_name = 76, RULE_column_name = 77, RULE_collation_name = 78, 
		RULE_foreign_table = 79, RULE_index_name = 80, RULE_trigger_name = 81, 
		RULE_view_name = 82, RULE_module_name = 83, RULE_pragma_name = 84, RULE_savepoint_name = 85, 
		RULE_table_alias = 86, RULE_transaction_name = 87, RULE_file_read = 88, 
		RULE_inner_loop = 89, RULE_any_name = 90, RULE_file_splitter = 91;
	public static final String[] ruleNames = {
		"parse", "error", "sql_stmt_list", "sql_stmt", "alter_table_stmt", "analyze_stmt", 
		"attach_stmt", "begin_stmt", "commit_stmt", "compound_select_stmt", "create_index_stmt", 
		"create_table_stmt", "create_trigger_stmt", "create_view_stmt", "create_virtual_table_stmt", 
		"delete_stmt", "delete_stmt_limited", "detach_stmt", "drop_index_stmt", 
		"drop_table_stmt", "drop_trigger_stmt", "drop_view_stmt", "factored_select_stmt", 
		"insert_stmt", "pragma_stmt", "reindex_stmt", "release_stmt", "rollback_stmt", 
		"savepoint_stmt", "simple_select_stmt", "select_stmt", "select_or_values", 
		"partition_term", "update_stmt", "update_stmt_limited", "vacuum_stmt", 
		"column_def", "type_name", "column_constraint", "conflict_clause", "limit", 
		"contain", "skip", "file_name", "read_name", "expr", "foreign_key_clause", 
		"raise_function", "indexed_column", "table_constraint", "with_clause", 
		"qualified_table_name", "ordering_term", "pragma_value", "common_table_expression", 
		"result_column", "table_or_subquery", "join_clause", "join_operator", 
		"join_constraint", "select_core", "compound_operator", "signed_number", 
		"literal_value", "unary_operator", "error_message", "module_argument", 
		"column_alias", "keyword", "name", "function_name", "database_name", "schema_name", 
		"table_function_name", "table_name", "table_or_index_name", "new_table_name", 
		"column_name", "collation_name", "foreign_table", "index_name", "trigger_name", 
		"view_name", "module_name", "pragma_name", "savepoint_name", "table_alias", 
		"transaction_name", "file_read", "inner_loop", "any_name", "file_splitter"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'\"'", "';'", "'.'", "'('", "')'", "','", "'='", "'*'", "'+'", 
		"'-'", "'~'", "'||'", "'/'", "'%'", "'<<'", "'>>'", "'&'", "'|'", "'<'", 
		"'<='", "'>'", "'>='", "'=='", "'!='", "'<>'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, "SCOL", "DOT", "OPEN_PAR", "CLOSE_PAR", "COMMA", "ASSIGN", 
		"STAR", "PLUS", "MINUS", "TILDE", "PIPE2", "DIV", "MOD", "LT2", "GT2", 
		"AMP", "PIPE", "LT", "LT_EQ", "GT", "GT_EQ", "EQ", "NOT_EQ1", "NOT_EQ2", 
		"K_ABORT", "K_ACTION", "K_ADD", "K_AFTER", "K_ALL", "K_ALTER", "K_ANALYZE", 
		"K_AND", "K_AS", "K_ASC", "K_ATTACH", "K_AUTOINCREMENT", "K_BEFORE", "K_BEGIN", 
		"K_BETWEEN", "K_BY", "K_CASCADE", "K_CASE", "K_CAST", "K_CHECK", "K_COLLATE", 
		"K_COLUMN", "K_COMMIT", "K_CONFLICT", "K_CONSTRAINT", "K_CREATE", "K_CROSS", 
		"K_CURRENT_DATE", "K_CURRENT_TIME", "K_CURRENT_TIMESTAMP", "K_DATABASE", 
		"K_DEFAULT", "K_DEFERRABLE", "K_DEFERRED", "K_DELETE", "K_DESC", "K_DETACH", 
		"K_DISTINCT", "K_DROP", "K_EACH", "K_ELSE", "K_END", "K_ESCAPE", "K_EXCEPT", 
		"K_EXCLUSIVE", "K_EXISTS", "K_EXPLAIN", "K_FAIL", "K_FOR", "K_FOREIGN", 
		"K_FROM", "K_FULL", "K_GLOB", "K_GROUP", "K_HAVING", "K_IF", "K_IGNORE", 
		"K_IMMEDIATE", "K_IN", "K_INDEX", "K_INDEXED", "K_INITIALLY", "K_INNER", 
		"K_INSERT", "K_INSTEAD", "K_INTERSECT", "K_INTO", "K_IS", "K_ISNULL", 
		"K_JOIN", "K_KEY", "K_LEFT", "K_LIKE", "K_LIMIT", "K_MATCH", "K_NATURAL", 
		"K_NO", "K_NOT", "K_NOTNULL", "K_NULL", "K_OF", "K_OFFSET", "K_ON", "K_OR", 
		"K_ORDER", "K_OUTER", "K_PLAN", "K_PRAGMA", "K_PRIMARY", "K_QUERY", "K_RAISE", 
		"K_RECURSIVE", "K_REFERENCES", "K_REGEXP", "K_REINDEX", "K_RELEASE", "K_RENAME", 
		"K_REPLACE", "K_RESTRICT", "K_RIGHT", "K_ROLLBACK", "K_ROW", "K_SAVEPOINT", 
		"K_SELECT", "K_SET", "K_TABLE", "K_TEMP", "K_TEMPORARY", "K_THEN", "K_TO", 
		"K_TRANSACTION", "K_TRIGGER", "K_UNION", "K_UNIQUE", "K_UPDATE", "K_USING", 
		"K_VACUUM", "K_VALUES", "K_VIEW", "K_VIRTUAL", "K_WHEN", "K_WHERE", "K_WITH", 
		"K_WITHOUT", "K_PARTITION", "K_SKIP", "K_OVER", "K_COLLECTIVE", "K_WITHIN", 
		"K_DISTANCE", "K_CONTAINS", "K_COLLECT", "K_OVERLAP", "IDENTIFIER", "NUMERIC_LITERAL", 
		"BIND_PARAMETER", "STRING_LITERAL", "BLOB_LITERAL", "SINGLE_LINE_COMMENT", 
		"MULTILINE_COMMENT", "SPACES", "UNEXPECTED_CHAR"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "SQLite.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SQLiteParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ParseContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SQLiteParser.EOF, 0); }
		public List<Sql_stmt_listContext> sql_stmt_list() {
			return getRuleContexts(Sql_stmt_listContext.class);
		}
		public Sql_stmt_listContext sql_stmt_list(int i) {
			return getRuleContext(Sql_stmt_listContext.class,i);
		}
		public List<ErrorContext> error() {
			return getRuleContexts(ErrorContext.class);
		}
		public ErrorContext error(int i) {
			return getRuleContext(ErrorContext.class,i);
		}
		public ParseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterParse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitParse(this);
		}
	}

	public final ParseContext parse() throws RecognitionException {
		ParseContext _localctx = new ParseContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_parse);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(188);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << SCOL) | (1L << K_ALTER) | (1L << K_ANALYZE) | (1L << K_ATTACH) | (1L << K_BEGIN) | (1L << K_COMMIT) | (1L << K_CREATE) | (1L << K_DELETE) | (1L << K_DETACH))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (K_DROP - 64)) | (1L << (K_END - 64)) | (1L << (K_EXPLAIN - 64)) | (1L << (K_INSERT - 64)) | (1L << (K_PRAGMA - 64)) | (1L << (K_REINDEX - 64)) | (1L << (K_RELEASE - 64)) | (1L << (K_REPLACE - 64)) | (1L << (K_ROLLBACK - 64)))) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & ((1L << (K_SAVEPOINT - 128)) | (1L << (K_SELECT - 128)) | (1L << (K_UPDATE - 128)) | (1L << (K_VACUUM - 128)) | (1L << (K_VALUES - 128)) | (1L << (K_WITH - 128)) | (1L << (UNEXPECTED_CHAR - 128)))) != 0)) {
				{
				setState(186);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case SCOL:
				case K_ALTER:
				case K_ANALYZE:
				case K_ATTACH:
				case K_BEGIN:
				case K_COMMIT:
				case K_CREATE:
				case K_DELETE:
				case K_DETACH:
				case K_DROP:
				case K_END:
				case K_EXPLAIN:
				case K_INSERT:
				case K_PRAGMA:
				case K_REINDEX:
				case K_RELEASE:
				case K_REPLACE:
				case K_ROLLBACK:
				case K_SAVEPOINT:
				case K_SELECT:
				case K_UPDATE:
				case K_VACUUM:
				case K_VALUES:
				case K_WITH:
					{
					setState(184);
					sql_stmt_list();
					}
					break;
				case UNEXPECTED_CHAR:
					{
					setState(185);
					error();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(190);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(191);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ErrorContext extends ParserRuleContext {
		public Token UNEXPECTED_CHAR;
		public TerminalNode UNEXPECTED_CHAR() { return getToken(SQLiteParser.UNEXPECTED_CHAR, 0); }
		public ErrorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_error; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterError(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitError(this);
		}
	}

	public final ErrorContext error() throws RecognitionException {
		ErrorContext _localctx = new ErrorContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_error);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(193);
			((ErrorContext)_localctx).UNEXPECTED_CHAR = match(UNEXPECTED_CHAR);
			 
			     throw new RuntimeException("UNEXPECTED_CHAR=" + (((ErrorContext)_localctx).UNEXPECTED_CHAR!=null?((ErrorContext)_localctx).UNEXPECTED_CHAR.getText():null)); 
			   
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Sql_stmt_listContext extends ParserRuleContext {
		public List<Sql_stmtContext> sql_stmt() {
			return getRuleContexts(Sql_stmtContext.class);
		}
		public Sql_stmtContext sql_stmt(int i) {
			return getRuleContext(Sql_stmtContext.class,i);
		}
		public Sql_stmt_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sql_stmt_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterSql_stmt_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitSql_stmt_list(this);
		}
	}

	public final Sql_stmt_listContext sql_stmt_list() throws RecognitionException {
		Sql_stmt_listContext _localctx = new Sql_stmt_listContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_sql_stmt_list);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SCOL) {
				{
				{
				setState(196);
				match(SCOL);
				}
				}
				setState(201);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(202);
			sql_stmt();
			setState(211);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(204); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(203);
						match(SCOL);
						}
						}
						setState(206); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==SCOL );
					setState(208);
					sql_stmt();
					}
					} 
				}
				setState(213);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			setState(217);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(214);
					match(SCOL);
					}
					} 
				}
				setState(219);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Sql_stmtContext extends ParserRuleContext {
		public Alter_table_stmtContext alter_table_stmt() {
			return getRuleContext(Alter_table_stmtContext.class,0);
		}
		public Analyze_stmtContext analyze_stmt() {
			return getRuleContext(Analyze_stmtContext.class,0);
		}
		public Attach_stmtContext attach_stmt() {
			return getRuleContext(Attach_stmtContext.class,0);
		}
		public Begin_stmtContext begin_stmt() {
			return getRuleContext(Begin_stmtContext.class,0);
		}
		public Commit_stmtContext commit_stmt() {
			return getRuleContext(Commit_stmtContext.class,0);
		}
		public Compound_select_stmtContext compound_select_stmt() {
			return getRuleContext(Compound_select_stmtContext.class,0);
		}
		public Create_index_stmtContext create_index_stmt() {
			return getRuleContext(Create_index_stmtContext.class,0);
		}
		public Create_table_stmtContext create_table_stmt() {
			return getRuleContext(Create_table_stmtContext.class,0);
		}
		public Create_trigger_stmtContext create_trigger_stmt() {
			return getRuleContext(Create_trigger_stmtContext.class,0);
		}
		public Create_view_stmtContext create_view_stmt() {
			return getRuleContext(Create_view_stmtContext.class,0);
		}
		public Create_virtual_table_stmtContext create_virtual_table_stmt() {
			return getRuleContext(Create_virtual_table_stmtContext.class,0);
		}
		public Delete_stmtContext delete_stmt() {
			return getRuleContext(Delete_stmtContext.class,0);
		}
		public Delete_stmt_limitedContext delete_stmt_limited() {
			return getRuleContext(Delete_stmt_limitedContext.class,0);
		}
		public Detach_stmtContext detach_stmt() {
			return getRuleContext(Detach_stmtContext.class,0);
		}
		public Drop_index_stmtContext drop_index_stmt() {
			return getRuleContext(Drop_index_stmtContext.class,0);
		}
		public Drop_table_stmtContext drop_table_stmt() {
			return getRuleContext(Drop_table_stmtContext.class,0);
		}
		public Drop_trigger_stmtContext drop_trigger_stmt() {
			return getRuleContext(Drop_trigger_stmtContext.class,0);
		}
		public Drop_view_stmtContext drop_view_stmt() {
			return getRuleContext(Drop_view_stmtContext.class,0);
		}
		public Factored_select_stmtContext factored_select_stmt() {
			return getRuleContext(Factored_select_stmtContext.class,0);
		}
		public Insert_stmtContext insert_stmt() {
			return getRuleContext(Insert_stmtContext.class,0);
		}
		public Pragma_stmtContext pragma_stmt() {
			return getRuleContext(Pragma_stmtContext.class,0);
		}
		public Reindex_stmtContext reindex_stmt() {
			return getRuleContext(Reindex_stmtContext.class,0);
		}
		public Release_stmtContext release_stmt() {
			return getRuleContext(Release_stmtContext.class,0);
		}
		public Rollback_stmtContext rollback_stmt() {
			return getRuleContext(Rollback_stmtContext.class,0);
		}
		public Savepoint_stmtContext savepoint_stmt() {
			return getRuleContext(Savepoint_stmtContext.class,0);
		}
		public Simple_select_stmtContext simple_select_stmt() {
			return getRuleContext(Simple_select_stmtContext.class,0);
		}
		public Select_stmtContext select_stmt() {
			return getRuleContext(Select_stmtContext.class,0);
		}
		public Update_stmtContext update_stmt() {
			return getRuleContext(Update_stmtContext.class,0);
		}
		public Update_stmt_limitedContext update_stmt_limited() {
			return getRuleContext(Update_stmt_limitedContext.class,0);
		}
		public Vacuum_stmtContext vacuum_stmt() {
			return getRuleContext(Vacuum_stmtContext.class,0);
		}
		public TerminalNode K_EXPLAIN() { return getToken(SQLiteParser.K_EXPLAIN, 0); }
		public TerminalNode K_QUERY() { return getToken(SQLiteParser.K_QUERY, 0); }
		public TerminalNode K_PLAN() { return getToken(SQLiteParser.K_PLAN, 0); }
		public Sql_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sql_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterSql_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitSql_stmt(this);
		}
	}

	public final Sql_stmtContext sql_stmt() throws RecognitionException {
		Sql_stmtContext _localctx = new Sql_stmtContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_sql_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(225);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_EXPLAIN) {
				{
				setState(220);
				match(K_EXPLAIN);
				setState(223);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_QUERY) {
					{
					setState(221);
					match(K_QUERY);
					setState(222);
					match(K_PLAN);
					}
				}

				}
			}

			setState(257);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(227);
				alter_table_stmt();
				}
				break;
			case 2:
				{
				setState(228);
				analyze_stmt();
				}
				break;
			case 3:
				{
				setState(229);
				attach_stmt();
				}
				break;
			case 4:
				{
				setState(230);
				begin_stmt();
				}
				break;
			case 5:
				{
				setState(231);
				commit_stmt();
				}
				break;
			case 6:
				{
				setState(232);
				compound_select_stmt();
				}
				break;
			case 7:
				{
				setState(233);
				create_index_stmt();
				}
				break;
			case 8:
				{
				setState(234);
				create_table_stmt();
				}
				break;
			case 9:
				{
				setState(235);
				create_trigger_stmt();
				}
				break;
			case 10:
				{
				setState(236);
				create_view_stmt();
				}
				break;
			case 11:
				{
				setState(237);
				create_virtual_table_stmt();
				}
				break;
			case 12:
				{
				setState(238);
				delete_stmt();
				}
				break;
			case 13:
				{
				setState(239);
				delete_stmt_limited();
				}
				break;
			case 14:
				{
				setState(240);
				detach_stmt();
				}
				break;
			case 15:
				{
				setState(241);
				drop_index_stmt();
				}
				break;
			case 16:
				{
				setState(242);
				drop_table_stmt();
				}
				break;
			case 17:
				{
				setState(243);
				drop_trigger_stmt();
				}
				break;
			case 18:
				{
				setState(244);
				drop_view_stmt();
				}
				break;
			case 19:
				{
				setState(245);
				factored_select_stmt();
				}
				break;
			case 20:
				{
				setState(246);
				insert_stmt();
				}
				break;
			case 21:
				{
				setState(247);
				pragma_stmt();
				}
				break;
			case 22:
				{
				setState(248);
				reindex_stmt();
				}
				break;
			case 23:
				{
				setState(249);
				release_stmt();
				}
				break;
			case 24:
				{
				setState(250);
				rollback_stmt();
				}
				break;
			case 25:
				{
				setState(251);
				savepoint_stmt();
				}
				break;
			case 26:
				{
				setState(252);
				simple_select_stmt();
				}
				break;
			case 27:
				{
				setState(253);
				select_stmt();
				}
				break;
			case 28:
				{
				setState(254);
				update_stmt();
				}
				break;
			case 29:
				{
				setState(255);
				update_stmt_limited();
				}
				break;
			case 30:
				{
				setState(256);
				vacuum_stmt();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Alter_table_stmtContext extends ParserRuleContext {
		public TerminalNode K_ALTER() { return getToken(SQLiteParser.K_ALTER, 0); }
		public TerminalNode K_TABLE() { return getToken(SQLiteParser.K_TABLE, 0); }
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public TerminalNode K_RENAME() { return getToken(SQLiteParser.K_RENAME, 0); }
		public TerminalNode K_TO() { return getToken(SQLiteParser.K_TO, 0); }
		public New_table_nameContext new_table_name() {
			return getRuleContext(New_table_nameContext.class,0);
		}
		public TerminalNode K_ADD() { return getToken(SQLiteParser.K_ADD, 0); }
		public Column_defContext column_def() {
			return getRuleContext(Column_defContext.class,0);
		}
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public TerminalNode K_COLUMN() { return getToken(SQLiteParser.K_COLUMN, 0); }
		public Alter_table_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alter_table_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterAlter_table_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitAlter_table_stmt(this);
		}
	}

	public final Alter_table_stmtContext alter_table_stmt() throws RecognitionException {
		Alter_table_stmtContext _localctx = new Alter_table_stmtContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_alter_table_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(259);
			match(K_ALTER);
			setState(260);
			match(K_TABLE);
			setState(264);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(261);
				database_name();
				setState(262);
				match(DOT);
				}
				break;
			}
			setState(266);
			table_name();
			setState(275);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case K_RENAME:
				{
				setState(267);
				match(K_RENAME);
				setState(268);
				match(K_TO);
				setState(269);
				new_table_name();
				}
				break;
			case K_ADD:
				{
				setState(270);
				match(K_ADD);
				setState(272);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
				case 1:
					{
					setState(271);
					match(K_COLUMN);
					}
					break;
				}
				setState(274);
				column_def();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Analyze_stmtContext extends ParserRuleContext {
		public TerminalNode K_ANALYZE() { return getToken(SQLiteParser.K_ANALYZE, 0); }
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public Table_or_index_nameContext table_or_index_name() {
			return getRuleContext(Table_or_index_nameContext.class,0);
		}
		public Analyze_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_analyze_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterAnalyze_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitAnalyze_stmt(this);
		}
	}

	public final Analyze_stmtContext analyze_stmt() throws RecognitionException {
		Analyze_stmtContext _localctx = new Analyze_stmtContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_analyze_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(277);
			match(K_ANALYZE);
			setState(284);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				setState(278);
				database_name();
				}
				break;
			case 2:
				{
				setState(279);
				table_or_index_name();
				}
				break;
			case 3:
				{
				setState(280);
				database_name();
				setState(281);
				match(DOT);
				setState(282);
				table_or_index_name();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Attach_stmtContext extends ParserRuleContext {
		public TerminalNode K_ATTACH() { return getToken(SQLiteParser.K_ATTACH, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode K_AS() { return getToken(SQLiteParser.K_AS, 0); }
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public TerminalNode K_DATABASE() { return getToken(SQLiteParser.K_DATABASE, 0); }
		public Attach_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attach_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterAttach_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitAttach_stmt(this);
		}
	}

	public final Attach_stmtContext attach_stmt() throws RecognitionException {
		Attach_stmtContext _localctx = new Attach_stmtContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_attach_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(286);
			match(K_ATTACH);
			setState(288);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(287);
				match(K_DATABASE);
				}
				break;
			}
			setState(290);
			expr(0);
			setState(291);
			match(K_AS);
			setState(292);
			database_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Begin_stmtContext extends ParserRuleContext {
		public TerminalNode K_BEGIN() { return getToken(SQLiteParser.K_BEGIN, 0); }
		public TerminalNode K_TRANSACTION() { return getToken(SQLiteParser.K_TRANSACTION, 0); }
		public TerminalNode K_DEFERRED() { return getToken(SQLiteParser.K_DEFERRED, 0); }
		public TerminalNode K_IMMEDIATE() { return getToken(SQLiteParser.K_IMMEDIATE, 0); }
		public TerminalNode K_EXCLUSIVE() { return getToken(SQLiteParser.K_EXCLUSIVE, 0); }
		public Transaction_nameContext transaction_name() {
			return getRuleContext(Transaction_nameContext.class,0);
		}
		public Begin_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_begin_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterBegin_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitBegin_stmt(this);
		}
	}

	public final Begin_stmtContext begin_stmt() throws RecognitionException {
		Begin_stmtContext _localctx = new Begin_stmtContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_begin_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(294);
			match(K_BEGIN);
			setState(296);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 59)) & ~0x3f) == 0 && ((1L << (_la - 59)) & ((1L << (K_DEFERRED - 59)) | (1L << (K_EXCLUSIVE - 59)) | (1L << (K_IMMEDIATE - 59)))) != 0)) {
				{
				setState(295);
				_la = _input.LA(1);
				if ( !(((((_la - 59)) & ~0x3f) == 0 && ((1L << (_la - 59)) & ((1L << (K_DEFERRED - 59)) | (1L << (K_EXCLUSIVE - 59)) | (1L << (K_IMMEDIATE - 59)))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(302);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_TRANSACTION) {
				{
				setState(298);
				match(K_TRANSACTION);
				setState(300);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
				case 1:
					{
					setState(299);
					transaction_name();
					}
					break;
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Commit_stmtContext extends ParserRuleContext {
		public TerminalNode K_COMMIT() { return getToken(SQLiteParser.K_COMMIT, 0); }
		public TerminalNode K_END() { return getToken(SQLiteParser.K_END, 0); }
		public TerminalNode K_TRANSACTION() { return getToken(SQLiteParser.K_TRANSACTION, 0); }
		public Transaction_nameContext transaction_name() {
			return getRuleContext(Transaction_nameContext.class,0);
		}
		public Commit_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commit_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterCommit_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitCommit_stmt(this);
		}
	}

	public final Commit_stmtContext commit_stmt() throws RecognitionException {
		Commit_stmtContext _localctx = new Commit_stmtContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_commit_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(304);
			_la = _input.LA(1);
			if ( !(_la==K_COMMIT || _la==K_END) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(309);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_TRANSACTION) {
				{
				setState(305);
				match(K_TRANSACTION);
				setState(307);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
				case 1:
					{
					setState(306);
					transaction_name();
					}
					break;
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Compound_select_stmtContext extends ParserRuleContext {
		public List<Select_coreContext> select_core() {
			return getRuleContexts(Select_coreContext.class);
		}
		public Select_coreContext select_core(int i) {
			return getRuleContext(Select_coreContext.class,i);
		}
		public With_clauseContext with_clause() {
			return getRuleContext(With_clauseContext.class,0);
		}
		public TerminalNode K_ORDER() { return getToken(SQLiteParser.K_ORDER, 0); }
		public TerminalNode K_BY() { return getToken(SQLiteParser.K_BY, 0); }
		public List<Ordering_termContext> ordering_term() {
			return getRuleContexts(Ordering_termContext.class);
		}
		public Ordering_termContext ordering_term(int i) {
			return getRuleContext(Ordering_termContext.class,i);
		}
		public TerminalNode K_LIMIT() { return getToken(SQLiteParser.K_LIMIT, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> K_UNION() { return getTokens(SQLiteParser.K_UNION); }
		public TerminalNode K_UNION(int i) {
			return getToken(SQLiteParser.K_UNION, i);
		}
		public List<TerminalNode> K_INTERSECT() { return getTokens(SQLiteParser.K_INTERSECT); }
		public TerminalNode K_INTERSECT(int i) {
			return getToken(SQLiteParser.K_INTERSECT, i);
		}
		public List<TerminalNode> K_EXCEPT() { return getTokens(SQLiteParser.K_EXCEPT); }
		public TerminalNode K_EXCEPT(int i) {
			return getToken(SQLiteParser.K_EXCEPT, i);
		}
		public TerminalNode K_OFFSET() { return getToken(SQLiteParser.K_OFFSET, 0); }
		public List<TerminalNode> K_ALL() { return getTokens(SQLiteParser.K_ALL); }
		public TerminalNode K_ALL(int i) {
			return getToken(SQLiteParser.K_ALL, i);
		}
		public Compound_select_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compound_select_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterCompound_select_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitCompound_select_stmt(this);
		}
	}

	public final Compound_select_stmtContext compound_select_stmt() throws RecognitionException {
		Compound_select_stmtContext _localctx = new Compound_select_stmtContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_compound_select_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(312);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WITH) {
				{
				setState(311);
				with_clause();
				}
			}

			setState(314);
			select_core();
			setState(324); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(321);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case K_UNION:
					{
					setState(315);
					match(K_UNION);
					setState(317);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==K_ALL) {
						{
						setState(316);
						match(K_ALL);
						}
					}

					}
					break;
				case K_INTERSECT:
					{
					setState(319);
					match(K_INTERSECT);
					}
					break;
				case K_EXCEPT:
					{
					setState(320);
					match(K_EXCEPT);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(323);
				select_core();
				}
				}
				setState(326); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==K_EXCEPT || _la==K_INTERSECT || _la==K_UNION );
			setState(338);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_ORDER) {
				{
				setState(328);
				match(K_ORDER);
				setState(329);
				match(K_BY);
				setState(330);
				ordering_term();
				setState(335);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(331);
					match(COMMA);
					setState(332);
					ordering_term();
					}
					}
					setState(337);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(346);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_LIMIT) {
				{
				setState(340);
				match(K_LIMIT);
				setState(341);
				expr(0);
				setState(344);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA || _la==K_OFFSET) {
					{
					setState(342);
					_la = _input.LA(1);
					if ( !(_la==COMMA || _la==K_OFFSET) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(343);
					expr(0);
					}
				}

				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Create_index_stmtContext extends ParserRuleContext {
		public TerminalNode K_CREATE() { return getToken(SQLiteParser.K_CREATE, 0); }
		public TerminalNode K_INDEX() { return getToken(SQLiteParser.K_INDEX, 0); }
		public Index_nameContext index_name() {
			return getRuleContext(Index_nameContext.class,0);
		}
		public TerminalNode K_ON() { return getToken(SQLiteParser.K_ON, 0); }
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public List<Indexed_columnContext> indexed_column() {
			return getRuleContexts(Indexed_columnContext.class);
		}
		public Indexed_columnContext indexed_column(int i) {
			return getRuleContext(Indexed_columnContext.class,i);
		}
		public TerminalNode K_UNIQUE() { return getToken(SQLiteParser.K_UNIQUE, 0); }
		public TerminalNode K_IF() { return getToken(SQLiteParser.K_IF, 0); }
		public TerminalNode K_NOT() { return getToken(SQLiteParser.K_NOT, 0); }
		public TerminalNode K_EXISTS() { return getToken(SQLiteParser.K_EXISTS, 0); }
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public TerminalNode K_WHERE() { return getToken(SQLiteParser.K_WHERE, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Create_index_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_index_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterCreate_index_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitCreate_index_stmt(this);
		}
	}

	public final Create_index_stmtContext create_index_stmt() throws RecognitionException {
		Create_index_stmtContext _localctx = new Create_index_stmtContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_create_index_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(348);
			match(K_CREATE);
			setState(350);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_UNIQUE) {
				{
				setState(349);
				match(K_UNIQUE);
				}
			}

			setState(352);
			match(K_INDEX);
			setState(356);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				{
				setState(353);
				match(K_IF);
				setState(354);
				match(K_NOT);
				setState(355);
				match(K_EXISTS);
				}
				break;
			}
			setState(361);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				{
				setState(358);
				database_name();
				setState(359);
				match(DOT);
				}
				break;
			}
			setState(363);
			index_name();
			setState(364);
			match(K_ON);
			setState(365);
			table_name();
			setState(366);
			match(OPEN_PAR);
			setState(367);
			indexed_column();
			setState(372);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(368);
				match(COMMA);
				setState(369);
				indexed_column();
				}
				}
				setState(374);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(375);
			match(CLOSE_PAR);
			setState(378);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WHERE) {
				{
				setState(376);
				match(K_WHERE);
				setState(377);
				expr(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Create_table_stmtContext extends ParserRuleContext {
		public TerminalNode K_CREATE() { return getToken(SQLiteParser.K_CREATE, 0); }
		public TerminalNode K_TABLE() { return getToken(SQLiteParser.K_TABLE, 0); }
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public List<Column_defContext> column_def() {
			return getRuleContexts(Column_defContext.class);
		}
		public Column_defContext column_def(int i) {
			return getRuleContext(Column_defContext.class,i);
		}
		public TerminalNode K_AS() { return getToken(SQLiteParser.K_AS, 0); }
		public Select_stmtContext select_stmt() {
			return getRuleContext(Select_stmtContext.class,0);
		}
		public TerminalNode K_IF() { return getToken(SQLiteParser.K_IF, 0); }
		public TerminalNode K_NOT() { return getToken(SQLiteParser.K_NOT, 0); }
		public TerminalNode K_EXISTS() { return getToken(SQLiteParser.K_EXISTS, 0); }
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public TerminalNode K_TEMP() { return getToken(SQLiteParser.K_TEMP, 0); }
		public TerminalNode K_TEMPORARY() { return getToken(SQLiteParser.K_TEMPORARY, 0); }
		public List<Table_constraintContext> table_constraint() {
			return getRuleContexts(Table_constraintContext.class);
		}
		public Table_constraintContext table_constraint(int i) {
			return getRuleContext(Table_constraintContext.class,i);
		}
		public TerminalNode K_WITHOUT() { return getToken(SQLiteParser.K_WITHOUT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(SQLiteParser.IDENTIFIER, 0); }
		public Create_table_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_table_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterCreate_table_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitCreate_table_stmt(this);
		}
	}

	public final Create_table_stmtContext create_table_stmt() throws RecognitionException {
		Create_table_stmtContext _localctx = new Create_table_stmtContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_create_table_stmt);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(380);
			match(K_CREATE);
			setState(382);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_TEMP || _la==K_TEMPORARY) {
				{
				setState(381);
				_la = _input.LA(1);
				if ( !(_la==K_TEMP || _la==K_TEMPORARY) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(384);
			match(K_TABLE);
			setState(388);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				{
				setState(385);
				match(K_IF);
				setState(386);
				match(K_NOT);
				setState(387);
				match(K_EXISTS);
				}
				break;
			}
			setState(393);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				{
				setState(390);
				database_name();
				setState(391);
				match(DOT);
				}
				break;
			}
			setState(395);
			table_name();
			setState(419);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OPEN_PAR:
				{
				setState(396);
				match(OPEN_PAR);
				setState(397);
				column_def();
				setState(402);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
				while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1+1 ) {
						{
						{
						setState(398);
						match(COMMA);
						setState(399);
						column_def();
						}
						} 
					}
					setState(404);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
				}
				setState(409);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(405);
					match(COMMA);
					setState(406);
					table_constraint();
					}
					}
					setState(411);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(412);
				match(CLOSE_PAR);
				setState(415);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_WITHOUT) {
					{
					setState(413);
					match(K_WITHOUT);
					setState(414);
					match(IDENTIFIER);
					}
				}

				}
				break;
			case K_AS:
				{
				setState(417);
				match(K_AS);
				setState(418);
				select_stmt();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Create_trigger_stmtContext extends ParserRuleContext {
		public TerminalNode K_CREATE() { return getToken(SQLiteParser.K_CREATE, 0); }
		public TerminalNode K_TRIGGER() { return getToken(SQLiteParser.K_TRIGGER, 0); }
		public Trigger_nameContext trigger_name() {
			return getRuleContext(Trigger_nameContext.class,0);
		}
		public TerminalNode K_ON() { return getToken(SQLiteParser.K_ON, 0); }
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public TerminalNode K_BEGIN() { return getToken(SQLiteParser.K_BEGIN, 0); }
		public TerminalNode K_END() { return getToken(SQLiteParser.K_END, 0); }
		public TerminalNode K_DELETE() { return getToken(SQLiteParser.K_DELETE, 0); }
		public TerminalNode K_INSERT() { return getToken(SQLiteParser.K_INSERT, 0); }
		public TerminalNode K_UPDATE() { return getToken(SQLiteParser.K_UPDATE, 0); }
		public TerminalNode K_IF() { return getToken(SQLiteParser.K_IF, 0); }
		public TerminalNode K_NOT() { return getToken(SQLiteParser.K_NOT, 0); }
		public TerminalNode K_EXISTS() { return getToken(SQLiteParser.K_EXISTS, 0); }
		public List<Database_nameContext> database_name() {
			return getRuleContexts(Database_nameContext.class);
		}
		public Database_nameContext database_name(int i) {
			return getRuleContext(Database_nameContext.class,i);
		}
		public TerminalNode K_BEFORE() { return getToken(SQLiteParser.K_BEFORE, 0); }
		public TerminalNode K_AFTER() { return getToken(SQLiteParser.K_AFTER, 0); }
		public TerminalNode K_INSTEAD() { return getToken(SQLiteParser.K_INSTEAD, 0); }
		public List<TerminalNode> K_OF() { return getTokens(SQLiteParser.K_OF); }
		public TerminalNode K_OF(int i) {
			return getToken(SQLiteParser.K_OF, i);
		}
		public TerminalNode K_FOR() { return getToken(SQLiteParser.K_FOR, 0); }
		public TerminalNode K_EACH() { return getToken(SQLiteParser.K_EACH, 0); }
		public TerminalNode K_ROW() { return getToken(SQLiteParser.K_ROW, 0); }
		public TerminalNode K_WHEN() { return getToken(SQLiteParser.K_WHEN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode K_TEMP() { return getToken(SQLiteParser.K_TEMP, 0); }
		public TerminalNode K_TEMPORARY() { return getToken(SQLiteParser.K_TEMPORARY, 0); }
		public List<Column_nameContext> column_name() {
			return getRuleContexts(Column_nameContext.class);
		}
		public Column_nameContext column_name(int i) {
			return getRuleContext(Column_nameContext.class,i);
		}
		public List<Update_stmtContext> update_stmt() {
			return getRuleContexts(Update_stmtContext.class);
		}
		public Update_stmtContext update_stmt(int i) {
			return getRuleContext(Update_stmtContext.class,i);
		}
		public List<Insert_stmtContext> insert_stmt() {
			return getRuleContexts(Insert_stmtContext.class);
		}
		public Insert_stmtContext insert_stmt(int i) {
			return getRuleContext(Insert_stmtContext.class,i);
		}
		public List<Delete_stmtContext> delete_stmt() {
			return getRuleContexts(Delete_stmtContext.class);
		}
		public Delete_stmtContext delete_stmt(int i) {
			return getRuleContext(Delete_stmtContext.class,i);
		}
		public List<Select_stmtContext> select_stmt() {
			return getRuleContexts(Select_stmtContext.class);
		}
		public Select_stmtContext select_stmt(int i) {
			return getRuleContext(Select_stmtContext.class,i);
		}
		public Create_trigger_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_trigger_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterCreate_trigger_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitCreate_trigger_stmt(this);
		}
	}

	public final Create_trigger_stmtContext create_trigger_stmt() throws RecognitionException {
		Create_trigger_stmtContext _localctx = new Create_trigger_stmtContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_create_trigger_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(421);
			match(K_CREATE);
			setState(423);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_TEMP || _la==K_TEMPORARY) {
				{
				setState(422);
				_la = _input.LA(1);
				if ( !(_la==K_TEMP || _la==K_TEMPORARY) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(425);
			match(K_TRIGGER);
			setState(429);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				{
				setState(426);
				match(K_IF);
				setState(427);
				match(K_NOT);
				setState(428);
				match(K_EXISTS);
				}
				break;
			}
			setState(434);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				{
				setState(431);
				database_name();
				setState(432);
				match(DOT);
				}
				break;
			}
			setState(436);
			trigger_name();
			setState(441);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case K_BEFORE:
				{
				setState(437);
				match(K_BEFORE);
				}
				break;
			case K_AFTER:
				{
				setState(438);
				match(K_AFTER);
				}
				break;
			case K_INSTEAD:
				{
				setState(439);
				match(K_INSTEAD);
				setState(440);
				match(K_OF);
				}
				break;
			case K_DELETE:
			case K_INSERT:
			case K_UPDATE:
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(457);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case K_DELETE:
				{
				setState(443);
				match(K_DELETE);
				}
				break;
			case K_INSERT:
				{
				setState(444);
				match(K_INSERT);
				}
				break;
			case K_UPDATE:
				{
				setState(445);
				match(K_UPDATE);
				setState(455);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_OF) {
					{
					setState(446);
					match(K_OF);
					setState(447);
					column_name();
					setState(452);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(448);
						match(COMMA);
						setState(449);
						column_name();
						}
						}
						setState(454);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(459);
			match(K_ON);
			setState(463);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				{
				setState(460);
				database_name();
				setState(461);
				match(DOT);
				}
				break;
			}
			setState(465);
			table_name();
			setState(469);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_FOR) {
				{
				setState(466);
				match(K_FOR);
				setState(467);
				match(K_EACH);
				setState(468);
				match(K_ROW);
				}
			}

			setState(473);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WHEN) {
				{
				setState(471);
				match(K_WHEN);
				setState(472);
				expr(0);
				}
			}

			setState(475);
			match(K_BEGIN);
			setState(484); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(480);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
				case 1:
					{
					setState(476);
					update_stmt();
					}
					break;
				case 2:
					{
					setState(477);
					insert_stmt();
					}
					break;
				case 3:
					{
					setState(478);
					delete_stmt();
					}
					break;
				case 4:
					{
					setState(479);
					select_stmt();
					}
					break;
				}
				setState(482);
				match(SCOL);
				}
				}
				setState(486); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==K_DELETE || ((((_la - 89)) & ~0x3f) == 0 && ((1L << (_la - 89)) & ((1L << (K_INSERT - 89)) | (1L << (K_REPLACE - 89)) | (1L << (K_SELECT - 89)) | (1L << (K_UPDATE - 89)) | (1L << (K_VALUES - 89)) | (1L << (K_WITH - 89)))) != 0) );
			setState(488);
			match(K_END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Create_view_stmtContext extends ParserRuleContext {
		public TerminalNode K_CREATE() { return getToken(SQLiteParser.K_CREATE, 0); }
		public TerminalNode K_VIEW() { return getToken(SQLiteParser.K_VIEW, 0); }
		public View_nameContext view_name() {
			return getRuleContext(View_nameContext.class,0);
		}
		public TerminalNode K_AS() { return getToken(SQLiteParser.K_AS, 0); }
		public Select_stmtContext select_stmt() {
			return getRuleContext(Select_stmtContext.class,0);
		}
		public TerminalNode K_IF() { return getToken(SQLiteParser.K_IF, 0); }
		public TerminalNode K_NOT() { return getToken(SQLiteParser.K_NOT, 0); }
		public TerminalNode K_EXISTS() { return getToken(SQLiteParser.K_EXISTS, 0); }
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public TerminalNode K_TEMP() { return getToken(SQLiteParser.K_TEMP, 0); }
		public TerminalNode K_TEMPORARY() { return getToken(SQLiteParser.K_TEMPORARY, 0); }
		public Create_view_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_view_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterCreate_view_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitCreate_view_stmt(this);
		}
	}

	public final Create_view_stmtContext create_view_stmt() throws RecognitionException {
		Create_view_stmtContext _localctx = new Create_view_stmtContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_create_view_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(490);
			match(K_CREATE);
			setState(492);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_TEMP || _la==K_TEMPORARY) {
				{
				setState(491);
				_la = _input.LA(1);
				if ( !(_la==K_TEMP || _la==K_TEMPORARY) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(494);
			match(K_VIEW);
			setState(498);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,52,_ctx) ) {
			case 1:
				{
				setState(495);
				match(K_IF);
				setState(496);
				match(K_NOT);
				setState(497);
				match(K_EXISTS);
				}
				break;
			}
			setState(503);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
			case 1:
				{
				setState(500);
				database_name();
				setState(501);
				match(DOT);
				}
				break;
			}
			setState(505);
			view_name();
			setState(506);
			match(K_AS);
			setState(507);
			select_stmt();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Create_virtual_table_stmtContext extends ParserRuleContext {
		public TerminalNode K_CREATE() { return getToken(SQLiteParser.K_CREATE, 0); }
		public TerminalNode K_VIRTUAL() { return getToken(SQLiteParser.K_VIRTUAL, 0); }
		public TerminalNode K_TABLE() { return getToken(SQLiteParser.K_TABLE, 0); }
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public TerminalNode K_USING() { return getToken(SQLiteParser.K_USING, 0); }
		public Module_nameContext module_name() {
			return getRuleContext(Module_nameContext.class,0);
		}
		public TerminalNode K_IF() { return getToken(SQLiteParser.K_IF, 0); }
		public TerminalNode K_NOT() { return getToken(SQLiteParser.K_NOT, 0); }
		public TerminalNode K_EXISTS() { return getToken(SQLiteParser.K_EXISTS, 0); }
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public List<Module_argumentContext> module_argument() {
			return getRuleContexts(Module_argumentContext.class);
		}
		public Module_argumentContext module_argument(int i) {
			return getRuleContext(Module_argumentContext.class,i);
		}
		public Create_virtual_table_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_create_virtual_table_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterCreate_virtual_table_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitCreate_virtual_table_stmt(this);
		}
	}

	public final Create_virtual_table_stmtContext create_virtual_table_stmt() throws RecognitionException {
		Create_virtual_table_stmtContext _localctx = new Create_virtual_table_stmtContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_create_virtual_table_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(509);
			match(K_CREATE);
			setState(510);
			match(K_VIRTUAL);
			setState(511);
			match(K_TABLE);
			setState(515);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
			case 1:
				{
				setState(512);
				match(K_IF);
				setState(513);
				match(K_NOT);
				setState(514);
				match(K_EXISTS);
				}
				break;
			}
			setState(520);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
			case 1:
				{
				setState(517);
				database_name();
				setState(518);
				match(DOT);
				}
				break;
			}
			setState(522);
			table_name();
			setState(523);
			match(K_USING);
			setState(524);
			module_name();
			setState(536);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_PAR) {
				{
				setState(525);
				match(OPEN_PAR);
				setState(526);
				module_argument();
				setState(531);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(527);
					match(COMMA);
					setState(528);
					module_argument();
					}
					}
					setState(533);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(534);
				match(CLOSE_PAR);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Delete_stmtContext extends ParserRuleContext {
		public TerminalNode K_DELETE() { return getToken(SQLiteParser.K_DELETE, 0); }
		public TerminalNode K_FROM() { return getToken(SQLiteParser.K_FROM, 0); }
		public Qualified_table_nameContext qualified_table_name() {
			return getRuleContext(Qualified_table_nameContext.class,0);
		}
		public With_clauseContext with_clause() {
			return getRuleContext(With_clauseContext.class,0);
		}
		public TerminalNode K_WHERE() { return getToken(SQLiteParser.K_WHERE, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Delete_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterDelete_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitDelete_stmt(this);
		}
	}

	public final Delete_stmtContext delete_stmt() throws RecognitionException {
		Delete_stmtContext _localctx = new Delete_stmtContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_delete_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(539);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WITH) {
				{
				setState(538);
				with_clause();
				}
			}

			setState(541);
			match(K_DELETE);
			setState(542);
			match(K_FROM);
			setState(543);
			qualified_table_name();
			setState(546);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WHERE) {
				{
				setState(544);
				match(K_WHERE);
				setState(545);
				expr(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Delete_stmt_limitedContext extends ParserRuleContext {
		public TerminalNode K_DELETE() { return getToken(SQLiteParser.K_DELETE, 0); }
		public TerminalNode K_FROM() { return getToken(SQLiteParser.K_FROM, 0); }
		public Qualified_table_nameContext qualified_table_name() {
			return getRuleContext(Qualified_table_nameContext.class,0);
		}
		public With_clauseContext with_clause() {
			return getRuleContext(With_clauseContext.class,0);
		}
		public TerminalNode K_WHERE() { return getToken(SQLiteParser.K_WHERE, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode K_LIMIT() { return getToken(SQLiteParser.K_LIMIT, 0); }
		public TerminalNode K_ORDER() { return getToken(SQLiteParser.K_ORDER, 0); }
		public TerminalNode K_BY() { return getToken(SQLiteParser.K_BY, 0); }
		public List<Ordering_termContext> ordering_term() {
			return getRuleContexts(Ordering_termContext.class);
		}
		public Ordering_termContext ordering_term(int i) {
			return getRuleContext(Ordering_termContext.class,i);
		}
		public TerminalNode K_OFFSET() { return getToken(SQLiteParser.K_OFFSET, 0); }
		public Delete_stmt_limitedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delete_stmt_limited; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterDelete_stmt_limited(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitDelete_stmt_limited(this);
		}
	}

	public final Delete_stmt_limitedContext delete_stmt_limited() throws RecognitionException {
		Delete_stmt_limitedContext _localctx = new Delete_stmt_limitedContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_delete_stmt_limited);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(549);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WITH) {
				{
				setState(548);
				with_clause();
				}
			}

			setState(551);
			match(K_DELETE);
			setState(552);
			match(K_FROM);
			setState(553);
			qualified_table_name();
			setState(556);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WHERE) {
				{
				setState(554);
				match(K_WHERE);
				setState(555);
				expr(0);
				}
			}

			setState(576);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_LIMIT || _la==K_ORDER) {
				{
				setState(568);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_ORDER) {
					{
					setState(558);
					match(K_ORDER);
					setState(559);
					match(K_BY);
					setState(560);
					ordering_term();
					setState(565);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(561);
						match(COMMA);
						setState(562);
						ordering_term();
						}
						}
						setState(567);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(570);
				match(K_LIMIT);
				setState(571);
				expr(0);
				setState(574);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA || _la==K_OFFSET) {
					{
					setState(572);
					_la = _input.LA(1);
					if ( !(_la==COMMA || _la==K_OFFSET) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(573);
					expr(0);
					}
				}

				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Detach_stmtContext extends ParserRuleContext {
		public TerminalNode K_DETACH() { return getToken(SQLiteParser.K_DETACH, 0); }
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public TerminalNode K_DATABASE() { return getToken(SQLiteParser.K_DATABASE, 0); }
		public Detach_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_detach_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterDetach_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitDetach_stmt(this);
		}
	}

	public final Detach_stmtContext detach_stmt() throws RecognitionException {
		Detach_stmtContext _localctx = new Detach_stmtContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_detach_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(578);
			match(K_DETACH);
			setState(580);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
			case 1:
				{
				setState(579);
				match(K_DATABASE);
				}
				break;
			}
			setState(582);
			database_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Drop_index_stmtContext extends ParserRuleContext {
		public TerminalNode K_DROP() { return getToken(SQLiteParser.K_DROP, 0); }
		public TerminalNode K_INDEX() { return getToken(SQLiteParser.K_INDEX, 0); }
		public Index_nameContext index_name() {
			return getRuleContext(Index_nameContext.class,0);
		}
		public TerminalNode K_IF() { return getToken(SQLiteParser.K_IF, 0); }
		public TerminalNode K_EXISTS() { return getToken(SQLiteParser.K_EXISTS, 0); }
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public Drop_index_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_drop_index_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterDrop_index_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitDrop_index_stmt(this);
		}
	}

	public final Drop_index_stmtContext drop_index_stmt() throws RecognitionException {
		Drop_index_stmtContext _localctx = new Drop_index_stmtContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_drop_index_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(584);
			match(K_DROP);
			setState(585);
			match(K_INDEX);
			setState(588);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,67,_ctx) ) {
			case 1:
				{
				setState(586);
				match(K_IF);
				setState(587);
				match(K_EXISTS);
				}
				break;
			}
			setState(593);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
			case 1:
				{
				setState(590);
				database_name();
				setState(591);
				match(DOT);
				}
				break;
			}
			setState(595);
			index_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Drop_table_stmtContext extends ParserRuleContext {
		public TerminalNode K_DROP() { return getToken(SQLiteParser.K_DROP, 0); }
		public TerminalNode K_TABLE() { return getToken(SQLiteParser.K_TABLE, 0); }
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public TerminalNode K_IF() { return getToken(SQLiteParser.K_IF, 0); }
		public TerminalNode K_EXISTS() { return getToken(SQLiteParser.K_EXISTS, 0); }
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public Drop_table_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_drop_table_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterDrop_table_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitDrop_table_stmt(this);
		}
	}

	public final Drop_table_stmtContext drop_table_stmt() throws RecognitionException {
		Drop_table_stmtContext _localctx = new Drop_table_stmtContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_drop_table_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(597);
			match(K_DROP);
			setState(598);
			match(K_TABLE);
			setState(601);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,69,_ctx) ) {
			case 1:
				{
				setState(599);
				match(K_IF);
				setState(600);
				match(K_EXISTS);
				}
				break;
			}
			setState(606);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
			case 1:
				{
				setState(603);
				database_name();
				setState(604);
				match(DOT);
				}
				break;
			}
			setState(608);
			table_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Drop_trigger_stmtContext extends ParserRuleContext {
		public TerminalNode K_DROP() { return getToken(SQLiteParser.K_DROP, 0); }
		public TerminalNode K_TRIGGER() { return getToken(SQLiteParser.K_TRIGGER, 0); }
		public Trigger_nameContext trigger_name() {
			return getRuleContext(Trigger_nameContext.class,0);
		}
		public TerminalNode K_IF() { return getToken(SQLiteParser.K_IF, 0); }
		public TerminalNode K_EXISTS() { return getToken(SQLiteParser.K_EXISTS, 0); }
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public Drop_trigger_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_drop_trigger_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterDrop_trigger_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitDrop_trigger_stmt(this);
		}
	}

	public final Drop_trigger_stmtContext drop_trigger_stmt() throws RecognitionException {
		Drop_trigger_stmtContext _localctx = new Drop_trigger_stmtContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_drop_trigger_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(610);
			match(K_DROP);
			setState(611);
			match(K_TRIGGER);
			setState(614);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,71,_ctx) ) {
			case 1:
				{
				setState(612);
				match(K_IF);
				setState(613);
				match(K_EXISTS);
				}
				break;
			}
			setState(619);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,72,_ctx) ) {
			case 1:
				{
				setState(616);
				database_name();
				setState(617);
				match(DOT);
				}
				break;
			}
			setState(621);
			trigger_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Drop_view_stmtContext extends ParserRuleContext {
		public TerminalNode K_DROP() { return getToken(SQLiteParser.K_DROP, 0); }
		public TerminalNode K_VIEW() { return getToken(SQLiteParser.K_VIEW, 0); }
		public View_nameContext view_name() {
			return getRuleContext(View_nameContext.class,0);
		}
		public TerminalNode K_IF() { return getToken(SQLiteParser.K_IF, 0); }
		public TerminalNode K_EXISTS() { return getToken(SQLiteParser.K_EXISTS, 0); }
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public Drop_view_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_drop_view_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterDrop_view_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitDrop_view_stmt(this);
		}
	}

	public final Drop_view_stmtContext drop_view_stmt() throws RecognitionException {
		Drop_view_stmtContext _localctx = new Drop_view_stmtContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_drop_view_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(623);
			match(K_DROP);
			setState(624);
			match(K_VIEW);
			setState(627);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
			case 1:
				{
				setState(625);
				match(K_IF);
				setState(626);
				match(K_EXISTS);
				}
				break;
			}
			setState(632);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,74,_ctx) ) {
			case 1:
				{
				setState(629);
				database_name();
				setState(630);
				match(DOT);
				}
				break;
			}
			setState(634);
			view_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Factored_select_stmtContext extends ParserRuleContext {
		public List<Select_coreContext> select_core() {
			return getRuleContexts(Select_coreContext.class);
		}
		public Select_coreContext select_core(int i) {
			return getRuleContext(Select_coreContext.class,i);
		}
		public With_clauseContext with_clause() {
			return getRuleContext(With_clauseContext.class,0);
		}
		public List<Compound_operatorContext> compound_operator() {
			return getRuleContexts(Compound_operatorContext.class);
		}
		public Compound_operatorContext compound_operator(int i) {
			return getRuleContext(Compound_operatorContext.class,i);
		}
		public TerminalNode K_ORDER() { return getToken(SQLiteParser.K_ORDER, 0); }
		public TerminalNode K_BY() { return getToken(SQLiteParser.K_BY, 0); }
		public List<Ordering_termContext> ordering_term() {
			return getRuleContexts(Ordering_termContext.class);
		}
		public Ordering_termContext ordering_term(int i) {
			return getRuleContext(Ordering_termContext.class,i);
		}
		public TerminalNode K_LIMIT() { return getToken(SQLiteParser.K_LIMIT, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode K_OFFSET() { return getToken(SQLiteParser.K_OFFSET, 0); }
		public Factored_select_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_factored_select_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterFactored_select_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitFactored_select_stmt(this);
		}
	}

	public final Factored_select_stmtContext factored_select_stmt() throws RecognitionException {
		Factored_select_stmtContext _localctx = new Factored_select_stmtContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_factored_select_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(637);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WITH) {
				{
				setState(636);
				with_clause();
				}
			}

			setState(639);
			select_core();
			setState(645);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==K_EXCEPT || _la==K_INTERSECT || _la==K_UNION) {
				{
				{
				setState(640);
				compound_operator();
				setState(641);
				select_core();
				}
				}
				setState(647);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(658);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_ORDER) {
				{
				setState(648);
				match(K_ORDER);
				setState(649);
				match(K_BY);
				setState(650);
				ordering_term();
				setState(655);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(651);
					match(COMMA);
					setState(652);
					ordering_term();
					}
					}
					setState(657);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(666);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_LIMIT) {
				{
				setState(660);
				match(K_LIMIT);
				setState(661);
				expr(0);
				setState(664);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA || _la==K_OFFSET) {
					{
					setState(662);
					_la = _input.LA(1);
					if ( !(_la==COMMA || _la==K_OFFSET) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(663);
					expr(0);
					}
				}

				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Insert_stmtContext extends ParserRuleContext {
		public TerminalNode K_INTO() { return getToken(SQLiteParser.K_INTO, 0); }
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public TerminalNode K_INSERT() { return getToken(SQLiteParser.K_INSERT, 0); }
		public TerminalNode K_REPLACE() { return getToken(SQLiteParser.K_REPLACE, 0); }
		public TerminalNode K_OR() { return getToken(SQLiteParser.K_OR, 0); }
		public TerminalNode K_ROLLBACK() { return getToken(SQLiteParser.K_ROLLBACK, 0); }
		public TerminalNode K_ABORT() { return getToken(SQLiteParser.K_ABORT, 0); }
		public TerminalNode K_FAIL() { return getToken(SQLiteParser.K_FAIL, 0); }
		public TerminalNode K_IGNORE() { return getToken(SQLiteParser.K_IGNORE, 0); }
		public TerminalNode K_VALUES() { return getToken(SQLiteParser.K_VALUES, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public Select_stmtContext select_stmt() {
			return getRuleContext(Select_stmtContext.class,0);
		}
		public TerminalNode K_DEFAULT() { return getToken(SQLiteParser.K_DEFAULT, 0); }
		public With_clauseContext with_clause() {
			return getRuleContext(With_clauseContext.class,0);
		}
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public List<Column_nameContext> column_name() {
			return getRuleContexts(Column_nameContext.class);
		}
		public Column_nameContext column_name(int i) {
			return getRuleContext(Column_nameContext.class,i);
		}
		public Insert_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_insert_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterInsert_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitInsert_stmt(this);
		}
	}

	public final Insert_stmtContext insert_stmt() throws RecognitionException {
		Insert_stmtContext _localctx = new Insert_stmtContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_insert_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(669);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WITH) {
				{
				setState(668);
				with_clause();
				}
			}

			setState(688);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,82,_ctx) ) {
			case 1:
				{
				setState(671);
				match(K_INSERT);
				}
				break;
			case 2:
				{
				setState(672);
				match(K_REPLACE);
				}
				break;
			case 3:
				{
				setState(673);
				match(K_INSERT);
				setState(674);
				match(K_OR);
				setState(675);
				match(K_REPLACE);
				}
				break;
			case 4:
				{
				setState(676);
				match(K_INSERT);
				setState(677);
				match(K_OR);
				setState(678);
				match(K_ROLLBACK);
				}
				break;
			case 5:
				{
				setState(679);
				match(K_INSERT);
				setState(680);
				match(K_OR);
				setState(681);
				match(K_ABORT);
				}
				break;
			case 6:
				{
				setState(682);
				match(K_INSERT);
				setState(683);
				match(K_OR);
				setState(684);
				match(K_FAIL);
				}
				break;
			case 7:
				{
				setState(685);
				match(K_INSERT);
				setState(686);
				match(K_OR);
				setState(687);
				match(K_IGNORE);
				}
				break;
			}
			setState(690);
			match(K_INTO);
			setState(694);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,83,_ctx) ) {
			case 1:
				{
				setState(691);
				database_name();
				setState(692);
				match(DOT);
				}
				break;
			}
			setState(696);
			table_name();
			setState(708);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_PAR) {
				{
				setState(697);
				match(OPEN_PAR);
				setState(698);
				column_name();
				setState(703);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(699);
					match(COMMA);
					setState(700);
					column_name();
					}
					}
					setState(705);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(706);
				match(CLOSE_PAR);
				}
			}

			setState(741);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,89,_ctx) ) {
			case 1:
				{
				setState(710);
				match(K_VALUES);
				setState(711);
				match(OPEN_PAR);
				setState(712);
				expr(0);
				setState(717);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(713);
					match(COMMA);
					setState(714);
					expr(0);
					}
					}
					setState(719);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(720);
				match(CLOSE_PAR);
				setState(735);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(721);
					match(COMMA);
					setState(722);
					match(OPEN_PAR);
					setState(723);
					expr(0);
					setState(728);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(724);
						match(COMMA);
						setState(725);
						expr(0);
						}
						}
						setState(730);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(731);
					match(CLOSE_PAR);
					}
					}
					setState(737);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				{
				setState(738);
				select_stmt();
				}
				break;
			case 3:
				{
				setState(739);
				match(K_DEFAULT);
				setState(740);
				match(K_VALUES);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pragma_stmtContext extends ParserRuleContext {
		public TerminalNode K_PRAGMA() { return getToken(SQLiteParser.K_PRAGMA, 0); }
		public Pragma_nameContext pragma_name() {
			return getRuleContext(Pragma_nameContext.class,0);
		}
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public Pragma_valueContext pragma_value() {
			return getRuleContext(Pragma_valueContext.class,0);
		}
		public Pragma_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pragma_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterPragma_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitPragma_stmt(this);
		}
	}

	public final Pragma_stmtContext pragma_stmt() throws RecognitionException {
		Pragma_stmtContext _localctx = new Pragma_stmtContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_pragma_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(743);
			match(K_PRAGMA);
			setState(747);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,90,_ctx) ) {
			case 1:
				{
				setState(744);
				database_name();
				setState(745);
				match(DOT);
				}
				break;
			}
			setState(749);
			pragma_name();
			setState(756);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ASSIGN:
				{
				setState(750);
				match(ASSIGN);
				setState(751);
				pragma_value();
				}
				break;
			case OPEN_PAR:
				{
				setState(752);
				match(OPEN_PAR);
				setState(753);
				pragma_value();
				setState(754);
				match(CLOSE_PAR);
				}
				break;
			case EOF:
			case SCOL:
			case K_ALTER:
			case K_ANALYZE:
			case K_ATTACH:
			case K_BEGIN:
			case K_COMMIT:
			case K_CREATE:
			case K_DELETE:
			case K_DETACH:
			case K_DROP:
			case K_END:
			case K_EXPLAIN:
			case K_INSERT:
			case K_PRAGMA:
			case K_REINDEX:
			case K_RELEASE:
			case K_REPLACE:
			case K_ROLLBACK:
			case K_SAVEPOINT:
			case K_SELECT:
			case K_UPDATE:
			case K_VACUUM:
			case K_VALUES:
			case K_WITH:
			case UNEXPECTED_CHAR:
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Reindex_stmtContext extends ParserRuleContext {
		public TerminalNode K_REINDEX() { return getToken(SQLiteParser.K_REINDEX, 0); }
		public Collation_nameContext collation_name() {
			return getRuleContext(Collation_nameContext.class,0);
		}
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public Index_nameContext index_name() {
			return getRuleContext(Index_nameContext.class,0);
		}
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public Reindex_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_reindex_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterReindex_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitReindex_stmt(this);
		}
	}

	public final Reindex_stmtContext reindex_stmt() throws RecognitionException {
		Reindex_stmtContext _localctx = new Reindex_stmtContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_reindex_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(758);
			match(K_REINDEX);
			setState(769);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,94,_ctx) ) {
			case 1:
				{
				setState(759);
				collation_name();
				}
				break;
			case 2:
				{
				setState(763);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,92,_ctx) ) {
				case 1:
					{
					setState(760);
					database_name();
					setState(761);
					match(DOT);
					}
					break;
				}
				setState(767);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,93,_ctx) ) {
				case 1:
					{
					setState(765);
					table_name();
					}
					break;
				case 2:
					{
					setState(766);
					index_name();
					}
					break;
				}
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Release_stmtContext extends ParserRuleContext {
		public TerminalNode K_RELEASE() { return getToken(SQLiteParser.K_RELEASE, 0); }
		public Savepoint_nameContext savepoint_name() {
			return getRuleContext(Savepoint_nameContext.class,0);
		}
		public TerminalNode K_SAVEPOINT() { return getToken(SQLiteParser.K_SAVEPOINT, 0); }
		public Release_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_release_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterRelease_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitRelease_stmt(this);
		}
	}

	public final Release_stmtContext release_stmt() throws RecognitionException {
		Release_stmtContext _localctx = new Release_stmtContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_release_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(771);
			match(K_RELEASE);
			setState(773);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,95,_ctx) ) {
			case 1:
				{
				setState(772);
				match(K_SAVEPOINT);
				}
				break;
			}
			setState(775);
			savepoint_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rollback_stmtContext extends ParserRuleContext {
		public TerminalNode K_ROLLBACK() { return getToken(SQLiteParser.K_ROLLBACK, 0); }
		public TerminalNode K_TRANSACTION() { return getToken(SQLiteParser.K_TRANSACTION, 0); }
		public TerminalNode K_TO() { return getToken(SQLiteParser.K_TO, 0); }
		public Savepoint_nameContext savepoint_name() {
			return getRuleContext(Savepoint_nameContext.class,0);
		}
		public Transaction_nameContext transaction_name() {
			return getRuleContext(Transaction_nameContext.class,0);
		}
		public TerminalNode K_SAVEPOINT() { return getToken(SQLiteParser.K_SAVEPOINT, 0); }
		public Rollback_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rollback_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterRollback_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitRollback_stmt(this);
		}
	}

	public final Rollback_stmtContext rollback_stmt() throws RecognitionException {
		Rollback_stmtContext _localctx = new Rollback_stmtContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_rollback_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(777);
			match(K_ROLLBACK);
			setState(782);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_TRANSACTION) {
				{
				setState(778);
				match(K_TRANSACTION);
				setState(780);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,96,_ctx) ) {
				case 1:
					{
					setState(779);
					transaction_name();
					}
					break;
				}
				}
			}

			setState(789);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_TO) {
				{
				setState(784);
				match(K_TO);
				setState(786);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,98,_ctx) ) {
				case 1:
					{
					setState(785);
					match(K_SAVEPOINT);
					}
					break;
				}
				setState(788);
				savepoint_name();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Savepoint_stmtContext extends ParserRuleContext {
		public TerminalNode K_SAVEPOINT() { return getToken(SQLiteParser.K_SAVEPOINT, 0); }
		public Savepoint_nameContext savepoint_name() {
			return getRuleContext(Savepoint_nameContext.class,0);
		}
		public Savepoint_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_savepoint_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterSavepoint_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitSavepoint_stmt(this);
		}
	}

	public final Savepoint_stmtContext savepoint_stmt() throws RecognitionException {
		Savepoint_stmtContext _localctx = new Savepoint_stmtContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_savepoint_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(791);
			match(K_SAVEPOINT);
			setState(792);
			savepoint_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Simple_select_stmtContext extends ParserRuleContext {
		public Select_coreContext select_core() {
			return getRuleContext(Select_coreContext.class,0);
		}
		public With_clauseContext with_clause() {
			return getRuleContext(With_clauseContext.class,0);
		}
		public TerminalNode K_ORDER() { return getToken(SQLiteParser.K_ORDER, 0); }
		public TerminalNode K_BY() { return getToken(SQLiteParser.K_BY, 0); }
		public List<Ordering_termContext> ordering_term() {
			return getRuleContexts(Ordering_termContext.class);
		}
		public Ordering_termContext ordering_term(int i) {
			return getRuleContext(Ordering_termContext.class,i);
		}
		public TerminalNode K_LIMIT() { return getToken(SQLiteParser.K_LIMIT, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode K_OFFSET() { return getToken(SQLiteParser.K_OFFSET, 0); }
		public Simple_select_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simple_select_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterSimple_select_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitSimple_select_stmt(this);
		}
	}

	public final Simple_select_stmtContext simple_select_stmt() throws RecognitionException {
		Simple_select_stmtContext _localctx = new Simple_select_stmtContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_simple_select_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(795);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WITH) {
				{
				setState(794);
				with_clause();
				}
			}

			setState(797);
			select_core();
			setState(808);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_ORDER) {
				{
				setState(798);
				match(K_ORDER);
				setState(799);
				match(K_BY);
				setState(800);
				ordering_term();
				setState(805);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(801);
					match(COMMA);
					setState(802);
					ordering_term();
					}
					}
					setState(807);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(816);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_LIMIT) {
				{
				setState(810);
				match(K_LIMIT);
				setState(811);
				expr(0);
				setState(814);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA || _la==K_OFFSET) {
					{
					setState(812);
					_la = _input.LA(1);
					if ( !(_la==COMMA || _la==K_OFFSET) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(813);
					expr(0);
					}
				}

				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Select_stmtContext extends ParserRuleContext {
		public List<Select_or_valuesContext> select_or_values() {
			return getRuleContexts(Select_or_valuesContext.class);
		}
		public Select_or_valuesContext select_or_values(int i) {
			return getRuleContext(Select_or_valuesContext.class,i);
		}
		public With_clauseContext with_clause() {
			return getRuleContext(With_clauseContext.class,0);
		}
		public List<Compound_operatorContext> compound_operator() {
			return getRuleContexts(Compound_operatorContext.class);
		}
		public Compound_operatorContext compound_operator(int i) {
			return getRuleContext(Compound_operatorContext.class,i);
		}
		public TerminalNode K_ORDER() { return getToken(SQLiteParser.K_ORDER, 0); }
		public TerminalNode K_BY() { return getToken(SQLiteParser.K_BY, 0); }
		public List<Ordering_termContext> ordering_term() {
			return getRuleContexts(Ordering_termContext.class);
		}
		public Ordering_termContext ordering_term(int i) {
			return getRuleContext(Ordering_termContext.class,i);
		}
		public TerminalNode K_LIMIT() { return getToken(SQLiteParser.K_LIMIT, 0); }
		public LimitContext limit() {
			return getRuleContext(LimitContext.class,0);
		}
		public TerminalNode K_SKIP() { return getToken(SQLiteParser.K_SKIP, 0); }
		public SkipContext skip() {
			return getRuleContext(SkipContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode K_OFFSET() { return getToken(SQLiteParser.K_OFFSET, 0); }
		public Select_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterSelect_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitSelect_stmt(this);
		}
	}

	public final Select_stmtContext select_stmt() throws RecognitionException {
		Select_stmtContext _localctx = new Select_stmtContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_select_stmt);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(819);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WITH) {
				{
				setState(818);
				with_clause();
				}
			}

			setState(821);
			select_or_values();
			setState(827);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,106,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(822);
					compound_operator();
					setState(823);
					select_or_values();
					}
					} 
				}
				setState(829);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,106,_ctx);
			}
			setState(840);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,108,_ctx) ) {
			case 1:
				{
				setState(830);
				match(K_ORDER);
				setState(831);
				match(K_BY);
				setState(832);
				ordering_term();
				setState(837);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(833);
					match(COMMA);
					setState(834);
					ordering_term();
					}
					}
					setState(839);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			}
			setState(849);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,110,_ctx) ) {
			case 1:
				{
				setState(842);
				match(K_LIMIT);
				setState(843);
				limit();
				}
				break;
			case 2:
				{
				{
				setState(844);
				expr(0);
				setState(847);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA || _la==K_OFFSET) {
					{
					setState(845);
					_la = _input.LA(1);
					if ( !(_la==COMMA || _la==K_OFFSET) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(846);
					expr(0);
					}
				}

				}
				}
				break;
			}
			setState(853);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_SKIP) {
				{
				setState(851);
				match(K_SKIP);
				setState(852);
				skip();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Select_or_valuesContext extends ParserRuleContext {
		public TerminalNode K_SELECT() { return getToken(SQLiteParser.K_SELECT, 0); }
		public List<Result_columnContext> result_column() {
			return getRuleContexts(Result_columnContext.class);
		}
		public Result_columnContext result_column(int i) {
			return getRuleContext(Result_columnContext.class,i);
		}
		public TerminalNode K_OVER() { return getToken(SQLiteParser.K_OVER, 0); }
		public TerminalNode K_PARTITION() { return getToken(SQLiteParser.K_PARTITION, 0); }
		public List<TerminalNode> K_BY() { return getTokens(SQLiteParser.K_BY); }
		public TerminalNode K_BY(int i) {
			return getToken(SQLiteParser.K_BY, i);
		}
		public Partition_termContext partition_term() {
			return getRuleContext(Partition_termContext.class,0);
		}
		public List<TerminalNode> K_COLLECTIVE() { return getTokens(SQLiteParser.K_COLLECTIVE); }
		public TerminalNode K_COLLECTIVE(int i) {
			return getToken(SQLiteParser.K_COLLECTIVE, i);
		}
		public TerminalNode K_FROM() { return getToken(SQLiteParser.K_FROM, 0); }
		public TerminalNode K_WHERE() { return getToken(SQLiteParser.K_WHERE, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> K_ORDER() { return getTokens(SQLiteParser.K_ORDER); }
		public TerminalNode K_ORDER(int i) {
			return getToken(SQLiteParser.K_ORDER, i);
		}
		public List<Ordering_termContext> ordering_term() {
			return getRuleContexts(Ordering_termContext.class);
		}
		public Ordering_termContext ordering_term(int i) {
			return getRuleContext(Ordering_termContext.class,i);
		}
		public TerminalNode K_GROUP() { return getToken(SQLiteParser.K_GROUP, 0); }
		public TerminalNode K_DISTINCT() { return getToken(SQLiteParser.K_DISTINCT, 0); }
		public TerminalNode K_ALL() { return getToken(SQLiteParser.K_ALL, 0); }
		public List<Table_or_subqueryContext> table_or_subquery() {
			return getRuleContexts(Table_or_subqueryContext.class);
		}
		public Table_or_subqueryContext table_or_subquery(int i) {
			return getRuleContext(Table_or_subqueryContext.class,i);
		}
		public Join_clauseContext join_clause() {
			return getRuleContext(Join_clauseContext.class,0);
		}
		public List<TerminalNode> K_SKIP() { return getTokens(SQLiteParser.K_SKIP); }
		public TerminalNode K_SKIP(int i) {
			return getToken(SQLiteParser.K_SKIP, i);
		}
		public List<SkipContext> skip() {
			return getRuleContexts(SkipContext.class);
		}
		public SkipContext skip(int i) {
			return getRuleContext(SkipContext.class,i);
		}
		public List<TerminalNode> K_HAVING() { return getTokens(SQLiteParser.K_HAVING); }
		public TerminalNode K_HAVING(int i) {
			return getToken(SQLiteParser.K_HAVING, i);
		}
		public List<TerminalNode> K_LIMIT() { return getTokens(SQLiteParser.K_LIMIT); }
		public TerminalNode K_LIMIT(int i) {
			return getToken(SQLiteParser.K_LIMIT, i);
		}
		public List<LimitContext> limit() {
			return getRuleContexts(LimitContext.class);
		}
		public LimitContext limit(int i) {
			return getRuleContext(LimitContext.class,i);
		}
		public TerminalNode K_VALUES() { return getToken(SQLiteParser.K_VALUES, 0); }
		public Select_or_valuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_or_values; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterSelect_or_values(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitSelect_or_values(this);
		}
	}

	public final Select_or_valuesContext select_or_values() throws RecognitionException {
		Select_or_valuesContext _localctx = new Select_or_valuesContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_select_or_values);
		int _la;
		try {
			int _alt;
			setState(1025);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case K_SELECT:
				enterOuterAlt(_localctx, 1);
				{
				setState(855);
				match(K_SELECT);
				setState(857);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,112,_ctx) ) {
				case 1:
					{
					setState(856);
					_la = _input.LA(1);
					if ( !(_la==K_ALL || _la==K_DISTINCT) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(859);
				result_column();
				setState(864);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,113,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(860);
						match(COMMA);
						setState(861);
						result_column();
						}
						} 
					}
					setState(866);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,113,_ctx);
				}
				setState(901);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(867);
					match(COMMA);
					setState(868);
					match(K_OVER);
					setState(869);
					match(OPEN_PAR);
					setState(870);
					match(K_PARTITION);
					setState(871);
					match(K_BY);
					setState(872);
					partition_term();
					setState(873);
					match(COMMA);
					setState(874);
					match(K_COLLECTIVE);
					setState(875);
					match(OPEN_PAR);
					setState(897);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==K_SKIP) {
						{
						setState(876);
						match(K_SKIP);
						setState(877);
						skip();
						setState(880);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==K_LIMIT) {
							{
							setState(878);
							match(K_LIMIT);
							setState(879);
							limit();
							}
						}

						setState(882);
						match(CLOSE_PAR);
						setState(895);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==K_HAVING) {
							{
							setState(883);
							match(K_HAVING);
							setState(884);
							expr(0);
							setState(885);
							match(K_ORDER);
							setState(886);
							match(K_BY);
							setState(887);
							ordering_term();
							setState(892);
							_errHandler.sync(this);
							_la = _input.LA(1);
							while (_la==COMMA) {
								{
								{
								setState(888);
								match(COMMA);
								setState(889);
								ordering_term();
								}
								}
								setState(894);
								_errHandler.sync(this);
								_la = _input.LA(1);
							}
							}
						}

						}
					}

					setState(899);
					match(CLOSE_PAR);
					}
				}

				setState(915);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,121,_ctx) ) {
				case 1:
					{
					setState(903);
					match(K_FROM);
					setState(913);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,120,_ctx) ) {
					case 1:
						{
						setState(904);
						table_or_subquery();
						setState(909);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==COMMA) {
							{
							{
							setState(905);
							match(COMMA);
							setState(906);
							table_or_subquery();
							}
							}
							setState(911);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						}
						break;
					case 2:
						{
						setState(912);
						join_clause();
						}
						break;
					}
					}
					break;
				}
				setState(919);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,122,_ctx) ) {
				case 1:
					{
					setState(917);
					match(K_WHERE);
					setState(918);
					expr(0);
					}
					break;
				}
				setState(944);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,126,_ctx) ) {
				case 1:
					{
					setState(921);
					match(K_COLLECTIVE);
					setState(922);
					match(OPEN_PAR);
					setState(923);
					match(K_ORDER);
					setState(924);
					match(K_BY);
					setState(925);
					ordering_term();
					setState(940);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,125,_ctx) ) {
					case 1:
						{
						setState(931);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==K_LIMIT) {
							{
							setState(926);
							match(K_LIMIT);
							setState(927);
							limit();
							setState(928);
							match(K_SKIP);
							setState(929);
							skip();
							}
						}

						}
						break;
					case 2:
						{
						setState(938);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==K_SKIP) {
							{
							setState(933);
							match(K_SKIP);
							setState(934);
							skip();
							setState(935);
							match(K_LIMIT);
							setState(936);
							limit();
							}
						}

						}
						break;
					}
					setState(942);
					match(CLOSE_PAR);
					}
					break;
				}
				setState(995);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,135,_ctx) ) {
				case 1:
					{
					setState(946);
					match(K_GROUP);
					setState(947);
					match(K_BY);
					setState(948);
					expr(0);
					setState(982);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,132,_ctx) ) {
					case 1:
						{
						setState(950);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==COMMA) {
							{
							setState(949);
							match(COMMA);
							}
						}

						setState(952);
						match(K_COLLECTIVE);
						setState(953);
						match(OPEN_PAR);
						setState(954);
						match(K_ORDER);
						setState(955);
						match(K_BY);
						setState(956);
						ordering_term();
						setState(961);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==COMMA) {
							{
							{
							setState(957);
							match(COMMA);
							setState(958);
							ordering_term();
							}
							}
							setState(963);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(978);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,131,_ctx) ) {
						case 1:
							{
							setState(969);
							_errHandler.sync(this);
							_la = _input.LA(1);
							if (_la==K_LIMIT) {
								{
								setState(964);
								match(K_LIMIT);
								setState(965);
								limit();
								setState(966);
								match(K_SKIP);
								setState(967);
								skip();
								}
							}

							}
							break;
						case 2:
							{
							setState(976);
							_errHandler.sync(this);
							_la = _input.LA(1);
							if (_la==K_SKIP) {
								{
								setState(971);
								match(K_SKIP);
								setState(972);
								skip();
								setState(973);
								match(K_LIMIT);
								setState(974);
								limit();
								}
							}

							}
							break;
						}
						setState(980);
						match(CLOSE_PAR);
						}
						break;
					}
					setState(988);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(984);
						match(COMMA);
						setState(985);
						expr(0);
						}
						}
						setState(990);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(993);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,134,_ctx) ) {
					case 1:
						{
						setState(991);
						match(K_HAVING);
						setState(992);
						expr(0);
						}
						break;
					}
					}
					break;
				}
				}
				break;
			case K_VALUES:
				enterOuterAlt(_localctx, 2);
				{
				setState(997);
				match(K_VALUES);
				setState(998);
				match(OPEN_PAR);
				setState(999);
				expr(0);
				setState(1004);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(1000);
					match(COMMA);
					setState(1001);
					expr(0);
					}
					}
					setState(1006);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1007);
				match(CLOSE_PAR);
				setState(1022);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(1008);
					match(COMMA);
					setState(1009);
					match(OPEN_PAR);
					setState(1010);
					expr(0);
					setState(1015);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(1011);
						match(COMMA);
						setState(1012);
						expr(0);
						}
						}
						setState(1017);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(1018);
					match(CLOSE_PAR);
					}
					}
					setState(1024);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Partition_termContext extends ParserRuleContext {
		public List<Any_nameContext> any_name() {
			return getRuleContexts(Any_nameContext.class);
		}
		public Any_nameContext any_name(int i) {
			return getRuleContext(Any_nameContext.class,i);
		}
		public TerminalNode K_WITHIN() { return getToken(SQLiteParser.K_WITHIN, 0); }
		public TerminalNode K_DISTANCE() { return getToken(SQLiteParser.K_DISTANCE, 0); }
		public Partition_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_partition_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterPartition_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitPartition_term(this);
		}
	}

	public final Partition_termContext partition_term() throws RecognitionException {
		Partition_termContext _localctx = new Partition_termContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_partition_term);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1029);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WITHIN) {
				{
				setState(1027);
				match(K_WITHIN);
				setState(1028);
				match(K_DISTANCE);
				}
			}

			setState(1031);
			match(OPEN_PAR);
			setState(1032);
			any_name(0);
			setState(1033);
			match(DOT);
			setState(1034);
			any_name(0);
			setState(1035);
			match(COMMA);
			setState(1036);
			any_name(0);
			setState(1037);
			match(CLOSE_PAR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Update_stmtContext extends ParserRuleContext {
		public TerminalNode K_UPDATE() { return getToken(SQLiteParser.K_UPDATE, 0); }
		public Qualified_table_nameContext qualified_table_name() {
			return getRuleContext(Qualified_table_nameContext.class,0);
		}
		public TerminalNode K_SET() { return getToken(SQLiteParser.K_SET, 0); }
		public List<Column_nameContext> column_name() {
			return getRuleContexts(Column_nameContext.class);
		}
		public Column_nameContext column_name(int i) {
			return getRuleContext(Column_nameContext.class,i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public With_clauseContext with_clause() {
			return getRuleContext(With_clauseContext.class,0);
		}
		public TerminalNode K_OR() { return getToken(SQLiteParser.K_OR, 0); }
		public TerminalNode K_ROLLBACK() { return getToken(SQLiteParser.K_ROLLBACK, 0); }
		public TerminalNode K_ABORT() { return getToken(SQLiteParser.K_ABORT, 0); }
		public TerminalNode K_REPLACE() { return getToken(SQLiteParser.K_REPLACE, 0); }
		public TerminalNode K_FAIL() { return getToken(SQLiteParser.K_FAIL, 0); }
		public TerminalNode K_IGNORE() { return getToken(SQLiteParser.K_IGNORE, 0); }
		public TerminalNode K_WHERE() { return getToken(SQLiteParser.K_WHERE, 0); }
		public Update_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterUpdate_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitUpdate_stmt(this);
		}
	}

	public final Update_stmtContext update_stmt() throws RecognitionException {
		Update_stmtContext _localctx = new Update_stmtContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_update_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1040);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WITH) {
				{
				setState(1039);
				with_clause();
				}
			}

			setState(1042);
			match(K_UPDATE);
			setState(1053);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,142,_ctx) ) {
			case 1:
				{
				setState(1043);
				match(K_OR);
				setState(1044);
				match(K_ROLLBACK);
				}
				break;
			case 2:
				{
				setState(1045);
				match(K_OR);
				setState(1046);
				match(K_ABORT);
				}
				break;
			case 3:
				{
				setState(1047);
				match(K_OR);
				setState(1048);
				match(K_REPLACE);
				}
				break;
			case 4:
				{
				setState(1049);
				match(K_OR);
				setState(1050);
				match(K_FAIL);
				}
				break;
			case 5:
				{
				setState(1051);
				match(K_OR);
				setState(1052);
				match(K_IGNORE);
				}
				break;
			}
			setState(1055);
			qualified_table_name();
			setState(1056);
			match(K_SET);
			setState(1057);
			column_name();
			setState(1058);
			match(ASSIGN);
			setState(1059);
			expr(0);
			setState(1067);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1060);
				match(COMMA);
				setState(1061);
				column_name();
				setState(1062);
				match(ASSIGN);
				setState(1063);
				expr(0);
				}
				}
				setState(1069);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1072);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WHERE) {
				{
				setState(1070);
				match(K_WHERE);
				setState(1071);
				expr(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Update_stmt_limitedContext extends ParserRuleContext {
		public TerminalNode K_UPDATE() { return getToken(SQLiteParser.K_UPDATE, 0); }
		public Qualified_table_nameContext qualified_table_name() {
			return getRuleContext(Qualified_table_nameContext.class,0);
		}
		public TerminalNode K_SET() { return getToken(SQLiteParser.K_SET, 0); }
		public List<Column_nameContext> column_name() {
			return getRuleContexts(Column_nameContext.class);
		}
		public Column_nameContext column_name(int i) {
			return getRuleContext(Column_nameContext.class,i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public With_clauseContext with_clause() {
			return getRuleContext(With_clauseContext.class,0);
		}
		public TerminalNode K_OR() { return getToken(SQLiteParser.K_OR, 0); }
		public TerminalNode K_ROLLBACK() { return getToken(SQLiteParser.K_ROLLBACK, 0); }
		public TerminalNode K_ABORT() { return getToken(SQLiteParser.K_ABORT, 0); }
		public TerminalNode K_REPLACE() { return getToken(SQLiteParser.K_REPLACE, 0); }
		public TerminalNode K_FAIL() { return getToken(SQLiteParser.K_FAIL, 0); }
		public TerminalNode K_IGNORE() { return getToken(SQLiteParser.K_IGNORE, 0); }
		public TerminalNode K_WHERE() { return getToken(SQLiteParser.K_WHERE, 0); }
		public TerminalNode K_LIMIT() { return getToken(SQLiteParser.K_LIMIT, 0); }
		public TerminalNode K_ORDER() { return getToken(SQLiteParser.K_ORDER, 0); }
		public TerminalNode K_BY() { return getToken(SQLiteParser.K_BY, 0); }
		public List<Ordering_termContext> ordering_term() {
			return getRuleContexts(Ordering_termContext.class);
		}
		public Ordering_termContext ordering_term(int i) {
			return getRuleContext(Ordering_termContext.class,i);
		}
		public TerminalNode K_OFFSET() { return getToken(SQLiteParser.K_OFFSET, 0); }
		public Update_stmt_limitedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_update_stmt_limited; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterUpdate_stmt_limited(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitUpdate_stmt_limited(this);
		}
	}

	public final Update_stmt_limitedContext update_stmt_limited() throws RecognitionException {
		Update_stmt_limitedContext _localctx = new Update_stmt_limitedContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_update_stmt_limited);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1075);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WITH) {
				{
				setState(1074);
				with_clause();
				}
			}

			setState(1077);
			match(K_UPDATE);
			setState(1088);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,146,_ctx) ) {
			case 1:
				{
				setState(1078);
				match(K_OR);
				setState(1079);
				match(K_ROLLBACK);
				}
				break;
			case 2:
				{
				setState(1080);
				match(K_OR);
				setState(1081);
				match(K_ABORT);
				}
				break;
			case 3:
				{
				setState(1082);
				match(K_OR);
				setState(1083);
				match(K_REPLACE);
				}
				break;
			case 4:
				{
				setState(1084);
				match(K_OR);
				setState(1085);
				match(K_FAIL);
				}
				break;
			case 5:
				{
				setState(1086);
				match(K_OR);
				setState(1087);
				match(K_IGNORE);
				}
				break;
			}
			setState(1090);
			qualified_table_name();
			setState(1091);
			match(K_SET);
			setState(1092);
			column_name();
			setState(1093);
			match(ASSIGN);
			setState(1094);
			expr(0);
			setState(1102);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1095);
				match(COMMA);
				setState(1096);
				column_name();
				setState(1097);
				match(ASSIGN);
				setState(1098);
				expr(0);
				}
				}
				setState(1104);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1107);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_WHERE) {
				{
				setState(1105);
				match(K_WHERE);
				setState(1106);
				expr(0);
				}
			}

			setState(1127);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_LIMIT || _la==K_ORDER) {
				{
				setState(1119);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_ORDER) {
					{
					setState(1109);
					match(K_ORDER);
					setState(1110);
					match(K_BY);
					setState(1111);
					ordering_term();
					setState(1116);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(1112);
						match(COMMA);
						setState(1113);
						ordering_term();
						}
						}
						setState(1118);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(1121);
				match(K_LIMIT);
				setState(1122);
				expr(0);
				setState(1125);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA || _la==K_OFFSET) {
					{
					setState(1123);
					_la = _input.LA(1);
					if ( !(_la==COMMA || _la==K_OFFSET) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(1124);
					expr(0);
					}
				}

				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Vacuum_stmtContext extends ParserRuleContext {
		public TerminalNode K_VACUUM() { return getToken(SQLiteParser.K_VACUUM, 0); }
		public Vacuum_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_vacuum_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterVacuum_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitVacuum_stmt(this);
		}
	}

	public final Vacuum_stmtContext vacuum_stmt() throws RecognitionException {
		Vacuum_stmtContext _localctx = new Vacuum_stmtContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_vacuum_stmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1129);
			match(K_VACUUM);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Column_defContext extends ParserRuleContext {
		public Column_nameContext column_name() {
			return getRuleContext(Column_nameContext.class,0);
		}
		public Type_nameContext type_name() {
			return getRuleContext(Type_nameContext.class,0);
		}
		public List<Column_constraintContext> column_constraint() {
			return getRuleContexts(Column_constraintContext.class);
		}
		public Column_constraintContext column_constraint(int i) {
			return getRuleContext(Column_constraintContext.class,i);
		}
		public Column_defContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column_def; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterColumn_def(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitColumn_def(this);
		}
	}

	public final Column_defContext column_def() throws RecognitionException {
		Column_defContext _localctx = new Column_defContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_column_def);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1131);
			column_name();
			setState(1133);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,153,_ctx) ) {
			case 1:
				{
				setState(1132);
				type_name();
				}
				break;
			}
			setState(1138);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << K_CHECK) | (1L << K_COLLATE) | (1L << K_CONSTRAINT) | (1L << K_DEFAULT))) != 0) || ((((_la - 103)) & ~0x3f) == 0 && ((1L << (_la - 103)) & ((1L << (K_NOT - 103)) | (1L << (K_NULL - 103)) | (1L << (K_PRIMARY - 103)) | (1L << (K_REFERENCES - 103)) | (1L << (K_UNIQUE - 103)))) != 0)) {
				{
				{
				setState(1135);
				column_constraint();
				}
				}
				setState(1140);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Type_nameContext extends ParserRuleContext {
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public List<Signed_numberContext> signed_number() {
			return getRuleContexts(Signed_numberContext.class);
		}
		public Signed_numberContext signed_number(int i) {
			return getRuleContext(Signed_numberContext.class,i);
		}
		public Type_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterType_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitType_name(this);
		}
	}

	public final Type_nameContext type_name() throws RecognitionException {
		Type_nameContext _localctx = new Type_nameContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_type_name);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1142); 
			_errHandler.sync(this);
			_alt = 1+1;
			do {
				switch (_alt) {
				case 1+1:
					{
					{
					setState(1141);
					name();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(1144); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,155,_ctx);
			} while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(1156);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,156,_ctx) ) {
			case 1:
				{
				setState(1146);
				match(OPEN_PAR);
				setState(1147);
				signed_number();
				setState(1148);
				match(CLOSE_PAR);
				}
				break;
			case 2:
				{
				setState(1150);
				match(OPEN_PAR);
				setState(1151);
				signed_number();
				setState(1152);
				match(COMMA);
				setState(1153);
				signed_number();
				setState(1154);
				match(CLOSE_PAR);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Column_constraintContext extends ParserRuleContext {
		public TerminalNode K_PRIMARY() { return getToken(SQLiteParser.K_PRIMARY, 0); }
		public TerminalNode K_KEY() { return getToken(SQLiteParser.K_KEY, 0); }
		public Conflict_clauseContext conflict_clause() {
			return getRuleContext(Conflict_clauseContext.class,0);
		}
		public TerminalNode K_NULL() { return getToken(SQLiteParser.K_NULL, 0); }
		public TerminalNode K_UNIQUE() { return getToken(SQLiteParser.K_UNIQUE, 0); }
		public TerminalNode K_CHECK() { return getToken(SQLiteParser.K_CHECK, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode K_DEFAULT() { return getToken(SQLiteParser.K_DEFAULT, 0); }
		public TerminalNode K_COLLATE() { return getToken(SQLiteParser.K_COLLATE, 0); }
		public Collation_nameContext collation_name() {
			return getRuleContext(Collation_nameContext.class,0);
		}
		public Foreign_key_clauseContext foreign_key_clause() {
			return getRuleContext(Foreign_key_clauseContext.class,0);
		}
		public TerminalNode K_CONSTRAINT() { return getToken(SQLiteParser.K_CONSTRAINT, 0); }
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public Signed_numberContext signed_number() {
			return getRuleContext(Signed_numberContext.class,0);
		}
		public Literal_valueContext literal_value() {
			return getRuleContext(Literal_valueContext.class,0);
		}
		public TerminalNode K_AUTOINCREMENT() { return getToken(SQLiteParser.K_AUTOINCREMENT, 0); }
		public TerminalNode K_NOT() { return getToken(SQLiteParser.K_NOT, 0); }
		public TerminalNode K_ASC() { return getToken(SQLiteParser.K_ASC, 0); }
		public TerminalNode K_DESC() { return getToken(SQLiteParser.K_DESC, 0); }
		public Column_constraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column_constraint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterColumn_constraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitColumn_constraint(this);
		}
	}

	public final Column_constraintContext column_constraint() throws RecognitionException {
		Column_constraintContext _localctx = new Column_constraintContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_column_constraint);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1160);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_CONSTRAINT) {
				{
				setState(1158);
				match(K_CONSTRAINT);
				setState(1159);
				name();
				}
			}

			setState(1195);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case K_PRIMARY:
				{
				setState(1162);
				match(K_PRIMARY);
				setState(1163);
				match(K_KEY);
				setState(1165);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_ASC || _la==K_DESC) {
					{
					setState(1164);
					_la = _input.LA(1);
					if ( !(_la==K_ASC || _la==K_DESC) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(1167);
				conflict_clause();
				setState(1169);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_AUTOINCREMENT) {
					{
					setState(1168);
					match(K_AUTOINCREMENT);
					}
				}

				}
				break;
			case K_NOT:
			case K_NULL:
				{
				setState(1172);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_NOT) {
					{
					setState(1171);
					match(K_NOT);
					}
				}

				setState(1174);
				match(K_NULL);
				setState(1175);
				conflict_clause();
				}
				break;
			case K_UNIQUE:
				{
				setState(1176);
				match(K_UNIQUE);
				setState(1177);
				conflict_clause();
				}
				break;
			case K_CHECK:
				{
				setState(1178);
				match(K_CHECK);
				setState(1179);
				match(OPEN_PAR);
				setState(1180);
				expr(0);
				setState(1181);
				match(CLOSE_PAR);
				}
				break;
			case K_DEFAULT:
				{
				setState(1183);
				match(K_DEFAULT);
				setState(1190);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,161,_ctx) ) {
				case 1:
					{
					setState(1184);
					signed_number();
					}
					break;
				case 2:
					{
					setState(1185);
					literal_value();
					}
					break;
				case 3:
					{
					setState(1186);
					match(OPEN_PAR);
					setState(1187);
					expr(0);
					setState(1188);
					match(CLOSE_PAR);
					}
					break;
				}
				}
				break;
			case K_COLLATE:
				{
				setState(1192);
				match(K_COLLATE);
				setState(1193);
				collation_name();
				}
				break;
			case K_REFERENCES:
				{
				setState(1194);
				foreign_key_clause();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Conflict_clauseContext extends ParserRuleContext {
		public TerminalNode K_ON() { return getToken(SQLiteParser.K_ON, 0); }
		public TerminalNode K_CONFLICT() { return getToken(SQLiteParser.K_CONFLICT, 0); }
		public TerminalNode K_ROLLBACK() { return getToken(SQLiteParser.K_ROLLBACK, 0); }
		public TerminalNode K_ABORT() { return getToken(SQLiteParser.K_ABORT, 0); }
		public TerminalNode K_FAIL() { return getToken(SQLiteParser.K_FAIL, 0); }
		public TerminalNode K_IGNORE() { return getToken(SQLiteParser.K_IGNORE, 0); }
		public TerminalNode K_REPLACE() { return getToken(SQLiteParser.K_REPLACE, 0); }
		public Conflict_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conflict_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterConflict_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitConflict_clause(this);
		}
	}

	public final Conflict_clauseContext conflict_clause() throws RecognitionException {
		Conflict_clauseContext _localctx = new Conflict_clauseContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_conflict_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1200);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_ON) {
				{
				setState(1197);
				match(K_ON);
				setState(1198);
				match(K_CONFLICT);
				setState(1199);
				_la = _input.LA(1);
				if ( !(_la==K_ABORT || ((((_la - 73)) & ~0x3f) == 0 && ((1L << (_la - 73)) & ((1L << (K_FAIL - 73)) | (1L << (K_IGNORE - 73)) | (1L << (K_REPLACE - 73)) | (1L << (K_ROLLBACK - 73)))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LimitContext extends ParserRuleContext {
		public TerminalNode K_CONTAINS() { return getToken(SQLiteParser.K_CONTAINS, 0); }
		public ContainContext contain() {
			return getRuleContext(ContainContext.class,0);
		}
		public LimitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_limit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterLimit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitLimit(this);
		}
	}

	public final LimitContext limit() throws RecognitionException {
		LimitContext _localctx = new LimitContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_limit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1206);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_CONTAINS) {
				{
				setState(1202);
				match(K_CONTAINS);
				setState(1204);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPEN_PAR) {
					{
					setState(1203);
					contain();
					}
				}

				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ContainContext extends ParserRuleContext {
		public TerminalNode K_COLLECT() { return getToken(SQLiteParser.K_COLLECT, 0); }
		public List<File_readContext> file_read() {
			return getRuleContexts(File_readContext.class);
		}
		public File_readContext file_read(int i) {
			return getRuleContext(File_readContext.class,i);
		}
		public List<Any_nameContext> any_name() {
			return getRuleContexts(Any_nameContext.class);
		}
		public Any_nameContext any_name(int i) {
			return getRuleContext(Any_nameContext.class,i);
		}
		public ContainContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_contain; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterContain(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitContain(this);
		}
	}

	public final ContainContext contain() throws RecognitionException {
		ContainContext _localctx = new ContainContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_contain);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1208);
			match(OPEN_PAR);
			setState(1209);
			match(K_COLLECT);
			setState(1210);
			match(OPEN_PAR);
			{
			setState(1211);
			file_read();
			}
			setState(1212);
			match(CLOSE_PAR);
			setState(1213);
			match(COMMA);
			setState(1215);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,166,_ctx) ) {
			case 1:
				{
				setState(1214);
				any_name(0);
				}
				break;
			}
			setState(1218);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << OPEN_PAR) | (1L << K_ABORT) | (1L << K_ACTION) | (1L << K_ADD) | (1L << K_AFTER) | (1L << K_ALL) | (1L << K_ALTER) | (1L << K_ANALYZE) | (1L << K_AND) | (1L << K_AS) | (1L << K_ASC) | (1L << K_ATTACH) | (1L << K_AUTOINCREMENT) | (1L << K_BEFORE) | (1L << K_BEGIN) | (1L << K_BETWEEN) | (1L << K_BY) | (1L << K_CASCADE) | (1L << K_CASE) | (1L << K_CAST) | (1L << K_CHECK) | (1L << K_COLLATE) | (1L << K_COLUMN) | (1L << K_COMMIT) | (1L << K_CONFLICT) | (1L << K_CONSTRAINT) | (1L << K_CREATE) | (1L << K_CROSS) | (1L << K_CURRENT_DATE) | (1L << K_CURRENT_TIME) | (1L << K_CURRENT_TIMESTAMP) | (1L << K_DATABASE) | (1L << K_DEFAULT) | (1L << K_DEFERRABLE) | (1L << K_DEFERRED) | (1L << K_DELETE) | (1L << K_DESC) | (1L << K_DETACH) | (1L << K_DISTINCT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (K_DROP - 64)) | (1L << (K_EACH - 64)) | (1L << (K_ELSE - 64)) | (1L << (K_END - 64)) | (1L << (K_ESCAPE - 64)) | (1L << (K_EXCEPT - 64)) | (1L << (K_EXCLUSIVE - 64)) | (1L << (K_EXISTS - 64)) | (1L << (K_EXPLAIN - 64)) | (1L << (K_FAIL - 64)) | (1L << (K_FOR - 64)) | (1L << (K_FOREIGN - 64)) | (1L << (K_FROM - 64)) | (1L << (K_FULL - 64)) | (1L << (K_GLOB - 64)) | (1L << (K_GROUP - 64)) | (1L << (K_HAVING - 64)) | (1L << (K_IF - 64)) | (1L << (K_IGNORE - 64)) | (1L << (K_IMMEDIATE - 64)) | (1L << (K_IN - 64)) | (1L << (K_INDEX - 64)) | (1L << (K_INDEXED - 64)) | (1L << (K_INITIALLY - 64)) | (1L << (K_INNER - 64)) | (1L << (K_INSERT - 64)) | (1L << (K_INSTEAD - 64)) | (1L << (K_INTERSECT - 64)) | (1L << (K_INTO - 64)) | (1L << (K_IS - 64)) | (1L << (K_ISNULL - 64)) | (1L << (K_JOIN - 64)) | (1L << (K_KEY - 64)) | (1L << (K_LEFT - 64)) | (1L << (K_LIKE - 64)) | (1L << (K_LIMIT - 64)) | (1L << (K_MATCH - 64)) | (1L << (K_NATURAL - 64)) | (1L << (K_NO - 64)) | (1L << (K_NOT - 64)) | (1L << (K_NOTNULL - 64)) | (1L << (K_NULL - 64)) | (1L << (K_OF - 64)) | (1L << (K_OFFSET - 64)) | (1L << (K_ON - 64)) | (1L << (K_OR - 64)) | (1L << (K_ORDER - 64)) | (1L << (K_OUTER - 64)) | (1L << (K_PLAN - 64)) | (1L << (K_PRAGMA - 64)) | (1L << (K_PRIMARY - 64)) | (1L << (K_QUERY - 64)) | (1L << (K_RAISE - 64)) | (1L << (K_RECURSIVE - 64)) | (1L << (K_REFERENCES - 64)) | (1L << (K_REGEXP - 64)) | (1L << (K_REINDEX - 64)) | (1L << (K_RELEASE - 64)) | (1L << (K_RENAME - 64)) | (1L << (K_REPLACE - 64)) | (1L << (K_RESTRICT - 64)) | (1L << (K_RIGHT - 64)) | (1L << (K_ROLLBACK - 64)) | (1L << (K_ROW - 64)))) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & ((1L << (K_SAVEPOINT - 128)) | (1L << (K_SELECT - 128)) | (1L << (K_SET - 128)) | (1L << (K_TABLE - 128)) | (1L << (K_TEMP - 128)) | (1L << (K_TEMPORARY - 128)) | (1L << (K_THEN - 128)) | (1L << (K_TO - 128)) | (1L << (K_TRANSACTION - 128)) | (1L << (K_TRIGGER - 128)) | (1L << (K_UNION - 128)) | (1L << (K_UNIQUE - 128)) | (1L << (K_UPDATE - 128)) | (1L << (K_USING - 128)) | (1L << (K_VACUUM - 128)) | (1L << (K_VALUES - 128)) | (1L << (K_VIEW - 128)) | (1L << (K_VIRTUAL - 128)) | (1L << (K_WHEN - 128)) | (1L << (K_WHERE - 128)) | (1L << (K_WITH - 128)) | (1L << (K_WITHOUT - 128)) | (1L << (K_PARTITION - 128)) | (1L << (K_SKIP - 128)) | (1L << (K_OVER - 128)) | (1L << (K_COLLECTIVE - 128)) | (1L << (K_WITHIN - 128)) | (1L << (K_DISTANCE - 128)) | (1L << (K_CONTAINS - 128)) | (1L << (K_COLLECT - 128)) | (1L << (K_OVERLAP - 128)) | (1L << (IDENTIFIER - 128)) | (1L << (STRING_LITERAL - 128)))) != 0)) {
				{
				setState(1217);
				file_read();
				}
			}

			setState(1220);
			match(CLOSE_PAR);
			setState(1227);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,170,_ctx) ) {
			case 1:
				{
				setState(1222);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,168,_ctx) ) {
				case 1:
					{
					setState(1221);
					file_read();
					}
					break;
				}
				}
				break;
			case 2:
				{
				setState(1225);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,169,_ctx) ) {
				case 1:
					{
					setState(1224);
					any_name(0);
					}
					break;
				}
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SkipContext extends ParserRuleContext {
		public TerminalNode K_CONTAINS() { return getToken(SQLiteParser.K_CONTAINS, 0); }
		public ContainContext contain() {
			return getRuleContext(ContainContext.class,0);
		}
		public SkipContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skip; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterSkip(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitSkip(this);
		}
	}

	public final SkipContext skip() throws RecognitionException {
		SkipContext _localctx = new SkipContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_skip);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1233);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_CONTAINS) {
				{
				setState(1229);
				match(K_CONTAINS);
				setState(1231);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==OPEN_PAR) {
					{
					setState(1230);
					contain();
					}
				}

				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class File_nameContext extends ParserRuleContext {
		public List<Any_nameContext> any_name() {
			return getRuleContexts(Any_nameContext.class);
		}
		public Any_nameContext any_name(int i) {
			return getRuleContext(Any_nameContext.class,i);
		}
		public File_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterFile_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitFile_name(this);
		}
	}

	public final File_nameContext file_name() throws RecognitionException {
		File_nameContext _localctx = new File_nameContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_file_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1235);
			any_name(0);
			setState(1236);
			match(DOT);
			setState(1237);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Read_nameContext extends ParserRuleContext {
		public List<Any_nameContext> any_name() {
			return getRuleContexts(Any_nameContext.class);
		}
		public Any_nameContext any_name(int i) {
			return getRuleContext(Any_nameContext.class,i);
		}
		public Read_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_read_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterRead_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitRead_name(this);
		}
	}

	public final Read_nameContext read_name() throws RecognitionException {
		Read_nameContext _localctx = new Read_nameContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_read_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1239);
			match(T__0);
			setState(1240);
			any_name(0);
			setState(1245);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1241);
				match(COMMA);
				setState(1242);
				any_name(0);
				}
				}
				setState(1247);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1248);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public Literal_valueContext literal_value() {
			return getRuleContext(Literal_valueContext.class,0);
		}
		public TerminalNode BIND_PARAMETER() { return getToken(SQLiteParser.BIND_PARAMETER, 0); }
		public File_readContext file_read() {
			return getRuleContext(File_readContext.class,0);
		}
		public Read_nameContext read_name() {
			return getRuleContext(Read_nameContext.class,0);
		}
		public Function_nameContext function_name() {
			return getRuleContext(Function_nameContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode K_DISTINCT() { return getToken(SQLiteParser.K_DISTINCT, 0); }
		public Column_nameContext column_name() {
			return getRuleContext(Column_nameContext.class,0);
		}
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public Unary_operatorContext unary_operator() {
			return getRuleContext(Unary_operatorContext.class,0);
		}
		public TerminalNode K_CAST() { return getToken(SQLiteParser.K_CAST, 0); }
		public TerminalNode K_AS() { return getToken(SQLiteParser.K_AS, 0); }
		public Type_nameContext type_name() {
			return getRuleContext(Type_nameContext.class,0);
		}
		public Select_stmtContext select_stmt() {
			return getRuleContext(Select_stmtContext.class,0);
		}
		public TerminalNode K_EXISTS() { return getToken(SQLiteParser.K_EXISTS, 0); }
		public TerminalNode K_NOT() { return getToken(SQLiteParser.K_NOT, 0); }
		public TerminalNode K_CASE() { return getToken(SQLiteParser.K_CASE, 0); }
		public TerminalNode K_END() { return getToken(SQLiteParser.K_END, 0); }
		public List<TerminalNode> K_WHEN() { return getTokens(SQLiteParser.K_WHEN); }
		public TerminalNode K_WHEN(int i) {
			return getToken(SQLiteParser.K_WHEN, i);
		}
		public List<TerminalNode> K_THEN() { return getTokens(SQLiteParser.K_THEN); }
		public TerminalNode K_THEN(int i) {
			return getToken(SQLiteParser.K_THEN, i);
		}
		public TerminalNode K_ELSE() { return getToken(SQLiteParser.K_ELSE, 0); }
		public Raise_functionContext raise_function() {
			return getRuleContext(Raise_functionContext.class,0);
		}
		public TerminalNode K_AND() { return getToken(SQLiteParser.K_AND, 0); }
		public TerminalNode K_OR() { return getToken(SQLiteParser.K_OR, 0); }
		public TerminalNode K_IS() { return getToken(SQLiteParser.K_IS, 0); }
		public TerminalNode K_BETWEEN() { return getToken(SQLiteParser.K_BETWEEN, 0); }
		public TerminalNode K_COLLATE() { return getToken(SQLiteParser.K_COLLATE, 0); }
		public Collation_nameContext collation_name() {
			return getRuleContext(Collation_nameContext.class,0);
		}
		public TerminalNode K_LIKE() { return getToken(SQLiteParser.K_LIKE, 0); }
		public TerminalNode K_GLOB() { return getToken(SQLiteParser.K_GLOB, 0); }
		public TerminalNode K_REGEXP() { return getToken(SQLiteParser.K_REGEXP, 0); }
		public TerminalNode K_MATCH() { return getToken(SQLiteParser.K_MATCH, 0); }
		public TerminalNode K_ESCAPE() { return getToken(SQLiteParser.K_ESCAPE, 0); }
		public TerminalNode K_ISNULL() { return getToken(SQLiteParser.K_ISNULL, 0); }
		public TerminalNode K_NOTNULL() { return getToken(SQLiteParser.K_NOTNULL, 0); }
		public TerminalNode K_NULL() { return getToken(SQLiteParser.K_NULL, 0); }
		public TerminalNode K_IN() { return getToken(SQLiteParser.K_IN, 0); }
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitExpr(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 90;
		enterRecursionRule(_localctx, 90, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1328);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,184,_ctx) ) {
			case 1:
				{
				setState(1251);
				literal_value();
				}
				break;
			case 2:
				{
				setState(1252);
				match(BIND_PARAMETER);
				}
				break;
			case 3:
				{
				setState(1253);
				file_read();
				}
				break;
			case 4:
				{
				setState(1254);
				read_name();
				}
				break;
			case 5:
				{
				setState(1255);
				function_name();
				setState(1256);
				match(OPEN_PAR);
				setState(1269);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__0:
				case OPEN_PAR:
				case PLUS:
				case MINUS:
				case TILDE:
				case K_ABORT:
				case K_ACTION:
				case K_ADD:
				case K_AFTER:
				case K_ALL:
				case K_ALTER:
				case K_ANALYZE:
				case K_AND:
				case K_AS:
				case K_ASC:
				case K_ATTACH:
				case K_AUTOINCREMENT:
				case K_BEFORE:
				case K_BEGIN:
				case K_BETWEEN:
				case K_BY:
				case K_CASCADE:
				case K_CASE:
				case K_CAST:
				case K_CHECK:
				case K_COLLATE:
				case K_COLUMN:
				case K_COMMIT:
				case K_CONFLICT:
				case K_CONSTRAINT:
				case K_CREATE:
				case K_CROSS:
				case K_CURRENT_DATE:
				case K_CURRENT_TIME:
				case K_CURRENT_TIMESTAMP:
				case K_DATABASE:
				case K_DEFAULT:
				case K_DEFERRABLE:
				case K_DEFERRED:
				case K_DELETE:
				case K_DESC:
				case K_DETACH:
				case K_DISTINCT:
				case K_DROP:
				case K_EACH:
				case K_ELSE:
				case K_END:
				case K_ESCAPE:
				case K_EXCEPT:
				case K_EXCLUSIVE:
				case K_EXISTS:
				case K_EXPLAIN:
				case K_FAIL:
				case K_FOR:
				case K_FOREIGN:
				case K_FROM:
				case K_FULL:
				case K_GLOB:
				case K_GROUP:
				case K_HAVING:
				case K_IF:
				case K_IGNORE:
				case K_IMMEDIATE:
				case K_IN:
				case K_INDEX:
				case K_INDEXED:
				case K_INITIALLY:
				case K_INNER:
				case K_INSERT:
				case K_INSTEAD:
				case K_INTERSECT:
				case K_INTO:
				case K_IS:
				case K_ISNULL:
				case K_JOIN:
				case K_KEY:
				case K_LEFT:
				case K_LIKE:
				case K_LIMIT:
				case K_MATCH:
				case K_NATURAL:
				case K_NO:
				case K_NOT:
				case K_NOTNULL:
				case K_NULL:
				case K_OF:
				case K_OFFSET:
				case K_ON:
				case K_OR:
				case K_ORDER:
				case K_OUTER:
				case K_PLAN:
				case K_PRAGMA:
				case K_PRIMARY:
				case K_QUERY:
				case K_RAISE:
				case K_RECURSIVE:
				case K_REFERENCES:
				case K_REGEXP:
				case K_REINDEX:
				case K_RELEASE:
				case K_RENAME:
				case K_REPLACE:
				case K_RESTRICT:
				case K_RIGHT:
				case K_ROLLBACK:
				case K_ROW:
				case K_SAVEPOINT:
				case K_SELECT:
				case K_SET:
				case K_TABLE:
				case K_TEMP:
				case K_TEMPORARY:
				case K_THEN:
				case K_TO:
				case K_TRANSACTION:
				case K_TRIGGER:
				case K_UNION:
				case K_UNIQUE:
				case K_UPDATE:
				case K_USING:
				case K_VACUUM:
				case K_VALUES:
				case K_VIEW:
				case K_VIRTUAL:
				case K_WHEN:
				case K_WHERE:
				case K_WITH:
				case K_WITHOUT:
				case K_PARTITION:
				case K_SKIP:
				case K_OVER:
				case K_COLLECTIVE:
				case K_WITHIN:
				case K_DISTANCE:
				case K_CONTAINS:
				case K_COLLECT:
				case K_OVERLAP:
				case IDENTIFIER:
				case NUMERIC_LITERAL:
				case BIND_PARAMETER:
				case STRING_LITERAL:
				case BLOB_LITERAL:
					{
					setState(1258);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,174,_ctx) ) {
					case 1:
						{
						setState(1257);
						match(K_DISTINCT);
						}
						break;
					}
					setState(1260);
					expr(0);
					setState(1265);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(1261);
						match(COMMA);
						setState(1262);
						expr(0);
						}
						}
						setState(1267);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					break;
				case STAR:
					{
					setState(1268);
					match(STAR);
					}
					break;
				case CLOSE_PAR:
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(1271);
				match(CLOSE_PAR);
				}
				break;
			case 6:
				{
				setState(1281);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,178,_ctx) ) {
				case 1:
					{
					setState(1276);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,177,_ctx) ) {
					case 1:
						{
						setState(1273);
						database_name();
						setState(1274);
						match(DOT);
						}
						break;
					}
					setState(1278);
					table_name();
					setState(1279);
					match(DOT);
					}
					break;
				}
				setState(1283);
				column_name();
				}
				break;
			case 7:
				{
				setState(1284);
				unary_operator();
				setState(1285);
				expr(20);
				}
				break;
			case 8:
				{
				setState(1287);
				match(OPEN_PAR);
				setState(1288);
				expr(0);
				setState(1289);
				match(CLOSE_PAR);
				}
				break;
			case 9:
				{
				setState(1291);
				match(K_CAST);
				setState(1292);
				match(OPEN_PAR);
				setState(1293);
				expr(0);
				setState(1294);
				match(K_AS);
				setState(1295);
				type_name();
				setState(1296);
				match(CLOSE_PAR);
				}
				break;
			case 10:
				{
				setState(1302);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_EXISTS || _la==K_NOT) {
					{
					setState(1299);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==K_NOT) {
						{
						setState(1298);
						match(K_NOT);
						}
					}

					setState(1301);
					match(K_EXISTS);
					}
				}

				setState(1304);
				match(OPEN_PAR);
				setState(1305);
				select_stmt();
				setState(1306);
				match(CLOSE_PAR);
				}
				break;
			case 11:
				{
				setState(1308);
				match(K_CASE);
				setState(1310);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,181,_ctx) ) {
				case 1:
					{
					setState(1309);
					expr(0);
					}
					break;
				}
				setState(1317); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(1312);
					match(K_WHEN);
					setState(1313);
					expr(0);
					setState(1314);
					match(K_THEN);
					setState(1315);
					expr(0);
					}
					}
					setState(1319); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==K_WHEN );
				setState(1323);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_ELSE) {
					{
					setState(1321);
					match(K_ELSE);
					setState(1322);
					expr(0);
					}
				}

				setState(1325);
				match(K_END);
				}
				break;
			case 12:
				{
				setState(1327);
				raise_function();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(1417);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,196,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(1415);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,195,_ctx) ) {
					case 1:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(1330);
						if (!(precpred(_ctx, 19))) throw new FailedPredicateException(this, "precpred(_ctx, 19)");
						setState(1331);
						match(PIPE2);
						setState(1332);
						expr(20);
						}
						break;
					case 2:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(1333);
						if (!(precpred(_ctx, 18))) throw new FailedPredicateException(this, "precpred(_ctx, 18)");
						setState(1334);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STAR) | (1L << DIV) | (1L << MOD))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1335);
						expr(19);
						}
						break;
					case 3:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(1336);
						if (!(precpred(_ctx, 17))) throw new FailedPredicateException(this, "precpred(_ctx, 17)");
						setState(1337);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1338);
						expr(18);
						}
						break;
					case 4:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(1339);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(1340);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LT2) | (1L << GT2) | (1L << AMP) | (1L << PIPE))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1341);
						expr(17);
						}
						break;
					case 5:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(1342);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(1343);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LT) | (1L << LT_EQ) | (1L << GT) | (1L << GT_EQ))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1344);
						expr(16);
						}
						break;
					case 6:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(1345);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(1346);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ASSIGN) | (1L << EQ) | (1L << NOT_EQ1) | (1L << NOT_EQ2))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1347);
						expr(15);
						}
						break;
					case 7:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(1348);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(1349);
						match(K_AND);
						setState(1350);
						expr(14);
						}
						break;
					case 8:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(1351);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(1352);
						match(K_OR);
						setState(1353);
						expr(13);
						}
						break;
					case 9:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(1354);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(1355);
						match(K_IS);
						setState(1357);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,185,_ctx) ) {
						case 1:
							{
							setState(1356);
							match(K_NOT);
							}
							break;
						}
						setState(1359);
						expr(7);
						}
						break;
					case 10:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(1360);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(1362);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==K_NOT) {
							{
							setState(1361);
							match(K_NOT);
							}
						}

						setState(1364);
						match(K_BETWEEN);
						setState(1365);
						expr(0);
						setState(1366);
						match(K_AND);
						setState(1367);
						expr(6);
						}
						break;
					case 11:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(1369);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(1370);
						match(K_COLLATE);
						setState(1371);
						collation_name();
						}
						break;
					case 12:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(1372);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(1374);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==K_NOT) {
							{
							setState(1373);
							match(K_NOT);
							}
						}

						setState(1376);
						_la = _input.LA(1);
						if ( !(((((_la - 78)) & ~0x3f) == 0 && ((1L << (_la - 78)) & ((1L << (K_GLOB - 78)) | (1L << (K_LIKE - 78)) | (1L << (K_MATCH - 78)) | (1L << (K_REGEXP - 78)))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(1377);
						expr(0);
						setState(1380);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,188,_ctx) ) {
						case 1:
							{
							setState(1378);
							match(K_ESCAPE);
							setState(1379);
							expr(0);
							}
							break;
						}
						}
						break;
					case 13:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(1382);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(1387);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
						case K_ISNULL:
							{
							setState(1383);
							match(K_ISNULL);
							}
							break;
						case K_NOTNULL:
							{
							setState(1384);
							match(K_NOTNULL);
							}
							break;
						case K_NOT:
							{
							setState(1385);
							match(K_NOT);
							setState(1386);
							match(K_NULL);
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						}
						break;
					case 14:
						{
						_localctx = new ExprContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(1389);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(1391);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==K_NOT) {
							{
							setState(1390);
							match(K_NOT);
							}
						}

						setState(1393);
						match(K_IN);
						setState(1413);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,194,_ctx) ) {
						case 1:
							{
							setState(1394);
							match(OPEN_PAR);
							setState(1404);
							_errHandler.sync(this);
							switch ( getInterpreter().adaptivePredict(_input,192,_ctx) ) {
							case 1:
								{
								setState(1395);
								select_stmt();
								}
								break;
							case 2:
								{
								setState(1396);
								expr(0);
								setState(1401);
								_errHandler.sync(this);
								_la = _input.LA(1);
								while (_la==COMMA) {
									{
									{
									setState(1397);
									match(COMMA);
									setState(1398);
									expr(0);
									}
									}
									setState(1403);
									_errHandler.sync(this);
									_la = _input.LA(1);
								}
								}
								break;
							}
							setState(1406);
							match(CLOSE_PAR);
							}
							break;
						case 2:
							{
							setState(1410);
							_errHandler.sync(this);
							switch ( getInterpreter().adaptivePredict(_input,193,_ctx) ) {
							case 1:
								{
								setState(1407);
								database_name();
								setState(1408);
								match(DOT);
								}
								break;
							}
							setState(1412);
							table_name();
							}
							break;
						}
						}
						break;
					}
					} 
				}
				setState(1419);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,196,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Foreign_key_clauseContext extends ParserRuleContext {
		public TerminalNode K_REFERENCES() { return getToken(SQLiteParser.K_REFERENCES, 0); }
		public Foreign_tableContext foreign_table() {
			return getRuleContext(Foreign_tableContext.class,0);
		}
		public List<Column_nameContext> column_name() {
			return getRuleContexts(Column_nameContext.class);
		}
		public Column_nameContext column_name(int i) {
			return getRuleContext(Column_nameContext.class,i);
		}
		public TerminalNode K_DEFERRABLE() { return getToken(SQLiteParser.K_DEFERRABLE, 0); }
		public List<TerminalNode> K_ON() { return getTokens(SQLiteParser.K_ON); }
		public TerminalNode K_ON(int i) {
			return getToken(SQLiteParser.K_ON, i);
		}
		public List<TerminalNode> K_MATCH() { return getTokens(SQLiteParser.K_MATCH); }
		public TerminalNode K_MATCH(int i) {
			return getToken(SQLiteParser.K_MATCH, i);
		}
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public List<TerminalNode> K_DELETE() { return getTokens(SQLiteParser.K_DELETE); }
		public TerminalNode K_DELETE(int i) {
			return getToken(SQLiteParser.K_DELETE, i);
		}
		public List<TerminalNode> K_UPDATE() { return getTokens(SQLiteParser.K_UPDATE); }
		public TerminalNode K_UPDATE(int i) {
			return getToken(SQLiteParser.K_UPDATE, i);
		}
		public TerminalNode K_NOT() { return getToken(SQLiteParser.K_NOT, 0); }
		public TerminalNode K_INITIALLY() { return getToken(SQLiteParser.K_INITIALLY, 0); }
		public TerminalNode K_DEFERRED() { return getToken(SQLiteParser.K_DEFERRED, 0); }
		public TerminalNode K_IMMEDIATE() { return getToken(SQLiteParser.K_IMMEDIATE, 0); }
		public List<TerminalNode> K_SET() { return getTokens(SQLiteParser.K_SET); }
		public TerminalNode K_SET(int i) {
			return getToken(SQLiteParser.K_SET, i);
		}
		public List<TerminalNode> K_NULL() { return getTokens(SQLiteParser.K_NULL); }
		public TerminalNode K_NULL(int i) {
			return getToken(SQLiteParser.K_NULL, i);
		}
		public List<TerminalNode> K_DEFAULT() { return getTokens(SQLiteParser.K_DEFAULT); }
		public TerminalNode K_DEFAULT(int i) {
			return getToken(SQLiteParser.K_DEFAULT, i);
		}
		public List<TerminalNode> K_CASCADE() { return getTokens(SQLiteParser.K_CASCADE); }
		public TerminalNode K_CASCADE(int i) {
			return getToken(SQLiteParser.K_CASCADE, i);
		}
		public List<TerminalNode> K_RESTRICT() { return getTokens(SQLiteParser.K_RESTRICT); }
		public TerminalNode K_RESTRICT(int i) {
			return getToken(SQLiteParser.K_RESTRICT, i);
		}
		public List<TerminalNode> K_NO() { return getTokens(SQLiteParser.K_NO); }
		public TerminalNode K_NO(int i) {
			return getToken(SQLiteParser.K_NO, i);
		}
		public List<TerminalNode> K_ACTION() { return getTokens(SQLiteParser.K_ACTION); }
		public TerminalNode K_ACTION(int i) {
			return getToken(SQLiteParser.K_ACTION, i);
		}
		public Foreign_key_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_foreign_key_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterForeign_key_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitForeign_key_clause(this);
		}
	}

	public final Foreign_key_clauseContext foreign_key_clause() throws RecognitionException {
		Foreign_key_clauseContext _localctx = new Foreign_key_clauseContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_foreign_key_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1420);
			match(K_REFERENCES);
			setState(1421);
			foreign_table();
			setState(1433);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_PAR) {
				{
				setState(1422);
				match(OPEN_PAR);
				setState(1423);
				column_name();
				setState(1428);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(1424);
					match(COMMA);
					setState(1425);
					column_name();
					}
					}
					setState(1430);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1431);
				match(CLOSE_PAR);
				}
			}

			setState(1453);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==K_MATCH || _la==K_ON) {
				{
				{
				setState(1449);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case K_ON:
					{
					setState(1435);
					match(K_ON);
					setState(1436);
					_la = _input.LA(1);
					if ( !(_la==K_DELETE || _la==K_UPDATE) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(1445);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,199,_ctx) ) {
					case 1:
						{
						setState(1437);
						match(K_SET);
						setState(1438);
						match(K_NULL);
						}
						break;
					case 2:
						{
						setState(1439);
						match(K_SET);
						setState(1440);
						match(K_DEFAULT);
						}
						break;
					case 3:
						{
						setState(1441);
						match(K_CASCADE);
						}
						break;
					case 4:
						{
						setState(1442);
						match(K_RESTRICT);
						}
						break;
					case 5:
						{
						setState(1443);
						match(K_NO);
						setState(1444);
						match(K_ACTION);
						}
						break;
					}
					}
					break;
				case K_MATCH:
					{
					setState(1447);
					match(K_MATCH);
					setState(1448);
					name();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				}
				setState(1455);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1466);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,204,_ctx) ) {
			case 1:
				{
				setState(1457);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_NOT) {
					{
					setState(1456);
					match(K_NOT);
					}
				}

				setState(1459);
				match(K_DEFERRABLE);
				setState(1464);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,203,_ctx) ) {
				case 1:
					{
					setState(1460);
					match(K_INITIALLY);
					setState(1461);
					match(K_DEFERRED);
					}
					break;
				case 2:
					{
					setState(1462);
					match(K_INITIALLY);
					setState(1463);
					match(K_IMMEDIATE);
					}
					break;
				}
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Raise_functionContext extends ParserRuleContext {
		public TerminalNode K_RAISE() { return getToken(SQLiteParser.K_RAISE, 0); }
		public TerminalNode K_IGNORE() { return getToken(SQLiteParser.K_IGNORE, 0); }
		public Error_messageContext error_message() {
			return getRuleContext(Error_messageContext.class,0);
		}
		public TerminalNode K_ROLLBACK() { return getToken(SQLiteParser.K_ROLLBACK, 0); }
		public TerminalNode K_ABORT() { return getToken(SQLiteParser.K_ABORT, 0); }
		public TerminalNode K_FAIL() { return getToken(SQLiteParser.K_FAIL, 0); }
		public Raise_functionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_raise_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterRaise_function(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitRaise_function(this);
		}
	}

	public final Raise_functionContext raise_function() throws RecognitionException {
		Raise_functionContext _localctx = new Raise_functionContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_raise_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1468);
			match(K_RAISE);
			setState(1469);
			match(OPEN_PAR);
			setState(1474);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case K_IGNORE:
				{
				setState(1470);
				match(K_IGNORE);
				}
				break;
			case K_ABORT:
			case K_FAIL:
			case K_ROLLBACK:
				{
				setState(1471);
				_la = _input.LA(1);
				if ( !(_la==K_ABORT || _la==K_FAIL || _la==K_ROLLBACK) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(1472);
				match(COMMA);
				setState(1473);
				error_message();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(1476);
			match(CLOSE_PAR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Indexed_columnContext extends ParserRuleContext {
		public Column_nameContext column_name() {
			return getRuleContext(Column_nameContext.class,0);
		}
		public TerminalNode K_COLLATE() { return getToken(SQLiteParser.K_COLLATE, 0); }
		public Collation_nameContext collation_name() {
			return getRuleContext(Collation_nameContext.class,0);
		}
		public TerminalNode K_ASC() { return getToken(SQLiteParser.K_ASC, 0); }
		public TerminalNode K_DESC() { return getToken(SQLiteParser.K_DESC, 0); }
		public Indexed_columnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_indexed_column; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterIndexed_column(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitIndexed_column(this);
		}
	}

	public final Indexed_columnContext indexed_column() throws RecognitionException {
		Indexed_columnContext _localctx = new Indexed_columnContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_indexed_column);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1478);
			column_name();
			setState(1481);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_COLLATE) {
				{
				setState(1479);
				match(K_COLLATE);
				setState(1480);
				collation_name();
				}
			}

			setState(1484);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_ASC || _la==K_DESC) {
				{
				setState(1483);
				_la = _input.LA(1);
				if ( !(_la==K_ASC || _la==K_DESC) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Table_constraintContext extends ParserRuleContext {
		public List<Indexed_columnContext> indexed_column() {
			return getRuleContexts(Indexed_columnContext.class);
		}
		public Indexed_columnContext indexed_column(int i) {
			return getRuleContext(Indexed_columnContext.class,i);
		}
		public Conflict_clauseContext conflict_clause() {
			return getRuleContext(Conflict_clauseContext.class,0);
		}
		public TerminalNode K_CHECK() { return getToken(SQLiteParser.K_CHECK, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode K_FOREIGN() { return getToken(SQLiteParser.K_FOREIGN, 0); }
		public TerminalNode K_KEY() { return getToken(SQLiteParser.K_KEY, 0); }
		public List<Column_nameContext> column_name() {
			return getRuleContexts(Column_nameContext.class);
		}
		public Column_nameContext column_name(int i) {
			return getRuleContext(Column_nameContext.class,i);
		}
		public Foreign_key_clauseContext foreign_key_clause() {
			return getRuleContext(Foreign_key_clauseContext.class,0);
		}
		public TerminalNode K_CONSTRAINT() { return getToken(SQLiteParser.K_CONSTRAINT, 0); }
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TerminalNode K_PRIMARY() { return getToken(SQLiteParser.K_PRIMARY, 0); }
		public TerminalNode K_UNIQUE() { return getToken(SQLiteParser.K_UNIQUE, 0); }
		public Table_constraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_constraint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterTable_constraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitTable_constraint(this);
		}
	}

	public final Table_constraintContext table_constraint() throws RecognitionException {
		Table_constraintContext _localctx = new Table_constraintContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_table_constraint);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1488);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==K_CONSTRAINT) {
				{
				setState(1486);
				match(K_CONSTRAINT);
				setState(1487);
				name();
				}
			}

			setState(1526);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case K_PRIMARY:
			case K_UNIQUE:
				{
				setState(1493);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case K_PRIMARY:
					{
					setState(1490);
					match(K_PRIMARY);
					setState(1491);
					match(K_KEY);
					}
					break;
				case K_UNIQUE:
					{
					setState(1492);
					match(K_UNIQUE);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(1495);
				match(OPEN_PAR);
				setState(1496);
				indexed_column();
				setState(1501);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(1497);
					match(COMMA);
					setState(1498);
					indexed_column();
					}
					}
					setState(1503);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1504);
				match(CLOSE_PAR);
				setState(1505);
				conflict_clause();
				}
				break;
			case K_CHECK:
				{
				setState(1507);
				match(K_CHECK);
				setState(1508);
				match(OPEN_PAR);
				setState(1509);
				expr(0);
				setState(1510);
				match(CLOSE_PAR);
				}
				break;
			case K_FOREIGN:
				{
				setState(1512);
				match(K_FOREIGN);
				setState(1513);
				match(K_KEY);
				setState(1514);
				match(OPEN_PAR);
				setState(1515);
				column_name();
				setState(1520);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(1516);
					match(COMMA);
					setState(1517);
					column_name();
					}
					}
					setState(1522);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1523);
				match(CLOSE_PAR);
				setState(1524);
				foreign_key_clause();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class With_clauseContext extends ParserRuleContext {
		public TerminalNode K_WITH() { return getToken(SQLiteParser.K_WITH, 0); }
		public List<Common_table_expressionContext> common_table_expression() {
			return getRuleContexts(Common_table_expressionContext.class);
		}
		public Common_table_expressionContext common_table_expression(int i) {
			return getRuleContext(Common_table_expressionContext.class,i);
		}
		public TerminalNode K_RECURSIVE() { return getToken(SQLiteParser.K_RECURSIVE, 0); }
		public With_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_with_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterWith_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitWith_clause(this);
		}
	}

	public final With_clauseContext with_clause() throws RecognitionException {
		With_clauseContext _localctx = new With_clauseContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_with_clause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1528);
			match(K_WITH);
			setState(1530);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,213,_ctx) ) {
			case 1:
				{
				setState(1529);
				match(K_RECURSIVE);
				}
				break;
			}
			setState(1532);
			common_table_expression();
			setState(1537);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1533);
				match(COMMA);
				setState(1534);
				common_table_expression();
				}
				}
				setState(1539);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Qualified_table_nameContext extends ParserRuleContext {
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public Database_nameContext database_name() {
			return getRuleContext(Database_nameContext.class,0);
		}
		public TerminalNode K_INDEXED() { return getToken(SQLiteParser.K_INDEXED, 0); }
		public TerminalNode K_BY() { return getToken(SQLiteParser.K_BY, 0); }
		public Index_nameContext index_name() {
			return getRuleContext(Index_nameContext.class,0);
		}
		public TerminalNode K_NOT() { return getToken(SQLiteParser.K_NOT, 0); }
		public Qualified_table_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qualified_table_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterQualified_table_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitQualified_table_name(this);
		}
	}

	public final Qualified_table_nameContext qualified_table_name() throws RecognitionException {
		Qualified_table_nameContext _localctx = new Qualified_table_nameContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_qualified_table_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1543);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,215,_ctx) ) {
			case 1:
				{
				setState(1540);
				database_name();
				setState(1541);
				match(DOT);
				}
				break;
			}
			setState(1545);
			table_name();
			setState(1551);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case K_INDEXED:
				{
				setState(1546);
				match(K_INDEXED);
				setState(1547);
				match(K_BY);
				setState(1548);
				index_name();
				}
				break;
			case K_NOT:
				{
				setState(1549);
				match(K_NOT);
				setState(1550);
				match(K_INDEXED);
				}
				break;
			case EOF:
			case SCOL:
			case K_ALTER:
			case K_ANALYZE:
			case K_ATTACH:
			case K_BEGIN:
			case K_COMMIT:
			case K_CREATE:
			case K_DELETE:
			case K_DETACH:
			case K_DROP:
			case K_END:
			case K_EXPLAIN:
			case K_INSERT:
			case K_LIMIT:
			case K_ORDER:
			case K_PRAGMA:
			case K_REINDEX:
			case K_RELEASE:
			case K_REPLACE:
			case K_ROLLBACK:
			case K_SAVEPOINT:
			case K_SELECT:
			case K_SET:
			case K_UPDATE:
			case K_VACUUM:
			case K_VALUES:
			case K_WHERE:
			case K_WITH:
			case UNEXPECTED_CHAR:
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Ordering_termContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode K_COLLATE() { return getToken(SQLiteParser.K_COLLATE, 0); }
		public Collation_nameContext collation_name() {
			return getRuleContext(Collation_nameContext.class,0);
		}
		public TerminalNode K_ASC() { return getToken(SQLiteParser.K_ASC, 0); }
		public TerminalNode K_DESC() { return getToken(SQLiteParser.K_DESC, 0); }
		public Ordering_termContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ordering_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterOrdering_term(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitOrdering_term(this);
		}
	}

	public final Ordering_termContext ordering_term() throws RecognitionException {
		Ordering_termContext _localctx = new Ordering_termContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_ordering_term);
		int _la;
		try {
			setState(1565);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,219,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1553);
				expr(0);
				setState(1556);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,217,_ctx) ) {
				case 1:
					{
					setState(1554);
					match(K_COLLATE);
					setState(1555);
					collation_name();
					}
					break;
				}
				setState(1559);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,218,_ctx) ) {
				case 1:
					{
					setState(1558);
					_la = _input.LA(1);
					if ( !(_la==K_ASC || _la==K_DESC) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1561);
				expr(0);
				setState(1562);
				match(PLUS);
				setState(1563);
				expr(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pragma_valueContext extends ParserRuleContext {
		public Signed_numberContext signed_number() {
			return getRuleContext(Signed_numberContext.class,0);
		}
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TerminalNode STRING_LITERAL() { return getToken(SQLiteParser.STRING_LITERAL, 0); }
		public Pragma_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pragma_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterPragma_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitPragma_value(this);
		}
	}

	public final Pragma_valueContext pragma_value() throws RecognitionException {
		Pragma_valueContext _localctx = new Pragma_valueContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_pragma_value);
		try {
			setState(1570);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,220,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1567);
				signed_number();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1568);
				name();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1569);
				match(STRING_LITERAL);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Common_table_expressionContext extends ParserRuleContext {
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public TerminalNode K_AS() { return getToken(SQLiteParser.K_AS, 0); }
		public Select_stmtContext select_stmt() {
			return getRuleContext(Select_stmtContext.class,0);
		}
		public List<Column_nameContext> column_name() {
			return getRuleContexts(Column_nameContext.class);
		}
		public Column_nameContext column_name(int i) {
			return getRuleContext(Column_nameContext.class,i);
		}
		public Common_table_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_common_table_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterCommon_table_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitCommon_table_expression(this);
		}
	}

	public final Common_table_expressionContext common_table_expression() throws RecognitionException {
		Common_table_expressionContext _localctx = new Common_table_expressionContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_common_table_expression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1572);
			table_name();
			setState(1584);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_PAR) {
				{
				setState(1573);
				match(OPEN_PAR);
				setState(1574);
				column_name();
				setState(1579);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(1575);
					match(COMMA);
					setState(1576);
					column_name();
					}
					}
					setState(1581);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1582);
				match(CLOSE_PAR);
				}
			}

			setState(1586);
			match(K_AS);
			setState(1587);
			match(OPEN_PAR);
			setState(1588);
			select_stmt();
			setState(1589);
			match(CLOSE_PAR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Result_columnContext extends ParserRuleContext {
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Column_aliasContext column_alias() {
			return getRuleContext(Column_aliasContext.class,0);
		}
		public TerminalNode K_AS() { return getToken(SQLiteParser.K_AS, 0); }
		public Result_columnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_result_column; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterResult_column(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitResult_column(this);
		}
	}

	public final Result_columnContext result_column() throws RecognitionException {
		Result_columnContext _localctx = new Result_columnContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_result_column);
		int _la;
		try {
			setState(1603);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,225,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1591);
				match(STAR);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1592);
				table_name();
				setState(1593);
				match(DOT);
				setState(1594);
				match(STAR);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1596);
				expr(0);
				setState(1601);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,224,_ctx) ) {
				case 1:
					{
					setState(1598);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==K_AS) {
						{
						setState(1597);
						match(K_AS);
						}
					}

					setState(1600);
					column_alias();
					}
					break;
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Table_or_subqueryContext extends ParserRuleContext {
		public Table_nameContext table_name() {
			return getRuleContext(Table_nameContext.class,0);
		}
		public Schema_nameContext schema_name() {
			return getRuleContext(Schema_nameContext.class,0);
		}
		public Table_aliasContext table_alias() {
			return getRuleContext(Table_aliasContext.class,0);
		}
		public TerminalNode K_INDEXED() { return getToken(SQLiteParser.K_INDEXED, 0); }
		public TerminalNode K_BY() { return getToken(SQLiteParser.K_BY, 0); }
		public Index_nameContext index_name() {
			return getRuleContext(Index_nameContext.class,0);
		}
		public TerminalNode K_NOT() { return getToken(SQLiteParser.K_NOT, 0); }
		public TerminalNode K_AS() { return getToken(SQLiteParser.K_AS, 0); }
		public Table_function_nameContext table_function_name() {
			return getRuleContext(Table_function_nameContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<Table_or_subqueryContext> table_or_subquery() {
			return getRuleContexts(Table_or_subqueryContext.class);
		}
		public Table_or_subqueryContext table_or_subquery(int i) {
			return getRuleContext(Table_or_subqueryContext.class,i);
		}
		public Join_clauseContext join_clause() {
			return getRuleContext(Join_clauseContext.class,0);
		}
		public Select_stmtContext select_stmt() {
			return getRuleContext(Select_stmtContext.class,0);
		}
		public Table_or_subqueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_or_subquery; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterTable_or_subquery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitTable_or_subquery(this);
		}
	}

	public final Table_or_subqueryContext table_or_subquery() throws RecognitionException {
		Table_or_subqueryContext _localctx = new Table_or_subqueryContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_table_or_subquery);
		int _la;
		try {
			setState(1671);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,239,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1608);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,226,_ctx) ) {
				case 1:
					{
					setState(1605);
					schema_name();
					setState(1606);
					match(DOT);
					}
					break;
				}
				setState(1610);
				table_name();
				setState(1615);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,228,_ctx) ) {
				case 1:
					{
					setState(1612);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==K_AS) {
						{
						setState(1611);
						match(K_AS);
						}
					}

					setState(1614);
					table_alias();
					}
					break;
				}
				setState(1622);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,229,_ctx) ) {
				case 1:
					{
					setState(1617);
					match(K_INDEXED);
					setState(1618);
					match(K_BY);
					setState(1619);
					index_name();
					}
					break;
				case 2:
					{
					setState(1620);
					match(K_NOT);
					setState(1621);
					match(K_INDEXED);
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1627);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,230,_ctx) ) {
				case 1:
					{
					setState(1624);
					schema_name();
					setState(1625);
					match(DOT);
					}
					break;
				}
				setState(1629);
				table_function_name();
				setState(1630);
				match(OPEN_PAR);
				setState(1639);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << OPEN_PAR) | (1L << PLUS) | (1L << MINUS) | (1L << TILDE) | (1L << K_ABORT) | (1L << K_ACTION) | (1L << K_ADD) | (1L << K_AFTER) | (1L << K_ALL) | (1L << K_ALTER) | (1L << K_ANALYZE) | (1L << K_AND) | (1L << K_AS) | (1L << K_ASC) | (1L << K_ATTACH) | (1L << K_AUTOINCREMENT) | (1L << K_BEFORE) | (1L << K_BEGIN) | (1L << K_BETWEEN) | (1L << K_BY) | (1L << K_CASCADE) | (1L << K_CASE) | (1L << K_CAST) | (1L << K_CHECK) | (1L << K_COLLATE) | (1L << K_COLUMN) | (1L << K_COMMIT) | (1L << K_CONFLICT) | (1L << K_CONSTRAINT) | (1L << K_CREATE) | (1L << K_CROSS) | (1L << K_CURRENT_DATE) | (1L << K_CURRENT_TIME) | (1L << K_CURRENT_TIMESTAMP) | (1L << K_DATABASE) | (1L << K_DEFAULT) | (1L << K_DEFERRABLE) | (1L << K_DEFERRED) | (1L << K_DELETE) | (1L << K_DESC) | (1L << K_DETACH) | (1L << K_DISTINCT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (K_DROP - 64)) | (1L << (K_EACH - 64)) | (1L << (K_ELSE - 64)) | (1L << (K_END - 64)) | (1L << (K_ESCAPE - 64)) | (1L << (K_EXCEPT - 64)) | (1L << (K_EXCLUSIVE - 64)) | (1L << (K_EXISTS - 64)) | (1L << (K_EXPLAIN - 64)) | (1L << (K_FAIL - 64)) | (1L << (K_FOR - 64)) | (1L << (K_FOREIGN - 64)) | (1L << (K_FROM - 64)) | (1L << (K_FULL - 64)) | (1L << (K_GLOB - 64)) | (1L << (K_GROUP - 64)) | (1L << (K_HAVING - 64)) | (1L << (K_IF - 64)) | (1L << (K_IGNORE - 64)) | (1L << (K_IMMEDIATE - 64)) | (1L << (K_IN - 64)) | (1L << (K_INDEX - 64)) | (1L << (K_INDEXED - 64)) | (1L << (K_INITIALLY - 64)) | (1L << (K_INNER - 64)) | (1L << (K_INSERT - 64)) | (1L << (K_INSTEAD - 64)) | (1L << (K_INTERSECT - 64)) | (1L << (K_INTO - 64)) | (1L << (K_IS - 64)) | (1L << (K_ISNULL - 64)) | (1L << (K_JOIN - 64)) | (1L << (K_KEY - 64)) | (1L << (K_LEFT - 64)) | (1L << (K_LIKE - 64)) | (1L << (K_LIMIT - 64)) | (1L << (K_MATCH - 64)) | (1L << (K_NATURAL - 64)) | (1L << (K_NO - 64)) | (1L << (K_NOT - 64)) | (1L << (K_NOTNULL - 64)) | (1L << (K_NULL - 64)) | (1L << (K_OF - 64)) | (1L << (K_OFFSET - 64)) | (1L << (K_ON - 64)) | (1L << (K_OR - 64)) | (1L << (K_ORDER - 64)) | (1L << (K_OUTER - 64)) | (1L << (K_PLAN - 64)) | (1L << (K_PRAGMA - 64)) | (1L << (K_PRIMARY - 64)) | (1L << (K_QUERY - 64)) | (1L << (K_RAISE - 64)) | (1L << (K_RECURSIVE - 64)) | (1L << (K_REFERENCES - 64)) | (1L << (K_REGEXP - 64)) | (1L << (K_REINDEX - 64)) | (1L << (K_RELEASE - 64)) | (1L << (K_RENAME - 64)) | (1L << (K_REPLACE - 64)) | (1L << (K_RESTRICT - 64)) | (1L << (K_RIGHT - 64)) | (1L << (K_ROLLBACK - 64)) | (1L << (K_ROW - 64)))) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & ((1L << (K_SAVEPOINT - 128)) | (1L << (K_SELECT - 128)) | (1L << (K_SET - 128)) | (1L << (K_TABLE - 128)) | (1L << (K_TEMP - 128)) | (1L << (K_TEMPORARY - 128)) | (1L << (K_THEN - 128)) | (1L << (K_TO - 128)) | (1L << (K_TRANSACTION - 128)) | (1L << (K_TRIGGER - 128)) | (1L << (K_UNION - 128)) | (1L << (K_UNIQUE - 128)) | (1L << (K_UPDATE - 128)) | (1L << (K_USING - 128)) | (1L << (K_VACUUM - 128)) | (1L << (K_VALUES - 128)) | (1L << (K_VIEW - 128)) | (1L << (K_VIRTUAL - 128)) | (1L << (K_WHEN - 128)) | (1L << (K_WHERE - 128)) | (1L << (K_WITH - 128)) | (1L << (K_WITHOUT - 128)) | (1L << (K_PARTITION - 128)) | (1L << (K_SKIP - 128)) | (1L << (K_OVER - 128)) | (1L << (K_COLLECTIVE - 128)) | (1L << (K_WITHIN - 128)) | (1L << (K_DISTANCE - 128)) | (1L << (K_CONTAINS - 128)) | (1L << (K_COLLECT - 128)) | (1L << (K_OVERLAP - 128)) | (1L << (IDENTIFIER - 128)) | (1L << (NUMERIC_LITERAL - 128)) | (1L << (BIND_PARAMETER - 128)) | (1L << (STRING_LITERAL - 128)) | (1L << (BLOB_LITERAL - 128)))) != 0)) {
					{
					setState(1631);
					expr(0);
					setState(1636);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(1632);
						match(COMMA);
						setState(1633);
						expr(0);
						}
						}
						setState(1638);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(1641);
				match(CLOSE_PAR);
				setState(1646);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,234,_ctx) ) {
				case 1:
					{
					setState(1643);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==K_AS) {
						{
						setState(1642);
						match(K_AS);
						}
					}

					setState(1645);
					table_alias();
					}
					break;
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1648);
				match(OPEN_PAR);
				setState(1658);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,236,_ctx) ) {
				case 1:
					{
					setState(1649);
					table_or_subquery();
					setState(1654);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(1650);
						match(COMMA);
						setState(1651);
						table_or_subquery();
						}
						}
						setState(1656);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					break;
				case 2:
					{
					setState(1657);
					join_clause();
					}
					break;
				}
				setState(1660);
				match(CLOSE_PAR);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1662);
				match(OPEN_PAR);
				setState(1663);
				select_stmt();
				setState(1664);
				match(CLOSE_PAR);
				setState(1669);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,238,_ctx) ) {
				case 1:
					{
					setState(1666);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==K_AS) {
						{
						setState(1665);
						match(K_AS);
						}
					}

					setState(1668);
					table_alias();
					}
					break;
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Join_clauseContext extends ParserRuleContext {
		public List<Table_or_subqueryContext> table_or_subquery() {
			return getRuleContexts(Table_or_subqueryContext.class);
		}
		public Table_or_subqueryContext table_or_subquery(int i) {
			return getRuleContext(Table_or_subqueryContext.class,i);
		}
		public List<Join_operatorContext> join_operator() {
			return getRuleContexts(Join_operatorContext.class);
		}
		public Join_operatorContext join_operator(int i) {
			return getRuleContext(Join_operatorContext.class,i);
		}
		public List<Join_constraintContext> join_constraint() {
			return getRuleContexts(Join_constraintContext.class);
		}
		public Join_constraintContext join_constraint(int i) {
			return getRuleContext(Join_constraintContext.class,i);
		}
		public Join_clauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_join_clause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterJoin_clause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitJoin_clause(this);
		}
	}

	public final Join_clauseContext join_clause() throws RecognitionException {
		Join_clauseContext _localctx = new Join_clauseContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_join_clause);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1673);
			table_or_subquery();
			setState(1680);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,240,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1674);
					join_operator();
					setState(1675);
					table_or_subquery();
					setState(1676);
					join_constraint();
					}
					} 
				}
				setState(1682);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,240,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Join_operatorContext extends ParserRuleContext {
		public TerminalNode K_JOIN() { return getToken(SQLiteParser.K_JOIN, 0); }
		public TerminalNode K_NATURAL() { return getToken(SQLiteParser.K_NATURAL, 0); }
		public TerminalNode K_LEFT() { return getToken(SQLiteParser.K_LEFT, 0); }
		public TerminalNode K_INNER() { return getToken(SQLiteParser.K_INNER, 0); }
		public TerminalNode K_CROSS() { return getToken(SQLiteParser.K_CROSS, 0); }
		public TerminalNode K_OUTER() { return getToken(SQLiteParser.K_OUTER, 0); }
		public Join_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_join_operator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterJoin_operator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitJoin_operator(this);
		}
	}

	public final Join_operatorContext join_operator() throws RecognitionException {
		Join_operatorContext _localctx = new Join_operatorContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_join_operator);
		int _la;
		try {
			setState(1696);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COMMA:
				enterOuterAlt(_localctx, 1);
				{
				setState(1683);
				match(COMMA);
				}
				break;
			case K_CROSS:
			case K_INNER:
			case K_JOIN:
			case K_LEFT:
			case K_NATURAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(1685);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_NATURAL) {
					{
					setState(1684);
					match(K_NATURAL);
					}
				}

				setState(1693);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case K_LEFT:
					{
					setState(1687);
					match(K_LEFT);
					setState(1689);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==K_OUTER) {
						{
						setState(1688);
						match(K_OUTER);
						}
					}

					}
					break;
				case K_INNER:
					{
					setState(1691);
					match(K_INNER);
					}
					break;
				case K_CROSS:
					{
					setState(1692);
					match(K_CROSS);
					}
					break;
				case K_JOIN:
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(1695);
				match(K_JOIN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Join_constraintContext extends ParserRuleContext {
		public TerminalNode K_ON() { return getToken(SQLiteParser.K_ON, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode K_USING() { return getToken(SQLiteParser.K_USING, 0); }
		public List<Column_nameContext> column_name() {
			return getRuleContexts(Column_nameContext.class);
		}
		public Column_nameContext column_name(int i) {
			return getRuleContext(Column_nameContext.class,i);
		}
		public Join_constraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_join_constraint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterJoin_constraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitJoin_constraint(this);
		}
	}

	public final Join_constraintContext join_constraint() throws RecognitionException {
		Join_constraintContext _localctx = new Join_constraintContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_join_constraint);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1712);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,246,_ctx) ) {
			case 1:
				{
				setState(1698);
				match(K_ON);
				setState(1699);
				expr(0);
				}
				break;
			case 2:
				{
				setState(1700);
				match(K_USING);
				setState(1701);
				match(OPEN_PAR);
				setState(1702);
				column_name();
				setState(1707);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(1703);
					match(COMMA);
					setState(1704);
					column_name();
					}
					}
					setState(1709);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1710);
				match(CLOSE_PAR);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Select_coreContext extends ParserRuleContext {
		public TerminalNode K_SELECT() { return getToken(SQLiteParser.K_SELECT, 0); }
		public List<Result_columnContext> result_column() {
			return getRuleContexts(Result_columnContext.class);
		}
		public Result_columnContext result_column(int i) {
			return getRuleContext(Result_columnContext.class,i);
		}
		public TerminalNode K_FROM() { return getToken(SQLiteParser.K_FROM, 0); }
		public TerminalNode K_WHERE() { return getToken(SQLiteParser.K_WHERE, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode K_GROUP() { return getToken(SQLiteParser.K_GROUP, 0); }
		public TerminalNode K_BY() { return getToken(SQLiteParser.K_BY, 0); }
		public TerminalNode K_DISTINCT() { return getToken(SQLiteParser.K_DISTINCT, 0); }
		public TerminalNode K_ALL() { return getToken(SQLiteParser.K_ALL, 0); }
		public List<Table_or_subqueryContext> table_or_subquery() {
			return getRuleContexts(Table_or_subqueryContext.class);
		}
		public Table_or_subqueryContext table_or_subquery(int i) {
			return getRuleContext(Table_or_subqueryContext.class,i);
		}
		public Join_clauseContext join_clause() {
			return getRuleContext(Join_clauseContext.class,0);
		}
		public TerminalNode K_HAVING() { return getToken(SQLiteParser.K_HAVING, 0); }
		public TerminalNode K_VALUES() { return getToken(SQLiteParser.K_VALUES, 0); }
		public Select_coreContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_select_core; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterSelect_core(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitSelect_core(this);
		}
	}

	public final Select_coreContext select_core() throws RecognitionException {
		Select_coreContext _localctx = new Select_coreContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_select_core);
		int _la;
		try {
			setState(1788);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case K_SELECT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1714);
				match(K_SELECT);
				setState(1716);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,247,_ctx) ) {
				case 1:
					{
					setState(1715);
					_la = _input.LA(1);
					if ( !(_la==K_ALL || _la==K_DISTINCT) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					break;
				}
				setState(1718);
				result_column();
				setState(1723);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(1719);
					match(COMMA);
					setState(1720);
					result_column();
					}
					}
					setState(1725);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1738);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_FROM) {
					{
					setState(1726);
					match(K_FROM);
					setState(1736);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,250,_ctx) ) {
					case 1:
						{
						setState(1727);
						table_or_subquery();
						setState(1732);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==COMMA) {
							{
							{
							setState(1728);
							match(COMMA);
							setState(1729);
							table_or_subquery();
							}
							}
							setState(1734);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						}
						break;
					case 2:
						{
						setState(1735);
						join_clause();
						}
						break;
					}
					}
				}

				setState(1742);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_WHERE) {
					{
					setState(1740);
					match(K_WHERE);
					setState(1741);
					expr(0);
					}
				}

				setState(1758);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==K_GROUP) {
					{
					setState(1744);
					match(K_GROUP);
					setState(1745);
					match(K_BY);
					setState(1746);
					expr(0);
					setState(1751);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(1747);
						match(COMMA);
						setState(1748);
						expr(0);
						}
						}
						setState(1753);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(1756);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==K_HAVING) {
						{
						setState(1754);
						match(K_HAVING);
						setState(1755);
						expr(0);
						}
					}

					}
				}

				}
				break;
			case K_VALUES:
				enterOuterAlt(_localctx, 2);
				{
				setState(1760);
				match(K_VALUES);
				setState(1761);
				match(OPEN_PAR);
				setState(1762);
				expr(0);
				setState(1767);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(1763);
					match(COMMA);
					setState(1764);
					expr(0);
					}
					}
					setState(1769);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1770);
				match(CLOSE_PAR);
				setState(1785);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(1771);
					match(COMMA);
					setState(1772);
					match(OPEN_PAR);
					setState(1773);
					expr(0);
					setState(1778);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(1774);
						match(COMMA);
						setState(1775);
						expr(0);
						}
						}
						setState(1780);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(1781);
					match(CLOSE_PAR);
					}
					}
					setState(1787);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Compound_operatorContext extends ParserRuleContext {
		public TerminalNode K_UNION() { return getToken(SQLiteParser.K_UNION, 0); }
		public TerminalNode K_ALL() { return getToken(SQLiteParser.K_ALL, 0); }
		public TerminalNode K_INTERSECT() { return getToken(SQLiteParser.K_INTERSECT, 0); }
		public TerminalNode K_EXCEPT() { return getToken(SQLiteParser.K_EXCEPT, 0); }
		public Compound_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compound_operator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterCompound_operator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitCompound_operator(this);
		}
	}

	public final Compound_operatorContext compound_operator() throws RecognitionException {
		Compound_operatorContext _localctx = new Compound_operatorContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_compound_operator);
		try {
			setState(1795);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,260,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1790);
				match(K_UNION);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1791);
				match(K_UNION);
				setState(1792);
				match(K_ALL);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1793);
				match(K_INTERSECT);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1794);
				match(K_EXCEPT);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Signed_numberContext extends ParserRuleContext {
		public TerminalNode NUMERIC_LITERAL() { return getToken(SQLiteParser.NUMERIC_LITERAL, 0); }
		public Signed_numberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_signed_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterSigned_number(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitSigned_number(this);
		}
	}

	public final Signed_numberContext signed_number() throws RecognitionException {
		Signed_numberContext _localctx = new Signed_numberContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_signed_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1798);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLUS || _la==MINUS) {
				{
				setState(1797);
				_la = _input.LA(1);
				if ( !(_la==PLUS || _la==MINUS) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(1800);
			match(NUMERIC_LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Literal_valueContext extends ParserRuleContext {
		public TerminalNode NUMERIC_LITERAL() { return getToken(SQLiteParser.NUMERIC_LITERAL, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(SQLiteParser.STRING_LITERAL, 0); }
		public TerminalNode BLOB_LITERAL() { return getToken(SQLiteParser.BLOB_LITERAL, 0); }
		public TerminalNode K_NULL() { return getToken(SQLiteParser.K_NULL, 0); }
		public TerminalNode K_CURRENT_TIME() { return getToken(SQLiteParser.K_CURRENT_TIME, 0); }
		public TerminalNode K_CURRENT_DATE() { return getToken(SQLiteParser.K_CURRENT_DATE, 0); }
		public TerminalNode K_CURRENT_TIMESTAMP() { return getToken(SQLiteParser.K_CURRENT_TIMESTAMP, 0); }
		public Literal_valueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterLiteral_value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitLiteral_value(this);
		}
	}

	public final Literal_valueContext literal_value() throws RecognitionException {
		Literal_valueContext _localctx = new Literal_valueContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_literal_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1802);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << K_CURRENT_DATE) | (1L << K_CURRENT_TIME) | (1L << K_CURRENT_TIMESTAMP))) != 0) || ((((_la - 105)) & ~0x3f) == 0 && ((1L << (_la - 105)) & ((1L << (K_NULL - 105)) | (1L << (NUMERIC_LITERAL - 105)) | (1L << (STRING_LITERAL - 105)) | (1L << (BLOB_LITERAL - 105)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Unary_operatorContext extends ParserRuleContext {
		public TerminalNode K_NOT() { return getToken(SQLiteParser.K_NOT, 0); }
		public Unary_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unary_operator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterUnary_operator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitUnary_operator(this);
		}
	}

	public final Unary_operatorContext unary_operator() throws RecognitionException {
		Unary_operatorContext _localctx = new Unary_operatorContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_unary_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1804);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PLUS) | (1L << MINUS) | (1L << TILDE))) != 0) || _la==K_NOT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Error_messageContext extends ParserRuleContext {
		public TerminalNode STRING_LITERAL() { return getToken(SQLiteParser.STRING_LITERAL, 0); }
		public Error_messageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_error_message; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterError_message(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitError_message(this);
		}
	}

	public final Error_messageContext error_message() throws RecognitionException {
		Error_messageContext _localctx = new Error_messageContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_error_message);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1806);
			match(STRING_LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Module_argumentContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Column_defContext column_def() {
			return getRuleContext(Column_defContext.class,0);
		}
		public Module_argumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_module_argument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterModule_argument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitModule_argument(this);
		}
	}

	public final Module_argumentContext module_argument() throws RecognitionException {
		Module_argumentContext _localctx = new Module_argumentContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_module_argument);
		try {
			setState(1810);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,262,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1808);
				expr(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1809);
				column_def();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Column_aliasContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SQLiteParser.IDENTIFIER, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(SQLiteParser.STRING_LITERAL, 0); }
		public Column_aliasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column_alias; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterColumn_alias(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitColumn_alias(this);
		}
	}

	public final Column_aliasContext column_alias() throws RecognitionException {
		Column_aliasContext _localctx = new Column_aliasContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_column_alias);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1812);
			_la = _input.LA(1);
			if ( !(_la==IDENTIFIER || _la==STRING_LITERAL) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KeywordContext extends ParserRuleContext {
		public TerminalNode K_ABORT() { return getToken(SQLiteParser.K_ABORT, 0); }
		public TerminalNode K_ACTION() { return getToken(SQLiteParser.K_ACTION, 0); }
		public TerminalNode K_ADD() { return getToken(SQLiteParser.K_ADD, 0); }
		public TerminalNode K_AFTER() { return getToken(SQLiteParser.K_AFTER, 0); }
		public TerminalNode K_ALL() { return getToken(SQLiteParser.K_ALL, 0); }
		public TerminalNode K_ALTER() { return getToken(SQLiteParser.K_ALTER, 0); }
		public TerminalNode K_ANALYZE() { return getToken(SQLiteParser.K_ANALYZE, 0); }
		public TerminalNode K_AND() { return getToken(SQLiteParser.K_AND, 0); }
		public TerminalNode K_AS() { return getToken(SQLiteParser.K_AS, 0); }
		public TerminalNode K_ASC() { return getToken(SQLiteParser.K_ASC, 0); }
		public TerminalNode K_ATTACH() { return getToken(SQLiteParser.K_ATTACH, 0); }
		public TerminalNode K_AUTOINCREMENT() { return getToken(SQLiteParser.K_AUTOINCREMENT, 0); }
		public TerminalNode K_BEFORE() { return getToken(SQLiteParser.K_BEFORE, 0); }
		public TerminalNode K_BEGIN() { return getToken(SQLiteParser.K_BEGIN, 0); }
		public TerminalNode K_BETWEEN() { return getToken(SQLiteParser.K_BETWEEN, 0); }
		public TerminalNode K_BY() { return getToken(SQLiteParser.K_BY, 0); }
		public TerminalNode K_CASCADE() { return getToken(SQLiteParser.K_CASCADE, 0); }
		public TerminalNode K_CASE() { return getToken(SQLiteParser.K_CASE, 0); }
		public TerminalNode K_CAST() { return getToken(SQLiteParser.K_CAST, 0); }
		public TerminalNode K_CHECK() { return getToken(SQLiteParser.K_CHECK, 0); }
		public TerminalNode K_COLLATE() { return getToken(SQLiteParser.K_COLLATE, 0); }
		public TerminalNode K_COLUMN() { return getToken(SQLiteParser.K_COLUMN, 0); }
		public TerminalNode K_COMMIT() { return getToken(SQLiteParser.K_COMMIT, 0); }
		public TerminalNode K_CONFLICT() { return getToken(SQLiteParser.K_CONFLICT, 0); }
		public TerminalNode K_CONSTRAINT() { return getToken(SQLiteParser.K_CONSTRAINT, 0); }
		public TerminalNode K_CREATE() { return getToken(SQLiteParser.K_CREATE, 0); }
		public TerminalNode K_CROSS() { return getToken(SQLiteParser.K_CROSS, 0); }
		public TerminalNode K_CURRENT_DATE() { return getToken(SQLiteParser.K_CURRENT_DATE, 0); }
		public TerminalNode K_CURRENT_TIME() { return getToken(SQLiteParser.K_CURRENT_TIME, 0); }
		public TerminalNode K_CURRENT_TIMESTAMP() { return getToken(SQLiteParser.K_CURRENT_TIMESTAMP, 0); }
		public TerminalNode K_DATABASE() { return getToken(SQLiteParser.K_DATABASE, 0); }
		public TerminalNode K_DEFAULT() { return getToken(SQLiteParser.K_DEFAULT, 0); }
		public TerminalNode K_DEFERRABLE() { return getToken(SQLiteParser.K_DEFERRABLE, 0); }
		public TerminalNode K_DEFERRED() { return getToken(SQLiteParser.K_DEFERRED, 0); }
		public TerminalNode K_DELETE() { return getToken(SQLiteParser.K_DELETE, 0); }
		public TerminalNode K_DESC() { return getToken(SQLiteParser.K_DESC, 0); }
		public TerminalNode K_DETACH() { return getToken(SQLiteParser.K_DETACH, 0); }
		public TerminalNode K_DISTINCT() { return getToken(SQLiteParser.K_DISTINCT, 0); }
		public TerminalNode K_DROP() { return getToken(SQLiteParser.K_DROP, 0); }
		public TerminalNode K_EACH() { return getToken(SQLiteParser.K_EACH, 0); }
		public TerminalNode K_ELSE() { return getToken(SQLiteParser.K_ELSE, 0); }
		public TerminalNode K_END() { return getToken(SQLiteParser.K_END, 0); }
		public TerminalNode K_ESCAPE() { return getToken(SQLiteParser.K_ESCAPE, 0); }
		public TerminalNode K_EXCEPT() { return getToken(SQLiteParser.K_EXCEPT, 0); }
		public TerminalNode K_EXCLUSIVE() { return getToken(SQLiteParser.K_EXCLUSIVE, 0); }
		public TerminalNode K_EXISTS() { return getToken(SQLiteParser.K_EXISTS, 0); }
		public TerminalNode K_EXPLAIN() { return getToken(SQLiteParser.K_EXPLAIN, 0); }
		public TerminalNode K_FAIL() { return getToken(SQLiteParser.K_FAIL, 0); }
		public TerminalNode K_FOR() { return getToken(SQLiteParser.K_FOR, 0); }
		public TerminalNode K_FOREIGN() { return getToken(SQLiteParser.K_FOREIGN, 0); }
		public TerminalNode K_FROM() { return getToken(SQLiteParser.K_FROM, 0); }
		public TerminalNode K_FULL() { return getToken(SQLiteParser.K_FULL, 0); }
		public TerminalNode K_GLOB() { return getToken(SQLiteParser.K_GLOB, 0); }
		public TerminalNode K_GROUP() { return getToken(SQLiteParser.K_GROUP, 0); }
		public TerminalNode K_HAVING() { return getToken(SQLiteParser.K_HAVING, 0); }
		public TerminalNode K_IF() { return getToken(SQLiteParser.K_IF, 0); }
		public TerminalNode K_IGNORE() { return getToken(SQLiteParser.K_IGNORE, 0); }
		public TerminalNode K_IMMEDIATE() { return getToken(SQLiteParser.K_IMMEDIATE, 0); }
		public TerminalNode K_IN() { return getToken(SQLiteParser.K_IN, 0); }
		public TerminalNode K_INDEX() { return getToken(SQLiteParser.K_INDEX, 0); }
		public TerminalNode K_INDEXED() { return getToken(SQLiteParser.K_INDEXED, 0); }
		public TerminalNode K_INITIALLY() { return getToken(SQLiteParser.K_INITIALLY, 0); }
		public TerminalNode K_INNER() { return getToken(SQLiteParser.K_INNER, 0); }
		public TerminalNode K_INSERT() { return getToken(SQLiteParser.K_INSERT, 0); }
		public TerminalNode K_INSTEAD() { return getToken(SQLiteParser.K_INSTEAD, 0); }
		public TerminalNode K_INTERSECT() { return getToken(SQLiteParser.K_INTERSECT, 0); }
		public TerminalNode K_INTO() { return getToken(SQLiteParser.K_INTO, 0); }
		public TerminalNode K_IS() { return getToken(SQLiteParser.K_IS, 0); }
		public TerminalNode K_ISNULL() { return getToken(SQLiteParser.K_ISNULL, 0); }
		public TerminalNode K_JOIN() { return getToken(SQLiteParser.K_JOIN, 0); }
		public TerminalNode K_KEY() { return getToken(SQLiteParser.K_KEY, 0); }
		public TerminalNode K_LEFT() { return getToken(SQLiteParser.K_LEFT, 0); }
		public TerminalNode K_LIKE() { return getToken(SQLiteParser.K_LIKE, 0); }
		public TerminalNode K_LIMIT() { return getToken(SQLiteParser.K_LIMIT, 0); }
		public TerminalNode K_MATCH() { return getToken(SQLiteParser.K_MATCH, 0); }
		public TerminalNode K_NATURAL() { return getToken(SQLiteParser.K_NATURAL, 0); }
		public TerminalNode K_NO() { return getToken(SQLiteParser.K_NO, 0); }
		public TerminalNode K_NOT() { return getToken(SQLiteParser.K_NOT, 0); }
		public TerminalNode K_NOTNULL() { return getToken(SQLiteParser.K_NOTNULL, 0); }
		public TerminalNode K_NULL() { return getToken(SQLiteParser.K_NULL, 0); }
		public TerminalNode K_OF() { return getToken(SQLiteParser.K_OF, 0); }
		public TerminalNode K_OFFSET() { return getToken(SQLiteParser.K_OFFSET, 0); }
		public TerminalNode K_ON() { return getToken(SQLiteParser.K_ON, 0); }
		public TerminalNode K_OR() { return getToken(SQLiteParser.K_OR, 0); }
		public TerminalNode K_ORDER() { return getToken(SQLiteParser.K_ORDER, 0); }
		public TerminalNode K_OUTER() { return getToken(SQLiteParser.K_OUTER, 0); }
		public TerminalNode K_PLAN() { return getToken(SQLiteParser.K_PLAN, 0); }
		public TerminalNode K_PRAGMA() { return getToken(SQLiteParser.K_PRAGMA, 0); }
		public TerminalNode K_PRIMARY() { return getToken(SQLiteParser.K_PRIMARY, 0); }
		public TerminalNode K_QUERY() { return getToken(SQLiteParser.K_QUERY, 0); }
		public TerminalNode K_RAISE() { return getToken(SQLiteParser.K_RAISE, 0); }
		public TerminalNode K_RECURSIVE() { return getToken(SQLiteParser.K_RECURSIVE, 0); }
		public TerminalNode K_REFERENCES() { return getToken(SQLiteParser.K_REFERENCES, 0); }
		public TerminalNode K_REGEXP() { return getToken(SQLiteParser.K_REGEXP, 0); }
		public TerminalNode K_REINDEX() { return getToken(SQLiteParser.K_REINDEX, 0); }
		public TerminalNode K_RELEASE() { return getToken(SQLiteParser.K_RELEASE, 0); }
		public TerminalNode K_RENAME() { return getToken(SQLiteParser.K_RENAME, 0); }
		public TerminalNode K_REPLACE() { return getToken(SQLiteParser.K_REPLACE, 0); }
		public TerminalNode K_RESTRICT() { return getToken(SQLiteParser.K_RESTRICT, 0); }
		public TerminalNode K_RIGHT() { return getToken(SQLiteParser.K_RIGHT, 0); }
		public TerminalNode K_ROLLBACK() { return getToken(SQLiteParser.K_ROLLBACK, 0); }
		public TerminalNode K_ROW() { return getToken(SQLiteParser.K_ROW, 0); }
		public TerminalNode K_SAVEPOINT() { return getToken(SQLiteParser.K_SAVEPOINT, 0); }
		public TerminalNode K_SELECT() { return getToken(SQLiteParser.K_SELECT, 0); }
		public TerminalNode K_SET() { return getToken(SQLiteParser.K_SET, 0); }
		public TerminalNode K_TABLE() { return getToken(SQLiteParser.K_TABLE, 0); }
		public TerminalNode K_TEMP() { return getToken(SQLiteParser.K_TEMP, 0); }
		public TerminalNode K_TEMPORARY() { return getToken(SQLiteParser.K_TEMPORARY, 0); }
		public TerminalNode K_THEN() { return getToken(SQLiteParser.K_THEN, 0); }
		public TerminalNode K_TO() { return getToken(SQLiteParser.K_TO, 0); }
		public TerminalNode K_TRANSACTION() { return getToken(SQLiteParser.K_TRANSACTION, 0); }
		public TerminalNode K_TRIGGER() { return getToken(SQLiteParser.K_TRIGGER, 0); }
		public TerminalNode K_UNION() { return getToken(SQLiteParser.K_UNION, 0); }
		public TerminalNode K_UNIQUE() { return getToken(SQLiteParser.K_UNIQUE, 0); }
		public TerminalNode K_UPDATE() { return getToken(SQLiteParser.K_UPDATE, 0); }
		public TerminalNode K_USING() { return getToken(SQLiteParser.K_USING, 0); }
		public TerminalNode K_VACUUM() { return getToken(SQLiteParser.K_VACUUM, 0); }
		public TerminalNode K_VALUES() { return getToken(SQLiteParser.K_VALUES, 0); }
		public TerminalNode K_VIEW() { return getToken(SQLiteParser.K_VIEW, 0); }
		public TerminalNode K_VIRTUAL() { return getToken(SQLiteParser.K_VIRTUAL, 0); }
		public TerminalNode K_WHEN() { return getToken(SQLiteParser.K_WHEN, 0); }
		public TerminalNode K_WHERE() { return getToken(SQLiteParser.K_WHERE, 0); }
		public TerminalNode K_WITH() { return getToken(SQLiteParser.K_WITH, 0); }
		public TerminalNode K_WITHOUT() { return getToken(SQLiteParser.K_WITHOUT, 0); }
		public TerminalNode K_PARTITION() { return getToken(SQLiteParser.K_PARTITION, 0); }
		public TerminalNode K_SKIP() { return getToken(SQLiteParser.K_SKIP, 0); }
		public TerminalNode K_OVER() { return getToken(SQLiteParser.K_OVER, 0); }
		public TerminalNode K_COLLECTIVE() { return getToken(SQLiteParser.K_COLLECTIVE, 0); }
		public TerminalNode K_WITHIN() { return getToken(SQLiteParser.K_WITHIN, 0); }
		public TerminalNode K_DISTANCE() { return getToken(SQLiteParser.K_DISTANCE, 0); }
		public TerminalNode K_CONTAINS() { return getToken(SQLiteParser.K_CONTAINS, 0); }
		public TerminalNode K_COLLECT() { return getToken(SQLiteParser.K_COLLECT, 0); }
		public TerminalNode K_OVERLAP() { return getToken(SQLiteParser.K_OVERLAP, 0); }
		public KeywordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keyword; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterKeyword(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitKeyword(this);
		}
	}

	public final KeywordContext keyword() throws RecognitionException {
		KeywordContext _localctx = new KeywordContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_keyword);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1814);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << K_ABORT) | (1L << K_ACTION) | (1L << K_ADD) | (1L << K_AFTER) | (1L << K_ALL) | (1L << K_ALTER) | (1L << K_ANALYZE) | (1L << K_AND) | (1L << K_AS) | (1L << K_ASC) | (1L << K_ATTACH) | (1L << K_AUTOINCREMENT) | (1L << K_BEFORE) | (1L << K_BEGIN) | (1L << K_BETWEEN) | (1L << K_BY) | (1L << K_CASCADE) | (1L << K_CASE) | (1L << K_CAST) | (1L << K_CHECK) | (1L << K_COLLATE) | (1L << K_COLUMN) | (1L << K_COMMIT) | (1L << K_CONFLICT) | (1L << K_CONSTRAINT) | (1L << K_CREATE) | (1L << K_CROSS) | (1L << K_CURRENT_DATE) | (1L << K_CURRENT_TIME) | (1L << K_CURRENT_TIMESTAMP) | (1L << K_DATABASE) | (1L << K_DEFAULT) | (1L << K_DEFERRABLE) | (1L << K_DEFERRED) | (1L << K_DELETE) | (1L << K_DESC) | (1L << K_DETACH) | (1L << K_DISTINCT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (K_DROP - 64)) | (1L << (K_EACH - 64)) | (1L << (K_ELSE - 64)) | (1L << (K_END - 64)) | (1L << (K_ESCAPE - 64)) | (1L << (K_EXCEPT - 64)) | (1L << (K_EXCLUSIVE - 64)) | (1L << (K_EXISTS - 64)) | (1L << (K_EXPLAIN - 64)) | (1L << (K_FAIL - 64)) | (1L << (K_FOR - 64)) | (1L << (K_FOREIGN - 64)) | (1L << (K_FROM - 64)) | (1L << (K_FULL - 64)) | (1L << (K_GLOB - 64)) | (1L << (K_GROUP - 64)) | (1L << (K_HAVING - 64)) | (1L << (K_IF - 64)) | (1L << (K_IGNORE - 64)) | (1L << (K_IMMEDIATE - 64)) | (1L << (K_IN - 64)) | (1L << (K_INDEX - 64)) | (1L << (K_INDEXED - 64)) | (1L << (K_INITIALLY - 64)) | (1L << (K_INNER - 64)) | (1L << (K_INSERT - 64)) | (1L << (K_INSTEAD - 64)) | (1L << (K_INTERSECT - 64)) | (1L << (K_INTO - 64)) | (1L << (K_IS - 64)) | (1L << (K_ISNULL - 64)) | (1L << (K_JOIN - 64)) | (1L << (K_KEY - 64)) | (1L << (K_LEFT - 64)) | (1L << (K_LIKE - 64)) | (1L << (K_LIMIT - 64)) | (1L << (K_MATCH - 64)) | (1L << (K_NATURAL - 64)) | (1L << (K_NO - 64)) | (1L << (K_NOT - 64)) | (1L << (K_NOTNULL - 64)) | (1L << (K_NULL - 64)) | (1L << (K_OF - 64)) | (1L << (K_OFFSET - 64)) | (1L << (K_ON - 64)) | (1L << (K_OR - 64)) | (1L << (K_ORDER - 64)) | (1L << (K_OUTER - 64)) | (1L << (K_PLAN - 64)) | (1L << (K_PRAGMA - 64)) | (1L << (K_PRIMARY - 64)) | (1L << (K_QUERY - 64)) | (1L << (K_RAISE - 64)) | (1L << (K_RECURSIVE - 64)) | (1L << (K_REFERENCES - 64)) | (1L << (K_REGEXP - 64)) | (1L << (K_REINDEX - 64)) | (1L << (K_RELEASE - 64)) | (1L << (K_RENAME - 64)) | (1L << (K_REPLACE - 64)) | (1L << (K_RESTRICT - 64)) | (1L << (K_RIGHT - 64)) | (1L << (K_ROLLBACK - 64)) | (1L << (K_ROW - 64)))) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & ((1L << (K_SAVEPOINT - 128)) | (1L << (K_SELECT - 128)) | (1L << (K_SET - 128)) | (1L << (K_TABLE - 128)) | (1L << (K_TEMP - 128)) | (1L << (K_TEMPORARY - 128)) | (1L << (K_THEN - 128)) | (1L << (K_TO - 128)) | (1L << (K_TRANSACTION - 128)) | (1L << (K_TRIGGER - 128)) | (1L << (K_UNION - 128)) | (1L << (K_UNIQUE - 128)) | (1L << (K_UPDATE - 128)) | (1L << (K_USING - 128)) | (1L << (K_VACUUM - 128)) | (1L << (K_VALUES - 128)) | (1L << (K_VIEW - 128)) | (1L << (K_VIRTUAL - 128)) | (1L << (K_WHEN - 128)) | (1L << (K_WHERE - 128)) | (1L << (K_WITH - 128)) | (1L << (K_WITHOUT - 128)) | (1L << (K_PARTITION - 128)) | (1L << (K_SKIP - 128)) | (1L << (K_OVER - 128)) | (1L << (K_COLLECTIVE - 128)) | (1L << (K_WITHIN - 128)) | (1L << (K_DISTANCE - 128)) | (1L << (K_CONTAINS - 128)) | (1L << (K_COLLECT - 128)) | (1L << (K_OVERLAP - 128)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitName(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1816);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Function_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Function_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterFunction_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitFunction_name(this);
		}
	}

	public final Function_nameContext function_name() throws RecognitionException {
		Function_nameContext _localctx = new Function_nameContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1818);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Database_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Database_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_database_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterDatabase_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitDatabase_name(this);
		}
	}

	public final Database_nameContext database_name() throws RecognitionException {
		Database_nameContext _localctx = new Database_nameContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_database_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1820);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Schema_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Schema_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schema_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterSchema_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitSchema_name(this);
		}
	}

	public final Schema_nameContext schema_name() throws RecognitionException {
		Schema_nameContext _localctx = new Schema_nameContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_schema_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1822);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Table_function_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Table_function_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_function_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterTable_function_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitTable_function_name(this);
		}
	}

	public final Table_function_nameContext table_function_name() throws RecognitionException {
		Table_function_nameContext _localctx = new Table_function_nameContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_table_function_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1824);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Table_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Table_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterTable_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitTable_name(this);
		}
	}

	public final Table_nameContext table_name() throws RecognitionException {
		Table_nameContext _localctx = new Table_nameContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_table_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1826);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Table_or_index_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Table_or_index_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_or_index_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterTable_or_index_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitTable_or_index_name(this);
		}
	}

	public final Table_or_index_nameContext table_or_index_name() throws RecognitionException {
		Table_or_index_nameContext _localctx = new Table_or_index_nameContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_table_or_index_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1828);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class New_table_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public New_table_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_new_table_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterNew_table_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitNew_table_name(this);
		}
	}

	public final New_table_nameContext new_table_name() throws RecognitionException {
		New_table_nameContext _localctx = new New_table_nameContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_new_table_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1830);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Column_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Column_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterColumn_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitColumn_name(this);
		}
	}

	public final Column_nameContext column_name() throws RecognitionException {
		Column_nameContext _localctx = new Column_nameContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_column_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1832);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Collation_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Collation_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_collation_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterCollation_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitCollation_name(this);
		}
	}

	public final Collation_nameContext collation_name() throws RecognitionException {
		Collation_nameContext _localctx = new Collation_nameContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_collation_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1834);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Foreign_tableContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Foreign_tableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_foreign_table; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterForeign_table(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitForeign_table(this);
		}
	}

	public final Foreign_tableContext foreign_table() throws RecognitionException {
		Foreign_tableContext _localctx = new Foreign_tableContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_foreign_table);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1836);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Index_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Index_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_index_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterIndex_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitIndex_name(this);
		}
	}

	public final Index_nameContext index_name() throws RecognitionException {
		Index_nameContext _localctx = new Index_nameContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_index_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1838);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Trigger_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Trigger_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_trigger_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterTrigger_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitTrigger_name(this);
		}
	}

	public final Trigger_nameContext trigger_name() throws RecognitionException {
		Trigger_nameContext _localctx = new Trigger_nameContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_trigger_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1840);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class View_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public View_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_view_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterView_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitView_name(this);
		}
	}

	public final View_nameContext view_name() throws RecognitionException {
		View_nameContext _localctx = new View_nameContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_view_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1842);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Module_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Module_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_module_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterModule_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitModule_name(this);
		}
	}

	public final Module_nameContext module_name() throws RecognitionException {
		Module_nameContext _localctx = new Module_nameContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_module_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1844);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pragma_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Pragma_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pragma_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterPragma_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitPragma_name(this);
		}
	}

	public final Pragma_nameContext pragma_name() throws RecognitionException {
		Pragma_nameContext _localctx = new Pragma_nameContext(_ctx, getState());
		enterRule(_localctx, 168, RULE_pragma_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1846);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Savepoint_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Savepoint_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_savepoint_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterSavepoint_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitSavepoint_name(this);
		}
	}

	public final Savepoint_nameContext savepoint_name() throws RecognitionException {
		Savepoint_nameContext _localctx = new Savepoint_nameContext(_ctx, getState());
		enterRule(_localctx, 170, RULE_savepoint_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1848);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Table_aliasContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SQLiteParser.IDENTIFIER, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(SQLiteParser.STRING_LITERAL, 0); }
		public Table_aliasContext table_alias() {
			return getRuleContext(Table_aliasContext.class,0);
		}
		public Table_aliasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_table_alias; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterTable_alias(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitTable_alias(this);
		}
	}

	public final Table_aliasContext table_alias() throws RecognitionException {
		Table_aliasContext _localctx = new Table_aliasContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_table_alias);
		try {
			setState(1856);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(1850);
				match(IDENTIFIER);
				}
				break;
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(1851);
				match(STRING_LITERAL);
				}
				break;
			case OPEN_PAR:
				enterOuterAlt(_localctx, 3);
				{
				setState(1852);
				match(OPEN_PAR);
				setState(1853);
				table_alias();
				setState(1854);
				match(CLOSE_PAR);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Transaction_nameContext extends ParserRuleContext {
		public Any_nameContext any_name() {
			return getRuleContext(Any_nameContext.class,0);
		}
		public Transaction_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_transaction_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterTransaction_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitTransaction_name(this);
		}
	}

	public final Transaction_nameContext transaction_name() throws RecognitionException {
		Transaction_nameContext _localctx = new Transaction_nameContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_transaction_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1858);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class File_readContext extends ParserRuleContext {
		public List<Any_nameContext> any_name() {
			return getRuleContexts(Any_nameContext.class);
		}
		public Any_nameContext any_name(int i) {
			return getRuleContext(Any_nameContext.class,i);
		}
		public File_readContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file_read; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterFile_read(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitFile_read(this);
		}
	}

	public final File_readContext file_read() throws RecognitionException {
		File_readContext _localctx = new File_readContext(_ctx, getState());
		enterRule(_localctx, 176, RULE_file_read);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1860);
			any_name(0);
			setState(1861);
			match(DOT);
			setState(1862);
			any_name(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Inner_loopContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SQLiteParser.IDENTIFIER, 0); }
		public List<Any_nameContext> any_name() {
			return getRuleContexts(Any_nameContext.class);
		}
		public Any_nameContext any_name(int i) {
			return getRuleContext(Any_nameContext.class,i);
		}
		public List<Inner_loopContext> inner_loop() {
			return getRuleContexts(Inner_loopContext.class);
		}
		public Inner_loopContext inner_loop(int i) {
			return getRuleContext(Inner_loopContext.class,i);
		}
		public Inner_loopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inner_loop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterInner_loop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitInner_loop(this);
		}
	}

	public final Inner_loopContext inner_loop() throws RecognitionException {
		return inner_loop(0);
	}

	private Inner_loopContext inner_loop(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Inner_loopContext _localctx = new Inner_loopContext(_ctx, _parentState);
		Inner_loopContext _prevctx = _localctx;
		int _startState = 178;
		enterRecursionRule(_localctx, 178, RULE_inner_loop, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1872);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,264,_ctx) ) {
			case 1:
				{
				setState(1865);
				match(IDENTIFIER);
				}
				break;
			case 2:
				{
				setState(1866);
				any_name(0);
				setState(1867);
				match(DOT);
				setState(1868);
				any_name(0);
				setState(1869);
				match(COMMA);
				setState(1870);
				any_name(0);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(1879);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,265,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Inner_loopContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_inner_loop);
					setState(1874);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(1875);
					match(COMMA);
					setState(1876);
					inner_loop(3);
					}
					} 
				}
				setState(1881);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,265,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Any_nameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SQLiteParser.IDENTIFIER, 0); }
		public KeywordContext keyword() {
			return getRuleContext(KeywordContext.class,0);
		}
		public TerminalNode STRING_LITERAL() { return getToken(SQLiteParser.STRING_LITERAL, 0); }
		public List<Any_nameContext> any_name() {
			return getRuleContexts(Any_nameContext.class);
		}
		public Any_nameContext any_name(int i) {
			return getRuleContext(Any_nameContext.class,i);
		}
		public Any_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_any_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterAny_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitAny_name(this);
		}
	}

	public final Any_nameContext any_name() throws RecognitionException {
		return any_name(0);
	}

	private Any_nameContext any_name(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Any_nameContext _localctx = new Any_nameContext(_ctx, _parentState);
		Any_nameContext _prevctx = _localctx;
		int _startState = 180;
		enterRecursionRule(_localctx, 180, RULE_any_name, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1894);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				{
				setState(1883);
				match(IDENTIFIER);
				}
				break;
			case K_ABORT:
			case K_ACTION:
			case K_ADD:
			case K_AFTER:
			case K_ALL:
			case K_ALTER:
			case K_ANALYZE:
			case K_AND:
			case K_AS:
			case K_ASC:
			case K_ATTACH:
			case K_AUTOINCREMENT:
			case K_BEFORE:
			case K_BEGIN:
			case K_BETWEEN:
			case K_BY:
			case K_CASCADE:
			case K_CASE:
			case K_CAST:
			case K_CHECK:
			case K_COLLATE:
			case K_COLUMN:
			case K_COMMIT:
			case K_CONFLICT:
			case K_CONSTRAINT:
			case K_CREATE:
			case K_CROSS:
			case K_CURRENT_DATE:
			case K_CURRENT_TIME:
			case K_CURRENT_TIMESTAMP:
			case K_DATABASE:
			case K_DEFAULT:
			case K_DEFERRABLE:
			case K_DEFERRED:
			case K_DELETE:
			case K_DESC:
			case K_DETACH:
			case K_DISTINCT:
			case K_DROP:
			case K_EACH:
			case K_ELSE:
			case K_END:
			case K_ESCAPE:
			case K_EXCEPT:
			case K_EXCLUSIVE:
			case K_EXISTS:
			case K_EXPLAIN:
			case K_FAIL:
			case K_FOR:
			case K_FOREIGN:
			case K_FROM:
			case K_FULL:
			case K_GLOB:
			case K_GROUP:
			case K_HAVING:
			case K_IF:
			case K_IGNORE:
			case K_IMMEDIATE:
			case K_IN:
			case K_INDEX:
			case K_INDEXED:
			case K_INITIALLY:
			case K_INNER:
			case K_INSERT:
			case K_INSTEAD:
			case K_INTERSECT:
			case K_INTO:
			case K_IS:
			case K_ISNULL:
			case K_JOIN:
			case K_KEY:
			case K_LEFT:
			case K_LIKE:
			case K_LIMIT:
			case K_MATCH:
			case K_NATURAL:
			case K_NO:
			case K_NOT:
			case K_NOTNULL:
			case K_NULL:
			case K_OF:
			case K_OFFSET:
			case K_ON:
			case K_OR:
			case K_ORDER:
			case K_OUTER:
			case K_PLAN:
			case K_PRAGMA:
			case K_PRIMARY:
			case K_QUERY:
			case K_RAISE:
			case K_RECURSIVE:
			case K_REFERENCES:
			case K_REGEXP:
			case K_REINDEX:
			case K_RELEASE:
			case K_RENAME:
			case K_REPLACE:
			case K_RESTRICT:
			case K_RIGHT:
			case K_ROLLBACK:
			case K_ROW:
			case K_SAVEPOINT:
			case K_SELECT:
			case K_SET:
			case K_TABLE:
			case K_TEMP:
			case K_TEMPORARY:
			case K_THEN:
			case K_TO:
			case K_TRANSACTION:
			case K_TRIGGER:
			case K_UNION:
			case K_UNIQUE:
			case K_UPDATE:
			case K_USING:
			case K_VACUUM:
			case K_VALUES:
			case K_VIEW:
			case K_VIRTUAL:
			case K_WHEN:
			case K_WHERE:
			case K_WITH:
			case K_WITHOUT:
			case K_PARTITION:
			case K_SKIP:
			case K_OVER:
			case K_COLLECTIVE:
			case K_WITHIN:
			case K_DISTANCE:
			case K_CONTAINS:
			case K_COLLECT:
			case K_OVERLAP:
				{
				setState(1884);
				keyword();
				}
				break;
			case STRING_LITERAL:
				{
				setState(1885);
				match(STRING_LITERAL);
				}
				break;
			case OPEN_PAR:
				{
				setState(1886);
				match(OPEN_PAR);
				setState(1887);
				any_name(0);
				setState(1888);
				match(CLOSE_PAR);
				}
				break;
			case T__0:
				{
				setState(1890);
				match(T__0);
				setState(1891);
				any_name(0);
				setState(1892);
				match(T__0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(1901);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,267,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Any_nameContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_any_name);
					setState(1896);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(1897);
					match(DOT);
					setState(1898);
					any_name(3);
					}
					} 
				}
				setState(1903);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,267,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class File_splitterContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SQLiteParser.IDENTIFIER, 0); }
		public KeywordContext keyword() {
			return getRuleContext(KeywordContext.class,0);
		}
		public TerminalNode STRING_LITERAL() { return getToken(SQLiteParser.STRING_LITERAL, 0); }
		public File_splitterContext file_splitter() {
			return getRuleContext(File_splitterContext.class,0);
		}
		public File_splitterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file_splitter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).enterFile_splitter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SQLiteListener ) ((SQLiteListener)listener).exitFile_splitter(this);
		}
	}

	public final File_splitterContext file_splitter() throws RecognitionException {
		return file_splitter(0);
	}

	private File_splitterContext file_splitter(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		File_splitterContext _localctx = new File_splitterContext(_ctx, _parentState);
		File_splitterContext _prevctx = _localctx;
		int _startState = 182;
		enterRecursionRule(_localctx, 182, RULE_file_splitter, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1914);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,268,_ctx) ) {
			case 1:
				{
				setState(1905);
				match(IDENTIFIER);
				}
				break;
			case 2:
				{
				setState(1906);
				keyword();
				}
				break;
			case 3:
				{
				setState(1907);
				match(STRING_LITERAL);
				}
				break;
			case 4:
				{
				setState(1908);
				match(COMMA);
				setState(1909);
				file_splitter(0);
				setState(1910);
				match(COMMA);
				}
				break;
			case 5:
				{
				setState(1912);
				match(COMMA);
				setState(1913);
				file_splitter(1);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(1920);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,269,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new File_splitterContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_file_splitter);
					setState(1916);
					if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
					setState(1917);
					match(COMMA);
					}
					} 
				}
				setState(1922);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,269,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 45:
			return expr_sempred((ExprContext)_localctx, predIndex);
		case 89:
			return inner_loop_sempred((Inner_loopContext)_localctx, predIndex);
		case 90:
			return any_name_sempred((Any_nameContext)_localctx, predIndex);
		case 91:
			return file_splitter_sempred((File_splitterContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 19);
		case 1:
			return precpred(_ctx, 18);
		case 2:
			return precpred(_ctx, 17);
		case 3:
			return precpred(_ctx, 16);
		case 4:
			return precpred(_ctx, 15);
		case 5:
			return precpred(_ctx, 14);
		case 6:
			return precpred(_ctx, 13);
		case 7:
			return precpred(_ctx, 12);
		case 8:
			return precpred(_ctx, 6);
		case 9:
			return precpred(_ctx, 5);
		case 10:
			return precpred(_ctx, 9);
		case 11:
			return precpred(_ctx, 8);
		case 12:
			return precpred(_ctx, 7);
		case 13:
			return precpred(_ctx, 4);
		}
		return true;
	}
	private boolean inner_loop_sempred(Inner_loopContext _localctx, int predIndex) {
		switch (predIndex) {
		case 14:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean any_name_sempred(Any_nameContext _localctx, int predIndex) {
		switch (predIndex) {
		case 15:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean file_splitter_sempred(File_splitterContext _localctx, int predIndex) {
		switch (predIndex) {
		case 16:
			return precpred(_ctx, 3);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\u00a9\u0786\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\3\2\3\2\7\2\u00bd"+
		"\n\2\f\2\16\2\u00c0\13\2\3\2\3\2\3\3\3\3\3\3\3\4\7\4\u00c8\n\4\f\4\16"+
		"\4\u00cb\13\4\3\4\3\4\6\4\u00cf\n\4\r\4\16\4\u00d0\3\4\7\4\u00d4\n\4\f"+
		"\4\16\4\u00d7\13\4\3\4\7\4\u00da\n\4\f\4\16\4\u00dd\13\4\3\5\3\5\3\5\5"+
		"\5\u00e2\n\5\5\5\u00e4\n\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\5\5\u0104\n\5\3\6\3\6\3\6\3\6\3\6\5\6\u010b\n\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\5\6\u0113\n\6\3\6\5\6\u0116\n\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5"+
		"\7\u011f\n\7\3\b\3\b\5\b\u0123\n\b\3\b\3\b\3\b\3\b\3\t\3\t\5\t\u012b\n"+
		"\t\3\t\3\t\5\t\u012f\n\t\5\t\u0131\n\t\3\n\3\n\3\n\5\n\u0136\n\n\5\n\u0138"+
		"\n\n\3\13\5\13\u013b\n\13\3\13\3\13\3\13\5\13\u0140\n\13\3\13\3\13\5\13"+
		"\u0144\n\13\3\13\6\13\u0147\n\13\r\13\16\13\u0148\3\13\3\13\3\13\3\13"+
		"\3\13\7\13\u0150\n\13\f\13\16\13\u0153\13\13\5\13\u0155\n\13\3\13\3\13"+
		"\3\13\3\13\5\13\u015b\n\13\5\13\u015d\n\13\3\f\3\f\5\f\u0161\n\f\3\f\3"+
		"\f\3\f\3\f\5\f\u0167\n\f\3\f\3\f\3\f\5\f\u016c\n\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\7\f\u0175\n\f\f\f\16\f\u0178\13\f\3\f\3\f\3\f\5\f\u017d\n\f\3"+
		"\r\3\r\5\r\u0181\n\r\3\r\3\r\3\r\3\r\5\r\u0187\n\r\3\r\3\r\3\r\5\r\u018c"+
		"\n\r\3\r\3\r\3\r\3\r\3\r\7\r\u0193\n\r\f\r\16\r\u0196\13\r\3\r\3\r\7\r"+
		"\u019a\n\r\f\r\16\r\u019d\13\r\3\r\3\r\3\r\5\r\u01a2\n\r\3\r\3\r\5\r\u01a6"+
		"\n\r\3\16\3\16\5\16\u01aa\n\16\3\16\3\16\3\16\3\16\5\16\u01b0\n\16\3\16"+
		"\3\16\3\16\5\16\u01b5\n\16\3\16\3\16\3\16\3\16\3\16\5\16\u01bc\n\16\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\16\7\16\u01c5\n\16\f\16\16\16\u01c8\13"+
		"\16\5\16\u01ca\n\16\5\16\u01cc\n\16\3\16\3\16\3\16\3\16\5\16\u01d2\n\16"+
		"\3\16\3\16\3\16\3\16\5\16\u01d8\n\16\3\16\3\16\5\16\u01dc\n\16\3\16\3"+
		"\16\3\16\3\16\3\16\5\16\u01e3\n\16\3\16\3\16\6\16\u01e7\n\16\r\16\16\16"+
		"\u01e8\3\16\3\16\3\17\3\17\5\17\u01ef\n\17\3\17\3\17\3\17\3\17\5\17\u01f5"+
		"\n\17\3\17\3\17\3\17\5\17\u01fa\n\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\5\20\u0206\n\20\3\20\3\20\3\20\5\20\u020b\n\20\3\20\3"+
		"\20\3\20\3\20\3\20\3\20\3\20\7\20\u0214\n\20\f\20\16\20\u0217\13\20\3"+
		"\20\3\20\5\20\u021b\n\20\3\21\5\21\u021e\n\21\3\21\3\21\3\21\3\21\3\21"+
		"\5\21\u0225\n\21\3\22\5\22\u0228\n\22\3\22\3\22\3\22\3\22\3\22\5\22\u022f"+
		"\n\22\3\22\3\22\3\22\3\22\3\22\7\22\u0236\n\22\f\22\16\22\u0239\13\22"+
		"\5\22\u023b\n\22\3\22\3\22\3\22\3\22\5\22\u0241\n\22\5\22\u0243\n\22\3"+
		"\23\3\23\5\23\u0247\n\23\3\23\3\23\3\24\3\24\3\24\3\24\5\24\u024f\n\24"+
		"\3\24\3\24\3\24\5\24\u0254\n\24\3\24\3\24\3\25\3\25\3\25\3\25\5\25\u025c"+
		"\n\25\3\25\3\25\3\25\5\25\u0261\n\25\3\25\3\25\3\26\3\26\3\26\3\26\5\26"+
		"\u0269\n\26\3\26\3\26\3\26\5\26\u026e\n\26\3\26\3\26\3\27\3\27\3\27\3"+
		"\27\5\27\u0276\n\27\3\27\3\27\3\27\5\27\u027b\n\27\3\27\3\27\3\30\5\30"+
		"\u0280\n\30\3\30\3\30\3\30\3\30\7\30\u0286\n\30\f\30\16\30\u0289\13\30"+
		"\3\30\3\30\3\30\3\30\3\30\7\30\u0290\n\30\f\30\16\30\u0293\13\30\5\30"+
		"\u0295\n\30\3\30\3\30\3\30\3\30\5\30\u029b\n\30\5\30\u029d\n\30\3\31\5"+
		"\31\u02a0\n\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31"+
		"\3\31\3\31\3\31\3\31\3\31\3\31\5\31\u02b3\n\31\3\31\3\31\3\31\3\31\5\31"+
		"\u02b9\n\31\3\31\3\31\3\31\3\31\3\31\7\31\u02c0\n\31\f\31\16\31\u02c3"+
		"\13\31\3\31\3\31\5\31\u02c7\n\31\3\31\3\31\3\31\3\31\3\31\7\31\u02ce\n"+
		"\31\f\31\16\31\u02d1\13\31\3\31\3\31\3\31\3\31\3\31\3\31\7\31\u02d9\n"+
		"\31\f\31\16\31\u02dc\13\31\3\31\3\31\7\31\u02e0\n\31\f\31\16\31\u02e3"+
		"\13\31\3\31\3\31\3\31\5\31\u02e8\n\31\3\32\3\32\3\32\3\32\5\32\u02ee\n"+
		"\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\5\32\u02f7\n\32\3\33\3\33\3\33"+
		"\3\33\3\33\5\33\u02fe\n\33\3\33\3\33\5\33\u0302\n\33\5\33\u0304\n\33\3"+
		"\34\3\34\5\34\u0308\n\34\3\34\3\34\3\35\3\35\3\35\5\35\u030f\n\35\5\35"+
		"\u0311\n\35\3\35\3\35\5\35\u0315\n\35\3\35\5\35\u0318\n\35\3\36\3\36\3"+
		"\36\3\37\5\37\u031e\n\37\3\37\3\37\3\37\3\37\3\37\3\37\7\37\u0326\n\37"+
		"\f\37\16\37\u0329\13\37\5\37\u032b\n\37\3\37\3\37\3\37\3\37\5\37\u0331"+
		"\n\37\5\37\u0333\n\37\3 \5 \u0336\n \3 \3 \3 \3 \7 \u033c\n \f \16 \u033f"+
		"\13 \3 \3 \3 \3 \3 \7 \u0346\n \f \16 \u0349\13 \5 \u034b\n \3 \3 \3 "+
		"\3 \3 \5 \u0352\n \5 \u0354\n \3 \3 \5 \u0358\n \3!\3!\5!\u035c\n!\3!"+
		"\3!\3!\7!\u0361\n!\f!\16!\u0364\13!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3"+
		"!\3!\5!\u0373\n!\3!\3!\3!\3!\3!\3!\3!\3!\7!\u037d\n!\f!\16!\u0380\13!"+
		"\5!\u0382\n!\5!\u0384\n!\3!\3!\5!\u0388\n!\3!\3!\3!\3!\7!\u038e\n!\f!"+
		"\16!\u0391\13!\3!\5!\u0394\n!\5!\u0396\n!\3!\3!\5!\u039a\n!\3!\3!\3!\3"+
		"!\3!\3!\3!\3!\3!\3!\5!\u03a6\n!\3!\3!\3!\3!\3!\5!\u03ad\n!\5!\u03af\n"+
		"!\3!\3!\5!\u03b3\n!\3!\3!\3!\3!\5!\u03b9\n!\3!\3!\3!\3!\3!\3!\3!\7!\u03c2"+
		"\n!\f!\16!\u03c5\13!\3!\3!\3!\3!\3!\5!\u03cc\n!\3!\3!\3!\3!\3!\5!\u03d3"+
		"\n!\5!\u03d5\n!\3!\3!\5!\u03d9\n!\3!\3!\7!\u03dd\n!\f!\16!\u03e0\13!\3"+
		"!\3!\5!\u03e4\n!\5!\u03e6\n!\3!\3!\3!\3!\3!\7!\u03ed\n!\f!\16!\u03f0\13"+
		"!\3!\3!\3!\3!\3!\3!\7!\u03f8\n!\f!\16!\u03fb\13!\3!\3!\7!\u03ff\n!\f!"+
		"\16!\u0402\13!\5!\u0404\n!\3\"\3\"\5\"\u0408\n\"\3\"\3\"\3\"\3\"\3\"\3"+
		"\"\3\"\3\"\3#\5#\u0413\n#\3#\3#\3#\3#\3#\3#\3#\3#\3#\3#\3#\5#\u0420\n"+
		"#\3#\3#\3#\3#\3#\3#\3#\3#\3#\3#\7#\u042c\n#\f#\16#\u042f\13#\3#\3#\5#"+
		"\u0433\n#\3$\5$\u0436\n$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\5$\u0443\n$"+
		"\3$\3$\3$\3$\3$\3$\3$\3$\3$\3$\7$\u044f\n$\f$\16$\u0452\13$\3$\3$\5$\u0456"+
		"\n$\3$\3$\3$\3$\3$\7$\u045d\n$\f$\16$\u0460\13$\5$\u0462\n$\3$\3$\3$\3"+
		"$\5$\u0468\n$\5$\u046a\n$\3%\3%\3&\3&\5&\u0470\n&\3&\7&\u0473\n&\f&\16"+
		"&\u0476\13&\3\'\6\'\u0479\n\'\r\'\16\'\u047a\3\'\3\'\3\'\3\'\3\'\3\'\3"+
		"\'\3\'\3\'\3\'\5\'\u0487\n\'\3(\3(\5(\u048b\n(\3(\3(\3(\5(\u0490\n(\3"+
		"(\3(\5(\u0494\n(\3(\5(\u0497\n(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3(\3"+
		"(\3(\3(\3(\5(\u04a9\n(\3(\3(\3(\5(\u04ae\n(\3)\3)\3)\5)\u04b3\n)\3*\3"+
		"*\5*\u04b7\n*\5*\u04b9\n*\3+\3+\3+\3+\3+\3+\3+\5+\u04c2\n+\3+\5+\u04c5"+
		"\n+\3+\3+\5+\u04c9\n+\3+\5+\u04cc\n+\5+\u04ce\n+\3,\3,\5,\u04d2\n,\5,"+
		"\u04d4\n,\3-\3-\3-\3-\3.\3.\3.\3.\7.\u04de\n.\f.\16.\u04e1\13.\3.\3.\3"+
		"/\3/\3/\3/\3/\3/\3/\3/\5/\u04ed\n/\3/\3/\3/\7/\u04f2\n/\f/\16/\u04f5\13"+
		"/\3/\5/\u04f8\n/\3/\3/\3/\3/\3/\5/\u04ff\n/\3/\3/\3/\5/\u0504\n/\3/\3"+
		"/\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/\5/\u0516\n/\3/\5/\u0519\n"+
		"/\3/\3/\3/\3/\3/\3/\5/\u0521\n/\3/\3/\3/\3/\3/\6/\u0528\n/\r/\16/\u0529"+
		"\3/\3/\5/\u052e\n/\3/\3/\3/\5/\u0533\n/\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/"+
		"\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/\5/\u0550\n/\3/\3/"+
		"\3/\5/\u0555\n/\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/\5/\u0561\n/\3/\3/\3/\3/"+
		"\5/\u0567\n/\3/\3/\3/\3/\3/\5/\u056e\n/\3/\3/\5/\u0572\n/\3/\3/\3/\3/"+
		"\3/\3/\7/\u057a\n/\f/\16/\u057d\13/\5/\u057f\n/\3/\3/\3/\3/\5/\u0585\n"+
		"/\3/\5/\u0588\n/\7/\u058a\n/\f/\16/\u058d\13/\3\60\3\60\3\60\3\60\3\60"+
		"\3\60\7\60\u0595\n\60\f\60\16\60\u0598\13\60\3\60\3\60\5\60\u059c\n\60"+
		"\3\60\3\60\3\60\3\60\3\60\3\60\3\60\3\60\3\60\3\60\5\60\u05a8\n\60\3\60"+
		"\3\60\5\60\u05ac\n\60\7\60\u05ae\n\60\f\60\16\60\u05b1\13\60\3\60\5\60"+
		"\u05b4\n\60\3\60\3\60\3\60\3\60\3\60\5\60\u05bb\n\60\5\60\u05bd\n\60\3"+
		"\61\3\61\3\61\3\61\3\61\3\61\5\61\u05c5\n\61\3\61\3\61\3\62\3\62\3\62"+
		"\5\62\u05cc\n\62\3\62\5\62\u05cf\n\62\3\63\3\63\5\63\u05d3\n\63\3\63\3"+
		"\63\3\63\5\63\u05d8\n\63\3\63\3\63\3\63\3\63\7\63\u05de\n\63\f\63\16\63"+
		"\u05e1\13\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3"+
		"\63\3\63\3\63\7\63\u05f1\n\63\f\63\16\63\u05f4\13\63\3\63\3\63\3\63\5"+
		"\63\u05f9\n\63\3\64\3\64\5\64\u05fd\n\64\3\64\3\64\3\64\7\64\u0602\n\64"+
		"\f\64\16\64\u0605\13\64\3\65\3\65\3\65\5\65\u060a\n\65\3\65\3\65\3\65"+
		"\3\65\3\65\3\65\5\65\u0612\n\65\3\66\3\66\3\66\5\66\u0617\n\66\3\66\5"+
		"\66\u061a\n\66\3\66\3\66\3\66\3\66\5\66\u0620\n\66\3\67\3\67\3\67\5\67"+
		"\u0625\n\67\38\38\38\38\38\78\u062c\n8\f8\168\u062f\138\38\38\58\u0633"+
		"\n8\38\38\38\38\38\39\39\39\39\39\39\39\59\u0641\n9\39\59\u0644\n9\59"+
		"\u0646\n9\3:\3:\3:\5:\u064b\n:\3:\3:\5:\u064f\n:\3:\5:\u0652\n:\3:\3:"+
		"\3:\3:\3:\5:\u0659\n:\3:\3:\3:\5:\u065e\n:\3:\3:\3:\3:\3:\7:\u0665\n:"+
		"\f:\16:\u0668\13:\5:\u066a\n:\3:\3:\5:\u066e\n:\3:\5:\u0671\n:\3:\3:\3"+
		":\3:\7:\u0677\n:\f:\16:\u067a\13:\3:\5:\u067d\n:\3:\3:\3:\3:\3:\3:\5:"+
		"\u0685\n:\3:\5:\u0688\n:\5:\u068a\n:\3;\3;\3;\3;\3;\7;\u0691\n;\f;\16"+
		";\u0694\13;\3<\3<\5<\u0698\n<\3<\3<\5<\u069c\n<\3<\3<\5<\u06a0\n<\3<\5"+
		"<\u06a3\n<\3=\3=\3=\3=\3=\3=\3=\7=\u06ac\n=\f=\16=\u06af\13=\3=\3=\5="+
		"\u06b3\n=\3>\3>\5>\u06b7\n>\3>\3>\3>\7>\u06bc\n>\f>\16>\u06bf\13>\3>\3"+
		">\3>\3>\7>\u06c5\n>\f>\16>\u06c8\13>\3>\5>\u06cb\n>\5>\u06cd\n>\3>\3>"+
		"\5>\u06d1\n>\3>\3>\3>\3>\3>\7>\u06d8\n>\f>\16>\u06db\13>\3>\3>\5>\u06df"+
		"\n>\5>\u06e1\n>\3>\3>\3>\3>\3>\7>\u06e8\n>\f>\16>\u06eb\13>\3>\3>\3>\3"+
		">\3>\3>\7>\u06f3\n>\f>\16>\u06f6\13>\3>\3>\7>\u06fa\n>\f>\16>\u06fd\13"+
		">\5>\u06ff\n>\3?\3?\3?\3?\3?\5?\u0706\n?\3@\5@\u0709\n@\3@\3@\3A\3A\3"+
		"B\3B\3C\3C\3D\3D\5D\u0715\nD\3E\3E\3F\3F\3G\3G\3H\3H\3I\3I\3J\3J\3K\3"+
		"K\3L\3L\3M\3M\3N\3N\3O\3O\3P\3P\3Q\3Q\3R\3R\3S\3S\3T\3T\3U\3U\3V\3V\3"+
		"W\3W\3X\3X\3X\3X\3X\3X\5X\u0743\nX\3Y\3Y\3Z\3Z\3Z\3Z\3[\3[\3[\3[\3[\3"+
		"[\3[\3[\5[\u0753\n[\3[\3[\3[\7[\u0758\n[\f[\16[\u075b\13[\3\\\3\\\3\\"+
		"\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\3\\\5\\\u0769\n\\\3\\\3\\\3\\\7\\\u076e"+
		"\n\\\f\\\16\\\u0771\13\\\3]\3]\3]\3]\3]\3]\3]\3]\3]\3]\5]\u077d\n]\3]"+
		"\3]\7]\u0781\n]\f]\16]\u0784\13]\3]\4\u0194\u047a\6\\\u00b4\u00b6\u00b8"+
		"^\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDF"+
		"HJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a\u008c"+
		"\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e\u00a0\u00a2\u00a4"+
		"\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6\u00b8\2\25\5\2"+
		"==HHUU\4\2\62\62EE\4\2\b\bmm\3\2\u0086\u0087\4\2  AA\4\2%%??\7\2\34\34"+
		"KKTT}}\u0080\u0080\4\2\n\n\17\20\3\2\13\f\3\2\21\24\3\2\25\30\4\2\t\t"+
		"\31\33\6\2PPddffyy\4\2>>\u008e\u008e\5\2\34\34KK\u0080\u0080\6\2\679k"+
		"k\u00a2\u00a2\u00a4\u00a5\4\2\13\rii\4\2\u00a1\u00a1\u00a4\u00a4\3\2\34"+
		"\u00a0\u08a3\2\u00be\3\2\2\2\4\u00c3\3\2\2\2\6\u00c9\3\2\2\2\b\u00e3\3"+
		"\2\2\2\n\u0105\3\2\2\2\f\u0117\3\2\2\2\16\u0120\3\2\2\2\20\u0128\3\2\2"+
		"\2\22\u0132\3\2\2\2\24\u013a\3\2\2\2\26\u015e\3\2\2\2\30\u017e\3\2\2\2"+
		"\32\u01a7\3\2\2\2\34\u01ec\3\2\2\2\36\u01ff\3\2\2\2 \u021d\3\2\2\2\"\u0227"+
		"\3\2\2\2$\u0244\3\2\2\2&\u024a\3\2\2\2(\u0257\3\2\2\2*\u0264\3\2\2\2,"+
		"\u0271\3\2\2\2.\u027f\3\2\2\2\60\u029f\3\2\2\2\62\u02e9\3\2\2\2\64\u02f8"+
		"\3\2\2\2\66\u0305\3\2\2\28\u030b\3\2\2\2:\u0319\3\2\2\2<\u031d\3\2\2\2"+
		">\u0335\3\2\2\2@\u0403\3\2\2\2B\u0407\3\2\2\2D\u0412\3\2\2\2F\u0435\3"+
		"\2\2\2H\u046b\3\2\2\2J\u046d\3\2\2\2L\u0478\3\2\2\2N\u048a\3\2\2\2P\u04b2"+
		"\3\2\2\2R\u04b8\3\2\2\2T\u04ba\3\2\2\2V\u04d3\3\2\2\2X\u04d5\3\2\2\2Z"+
		"\u04d9\3\2\2\2\\\u0532\3\2\2\2^\u058e\3\2\2\2`\u05be\3\2\2\2b\u05c8\3"+
		"\2\2\2d\u05d2\3\2\2\2f\u05fa\3\2\2\2h\u0609\3\2\2\2j\u061f\3\2\2\2l\u0624"+
		"\3\2\2\2n\u0626\3\2\2\2p\u0645\3\2\2\2r\u0689\3\2\2\2t\u068b\3\2\2\2v"+
		"\u06a2\3\2\2\2x\u06b2\3\2\2\2z\u06fe\3\2\2\2|\u0705\3\2\2\2~\u0708\3\2"+
		"\2\2\u0080\u070c\3\2\2\2\u0082\u070e\3\2\2\2\u0084\u0710\3\2\2\2\u0086"+
		"\u0714\3\2\2\2\u0088\u0716\3\2\2\2\u008a\u0718\3\2\2\2\u008c\u071a\3\2"+
		"\2\2\u008e\u071c\3\2\2\2\u0090\u071e\3\2\2\2\u0092\u0720\3\2\2\2\u0094"+
		"\u0722\3\2\2\2\u0096\u0724\3\2\2\2\u0098\u0726\3\2\2\2\u009a\u0728\3\2"+
		"\2\2\u009c\u072a\3\2\2\2\u009e\u072c\3\2\2\2\u00a0\u072e\3\2\2\2\u00a2"+
		"\u0730\3\2\2\2\u00a4\u0732\3\2\2\2\u00a6\u0734\3\2\2\2\u00a8\u0736\3\2"+
		"\2\2\u00aa\u0738\3\2\2\2\u00ac\u073a\3\2\2\2\u00ae\u0742\3\2\2\2\u00b0"+
		"\u0744\3\2\2\2\u00b2\u0746\3\2\2\2\u00b4\u0752\3\2\2\2\u00b6\u0768\3\2"+
		"\2\2\u00b8\u077c\3\2\2\2\u00ba\u00bd\5\6\4\2\u00bb\u00bd\5\4\3\2\u00bc"+
		"\u00ba\3\2\2\2\u00bc\u00bb\3\2\2\2\u00bd\u00c0\3\2\2\2\u00be\u00bc\3\2"+
		"\2\2\u00be\u00bf\3\2\2\2\u00bf\u00c1\3\2\2\2\u00c0\u00be\3\2\2\2\u00c1"+
		"\u00c2\7\2\2\3\u00c2\3\3\2\2\2\u00c3\u00c4\7\u00a9\2\2\u00c4\u00c5\b\3"+
		"\1\2\u00c5\5\3\2\2\2\u00c6\u00c8\7\4\2\2\u00c7\u00c6\3\2\2\2\u00c8\u00cb"+
		"\3\2\2\2\u00c9\u00c7\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00cc\3\2\2\2\u00cb"+
		"\u00c9\3\2\2\2\u00cc\u00d5\5\b\5\2\u00cd\u00cf\7\4\2\2\u00ce\u00cd\3\2"+
		"\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00ce\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1"+
		"\u00d2\3\2\2\2\u00d2\u00d4\5\b\5\2\u00d3\u00ce\3\2\2\2\u00d4\u00d7\3\2"+
		"\2\2\u00d5\u00d3\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6\u00db\3\2\2\2\u00d7"+
		"\u00d5\3\2\2\2\u00d8\u00da\7\4\2\2\u00d9\u00d8\3\2\2\2\u00da\u00dd\3\2"+
		"\2\2\u00db\u00d9\3\2\2\2\u00db\u00dc\3\2\2\2\u00dc\7\3\2\2\2\u00dd\u00db"+
		"\3\2\2\2\u00de\u00e1\7J\2\2\u00df\u00e0\7u\2\2\u00e0\u00e2\7r\2\2\u00e1"+
		"\u00df\3\2\2\2\u00e1\u00e2\3\2\2\2\u00e2\u00e4\3\2\2\2\u00e3\u00de\3\2"+
		"\2\2\u00e3\u00e4\3\2\2\2\u00e4\u0103\3\2\2\2\u00e5\u0104\5\n\6\2\u00e6"+
		"\u0104\5\f\7\2\u00e7\u0104\5\16\b\2\u00e8\u0104\5\20\t\2\u00e9\u0104\5"+
		"\22\n\2\u00ea\u0104\5\24\13\2\u00eb\u0104\5\26\f\2\u00ec\u0104\5\30\r"+
		"\2\u00ed\u0104\5\32\16\2\u00ee\u0104\5\34\17\2\u00ef\u0104\5\36\20\2\u00f0"+
		"\u0104\5 \21\2\u00f1\u0104\5\"\22\2\u00f2\u0104\5$\23\2\u00f3\u0104\5"+
		"&\24\2\u00f4\u0104\5(\25\2\u00f5\u0104\5*\26\2\u00f6\u0104\5,\27\2\u00f7"+
		"\u0104\5.\30\2\u00f8\u0104\5\60\31\2\u00f9\u0104\5\62\32\2\u00fa\u0104"+
		"\5\64\33\2\u00fb\u0104\5\66\34\2\u00fc\u0104\58\35\2\u00fd\u0104\5:\36"+
		"\2\u00fe\u0104\5<\37\2\u00ff\u0104\5> \2\u0100\u0104\5D#\2\u0101\u0104"+
		"\5F$\2\u0102\u0104\5H%\2\u0103\u00e5\3\2\2\2\u0103\u00e6\3\2\2\2\u0103"+
		"\u00e7\3\2\2\2\u0103\u00e8\3\2\2\2\u0103\u00e9\3\2\2\2\u0103\u00ea\3\2"+
		"\2\2\u0103\u00eb\3\2\2\2\u0103\u00ec\3\2\2\2\u0103\u00ed\3\2\2\2\u0103"+
		"\u00ee\3\2\2\2\u0103\u00ef\3\2\2\2\u0103\u00f0\3\2\2\2\u0103\u00f1\3\2"+
		"\2\2\u0103\u00f2\3\2\2\2\u0103\u00f3\3\2\2\2\u0103\u00f4\3\2\2\2\u0103"+
		"\u00f5\3\2\2\2\u0103\u00f6\3\2\2\2\u0103\u00f7\3\2\2\2\u0103\u00f8\3\2"+
		"\2\2\u0103\u00f9\3\2\2\2\u0103\u00fa\3\2\2\2\u0103\u00fb\3\2\2\2\u0103"+
		"\u00fc\3\2\2\2\u0103\u00fd\3\2\2\2\u0103\u00fe\3\2\2\2\u0103\u00ff\3\2"+
		"\2\2\u0103\u0100\3\2\2\2\u0103\u0101\3\2\2\2\u0103\u0102\3\2\2\2\u0104"+
		"\t\3\2\2\2\u0105\u0106\7!\2\2\u0106\u010a\7\u0085\2\2\u0107\u0108\5\u0090"+
		"I\2\u0108\u0109\7\5\2\2\u0109\u010b\3\2\2\2\u010a\u0107\3\2\2\2\u010a"+
		"\u010b\3\2\2\2\u010b\u010c\3\2\2\2\u010c\u0115\5\u0096L\2\u010d\u010e"+
		"\7|\2\2\u010e\u010f\7\u0089\2\2\u010f\u0116\5\u009aN\2\u0110\u0112\7\36"+
		"\2\2\u0111\u0113\7\61\2\2\u0112\u0111\3\2\2\2\u0112\u0113\3\2\2\2\u0113"+
		"\u0114\3\2\2\2\u0114\u0116\5J&\2\u0115\u010d\3\2\2\2\u0115\u0110\3\2\2"+
		"\2\u0116\13\3\2\2\2\u0117\u011e\7\"\2\2\u0118\u011f\5\u0090I\2\u0119\u011f"+
		"\5\u0098M\2\u011a\u011b\5\u0090I\2\u011b\u011c\7\5\2\2\u011c\u011d\5\u0098"+
		"M\2\u011d\u011f\3\2\2\2\u011e\u0118\3\2\2\2\u011e\u0119\3\2\2\2\u011e"+
		"\u011a\3\2\2\2\u011e\u011f\3\2\2\2\u011f\r\3\2\2\2\u0120\u0122\7&\2\2"+
		"\u0121\u0123\7:\2\2\u0122\u0121\3\2\2\2\u0122\u0123\3\2\2\2\u0123\u0124"+
		"\3\2\2\2\u0124\u0125\5\\/\2\u0125\u0126\7$\2\2\u0126\u0127\5\u0090I\2"+
		"\u0127\17\3\2\2\2\u0128\u012a\7)\2\2\u0129\u012b\t\2\2\2\u012a\u0129\3"+
		"\2\2\2\u012a\u012b\3\2\2\2\u012b\u0130\3\2\2\2\u012c\u012e\7\u008a\2\2"+
		"\u012d\u012f\5\u00b0Y\2\u012e\u012d\3\2\2\2\u012e\u012f\3\2\2\2\u012f"+
		"\u0131\3\2\2\2\u0130\u012c\3\2\2\2\u0130\u0131\3\2\2\2\u0131\21\3\2\2"+
		"\2\u0132\u0137\t\3\2\2\u0133\u0135\7\u008a\2\2\u0134\u0136\5\u00b0Y\2"+
		"\u0135\u0134\3\2\2\2\u0135\u0136\3\2\2\2\u0136\u0138\3\2\2\2\u0137\u0133"+
		"\3\2\2\2\u0137\u0138\3\2\2\2\u0138\23\3\2\2\2\u0139\u013b\5f\64\2\u013a"+
		"\u0139\3\2\2\2\u013a\u013b\3\2\2\2\u013b\u013c\3\2\2\2\u013c\u0146\5z"+
		">\2\u013d\u013f\7\u008c\2\2\u013e\u0140\7 \2\2\u013f\u013e\3\2\2\2\u013f"+
		"\u0140\3\2\2\2\u0140\u0144\3\2\2\2\u0141\u0144\7]\2\2\u0142\u0144\7G\2"+
		"\2\u0143\u013d\3\2\2\2\u0143\u0141\3\2\2\2\u0143\u0142\3\2\2\2\u0144\u0145"+
		"\3\2\2\2\u0145\u0147\5z>\2\u0146\u0143\3\2\2\2\u0147\u0148\3\2\2\2\u0148"+
		"\u0146\3\2\2\2\u0148\u0149\3\2\2\2\u0149\u0154\3\2\2\2\u014a\u014b\7p"+
		"\2\2\u014b\u014c\7+\2\2\u014c\u0151\5j\66\2\u014d\u014e\7\b\2\2\u014e"+
		"\u0150\5j\66\2\u014f\u014d\3\2\2\2\u0150\u0153\3\2\2\2\u0151\u014f\3\2"+
		"\2\2\u0151\u0152\3\2\2\2\u0152\u0155\3\2\2\2\u0153\u0151\3\2\2\2\u0154"+
		"\u014a\3\2\2\2\u0154\u0155\3\2\2\2\u0155\u015c\3\2\2\2\u0156\u0157\7e"+
		"\2\2\u0157\u015a\5\\/\2\u0158\u0159\t\4\2\2\u0159\u015b\5\\/\2\u015a\u0158"+
		"\3\2\2\2\u015a\u015b\3\2\2\2\u015b\u015d\3\2\2\2\u015c\u0156\3\2\2\2\u015c"+
		"\u015d\3\2\2\2\u015d\25\3\2\2\2\u015e\u0160\7\65\2\2\u015f\u0161\7\u008d"+
		"\2\2\u0160\u015f\3\2\2\2\u0160\u0161\3\2\2\2\u0161\u0162\3\2\2\2\u0162"+
		"\u0166\7W\2\2\u0163\u0164\7S\2\2\u0164\u0165\7i\2\2\u0165\u0167\7I\2\2"+
		"\u0166\u0163\3\2\2\2\u0166\u0167\3\2\2\2\u0167\u016b\3\2\2\2\u0168\u0169"+
		"\5\u0090I\2\u0169\u016a\7\5\2\2\u016a\u016c\3\2\2\2\u016b\u0168\3\2\2"+
		"\2\u016b\u016c\3\2\2\2\u016c\u016d\3\2\2\2\u016d\u016e\5\u00a2R\2\u016e"+
		"\u016f\7n\2\2\u016f\u0170\5\u0096L\2\u0170\u0171\7\6\2\2\u0171\u0176\5"+
		"b\62\2\u0172\u0173\7\b\2\2\u0173\u0175\5b\62\2\u0174\u0172\3\2\2\2\u0175"+
		"\u0178\3\2\2\2\u0176\u0174\3\2\2\2\u0176\u0177\3\2\2\2\u0177\u0179\3\2"+
		"\2\2\u0178\u0176\3\2\2\2\u0179\u017c\7\7\2\2\u017a\u017b\7\u0095\2\2\u017b"+
		"\u017d\5\\/\2\u017c\u017a\3\2\2\2\u017c\u017d\3\2\2\2\u017d\27\3\2\2\2"+
		"\u017e\u0180\7\65\2\2\u017f\u0181\t\5\2\2\u0180\u017f\3\2\2\2\u0180\u0181"+
		"\3\2\2\2\u0181\u0182\3\2\2\2\u0182\u0186\7\u0085\2\2\u0183\u0184\7S\2"+
		"\2\u0184\u0185\7i\2\2\u0185\u0187\7I\2\2\u0186\u0183\3\2\2\2\u0186\u0187"+
		"\3\2\2\2\u0187\u018b\3\2\2\2\u0188\u0189\5\u0090I\2\u0189\u018a\7\5\2"+
		"\2\u018a\u018c\3\2\2\2\u018b\u0188\3\2\2\2\u018b\u018c\3\2\2\2\u018c\u018d"+
		"\3\2\2\2\u018d\u01a5\5\u0096L\2\u018e\u018f\7\6\2\2\u018f\u0194\5J&\2"+
		"\u0190\u0191\7\b\2\2\u0191\u0193\5J&\2\u0192\u0190\3\2\2\2\u0193\u0196"+
		"\3\2\2\2\u0194\u0195\3\2\2\2\u0194\u0192\3\2\2\2\u0195\u019b\3\2\2\2\u0196"+
		"\u0194\3\2\2\2\u0197\u0198\7\b\2\2\u0198\u019a\5d\63\2\u0199\u0197\3\2"+
		"\2\2\u019a\u019d\3\2\2\2\u019b\u0199\3\2\2\2\u019b\u019c\3\2\2\2\u019c"+
		"\u019e\3\2\2\2\u019d\u019b\3\2\2\2\u019e\u01a1\7\7\2\2\u019f\u01a0\7\u0097"+
		"\2\2\u01a0\u01a2\7\u00a1\2\2\u01a1\u019f\3\2\2\2\u01a1\u01a2\3\2\2\2\u01a2"+
		"\u01a6\3\2\2\2\u01a3\u01a4\7$\2\2\u01a4\u01a6\5> \2\u01a5\u018e\3\2\2"+
		"\2\u01a5\u01a3\3\2\2\2\u01a6\31\3\2\2\2\u01a7\u01a9\7\65\2\2\u01a8\u01aa"+
		"\t\5\2\2\u01a9\u01a8\3\2\2\2\u01a9\u01aa\3\2\2\2\u01aa\u01ab\3\2\2\2\u01ab"+
		"\u01af\7\u008b\2\2\u01ac\u01ad\7S\2\2\u01ad\u01ae\7i\2\2\u01ae\u01b0\7"+
		"I\2\2\u01af\u01ac\3\2\2\2\u01af\u01b0\3\2\2\2\u01b0\u01b4\3\2\2\2\u01b1"+
		"\u01b2\5\u0090I\2\u01b2\u01b3\7\5\2\2\u01b3\u01b5\3\2\2\2\u01b4\u01b1"+
		"\3\2\2\2\u01b4\u01b5\3\2\2\2\u01b5\u01b6\3\2\2\2\u01b6\u01bb\5\u00a4S"+
		"\2\u01b7\u01bc\7(\2\2\u01b8\u01bc\7\37\2\2\u01b9\u01ba\7\\\2\2\u01ba\u01bc"+
		"\7l\2\2\u01bb\u01b7\3\2\2\2\u01bb\u01b8\3\2\2\2\u01bb\u01b9\3\2\2\2\u01bb"+
		"\u01bc\3\2\2\2\u01bc\u01cb\3\2\2\2\u01bd\u01cc\7>\2\2\u01be\u01cc\7[\2"+
		"\2\u01bf\u01c9\7\u008e\2\2\u01c0\u01c1\7l\2\2\u01c1\u01c6\5\u009cO\2\u01c2"+
		"\u01c3\7\b\2\2\u01c3\u01c5\5\u009cO\2\u01c4\u01c2\3\2\2\2\u01c5\u01c8"+
		"\3\2\2\2\u01c6\u01c4\3\2\2\2\u01c6\u01c7\3\2\2\2\u01c7\u01ca\3\2\2\2\u01c8"+
		"\u01c6\3\2\2\2\u01c9\u01c0\3\2\2\2\u01c9\u01ca\3\2\2\2\u01ca\u01cc\3\2"+
		"\2\2\u01cb\u01bd\3\2\2\2\u01cb\u01be\3\2\2\2\u01cb\u01bf\3\2\2\2\u01cc"+
		"\u01cd\3\2\2\2\u01cd\u01d1\7n\2\2\u01ce\u01cf\5\u0090I\2\u01cf\u01d0\7"+
		"\5\2\2\u01d0\u01d2\3\2\2\2\u01d1\u01ce\3\2\2\2\u01d1\u01d2\3\2\2\2\u01d2"+
		"\u01d3\3\2\2\2\u01d3\u01d7\5\u0096L\2\u01d4\u01d5\7L\2\2\u01d5\u01d6\7"+
		"C\2\2\u01d6\u01d8\7\u0081\2\2\u01d7\u01d4\3\2\2\2\u01d7\u01d8\3\2\2\2"+
		"\u01d8\u01db\3\2\2\2\u01d9\u01da\7\u0094\2\2\u01da\u01dc\5\\/\2\u01db"+
		"\u01d9\3\2\2\2\u01db\u01dc\3\2\2\2\u01dc\u01dd\3\2\2\2\u01dd\u01e6\7)"+
		"\2\2\u01de\u01e3\5D#\2\u01df\u01e3\5\60\31\2\u01e0\u01e3\5 \21\2\u01e1"+
		"\u01e3\5> \2\u01e2\u01de\3\2\2\2\u01e2\u01df\3\2\2\2\u01e2\u01e0\3\2\2"+
		"\2\u01e2\u01e1\3\2\2\2\u01e3\u01e4\3\2\2\2\u01e4\u01e5\7\4\2\2\u01e5\u01e7"+
		"\3\2\2\2\u01e6\u01e2\3\2\2\2\u01e7\u01e8\3\2\2\2\u01e8\u01e6\3\2\2\2\u01e8"+
		"\u01e9\3\2\2\2\u01e9\u01ea\3\2\2\2\u01ea\u01eb\7E\2\2\u01eb\33\3\2\2\2"+
		"\u01ec\u01ee\7\65\2\2\u01ed\u01ef\t\5\2\2\u01ee\u01ed\3\2\2\2\u01ee\u01ef"+
		"\3\2\2\2\u01ef\u01f0\3\2\2\2\u01f0\u01f4\7\u0092\2\2\u01f1\u01f2\7S\2"+
		"\2\u01f2\u01f3\7i\2\2\u01f3\u01f5\7I\2\2\u01f4\u01f1\3\2\2\2\u01f4\u01f5"+
		"\3\2\2\2\u01f5\u01f9\3\2\2\2\u01f6\u01f7\5\u0090I\2\u01f7\u01f8\7\5\2"+
		"\2\u01f8\u01fa\3\2\2\2\u01f9\u01f6\3\2\2\2\u01f9\u01fa\3\2\2\2\u01fa\u01fb"+
		"\3\2\2\2\u01fb\u01fc\5\u00a6T\2\u01fc\u01fd\7$\2\2\u01fd\u01fe\5> \2\u01fe"+
		"\35\3\2\2\2\u01ff\u0200\7\65\2\2\u0200\u0201\7\u0093\2\2\u0201\u0205\7"+
		"\u0085\2\2\u0202\u0203\7S\2\2\u0203\u0204\7i\2\2\u0204\u0206\7I\2\2\u0205"+
		"\u0202\3\2\2\2\u0205\u0206\3\2\2\2\u0206\u020a\3\2\2\2\u0207\u0208\5\u0090"+
		"I\2\u0208\u0209\7\5\2\2\u0209\u020b\3\2\2\2\u020a\u0207\3\2\2\2\u020a"+
		"\u020b\3\2\2\2\u020b\u020c\3\2\2\2\u020c\u020d\5\u0096L\2\u020d\u020e"+
		"\7\u008f\2\2\u020e\u021a\5\u00a8U\2\u020f\u0210\7\6\2\2\u0210\u0215\5"+
		"\u0086D\2\u0211\u0212\7\b\2\2\u0212\u0214\5\u0086D\2\u0213\u0211\3\2\2"+
		"\2\u0214\u0217\3\2\2\2\u0215\u0213\3\2\2\2\u0215\u0216\3\2\2\2\u0216\u0218"+
		"\3\2\2\2\u0217\u0215\3\2\2\2\u0218\u0219\7\7\2\2\u0219\u021b\3\2\2\2\u021a"+
		"\u020f\3\2\2\2\u021a\u021b\3\2\2\2\u021b\37\3\2\2\2\u021c\u021e\5f\64"+
		"\2\u021d\u021c\3\2\2\2\u021d\u021e\3\2\2\2\u021e\u021f\3\2\2\2\u021f\u0220"+
		"\7>\2\2\u0220\u0221\7N\2\2\u0221\u0224\5h\65\2\u0222\u0223\7\u0095\2\2"+
		"\u0223\u0225\5\\/\2\u0224\u0222\3\2\2\2\u0224\u0225\3\2\2\2\u0225!\3\2"+
		"\2\2\u0226\u0228\5f\64\2\u0227\u0226\3\2\2\2\u0227\u0228\3\2\2\2\u0228"+
		"\u0229\3\2\2\2\u0229\u022a\7>\2\2\u022a\u022b\7N\2\2\u022b\u022e\5h\65"+
		"\2\u022c\u022d\7\u0095\2\2\u022d\u022f\5\\/\2\u022e\u022c\3\2\2\2\u022e"+
		"\u022f\3\2\2\2\u022f\u0242\3\2\2\2\u0230\u0231\7p\2\2\u0231\u0232\7+\2"+
		"\2\u0232\u0237\5j\66\2\u0233\u0234\7\b\2\2\u0234\u0236\5j\66\2\u0235\u0233"+
		"\3\2\2\2\u0236\u0239\3\2\2\2\u0237\u0235\3\2\2\2\u0237\u0238\3\2\2\2\u0238"+
		"\u023b\3\2\2\2\u0239\u0237\3\2\2\2\u023a\u0230\3\2\2\2\u023a\u023b\3\2"+
		"\2\2\u023b\u023c\3\2\2\2\u023c\u023d\7e\2\2\u023d\u0240\5\\/\2\u023e\u023f"+
		"\t\4\2\2\u023f\u0241\5\\/\2\u0240\u023e\3\2\2\2\u0240\u0241\3\2\2\2\u0241"+
		"\u0243\3\2\2\2\u0242\u023a\3\2\2\2\u0242\u0243\3\2\2\2\u0243#\3\2\2\2"+
		"\u0244\u0246\7@\2\2\u0245\u0247\7:\2\2\u0246\u0245\3\2\2\2\u0246\u0247"+
		"\3\2\2\2\u0247\u0248\3\2\2\2\u0248\u0249\5\u0090I\2\u0249%\3\2\2\2\u024a"+
		"\u024b\7B\2\2\u024b\u024e\7W\2\2\u024c\u024d\7S\2\2\u024d\u024f\7I\2\2"+
		"\u024e\u024c\3\2\2\2\u024e\u024f\3\2\2\2\u024f\u0253\3\2\2\2\u0250\u0251"+
		"\5\u0090I\2\u0251\u0252\7\5\2\2\u0252\u0254\3\2\2\2\u0253\u0250\3\2\2"+
		"\2\u0253\u0254\3\2\2\2\u0254\u0255\3\2\2\2\u0255\u0256\5\u00a2R\2\u0256"+
		"\'\3\2\2\2\u0257\u0258\7B\2\2\u0258\u025b\7\u0085\2\2\u0259\u025a\7S\2"+
		"\2\u025a\u025c\7I\2\2\u025b\u0259\3\2\2\2\u025b\u025c\3\2\2\2\u025c\u0260"+
		"\3\2\2\2\u025d\u025e\5\u0090I\2\u025e\u025f\7\5\2\2\u025f\u0261\3\2\2"+
		"\2\u0260\u025d\3\2\2\2\u0260\u0261\3\2\2\2\u0261\u0262\3\2\2\2\u0262\u0263"+
		"\5\u0096L\2\u0263)\3\2\2\2\u0264\u0265\7B\2\2\u0265\u0268\7\u008b\2\2"+
		"\u0266\u0267\7S\2\2\u0267\u0269\7I\2\2\u0268\u0266\3\2\2\2\u0268\u0269"+
		"\3\2\2\2\u0269\u026d\3\2\2\2\u026a\u026b\5\u0090I\2\u026b\u026c\7\5\2"+
		"\2\u026c\u026e\3\2\2\2\u026d\u026a\3\2\2\2\u026d\u026e\3\2\2\2\u026e\u026f"+
		"\3\2\2\2\u026f\u0270\5\u00a4S\2\u0270+\3\2\2\2\u0271\u0272\7B\2\2\u0272"+
		"\u0275\7\u0092\2\2\u0273\u0274\7S\2\2\u0274\u0276\7I\2\2\u0275\u0273\3"+
		"\2\2\2\u0275\u0276\3\2\2\2\u0276\u027a\3\2\2\2\u0277\u0278\5\u0090I\2"+
		"\u0278\u0279\7\5\2\2\u0279\u027b\3\2\2\2\u027a\u0277\3\2\2\2\u027a\u027b"+
		"\3\2\2\2\u027b\u027c\3\2\2\2\u027c\u027d\5\u00a6T\2\u027d-\3\2\2\2\u027e"+
		"\u0280\5f\64\2\u027f\u027e\3\2\2\2\u027f\u0280\3\2\2\2\u0280\u0281\3\2"+
		"\2\2\u0281\u0287\5z>\2\u0282\u0283\5|?\2\u0283\u0284\5z>\2\u0284\u0286"+
		"\3\2\2\2\u0285\u0282\3\2\2\2\u0286\u0289\3\2\2\2\u0287\u0285\3\2\2\2\u0287"+
		"\u0288\3\2\2\2\u0288\u0294\3\2\2\2\u0289\u0287\3\2\2\2\u028a\u028b\7p"+
		"\2\2\u028b\u028c\7+\2\2\u028c\u0291\5j\66\2\u028d\u028e\7\b\2\2\u028e"+
		"\u0290\5j\66\2\u028f\u028d\3\2\2\2\u0290\u0293\3\2\2\2\u0291\u028f\3\2"+
		"\2\2\u0291\u0292\3\2\2\2\u0292\u0295\3\2\2\2\u0293\u0291\3\2\2\2\u0294"+
		"\u028a\3\2\2\2\u0294\u0295\3\2\2\2\u0295\u029c\3\2\2\2\u0296\u0297\7e"+
		"\2\2\u0297\u029a\5\\/\2\u0298\u0299\t\4\2\2\u0299\u029b\5\\/\2\u029a\u0298"+
		"\3\2\2\2\u029a\u029b\3\2\2\2\u029b\u029d\3\2\2\2\u029c\u0296\3\2\2\2\u029c"+
		"\u029d\3\2\2\2\u029d/\3\2\2\2\u029e\u02a0\5f\64\2\u029f\u029e\3\2\2\2"+
		"\u029f\u02a0\3\2\2\2\u02a0\u02b2\3\2\2\2\u02a1\u02b3\7[\2\2\u02a2\u02b3"+
		"\7}\2\2\u02a3\u02a4\7[\2\2\u02a4\u02a5\7o\2\2\u02a5\u02b3\7}\2\2\u02a6"+
		"\u02a7\7[\2\2\u02a7\u02a8\7o\2\2\u02a8\u02b3\7\u0080\2\2\u02a9\u02aa\7"+
		"[\2\2\u02aa\u02ab\7o\2\2\u02ab\u02b3\7\34\2\2\u02ac\u02ad\7[\2\2\u02ad"+
		"\u02ae\7o\2\2\u02ae\u02b3\7K\2\2\u02af\u02b0\7[\2\2\u02b0\u02b1\7o\2\2"+
		"\u02b1\u02b3\7T\2\2\u02b2\u02a1\3\2\2\2\u02b2\u02a2\3\2\2\2\u02b2\u02a3"+
		"\3\2\2\2\u02b2\u02a6\3\2\2\2\u02b2\u02a9\3\2\2\2\u02b2\u02ac\3\2\2\2\u02b2"+
		"\u02af\3\2\2\2\u02b3\u02b4\3\2\2\2\u02b4\u02b8\7^\2\2\u02b5\u02b6\5\u0090"+
		"I\2\u02b6\u02b7\7\5\2\2\u02b7\u02b9\3\2\2\2\u02b8\u02b5\3\2\2\2\u02b8"+
		"\u02b9\3\2\2\2\u02b9\u02ba\3\2\2\2\u02ba\u02c6\5\u0096L\2\u02bb\u02bc"+
		"\7\6\2\2\u02bc\u02c1\5\u009cO\2\u02bd\u02be\7\b\2\2\u02be\u02c0\5\u009c"+
		"O\2\u02bf\u02bd\3\2\2\2\u02c0\u02c3\3\2\2\2\u02c1\u02bf\3\2\2\2\u02c1"+
		"\u02c2\3\2\2\2\u02c2\u02c4\3\2\2\2\u02c3\u02c1\3\2\2\2\u02c4\u02c5\7\7"+
		"\2\2\u02c5\u02c7\3\2\2\2\u02c6\u02bb\3\2\2\2\u02c6\u02c7\3\2\2\2\u02c7"+
		"\u02e7\3\2\2\2\u02c8\u02c9\7\u0091\2\2\u02c9\u02ca\7\6\2\2\u02ca\u02cf"+
		"\5\\/\2\u02cb\u02cc\7\b\2\2\u02cc\u02ce\5\\/\2\u02cd\u02cb\3\2\2\2\u02ce"+
		"\u02d1\3\2\2\2\u02cf\u02cd\3\2\2\2\u02cf\u02d0\3\2\2\2\u02d0\u02d2\3\2"+
		"\2\2\u02d1\u02cf\3\2\2\2\u02d2\u02e1\7\7\2\2\u02d3\u02d4\7\b\2\2\u02d4"+
		"\u02d5\7\6\2\2\u02d5\u02da\5\\/\2\u02d6\u02d7\7\b\2\2\u02d7\u02d9\5\\"+
		"/\2\u02d8\u02d6\3\2\2\2\u02d9\u02dc\3\2\2\2\u02da\u02d8\3\2\2\2\u02da"+
		"\u02db\3\2\2\2\u02db\u02dd\3\2\2\2\u02dc\u02da\3\2\2\2\u02dd\u02de\7\7"+
		"\2\2\u02de\u02e0\3\2\2\2\u02df\u02d3\3\2\2\2\u02e0\u02e3\3\2\2\2\u02e1"+
		"\u02df\3\2\2\2\u02e1\u02e2\3\2\2\2\u02e2\u02e8\3\2\2\2\u02e3\u02e1\3\2"+
		"\2\2\u02e4\u02e8\5> \2\u02e5\u02e6\7;\2\2\u02e6\u02e8\7\u0091\2\2\u02e7"+
		"\u02c8\3\2\2\2\u02e7\u02e4\3\2\2\2\u02e7\u02e5\3\2\2\2\u02e8\61\3\2\2"+
		"\2\u02e9\u02ed\7s\2\2\u02ea\u02eb\5\u0090I\2\u02eb\u02ec\7\5\2\2\u02ec"+
		"\u02ee\3\2\2\2\u02ed\u02ea\3\2\2\2\u02ed\u02ee\3\2\2\2\u02ee\u02ef\3\2"+
		"\2\2\u02ef\u02f6\5\u00aaV\2\u02f0\u02f1\7\t\2\2\u02f1\u02f7\5l\67\2\u02f2"+
		"\u02f3\7\6\2\2\u02f3\u02f4\5l\67\2\u02f4\u02f5\7\7\2\2\u02f5\u02f7\3\2"+
		"\2\2\u02f6\u02f0\3\2\2\2\u02f6\u02f2\3\2\2\2\u02f6\u02f7\3\2\2\2\u02f7"+
		"\63\3\2\2\2\u02f8\u0303\7z\2\2\u02f9\u0304\5\u009eP\2\u02fa\u02fb\5\u0090"+
		"I\2\u02fb\u02fc\7\5\2\2\u02fc\u02fe\3\2\2\2\u02fd\u02fa\3\2\2\2\u02fd"+
		"\u02fe\3\2\2\2\u02fe\u0301\3\2\2\2\u02ff\u0302\5\u0096L\2\u0300\u0302"+
		"\5\u00a2R\2\u0301\u02ff\3\2\2\2\u0301\u0300\3\2\2\2\u0302\u0304\3\2\2"+
		"\2\u0303\u02f9\3\2\2\2\u0303\u02fd\3\2\2\2\u0303\u0304\3\2\2\2\u0304\65"+
		"\3\2\2\2\u0305\u0307\7{\2\2\u0306\u0308\7\u0082\2\2\u0307\u0306\3\2\2"+
		"\2\u0307\u0308\3\2\2\2\u0308\u0309\3\2\2\2\u0309\u030a\5\u00acW\2\u030a"+
		"\67\3\2\2\2\u030b\u0310\7\u0080\2\2\u030c\u030e\7\u008a\2\2\u030d\u030f"+
		"\5\u00b0Y\2\u030e\u030d\3\2\2\2\u030e\u030f\3\2\2\2\u030f\u0311\3\2\2"+
		"\2\u0310\u030c\3\2\2\2\u0310\u0311\3\2\2\2\u0311\u0317\3\2\2\2\u0312\u0314"+
		"\7\u0089\2\2\u0313\u0315\7\u0082\2\2\u0314\u0313\3\2\2\2\u0314\u0315\3"+
		"\2\2\2\u0315\u0316\3\2\2\2\u0316\u0318\5\u00acW\2\u0317\u0312\3\2\2\2"+
		"\u0317\u0318\3\2\2\2\u03189\3\2\2\2\u0319\u031a\7\u0082\2\2\u031a\u031b"+
		"\5\u00acW\2\u031b;\3\2\2\2\u031c\u031e\5f\64\2\u031d\u031c\3\2\2\2\u031d"+
		"\u031e\3\2\2\2\u031e\u031f\3\2\2\2\u031f\u032a\5z>\2\u0320\u0321\7p\2"+
		"\2\u0321\u0322\7+\2\2\u0322\u0327\5j\66\2\u0323\u0324\7\b\2\2\u0324\u0326"+
		"\5j\66\2\u0325\u0323\3\2\2\2\u0326\u0329\3\2\2\2\u0327\u0325\3\2\2\2\u0327"+
		"\u0328\3\2\2\2\u0328\u032b\3\2\2\2\u0329\u0327\3\2\2\2\u032a\u0320\3\2"+
		"\2\2\u032a\u032b\3\2\2\2\u032b\u0332\3\2\2\2\u032c\u032d\7e\2\2\u032d"+
		"\u0330\5\\/\2\u032e\u032f\t\4\2\2\u032f\u0331\5\\/\2\u0330\u032e\3\2\2"+
		"\2\u0330\u0331\3\2\2\2\u0331\u0333\3\2\2\2\u0332\u032c\3\2\2\2\u0332\u0333"+
		"\3\2\2\2\u0333=\3\2\2\2\u0334\u0336\5f\64\2\u0335\u0334\3\2\2\2\u0335"+
		"\u0336\3\2\2\2\u0336\u0337\3\2\2\2\u0337\u033d\5@!\2\u0338\u0339\5|?\2"+
		"\u0339\u033a\5@!\2\u033a\u033c\3\2\2\2\u033b\u0338\3\2\2\2\u033c\u033f"+
		"\3\2\2\2\u033d\u033b\3\2\2\2\u033d\u033e\3\2\2\2\u033e\u034a\3\2\2\2\u033f"+
		"\u033d\3\2\2\2\u0340\u0341\7p\2\2\u0341\u0342\7+\2\2\u0342\u0347\5j\66"+
		"\2\u0343\u0344\7\b\2\2\u0344\u0346\5j\66\2\u0345\u0343\3\2\2\2\u0346\u0349"+
		"\3\2\2\2\u0347\u0345\3\2\2\2\u0347\u0348\3\2\2\2\u0348\u034b\3\2\2\2\u0349"+
		"\u0347\3\2\2\2\u034a\u0340\3\2\2\2\u034a\u034b\3\2\2\2\u034b\u0353\3\2"+
		"\2\2\u034c\u034d\7e\2\2\u034d\u0354\5R*\2\u034e\u0351\5\\/\2\u034f\u0350"+
		"\t\4\2\2\u0350\u0352\5\\/\2\u0351\u034f\3\2\2\2\u0351\u0352\3\2\2\2\u0352"+
		"\u0354\3\2\2\2\u0353\u034c\3\2\2\2\u0353\u034e\3\2\2\2\u0353\u0354\3\2"+
		"\2\2\u0354\u0357\3\2\2\2\u0355\u0356\7\u0099\2\2\u0356\u0358\5V,\2\u0357"+
		"\u0355\3\2\2\2\u0357\u0358\3\2\2\2\u0358?\3\2\2\2\u0359\u035b\7\u0083"+
		"\2\2\u035a\u035c\t\6\2\2\u035b\u035a\3\2\2\2\u035b\u035c\3\2\2\2\u035c"+
		"\u035d\3\2\2\2\u035d\u0362\5p9\2\u035e\u035f\7\b\2\2\u035f\u0361\5p9\2"+
		"\u0360\u035e\3\2\2\2\u0361\u0364\3\2\2\2\u0362\u0360\3\2\2\2\u0362\u0363"+
		"\3\2\2\2\u0363\u0387\3\2\2\2\u0364\u0362\3\2\2\2\u0365\u0366\7\b\2\2\u0366"+
		"\u0367\7\u009a\2\2\u0367\u0368\7\6\2\2\u0368\u0369\7\u0098\2\2\u0369\u036a"+
		"\7+\2\2\u036a\u036b\5B\"\2\u036b\u036c\7\b\2\2\u036c\u036d\7\u009b\2\2"+
		"\u036d\u0383\7\6\2\2\u036e\u036f\7\u0099\2\2\u036f\u0372\5V,\2\u0370\u0371"+
		"\7e\2\2\u0371\u0373\5R*\2\u0372\u0370\3\2\2\2\u0372\u0373\3\2\2\2\u0373"+
		"\u0374\3\2\2\2\u0374\u0381\7\7\2\2\u0375\u0376\7R\2\2\u0376\u0377\5\\"+
		"/\2\u0377\u0378\7p\2\2\u0378\u0379\7+\2\2\u0379\u037e\5j\66\2\u037a\u037b"+
		"\7\b\2\2\u037b\u037d\5j\66\2\u037c\u037a\3\2\2\2\u037d\u0380\3\2\2\2\u037e"+
		"\u037c\3\2\2\2\u037e\u037f\3\2\2\2\u037f\u0382\3\2\2\2\u0380\u037e\3\2"+
		"\2\2\u0381\u0375\3\2\2\2\u0381\u0382\3\2\2\2\u0382\u0384\3\2\2\2\u0383"+
		"\u036e\3\2\2\2\u0383\u0384\3\2\2\2\u0384\u0385\3\2\2\2\u0385\u0386\7\7"+
		"\2\2\u0386\u0388\3\2\2\2\u0387\u0365\3\2\2\2\u0387\u0388\3\2\2\2\u0388"+
		"\u0395\3\2\2\2\u0389\u0393\7N\2\2\u038a\u038f\5r:\2\u038b\u038c\7\b\2"+
		"\2\u038c\u038e\5r:\2\u038d\u038b\3\2\2\2\u038e\u0391\3\2\2\2\u038f\u038d"+
		"\3\2\2\2\u038f\u0390\3\2\2\2\u0390\u0394\3\2\2\2\u0391\u038f\3\2\2\2\u0392"+
		"\u0394\5t;\2\u0393\u038a\3\2\2\2\u0393\u0392\3\2\2\2\u0394\u0396\3\2\2"+
		"\2\u0395\u0389\3\2\2\2\u0395\u0396\3\2\2\2\u0396\u0399\3\2\2\2\u0397\u0398"+
		"\7\u0095\2\2\u0398\u039a\5\\/\2\u0399\u0397\3\2\2\2\u0399\u039a\3\2\2"+
		"\2\u039a\u03b2\3\2\2\2\u039b\u039c\7\u009b\2\2\u039c\u039d\7\6\2\2\u039d"+
		"\u039e\7p\2\2\u039e\u039f\7+\2\2\u039f\u03ae\5j\66\2\u03a0\u03a1\7e\2"+
		"\2\u03a1\u03a2\5R*\2\u03a2\u03a3\7\u0099\2\2\u03a3\u03a4\5V,\2\u03a4\u03a6"+
		"\3\2\2\2\u03a5\u03a0\3\2\2\2\u03a5\u03a6\3\2\2\2\u03a6\u03af\3\2\2\2\u03a7"+
		"\u03a8\7\u0099\2\2\u03a8\u03a9\5V,\2\u03a9\u03aa\7e\2\2\u03aa\u03ab\5"+
		"R*\2\u03ab\u03ad\3\2\2\2\u03ac\u03a7\3\2\2\2\u03ac\u03ad\3\2\2\2\u03ad"+
		"\u03af\3\2\2\2\u03ae\u03a5\3\2\2\2\u03ae\u03ac\3\2\2\2\u03af\u03b0\3\2"+
		"\2\2\u03b0\u03b1\7\7\2\2\u03b1\u03b3\3\2\2\2\u03b2\u039b\3\2\2\2\u03b2"+
		"\u03b3\3\2\2\2\u03b3\u03e5\3\2\2\2\u03b4\u03b5\7Q\2\2\u03b5\u03b6\7+\2"+
		"\2\u03b6\u03d8\5\\/\2\u03b7\u03b9\7\b\2\2\u03b8\u03b7\3\2\2\2\u03b8\u03b9"+
		"\3\2\2\2\u03b9\u03ba\3\2\2\2\u03ba\u03bb\7\u009b\2\2\u03bb\u03bc\7\6\2"+
		"\2\u03bc\u03bd\7p\2\2\u03bd\u03be\7+\2\2\u03be\u03c3\5j\66\2\u03bf\u03c0"+
		"\7\b\2\2\u03c0\u03c2\5j\66\2\u03c1\u03bf\3\2\2\2\u03c2\u03c5\3\2\2\2\u03c3"+
		"\u03c1\3\2\2\2\u03c3\u03c4\3\2\2\2\u03c4\u03d4\3\2\2\2\u03c5\u03c3\3\2"+
		"\2\2\u03c6\u03c7\7e\2\2\u03c7\u03c8\5R*\2\u03c8\u03c9\7\u0099\2\2\u03c9"+
		"\u03ca\5V,\2\u03ca\u03cc\3\2\2\2\u03cb\u03c6\3\2\2\2\u03cb\u03cc\3\2\2"+
		"\2\u03cc\u03d5\3\2\2\2\u03cd\u03ce\7\u0099\2\2\u03ce\u03cf\5V,\2\u03cf"+
		"\u03d0\7e\2\2\u03d0\u03d1\5R*\2\u03d1\u03d3\3\2\2\2\u03d2\u03cd\3\2\2"+
		"\2\u03d2\u03d3\3\2\2\2\u03d3\u03d5\3\2\2\2\u03d4\u03cb\3\2\2\2\u03d4\u03d2"+
		"\3\2\2\2\u03d5\u03d6\3\2\2\2\u03d6\u03d7\7\7\2\2\u03d7\u03d9\3\2\2\2\u03d8"+
		"\u03b8\3\2\2\2\u03d8\u03d9\3\2\2\2\u03d9\u03de\3\2\2\2\u03da\u03db\7\b"+
		"\2\2\u03db\u03dd\5\\/\2\u03dc\u03da\3\2\2\2\u03dd\u03e0\3\2\2\2\u03de"+
		"\u03dc\3\2\2\2\u03de\u03df\3\2\2\2\u03df\u03e3\3\2\2\2\u03e0\u03de\3\2"+
		"\2\2\u03e1\u03e2\7R\2\2\u03e2\u03e4\5\\/\2\u03e3\u03e1\3\2\2\2\u03e3\u03e4"+
		"\3\2\2\2\u03e4\u03e6\3\2\2\2\u03e5\u03b4\3\2\2\2\u03e5\u03e6\3\2\2\2\u03e6"+
		"\u0404\3\2\2\2\u03e7\u03e8\7\u0091\2\2\u03e8\u03e9\7\6\2\2\u03e9\u03ee"+
		"\5\\/\2\u03ea\u03eb\7\b\2\2\u03eb\u03ed\5\\/\2\u03ec\u03ea\3\2\2\2\u03ed"+
		"\u03f0\3\2\2\2\u03ee\u03ec\3\2\2\2\u03ee\u03ef\3\2\2\2\u03ef\u03f1\3\2"+
		"\2\2\u03f0\u03ee\3\2\2\2\u03f1\u0400\7\7\2\2\u03f2\u03f3\7\b\2\2\u03f3"+
		"\u03f4\7\6\2\2\u03f4\u03f9\5\\/\2\u03f5\u03f6\7\b\2\2\u03f6\u03f8\5\\"+
		"/\2\u03f7\u03f5\3\2\2\2\u03f8\u03fb\3\2\2\2\u03f9\u03f7\3\2\2\2\u03f9"+
		"\u03fa\3\2\2\2\u03fa\u03fc\3\2\2\2\u03fb\u03f9\3\2\2\2\u03fc\u03fd\7\7"+
		"\2\2\u03fd\u03ff\3\2\2\2\u03fe\u03f2\3\2\2\2\u03ff\u0402\3\2\2\2\u0400"+
		"\u03fe\3\2\2\2\u0400\u0401\3\2\2\2\u0401\u0404\3\2\2\2\u0402\u0400\3\2"+
		"\2\2\u0403\u0359\3\2\2\2\u0403\u03e7\3\2\2\2\u0404A\3\2\2\2\u0405\u0406"+
		"\7\u009c\2\2\u0406\u0408\7\u009d\2\2\u0407\u0405\3\2\2\2\u0407\u0408\3"+
		"\2\2\2\u0408\u0409\3\2\2\2\u0409\u040a\7\6\2\2\u040a\u040b\5\u00b6\\\2"+
		"\u040b\u040c\7\5\2\2\u040c\u040d\5\u00b6\\\2\u040d\u040e\7\b\2\2\u040e"+
		"\u040f\5\u00b6\\\2\u040f\u0410\7\7\2\2\u0410C\3\2\2\2\u0411\u0413\5f\64"+
		"\2\u0412\u0411\3\2\2\2\u0412\u0413\3\2\2\2\u0413\u0414\3\2\2\2\u0414\u041f"+
		"\7\u008e\2\2\u0415\u0416\7o\2\2\u0416\u0420\7\u0080\2\2\u0417\u0418\7"+
		"o\2\2\u0418\u0420\7\34\2\2\u0419\u041a\7o\2\2\u041a\u0420\7}\2\2\u041b"+
		"\u041c\7o\2\2\u041c\u0420\7K\2\2\u041d\u041e\7o\2\2\u041e\u0420\7T\2\2"+
		"\u041f\u0415\3\2\2\2\u041f\u0417\3\2\2\2\u041f\u0419\3\2\2\2\u041f\u041b"+
		"\3\2\2\2\u041f\u041d\3\2\2\2\u041f\u0420\3\2\2\2\u0420\u0421\3\2\2\2\u0421"+
		"\u0422\5h\65\2\u0422\u0423\7\u0084\2\2\u0423\u0424\5\u009cO\2\u0424\u0425"+
		"\7\t\2\2\u0425\u042d\5\\/\2\u0426\u0427\7\b\2\2\u0427\u0428\5\u009cO\2"+
		"\u0428\u0429\7\t\2\2\u0429\u042a\5\\/\2\u042a\u042c\3\2\2\2\u042b\u0426"+
		"\3\2\2\2\u042c\u042f\3\2\2\2\u042d\u042b\3\2\2\2\u042d\u042e\3\2\2\2\u042e"+
		"\u0432\3\2\2\2\u042f\u042d\3\2\2\2\u0430\u0431\7\u0095\2\2\u0431\u0433"+
		"\5\\/\2\u0432\u0430\3\2\2\2\u0432\u0433\3\2\2\2\u0433E\3\2\2\2\u0434\u0436"+
		"\5f\64\2\u0435\u0434\3\2\2\2\u0435\u0436\3\2\2\2\u0436\u0437\3\2\2\2\u0437"+
		"\u0442\7\u008e\2\2\u0438\u0439\7o\2\2\u0439\u0443\7\u0080\2\2\u043a\u043b"+
		"\7o\2\2\u043b\u0443\7\34\2\2\u043c\u043d\7o\2\2\u043d\u0443\7}\2\2\u043e"+
		"\u043f\7o\2\2\u043f\u0443\7K\2\2\u0440\u0441\7o\2\2\u0441\u0443\7T\2\2"+
		"\u0442\u0438\3\2\2\2\u0442\u043a\3\2\2\2\u0442\u043c\3\2\2\2\u0442\u043e"+
		"\3\2\2\2\u0442\u0440\3\2\2\2\u0442\u0443\3\2\2\2\u0443\u0444\3\2\2\2\u0444"+
		"\u0445\5h\65\2\u0445\u0446\7\u0084\2\2\u0446\u0447\5\u009cO\2\u0447\u0448"+
		"\7\t\2\2\u0448\u0450\5\\/\2\u0449\u044a\7\b\2\2\u044a\u044b\5\u009cO\2"+
		"\u044b\u044c\7\t\2\2\u044c\u044d\5\\/\2\u044d\u044f\3\2\2\2\u044e\u0449"+
		"\3\2\2\2\u044f\u0452\3\2\2\2\u0450\u044e\3\2\2\2\u0450\u0451\3\2\2\2\u0451"+
		"\u0455\3\2\2\2\u0452\u0450\3\2\2\2\u0453\u0454\7\u0095\2\2\u0454\u0456"+
		"\5\\/\2\u0455\u0453\3\2\2\2\u0455\u0456\3\2\2\2\u0456\u0469\3\2\2\2\u0457"+
		"\u0458\7p\2\2\u0458\u0459\7+\2\2\u0459\u045e\5j\66\2\u045a\u045b\7\b\2"+
		"\2\u045b\u045d\5j\66\2\u045c\u045a\3\2\2\2\u045d\u0460\3\2\2\2\u045e\u045c"+
		"\3\2\2\2\u045e\u045f\3\2\2\2\u045f\u0462\3\2\2\2\u0460\u045e\3\2\2\2\u0461"+
		"\u0457\3\2\2\2\u0461\u0462\3\2\2\2\u0462\u0463\3\2\2\2\u0463\u0464\7e"+
		"\2\2\u0464\u0467\5\\/\2\u0465\u0466\t\4\2\2\u0466\u0468\5\\/\2\u0467\u0465"+
		"\3\2\2\2\u0467\u0468\3\2\2\2\u0468\u046a\3\2\2\2\u0469\u0461\3\2\2\2\u0469"+
		"\u046a\3\2\2\2\u046aG\3\2\2\2\u046b\u046c\7\u0090\2\2\u046cI\3\2\2\2\u046d"+
		"\u046f\5\u009cO\2\u046e\u0470\5L\'\2\u046f\u046e\3\2\2\2\u046f\u0470\3"+
		"\2\2\2\u0470\u0474\3\2\2\2\u0471\u0473\5N(\2\u0472\u0471\3\2\2\2\u0473"+
		"\u0476\3\2\2\2\u0474\u0472\3\2\2\2\u0474\u0475\3\2\2\2\u0475K\3\2\2\2"+
		"\u0476\u0474\3\2\2\2\u0477\u0479\5\u008cG\2\u0478\u0477\3\2\2\2\u0479"+
		"\u047a\3\2\2\2\u047a\u047b\3\2\2\2\u047a\u0478\3\2\2\2\u047b\u0486\3\2"+
		"\2\2\u047c\u047d\7\6\2\2\u047d\u047e\5~@\2\u047e\u047f\7\7\2\2\u047f\u0487"+
		"\3\2\2\2\u0480\u0481\7\6\2\2\u0481\u0482\5~@\2\u0482\u0483\7\b\2\2\u0483"+
		"\u0484\5~@\2\u0484\u0485\7\7\2\2\u0485\u0487\3\2\2\2\u0486\u047c\3\2\2"+
		"\2\u0486\u0480\3\2\2\2\u0486\u0487\3\2\2\2\u0487M\3\2\2\2\u0488\u0489"+
		"\7\64\2\2\u0489\u048b\5\u008cG\2\u048a\u0488\3\2\2\2\u048a\u048b\3\2\2"+
		"\2\u048b\u04ad\3\2\2\2\u048c\u048d\7t\2\2\u048d\u048f\7b\2\2\u048e\u0490"+
		"\t\7\2\2\u048f\u048e\3\2\2\2\u048f\u0490\3\2\2\2\u0490\u0491\3\2\2\2\u0491"+
		"\u0493\5P)\2\u0492\u0494\7\'\2\2\u0493\u0492\3\2\2\2\u0493\u0494\3\2\2"+
		"\2\u0494\u04ae\3\2\2\2\u0495\u0497\7i\2\2\u0496\u0495\3\2\2\2\u0496\u0497"+
		"\3\2\2\2\u0497\u0498\3\2\2\2\u0498\u0499\7k\2\2\u0499\u04ae\5P)\2\u049a"+
		"\u049b\7\u008d\2\2\u049b\u04ae\5P)\2\u049c\u049d\7/\2\2\u049d\u049e\7"+
		"\6\2\2\u049e\u049f\5\\/\2\u049f\u04a0\7\7\2\2\u04a0\u04ae\3\2\2\2\u04a1"+
		"\u04a8\7;\2\2\u04a2\u04a9\5~@\2\u04a3\u04a9\5\u0080A\2\u04a4\u04a5\7\6"+
		"\2\2\u04a5\u04a6\5\\/\2\u04a6\u04a7\7\7\2\2\u04a7\u04a9\3\2\2\2\u04a8"+
		"\u04a2\3\2\2\2\u04a8\u04a3\3\2\2\2\u04a8\u04a4\3\2\2\2\u04a9\u04ae\3\2"+
		"\2\2\u04aa\u04ab\7\60\2\2\u04ab\u04ae\5\u009eP\2\u04ac\u04ae\5^\60\2\u04ad"+
		"\u048c\3\2\2\2\u04ad\u0496\3\2\2\2\u04ad\u049a\3\2\2\2\u04ad\u049c\3\2"+
		"\2\2\u04ad\u04a1\3\2\2\2\u04ad\u04aa\3\2\2\2\u04ad\u04ac\3\2\2\2\u04ae"+
		"O\3\2\2\2\u04af\u04b0\7n\2\2\u04b0\u04b1\7\63\2\2\u04b1\u04b3\t\b\2\2"+
		"\u04b2\u04af\3\2\2\2\u04b2\u04b3\3\2\2\2\u04b3Q\3\2\2\2\u04b4\u04b6\7"+
		"\u009e\2\2\u04b5\u04b7\5T+\2\u04b6\u04b5\3\2\2\2\u04b6\u04b7\3\2\2\2\u04b7"+
		"\u04b9\3\2\2\2\u04b8\u04b4\3\2\2\2\u04b8\u04b9\3\2\2\2\u04b9S\3\2\2\2"+
		"\u04ba\u04bb\7\6\2\2\u04bb\u04bc\7\u009f\2\2\u04bc\u04bd\7\6\2\2\u04bd"+
		"\u04be\5\u00b2Z\2\u04be\u04bf\7\7\2\2\u04bf\u04c1\7\b\2\2\u04c0\u04c2"+
		"\5\u00b6\\\2\u04c1\u04c0\3\2\2\2\u04c1\u04c2\3\2\2\2\u04c2\u04c4\3\2\2"+
		"\2\u04c3\u04c5\5\u00b2Z\2\u04c4\u04c3\3\2\2\2\u04c4\u04c5\3\2\2\2\u04c5"+
		"\u04c6\3\2\2\2\u04c6\u04cd\7\7\2\2\u04c7\u04c9\5\u00b2Z\2\u04c8\u04c7"+
		"\3\2\2\2\u04c8\u04c9\3\2\2\2\u04c9\u04ce\3\2\2\2\u04ca\u04cc\5\u00b6\\"+
		"\2\u04cb\u04ca\3\2\2\2\u04cb\u04cc\3\2\2\2\u04cc\u04ce\3\2\2\2\u04cd\u04c8"+
		"\3\2\2\2\u04cd\u04cb\3\2\2\2\u04ceU\3\2\2\2\u04cf\u04d1\7\u009e\2\2\u04d0"+
		"\u04d2\5T+\2\u04d1\u04d0\3\2\2\2\u04d1\u04d2\3\2\2\2\u04d2\u04d4\3\2\2"+
		"\2\u04d3\u04cf\3\2\2\2\u04d3\u04d4\3\2\2\2\u04d4W\3\2\2\2\u04d5\u04d6"+
		"\5\u00b6\\\2\u04d6\u04d7\7\5\2\2\u04d7\u04d8\5\u00b6\\\2\u04d8Y\3\2\2"+
		"\2\u04d9\u04da\7\3\2\2\u04da\u04df\5\u00b6\\\2\u04db\u04dc\7\b\2\2\u04dc"+
		"\u04de\5\u00b6\\\2\u04dd\u04db\3\2\2\2\u04de\u04e1\3\2\2\2\u04df\u04dd"+
		"\3\2\2\2\u04df\u04e0\3\2\2\2\u04e0\u04e2\3\2\2\2\u04e1\u04df\3\2\2\2\u04e2"+
		"\u04e3\7\3\2\2\u04e3[\3\2\2\2\u04e4\u04e5\b/\1\2\u04e5\u0533\5\u0080A"+
		"\2\u04e6\u0533\7\u00a3\2\2\u04e7\u0533\5\u00b2Z\2\u04e8\u0533\5Z.\2\u04e9"+
		"\u04ea\5\u008eH\2\u04ea\u04f7\7\6\2\2\u04eb\u04ed\7A\2\2\u04ec\u04eb\3"+
		"\2\2\2\u04ec\u04ed\3\2\2\2\u04ed\u04ee\3\2\2\2\u04ee\u04f3\5\\/\2\u04ef"+
		"\u04f0\7\b\2\2\u04f0\u04f2\5\\/\2\u04f1\u04ef\3\2\2\2\u04f2\u04f5\3\2"+
		"\2\2\u04f3\u04f1\3\2\2\2\u04f3\u04f4\3\2\2\2\u04f4\u04f8\3\2\2\2\u04f5"+
		"\u04f3\3\2\2\2\u04f6\u04f8\7\n\2\2\u04f7\u04ec\3\2\2\2\u04f7\u04f6\3\2"+
		"\2\2\u04f7\u04f8\3\2\2\2\u04f8\u04f9\3\2\2\2\u04f9\u04fa\7\7\2\2\u04fa"+
		"\u0533\3\2\2\2\u04fb\u04fc\5\u0090I\2\u04fc\u04fd\7\5\2\2\u04fd\u04ff"+
		"\3\2\2\2\u04fe\u04fb\3\2\2\2\u04fe\u04ff\3\2\2\2\u04ff\u0500\3\2\2\2\u0500"+
		"\u0501\5\u0096L\2\u0501\u0502\7\5\2\2\u0502\u0504\3\2\2\2\u0503\u04fe"+
		"\3\2\2\2\u0503\u0504\3\2\2\2\u0504\u0505\3\2\2\2\u0505\u0533\5\u009cO"+
		"\2\u0506\u0507\5\u0082B\2\u0507\u0508\5\\/\26\u0508\u0533\3\2\2\2\u0509"+
		"\u050a\7\6\2\2\u050a\u050b\5\\/\2\u050b\u050c\7\7\2\2\u050c\u0533\3\2"+
		"\2\2\u050d\u050e\7.\2\2\u050e\u050f\7\6\2\2\u050f\u0510\5\\/\2\u0510\u0511"+
		"\7$\2\2\u0511\u0512\5L\'\2\u0512\u0513\7\7\2\2\u0513\u0533\3\2\2\2\u0514"+
		"\u0516\7i\2\2\u0515\u0514\3\2\2\2\u0515\u0516\3\2\2\2\u0516\u0517\3\2"+
		"\2\2\u0517\u0519\7I\2\2\u0518\u0515\3\2\2\2\u0518\u0519\3\2\2\2\u0519"+
		"\u051a\3\2\2\2\u051a\u051b\7\6\2\2\u051b\u051c\5> \2\u051c\u051d\7\7\2"+
		"\2\u051d\u0533\3\2\2\2\u051e\u0520\7-\2\2\u051f\u0521\5\\/\2\u0520\u051f"+
		"\3\2\2\2\u0520\u0521\3\2\2\2\u0521\u0527\3\2\2\2\u0522\u0523\7\u0094\2"+
		"\2\u0523\u0524\5\\/\2\u0524\u0525\7\u0088\2\2\u0525\u0526\5\\/\2\u0526"+
		"\u0528\3\2\2\2\u0527\u0522\3\2\2\2\u0528\u0529\3\2\2\2\u0529\u0527\3\2"+
		"\2\2\u0529\u052a\3\2\2\2\u052a\u052d\3\2\2\2\u052b\u052c\7D\2\2\u052c"+
		"\u052e\5\\/\2\u052d\u052b\3\2\2\2\u052d\u052e\3\2\2\2\u052e\u052f\3\2"+
		"\2\2\u052f\u0530\7E\2\2\u0530\u0533\3\2\2\2\u0531\u0533\5`\61\2\u0532"+
		"\u04e4\3\2\2\2\u0532\u04e6\3\2\2\2\u0532\u04e7\3\2\2\2\u0532\u04e8\3\2"+
		"\2\2\u0532\u04e9\3\2\2\2\u0532\u0503\3\2\2\2\u0532\u0506\3\2\2\2\u0532"+
		"\u0509\3\2\2\2\u0532\u050d\3\2\2\2\u0532\u0518\3\2\2\2\u0532\u051e\3\2"+
		"\2\2\u0532\u0531\3\2\2\2\u0533\u058b\3\2\2\2\u0534\u0535\f\25\2\2\u0535"+
		"\u0536\7\16\2\2\u0536\u058a\5\\/\26\u0537\u0538\f\24\2\2\u0538\u0539\t"+
		"\t\2\2\u0539\u058a\5\\/\25\u053a\u053b\f\23\2\2\u053b\u053c\t\n\2\2\u053c"+
		"\u058a\5\\/\24\u053d\u053e\f\22\2\2\u053e\u053f\t\13\2\2\u053f\u058a\5"+
		"\\/\23\u0540\u0541\f\21\2\2\u0541\u0542\t\f\2\2\u0542\u058a\5\\/\22\u0543"+
		"\u0544\f\20\2\2\u0544\u0545\t\r\2\2\u0545\u058a\5\\/\21\u0546\u0547\f"+
		"\17\2\2\u0547\u0548\7#\2\2\u0548\u058a\5\\/\20\u0549\u054a\f\16\2\2\u054a"+
		"\u054b\7o\2\2\u054b\u058a\5\\/\17\u054c\u054d\f\b\2\2\u054d\u054f\7_\2"+
		"\2\u054e\u0550\7i\2\2\u054f\u054e\3\2\2\2\u054f\u0550\3\2\2\2\u0550\u0551"+
		"\3\2\2\2\u0551\u058a\5\\/\t\u0552\u0554\f\7\2\2\u0553\u0555\7i\2\2\u0554"+
		"\u0553\3\2\2\2\u0554\u0555\3\2\2\2\u0555\u0556\3\2\2\2\u0556\u0557\7*"+
		"\2\2\u0557\u0558\5\\/\2\u0558\u0559\7#\2\2\u0559\u055a\5\\/\b\u055a\u058a"+
		"\3\2\2\2\u055b\u055c\f\13\2\2\u055c\u055d\7\60\2\2\u055d\u058a\5\u009e"+
		"P\2\u055e\u0560\f\n\2\2\u055f\u0561\7i\2\2\u0560\u055f\3\2\2\2\u0560\u0561"+
		"\3\2\2\2\u0561\u0562\3\2\2\2\u0562\u0563\t\16\2\2\u0563\u0566\5\\/\2\u0564"+
		"\u0565\7F\2\2\u0565\u0567\5\\/\2\u0566\u0564\3\2\2\2\u0566\u0567\3\2\2"+
		"\2\u0567\u058a\3\2\2\2\u0568\u056d\f\t\2\2\u0569\u056e\7`\2\2\u056a\u056e"+
		"\7j\2\2\u056b\u056c\7i\2\2\u056c\u056e\7k\2\2\u056d\u0569\3\2\2\2\u056d"+
		"\u056a\3\2\2\2\u056d\u056b\3\2\2\2\u056e\u058a\3\2\2\2\u056f\u0571\f\6"+
		"\2\2\u0570\u0572\7i\2\2\u0571\u0570\3\2\2\2\u0571\u0572\3\2\2\2\u0572"+
		"\u0573\3\2\2\2\u0573\u0587\7V\2\2\u0574\u057e\7\6\2\2\u0575\u057f\5> "+
		"\2\u0576\u057b\5\\/\2\u0577\u0578\7\b\2\2\u0578\u057a\5\\/\2\u0579\u0577"+
		"\3\2\2\2\u057a\u057d\3\2\2\2\u057b\u0579\3\2\2\2\u057b\u057c\3\2\2\2\u057c"+
		"\u057f\3\2\2\2\u057d\u057b\3\2\2\2\u057e\u0575\3\2\2\2\u057e\u0576\3\2"+
		"\2\2\u057e\u057f\3\2\2\2\u057f\u0580\3\2\2\2\u0580\u0588\7\7\2\2\u0581"+
		"\u0582\5\u0090I\2\u0582\u0583\7\5\2\2\u0583\u0585\3\2\2\2\u0584\u0581"+
		"\3\2\2\2\u0584\u0585\3\2\2\2\u0585\u0586\3\2\2\2\u0586\u0588\5\u0096L"+
		"\2\u0587\u0574\3\2\2\2\u0587\u0584\3\2\2\2\u0588\u058a\3\2\2\2\u0589\u0534"+
		"\3\2\2\2\u0589\u0537\3\2\2\2\u0589\u053a\3\2\2\2\u0589\u053d\3\2\2\2\u0589"+
		"\u0540\3\2\2\2\u0589\u0543\3\2\2\2\u0589\u0546\3\2\2\2\u0589\u0549\3\2"+
		"\2\2\u0589\u054c\3\2\2\2\u0589\u0552\3\2\2\2\u0589\u055b\3\2\2\2\u0589"+
		"\u055e\3\2\2\2\u0589\u0568\3\2\2\2\u0589\u056f\3\2\2\2\u058a\u058d\3\2"+
		"\2\2\u058b\u0589\3\2\2\2\u058b\u058c\3\2\2\2\u058c]\3\2\2\2\u058d\u058b"+
		"\3\2\2\2\u058e\u058f\7x\2\2\u058f\u059b\5\u00a0Q\2\u0590\u0591\7\6\2\2"+
		"\u0591\u0596\5\u009cO\2\u0592\u0593\7\b\2\2\u0593\u0595\5\u009cO\2\u0594"+
		"\u0592\3\2\2\2\u0595\u0598\3\2\2\2\u0596\u0594\3\2\2\2\u0596\u0597\3\2"+
		"\2\2\u0597\u0599\3\2\2\2\u0598\u0596\3\2\2\2\u0599\u059a\7\7\2\2\u059a"+
		"\u059c\3\2\2\2\u059b\u0590\3\2\2\2\u059b\u059c\3\2\2\2\u059c\u05af\3\2"+
		"\2\2\u059d\u059e\7n\2\2\u059e\u05a7\t\17\2\2\u059f\u05a0\7\u0084\2\2\u05a0"+
		"\u05a8\7k\2\2\u05a1\u05a2\7\u0084\2\2\u05a2\u05a8\7;\2\2\u05a3\u05a8\7"+
		",\2\2\u05a4\u05a8\7~\2\2\u05a5\u05a6\7h\2\2\u05a6\u05a8\7\35\2\2\u05a7"+
		"\u059f\3\2\2\2\u05a7\u05a1\3\2\2\2\u05a7\u05a3\3\2\2\2\u05a7\u05a4\3\2"+
		"\2\2\u05a7\u05a5\3\2\2\2\u05a8\u05ac\3\2\2\2\u05a9\u05aa\7f\2\2\u05aa"+
		"\u05ac\5\u008cG\2\u05ab\u059d\3\2\2\2\u05ab\u05a9\3\2\2\2\u05ac\u05ae"+
		"\3\2\2\2\u05ad\u05ab\3\2\2\2\u05ae\u05b1\3\2\2\2\u05af\u05ad\3\2\2\2\u05af"+
		"\u05b0\3\2\2\2\u05b0\u05bc\3\2\2\2\u05b1\u05af\3\2\2\2\u05b2\u05b4\7i"+
		"\2\2\u05b3\u05b2\3\2\2\2\u05b3\u05b4\3\2\2\2\u05b4\u05b5\3\2\2\2\u05b5"+
		"\u05ba\7<\2\2\u05b6\u05b7\7Y\2\2\u05b7\u05bb\7=\2\2\u05b8\u05b9\7Y\2\2"+
		"\u05b9\u05bb\7U\2\2\u05ba\u05b6\3\2\2\2\u05ba\u05b8\3\2\2\2\u05ba\u05bb"+
		"\3\2\2\2\u05bb\u05bd\3\2\2\2\u05bc\u05b3\3\2\2\2\u05bc\u05bd\3\2\2\2\u05bd"+
		"_\3\2\2\2\u05be\u05bf\7v\2\2\u05bf\u05c4\7\6\2\2\u05c0\u05c5\7T\2\2\u05c1"+
		"\u05c2\t\20\2\2\u05c2\u05c3\7\b\2\2\u05c3\u05c5\5\u0084C\2\u05c4\u05c0"+
		"\3\2\2\2\u05c4\u05c1\3\2\2\2\u05c5\u05c6\3\2\2\2\u05c6\u05c7\7\7\2\2\u05c7"+
		"a\3\2\2\2\u05c8\u05cb\5\u009cO\2\u05c9\u05ca\7\60\2\2\u05ca\u05cc\5\u009e"+
		"P\2\u05cb\u05c9\3\2\2\2\u05cb\u05cc\3\2\2\2\u05cc\u05ce\3\2\2\2\u05cd"+
		"\u05cf\t\7\2\2\u05ce\u05cd\3\2\2\2\u05ce\u05cf\3\2\2\2\u05cfc\3\2\2\2"+
		"\u05d0\u05d1\7\64\2\2\u05d1\u05d3\5\u008cG\2\u05d2\u05d0\3\2\2\2\u05d2"+
		"\u05d3\3\2\2\2\u05d3\u05f8\3\2\2\2\u05d4\u05d5\7t\2\2\u05d5\u05d8\7b\2"+
		"\2\u05d6\u05d8\7\u008d\2\2\u05d7\u05d4\3\2\2\2\u05d7\u05d6\3\2\2\2\u05d8"+
		"\u05d9\3\2\2\2\u05d9\u05da\7\6\2\2\u05da\u05df\5b\62\2\u05db\u05dc\7\b"+
		"\2\2\u05dc\u05de\5b\62\2\u05dd\u05db\3\2\2\2\u05de\u05e1\3\2\2\2\u05df"+
		"\u05dd\3\2\2\2\u05df\u05e0\3\2\2\2\u05e0\u05e2\3\2\2\2\u05e1\u05df\3\2"+
		"\2\2\u05e2\u05e3\7\7\2\2\u05e3\u05e4\5P)\2\u05e4\u05f9\3\2\2\2\u05e5\u05e6"+
		"\7/\2\2\u05e6\u05e7\7\6\2\2\u05e7\u05e8\5\\/\2\u05e8\u05e9\7\7\2\2\u05e9"+
		"\u05f9\3\2\2\2\u05ea\u05eb\7M\2\2\u05eb\u05ec\7b\2\2\u05ec\u05ed\7\6\2"+
		"\2\u05ed\u05f2\5\u009cO\2\u05ee\u05ef\7\b\2\2\u05ef\u05f1\5\u009cO\2\u05f0"+
		"\u05ee\3\2\2\2\u05f1\u05f4\3\2\2\2\u05f2\u05f0\3\2\2\2\u05f2\u05f3\3\2"+
		"\2\2\u05f3\u05f5\3\2\2\2\u05f4\u05f2\3\2\2\2\u05f5\u05f6\7\7\2\2\u05f6"+
		"\u05f7\5^\60\2\u05f7\u05f9\3\2\2\2\u05f8\u05d7\3\2\2\2\u05f8\u05e5\3\2"+
		"\2\2\u05f8\u05ea\3\2\2\2\u05f9e\3\2\2\2\u05fa\u05fc\7\u0096\2\2\u05fb"+
		"\u05fd\7w\2\2\u05fc\u05fb\3\2\2\2\u05fc\u05fd\3\2\2\2\u05fd\u05fe\3\2"+
		"\2\2\u05fe\u0603\5n8\2\u05ff\u0600\7\b\2\2\u0600\u0602\5n8\2\u0601\u05ff"+
		"\3\2\2\2\u0602\u0605\3\2\2\2\u0603\u0601\3\2\2\2\u0603\u0604\3\2\2\2\u0604"+
		"g\3\2\2\2\u0605\u0603\3\2\2\2\u0606\u0607\5\u0090I\2\u0607\u0608\7\5\2"+
		"\2\u0608\u060a\3\2\2\2\u0609\u0606\3\2\2\2\u0609\u060a\3\2\2\2\u060a\u060b"+
		"\3\2\2\2\u060b\u0611\5\u0096L\2\u060c\u060d\7X\2\2\u060d\u060e\7+\2\2"+
		"\u060e\u0612\5\u00a2R\2\u060f\u0610\7i\2\2\u0610\u0612\7X\2\2\u0611\u060c"+
		"\3\2\2\2\u0611\u060f\3\2\2\2\u0611\u0612\3\2\2\2\u0612i\3\2\2\2\u0613"+
		"\u0616\5\\/\2\u0614\u0615\7\60\2\2\u0615\u0617\5\u009eP\2\u0616\u0614"+
		"\3\2\2\2\u0616\u0617\3\2\2\2\u0617\u0619\3\2\2\2\u0618\u061a\t\7\2\2\u0619"+
		"\u0618\3\2\2\2\u0619\u061a\3\2\2\2\u061a\u0620\3\2\2\2\u061b\u061c\5\\"+
		"/\2\u061c\u061d\7\13\2\2\u061d\u061e\5\\/\2\u061e\u0620\3\2\2\2\u061f"+
		"\u0613\3\2\2\2\u061f\u061b\3\2\2\2\u0620k\3\2\2\2\u0621\u0625\5~@\2\u0622"+
		"\u0625\5\u008cG\2\u0623\u0625\7\u00a4\2\2\u0624\u0621\3\2\2\2\u0624\u0622"+
		"\3\2\2\2\u0624\u0623\3\2\2\2\u0625m\3\2\2\2\u0626\u0632\5\u0096L\2\u0627"+
		"\u0628\7\6\2\2\u0628\u062d\5\u009cO\2\u0629\u062a\7\b\2\2\u062a\u062c"+
		"\5\u009cO\2\u062b\u0629\3\2\2\2\u062c\u062f\3\2\2\2\u062d\u062b\3\2\2"+
		"\2\u062d\u062e\3\2\2\2\u062e\u0630\3\2\2\2\u062f\u062d\3\2\2\2\u0630\u0631"+
		"\7\7\2\2\u0631\u0633\3\2\2\2\u0632\u0627\3\2\2\2\u0632\u0633\3\2\2\2\u0633"+
		"\u0634\3\2\2\2\u0634\u0635\7$\2\2\u0635\u0636\7\6\2\2\u0636\u0637\5> "+
		"\2\u0637\u0638\7\7\2\2\u0638o\3\2\2\2\u0639\u0646\7\n\2\2\u063a\u063b"+
		"\5\u0096L\2\u063b\u063c\7\5\2\2\u063c\u063d\7\n\2\2\u063d\u0646\3\2\2"+
		"\2\u063e\u0643\5\\/\2\u063f\u0641\7$\2\2\u0640\u063f\3\2\2\2\u0640\u0641"+
		"\3\2\2\2\u0641\u0642\3\2\2\2\u0642\u0644\5\u0088E\2\u0643\u0640\3\2\2"+
		"\2\u0643\u0644\3\2\2\2\u0644\u0646\3\2\2\2\u0645\u0639\3\2\2\2\u0645\u063a"+
		"\3\2\2\2\u0645\u063e\3\2\2\2\u0646q\3\2\2\2\u0647\u0648\5\u0092J\2\u0648"+
		"\u0649\7\5\2\2\u0649\u064b\3\2\2\2\u064a\u0647\3\2\2\2\u064a\u064b\3\2"+
		"\2\2\u064b\u064c\3\2\2\2\u064c\u0651\5\u0096L\2\u064d\u064f\7$\2\2\u064e"+
		"\u064d\3\2\2\2\u064e\u064f\3\2\2\2\u064f\u0650\3\2\2\2\u0650\u0652\5\u00ae"+
		"X\2\u0651\u064e\3\2\2\2\u0651\u0652\3\2\2\2\u0652\u0658\3\2\2\2\u0653"+
		"\u0654\7X\2\2\u0654\u0655\7+\2\2\u0655\u0659\5\u00a2R\2\u0656\u0657\7"+
		"i\2\2\u0657\u0659\7X\2\2\u0658\u0653\3\2\2\2\u0658\u0656\3\2\2\2\u0658"+
		"\u0659\3\2\2\2\u0659\u068a\3\2\2\2\u065a\u065b\5\u0092J\2\u065b\u065c"+
		"\7\5\2\2\u065c\u065e\3\2\2\2\u065d\u065a\3\2\2\2\u065d\u065e\3\2\2\2\u065e"+
		"\u065f\3\2\2\2\u065f\u0660\5\u0094K\2\u0660\u0669\7\6\2\2\u0661\u0666"+
		"\5\\/\2\u0662\u0663\7\b\2\2\u0663\u0665\5\\/\2\u0664\u0662\3\2\2\2\u0665"+
		"\u0668\3\2\2\2\u0666\u0664\3\2\2\2\u0666\u0667\3\2\2\2\u0667\u066a\3\2"+
		"\2\2\u0668\u0666\3\2\2\2\u0669\u0661\3\2\2\2\u0669\u066a\3\2\2\2\u066a"+
		"\u066b\3\2\2\2\u066b\u0670\7\7\2\2\u066c\u066e\7$\2\2\u066d\u066c\3\2"+
		"\2\2\u066d\u066e\3\2\2\2\u066e\u066f\3\2\2\2\u066f\u0671\5\u00aeX\2\u0670"+
		"\u066d\3\2\2\2\u0670\u0671\3\2\2\2\u0671\u068a\3\2\2\2\u0672\u067c\7\6"+
		"\2\2\u0673\u0678\5r:\2\u0674\u0675\7\b\2\2\u0675\u0677\5r:\2\u0676\u0674"+
		"\3\2\2\2\u0677\u067a\3\2\2\2\u0678\u0676\3\2\2\2\u0678\u0679\3\2\2\2\u0679"+
		"\u067d\3\2\2\2\u067a\u0678\3\2\2\2\u067b\u067d\5t;\2\u067c\u0673\3\2\2"+
		"\2\u067c\u067b\3\2\2\2\u067d\u067e\3\2\2\2\u067e\u067f\7\7\2\2\u067f\u068a"+
		"\3\2\2\2\u0680\u0681\7\6\2\2\u0681\u0682\5> \2\u0682\u0687\7\7\2\2\u0683"+
		"\u0685\7$\2\2\u0684\u0683\3\2\2\2\u0684\u0685\3\2\2\2\u0685\u0686\3\2"+
		"\2\2\u0686\u0688\5\u00aeX\2\u0687\u0684\3\2\2\2\u0687\u0688\3\2\2\2\u0688"+
		"\u068a\3\2\2\2\u0689\u064a\3\2\2\2\u0689\u065d\3\2\2\2\u0689\u0672\3\2"+
		"\2\2\u0689\u0680\3\2\2\2\u068as\3\2\2\2\u068b\u0692\5r:\2\u068c\u068d"+
		"\5v<\2\u068d\u068e\5r:\2\u068e\u068f\5x=\2\u068f\u0691\3\2\2\2\u0690\u068c"+
		"\3\2\2\2\u0691\u0694\3\2\2\2\u0692\u0690\3\2\2\2\u0692\u0693\3\2\2\2\u0693"+
		"u\3\2\2\2\u0694\u0692\3\2\2\2\u0695\u06a3\7\b\2\2\u0696\u0698\7g\2\2\u0697"+
		"\u0696\3\2\2\2\u0697\u0698\3\2\2\2\u0698\u069f\3\2\2\2\u0699\u069b\7c"+
		"\2\2\u069a\u069c\7q\2\2\u069b\u069a\3\2\2\2\u069b\u069c\3\2\2\2\u069c"+
		"\u06a0\3\2\2\2\u069d\u06a0\7Z\2\2\u069e\u06a0\7\66\2\2\u069f\u0699\3\2"+
		"\2\2\u069f\u069d\3\2\2\2\u069f\u069e\3\2\2\2\u069f\u06a0\3\2\2\2\u06a0"+
		"\u06a1\3\2\2\2\u06a1\u06a3\7a\2\2\u06a2\u0695\3\2\2\2\u06a2\u0697\3\2"+
		"\2\2\u06a3w\3\2\2\2\u06a4\u06a5\7n\2\2\u06a5\u06b3\5\\/\2\u06a6\u06a7"+
		"\7\u008f\2\2\u06a7\u06a8\7\6\2\2\u06a8\u06ad\5\u009cO\2\u06a9\u06aa\7"+
		"\b\2\2\u06aa\u06ac\5\u009cO\2\u06ab\u06a9\3\2\2\2\u06ac\u06af\3\2\2\2"+
		"\u06ad\u06ab\3\2\2\2\u06ad\u06ae\3\2\2\2\u06ae\u06b0\3\2\2\2\u06af\u06ad"+
		"\3\2\2\2\u06b0\u06b1\7\7\2\2\u06b1\u06b3\3\2\2\2\u06b2\u06a4\3\2\2\2\u06b2"+
		"\u06a6\3\2\2\2\u06b2\u06b3\3\2\2\2\u06b3y\3\2\2\2\u06b4\u06b6\7\u0083"+
		"\2\2\u06b5\u06b7\t\6\2\2\u06b6\u06b5\3\2\2\2\u06b6\u06b7\3\2\2\2\u06b7"+
		"\u06b8\3\2\2\2\u06b8\u06bd\5p9\2\u06b9\u06ba\7\b\2\2\u06ba\u06bc\5p9\2"+
		"\u06bb\u06b9\3\2\2\2\u06bc\u06bf\3\2\2\2\u06bd\u06bb\3\2\2\2\u06bd\u06be"+
		"\3\2\2\2\u06be\u06cc\3\2\2\2\u06bf\u06bd\3\2\2\2\u06c0\u06ca\7N\2\2\u06c1"+
		"\u06c6\5r:\2\u06c2\u06c3\7\b\2\2\u06c3\u06c5\5r:\2\u06c4\u06c2\3\2\2\2"+
		"\u06c5\u06c8\3\2\2\2\u06c6\u06c4\3\2\2\2\u06c6\u06c7\3\2\2\2\u06c7\u06cb"+
		"\3\2\2\2\u06c8\u06c6\3\2\2\2\u06c9\u06cb\5t;\2\u06ca\u06c1\3\2\2\2\u06ca"+
		"\u06c9\3\2\2\2\u06cb\u06cd\3\2\2\2\u06cc\u06c0\3\2\2\2\u06cc\u06cd\3\2"+
		"\2\2\u06cd\u06d0\3\2\2\2\u06ce\u06cf\7\u0095\2\2\u06cf\u06d1\5\\/\2\u06d0"+
		"\u06ce\3\2\2\2\u06d0\u06d1\3\2\2\2\u06d1\u06e0\3\2\2\2\u06d2\u06d3\7Q"+
		"\2\2\u06d3\u06d4\7+\2\2\u06d4\u06d9\5\\/\2\u06d5\u06d6\7\b\2\2\u06d6\u06d8"+
		"\5\\/\2\u06d7\u06d5\3\2\2\2\u06d8\u06db\3\2\2\2\u06d9\u06d7\3\2\2\2\u06d9"+
		"\u06da\3\2\2\2\u06da\u06de\3\2\2\2\u06db\u06d9\3\2\2\2\u06dc\u06dd\7R"+
		"\2\2\u06dd\u06df\5\\/\2\u06de\u06dc\3\2\2\2\u06de\u06df\3\2\2\2\u06df"+
		"\u06e1\3\2\2\2\u06e0\u06d2\3\2\2\2\u06e0\u06e1\3\2\2\2\u06e1\u06ff\3\2"+
		"\2\2\u06e2\u06e3\7\u0091\2\2\u06e3\u06e4\7\6\2\2\u06e4\u06e9\5\\/\2\u06e5"+
		"\u06e6\7\b\2\2\u06e6\u06e8\5\\/\2\u06e7\u06e5\3\2\2\2\u06e8\u06eb\3\2"+
		"\2\2\u06e9\u06e7\3\2\2\2\u06e9\u06ea\3\2\2\2\u06ea\u06ec\3\2\2\2\u06eb"+
		"\u06e9\3\2\2\2\u06ec\u06fb\7\7\2\2\u06ed\u06ee\7\b\2\2\u06ee\u06ef\7\6"+
		"\2\2\u06ef\u06f4\5\\/\2\u06f0\u06f1\7\b\2\2\u06f1\u06f3\5\\/\2\u06f2\u06f0"+
		"\3\2\2\2\u06f3\u06f6\3\2\2\2\u06f4\u06f2\3\2\2\2\u06f4\u06f5\3\2\2\2\u06f5"+
		"\u06f7\3\2\2\2\u06f6\u06f4\3\2\2\2\u06f7\u06f8\7\7\2\2\u06f8\u06fa\3\2"+
		"\2\2\u06f9\u06ed\3\2\2\2\u06fa\u06fd\3\2\2\2\u06fb\u06f9\3\2\2\2\u06fb"+
		"\u06fc\3\2\2\2\u06fc\u06ff\3\2\2\2\u06fd\u06fb\3\2\2\2\u06fe\u06b4\3\2"+
		"\2\2\u06fe\u06e2\3\2\2\2\u06ff{\3\2\2\2\u0700\u0706\7\u008c\2\2\u0701"+
		"\u0702\7\u008c\2\2\u0702\u0706\7 \2\2\u0703\u0706\7]\2\2\u0704\u0706\7"+
		"G\2\2\u0705\u0700\3\2\2\2\u0705\u0701\3\2\2\2\u0705\u0703\3\2\2\2\u0705"+
		"\u0704\3\2\2\2\u0706}\3\2\2\2\u0707\u0709\t\n\2\2\u0708\u0707\3\2\2\2"+
		"\u0708\u0709\3\2\2\2\u0709\u070a\3\2\2\2\u070a\u070b\7\u00a2\2\2\u070b"+
		"\177\3\2\2\2\u070c\u070d\t\21\2\2\u070d\u0081\3\2\2\2\u070e\u070f\t\22"+
		"\2\2\u070f\u0083\3\2\2\2\u0710\u0711\7\u00a4\2\2\u0711\u0085\3\2\2\2\u0712"+
		"\u0715\5\\/\2\u0713\u0715\5J&\2\u0714\u0712\3\2\2\2\u0714\u0713\3\2\2"+
		"\2\u0715\u0087\3\2\2\2\u0716\u0717\t\23\2\2\u0717\u0089\3\2\2\2\u0718"+
		"\u0719\t\24\2\2\u0719\u008b\3\2\2\2\u071a\u071b\5\u00b6\\\2\u071b\u008d"+
		"\3\2\2\2\u071c\u071d\5\u00b6\\\2\u071d\u008f\3\2\2\2\u071e\u071f\5\u00b6"+
		"\\\2\u071f\u0091\3\2\2\2\u0720\u0721\5\u00b6\\\2\u0721\u0093\3\2\2\2\u0722"+
		"\u0723\5\u00b6\\\2\u0723\u0095\3\2\2\2\u0724\u0725\5\u00b6\\\2\u0725\u0097"+
		"\3\2\2\2\u0726\u0727\5\u00b6\\\2\u0727\u0099\3\2\2\2\u0728\u0729\5\u00b6"+
		"\\\2\u0729\u009b\3\2\2\2\u072a\u072b\5\u00b6\\\2\u072b\u009d\3\2\2\2\u072c"+
		"\u072d\5\u00b6\\\2\u072d\u009f\3\2\2\2\u072e\u072f\5\u00b6\\\2\u072f\u00a1"+
		"\3\2\2\2\u0730\u0731\5\u00b6\\\2\u0731\u00a3\3\2\2\2\u0732\u0733\5\u00b6"+
		"\\\2\u0733\u00a5\3\2\2\2\u0734\u0735\5\u00b6\\\2\u0735\u00a7\3\2\2\2\u0736"+
		"\u0737\5\u00b6\\\2\u0737\u00a9\3\2\2\2\u0738\u0739\5\u00b6\\\2\u0739\u00ab"+
		"\3\2\2\2\u073a\u073b\5\u00b6\\\2\u073b\u00ad\3\2\2\2\u073c\u0743\7\u00a1"+
		"\2\2\u073d\u0743\7\u00a4\2\2\u073e\u073f\7\6\2\2\u073f\u0740\5\u00aeX"+
		"\2\u0740\u0741\7\7\2\2\u0741\u0743\3\2\2\2\u0742\u073c\3\2\2\2\u0742\u073d"+
		"\3\2\2\2\u0742\u073e\3\2\2\2\u0743\u00af\3\2\2\2\u0744\u0745\5\u00b6\\"+
		"\2\u0745\u00b1\3\2\2\2\u0746\u0747\5\u00b6\\\2\u0747\u0748\7\5\2\2\u0748"+
		"\u0749\5\u00b6\\\2\u0749\u00b3\3\2\2\2\u074a\u074b\b[\1\2\u074b\u0753"+
		"\7\u00a1\2\2\u074c\u074d\5\u00b6\\\2\u074d\u074e\7\5\2\2\u074e\u074f\5"+
		"\u00b6\\\2\u074f\u0750\7\b\2\2\u0750\u0751\5\u00b6\\\2\u0751\u0753\3\2"+
		"\2\2\u0752\u074a\3\2\2\2\u0752\u074c\3\2\2\2\u0753\u0759\3\2\2\2\u0754"+
		"\u0755\f\4\2\2\u0755\u0756\7\b\2\2\u0756\u0758\5\u00b4[\5\u0757\u0754"+
		"\3\2\2\2\u0758\u075b\3\2\2\2\u0759\u0757\3\2\2\2\u0759\u075a\3\2\2\2\u075a"+
		"\u00b5\3\2\2\2\u075b\u0759\3\2\2\2\u075c\u075d\b\\\1\2\u075d\u0769\7\u00a1"+
		"\2\2\u075e\u0769\5\u008aF\2\u075f\u0769\7\u00a4\2\2\u0760\u0761\7\6\2"+
		"\2\u0761\u0762\5\u00b6\\\2\u0762\u0763\7\7\2\2\u0763\u0769\3\2\2\2\u0764"+
		"\u0765\7\3\2\2\u0765\u0766\5\u00b6\\\2\u0766\u0767\7\3\2\2\u0767\u0769"+
		"\3\2\2\2\u0768\u075c\3\2\2\2\u0768\u075e\3\2\2\2\u0768\u075f\3\2\2\2\u0768"+
		"\u0760\3\2\2\2\u0768\u0764\3\2\2\2\u0769\u076f\3\2\2\2\u076a\u076b\f\4"+
		"\2\2\u076b\u076c\7\5\2\2\u076c\u076e\5\u00b6\\\5\u076d\u076a\3\2\2\2\u076e"+
		"\u0771\3\2\2\2\u076f\u076d\3\2\2\2\u076f\u0770\3\2\2\2\u0770\u00b7\3\2"+
		"\2\2\u0771\u076f\3\2\2\2\u0772\u0773\b]\1\2\u0773\u077d\7\u00a1\2\2\u0774"+
		"\u077d\5\u008aF\2\u0775\u077d\7\u00a4\2\2\u0776\u0777\7\b\2\2\u0777\u0778"+
		"\5\u00b8]\2\u0778\u0779\7\b\2\2\u0779\u077d\3\2\2\2\u077a\u077b\7\b\2"+
		"\2\u077b\u077d\5\u00b8]\3\u077c\u0772\3\2\2\2\u077c\u0774\3\2\2\2\u077c"+
		"\u0775\3\2\2\2\u077c\u0776\3\2\2\2\u077c\u077a\3\2\2\2\u077d\u0782\3\2"+
		"\2\2\u077e\u077f\f\5\2\2\u077f\u0781\7\b\2\2\u0780\u077e\3\2\2\2\u0781"+
		"\u0784\3\2\2\2\u0782\u0780\3\2\2\2\u0782\u0783\3\2\2\2\u0783\u00b9\3\2"+
		"\2\2\u0784\u0782\3\2\2\2\u0110\u00bc\u00be\u00c9\u00d0\u00d5\u00db\u00e1"+
		"\u00e3\u0103\u010a\u0112\u0115\u011e\u0122\u012a\u012e\u0130\u0135\u0137"+
		"\u013a\u013f\u0143\u0148\u0151\u0154\u015a\u015c\u0160\u0166\u016b\u0176"+
		"\u017c\u0180\u0186\u018b\u0194\u019b\u01a1\u01a5\u01a9\u01af\u01b4\u01bb"+
		"\u01c6\u01c9\u01cb\u01d1\u01d7\u01db\u01e2\u01e8\u01ee\u01f4\u01f9\u0205"+
		"\u020a\u0215\u021a\u021d\u0224\u0227\u022e\u0237\u023a\u0240\u0242\u0246"+
		"\u024e\u0253\u025b\u0260\u0268\u026d\u0275\u027a\u027f\u0287\u0291\u0294"+
		"\u029a\u029c\u029f\u02b2\u02b8\u02c1\u02c6\u02cf\u02da\u02e1\u02e7\u02ed"+
		"\u02f6\u02fd\u0301\u0303\u0307\u030e\u0310\u0314\u0317\u031d\u0327\u032a"+
		"\u0330\u0332\u0335\u033d\u0347\u034a\u0351\u0353\u0357\u035b\u0362\u0372"+
		"\u037e\u0381\u0383\u0387\u038f\u0393\u0395\u0399\u03a5\u03ac\u03ae\u03b2"+
		"\u03b8\u03c3\u03cb\u03d2\u03d4\u03d8\u03de\u03e3\u03e5\u03ee\u03f9\u0400"+
		"\u0403\u0407\u0412\u041f\u042d\u0432\u0435\u0442\u0450\u0455\u045e\u0461"+
		"\u0467\u0469\u046f\u0474\u047a\u0486\u048a\u048f\u0493\u0496\u04a8\u04ad"+
		"\u04b2\u04b6\u04b8\u04c1\u04c4\u04c8\u04cb\u04cd\u04d1\u04d3\u04df\u04ec"+
		"\u04f3\u04f7\u04fe\u0503\u0515\u0518\u0520\u0529\u052d\u0532\u054f\u0554"+
		"\u0560\u0566\u056d\u0571\u057b\u057e\u0584\u0587\u0589\u058b\u0596\u059b"+
		"\u05a7\u05ab\u05af\u05b3\u05ba\u05bc\u05c4\u05cb\u05ce\u05d2\u05d7\u05df"+
		"\u05f2\u05f8\u05fc\u0603\u0609\u0611\u0616\u0619\u061f\u0624\u062d\u0632"+
		"\u0640\u0643\u0645\u064a\u064e\u0651\u0658\u065d\u0666\u0669\u066d\u0670"+
		"\u0678\u067c\u0684\u0687\u0689\u0692\u0697\u069b\u069f\u06a2\u06ad\u06b2"+
		"\u06b6\u06bd\u06c6\u06ca\u06cc\u06d0\u06d9\u06de\u06e0\u06e9\u06f4\u06fb"+
		"\u06fe\u0705\u0708\u0714\u0742\u0752\u0759\u0768\u076f\u077c\u0782";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}