package com.fantasy.framework.dao.hibernate.cache;


import com.fantasy.attr.storage.bean.Article;
import com.fantasy.framework.util.cglib.CglibUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.hibernate.HibernateException;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.QueryCache;
import org.hibernate.cache.spi.QueryKey;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.UpdateTimestampsCache;
import org.hibernate.cfg.Settings;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeHelper;
import org.jboss.logging.Logger;

import javax.persistence.EntityNotFoundException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

public class CacheQueryCache implements QueryCache {

    private static final CoreMessageLogger LOG = Logger.getMessageLogger(
            CoreMessageLogger.class,
            CacheQueryCache.class.getName()
    );

    private static final boolean DEBUGGING = LOG.isDebugEnabled();
    private static final boolean TRACING = LOG.isTraceEnabled();

    private QueryResultsRegion cacheRegion;
    private UpdateTimestampsCache updateTimestampsCache;

    /**
     * Constructs a StandardQueryCache instance
     *
     * @param settings              The SessionFactory settings.
     * @param props                 Any properties
     * @param updateTimestampsCache The update-timestamps cache to use.
     * @param regionName            The base query cache region name
     */
    public CacheQueryCache(
            final Settings settings,
            final Properties props,
            final UpdateTimestampsCache updateTimestampsCache,
            final String regionName) {
        String regionNameToUse = regionName;
        if (regionNameToUse == null) {
            regionNameToUse = CacheQueryCache.class.getName();
        }
        final String prefix = settings.getCacheRegionPrefix();
        if (prefix != null) {
            regionNameToUse = prefix + '.' + regionNameToUse;
        }
        LOG.startingQueryCache(regionNameToUse);

        this.cacheRegion = settings.getRegionFactory().buildQueryResultsRegion(regionNameToUse, props);
        this.updateTimestampsCache = updateTimestampsCache;
    }

    @Override
    public QueryResultsRegion getRegion() {
        return cacheRegion;
    }

    @Override
    public void destroy() {
        try {
            cacheRegion.destroy();
        } catch (Exception e) {
            LOG.unableToDestroyQueryCache(cacheRegion.getName(), e.getMessage());
        }
    }

    @Override
    public void clear() throws CacheException {
        cacheRegion.evictAll();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public boolean put(
            final QueryKey key,
            final Type[] returnTypes,
            final List result,
            final boolean isNaturalKeyLookup,
            final SessionImplementor session) throws HibernateException {
        if (isNaturalKeyLookup && result.isEmpty()) {
            return false;
        }
        final long ts = cacheRegion.nextTimestamp();

        if (DEBUGGING) {
            LOG.debugf("Caching query results in region: %s; timestamp=%s", cacheRegion.getName(), ts);
        }

        final List cacheable = new ArrayList(result.size() + 1);
        logCachedResultDetails(key, null, returnTypes, cacheable);
        cacheable.add(ts);

        final boolean isSingleResult = returnTypes.length == 1;
        for(int i=0;i<result.size();i++){
            final Object aResult = result.get(i);
            final Serializable cacheItem = isSingleResult
                    ? returnTypes[0].disassemble(aResult, session, null)
                    : TypeHelper.disassemble((Object[]) aResult, returnTypes, null, session, null);
            cacheable.add(cacheItem);
            logCachedResultRowDetails(returnTypes, aResult);
            //TODO hibernate默认的查询缓存，只缓存标识符。对象还是通过session中load出来的
            if (!ManyToOneType.class.isAssignableFrom(returnTypes[0].getClass())) {
                continue;
            }
            if (isSingleResult) {
                ManyToOneType manyToOneType = ((ManyToOneType) returnTypes[0]);
                if(Article.class.isAssignableFrom(manyToOneType.getReturnedClass())){
//                    cacheRegion.put(manyToOneType.getAssociatedEntityName() + ":" + cacheItem,aResult);
                    Article article = CglibUtil.newInstance(Article.class, new MethodInterceptor() {

                        @Override
                        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                            System.out.println("put >" + method.getName());
                            return methodProxy.invoke(aResult, objects);
                        }

                    });
                    result.set(i,article);
                }
            } else {
                System.err.print("xxxxx");
            }
        }

        try {
            session.getEventListenerManager().cachePutStart();
            cacheRegion.put(key, cacheable);
        } finally {
            session.getEventListenerManager().cachePutEnd();
        }

        return true;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public List get(
            final QueryKey key,
            final Type[] returnTypes,
            final boolean isNaturalKeyLookup,
            final Set<Serializable> spaces,
            final SessionImplementor session) throws HibernateException {
        if (DEBUGGING) {
            LOG.debugf("Checking cached query results in region: %s", cacheRegion.getName());
        }

        final List cacheable = getCachedResults(key, session);
        logCachedResultDetails(key, spaces, returnTypes, cacheable);

        if (cacheable == null) {
            if (DEBUGGING) {
                LOG.debug("Query results were not found in cache");
            }
            return null;
        }

        final Long timestamp = (Long) cacheable.get(0);
        if (!isNaturalKeyLookup && !isUpToDate(spaces, timestamp, session)) {
            if (DEBUGGING) {
                LOG.debug("Cached query results were not up-to-date");
            }
            return null;
        }

        if (DEBUGGING) {
            LOG.debug("Returning cached query results");
        }
        final boolean singleResult = returnTypes.length == 1;
        for (int i = 1; i < cacheable.size(); i++) {
            if (singleResult) {
                returnTypes[0].beforeAssemble((Serializable) cacheable.get(i), session);
            } else {
                TypeHelper.beforeAssemble((Serializable[]) cacheable.get(i), returnTypes, session);
            }
        }

        final List result = new ArrayList(cacheable.size() - 1);
        for (int i = 1; i < cacheable.size(); i++) {
            try {
                if (singleResult) {
                    if (!ManyToOneType.class.isAssignableFrom(returnTypes[0].getClass())) {
                        result.add(returnTypes[0].assemble((Serializable) cacheable.get(i), session, null));
                    } else {
                        //TODO hibernate默认的查询缓存，只缓存标识符。对象还是通过session中load出来的
                        Object object = cacheRegion.get(((ManyToOneType) returnTypes[0]).getAssociatedEntityName() + ":" + cacheable.get(i));
                        if(object == null){
                            object = returnTypes[0].assemble((Serializable) cacheable.get(i), session, null);
                        }
                        ManyToOneType manyToOneType = ((ManyToOneType) returnTypes[0]);
                        if(Article.class.isAssignableFrom(manyToOneType.getReturnedClass())) {
                            final Object art = object;
                            Article article = CglibUtil.newInstance(Article.class, new MethodInterceptor() {

                                @Override
                                public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                                    return methodProxy.invoke(art, objects);
                                }

                            });
                            result.add(article);
                        }else{
                            result.add(object);
                        }
                    }
                } else {
                    System.err.print(Arrays.toString(returnTypes));
                    result.add(
                            TypeHelper.assemble((Serializable[]) cacheable.get(i), returnTypes, session, null)
                    );
                }
                logCachedResultRowDetails(returnTypes, result.get(i - 1));
            } catch (RuntimeException ex) {
                if (isNaturalKeyLookup) {
                    // potentially perform special handling for natural-id look ups.
                    if (UnresolvableObjectException.class.isInstance(ex)
                            || EntityNotFoundException.class.isInstance(ex)) {
                        if (DEBUGGING) {
                            LOG.debug("Unable to reassemble cached natural-id query result");
                        }
                        cacheRegion.evict(key);

                        // EARLY EXIT !!!!!
                        return null;
                    }
                }
                throw ex;
            }
        }
        return result;
    }

