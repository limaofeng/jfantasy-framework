package net.asany.jfantasy.graphql.context;

import org.dataloader.DataLoaderRegistry;

/**
 * DataLoaderRegistry 自定义
 *
 * @author limaofeng
 */
public interface DataLoaderRegistryCustomizer {

  /**
   * 自定义
   *
   * @param registry DataLoaderRegistry
   */
  void customize(DataLoaderRegistry registry);
}
