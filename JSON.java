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
    JSONHash hashMap = new JSONHash();
    int ch;
    ch = skipWhitespace(source);
    if (-1 == ch) {
      throw new ParseException("Unexpected end of file", pos);
    } // if

    while (ch != -1) {
      //char c = (char) ch;
      //System.out.print(c);
      //check whether to store hashMap
      System.out.print(switchStat(source, ch));
      ch = source.read();
      ++pos;
    } // while

    return hashMap;
  } // parseKernel

  /**
   * Based on the input character c, take appropriate parsing action.
   */
  static JSONValue switchStat(Reader source, int c) throws IOException{
         switch (c) {
      case '{':
        return hashMapParser(source, c, '}');
      case '"':
        return stringParser(source, c, '"');
      case '[':
        return arrayParser(source, c, ']');
      default:
      if (Character.isAlphabetic(c)){
         return constantParser(source, c,  ',');
      } // if
      else if (Character.isDigit(c) || c == '-' || c == '+') {
         return digitParser(source, c, ',');
      } // else if
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

    do {
      ch = source.read();
      String strCH = String.valueOf((char)ch);
      str = str.concat(strCH);
      pos++;
      //System.out.print("char = " + (char) ch);
    } while ((char)ch != target && !isWhitespace(ch) && (char)ch != ']');
    

    //str = str.substring(0, str.length() - 1);
    if (str.charAt(0) == '"' && str.charAt(str.length() - 1) == '"') {
      str = str.substring(1, str.length() - 1);
    } // if
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

    for (int i = 0; i < str.length(); i++) {
      if (!Character.isDigit(str.charAt(i))) {
          isInteger = false;
      } // if
    } // for

    //str = str.substring(0, str.length() - 1);
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
    
    if (str.charAt(str.length() - 1) == ']'){
      str = str.substring(0, str.length() -1);
    } // if

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
    int i = 0;

    // arr.add(switchStat(source, c));
    // c = source.read();
    // while(c != target && i < 10 && c != ' ' && c != ','){
    //   arr.add(switchStat(source, c));
    //    i++;
    //   c = source.read();
    // }

    while (c != target && i < 10 && c != ' ' && c != ','){
      c = source.read();
      //System.out.println("c is : " + (char) c);
      if(c != ' ' && c != ','){
      arr.add(switchStat(source, c));
      } 
      i++;
    }
     return arr; 
  } // arrayParser(Reader, int, char)

  static JSONHash hashMapParser (Reader source, int c, char target) throws IOException{
    JSONHash JHash = new JSONHash();
    JSONArray arr = new JSONArray();

    while( c != target && c != '}'){

      
      c = source.read();
      // System.out.println("tis is c : " + (char) c);

      if(c != ' ' && c != ',' && c!= ':' && c != '}'){
        System.out.println((char)c);

        JSONValue obj = switchStat(source, c);

        System.out.print(obj.getValue() + " ");
       // System.out.print(obj.getValue() + " ");
        //JHash.set(str, obj);
        arr.add(obj);
        //System.out.println(JHash.toString());
      } // if
    } // while

    //System.out.println("\n" + arr.toString());

    for (int i = 0; i < arr.size(); i += 2) {
      //System.out.print(arr.get(i) + " ");
      JSONString key = new JSONString(arr.get(i).toString());
      JSONValue val = (JSONValue)(arr.get(i + 1));
      JHash.set(key, val);
    }

    return JHash;
  }

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

  static int skipWhitespaceAndOthers(Reader source) throws IOException {
    int ch;
    do {
      ch = source.read();
      ++pos;
    } while (isWhitespace(ch) || ch == ':' || ch ==',');
    return ch;
  } // skipWhitespace(Reader)



  /**
   * Determine if a character is JSON whitespace (newline, carriage return, space, or tab).
   */
  static boolean isWhitespace(int ch) {
    return (' ' == ch) || ('\n' == ch) || ('\r' == ch) || ('\t' == ch);
  } // isWhiteSpace(int)

} // class JSON
