package org.jfantasy.graphql.context;

import graphql.kickstart.execution.context.GraphQLContext;
import graphql.kickstart.servlet.context.GraphQLServletContextBuilder;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.security.DefaultSecurityContext;
import org.jfantasy.framework.security.LoginUser;
import org.jfantasy.framework.security.SecurityContextHolder;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-14 14:13
 */
@Component
public class SecurityGraphQLContextBuilder implements GraphQLServletContextBuilder {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public GraphQLContext build(HttpServletRequest req, HttpServletResponse response) {
        AuthorizationGraphQLServletContext context = new AuthorizationGraphQLServletContext(req, response);
        context.setDataLoaderRegistry(buildDataLoaderRegistry());

        String authorization = req.getHeader("Authorization");
        if(StringUtil.isNotBlank(authorization) && authorization.startsWith("token ")){
            String value = redisTemplate.boundValueOps(authorization.replaceAll("^token ","")).get();
            if (!StringUtils.isEmpty(value)) {
                Map<String, String> map = JSON.deserialize(value, HashMap.class);
                LoginUser user = JSON.deserialize(JSON.serialize(map.get("user")), LoginUser.class);
                SecurityContextHolder.setContext(new DefaultSecurityContext(user));
            }
        }
        return context;
    }

    @Override
    public GraphQLContext build(Session session, HandshakeRequest request) {
        AuthorizationGraphQLServletContext context = new AuthorizationGraphQLServletContext(session, request);
        context.setDataLoaderRegistry(buildDataLoaderRegistry());
        return context;
    }

    private DataLoaderRegistry buildDataLoaderRegistry() {
        DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
        dataLoaderRegistry.register("customerDataLoader",
            new DataLoader<Integer, String>(customerIds ->
                CompletableFuture.supplyAsync(() -> {
                    System.out.println(customerIds);
                    return new ArrayList<>();
                })));
        return dataLoaderRegistry;
    }

    @Override
    public GraphQLContext build() {
        return null;
    }
}