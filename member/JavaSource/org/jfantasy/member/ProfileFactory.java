package org.jfantasy.member;

public interface ProfileFactory {

    void register(String type, ProfileService profileService);

    ProfileService getProfileService(String type);

}
