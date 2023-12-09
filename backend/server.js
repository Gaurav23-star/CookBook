const express = require('express')
const db = require('./db')
const app = express()
const port = 8080
const bodyParser = require("body-parser");
const fs = require('fs');
const path = require('path');
const multer = require('multer');
const upload = multer();

app.use(bodyParser.raw({ limit: '10mb'}));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));


const authRouter = require('./authenticate')
app.use('/', authRouter)

// Define endpoint constants
const USER_DEFINED_RECIPES_ENDPOINT = '/user-defined-recipes';
const CREATE_ACCOUNT_ENDPOINT = '/create-account';
const DELETE_ACCOUNT_ENDPOINT = '/delete-account';
const LOGIN_ENDPOINT = '/login';
const COMMENTS_ENDPOINT = '/user-defined-recipes/comments';
const USER_FOLLOWERS_FOLLOWING_COUNT_ENDPOINT = "/user-followers-following-count";
const USERS_NETWORK_LIST_ENDPOINT = "/users-network-list";
const USER_IS_FOLLOWING_ENDPOINT = "/user-is-following";
const USER_FOLLOW_ENDPOINT = "/user-follow";
const USER_UNFOLLOW_ENDPOINT = "/user-unfollow";
const UPLOAD_RECIPE_IMAGE_ENDPOINT = USER_DEFINED_RECIPES_ENDPOINT.concat('/upload_image');
const DOWNLOAD_RECIPE_IMAGE_ENDPOINT = USER_DEFINED_RECIPES_ENDPOINT.concat('/download_image/:recipe_id');
const USER_SEARCH_ENDPOINT = "/user-search";
const FAVORITES_ENDPOINT = '/favorites';
const USERS_ENDPOINT = '/users';
const RECIPE_SEARCH = '/search-recipe';
const USER_NOTIFICATION_ENDPOINT = "/users-notifications";
const UPDATE_USER_ENDPOINT = '/update-user'
const USER_HAS_FAVORITED_ENDPOINT = "/user-has-favorited";


// Define table constants
const USER_DEFINED_RECIPES_TABLE = 'user_defined_recipes';
const USERS_TABLE = 'users';
const COMMENTS_TABLE = 'comments';
const FOLLOWS_TABLE = 'follows';
const FAVORITES_TABLE = 'favorites'
const NOTIFICATIONS_TABLE = 'notifications';

// GET method for /favorites
app.get(FAVORITES_ENDPOINT, async (req, res) => {
    try {
        const user_id = req.query.user_id;
        if (user_id == undefined) {
            res.status(400).send('You must provide the user_id.\n');
            return;
        }

        const sql = `SELECT ${USER_DEFINED_RECIPES_TABLE}.* FROM ${FAVORITES_TABLE} JOIN ${USER_DEFINED_RECIPES_TABLE} ON ${FAVORITES_TABLE}.recipe_id = ${USER_DEFINED_RECIPES_TABLE}.recipe_id WHERE ${FAVORITES_TABLE}.user_id = ?`;
        const result = await db.pool.query(sql, [user_id]);
        res.status(200).send(convertBigIntsToNumbers(result));
    } catch (err) {
        console.log(err);
        res.status(500).send(convertBigIntsToNumbers(err))
    }
});


// GET method for /favorites  -> checks if a user liked a given recipe or not
//This could've been done in above endpoint, but i think app.use(bodyParser.urlencoded({ extended: false })); provides 
//an issue and i didn't want to change it to true
app.get(USER_HAS_FAVORITED_ENDPOINT, async (req, res) => {
    try {
        const user_id = req.query.user_id;
        const recipe_id = req.query.recipe_id;

        const sql = `SELECT COUNT(*) FROM ${FAVORITES_TABLE} WHERE user_id =${user_id} AND recipe_id =${recipe_id};`

        let result = await db.pool.query(sql);
        result = serializeResult(result);

        res.status(200).send(result);

    } catch (err) {
        console.log(err);
        res.status(500).send((err))
    }
});

