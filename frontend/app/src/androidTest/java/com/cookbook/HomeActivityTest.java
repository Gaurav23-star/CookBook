package com.cookbook;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.cookbook.model.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;


@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {
    @Mock
    public User mockedUser = Mockito.mock(User.class);
    @Rule
    public final ActivityScenarioRule<HomeActivity> scenarioRule =
            new ActivityScenarioRule<>(
                    new Intent(
                            InstrumentationRegistry.getInstrumentation().getTargetContext(),
                            HomeActivity.class).putExtra("current_user", mockedUser));

    @Before
    public void setUp() throws IOException {

        // Inject the mocked adapter into the RecyclerView

        Mockito.when(mockedUser.getUser_id()).thenReturn(9); // Set user_id as needed
        Mockito.when(mockedUser.getEmail_id()).thenReturn("dummy@dummy.com"); // Set email_id as needed
        Mockito.when(mockedUser.getPassword()).thenReturn("12345"); // Set password as needed
        Mockito.when(mockedUser.getIsAdmin()).thenReturn(0); // Set isAdmin as needed
        Mockito.when(mockedUser.getIsBanned()).thenReturn(0); // Set isBanned as needed
        Mockito.when(mockedUser.getUsername()).thenReturn("dummy"); // Set username as needed\


    }

    @Test
    //The system must allow users to view their own recipes as a list.
    public void testRecyclerViewDisplayed() {
        ActivityScenario<HomeActivity> activityActivityScenarioRule= scenarioRule.getScenario();

        // Perform actions with Espresso
        onView(withId(R.id.recyclerview))
                .check(matches(isDisplayed()));

    }

}