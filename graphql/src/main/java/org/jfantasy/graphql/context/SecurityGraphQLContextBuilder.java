package org.jfantasy.graphql.context;

import graphql.kickstart.execution.context.DefaultGraphQLContextBuilder;
import graphql.kickstart.execution.context.GraphQLContext;
import graphql.kickstart.servlet.context.GraphQLServletContextBuilder;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.jfantasy.framework.security.DefaultSecurityContext;
import org.jfantasy.framework.security.LoginUser;
import org.jfantasy.framework.security.SecurityContextHolder;
import org.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * 从请求头中获取 Token 并转化未用户
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-14 14:13
 */
@Component
public class SecurityGraphQLContextBuilder extends DefaultGraphQLContextBuilder implements GraphQLServletContextBuilder {

    @Autowired(required = false)
    private GraphQLUserDetailsService userDetailsService;

    @Override
    public GraphQLContext build(HttpServletRequest req, HttpServletResponse response) {
        SecurityContextHolder.clear();

        AuthorizationGraphQLServletContext context = new AuthorizationGraphQLServletContext(req, response);
        context.setDataLoaderRegistry(buildDataLoaderRegistry());

        GraphQLContextHolder.setContext(context);

        String authorization = req.getHeader("Authorization");

        if (StringUtil.isBlank(authorization) || !authorization.startsWith("token ")) {
            return context;
        }

        String token = authorization.replaceAll("^token ", "");

        LoginUser loadedUser = retrieveUser(token);

        if (loadedUser != null) {
            SecurityContextHolder.setContext(new DefaultSecurityContext(loadedUser));
        }

        return context;
    }

    private LoginUser retrieveUser(String token) {
        try {
            return (LoginUser) userDetailsService.loadUserByToken(token);
        } catch (UsernameNotFoundException e) {
            return null;
        }
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

}