package com.cookbook;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.cookbook.model.ApiResponse;
import com.cookbook.model.Recipe;
import com.cookbook.model.User;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class FavoriteActivityTest {
    @Mock
    private MyAdapter mockedAdapter;
    User mockedUser = Mockito.mock(User.class);
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, FavoriteActivity.class);
                    result.putExtra("current_user", mockedUser);
                    return result;
                }
            };

    @Before
    public void setUp() {

        // Inject the mocked adapter into the RecyclerView
        User mockedUser = Mockito.mock(User.class);
        Mockito.when(mockedUser.getUser_id()).thenReturn(9); // Set user_id as needed
        Mockito.when(mockedUser.getEmail_id()).thenReturn("dummy@dummy.com"); // Set email_id as needed
        Mockito.when(mockedUser.getPassword()).thenReturn("12345"); // Set password as needed
        Mockito.when(mockedUser.getIsAdmin()).thenReturn(0); // Set isAdmin as needed
        Mockito.when(mockedUser.getIsBanned()).thenReturn(0); // Set isBanned as needed
        Mockito.when(mockedUser.getUsername()).thenReturn("dummy"); // Set username as needed\
    }

    @Test
    public void testRecyclerViewDisplayed() {
        onView(ViewMatchers.withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }
}