import java.io.*;

public class StatsWriter {
 public void write(Writer out, StatEntity[] stats) throws IOException {
    out.write('{');
    out.write("\"applicationtype\": \" service\",");
    out.write("\"applicationname\": \" myapp\",");
    out.write("\"stats\":");
    out.write('[');

    for(int i = 0; i < stats.length; i++) {
      StatEntity stat =  stats[i];
      out.write('{');
      addElement(out, "time", Long.toString(stat.getTimeStamp()));
      out.write(',');
      addElement(out, "price", Long.toString(stat.getPrice()));
      out.write(',');
      addElement(out, "items", String.join(",",stat.getItems()));
      out.write('}');

      if(i + 1 < stats.length) {
        out.write(',');
      }
    }

    out.write(']'); 
    out.write('}');
    out.flush();  
 }

 private void addElement(Writer out, String name, String value) throws IoException {
    out.write("\"" + name + "\":");
    out.write(' " ');
    out.write(value);
    out.write(' " ');
 } 
}
