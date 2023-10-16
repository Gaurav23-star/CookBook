const express = require('express')
const router = express.Router()
const db = require('./db')

//endpoints
const LOGIN = '/login2';
const SIGNUP = '/signup';

//define tables
const USERS_TABLE = 'users';


//route to accept login request
router.get(LOGIN, async (req, res) => {

    //query parameters
    const email = req.query.email;
    const password = req.query.password;

    if(email && password){
        console.log("LOGIN Requested. email = " + email + " password = " + password);
        const result = await db.pool.query('SELECT user_id, first_name, last_name FROM ' + USERS_TABLE + ' WHERE email_id = ? AND password = ?', [email, password])
        
        if(result.length > 0){
            res.status(200).json(result[0])
        }else{
            res.status(401).send('wrong password or email.')
        }

    }else{
        res.sendStatus(400);
    }
})


router.post(SIGNUP, async (req, res) => {

    const first_name = req.body.first_name;
    const last_name = req.body.last_name;
    const email_id = req.body.email_id;
    const password = req.body.password;

    if(first_name && last_name && email_id && password){
        try {
            //insert new user into the db
            const query = 'INSERT INTO ' + USERS_TABLE + ' (first_name, last_name, email_id, password) VALUES (? , ? , ? , ?)';
            const result = await db.pool.query(query, [first_name, last_name, email_id, password]);   
            
            //get user id of newly created user
            const getIdQuery = 'SELECT user_id, first_name, last_name, email_id FROM ' + USERS_TABLE + ' WHERE email_id = ?';
            const userId = await db.pool.query(getIdQuery, [email_id]);

            //return newly created user
            res.status(201).json(userId[0]);


        } catch (error) {
            res.status(406).send('user already exists with email.');
        }
    }
    else{
        res.status(400);
    }

})

module.exports = router