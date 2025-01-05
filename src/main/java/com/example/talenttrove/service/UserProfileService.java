package com.example.talenttrove.service;


import com.example.talenttrove.doa.Users_Repo;
import java.util.Optional;
import com.example.talenttrove.dto.UserProfile;
import com.example.talenttrove.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    @Autowired
    private Users_Repo users_Repo;


    public UserProfile getProfile(String email) {

        return users_Repo.findByEmail(email)
                .map(user -> {

                    UserProfile profile = new UserProfile();
                    profile.setUsername(user.getUsername());
                    profile.setEmail(user.getEmail());
                    profile.setMobile(user.getMobile());
                    profile.setTitle(user.getTitle());
                    profile.setBio(user.getBio());
                    profile.setAddress(user.getAddress());
                    profile.setWebsite(user.getWebsite());
                    // profile.setSkills(user.getSkills()); // Uncomment when implemented
                    return profile;
                })
                .orElse(null);
    }


    public Users updateProfile(UserProfile userProfile) throws Exception {
        Optional<Users> existingProfileOpt = users_Repo.findByEmail(userProfile.getEmail());
        if (!existingProfileOpt.isPresent()) {
            throw new Exception("Profile not found for email: " + userProfile.getEmail());
        }
        Users existingProfile = existingProfileOpt.get();
        existingProfile.setUsername(userProfile.getUsername());
        existingProfile.setMobile(userProfile.getMobile());
        existingProfile.setTitle(userProfile.getTitle());
        existingProfile.setBio(userProfile.getBio());
        existingProfile.setAddress(userProfile.getAddress());
        existingProfile.setWebsite(userProfile.getWebsite());
        return users_Repo.save(existingProfile);
    }
}