    private List getCachedResults(QueryKey key, SessionImplementor session) {
        List cacheable = null;
        try {
            session.getEventListenerManager().cacheGetStart();
            cacheable = (List) cacheRegion.get(key);
        } finally {
            session.getEventListenerManager().cacheGetEnd(cacheable != null);
        }
        return cacheable;
    }


    protected boolean isUpToDate(Set<Serializable> spaces, Long timestamp, SessionImplementor session) {
        if (DEBUGGING) {
            LOG.debugf("Checking query spaces are up-to-date: %s", spaces);
        }
        return updateTimestampsCache.isUpToDate(spaces, timestamp, session);
    }

    @Override
    public String toString() {
        return "StandardQueryCache(" + cacheRegion.getName() + ')';
    }

    private static void logCachedResultDetails(QueryKey key, Set querySpaces, Type[] returnTypes, List result) {
        if (!TRACING) {
            return;
        }
        LOG.trace("key.hashCode=" + key.hashCode());
        LOG.trace("querySpaces=" + querySpaces);
        if (returnTypes == null || returnTypes.length == 0) {
            LOG.trace(
                    "Unexpected returnTypes is "
                            + (returnTypes == null ? "null" : "empty") + "! result"
                            + (result == null ? " is null" : ".size()=" + result.size())
            );
        } else {
            final StringBuilder returnTypeInfo = new StringBuilder();
            for (Type returnType : returnTypes) {
                returnTypeInfo.append("typename=")
                        .append(returnType.getName())
                        .append(" class=")
                        .append(returnType.getReturnedClass().getName())
                        .append(' ');
            }
            LOG.trace("unexpected returnTypes is " + returnTypeInfo.toString() + "! result");
        }
    }

    private static void logCachedResultRowDetails(Type[] returnTypes, Object result) {
        if (!TRACING) {
            return;
        }
        logCachedResultRowDetails(
                returnTypes,
                (result instanceof Object[] ? (Object[]) result : new Object[]{result})
        );
    }

    private static void logCachedResultRowDetails(Type[] returnTypes, Object[] tuple) {
        if (!TRACING) {
            return;
        }
        if (tuple == null) {
            LOG.tracef(
                    "tuple is null; returnTypes is %s",
                    returnTypes == null ? "null" : "Type[" + returnTypes.length + "]"
            );
            if (returnTypes != null && returnTypes.length > 1) {
                LOG.trace(
                        "Unexpected result tuple! tuple is null; should be Object["
                                + returnTypes.length + "]!"
                );
            }
        } else {
            if (returnTypes == null || returnTypes.length == 0) {
                LOG.trace(
                        "Unexpected result tuple! tuple is null; returnTypes is "
                                + (returnTypes == null ? "null" : "empty")
                );
            }
            LOG.tracef(
                    "tuple is Object[%s]; returnTypes is %s",
                    tuple.length,
                    returnTypes == null ? "null" : "Type[" + returnTypes.length + "]"
            );
            if (returnTypes != null && tuple.length != returnTypes.length) {
                LOG.trace(
                        "Unexpected tuple length! transformer= expected="
                                + returnTypes.length + " got=" + tuple.length
                );
            } else {
                for (int j = 0; j < tuple.length; j++) {
                    if (tuple[j] != null && returnTypes != null
                            && !returnTypes[j].getReturnedClass().isInstance(tuple[j])) {
                        LOG.trace(
                                "Unexpected tuple value type! transformer= expected="
                                        + returnTypes[j].getReturnedClass().getName()
                                        + " got="
                                        + tuple[j].getClass().getName()
                        );
                    }
                }
            }
        }
    }

}
