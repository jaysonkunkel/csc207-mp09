import java.io.PrintWriter;

/**
 * JSON strings.
 * 
 * @author Jayson Kunkel
 * @author Sam Bigham
 */
public class JSONString implements JSONValue {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The underlying string.
   */
  String value;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Build a new JSON string for a particular string.
   */
  public JSONString(String value) {
    this.value = value;
  } // JSONString(String)

  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * Convert to a string (e.g., for printing).
   */
  public String toString() {
    return this.value.toString();
  } // toString()

  /**
   * Compare to another object.
   */
  public boolean equals(Object other) {
    return (((other instanceof JSONString) && (this.value == ((JSONString) other).value))
        || (this.value == other));
  } // equals(Object)

  /**
   * Compute the hash code.
   */
  public int hashCode() {
    if (this.value == null)
      return 0;
    else
      return this.value.hashCode();
  } // hashCode()

  // +--------------------+------------------------------------------
  // | Additional methods |
  // +--------------------+

  /**
   * Write the value as JSON.
   */
  public void writeJSON(PrintWriter pen) {
    // unlike toString, we print the enclosing "" and escape characters
    String str = "\"";
    int i = 0;
    while (i < this.value.length()) {
      switch (this.value.charAt(i)) {
        case '\"': str = str + "\\\"";
        break;
        case '\b' : str = str + "\\b";
        break;
        case '\n' : str = str + "\\n";
        break;
        case '\r' : str = str + "\\r";
        break;
        case '\f' : str = str + "\\f";
        break;
        case '\'' : str = str + "\\'";
        break;
        case '\t' : str = str + "\\t";
        break;
        case '\\' : str = str + "\\\\";
        break;
        default:
        str = str + this.value.charAt(i);
      } // switch
      i++;
    } // while
    pen.print(str + "\"");
    // TODO: add slashes
  } // writeJSON(PrintWriter)

  /**
   * Get the underlying value.
   */
  public String getValue() {
    return this.value;
  } // getValue()

} // class JSONString
