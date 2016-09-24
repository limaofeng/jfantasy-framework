package org.jfantasy.framework.hibernate.cache;

import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.spi.*;
import org.hibernate.cache.spi.access.AccessType;
import org.jfantasy.framework.hibernate.cache.regions.*;
import org.jfantasy.framework.hibernate.cache.strategy.NonstopAccessStrategyFactory;
import org.jfantasy.framework.hibernate.cache.strategy.SpringCacheAccessStrategyFactory;
import org.jfantasy.framework.hibernate.cache.strategy.SpringCacheAccessStrategyFactoryImpl;
import org.jfantasy.framework.hibernate.cache.util.Timestamper;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Properties;

abstract class AbstractSpringCacheRegionFactory implements RegionFactory {

    private static final long serialVersionUID = -4703807378849600952L;

    protected transient volatile CacheManager manager;

    protected transient SessionFactoryOptions settings;

    private final transient SpringCacheAccessStrategyFactory accessStrategyFactory = new NonstopAccessStrategyFactory(new SpringCacheAccessStrategyFactoryImpl());

    @Override
    public boolean isMinimalPutsEnabledByDefault() {
        return true;
    }

    @Override
    public long nextTimestamp() {
        return Timestamper.next();
    }

    @Override
    public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) {
        return new SpringCacheEntityRegion(accessStrategyFactory, getCache(regionName), settings, metadata, properties);
    }

    @Override
    public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata) {
        return new SpringCacheNaturalIdRegion(accessStrategyFactory, getCache(regionName), settings, metadata, properties);
    }

    @Override
    public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata) {
        return new SpringCacheCollectionRegion(accessStrategyFactory, getCache(regionName), settings, metadata, properties);
    }

    @Override
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) {
        return new SpringCacheQueryResultsRegion(accessStrategyFactory, getCache(regionName), properties);
    }

    @Override
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) {
        return new SpringCacheTimestampsRegion(accessStrategyFactory, getCache(regionName), properties);
    }

    private Cache getCache(String name) {
        return manager.getCache(name);
    }

    @Override
    public AccessType getDefaultAccessType() {
        return AccessType.READ_WRITE;
    }

}

