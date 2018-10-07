"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const GameRunner_1 = require("./GameRunner");
const Netcom_1 = require("./Netcom");
class Main {
    run(divIdName, auth) {
        console.log("Running game");
        var game = new GameRunner_1.GameRunner();
        game.run(divIdName);
        this.netcom = new Netcom_1.Netcom(auth, new ZombieMovedProcessor());
        //this.netcom.init(game);
    }
    disconnect() {
        this.netcom.disconnect();
    }
}
exports.Main = Main;
