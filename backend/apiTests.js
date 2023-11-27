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

});

// Run the tests
// npx mocha apiTests.js or npm test after starting the server. (Note: don't forget to change base url)
