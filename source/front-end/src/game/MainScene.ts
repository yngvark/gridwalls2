export class MainScene extends Phaser.Scene {
    private humanSprite: Phaser.GameObjects.Sprite;
    private cursors: any;

    constructor() {
        super({
            key: "MainScene"
        });
    }

    preload(): void {
        this.load.image("skeleton", "img/skeleton.png");
        this.load.image("human", "img/human.png");
    }


    private x:integer = 100;
    private y:integer = 100;

    create(): void {
        this.humanSprite = this.add.sprite(this.x, this.y, "human");
        this.humanSprite.setScale(0.2 , 0.2);
        this.cursors = this.input.keyboard.createCursorKeys();
    }

    update(): void {
        // https://phaser.io/tutorials/making-your-first-phaser-3-game/part7

        if (this.cursors.left.isDown)
        {
            this.x -= 15;
        }
        else if (this.cursors.right.isDown)
        {
            this.x += 15;
        }
        else if (this.cursors.up.isDown)
        {
            this.y -= 15;
        }
        else if (this.cursors.down.isDown)
        {
            this.y += 15
        }

        this.humanSprite.setPosition(this.x, this.y);
    }
}