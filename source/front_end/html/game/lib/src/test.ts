class GameRUnner {
    private game:Phaser.Game;

    constructor() {
        
    }

    run():void {
        this.game = new Phaser.Game(800, 600, Phaser.AUTO, 'content', {
            preload: this.preload,
            create: this.create
        });
    }

    private preload():void {
        this.game.load.image("logo", "lib/npm/node_modules/phaser/phaser-logo-small.png");
    }

    private create():void {
        var logo = this.game.add.sprite(this.game.world.centerX, this.game.world.centerY, "logo");
        logo.anchor.setTo(0.5, 0.5);
    }
}

window.onload = () => {
    var game = new GameRUnner();
    game.run();
    console.log("loading game");
};