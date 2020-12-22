package org.jfantasy.graphql.context;

import graphql.kickstart.execution.context.GraphQLContext;
import graphql.kickstart.servlet.context.GraphQLServletContextBuilder;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.security.DefaultSecurityContext;
import org.jfantasy.framework.security.SecurityContextHolder;
import org.jfantasy.framework.security.LoginUser;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-14 14:13
 */
@Component
public class SecurityGraphQLContextBuilder implements GraphQLServletContextBuilder {

    @Override
    public GraphQLContext build(HttpServletRequest req, HttpServletResponse response) {
        AuthorizationGraphQLServletContext context = new AuthorizationGraphQLServletContext(req, response);
        context.setDataLoaderRegistry(buildDataLoaderRegistry());

        String authorization = req.getHeader("Authorization");
        if(StringUtil.isNotBlank(authorization) && authorization.startsWith("token ")){
            LoginUser user = JSON.deserialize(new String(Base64Utils.decodeFromString(authorization.replaceAll("^token ", ""))), LoginUser.class);
            SecurityContextHolder.setContext(new DefaultSecurityContext(user));
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