// apiTests.js

const assert = require('assert');
const axios = require('axios'); // or use your preferred HTTP client

// Replace with the actual base URL of your API
const BASE_URL = 'http://localhost:8080'; 

describe('API Tests', () => {

 // Test case for creating an account
 const newAccountUserId = 123456789;
 const newRecipeId = 987654321;

  it('Should create an account', async () => {
    const response = await axios.post(`${BASE_URL}/create-account`, {
      // Provide test data for creating an account
      user_id: newAccountUserId,
      first_name: 'Auto',
      last_name: 'Test',
      email_id: 'testuser@example.com',
      password: 'testpassword',
      username: 'testuser'
    });

    assert.strictEqual(response.status, 200);
    // Add more assertions based on your expected response
  });

  // Test case for login
  it('Should log in', async () => {
    const response = await axios.post(`${BASE_URL}/login`, {
      // Provide test data for logging in
      email_id: 'testuser@example.com',
      password: 'testpassword',
    });

    assert.strictEqual(response.status, 200);
    // Add more assertions based on your expected response
  });
  

  //Test case for create recipe
  it('Should create a recipe', async () => {
    const response = await axios.post(`${BASE_URL}/user-defined-recipes`, {
      // Provide test data for logging in
      recipe_id: newRecipeId,
      recipe_name: 'Auto Test Recipe',
      servings: 10,
      preparation_time_minutes: 25,
      ingredients: 'Testing',
      description: 'Automated testing recipe creation',
      instructions: 'Start Automated Testing',
      user_id: newAccountUserId
    });

    assert.strictEqual(response.status, 200);
    // Add more assertions based on your expected response
  });

  //Test case for get recipe
  it('Should get a recipe', async () => {
    const response = await axios.get(`${BASE_URL}/user-defined-recipes?user_id=${newAccountUserId}`);
    assert.strictEqual(response.status, 200);
  })
  
  //Test case for update a recipe
  it('Should update a recipe', async () => {
    const response = await axios.patch(`${BASE_URL}/user-defined-recipes?recipe_id=${newRecipeId}`, {
      // Provide test data for logging in
      recipe_name: 'Auto Test Recipe 2',
      servings: 10,
      preparation_time_minutes: 30,
      ingredients: 'Testing 2',
      description: 'Automated testing recipe creation 2',
      instructions: 'Start Automated Testing 2',
      user_id: newAccountUserId
    });

    assert.strictEqual(response.status, 200);
    // Add more assertions based on your expected response
  });

  //Test case for delete recipe
  it('Should delete a recipe', async () => {
    const response = await axios.delete(`${BASE_URL}/user-defined-recipes?recipe_id=${newRecipeId}&user_id=${newAccountUserId}`);
    assert.strictEqual(response.status, 200);
    
  })

  //Test case for delete account
  it('Should delete an account', async () => {
        const response = await axios.delete(`${BASE_URL}/delete-account?userId=${newAccountUserId}`);
        assert.strictEqual(response.status, 200);
  })

  // Add more test cases for other endpoints...

  //Test case for getting a users followers & following counts
  it('Should get a users followers & following count', async () => {
    const response = await axios.get(`${BASE_URL}/user-followers-following-count?user_id=${newAccountUserId}`);
    assert.strictEqual(response.status, 200);
  })

  it('Should get a users followers & following count when they have none', async () => {
    const response = await axios.get(`${BASE_URL}/user-followers-following-count?user_id=${1}`);
    assert.strictEqual(response.status, 200);
  })

  it('Should get a users following network list ', async () => {
    const typeOfNetworkList = "following"
    const response = await axios.get(`${BASE_URL}/users-network-list?user_id=${newAccountUserId}&network_type=${typeOfNetworkList}`);
    assert.strictEqual(response.status, 200);
  })

  it('Should get a users followers network list ', async () => {
    const typeOfNetworkList = "followers"
    const response = await axios.get(`${BASE_URL}/users-network-list?user_id=${newAccountUserId}&network_type=${typeOfNetworkList}`);
    assert.strictEqual(response.status, 200);
  })

  it('Should check whether a user is following another user', async () => {
    const user_id = newAccountUserId;
    const visitor_id = 45;
    const response = await axios.get(`${BASE_URL}/user-is-following?user_id=${user_id}&visitor_id=${visitor_id}`);
    assert.strictEqual(response.status, 200);
  })

  it('Should check whether a user is being followed by another user', async () => {
    const user_id = 45;
    const visitor_id = newAccountUserId;
    const response = await axios.get(`${BASE_URL}/user-is-following?user_id=${user_id}&visitor_id=${visitor_id}`);
    assert.strictEqual(response.status, 200);
  })

  //Test case for making a user follow another user
  it('Should make a user follow another user', async () => {
    const user_id = 45;
    const visitor_id = 9;
    const response = await axios.post(`${BASE_URL}/user-follow`, {
      user_id: user_id,
      visitor_id: visitor_id,
    });

    assert.strictEqual(response.status, 200);
  });
  
  //Test case for making a user follow another user
  it('Should make a user follow another user', async () => {
    const user_id = 1;
    const visitor_id = 9;
    const response = await axios.post(`${BASE_URL}/user-follow`, {
      user_id: user_id,
      visitor_id: visitor_id,
    });

    assert.strictEqual(response.status, 200);
  });

  //Test case for making user unfollow another user
  it('Should make a user un-follow another user', async () => {
    const user_id = 1;
    const visitor_id = 9;

    const response = await axios.delete(`${BASE_URL}/user-unfollow`, {
      data:{
      user_id: user_id,
      visitor_id: visitor_id,
      },
    });

    assert.strictEqual(response.status, 200);
    
  });


  //Test case for making user unfollow another user
  it('Should make a user un-follow another user', async () => {
    const user_id = 45;
    const visitor_id = 9;

    const response = await axios.delete(`${BASE_URL}/user-unfollow`, {
      data:{
      user_id: user_id,
      visitor_id: visitor_id,
      },
    });

    assert.strictEqual(response.status, 200);
    
  });

  it('Should search a user given some text', async () => {
    const text = "guarav_p";
    const response = await axios.get(`${BASE_URL}/user-search?text=${text}`);
    assert.strictEqual(response.status, 200);
  })

  it('Should search a user given non-sense search text', async () => {
    const text = "ABNGOJDMNFGUIODBGNKLGDNF30932n3!!@0nfwK";
    const response = await axios.get(`${BASE_URL}/user-search?text=${text}`);
    assert.strictEqual(response.status, 200);
  })

  it('Should get the user favorite recipes', async () => {
    const user_id = 45;
    const response = await axios.get(`${BASE_URL}/favorites?user_id=${user_id}`);
    assert.strictEqual(response.status, 200);
  })

  it('Should get the user favorite recipes when they have none', async () => {
    const user_id = 10006;
    const response = await axios.get(`${BASE_URL}/favorites?user_id=${user_id}`);
    assert.strictEqual(response.status, 200);
  })


  //Test case for making a user follow another user
  it('Should make a user favorite a recipe ', async () => {
    const user_id = 45;
    const recipe_id = 30110;

    const response = await axios.post(`${BASE_URL}/favorites`, {
      user_id: user_id,
      recipe_id:recipe_id,
    });

    assert.strictEqual(response.status, 200);
  });

  it('Should make a user favorite their own recipe ', async () => {
    const user_id = 45;
    const recipe_id = 2050220;

    const response = await axios.post(`${BASE_URL}/favorites`, {
      user_id: user_id,
      recipe_id:recipe_id,
    });

    assert.strictEqual(response.status, 200);
  });
  

  it('Should un-favorite a users previous favorite recipe', async () => {
    const user_id = 45;
    const recipe_id = 105030;

    const response = await axios.delete(`${BASE_URL}/favorites`, {
      data:{
      user_id: user_id,
      recipe_id: recipe_id,
      },
    });
    assert.strictEqual(response.status, 200);
  })

  it('Should allow a user to un-favorite their own recipe', async () => {
    const user_id = 45;
    const recipe_id = 2050220;

    const response = await axios.delete(`${BASE_URL}/favorites`, {
      data:{
      user_id: user_id,
      recipe_id: recipe_id,
      },
    });
    assert.strictEqual(response.status, 200);
  })

  
  it('Should get the users list of notifications', async () => {
    const to_user_id = 45;
    const response = await axios.get(`${BASE_URL}/users-notifications?to_user_id=${to_user_id}`);
    assert.strictEqual(response.status, 200);
  })

  it('Should get the users list of notifications when they none', async () => {
    const to_user_id = 10006;
    const response = await axios.get(`${BASE_URL}/users-notifications?to_user_id=${to_user_id}`);
    assert.strictEqual(response.status, 200);
  })

  //Test case for making a user follow another user
  it('Should add a notification given from a user to another user ', async () => {
    const type = "like";
    const from_user_id = 9;
    const to_user_id = 45;
    const post_id = 105030;

    const response = await axios.post(`${BASE_URL}/users-notifications`, {
      type:type,
      to_user_id:to_user_id,
      from_user_id: from_user_id,
      post_id: post_id,

    });

    assert.strictEqual(response.status, 200);
  });
    

  it('Should delete a notification given from a user to another user', async () => {
    const type = "like";
    const to_user_id = 45;
    const from_user_id = 9;
    const post_id = 105030;

    const response = await axios.delete(`${BASE_URL}/users-notifications`, {
      data:{
        type:type,
        to_user_id:to_user_id,
        from_user_id: from_user_id,
        post_id: post_id,
      },
    });
    assert.strictEqual(response.status, 200);
  })


  it('Should check whether its true that a user favorited a given recipe', async () => {
    const user_id = 45;
    const recipe_id = 105030;
    const response = await axios.get(`${BASE_URL}/user-has-favorited?user_id=${user_id}&recipe_id=${recipe_id}`);
    assert.strictEqual(response.status, 200);
  })

  
  it('Should get the given recipe from a given notification object', async () => {
    const recipe_id = 105030;
    const response = await axios.get(`${BASE_URL}/get-notification-recipe?recipe_id=${recipe_id}`);
    assert.strictEqual(response.status, 200);
  })

  it('Should get the given recipe from a given notification object', async () => {
    const recipe_id = 30110;
    const response = await axios.get(`${BASE_URL}/get-notification-recipe?recipe_id=${recipe_id}`);
    assert.strictEqual(response.status, 200);
  })



});

// Run the tests
// npx mocha apiTests.js or npm test after starting the server. (Note: don't forget to change base url)
