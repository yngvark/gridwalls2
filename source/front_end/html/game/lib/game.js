var Coordinate = (function () {
    function Coordinate(x, y) {
        this.x = x;
        this.y = y;
    }
    return Coordinate;
}());
var GameRunner = (function () {
    function GameRunner() {
        this.zombies = {};
    }
    GameRunner.prototype.run = function () {
        this.game = new Phaser.Game(800, 600, Phaser.AUTO, 'content', {
            preload: this.preload,
            create: this.create
        });
    };
    GameRunner.prototype.preload = function () {
        this.game.load.image("skeleton", "img/skeleton.png");
    };
    GameRunner.prototype.create = function () {
    };
    GameRunner.prototype.zombieMoved = function (zombieMoved) {
        if (this.zombies.hasOwnProperty(zombieMoved.id)) {
            var zombie = this.zombies[zombieMoved.id];
            zombie.sprite.position.setTo(zombieMoved.coordinate.x * 40, zombieMoved.coordinate.y * 40);
        }
        else {
            var sprite = this.game.add.sprite(zombieMoved.coordinate.x, zombieMoved.coordinate.y, "skeleton");
            sprite.scale.setTo(0.2, 0.2);
            var zombie = {
                id: zombieMoved.id,
                sprite: sprite
            };
            this.zombies[zombie.id] = zombie;
        }
    };
    return GameRunner;
}());
var Zombie = (function () {
    function Zombie() {
    }
    return Zombie;
}());
var Map = (function () {
    function Map() {
        this.items = {};
    }
    Map.prototype.add = function (key, value) {
        this.items[key] = value;
    };
    Map.prototype.has = function (key) {
        return key in this.items;
    };
    Map.prototype.get = function (key) {
        return this.items[key];
    };
    return Map;
}());
var Main = (function () {
    function Main() {
    }
    Main.prototype.run = function () {
        var game = new GameRunner();
        game.run();
        console.log("loading game");
        this.netcom = new Netcom(new ZombieMovedProcessor());
        this.netcom.init(game);
    };
    Main.prototype.disconnect = function () {
        this.netcom.disconnect();
    };
    return Main;
}());
var Netcom = (function () {
    function Netcom(zombieMovedProcessor) {
        this.zombieMovedProcessor = zombieMovedProcessor;
        console.log("inited with: ");
        console.log(zombieMovedProcessor);
        console.log(this.zombieMovedProcessor);
    }
    Netcom.prototype.init = function (game) {
        var ws = new WebSocket('ws://127.0.0.1:15674/ws');
        //var client = Stomp.client(ws);
        this.client = Stomp.over(ws);
        var client = this.client;
        var _that = this;
        var on_connect = function () {
            console.log('connected');
            console.log(".-SUBSCRIBE");
            var subscription = client.subscribe("/exchange/ZombieMoved", function (msg) {
                console.log(msg);
                var zombieMoved = _that.zombieMovedProcessor.process(msg.body);
                game.zombieMoved(zombieMoved);
            });
            console.log(".-subscription");
            console.log(subscription);
        };
        var on_error = function (error) {
            console.log('error:');
            console.log(error);
        };
        client.connect('guest', 'guest', on_connect, on_error, '/');
    };
    Netcom.prototype.disconnect = function () {
        this.client.disconnect(function () {
            console.log("Disconnected! Cya.");
        });
    };
    return Netcom;
}());
var ZombieMoved = (function () {
    function ZombieMoved(id, coordinate) {
        this.id = id;
        this.coordinate = coordinate;
    }
    return ZombieMoved;
}());
var ZombieMovedProcessor = (function () {
    function ZombieMovedProcessor() {
        this.regex = /([a-z]+)=([a-zA-Z0-9,-]+)/g;
    }
    ZombieMovedProcessor.prototype.process = function (msg) {
        var matches = this.regex.exec(msg);
        var move = {};
        while (matches != null) {
            move[matches[1]] = matches[2];
            matches = this.regex.exec(msg);
        }
        console.log(move);
        var id = move.id;
        var coords = move.tc.split(",");
        var coordinate = new Coordinate(coords[0], coords[1]);
        return new ZombieMoved(id, coordinate);
    };
    return ZombieMovedProcessor;
}());
