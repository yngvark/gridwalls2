import { GameRunner } from "./GameRunner"
import {Authentication} from "./Authentication";
import {ZombieMovedProcessor} from "./ZombieMovedProcessor";
import {ZombieMoved} from "./ZombieMoved";
// import 'stomp';

export class Netcom {
    private readonly auth:Authentication;
    private zombieMovedProcessor:ZombieMovedProcessor;
	private client:any;
			
    constructor(auth:Authentication, zombieMovedProcessor:ZombieMovedProcessor) {
		this.auth = auth
        this.zombieMovedProcessor = zombieMovedProcessor;
    }

    init(game:GameRunner):void {
		var websocketUrl = 'ws://' + this.auth.host + ':15674/ws';
		var ws = new WebSocket(websocketUrl);
		this.client = Stomp.over(ws);

		this.client.debug = () => {};

		var _that = this;
	    var on_connect = function() {
	        console.log('Connected');
			var subscription = _that.client.subscribe("/exchange/Zombie", function(msg:any) {

				//console.log("<<< RECEIVED:")
				//console.log(msg);
                let zombieMoved:ZombieMoved = _that.zombieMovedProcessor.process(JSON.parse(msg.body));
                game.zombieMoved(zombieMoved);
			});
			//console.log(".-subscription");
			//console.log(subscription);
	    };

	    var on_error = function(error) {
	        console.error('error:');
	        console.error(error);
	    };

		//client.connect('guest', 'guest', on_connect, on_error, '/');
		console.log("Connecting with auth:");
		console.log(this.auth)
		this.client.connect(this.auth.username, this.auth.password, on_connect, on_error, '/');
		
    }

	disconnect():void {
		this.client.disconnect(function() {
			console.log("Disconnected! Cya.");
		});
	}
}
		