// DELETE method for /favorites
app.delete(FAVORITES_ENDPOINT, async (req, res) => {
    try {
        const user_id = req.body.user_id;
        if (user_id == undefined) {
            res.status(400).send('You must provide the user_id.\n');
            return;
        }

        const recipe_id = req.body.recipe_id;
        if (recipe_id == undefined) {
            res.status(400).send('You must provide a recipe_id to unfavorite.\n');
            return;
        }

        const sqlFavoriteRelationExistsQuery = `SELECT COUNT(*) FROM ${FAVORITES_TABLE} WHERE user_id = ? AND recipe_id = ?`;
        const favoriteRelationExists = await db.pool.query(sqlFavoriteRelationExistsQuery, [user_id, recipe_id]);
        if (favoriteRelationExists[0]['COUNT(*)'] == 0) {
            res.status(200).send('No update made. The user provided already does not favorite the provided recipe.\n');
            return;
        }

        const deleteQuery = `DELETE FROM ${FAVORITES_TABLE} WHERE user_id = ? AND recipe_id = ?`;
        const result = await db.pool.query(deleteQuery, [user_id, recipe_id]);
        res.status(200).send(convertBigIntsToNumbers(result));
    } catch (err) {
        console.log(err);
        res.status(500).send(convertBigIntsToNumbers(err));
    }
});

// POST method for /favorites
app.post(FAVORITES_ENDPOINT, async (req, res) => {
    try {
        const { user_id, recipe_id } = req.body;

        if (user_id && recipe_id) {
            const sql = `INSERT INTO ${FAVORITES_TABLE} VALUES (?, ?)`;

            try {
                const result = await db.pool.query(sql, [user_id, recipe_id]);
                res.status(200).send(convertBigIntsToNumbers(result));
            } catch (err) {
                // Error in SQL query is fault of client (send 200 OK)
                res.status(200).send(convertBigIntsToNumbers(err));
            }
        } else {
            res.status(400).json({ message: 'user_id and recipe_id are required' });
        }
    } catch (err) {
        console.log(err);
        res.status(500).send(convertBigIntsToNumbers(err));
    }
});

// GET method for /user-defined-recipes
app.get(USER_DEFINED_RECIPES_ENDPOINT, async (req, res) => {
    try {
        const user_id = req.query.user_id;
        let condition;
        if (user_id == undefined) condition = "TRUE";
        else condition = `u.user_id = ${user_id}`;

        const sql = `SELECT udr.*, 
            (SELECT COUNT(*) FROM ${COMMENTS_TABLE} c WHERE c.recipe_id = udr.recipe_id) AS num_comments,
            (SELECT COUNT(*) FROM ${FAVORITES_TABLE} f WHERE f.recipe_id = udr.recipe_id) AS num_likes
            FROM ${USER_DEFINED_RECIPES_TABLE} udr 
            JOIN ${USERS_TABLE} u ON u.user_id = udr.user_id WHERE ${condition} AND u.isBanned = 0`;
        const result = await db.pool.query(sql);
        res.status(200).send(convertBigIntsToNumbers(result));
    } catch (err) {
        console.log(err);
        res.status(500).send(convertBigIntsToNumbers(err))
    }
});

// POST method for /user-defined-recipes
app.post(USER_DEFINED_RECIPES_ENDPOINT, async (req, res) => {
    try {
        const userDefinedRecipe = req.body;

        const keys = [];
        const values = [];
        for (const [key, value] of Object.entries(userDefinedRecipe)) {
            if (value !== null && value !== undefined) {
                keys.push(key);
                values.push(value);
            }
        }

        const placeholders = values.map(() => "?").join(", ");
        
        const sql = `INSERT INTO ${USER_DEFINED_RECIPES_TABLE} (${keys.join(", ")}) VALUES (${placeholders})`;

        const result = await db.pool.query(sql, values);

        res.status(200).send(convertBigIntsToNumbers(result));
    } catch (err) {
        console.log(err);
        res.status(500).send(convertBigIntsToNumbers(err));
    }
});

