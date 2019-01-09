import 'phaser';
import {GameRunner} from "./GameRunner";
import {Netcom} from "./Netcom";
import {Authentication} from "./Authentication";
import {ZombieMovedProcessor} from "./ZombieMovedProcessor";

export class Main {
    private netcom:Netcom;

    constructor() {
        console.log("Main version 55555555")
    }

    run(auth:Authentication):void {
        console.log("Running game")

        var game = new GameRunner();
        game.run();

        this.netcom = new Netcom(auth, new ZombieMovedProcessor());
        this.netcom.init(game);
    }

    disconnect():void {
        console.log("main disconnect!!!!!!!!!");
        this.netcom.disconnect();
    }
}
