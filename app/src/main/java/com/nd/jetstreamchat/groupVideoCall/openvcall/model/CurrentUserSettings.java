package com.nd.jetstreamchat.groupVideoCall.openvcall.model;

@SuppressWarnings("ALL")
public class CurrentUserSettings {
    public int mEncryptionModeIndex;

    public String mEncryptionKey;

    public String mChannelName;

    public CurrentUserSettings() {
        reset();
    }

    public void reset() {
    }
}
