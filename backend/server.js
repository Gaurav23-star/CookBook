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
 
// GET
app.get(USER_DEFINED_RECIPES_ENDPOINT, async (req, res) => {
    try {
        const result = await db.pool.query("select * from " + USER_DEFINED_RECIPES_TABLE);
        res.send(result);
    } catch (err) {
        throw err;
    }
});
 
// POST
app.post(USER_DEFINED_RECIPES_ENDPOINT, async (req, res) => {
    let userDefinedRecipe = req.body;
    try {
        const result = await db.pool.query("insert into " + USER_DEFINED_RECIPES_TABLE + "(description) values (?)", [userDefinedRecipe.description]);
        res.send(result);
    } catch (err) {
        throw err;
    }
});
 
app.put(USER_DEFINED_RECIPES_ENDPOINT, async (req, res) => {
    let task = req.body;
    try {
        const result = await db.pool.query("update tasks set description = ?, completed = ? where id = ?", [task.description, task.completed, task.id]);
        res.send(result);
    } catch (err) {
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
 
app.listen(port, () => console.log(`Listening on port ${port}`));
