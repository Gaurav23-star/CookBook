const express = require('express')
const db = require('./db')
const app = express()
const port = 8080
const bodyParser = require("body-parser");

 
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

const authRouter = require('./authenticate')
app.use('/', authRouter)

// Define endpoint constants
const USER_DEFINED_RECIPES_ENDPOINT = '/user-defined-recipes';
const CREATE_ACCOUNT_ENDPOINT = '/create-account';
const LOGIN_ENDPOINT = '/login';

// Define table constants
const USER_DEFINED_RECIPES_TABLE = 'user_defined_recipes';
const USERS_TABLE = 'users';
 
// GET method for /user-defined-recipes
app.get(USER_DEFINED_RECIPES_ENDPOINT, async (req, res) => {
    try {
        const user_id = req.query.user_id;
        let condition;
        if (user_id == undefined) condition = "TRUE";
        else condition = `user_id = ${user_id}`;

        const sql = `SELECT * FROM ${USER_DEFINED_RECIPES_TABLE} WHERE ${condition}`
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

function convertBigIntsToNumbers(obj) {
    for (const key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
            const value = obj[key];
            if (typeof value === 'bigint') {
                obj[key] = Number(value);
            }
        }
    }
    return obj;
}

app.listen(port, () => console.log(`Listening on port ${port}`));
