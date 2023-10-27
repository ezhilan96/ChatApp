const express = require('express'); //requires express module
const socket = require('socket.io'); //requires socket.io module
const fs = require('fs');//requires fs module
const app = express();
var PORT = process.env.PORT || 3000;
const server = app.listen(PORT); //hosts server on localhost:3000

app.use(express.static('public'));
console.log('Server is running...');
const io = socket(server);

var chatHistory = [];

io.on('connection', (socket) => {
    socket.on('chat', (data) => {
        const messageData = JSON.parse(data);
        chatHistory.push(messageData);
        io.emit('chat', JSON.stringify(chatHistory));
    })
})