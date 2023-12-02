import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

/*
 * @author Jayson Kunkel
 * 
 * @author Sam Bigham
 */

/**
 * Utilities for our simple implementation of JSON.
 */
public class JSON {
  // +---------------+-----------------------------------------------
  // | Static fields |
  // +---------------+

  /**
   * The current position in the input.
   */
  static int pos;

  // +----------------+----------------------------------------------
  // | Static methods |
  // +----------------+

  /**
   * Parse a string into JSON.
   */
  public static JSONValue parse(String source) throws ParseException, IOException {
    return parse(new StringReader(source));
  } // parse(String)

  /**
   * Parse a file into JSON.
   */
  public static JSONValue parseFile(String filename) throws ParseException, IOException {
    FileReader reader = new FileReader(filename);
    JSONValue result = parse(reader);
    reader.close();
    return result;
  } // parseFile(String)

  /**
   * Parse JSON from a reader.
   */
  public static JSONValue parse(Reader source) throws ParseException, IOException {
    pos = 0;
    JSONValue result = parseKernel(source);
    if (-1 != skipWhitespace(source)) {
      throw new ParseException("Characters remain at end", pos);
    } // if
    return result;
  } // parse(Reader)

  // +---------------+-----------------------------------------------
  // | Local helpers |
  // +---------------+

  /**
   * Parse JSON from a reader, keeping track of the current position
   */
  static JSONValue parseKernel(Reader source) throws ParseException, IOException {
    // the hash table to store key/value pairs
    JSONHash hashMap = new JSONHash();
    // the current character in the source
    int ch;
    ch = skipWhitespace(source);
    if (-1 == ch) {
      throw new ParseException("Unexpected end of file", pos);
    } // if

    // parsing first '{' creates hash to hold pairs
    if ((char) ch == '{'){
      hashMap = (JSONHash) switchStat(source, ch);
      ch = source.read();
    } // if

    // while there are characters left to read, read the next one
    while (ch != -1) {

      // parse each character and increase position
      switchStat(source, ch);
      ch = source.read();
      ++pos;
    } // while
    //System.out.println(hashMap.toString());
    return hashMap;
  } // parseKernel

  /**
   * Based on the input character c, take appropriate parsing action.
   */
  static JSONValue switchStat(Reader source, int c) throws IOException{
    switch (c) {
      // indicates a hash
      case '{':
        return hashMapParser(source, c, '}');
      // indicates a string
      case '"':
        return stringParser(source, c, '"');
      // indicates an array
      case '[':
        return arrayParser(source, c, ']');
      default:
      // indicates a constant
      if (Character.isAlphabetic(c)){
         return constantParser(source, c,  ',');
      } // if
      // indicates a number
      else if (Character.isDigit(c) || c == '-' || c == '+') {
         return digitParser(source, c, ',');
      } // else if
      // all other input
      else return new JSONString(String.valueOf((char) c));
      // else if (!Character.isWhitespace(c) && !(c == ':') && !(c == ',') && !(c == '}')){
      //   throw new IOException("Error: unsupported character " + (char) c);
      // } // else
     } // switch
  } // switchStat(Reader, int)

  /*
  * parses string until it reaches the target char and then returns string
  */
 static JSONString stringParser(Reader source, int c,  char target) throws IOException{
    String str = String.valueOf((char)c);
    int ch; 

    // read characters into a string until the target character
    do {
      ch = source.read();
      String strCH = String.valueOf((char)ch);
      str = str.concat(strCH);
      pos++;
      //System.out.print("char = " + (char) ch);
    } while ((char)ch != target && !isWhitespace(ch) && (char)ch != ']');
    
    // remove enclosing quotes
    if (str.charAt(0) == '"' && str.charAt(str.length() - 1) == '"') {
      str = str.substring(1, str.length() - 1);
    } // if

    // remove trailing comma
    if (str.charAt(str.length() - 1) == ','){
      str = str.substring(0, str.length() - 1);
    } // if

    return new JSONString(str.trim());
  } // stringParser(Reader, char)
  
