// package cn.asany.example.autoconfigure;
//
// import java.util.Collection;
// import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
// import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
//
// public class MyDatabaseShardingAlgorithm implements PreciseShardingAlgorithm<String> {
//  @Override
//  public String doSharding(
//      Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
//    // 根据租户ID来进行分库逻辑，返回目标数据库名称
//    // availableTargetNames 是所有可用的数据库实例的名称列表
//    // shardingValue 是分片键的信息，包括逻辑表名、分片键名、分片键值等信息
//    String tenantId = shardingValue.getValue();
//    String dbName = "ds_" + tenantId; // 假设将偶数租户数据分到 ds_0，奇数租户数据分到 ds_1
//    if (availableTargetNames.contains(dbName)) {
//      return dbName;
//    }
//    throw new IllegalArgumentException("Invalid database name: " + dbName);
//  }
// }
