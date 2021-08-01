public interface StatsPublisherMbean {
  String objectName = "com.my.projct.publisher:type=StatsPublisher";
  int getbatchSize();
  long getFailureCount();
  Boolean getIsEnabled();
  void publish();
  void reset();
  void enable();
  void disable();
}
