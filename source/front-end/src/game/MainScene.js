"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class MainScene extends Phaser.Scene {
    constructor() {
        super({
            key: "MainScene"
        });
    }
    preload() {
        // this.load.image("logo", "./assets/boilerplate/phaser.png");
        this.load.image("skeleton", "img/skeleton.png");
        this.load.image("human", "img/human.png");
    }
    create() {
        // this.phaserSprite = this.add.sprite(400, 300, "logo");
        this.phaserSprite = this.add.sprite(100, 100, "human");
    }
}
exports.MainScene = MainScene;
