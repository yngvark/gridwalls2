class GameRunner {
    private game:Phaser.Game;

    run(divIdName:String):void {
        console.log("Starting game!");
        this.game = new Phaser.Game(800, 350, Phaser.AUTO, divIdName, {
            preload: this.preload,
            create: this.create
        });
    }

    private preload():void {
        this.game.load.image("skeleton", "img/skeleton.png");
        this.game.load.image("human", "img/human.png");

        console.log("Starting game!!");
        this.game.add.sprite(1, 1, "human");
        document.game = this.game;
    }

    private create():void {
    }

    private zombies:{
        [key: string]: Zombie
    } = {};

    zombieMoved(zombieMoved:ZombieMoved) {
        if (this.zombies.hasOwnProperty(zombieMoved.id)) {
            let zombie = this.zombies[zombieMoved.id];
            console.log("Existing zombie:");
            console.log(zombie);
            console.log(zombieMoved);
            zombie.sprite.position.setTo(zombieMoved.coordinate.x * 15, zombieMoved.coordinate.y * 15);
        } else {
            let sprite = this.game.add.sprite(zombieMoved.coordinate.x, zombieMoved.coordinate.y, "skeleton"); 
            sprite.scale.setTo(0.2 , 0.2);

            let zombie:Zombie = { 
                id: zombieMoved.id,
                sprite: sprite
            }

            console.log("New zombie:");
            console.log(zombie);
            console.log(zombieMoved);

            this.zombies[zombie.id] = zombie;
        }
    }
}


class Zombie {
    readonly id:string;
    readonly sprite:Phaser.Sprite;
}

class Map<V> {
    private items: { [key: string]: V };

    constructor() {
        this.items = {};
    }

    add(key: string, value: V): void {
        this.items[key] = value;
    }

    has(key: string): boolean {
        return key in this.items;
    }

    get(key: string): V {
        return this.items[key];
    }
}
