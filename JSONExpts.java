import java.io.PrintWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ArrayList;

public class JSONExpts {

    public static void main(String[] args) throws Exception {
        PrintWriter pen = new PrintWriter(System.out, true);

        JSONString js = new JSONString("hello");
        JSONString js2 = new JSONString("hello");

        // pen.print(js.toString());
        //js.writeJSON(pen);
        //pen.println(js.hashCode());
        //pen.println(js2.hashCode());

        // JSONReal jr = new JSONReal(5.0);
        // JSONReal jr2 = new JSONReal(5);
        // note: how to handle this? use == or Object equals() method?
        // probably the latter

        // pen.println(jr.hashCode());
        // pen.println(jr2.hashCode());

        // pen.println(jr.toString());
        // pen.println(jr2.toString());

        // pen.println(jr.equals(jr2));

        JSONArray ja = new JSONArray();
        ja.add(new JSONString("hello"));
        ja.add(new JSONString("goodbye"));
        ja.add(new JSONString("0"));
        ja.add(new JSONString("hhhhhh"));

         pen.println(ja.get(0));
         pen.println(ja.toString());

        ArrayList<KVPair<JSONString, JSONValue>> pairs = new ArrayList<KVPair<JSONString, JSONValue>>();
        pairs.add(0, new KVPair<JSONString,JSONValue>(new JSONString("A"), new JSONString("apple")));
        pairs.add(1, new KVPair<JSONString,JSONValue>(new JSONString("B"), new JSONString("Banana")));

        //pairs.set(0, new KVPair<JSONString,JSONValue>(new JSONString("C"), new JSONString("car")));

        pen.println(Math.abs(ja.hashCode()) % ja.values.size());
        pen.println(ja.hashCode());

        // to consider: writeJSON for others than constant

        JSONHash jh = new JSONHash();
        jh.set(new JSONString("a"), new JSONString("animal"));
        pen.println(jh.get(new JSONString("a")));
        pen.println(jh.find(new JSONString("a")));

        jh.set(new JSONString("b"), new JSONString("banana"));
        pen.println(jh.get(new JSONString("b")));
        pen.println(jh.find(new JSONString("b")));

        jh.set(new JSONString("z"), new JSONString("zebra"));

        pen.println(jh.toString());
        jh.writeJSON(pen);

        Iterator<KVPair<JSONString, JSONValue>> it = jh.iterator();
        while(it.hasNext()){
            pen.println(it.next());
        }


        // JSON.parseFile("test.txt");
        // JSON.parse("{ a : animal, b : banana }");

    }
}
