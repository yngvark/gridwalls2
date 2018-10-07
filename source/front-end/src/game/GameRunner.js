"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MainScene_1 = require("./MainScene");
class GameRunner {
    constructor() {
        //
        // private preload():void {
        //     this.game.load.image("skeleton", "img/skeleton.png");
        //     this.game.load.image("human", "img/human.png");
        //
        //     console.log("Starting game!!");
        //     this.game.add.sprite(1, 1, "human");
        //     document.game = this.game;
        // }
        //
        // private create():void {
        // }
        this.zombies = {};
    }
    run(divIdName) {
        console.log("Starting game!");
        this.scene = new MainScene_1.MainScene();
        this.game = new Phaser.Game({
            width: 800,
            height: 350,
            type: Phaser.AUTO,
            scene: this.scene
        });
    }
    zombieMoved(zombieMoved) {
        if (this.zombies.hasOwnProperty(zombieMoved.id)) {
            let zombie = this.zombies[zombieMoved.id];
            console.log("Existing zombie:");
            console.log(zombie);
            console.log(zombieMoved);
            zombie.sprite.setPosition(zombieMoved.coordinate.x * 15, zombieMoved.coordinate.y * 15);
        }
        else {
            let sprite = this.scene.add.sprite(zombieMoved.coordinate.x, zombieMoved.coordinate.y, "skeleton");
            sprite.setScale(0.2, 0.2);
            let zombie = {
                id: zombieMoved.id,
                sprite: sprite
            };
            console.log("New zombie:");
            console.log(zombie);
            console.log(zombieMoved);
            this.zombies[zombie.id] = zombie;
        }
    }
}
exports.GameRunner = GameRunner;
class Zombie {
}
class Map {
    constructor() {
        this.items = {};
    }
    add(key, value) {
        this.items[key] = value;
    }
    has(key) {
        return key in this.items;
    }
    get(key) {
        return this.items[key];
    }
}
