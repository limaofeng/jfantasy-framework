package com.fantasy.framework.dao.hibernate.cache.redis.strategy;

import com.fantasy.framework.dao.hibernate.cache.redis.regions.RedisCacheNaturalIdRegion;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

public class ReadWriteRedisCacheNaturalIdRegionAccessStrategy extends AbstractReadWriteRedisCacheAccessStrategy<RedisCacheNaturalIdRegion> implements NaturalIdRegionAccessStrategy {

    public ReadWriteRedisCacheNaturalIdRegionAccessStrategy(RedisCacheNaturalIdRegion region, Settings settings) {
        super(region, settings);
    }

    @Override
    public NaturalIdRegion getRegion() {
        return region();
    }

    @Override
    public boolean insert(Object key, Object value) throws CacheException {
        return false;
    }

    @Override
    public boolean afterInsert(Object key, Object value) throws CacheException {
        region().writeLock(key);
        try {
            final Lockable item = (Lockable) region().get(key);
            if (item == null) {
                region().put(key, new Item(value, null, region().nextTimestamp()));
                return true;
            } else {
                return false;
            }
        } finally {
            region().writeUnlock(key);
        }
    }

    @Override
    public boolean update(Object key, Object value) throws CacheException {
        return false;
    }

    @Override
    public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
        region().writeLock(key);
        try {
            final Lockable item = (Lockable) region().get(key);
            if (item != null && item.isUnlockable(lock)) {
                final Lock lockItem = (Lock) item;
                if (lockItem.wasLockedConcurrently()) {
                    decrementLock(key, lockItem);
                    return false;
                } else {
                    region().put(key, new Item(value, null, region().nextTimestamp()));
                    return true;
                }
            } else {
                handleLockExpiry(key, item);
                return false;
            }
        } finally {
            region().writeUnlock(key);
        }
    }
}