// PATCH method for /user-defined-recipes
app.patch(USER_DEFINED_RECIPES_ENDPOINT, async (req, res) => {
    try {
        const recipe_id = req.query.recipe_id;
        if (recipe_id == undefined) {
            res.status(400).send('You must provide the recipe_id to update as a URL parameter.\n');
            return;
        }

        const sqlRecipeExistsQuery = `SELECT COUNT(*) FROM ${USER_DEFINED_RECIPES_TABLE} WHERE recipe_id = ?`;
        const recipeExists = (await db.pool.query(sqlRecipeExistsQuery, [recipe_id]))[0]['COUNT(*)'];
        if (recipeExists == 0) {
            res.status(200).send('No recipe exists with the provided id.\n');
            return;
        }

        const updates = req.body;

        const keys = [];
        const values = [];
        for (const [key, value] of Object.entries(updates)) {
            if (value !== null && value !== undefined) {
                keys.push(key);
                values.push(value);
            }
        }

        let sqlUpdateQuery = `UPDATE ${USER_DEFINED_RECIPES_TABLE} SET `;
        for (let i = 0; i < keys.length; i++) {
            sqlUpdateQuery = sqlUpdateQuery.concat(`${keys[i]} = ?`);
            if (i !== keys.length - 1) sqlUpdateQuery = sqlUpdateQuery.concat(', ');
        }
        sqlUpdateQuery = sqlUpdateQuery.concat(' WHERE recipe_id = ?');

        values.push(Number(recipe_id));

        try {
            const result = await db.pool.query(sqlUpdateQuery, values);

            res.status(200).send(convertBigIntsToNumbers(result));
        } catch (err) {
            // Error in SQL query is fault of client (send 200 OK)
            res.status(200).send(convertBigIntsToNumbers(err));
        }
    } catch (err) {
        console.log(err);
        res.status(500).send(convertBigIntsToNumbers(err));
    }
});

// DELETE method for /user-defined-recipes
app.delete(USER_DEFINED_RECIPES_ENDPOINT, async (req, res) => {
    try {
        const recipe_id = req.query.recipe_id;
        if (recipe_id == undefined) {
            res.status(400).send('You must provide a recipe_id to delete.\n');
            return;
        }

        const user_id = req.query.user_id;
        if (user_id == undefined) {
            res.status(400).send('You must provide the user_id of the current user in order to delete a recipe.\n');
            return;
        }

        const sqlRecipeExistsQuery = `SELECT COUNT(*) FROM ${USER_DEFINED_RECIPES_TABLE} WHERE recipe_id = ?`;
        const recipeExists = await db.pool.query(sqlRecipeExistsQuery, [recipe_id]);
        if (recipeExists[0]['COUNT(*)'] == 0) {
            res.status(200).send('No recipe exists with the provided id.\n');
            return;
        }

        const sqlUserIsAdminQuery = `SELECT isAdmin FROM ${USERS_TABLE} WHERE user_id = ?`;
        const isAdmin = await db.pool.query(sqlUserIsAdminQuery, [user_id]);

        const sqlUserIdOfRecipeToDeleteQuery = `SELECT user_id FROM ${USER_DEFINED_RECIPES_TABLE} WHERE recipe_id = ?`;
        const userIdOfRecipeToDelete = await db.pool.query(sqlUserIdOfRecipeToDeleteQuery, [recipe_id]);

        if (isAdmin[0]['isAdmin'] == 1 || user_id == userIdOfRecipeToDelete[0]['user_id']) {
            const sqlDeleteRecipeQuery = `DELETE FROM ${USER_DEFINED_RECIPES_TABLE} WHERE recipe_id = ?`;
            const result = await db.pool.query(sqlDeleteRecipeQuery, [recipe_id]);
            
            res.status(200).send(convertBigIntsToNumbers(result));
        } else {
            res.status(401).send('The provided user is not authorized to delete this recipe.\n');
        }
    } catch (err) {
        console.log(err);
        res.status(500).send(convertBigIntsToNumbers(err));
    }
});

