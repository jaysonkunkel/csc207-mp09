import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ArrayList;

/**
 * JSON hashes/objects.
 * @author Jayson Kunkel
 * @author Sam Bigham
 */
public class JSONHash {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  //KVPair<JSONString, JSONValue>[] pairs;
  /**
   * The underlying array.
   */
  ArrayList<KVPair<JSONString, JSONValue>> pairs;

  /**
   * The number of pairs in the underlying array.
   */
  int size = 0;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new hash given the underlying array.
   */
  public JSONHash(ArrayList<KVPair<JSONString, JSONValue>>pairs) {
    this.pairs = pairs;
    this.size = pairs.size();
  } // JSONInteger(String)

  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * Convert to a string (e.g., for printing).
   */
  public String toString() {
    return this.pairs.toString();
  } // toString()

  /**
   * Compare to another object.
   */
  public boolean equals(Object other) {
   // KVPair[] arr = (other instanceof KVPair[]) // want to convert other to KVPair[] but can't figure it out
    if (this == null || other == null) { //should it return false if both are null?
      return false;
    }
    // for(int i = 0; i < this.size -1; i++){
    //   if(this.pairs[i].equals(other[i]))
    //   }//for
          
  } // equals(Object)

  /**
   * Compute the hash code.
   */
  public int hashCode() {
    return 0;           // STUB
  } // hashCode()

  // +--------------------+------------------------------------------
  // | Additional methods |
  // +--------------------+

  /**
   * Write the value as JSON.
   */
  public void writeJSON(PrintWriter pen) {
    //simple implementation for writeJSON... may not work correctly
    for (int i = 0; i < this.size - 1; i++) {
      pen.println("key is: " + pairs[i].key()+  " value is : " +pairs[i].value());
    }//for
  } // writeJSON(PrintWriter)

  /**
   * Get the underlying value.
   */
  public Iterator<KVPair<JSONString,JSONValue>> getValue() {
    return this.iterator();
  } // getValue()

  // +-------------------+-------------------------------------------
  // | Hashtable methods |
  // +-------------------+

  /**
   * Get the value associated with a key.
   */
  public JSONValue get (JSONString key) throws Exception{
    // int n = key.getValue().hashCode();
    // return pairs[n].value();
    for (KVPair<JSONString, JSONValue> pair : this.pairs) {
      if (pair.key().equals(key)) {
        return pair.value();
      } // if
    } // for
    throw new Exception("Error: key not found");
  } // get(JSONString)

  /**
   * Get all of the key/value pairs.
   */
  public Iterator<KVPair<JSONString,JSONValue>> iterator() {

    KVPair<JSONString, JSONValue>[] pairs = this.pairs;
    int size = this.size;
    
    return new Iterator<KVPair<JSONString,JSONValue>>() {

      int i = 0;
      
      public boolean hasNext() {
        return i < size;
      } // hasNext()

      public KVPair<JSONString,JSONValue> next() throws NoSuchElementException{
        if (!this.hasNext()) {
          throw new NoSuchElementException("No elements remaining");
        }
        return pairs[i++];
      } // next()
    }; // new Iterator
  } // iterator()

  /**
   * Set the value associated with a key.
   */
  public void set(JSONString key, JSONValue value) {
    for (int i = 0; i < this.size; i++) {
      if (this.pairs.get(i).key().equals(key)) {
        this.pairs.set(i, new KVPair<JSONString,JSONValue>(key, value));
        return;
      } // if
    } // for
    this.pairs.set(this.size++, new KVPair<JSONString,JSONValue>(key, value));
  } // set(JSONString, JSONValue)

  /**
   * Find out how many key/value pairs are in the hash table.
   */
  public int size() {
    return this.size;
  } // size()

} // class JSONHash
