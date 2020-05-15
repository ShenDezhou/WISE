/* Generated By:JavaCC: Do not edit this line. QueryParser.java */
package org.apache.lucene.queryParser;

import java.util.Vector;
import java.io.*;
import org.apache.lucene.index.Term;
import org.apache.lucene.analysis.*;
import org.apache.lucene.search.*;

/**
 * This class is generated by JavaCC.  The only method that clients should need
 * to call is <a href="#parse">parse()</a>.
 *
 * The syntax for query strings is as follows:
 * A Query is a series of clauses.
 * A clause may be prefixed by: 
 * <ul>
 * <li> a plus (<code>+</code>) or a minus (<code>-</code>) sign, indicating
 * that the clause is required or prohibited respectively; or
 * <li> a term followed by a colon, indicating the field to be searched.
 * This enables one to construct queries which search multiple fields.
 * </ul>
 *
 * A clause may be either a:
 * <ul>
 * <li> a term, indicating all the documents that contain this term; or
 * <li> a nested query, enclosed in parentheses.  Note that this may be used
 * with a <code>+</code>/<code>-</code> prefix to require any of a set of
 * terms.
 * </ul>
 *
 * Thus, in BNF, the query grammar is:
 * <pre>
 *   Query  ::= ( Clause )*
 *   Clause ::= ["+", "-"] [<TERM> ":"] ( <TERM> | "(" Query ")" )
 * </pre>
 */

public class QueryParser implements QueryParserConstants {
  /** Parses a query string, returning a
   * <a href="lucene.search.Query.html">Query</a>.
   *  @param query	the query string to be parsed.
   *  @param field	the default field for query terms.
   *  @param analyzer   used to find terms in the query text.
   */
  static public Query parse(String query, String field, Analyzer analyzer)
       throws ParseException {
    QueryParser parser = new QueryParser(field, analyzer);
    return parser.parse(query);
  }

  Analyzer analyzer;
  String field;
  int phraseSlop = 0;

  /** Constructs a query parser.
   *  @param field	the default field for query terms.
   *  @param analyzer   used to find terms in the query text.
   */
  public QueryParser(String f, Analyzer a) {
    this(new StringReader(""));
    analyzer = a;
    field = f;
  }

  /** Parses a query string, returning a
   * <a href="lucene.search.Query.html">Query</a>.
   *  @param query	the query string to be parsed.
   */
  public Query parse(String query) throws ParseException {
    ReInit(new StringReader(query));
    return Query(field);
  }

  /** Sets the default slop for phrases.  If zero, then exact phrase matches
    are required.  Zero by default. */
  public void setPhraseSlop(int s) { phraseSlop = s; }
  /** Gets the default slop for phrases. */
  public int getPhraseSlop() { return phraseSlop; }

  private void addClause(Vector clauses, int conj, int mods,
                        Query q) {
    boolean required, prohibited;

    // If this term is introduced by AND, make the preceding term required,
    // unless it's already prohibited
    if (conj == CONJ_AND) {
      BooleanClause c = (BooleanClause) clauses.elementAt(clauses.size()-1);
      if (!c.prohibited)
        c.required = true;
    }

    // We might have been passed a null query; the term might have been
    // filtered away by the analyzer. 
    if (q == null)
      return;

    // We set REQUIRED if we're introduced by AND or +; PROHIBITED if
    // introduced by NOT or -; make sure not to set both.
    prohibited = (mods == MOD_NOT);
    required = (mods == MOD_REQ);
    if (conj == CONJ_AND && !prohibited)
      required = true;
    clauses.addElement(new BooleanClause(q, required, prohibited));
  }

  private Query getFieldQuery(String field, Analyzer analyzer, String queryText) {
    // Use the analyzer to get all the tokens, and then build a TermQuery,
    // PhraseQuery, or nothing based on the term count

    TokenStream source = analyzer.tokenStream(field, new StringReader(queryText));
    Vector v = new Vector();
    org.apache.lucene.analysis.Token t;

    while (true) {
      try {
        t = source.next();
      }
      catch (IOException e) {
        t = null;
      }
      if (t == null)
        break;
      v.addElement(t.termText());
    }
    if (v.size() == 0)
      return null;
    else if (v.size() == 1)
      return new TermQuery(new Term(field, (String) v.elementAt(0)));
    else {
      PhraseQuery q = new PhraseQuery();
      q.setSlop(phraseSlop);
      for (int i=0; i<v.size(); i++) {
        q.add(new Term(field, (String) v.elementAt(i)));
      }
      return q;
    }
  }

  public static void main(String[] args) throws Exception {
    QueryParser qp = new QueryParser("field",
                                     new org.apache.lucene.analysis.SimpleAnalyzer());
    Query q = qp.parse(args[0]);
    System.out.println(q.toString("field"));
  }

  private static final int CONJ_NONE   = 0;
  private static final int CONJ_AND    = 1;
  private static final int CONJ_OR     = 2;

