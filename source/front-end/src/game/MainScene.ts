export class MainScene extends Phaser.Scene {
    private phaserSprite: Phaser.GameObjects.Sprite;
    private cursors: any;

    constructor() {
        super({
            key: "MainScene"
        });

        console.log("CREATECORSKORSKEYS 5555555555555555");
    }

    preload(): void {
        // this.load.image("logo", "./assets/boilerplate/phaser.png");
        this.load.image("skeleton", "img/skeleton.png");
        this.load.image("human", "img/human.png");
    }

    create(): void {
        // this.phaserSprite = this.add.sprite(400, 300, "logo");

        console.log("HALLO!");
        this.phaserSprite = this.add.sprite(100, 100, "human");

        this.cursors = this.input.keyboard.createCursorKeys();
    }

    update(): void {
        //console.log("update!!");

        if (this.cursors.left.isDown)
        {
            console.log("LEFT");
            //player.setVelocityX(-160);
            //player.anims.play('left', true);
        }
        else if (this.cursors.right.isDown)
        {
            console.log("RIGHT");
            //player.setVelocityX(160);
            //player.anims.play('right', true);
        }
        else
        {
            //player.setVelocityX(0);
            //player.anims.play('turn');
        }

        /*
        if (cursors.up.isDown && player.body.touching.down)
        {
            player.setVelocityY(-330);
        }
        */
    }
}