  /**
   * Parses a string of digits until it reaches target.
   * Returns either a JSONInteger or a JSONReal.
   */
  static JSONValue digitParser (Reader source, int c, char target) throws IOException{
    String str = stringParser(source, c, target).getValue();
    boolean isInteger = true;

    // check if it's a real or integer
    for (int i = 0; i < str.length(); i++) {
      if (!Character.isDigit(str.charAt(i))) {
          isInteger = false;
      } // if
    } // for

    // removes any extra character at the end
    if (!Character.isDigit(str.charAt(str.length()-1))) {
      str = str.substring(0, str.length()-1);
    } // if

    if (isInteger) {
      return new JSONInteger(str);
    } // if
    return new JSONReal(str);
  } // digitParser(Reader, char)

  /*
  * parses constant and returns value
  */
  static JSONConstant constantParser(Reader source, int c, char target) throws IOException {
    String str = stringParser(source, c, target).getValue();
    
    // remove trailing square bracket, if necessary
    if (str.charAt(str.length() - 1) == ']'){
      str = str.substring(0, str.length() -1);
    } // if

    // determine which constant it is, or throw exception if invalid
    if (str.equals("true")) {
      return new JSONConstant(true);
    } else if (str.equals("false")) {
      return new JSONConstant(false);
    } else if (str.equals("null")) {
    return new JSONConstant(null);
    }
    else {
      throw new IOException("Error: invalid constant");
    } // if/else
  } // constantParser(Reader)

  /*
  *parses through an array recursively
  */
  static JSONArray arrayParser(Reader source, int c, char target) throws IOException{
    JSONArray arr = new JSONArray();

    // read characters and add corresponding objects to array
    while (c != target && c != ' ' && c != ','){
      c = source.read();
      //System.out.println("c is : " + (char) c);
      if (c != ' ' && c != ',') {
        arr.add(switchStat(source, c));
      } // if
    } // while
     return arr; 
  } // arrayParser(Reader, int, char)

  static JSONHash hashMapParser (Reader source, int c, char target) throws IOException{
    // a hash to store the key/value pairs
    JSONHash JHash = new JSONHash();
    // an array that stores every object read in
    JSONArray arr = new JSONArray();

    // read/parse characters and add corresponding objects to array
    while( c != target && c != '}'){
      c = source.read();

      // some characters don't indicate objects
      if(c != ' ' && c != ',' && c!= ':' && c != '}'){
        JSONValue obj = switchStat(source, c);
        arr.add(obj);
      } // if
    } // while

    //System.out.println("\n" + arr.toString());

    // add key/value pairs to the hash map
    for (int i = 0; i < arr.size(); i += 2) {
      // keys are always JSONStrings
      JSONString key = new JSONString(arr.get(i).toString());
      // values are always JSONValues
      JSONValue val = (JSONValue)(arr.get(i + 1));
      JHash.set(key, val);
    } // for

    return JHash;
  } // hashMapParser(Reader, int, char)

  /**
   * Get the next character from source, skipping over whitespace.
   */
  static int skipWhitespace(Reader source) throws IOException {
    int ch;
    do {
      ch = source.read();
      ++pos;
    } while (isWhitespace(ch));
    return ch;
  } // skipWhitespace(Reader)

  // static int skipWhitespaceAndOthers(Reader source) throws IOException {
  //   int ch;
  //   do {
  //     ch = source.read();
  //     ++pos;
  //   } while (isWhitespace(ch) || ch == ':' || ch ==',');
  //   return ch;
  // } // skipWhitespace(Reader)

  /**
   * Determine if a character is JSON whitespace (newline, carriage return, space, or tab).
   */
  static boolean isWhitespace(int ch) {
    return (' ' == ch) || ('\n' == ch) || ('\r' == ch) || ('\t' == ch);
  } // isWhiteSpace(int)

} // class JSON
