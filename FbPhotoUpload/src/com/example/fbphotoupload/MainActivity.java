package com.example.fbphotoupload;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class MainActivity extends Activity {

	static final String PUBLISH_PERMISSION = "publish_actions";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Start the session
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			// Call back for session changes
			@Override
			public void call(final Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {
					Request.newMeRequest(session, new Request.GraphUserCallback() {

						@Override
						public void onCompleted(GraphUser user, Response response) {
							if (user != null) {
								TextView welcome = (TextView) findViewById(R.id.welcome);
								welcome.setText("Hello " + user.getName() + " !");
							}
							if (!session.getPermissions().contains(PUBLISH_PERMISSION)) {
								session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
										MainActivity.this, PUBLISH_PERMISSION));
							}
						}
					}).executeAsync();
				}
			}
		});
		findViewById(R.id.buttonUploadPhoto).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
				Request.newUploadPhotoRequest(Session.getActiveSession(), image,
						new Request.Callback() {

							@Override
							public void onCompleted(Response response) {
								if (response.getError() != null) {
									Log.e("HI", "---" + response.getError().getErrorMessage());
								}
							}
						}).executeAsync();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
}
