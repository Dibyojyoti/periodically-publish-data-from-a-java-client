import java.util.List;

public class StatEntity {
  private long timestamp;
  private long price;
  private List<String> items;

  public long getTimeStamp() { return timestamp;}  
  public void setTimeStamp(long timestamp) {this.timestamp = timestamp;}
  public long getPrice() {return price;}
  public void setPrice(long price) {this.price = price;}
  public List<String> getItems() { return items;}
  public void setItems( List<String> items) {this.items = items;}
}