// PATCH method for /update-user
app.patch(UPDATE_USER_ENDPOINT, async (req, res) => {
	try {
		const user_id = req.query.user_id;
		if (user_id == undefined) {
			res.status(400).send('You must provide the user_id to update as a URL parameter.\n');
			return;
		}
		
		const sqlUserExistsQuery = `SELECT COUNT(*) FROM ${USERS_TABLE} WHERE user_id = ?`;
		const userExists = (await db.pool.query(sqlUserExistsQuery, [user_id]))[0]['COUNT(*)'];
		if (userExists == 0) {
			res.status(200).send('No user exists with the provided id.\n');
			return;
		}
		
		const updates = req.body;
		
		const keys = [];
		const values = [];
		for (const [key, value] of Object.entries(updates)) {
			if (value !== null && value !== undefined) {
				keys.push(key);
				values.push(value);
			}
		}
		
		let sqlUpdateQuery = `UPDATE ${USERS_TABLE} SET `;
		for (let i = 0; i < keys.length; i++) {
			sqlUpdateQuery = sqlUpdateQuery.concat(`${keys[i]} = ?`);
			if (i !== keys.length - 1) sqlUpdateQuery = sqlUpdateQuery.concat(', ');
		}
		sqlUpdateQuery = sqlUpdateQuery.concat(' WHERE user_id = ?');
		
		values.push(Number(user_id));
		
		try {
            const result = await db.pool.query(sqlUpdateQuery, values);

            res.status(200).send(convertBigIntsToNumbers(result));
        } catch (err) {
            // Error in SQL query is fault of client (send 200 OK)
            res.status(200).send(convertBigIntsToNumbers(err));
        } 
	} catch (err) {
        console.log(err);
        res.status(500).send(convertBigIntsToNumbers(err));
    }

});

// POST method for /create-account
app.post(CREATE_ACCOUNT_ENDPOINT, async (req, res) => {
    try {
        const user = req.body;

        const keys = [];
        const values = [];
        for (const [key, value] of Object.entries(user)) {
            if (value !== null && value !== undefined) {
                keys.push(key);
                values.push(value);
            }
        }

        const placeholders = values.map(() => "?").join(", ");
        
        const sql = `INSERT INTO ${USERS_TABLE} (${keys.join(", ")}) VALUES (${placeholders})`;

        const result = await db.pool.query(sql, values);

        res.status(200).send(convertBigIntsToNumbers(result));
    } catch (err) {
        console.log(err);
        res.status(500).send(convertBigIntsToNumbers(err));
    }
});

//DELETE method for /delete-account
app.delete(DELETE_ACCOUNT_ENDPOINT, async (req, res) => {
    try {
        const userId = req.query.userId;
        if (userId == undefined) {
            res.status(400).send('You must provide the userId.\n');
            return;
        }
        const sql= `DELETE FROM ${USERS_TABLE} WHERE user_id = ${userId}`;
        const result = await db.pool.query(sql);
        res.status(200).send(convertBigIntsToNumbers(result));
    } catch (error) {
        res.status(500).send(convertBigIntsToNumbers(error));
    }
})

// POST method for /login
app.post(LOGIN_ENDPOINT, async (req, res) => {
    try {
        const { email_id, password } = req.body;

        if (email_id && password) {
            console.log(email_id); console.log(password);
            // Replace this with your authentication logic
            const sql = `SELECT * FROM ${USERS_TABLE} WHERE email_id = ? AND password = ?`;

            const result = await db.pool.query(sql, [email_id, password]);

            console.log(result);

            if (result.length > 0) {
                const user = result[0];
                res.status(200).json({ message: 'Login successful', user: user });
            } else {
                res.status(401).json({ message: 'Invalid credentials' });
            }
        } else {
            res.status(400).json({ message: 'Email and password are required' });
        }
    } catch (err) {
        console.log(err);
        res.status(500).send(convertBigIntsToNumbers(err));
    }
});

//Post method for /user-defined-recipes/comments
app.post(COMMENTS_ENDPOINT, async (req, res) => {
    try {
        const { user_id, recipe_id, comment } = req.body

        if(user_id && recipe_id && comment){

            sqlQuery = `INSERT INTO ${COMMENTS_TABLE} (recipe_id, comment, user_id) VALUES (? , ? , ?)`;
            const result = await db.pool.query(sqlQuery, [recipe_id, comment, user_id]);

            console.log(result);
            res.status(200).send(convertBigIntsToNumbers(result));
        }
        else{
            res.status(400).json({message: "All fields are required."});
        }

    }
    catch(error){
        console.log(error);
        res.status(500).send(convertBigIntsToNumbers(error));
    }

})

