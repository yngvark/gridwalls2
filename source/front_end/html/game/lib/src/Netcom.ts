class Netcom {
    private zombieMovedProcessor:ZombieMovedProcessor;
	private client:any;
			
    constructor(zombieMovedProcessor:ZombieMovedProcessor) {
        this.zombieMovedProcessor = zombieMovedProcessor;

		console.log("inited with: ");
		console.log(zombieMovedProcessor);
		console.log(this.zombieMovedProcessor);
    }

    init(game:GameRunner):void {
        var ws = new WebSocket('ws://127.0.0.1:15674/ws');
		//var client = Stomp.client(ws);
	    this.client = Stomp.over(ws);
		var client = this.client;

		var _that = this;
	    var on_connect = function() {
	        console.log('connected');

			console.log(".-SUBSCRIBE");
			var subscription = client.subscribe("/exchange/ZombieMoved", function(msg:any) {
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

	    client.connect('guest', 'guest', on_connect, on_error, '/');
    }

	disconnect():void {
		this.client.disconnect(function() {
			console.log("Disconnected! Cya.");
		});
	}
}
		