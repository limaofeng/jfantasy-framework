package org.jfantasy.member;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProfileFactoryBean implements ProfileFactory {

    private static final ConcurrentMap<String, ProfileService> profileServiceCache = new ConcurrentHashMap<>();

    @Override
    public void register(String type, ProfileService profileService) {
        profileServiceCache.put(type, profileService);
    }

    @Override
    public ProfileService getProfileService(String type) {
        return profileServiceCache.get(type);
    }

}
