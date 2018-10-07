export class MainScene extends Phaser.Scene {
    private phaserSprite: Phaser.GameObjects.Sprite;

    constructor() {
        super({
            key: "MainScene"
        });
    }

    preload(): void {
        // this.load.image("logo", "./assets/boilerplate/phaser.png");
        this.load.image("skeleton", "img/skeleton.png");
        this.load.image("human", "img/human.png");
    }

    create(): void {
        // this.phaserSprite = this.add.sprite(400, 300, "logo");
        this.phaserSprite = this.add.sprite(100, 100, "human");
    }
}