//GET method for /user-defined-recipes/comments
app.get(COMMENTS_ENDPOINT, async (req, res) => {
    try {
        const recipeId = req.query.recipeId;
        const commentId = req.query.commentId;

        if (commentId != undefined && recipeId == undefined) {
            // check if the comment with the provided commentId exists
            const sqlCommentExistsQuery = `SELECT COUNT(*) FROM ${COMMENTS_TABLE} WHERE comment_id = ?`;
		    const commentExists = (await db.pool.query(sqlCommentExistsQuery, [commentId]))[0]['COUNT(*)'];

            if (commentExists == 0) {
                res.status(200).send('No comment exists with the provided id.\n');
                return;
            }

            sqlQuery = `SELECT * FROM ${COMMENTS_TABLE} WHERE comment_id = ?`;
            const result = await db.pool.query(sqlQuery, [commentId]);
            console.log(result);
            res.status(200).send(convertBigIntsToNumbers(result));
        } else if (recipeId != undefined && commentId == undefined) {
            const sqlRecipeExistsQuery = `SELECT COUNT(*) FROM ${COMMENTS_TABLE} WHERE recipe_id = ?`;
            const recipeExists = (await db.pool.query(sqlRecipeExistsQuery, [recipeId]))[0]['COUNT(*)'];

            if (recipeExists == 0) {
                res.status(200).send('No recipe exists with the provided id.\n');
                return;
            }

            sqlQuery = `SELECT * FROM ${COMMENTS_TABLE} WHERE recipe_id = ?`;
            const result = await db.pool.query(sqlQuery, [recipeId]);
            console.log(result);
            res.status(200).send(convertBigIntsToNumbers(result));
        } else if (recipeId != undefined && commentId != undefined) {
            res.status(400).json({message: "You can query by commentId or recipeId but not both.\n"});
        } else {
            res.status(400).json({message: "You must provide either a commentId or a recipeId.\n"});
        }
    } catch (error) {
        res.status(500).send(convertBigIntsToNumbers(error));
    }
});

//DELETE method for /user-defined-recipes/comments
app.delete(COMMENTS_ENDPOINT, async (req, res) => {
    try {
        const commentId = req.query.commentId;
        if (commentId == undefined) {
            res.status(400).send('You must provide the commentId.\n');
            return;
        }
        const sql= `DELETE FROM ${COMMENTS_TABLE} WHERE comment_id = ${commentId}`;
        const result = await db.pool.query(sql);
        res.status(200).send(convertBigIntsToNumbers(result));
    } catch (error) {
        res.status(500).send(convertBigIntsToNumbers(error));
    }
})

// GET method for /user-followers-following-count"
app.get(USER_FOLLOWERS_FOLLOWING_COUNT_ENDPOINT, async (req, res) => {
    try {
        const user_id = req.query.user_id;
        let condition;
        condition = `user_id = ${user_id}`;

        const sql = `(SELECT 'following_count' AS typeOfFollow, COUNT(*) AS count FROM ${FOLLOWS_TABLE} WHERE ${condition}) UNION ALL (SELECT 'followers_count' AS type, COUNT(*) AS count FROM ${FOLLOWS_TABLE} WHERE follower_id =${user_id});`

        let result = await db.pool.query(sql);
        result = serializeResult(result);
        res.status(200).send(result);
    } catch (err) {
        console.log(err);
        res.status(500).send(err)
    }
});


//Get method for /users-network-list
app.get(USERS_NETWORK_LIST_ENDPOINT, async (req, res) => {
    try {
        const user_id = req.query.user_id;
        const network_type = req.query.network_type;

        let sql;
        let result;
        if (network_type === "following") {
            sql = `SELECT u.* FROM ${USERS_TABLE} AS u JOIN ${FOLLOWS_TABLE} AS f ON u.user_id = f.user_id WHERE f.follower_id = ${user_id};`
            result = await db.pool.query(sql);
            result = serializeResult(result);
            res.status(200).send(convertBigIntsToNumbers(result));
        }
        else if (network_type === "followers") {
            sql = `SELECT users.* FROM ${USERS_TABLE} JOIN ${FOLLOWS_TABLE} ON users.user_id = follows.follower_id WHERE follows.user_id = ${user_id};`
            result = await db.pool.query(sql);
            result = serializeResult(result);
            res.status(200).send(convertBigIntsToNumbers(result));

        } else {
            throw new Error("Did not specify Type of Network list correctly");
        }

    } catch (err) {
        console.log(err);
        res.status(500).send(convertBigIntsToNumbers(err));
    }
});

