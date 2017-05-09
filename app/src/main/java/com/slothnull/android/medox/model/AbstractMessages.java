package com.slothnull.android.medox.model;

/**
 * Created by Shaheen on 15-Apr-17
 * Project: Medox
 * Package: com.slothnull.android.medox
 */
import com.google.firebase.database.IgnoreExtraProperties;

// [START user_class]
@IgnoreExtraProperties
public class AbstractMessages {
    public String to;
    public String level;
    public String message;
    public String title;

    public AbstractMessages() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AbstractMessages(String token, String level ) {
        this.to = token;
        this.level = level;
        this.message="message";
        this.title = "title";
    }
}
