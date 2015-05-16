package com.fantasy.framework.dao.hibernate.cache.redis;

import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

import static org.jboss.logging.Logger.Level.WARN;


@MessageLogger(projectCode = "HHH")
public interface RedisCacheMessageLogger extends CoreMessageLogger {


    @LogMessage(level = WARN)
    @Message(
            value = "Attempt to restart an already started RedisRegionFactory.  Use sessionFactory.close() between " +
                    "repeated calls to buildSessionFactory. Using previously created RedisRegionFactory. If this " +
                    "behaviour is required, consider using org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory.",
            id = 20001
    )
    void attemptToRestartAlreadyStartedRedisCacheProvider();

    /**
     * Log a message (WARN) about inability to find configuration file
     *
     * @param name The name of the configuration file
     */
    @LogMessage(level = WARN)
    @Message(value = "Could not find configuration [%s]; using defaults.", id = 20002)
    void unableToFindConfiguration(String name);

    /**
     * Log a message (WARN) about inability to find named cache configuration
     *
     * @param name The name of the cache configuration
     */
    @LogMessage(level = WARN)
    @Message(value = "Could not find a specific RedisCache configuration for cache named [%s]; using defaults.", id = 20003)
    void unableToFindRedisCacheConfiguration(String name);

    /**
     * Logs a message about not being able to resolve the configuration by resource name.
     *
     * @param configurationResourceName The resource name we attempted to resolve
     */
    @LogMessage(level = WARN)
    @Message(
            value = "A configurationResourceName was set to %s but the resource could not be loaded from the classpath. " +
                    "RedisCache will configure itself using defaults.",
            id = 20004
    )
    void unableToLoadConfiguration(String configurationResourceName);

    /**
     * Logs a message (WARN) about attempt to use an incompatible
     */
    @LogMessage(level = WARN)
    @Message(
            value = "The default cache value mode for this RedisCache configuration is \"identity\". " +
                    "This is incompatible with clustered Hibernate caching - the value mode has therefore been " +
                    "switched to \"serialization\"",
            id = 20005
    )
    void incompatibleCacheValueMode();

    /**
     * Logs a message (WARN) about attempt to use an incompatible
     *
     * @param cacheName The name of the cache whose config attempted to specify value mode.
     */
    @LogMessage(level = WARN)
    @Message(value = "The value mode for the cache[%s] is \"identity\". This is incompatible with clustered Hibernate caching - "
            + "the value mode has therefore been switched to \"serialization\"", id = 20006)
    void incompatibleCacheValueModePerCache(String cacheName);

    /**
     * Log a message (WARN) about an attempt to specify read-only caching for a mutable entity
     *
     * @param entityName The name of the entity
     */
    @LogMessage(level = WARN)
    @Message(value = "read-only cache configured for mutable entity [%s]", id = 20007)
    void readOnlyCacheConfiguredForMutableEntity(String entityName);

    /**
     * Log a message (WARN) about expiry of soft-locked region.
     *
     * @param regionName The region name
     * @param key        The cache key
     * @param lock       The lock
     */
    @LogMessage(level = WARN)
    @Message(
            value = "Cache[%s] Key[%s] Lockable[%s]\n" +
                    "A soft-locked cache entry was expired by the underlying RedisCache. If this happens regularly you " +
                    "should consider increasing the cache timeouts and/or capacity limits",
            id = 20008
    )
    void softLockedCacheExpired(String regionName, Object key, String lock);

}