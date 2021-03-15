package org.jfantasy.graphql.context;

import graphql.kickstart.execution.context.GraphQLContext;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2020/4/26 11:33 上午
 */
public class AuthorizationGraphQLServletContext implements GraphQLContext {

    private HttpServletRequest request;
    private Session session;
    private HttpServletResponse response;
    private DataLoaderRegistry dataLoaderRegistry;
    private HandshakeRequest handshakeRequest;

    public AuthorizationGraphQLServletContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public AuthorizationGraphQLServletContext(Session session, HandshakeRequest request) {
        this.session = session;
        this.handshakeRequest = request;
    }

    @Override
    public Optional<Subject> getSubject() {
        return Optional.empty();
    }

    @Override
    public DataLoaderRegistry getDataLoaderRegistry() {
        return dataLoaderRegistry != null ? dataLoaderRegistry : null;
    }

    public void setDataLoaderRegistry(DataLoaderRegistry dataLoaderRegistry) {
        this.dataLoaderRegistry = dataLoaderRegistry;
    }
}
