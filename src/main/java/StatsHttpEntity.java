import java.io.*;
import java.nio.charset.Charset;
import prg.apache.http.entity.AbstractHttpEntity;

public class StatsHttpEntity extends  AbstractHttpEntity {
  private static Charset utf8 = Charset.forname("UTF-8");
  private final StatsWriter writer;
  private final StatsEntity[] stats;
 
  public StatsHttpEntity(StatsWriter writer, StatsEntity[] stats) {
    this.writer = writer;
    this.stats = stats;
    this.setContenttype("application/json");
  }
  @Override
  public boolean isRepetable() {
return false;
  }
  @Override
  public long getContentLength() {
return -1;
  }
  @Override
  public InputStream getContent() throws IOException, UnsupportedOperationException {
    throw new  UnsupportedOperationException();
  }
  @Override
  public void writeTo(OutputStream stream) throws IOException {
    Write out = new OutputStreamWriter(stream, utf8);
    writer.write(out, stats);
    out.flush();
  }
  @Override
  public boolean isStreaming() {return true;}
}
