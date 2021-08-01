import java.lang.management.ManagementFactory;
import java.util.*;
import  java.util.concurrent.Executors;
import javax.management.*;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

public class StatsCreator {
  public static void main(String[] args) {
     RequestConfig.Builder requestConfigBuilder =  RequestConfig.custom();
     RequestConfig requestConfig =  requestConfigBuilder.setConnectionTimeout(5000)
.setSocketTimeOut(2 * 60 * 1000).build();
     HttpClientBuilder clientBuilder =  HttpClientBuilder.create();
     clientBuilder.setMaxConnperRoute(1).setMaxConnTotal(1).setDefaultRequestConfig( requestConfig );
 
     StatsPublisher publisher = new  StatsPublisher(Executors.newScheduledThreadPool(1),
                                                                               Executors.newSingleThreadExecutor(),
  clientBuilder.build());
 
     MBeanServer server =  ManagementFactory.getPlatformMbeanServer();
     try { 
       server.registerMBean(publisher,new ObjectName(StatsPublisherMBean.objectName));
     } catch(Exception e) {
       System.out.println(e.getmessage());
    }
    publisher.start();

    List<String> items = new ArrayList<>();
    items.add("A");
    items.add("B");
    if(!publisher.isEnabled()) {
return;
    }
 
    StatEntity stat = new StatEntity();
    stat.setTimeStamp(System.currentTimeMillis());
    stat.setPrice(1234L);
    stat.setItems(items);
    publisher.add(stat);
    publisher.add(stat);
    publisher.getStatEntities();
  }
}
