"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class Netcom {
    constructor(auth, zombieMovedProcessor) {
        this.auth = auth;
        this.zombieMovedProcessor = zombieMovedProcessor;
    }
    init(game) {
        //var ws = new WebSocket('ws://127.0.0.1:15674/ws');
        var websocketUrl = 'ws://' + this.auth.host + ':15674/ws';
        console.log("Websocket url: " + websocketUrl);
        var ws = new WebSocket(websocketUrl);
        this.client = Stomp.over(ws);
        //var client = Stomp.client(ws);
        //var client = this.client;
        var _that = this;
        var on_connect = function () {
            console.log('connected');
            console.log(".-SUBSCRIBE");
            var subscription = _that.client.subscribe("/exchange/Zombie", function (msg) {
                console.log("<<< RECEIVED:");
                console.log(msg);
                let zombieMoved = _that.zombieMovedProcessor.process(JSON.parse(msg.body));
                game.zombieMoved(zombieMoved);
            });
            console.log(".-subscription");
            console.log(subscription);
        };
        var on_error = function (error) {
            console.log('error:');
            console.log(error);
        };
        //client.connect('guest', 'guest', on_connect, on_error, '/');
        console.log("Connecting with auth:");
        console.log(this.auth);
        this.client.connect(this.auth.username, this.auth.password, on_connect, on_error, '/');
    }
    disconnect() {
        this.client.disconnect(function () {
            console.log("Disconnected! Cya.");
        });
    }
}
exports.Netcom = Netcom;
