class Netcom {
    private zombieMovedProcessor:ZombieMovedProcessor;
	private client:any;
			
    constructor(auth:Authentication, zombieMovedProcessor:ZombieMovedProcessor) {
        this.zombieMovedProcessor = zombieMovedProcessor;
    }

    init(connectionString:any, game:GameRunner):void {
		console.log("connectionString:");
		console.log(connectionString);
		//var ws = new WebSocket('ws://127.0.0.1:15674/ws');
		var ws = new WebSocket('ws://' + connectionString.server + ':15674/ws');
		//var client = Stomp.client(ws);
	    this.client = Stomp.over(ws); 
		var client = this.client;

		var _that = this;
	    var on_connect = function() {
	        console.log('connected');

			console.log(".-SUBSCRIBE");
			var subscription = client.subscribe("/exchange/Zombie", function(msg:any) {
				console.log("<<< RECEIVED:")
				console.log(msg);
                let zombieMoved:ZombieMoved = _that.zombieMovedProcessor.process(msg.body);
                game.zombieMoved(zombieMoved);
			});
			console.log(".-subscription");
			console.log(subscription);
			
	    };

	    var on_error = function(error) {
	        console.log('error:');
	        console.log(error);
	    };

		//client.connect('guest', 'guest', on_connect, on_error, '/');
		client.connect(connectionString.username, connectionString.password, on_connect, on_error, '/');
		
    }

	disconnect():void {
		this.client.disconnect(function() {
			console.log("Disconnected! Cya.");
		});
	}
}
		