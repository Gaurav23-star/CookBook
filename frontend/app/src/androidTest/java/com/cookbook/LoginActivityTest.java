package com.cookbook;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.rule.ActivityTestRule;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.*;

import com.cookbook.model.User;
import com.google.gson.Gson;
import com.cookbook.model.ApiResponse;

import java.net.HttpURLConnection;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    private static final String INVALID_EMAIL = "invalid@email.com";
    private static final String INVALID_PASS = "fortnite";
    private static final String TEST_EMAIL = "dummy@dummy.com";
    private static final String TEST_PASSWORD = "12345";

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<LoginActivity>(LoginActivity.class);
    @Before
    public void setUp() {
        ApiResponse mockResponse = new ApiResponse(HttpURLConnection.HTTP_OK,
                "{\"user_id\":9,\"first_name\":\"Daniel\",\"last_name\":\"Lusdyk\",\"email_id\":\"dummy@dummy.com\",\"password\":\"12345\",\"isAdmin\":0,\"isBanned\":1,\"username\":\"dummy\"}");
        ApiCaller.get_caller_instance();
        ApiCaller.setMockApiResponse(mockResponse);
    }

    @Test
    public void testValidLogin() {
        // Start the activity

        ActivityScenario.launch(LoginActivity.class);
        // Enter valid credentials and click login

        onView(withId(R.id.emailEditText)).perform(ViewActions.typeText("dummy@dummy.com"));
        onView(withId(R.id.passwordEditText)).perform(ViewActions.typeText("12345"));
        onView(withId(R.id.loginButton_id)).perform(ViewActions.click());
        Intents.init();
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
        /*Intents.release();
        intended(allOf(
                hasExtra(PoiActivity.EXTRA_SEARCH_TYPE, PlacesAPIRequest.PARAM_SEARCH_TYPE_DESTINATION),
                hasComponent("com.XXX.passenger.poi.PoiActivity")));*/

    }

    @Test
    public void testInvalidLogin() {
        // Start the activity
        ActivityScenario.launch(LoginActivity.class);

        // Enter invalid credentials and click login
        Espresso.onView(withId(R.id.emailEditText)).perform(ViewActions.typeText(INVALID_EMAIL));
        Espresso.onView(withId(R.id.passwordEditText)).perform(ViewActions.typeText(INVALID_PASS));
        Espresso.onView(withId(R.id.loginButton_id)).perform(ViewActions.click());

        // Check if the error message is displayed
        Espresso.onView(withId(R.id.ErrorTextView)).check(ViewAssertions.matches(ViewMatchers.withText("invalid credentials. please try again.")));
    }



}

