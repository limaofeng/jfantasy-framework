package org.jfantasy.weixin.framework.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.weixin.framework.core.Jsapi;
import org.jfantasy.weixin.framework.core.WeixinCoreHelper;
import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.message.content.*;
import org.jfantasy.weixin.framework.message.user.Group;
import org.jfantasy.weixin.framework.message.user.User;
import org.jfantasy.weixin.framework.oauth2.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 微信 session 抽象实现
 */
public abstract class AbstractWeixinSession implements WeixinSession {

    private final Log LOG = LogFactory.getLog(this.getClass());

    private String id;

    //缓存所有group信息
    private List<Group> groups = new ArrayList<Group>();
    private WeixinApp weixinApp;
    private WeixinCoreHelper weixinCoreHelper;
    private static final ExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public AbstractWeixinSession(WeixinApp weixinApp, WeixinCoreHelper weixinCoreHelper) {
        this.weixinApp = weixinApp;
        this.id = weixinApp.getId();
        this.weixinCoreHelper = weixinCoreHelper;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void sendImageMessage(final Image content, final String... toUsers) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AbstractWeixinSession.this.weixinCoreHelper.sendImageMessage(AbstractWeixinSession.this, content, toUsers);
                } catch (WeixinException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void sendImageMessage(final Image content, final long toGroup) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AbstractWeixinSession.this.weixinCoreHelper.sendImageMessage(AbstractWeixinSession.this, content, toGroup);
                } catch (WeixinException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void sendVoiceMessage(final Voice content, final String... toUsers) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AbstractWeixinSession.this.weixinCoreHelper.sendVoiceMessage(AbstractWeixinSession.this, content, toUsers);
                } catch (WeixinException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void sendVoiceMessage(final Voice content, final long toGroup) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AbstractWeixinSession.this.weixinCoreHelper.sendVoiceMessage(AbstractWeixinSession.this, content, toGroup);
                } catch (WeixinException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void sendVideoMessage(final Video content, final String... toUsers) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AbstractWeixinSession.this.weixinCoreHelper.sendVideoMessage(AbstractWeixinSession.this, content, toUsers);
                } catch (WeixinException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void sendVideoMessage(final Video content, final long toGroup) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AbstractWeixinSession.this.weixinCoreHelper.sendVideoMessage(AbstractWeixinSession.this, content, toGroup);
                } catch (WeixinException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void sendMusicMessage(final Music content, final String toUser) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AbstractWeixinSession.this.weixinCoreHelper.sendMusicMessage(AbstractWeixinSession.this, content, toUser);
                } catch (WeixinException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void sendNewsMessage(final List<Article> content, final String... toUsers) {
        if (content.isEmpty()) {
            return;
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AbstractWeixinSession.this.weixinCoreHelper.sendNewsMessage(AbstractWeixinSession.this, content, toUsers);
                } catch (WeixinException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void sendNewsMessage(final List<News> content, final String toUser) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AbstractWeixinSession.this.weixinCoreHelper.sendNewsMessage(AbstractWeixinSession.this, content, toUser);
                } catch (WeixinException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void sendNewsMessage(final List<Article> content, final long toGroup) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AbstractWeixinSession.this.weixinCoreHelper.sendNewsMessage(AbstractWeixinSession.this, content, toGroup);
                } catch (WeixinException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void sendTextMessage(final String content, final String... toUsers) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AbstractWeixinSession.this.weixinCoreHelper.sendTextMessage(AbstractWeixinSession.this, content, toUsers);
                } catch (WeixinException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void sendTextMessage(final String content, final long toGroup) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AbstractWeixinSession.this.weixinCoreHelper.sendTextMessage(AbstractWeixinSession.this, content, toGroup);
                } catch (WeixinException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public void sendTemplateMessage(final Template content, final String toUser) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AbstractWeixinSession.this.weixinCoreHelper.sendTemplateMessage(AbstractWeixinSession.this, content, toUser);
                } catch (WeixinException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public String getAuthorizationUrl(String redirectUri, Scope scope) {
        try {
            return this.weixinCoreHelper.oauth2buildAuthorizationUrl(this, redirectUri, scope, "");
        } catch (WeixinException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getAuthorizationUrl(String redirectUri, Scope scope, String state) {
        try {
            return this.weixinCoreHelper.oauth2buildAuthorizationUrl(this, redirectUri, scope, state);
        } catch (WeixinException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public User getOauth2User(String code) {
        try {
            return this.weixinCoreHelper.getOauth2User(this, code);
        } catch (WeixinException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public User getUser(String userId) {
        try {
            return this.weixinCoreHelper.getUser(this, userId);
        } catch (WeixinException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<User> getUsers() {
        try {
            return this.weixinCoreHelper.getUsers(this);
        } catch (WeixinException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void refreshMenu(Menu... menus) {
        try {
            this.weixinCoreHelper.refreshMenu(this, menus);
        } catch (WeixinException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public List<Menu> getMenus() {
        try {
            return this.weixinCoreHelper.getMenus(this);
        } catch (WeixinException e) {
            LOG.error(e.getMessage(), e);
            throw new IgnoreException(e.getMessage());
        }
    }

    @Override
    public void clearMenu() {
        try {
            this.weixinCoreHelper.clearMenu(this);
        } catch (WeixinException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public WeixinApp getWeixinApp() {
        return this.weixinApp;
    }

    @Override
    public Jsapi getJsapi() {
        try {
            return this.weixinCoreHelper.getJsapi(this);
        } catch (WeixinException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

}