//WORK IN PROGRESS, NOT DONE YET, SO MIGHT NOT WORK IF YOU TEST IT.
//POST to upload image
console.log('URL ', UPLOAD_RECIPE_IMAGE_ENDPOINT)
app.post(UPLOAD_RECIPE_IMAGE_ENDPOINT, upload.single('image'), (req, res) => {
    console.log(req.file)
    console.log(req.body);


    if(req.file !== undefined){
        const fileName = req.file.originalname;
        const imagePath = path.join(__dirname, 'recipe_images', fileName);
        fs.writeFile(imagePath, req.file.buffer, err => {
            if (err) {
                console.log(err)
            }

            res.status(200).json({response_code : 200, response_body : 'Ok'});
        })
    }else{
        res.status(500).json({response_code : 500, response_body : 'Something went wrong'});
    }

    /*
    const imagePath = path.join(__dirname, 'recipe_images', image_uri);
    console.log(`Image path is ${imagePath}`)
    fs.writeFile(imagePath, req.body, err => {
        if (err) {
          console.error(err);
        }
    });
    */
})

// /user-defined-recipes/download_image/:recipe_id
//GET to get recipe image
app.get(DOWNLOAD_RECIPE_IMAGE_ENDPOINT, (req, res) => {
    const recipe_id = req.params.recipe_id;
    console.log('Image requested ' , req.params.recipe_id)


    const defaultImagePath = path.join(__dirname, 'recipe_images', 'food.jpg');
    const imagePath = path.join(__dirname, 'recipe_images', recipe_id.concat('.jpg'));
    
    fs.stat(imagePath, function(err, stat) {
        if (err == null) {
          console.log('File exists');
          console.log(imagePath);
          res.status(200).sendFile(imagePath);
          
        } else if (err.code === 'ENOENT') {
          // file does not exist
          console.log('File does not exist');
          res.status(200).sendFile(defaultImagePath);
          
        } else {
          console.log('Some other error: ', err.code);
          res.send(500);
        }
      });
})



// GET method for /user-search to search the user, given a username"
app.get(USER_SEARCH_ENDPOINT, async (req, res) => {
    try {

        const searchText = req.query.text;
        console.log("user searched " + searchText);
        
        console.log(" ---- within user_search endpoint");
        
        if(searchText.trim() === '') {
            res.status(400).send({message: 'Search text cannot be empty'});
            return;
        }
        
        const sql = `SELECT * FROM ${USERS_TABLE} WHERE username LIKE ? AND username != '';`
        const values = [`%${searchText}%`];
        
        // parameterized query prevents sql injection attacks -> ? gets replaced with values
        let result = await db.pool.query(sql, values);
        result = serializeResult(result);

        res.status(200).send(convertBigIntsToNumbers(result));
    } catch (err) {
        console.log(err);
        res.status(500).send(err)
    }
});

//Get method to check if a user if following the visitin user
app.get(USER_IS_FOLLOWING_ENDPOINT, async (req, res) => {
    try {
        const user_id = req.query.user_id;
        const visitor_id = req.query.visitor_id;

        let condition;
        condition = `user_id = ${user_id}`;

        const sql = `SELECT COUNT(*) FROM ${FOLLOWS_TABLE} WHERE user_id = ${visitor_id} AND follower_id = ${user_id};`

        let result = await db.pool.query(sql);
        result = serializeResult(result);
        res.status(200).send(result);
    } catch (err) {
        console.log(err);
        res.status(500).send(err)
    }
});

