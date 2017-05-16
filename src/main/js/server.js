/*
Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

Licensed under the Amazon Software License (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

    http://aws.amazon.com/asl/

or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
var faker = require('faker');
var moment = require('moment');
var request = require('superagent');

var express = require('express');
var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var port = process.env.PORT || 3000;

app.use(express.static('public'));
app.get('/health', function(request, response) {
    response.send('ok');
});

io.on('connection', function(socket) {
	// Filter socket.io client from Spring
	if (socket.handshake.headers['user-agent'].includes('Java')) {
        socket.on('messages', function(message) {
			io.emit('messages', JSON.parse(message));
        });
		
        socket.on('member_add', function(member) {
			io.emit('member_add', JSON.parse(member));
        });
		return;
	}
	
	var get_members = request('GET','http://localhost:8080/members')
		.then(function(res) {
	    	var members = {};
	    	for (var index in res.body) {
	      	  var member = res.body[index]
	      	  members[member.socket] = member;
	    	}
	    	return members;
	  	})
	  	.catch(function (error) {
	    	console.log(error);
		});

	var initialize_member = get_members
	    .then(function(members) {
	      	if (members[socket.id]) {
	        	return members[socket.id];
	      	}

	      	var username = faker.fake("{{name.firstName}} {{name.lastName}}");
	      	var member = {
	        	socket: socket.id,
	        	username: username,
	        	avatar: "//api.adorable.io/avatars/30/" + username + '.png'
	      	};

	      	request.post('http://localhost:8080/add_member')
	             	.set('Content-Type','application/json')
	             	.send(member)
	             	.end();

	      	return member;
	    });

    // get the highest ranking messages (most recent) up to channel_history_max size
	var get_messages = request('GET','http://localhost:8080/messages')
		.then(function(res) {
		    return res.body;
		})
		.catch(function (error) {
		    console.log(error);
		});

    Promise.all([get_members, initialize_member, get_messages]).then(function(values) {
        var members = values[0];
        var member = values[1];
        var messages = values[2];

        io.emit('member_history', members);
        io.emit('message_history', messages);

        socket.on('send', function(message_text) {
            var date = moment.now();
            var message = {
                epoch: date,
                username: member['username'],
                avatar: member['avatar'],
                message: message_text
            };

			request.post('http://localhost:8080/add_message')
			           .set('Content-Type','application/json')
			           .send(message)
			           .end();
        });
		
        socket.on('disconnect', function() {
			request.post('http://localhost:8080/delete_member')
			           .set('Content-Type','application/json')
			           .send(socket.id)
			           .end();
					   
            io.emit('member_delete', socket.id);
        });
    }).catch(function(reason) {
        console.log('ERROR: ' + reason);
    });
});

http.listen(port, function() {
    console.log('Started server on port ' + port);
});
