var GameRUnner = (function () {
    function GameRUnner() {
    }
    GameRUnner.prototype.run = function () {
        this.game = new Phaser.Game(800, 600, Phaser.AUTO, 'content', {
            preload: this.preload,
            create: this.create
        });
    };
    GameRUnner.prototype.preload = function () {
        this.game.load.image("logo", "lib/npm/node_modules/phaser/phaser-logo-small.png");
    };
    GameRUnner.prototype.create = function () {
        var logo = this.game.add.sprite(this.game.world.centerX, this.game.world.centerY, "logo");
        logo.anchor.setTo(0.5, 0.5);
    };
    return GameRUnner;
}());
window.onload = function () {
    var game = new GameRUnner();
    game.run();
    console.log("loading game");
};
var Student = (function () {
    function Student() {
    }
    Student.prototype.foo = function () {
        return "fooeef";
    };
    return Student;
}());
