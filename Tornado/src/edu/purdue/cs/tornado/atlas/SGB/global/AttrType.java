package edu.purdue.cs.tornado.atlas.SGB.global;


/**
 * Enumeration class for attribute data types.
 */
public class AttrType {

  public static final int INTEGER = 11;
  public static final int FLOAT = 12;
  public static final int STRING = 13;

  public static final int COLNAME = 21;
  public static final int FIELDNO = 22;

  public static final int L2=1;
  public static final int Linfinity=2;
  
 public static  final int  ODUPLICATIED=1;
  public static final int OANY=2;
  public static final int OELIMINATED=3;
  public static final int OFORMNEW=4;
  
  // --------------------------------------------------------------------------

  /**
   * Private constructor (static class).
   */
  private AttrType() {}

  /**
   * Gets the AttrType of the given value; i.e. not applicable for column names
   * or field numbers.
   * 
   * @throws IllegalArgumentException if obj is not an AttrType
   */
  public static int getType(Object obj) {

    if (obj instanceof Integer) {
      return INTEGER;
    }

    if (obj instanceof Float) {
      return FLOAT;
    }

    if (obj instanceof String) {
      return STRING;
    }

    // otherwise, unknown type
    throw new IllegalArgumentException("Unknown AttrType "
        + obj.getClass().getName());

  } // public static int getType(Object obj)

  /**
   * Returns a string representation of an AttrType.
   */
  public static String toString(int value) {

    switch (value) {

      case INTEGER:
        return "INTEGER";

      case FLOAT:
        return "FLOAT";

      case STRING:
        return "STRING";

      case COLNAME:
        return "COLNAME";

      case FIELDNO:
        return "FIELDNO";

    } // switch

    return ("Unexpected AttrType " + value);

  } // public static String toString(int value)

} // public class AttrType