//Post method that makes a user follow the visiting user
app.post(USER_FOLLOW_ENDPOINT, async (req, res) => {

    try{


    let user_id = req.body.user_id;
    let visitor_id = req.body.visitor_id;

    
    let sqlQuery = 'INSERT INTO follows (user_id, follower_id) VALUES (?, ?)';
    
    const result = await db.pool.query(sqlQuery, [visitor_id, user_id]);

    console.log(result);
    res.status(200).send(convertBigIntsToNumbers(result));

    }catch(error){
        console.log(error);
        res.status(500).send(convertBigIntsToNumbers(error));
    }
});

//Delete method that makes a user unfollow the visiting user
app.delete(USER_UNFOLLOW_ENDPOINT, async (req, res) => {

    try{
    
    let user_id = req.body.user_id;
    let visitor_id = req.body.visitor_id;

    let query = 'DELETE FROM follows WHERE user_id = ? AND follower_id = ?';

    const result = await db.pool.query(query, [visitor_id, user_id]);
    res.status(200).send(convertBigIntsToNumbers(result));

    }catch(error){
        console.log(err);
        res.status(500).send(convertBigIntsToNumbers(err));
    }
});


//GET method for /user-dfined-recipes/ pagination
app.get(USER_DEFINED_RECIPES_ENDPOINT.concat('/:pageNumber'), async (req, res) => {
    try {
        const pageNumber = req.params.pageNumber;
        const recipesPerPage = 10;
        const offset = (pageNumber - 1) * recipesPerPage;
        //await new Promise(resolve => setTimeout(resolve, 1000));
          console.log("Delay ended");

        const sql = `SELECT R.* FROM ${USER_DEFINED_RECIPES_TABLE} AS R JOIN ${USERS_TABLE} as U ON R.user_id = U.user_id WHERE U.isBanned = 0 ORDER BY R.recipe_id LIMIT ${recipesPerPage} OFFSET ${offset}`;
        //const sql = `SELECT * FROM ${USER_DEFINED_RECIPES_TABLE} ORDER BY recipe_id LIMIT ${recipesPerPage} OFFSET ${offset}`;
        const result = await db.pool.query(sql);
        res.status(200).send(convertBigIntsToNumbers(result));
    } catch (err) {
        console.log(err);
        res.status(500).send(convertBigIntsToNumbers(err))
    }
})

//GET method to get user given their userid /user/:userId
app.get(USERS_ENDPOINT.concat('/:userId'), async (req, res) => {
    try {
        const userId = req.params.userId;
        console.log("USER ID IS " + userId);
        const sql = `SELECT * FROM ${USERS_TABLE} WHERE user_id = ${userId}`;
        const result = await db.pool.query(sql);
        res.status(200).send(convertBigIntsToNumbers(result[0]));

    } catch (error) {
        console.log(error);
        res.status(500).send(convertBigIntsToNumbers(error));
        
    }
})

//GET method to search recipe /search-recipe?search=

app.get(RECIPE_SEARCH, async (req, res) => {
    try {
        const searchQuery = req.query.search;
        const searchResultsLimit = 10;
        console.log("SEARCH IS " + searchQuery)

        const sql = `SELECT * FROM ${USER_DEFINED_RECIPES_TABLE} WHERE recipe_name LIKE '%${searchQuery}%' OR ingredients LIKE '%${searchQuery}%' OR description LIKE '%${searchQuery}%' OR instructions LIKE '%${searchQuery}%' LIMIT ${searchResultsLimit}`;
        const result = await db.pool.query(sql)
        res.status(200).send(convertBigIntsToNumbers(result));
    } catch (error) {
        console.log(error);
        res.status(500).send(convertBigIntsToNumbers(error));
    }
})

// get method that gets a users notifications
app.get(USER_NOTIFICATION_ENDPOINT, async (req, res) => {
    try {
        const to_user_id = req.query.to_user_id;

        let sql;
        let result;
        
        sql = `SELECT ${NOTIFICATIONS_TABLE}.id, ${NOTIFICATIONS_TABLE}.type, ${USERS_TABLE}.username, ${NOTIFICATIONS_TABLE}.from_user_id, ${NOTIFICATIONS_TABLE}.to_user_id, ${NOTIFICATIONS_TABLE}.post_id, ${NOTIFICATIONS_TABLE}.created_at FROM ${NOTIFICATIONS_TABLE}, ${USERS_TABLE} Where ${NOTIFICATIONS_TABLE}.to_user_id=${to_user_id} and ${NOTIFICATIONS_TABLE}.from_user_id=${USERS_TABLE}.user_id;`
        result = await db.pool.query(sql);
        result = serializeResult(result);
        res.status(200).send(convertBigIntsToNumbers(result));
        

    } catch (err) {
        console.log(err);
        res.status(500).send(convertBigIntsToNumbers(err));
    }
})

