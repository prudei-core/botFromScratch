package com.nailBot.cache;


import com.nailBot.botApi.BotState;
import com.nailBot.model.UserProfileData;

public interface DataCache {
    void setUsersCurrentBotState(int userId, BotState botState);

    BotState getUsersCurrentBotState(int userId);

    UserProfileData getUserProfileData(int userId);

    void saveUserProfileData(int userId, UserProfileData userProfileData);
}