  private static final int MOD_NONE    = 0;
  private static final int MOD_NOT     = 10;
  private static final int MOD_REQ     = 11;

// *   Query  ::= ( Clause )*
// *   Clause ::= ["+", "-"] [<TERM> ":"] ( <TERM> | "(" Query ")" )
  final public int Conjunction() throws ParseException {
  int ret = CONJ_NONE;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AND:
    case OR:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case AND:
        jj_consume_token(AND);
            ret = CONJ_AND;
        break;
      case OR:
        jj_consume_token(OR);
              ret = CONJ_OR;
        break;
      default:
        jj_la1[0] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
    default:
      jj_la1[1] = jj_gen;
      ;
    }
    {if (true) return ret;}
    throw new Error("Missing return statement in function");
  }

  final public int Modifiers() throws ParseException {
  int ret = MOD_NONE;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NOT:
    case PLUS:
    case MINUS:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PLUS:
        jj_consume_token(PLUS);
              ret = MOD_REQ;
        break;
      case MINUS:
        jj_consume_token(MINUS);
                 ret = MOD_NOT;
        break;
      case NOT:
        jj_consume_token(NOT);
               ret = MOD_NOT;
        break;
      default:
        jj_la1[2] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
    default:
      jj_la1[3] = jj_gen;
      ;
    }
    {if (true) return ret;}
    throw new Error("Missing return statement in function");
  }

  final public Query Query(String field) throws ParseException {
  Vector clauses = new Vector();
  Query q;
  int conj, mods;
    mods = Modifiers();
    q = Clause(field);
    addClause(clauses, CONJ_NONE, mods, q);
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case AND:
      case OR:
      case NOT:
      case PLUS:
      case MINUS:
      case LPAREN:
      case QUOTED:
      case NUMBER:
      case TERM:
      case WILDTERM:
        ;
        break;
      default:
        jj_la1[4] = jj_gen;
        break label_1;
      }
      conj = Conjunction();
      mods = Modifiers();
      q = Clause(field);
      addClause(clauses, conj, mods, q);
    }
      BooleanQuery query = new BooleanQuery();
      for (int i = 0; i < clauses.size(); i++)
        query.add((BooleanClause)clauses.elementAt(i));
      {if (true) return query;}
    throw new Error("Missing return statement in function");
  }

  final public Query Clause(String field) throws ParseException {
  Query q;
  Token fieldToken=null;
    if (jj_2_1(2)) {
      fieldToken = jj_consume_token(TERM);
      jj_consume_token(COLON);
                                field = fieldToken.image;
    } else {
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case QUOTED:
    case NUMBER:
    case TERM:
    case WILDTERM:
      q = Term(field);
      break;
    case LPAREN:
      jj_consume_token(LPAREN);
      q = Query(field);
      jj_consume_token(RPAREN);
      break;
    default:
      jj_la1[5] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
      {if (true) return q;}
    throw new Error("Missing return statement in function");
  }

  final public Query Term(String field) throws ParseException {
  Token term, boost=null;
  boolean prefix = false;
  boolean wildcard = false;
  boolean fuzzy = false;
  Query q;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NUMBER:
    case TERM:
    case WILDTERM:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TERM:
        term = jj_consume_token(TERM);
        break;
      case WILDTERM:
        term = jj_consume_token(WILDTERM);
                                  wildcard=true;
        break;
      case NUMBER:
        term = jj_consume_token(NUMBER);
        break;
      default:
        jj_la1[6] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case STAR:
      case FUZZY:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case STAR:
          jj_consume_token(STAR);
                                                                        prefix=true;
          break;
        case FUZZY:
          jj_consume_token(FUZZY);
                                                                                              fuzzy=true;
          break;
        default:
          jj_la1[7] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
      default:
        jj_la1[8] = jj_gen;
        ;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CARAT:
        jj_consume_token(CARAT);
        boost = jj_consume_token(NUMBER);
        break;
      default:
        jj_la1[9] = jj_gen;
        ;
      }
        if (wildcard)
          q = new WildcardQuery(new Term(field, term.image));
        else if (prefix)
          q = new PrefixQuery(new Term(field, term.image));
        else if (fuzzy)
          q = new FuzzyQuery(new Term(field, term.image));
        else
          q = getFieldQuery(field, analyzer, term.image);
      break;
    case QUOTED:
      term = jj_consume_token(QUOTED);
        q = getFieldQuery(field, analyzer,
                          term.image.substring(1, term.image.length()-1));
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    if (boost != null) {
      float f = (float) 1.0;
      try {
        f = Float.valueOf(boost.image).floatValue();
      }
      catch (Exception ignored) { }

      if (q instanceof TermQuery)
        ((TermQuery) q).setBoost(f);
      else if (q instanceof PhraseQuery)
        ((PhraseQuery) q).setBoost(f);
      else if (q instanceof MultiTermQuery)
        ((MultiTermQuery) q).setBoost(f);
    }
    {if (true) return q;}
    throw new Error("Missing return statement in function");
  }

  final private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  final private boolean jj_3_1() {
    if (jj_scan_token(TERM)) return true;
    if (jj_scan_token(COLON)) return true;
    return false;
  }

  public QueryParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[11];
  static private int[] jj_la1_0;
  static {
      jj_la1_0();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0xc00,0xc00,0x7000,0x7000,0x170fc00,0x1708000,0x1600000,0x880000,0x880000,0x40000,0x1700000,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[1];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  public QueryParser(java.io.InputStream stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new QueryParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.InputStream stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public QueryParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new QueryParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public QueryParser(QueryParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(QueryParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  final private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Enumeration e = jj_expentries.elements(); e.hasMoreElements();) {
        int[] oldentry = (int[])(e.nextElement());
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.addElement(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[26];
    for (int i = 0; i < 26; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 11; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 26; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

  final private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 1; i++) {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
          }
        }
        p = p.next;
      } while (p != null);
    }
    jj_rescan = false;
  }

  final private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