//delete an entry from notification that was for to_user_id
app.delete(USER_NOTIFICATION_ENDPOINT, async (req, res) => {

    try{
    
    const type = req.body.type;
    const to_user_id = req.body.to_user_id;
    const from_user_id = req.body.from_user_id;
    const post_id = req.body.post_id;

    let sql;
    let result;

    if(type === "follow"){
        sql = `DELETE FROM ${NOTIFICATIONS_TABLE} WHERE type = 'follow' AND from_user_id = ? AND to_user_id = ? ;`
        result = await db.pool.query(sql, [from_user_id, to_user_id]);
        res.status(200).send(convertBigIntsToNumbers(result));
    }
    else if(type === "like"){
        sql = `DELETE FROM ${NOTIFICATIONS_TABLE} WHERE type = 'like' AND from_user_id = ? AND to_user_id = ? AND post_id = ? ;`
        result = await db.pool.query(sql, [from_user_id, to_user_id, post_id]);
        res.status(200).send(convertBigIntsToNumbers(result));
    }
    else {
        throw new Error("Error on USER_NOTIFICATION_ENDPOINT");
    }

    }catch(error){
        console.log(error);
        res.status(500).send(convertBigIntsToNumbers(error));
    }
});

//Post method that gives a notification to to_user_id
app.post(USER_NOTIFICATION_ENDPOINT, async (req, res) => {

    try{

    const type = req.body.type;
    const to_user_id = req.body.to_user_id;
    const from_user_id = req.body.from_user_id;
    const post_id = req.body.post_id;
    console.log("type is : " + type);
    let sql;
    let result;

    if(type === "follow"){
        sql = `INSERT INTO ${NOTIFICATIONS_TABLE} (type, from_user_id, to_user_id, post_id) VALUES (?, ?, ?, ?);`
        result = await db.pool.query(sql, [type, from_user_id, to_user_id, post_id === 'null' ? null : post_id]);
        res.status(200).send(convertBigIntsToNumbers(result));
    } 
    else if(type === "like"){
        sql = `INSERT INTO ${NOTIFICATIONS_TABLE} (type, from_user_id, to_user_id, post_id) VALUES (?, ?, ?, ?);`
        result = await db.pool.query(sql, [type, from_user_id, to_user_id, post_id === 'null' ? null : post_id]);
        res.status(200).send(convertBigIntsToNumbers(result));
    }
    else {
        throw new Error("Error on USER_NOTIFICATION_ENDPOINT");
    }


    }catch(error){
        console.log(error);
        res.status(500).send(convertBigIntsToNumbers(error));
    }
});



function bigIntToString(value) {
    const MAX_SAFE_INTEGER = 2 ** 53 - 1;
    return value <= MAX_SAFE_INTEGER ? Number(value) : value.toString();
}

function serializeResult(result) {
    return result.map(row => {
        const newRow = { ...row };
        for (const key in newRow) {
            if (typeof newRow[key] === 'bigint') {
                newRow[key] = bigIntToString(newRow[key]);
            }
        }
        return newRow;
    });
}


function convertBigIntsToNumbers(obj) {
    if (typeof obj === 'bigint') {
        return Number(obj);
    } else if (typeof obj === 'object') {
        for (const key in obj) {
            if (Object.prototype.hasOwnProperty.call(obj, key)) {
                const value = obj[key];
                if (typeof value === 'bigint') {
                    obj[key] = Number(value);
                } else if (typeof value === 'object') {
                    obj[key] = convertBigIntsToNumbers(value);
                }
            }
        }
    }
    return obj;
}

app.listen(port, () => console.log(`Listening on port ${port}`));
