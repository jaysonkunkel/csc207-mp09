import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ArrayList;

/**
 * JSON hashes/objects.
 * 
 * @author Jayson Kunkel
 * @author Sam Bigham
 */
public class JSONHash implements JSONValue {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The underlying array.
   */
  Object[] buckets = new Object[50];

  /**
   * The number of pairs in the underlying array.
   */
  int size = 0;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  // /**
  // * Create a new hash given the underlying array.
  // */
  // public JSONHash(ArrayList<KVPair<JSONString, JSONValue>> pairs) {
  // this.pairs = pairs;
  // this.size = pairs.size();
  // } // JSONInteger(String)

  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * basically toString but modified. BUG TO FIX.... string's don't have quotes around them. same
   * thing for other objects I want to use toString in the respective classes, but that may not
   * work.
   */
  public String writeJSONHelper(PrintWriter pen) {

    String str = "";
    boolean firstTime = true;

    // goes through each value in the array
    for (int i = 0; i < this.buckets.length; i++) {
      @SuppressWarnings("unchecked")
      ArrayList<KVPair<JSONString, JSONValue>> alist =
          (ArrayList<KVPair<JSONString, JSONValue>>) buckets[i];
      if (alist != null) {
        // goes through each pair in the array list
        for (KVPair<JSONString, JSONValue> pair : alist) {
          if (firstTime) {
            str = str.concat(pair.key() + " : " + pair.value().toString());
            firstTime = false;
          } else {
            str = str.concat(", " + pair.key() + " : " + pair.value().toString());
          } // else
        } // for each pair in the bucket
      } // if the current bucket is not null
    } // for each bucket
    return str;
  } // toString()

  /**
   * Convert to a string (e.g., for printing). similar to writeJSONHelper except returns a string
   */
  public String toString() {
    String str = "";
    str = str.concat("Capacity: " + this.buckets.length + ", Size: " + this.size + "\n");
    // goes through each value in the array
    for (int i = 0; i < this.buckets.length; i++) {
      @SuppressWarnings("unchecked")
      ArrayList<KVPair<JSONString, JSONValue>> alist =
          (ArrayList<KVPair<JSONString, JSONValue>>) buckets[i];
      if (alist != null) {
        // goes through each pair in the array list
        for (KVPair<JSONString, JSONValue> pair : alist) {
          str = str.concat("  " + i + " <" + pair.key() + " : " + pair.value() + ">\n");
        } // for each pair in the bucket
      } // if the current bucket is not null
    } // for each bucket
    return str;
  } // toString()

  /**
   * Compare to another object.
   */
  public boolean equals(Object other) {
    boolean isTrue = true;
    if ((this.buckets != other)) {
      isTrue = false;
    } // if
    if (other instanceof JSONHash) {
      for(int i = 0; i < this.buckets.length; i++){
        if (!this.buckets[i].equals(((JSONHash)other).buckets[i])) {
          isTrue = false;
        } // if
      } // for
    } // if
    return isTrue;
  } // equals(Object)

  /**
   * Compute the hash code.
   */
  public int hashCode() {
    if (this.buckets == null) {
      return 0;
    } else {
      return this.buckets.hashCode();
    } // else
  } // hashCode()

  // +--------------------+------------------------------------------
  // | Additional methods |
  // +--------------------+

  /**
   * Write the value as JSON.
   */
  public void writeJSON(PrintWriter pen) {

    String fileName = "test.txt";
    String str = "{ ";
    str = str.concat(writeJSONHelper(pen) + " }");
    try {
      FileWriter writer = new FileWriter(fileName);
      String hashTable = str;
      writer.write(hashTable);
      writer.close();
      pen.println("wrote to file");
    } catch (IOException e) {
      System.err.println("exception at writeJson : " + e);
    } // catch
  } // writeJSON(PrintWriter)

  /**
   * Get the underlying value.
   */
  public Iterator<KVPair<JSONString, JSONValue>> getValue() {
    return this.iterator();
  } // getValue()

