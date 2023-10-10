Authenication Api Endpoint:

    LOGIN:
        METHOD: GET
        ENDPOINT: <host>:<port>/login?email_id=<email of user>&password=<password entered by user>
        RESPONSE: 
            ON SUCCUSSFULLY AUTHENTICATION:
                http status code: 200
                json payload containing user information: e.g 
                {
                    user_id: 1,
                    first_name: 'test',
                    last_name: 'test',
                    email_id: 'test@gmail.com'
                }

            IF WRONG USER EMAIL OR PASSWORD
                http staus code: 401
                message: wrong password or email

            IF ANY QUERY PARAMETER MISSING (e.g either no email or password provided)
                http status code: 400
                message: bad request


    SIGNUP
        REQUEST:
            METHOD: POST
            ENDPOINT: <HOST>:<PORT>/signup
            REQUEST BODY TYPE Application/json,
                e.g 
                {
                    first_name: 'test',
                    last_name: 'test',
                    email_id: 'test@gmail.com',
                    password: '1211212'
                }

        
        RESPONSE:
            ON SUCCESSFULLY USER CREATED:
                http status code: 201
                json payload containing newly created user infromation,
                sample response:
                {
                    user_id : 1,
                    first_name: 'test',
                    last_name: 'test',
                    email_id: 'test@gmail.com'
                }

            IF USER ALREADY EXISTS WITH GIVEN EMAIL_ID:
                http status code: 406
                message: user already exists with email.


            IF WRONG PAYLOAD SENT FOR USER CREATION (missing fields such as email_id, or passowrd, or wrong formate in general)
                http status code: 400
                message: bad request