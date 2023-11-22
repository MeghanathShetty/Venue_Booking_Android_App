package com.example.myapplication.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.example.myapplication.R;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Properties;


public class emailHelper {
    private static final int REQUEST_AUTHORIZATION = 123;
    public static void sendEmailInBackground(final Context context,final Activity callingActivity, final String recipientEmail, final String subject, final String emailContent) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                sendEmail(context,callingActivity, recipientEmail, subject, emailContent);
            }
        });

        thread.start();
    }

    private static void sendEmail(Context context,Activity callingActivity, String recipientEmail, String subject, String emailContent) {
        try {
            String[] SCOPES = {"https://www.googleapis.com/auth/gmail.send"};
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                    context,
                    Collections.singleton(SCOPES[0])
            );

            // Set the Google account (replace with the desired Gmail account):
            credential.setSelectedAccountName(context.getString(R.string.from_email));

            Gmail service = new Gmail.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    AndroidJsonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName(context.getString(R.string.app_name))
                    .build();

            MimeMessage email = createEmail(context, recipientEmail, subject, emailContent);
            Message message = createMessageWithEmail(email);

            service.users().messages().send("me", message).execute();
            Log.d("My-Log:", "Email sent");
        }
        catch (UserRecoverableAuthIOException userRecoverableException) {
            // Start the authorization activity from the callingActivity.
            callingActivity.startActivityForResult(
                    userRecoverableException.getIntent(), REQUEST_AUTHORIZATION);
        } catch (Exception e) {
            Log.e("App-Error:", "Email Error = " + e.getMessage());
            Log.e("App-Error:", context.getString(R.string.from_email));

            e.printStackTrace();
        }
    }

    private static MimeMessage createEmail(Context con,String to, String subject, String body) {
        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage email = new MimeMessage(session);
            email.setFrom(new InternetAddress(con.getString(R.string.from_email))); // Set your email address
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
            email.setSubject(subject);
            email.setText(body);

            return email;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Message createMessageWithEmail(MimeMessage email) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            byte[] bytes = buffer.toByteArray();
            String encodedEmail = Base64.encodeBase64URLSafeString(bytes);

            Message message = new Message();
            message.setRaw(encodedEmail);

            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
