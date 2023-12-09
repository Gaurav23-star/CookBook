package com.cookbook;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;



import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import com.cookbook.model.ApiResponse;

import java.net.HttpURLConnection;

@RunWith(AndroidJUnit4.class)
    public class SignUpActivityTest {

        @Before
        public void setUp() {
            Intents.init();
            ApiResponse mockResponse = new ApiResponse(HttpURLConnection.HTTP_INTERNAL_ERROR,"");
            ApiCaller.setMockApiResponse(mockResponse);
        }
        @Test
        public void testSuccessfulSignUp() {
            // Launch the SignUpActivity
            ActivityScenario.launch(SignUpActivity.class);


            Espresso.onView(withId(R.id.firstNameEditText)).perform(ViewActions.typeText("John"));
            Espresso.onView(withId(R.id.lastNameEditText)).perform(ViewActions.typeText("Doe"));
            Espresso.onView(withId(R.id.emailEditTextSignUp)).perform(ViewActions.typeText("john.doe@example.com"));
            Espresso.onView(withId(R.id.passwordEditTextSignUp)).perform(ViewActions.typeText("password"));
            Espresso.onView(withId(R.id.usernameEditTextSignUp)).perform(ViewActions.typeText("john_doe"));
            Espresso.onView(withId(R.id.signupButton)).perform(ViewActions.click());

            // Check if the LoginActivity is launched after successful signup
            Espresso.onView(ViewMatchers.withId(R.id.login_activity))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }

        @Test
        public void testUserAlreadyExists() {
            // Launch the SignUpActivity
            ActivityScenario.launch(SignUpActivity.class);

            // Mock the ApiCaller to return a response indicating that the user already exists

            // Perform actions that lead to user already exists scenario
            Espresso.onView(withId(R.id.firstNameEditText)).perform(ViewActions.typeText("John"));
            Espresso.onView(withId(R.id.lastNameEditText)).perform(ViewActions.typeText("Doe"));
            Espresso.onView(withId(R.id.passwordEditTextSignUp)).perform(ViewActions.typeText("password"));
            Espresso.onView(withId(R.id.usernameEditTextSignUp)).perform(ViewActions.typeText("john_doe"));
            Espresso.onView(withId(R.id.emailEditTextSignUp)).perform(ViewActions.typeText("dummy@dummy.com"));

            // Perform signup
            Espresso.onView(withId(R.id.signupButton)).perform(ViewActions.click());

            // Check if the error message is displayed
            Espresso.onView(withId(R.id.errorTextViewSignUp))
                    .check(ViewAssertions.matches(ViewMatchers.withText("user already exists with email.")));
        }
    }