  // +-------------------+-------------------------------------------
  // | Hashtable methods |
  // +-------------------+

  /**
   * Get the value associated with a key. Throws an exception if the given key does not appear in
   * the hash table. If it does, returns the value associated with the given key.
   */
  public JSONValue get(JSONString key) throws Exception {
    // get location of key in hash table
    int index = find(key);

    // grab the bucket at index of key
    @SuppressWarnings("unchecked")
    ArrayList<KVPair<JSONString, JSONValue>> alist =
        (ArrayList<KVPair<JSONString, JSONValue>>) buckets[index];

    // if the key does not exist
    if (alist == null) {
      throw new IndexOutOfBoundsException("Invalid key: " + key);
    } // if
    else {
      // check if given key is in the bucket
      for (KVPair<JSONString, JSONValue> p : alist) {
        if (p.key().equals(key)) {
          return p.value();
        } // if
      } // for
    } // if/else

    return null;
  } // get(JSONString)

  /**
   * Get all of the key/value pairs.
   */
  public Iterator<KVPair<JSONString, JSONValue>> iterator() {

    return new Iterator<KVPair<JSONString, JSONValue>>() {
      // the position in the underlying array
      int i = 0;

      public boolean hasNext() {
        // if index is out of bounds
        if (i >= buckets.length) {
          return false;
        } // if
        // if bucket is not empty
        if (buckets[i] != null) {
          return true;
        } // if
        else {
          // increment index until non-empty bucket
          while (i < buckets.length && buckets[i] == null) {
            i++;
          } // while
          return i < buckets.length;
        } // if/else
      } // hasNext()

      public KVPair<JSONString, JSONValue> next() throws NoSuchElementException {
        if (!this.hasNext()) {
          throw new NoSuchElementException("No elements remaining");
        } // if

        // grab bucket at the current index
        @SuppressWarnings("unchecked")
        ArrayList<KVPair<JSONString, JSONValue>> alist =
            (ArrayList<KVPair<JSONString, JSONValue>>) buckets[i++];
        
        // grab the iterator for that bucket
        Iterator<KVPair<JSONString, JSONValue>> it = alist.iterator();

        // print out all of the pairs in the bucket
        while (it.hasNext()) {
          System.out.println((KVPair<JSONString, JSONValue>) it.next());
        } // while

        return null;
      } // next()
    }; // new Iterator
  } // iterator()

  /**
   * Set the value associated with a key.
   */
  @SuppressWarnings("unchecked")
  public void set(JSONString key, JSONValue value) {
    // get location of key in hash table
    int index = find(key);

    // grab the bucket at index of key
    ArrayList<KVPair<JSONString, JSONValue>> alist =
        (ArrayList<KVPair<JSONString, JSONValue>>) this.buckets[index];

    // Special case: Nothing there yet
    if (alist == null) {
      // create an empty bucket and add given key/value pair, then increase size
      alist = new ArrayList<KVPair<JSONString, JSONValue>>();
      this.buckets[index] = alist;
      alist.add(new KVPair<JSONString, JSONValue>(key, value));
      ++this.size;
    } // if
    else {
      // go through bucket and check if given key already exists
      for (int i = 0; i < alist.size(); i++) {
        KVPair<JSONString, JSONValue> pair = alist.get(i);
        // if key already exists, replace current value with given value
        if (pair.key().equals(key)) {
          alist.set(i, new KVPair<JSONString, JSONValue>(key, value));
        } // if
      } // for
      // if key does not already exist, add given key/value pair, then increase size
      alist.add(new KVPair<JSONString, JSONValue>(key, value));
      ++this.size;
    } // if/else
  } // set(JSONString, JSONValue)

  /**
   * Find out how many key/value pairs are in the hash table.
   */
  public int size() {
    return this.size;
  } // size()

  /**
   * Find the index of the entry with a given key.
   */
  public int find(JSONString key) {
    return Math.abs(key.hashCode()) % this.buckets.length;
  } // find(JSONString)

} // class JSONHash
