// Use the MariaDB Node.js Connector
var mariadb = require('mariadb');

//VM IP: 172.16.122.20
// Create a connection pool
var pool = 
  mariadb.createPool({
    host: "127.0.0.1", 
    //host: "172.16.122.20",
    port: 3306,
    user: "shared_user", 
    password: "shared_password",
    database: "CookBookDB"
  });
 
// Expose a method to establish connection with MariaDB SkySQL
module.exports = Object.freeze({
  pool: pool
});
