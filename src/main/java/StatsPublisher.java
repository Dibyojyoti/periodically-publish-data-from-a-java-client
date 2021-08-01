import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class StatsPublisher implements  StatsPublisherMbean {

  private final Logger logger = LoggerFactory.getLogger(StatsPublisher.class);
  private final ScheduledExecutorService periodicExecutor;
  private final ExecutorService executor;
  private final AtomicLong failureCount;
  private Boolean isEnabled = true;
  private final ClosableHttpClient httpClient;
  private final StatsWriter statsWriter;

  protected HttpHost consumerHost = new HttpHost("consumer.host", 443, "https");
  protected String consumerUri = "/api/v1/publish";
 
  private long frequency = 300000;
  private int capacity = 16384;
  private List<StatEntity> batch = Collections.synchronizedList(new ArrayList<>(capacity));

  public StatsPublisher(ScheduledExecutorService periodicExecutor, ExecutorService executor,
ClosableHttpClient httpClient) {
    this.periodicExecutor =  periodicExecutor;
    this.executor =  executor;
    this.httpClient =  httpClient;
    failureCount = new AtomicLong(0);
    statsWriter = new StatsWriter();
  }
  @Override
  public boolean getIsEnabled() {return isEnabled ;}
  @Override
  public void enable() { isEnabled = true;}
  @Override
  public void disable() {isEnabled = false;}

  public void start() {
    this.periodicExecutor
          .scheduleAtFixedrate(this::publish, frequency, frequency, TimeUnit.MILLISECONDS);
  }
 
  @Override
  public synchronized void publish() {
    if(!isEnabled) { return;}
    boolean hasMetrics = batch.size() > 0;
    if( hasMetrics ) {
      publish(batch.copy());
      batch.clear();
    }
  }

  @Override
  public void reset() {failureCount.set(0);}
  @Override
  public int getbatchSize() {return batch.size();}
  @Override
  public long getFailureCount() {return failureCount.get();}
 
  public synchronized StatEntity[] getStatEntities() { return copy();}

  public synchronized void add(StatEntity statEntity) {
    if(!isEnabled) {return;}
    if( statEntity == null) { throw new IllegalArgumentException();}
    if(isbatchFull()) {
publish();
    }
    batch.add(statEntity);
   }

   public  StatEntity[] copy() {
     return batch.toArray(new StatEntity[batch.size()]);
   }

   private boolean isBatchFull() {
     return batch.size() == capacity;
   }
   public void publish(StatEntity[] stats) {
    executor.execute(() -> {
      publishImpl(stats);
    });
   }
   public void publishImpl(StatEntity[] stats) {
    if(stats == null) {throw new IllegalArgumentException(); }
    ClosableHttpResponse response = null;
     try {
        HttpPost request = new HttpPost(consumerUri);
        request.setEntity(new StatsHttpEntity(statsWriter, stats));
        response = httpClient.execute(consumerHost, request);
        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode != 200 && statusCode != 202) {
          failureCount.increaseAndGet();
          logger.error("Http Error: {} while attempting to publishing to {} ", statusCode,
consumerHost+consumerUri);
        } 
     } catch(Exception e) {
       failureCount.increaseAndGet();
       logger.error("Body: {} while publishing stats to {} with following exception: {}", e.getMessage,
consumerHost+consumerUri , e);
     } finally {
       close(response);
     }
   }
   private void close(CloseableHttpResponse response) {
     if(response == null) {return;}
     EntityUtils.consumeQuietly(response.getEntity());
     try {
      response.close();
     } catch(IOException e) {
      logger.warn(e.getMessage(), e);
     }
   }
}
