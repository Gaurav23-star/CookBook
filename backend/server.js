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

// Define tables
const USER_DEFINED_RECIPES_TABLE = 'user_defined_recipes';
 
// GET method for user-defined-recipes
app.get(USER_DEFINED_RECIPES_ENDPOINT, async (req, res) => {
    try {
        const result = await db.pool.query("select * from " + USER_DEFINED_RECIPES_TABLE);
        res.send(convertBigIntsToNumbers(result));
    } catch (err) {
        res.send(convertBigIntsToNumbers(err))
        console.log(err);
    }
});
 
// POST method for user-defined-recipes
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

        res.send(convertBigIntsToNumbers(result));
    } catch (err) {
        res.send(convertBigIntsToNumbers(err));
        console.log(err);
    }
});

app.put(USER_DEFINED_RECIPES_ENDPOINT, async (req, res) => {
    let task = req.body;
    try {
        const result = await db.pool.query("update tasks set description = ?, completed = ? where id = ?", [task.description, task.completed, task.id]);
        res.send(result);
    } catch (err) {
        res.send(err);
        throw err;
    } 
});
 
app.delete(USER_DEFINED_RECIPES_ENDPOINT, async (req, res) => {
    let id = req.query.id;
    try {
        const result = await db.pool.query("delete from tasks where id = ?", [id]);
        res.send(result);
    } catch (err) {
        throw err;
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
