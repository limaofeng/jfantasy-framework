package org.jfantasy.auth.rest;

import org.jfantasy.member.Profile;
import org.jfantasy.member.ProfileFactory;
import org.jfantasy.member.ProfileService;
import org.jfantasy.security.bean.enums.Sex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ProfileInit implements CommandLineRunner {

    @Autowired
    private ProfileFactory profileFactory;

    @Override
    public void run(String... args) throws Exception {
        profileFactory.register("doctor", new ProfileService() {
            @Override
            public Profile loadProfileByPhone(String name) {
                return new Profile() {
                    @Override
                    public String getName() {
                        return null;
                    }

                    @Override
                    public Sex getSex() {
                        return null;
                    }

                    @Override
                    public Date getBirthday() {
                        return null;
                    }

                    @Override
                    public String getMobile() {
                        return null;
                    }

                    @Override
                    public String getTel() {
                        return null;
                    }

                    @Override
                    public String getEmail() {
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return null;
                    }

                    @Override
                    public String getId() {
                        return "4028808b57230f800157232807800000";
                    }
                };
            }
        });
    }

}
