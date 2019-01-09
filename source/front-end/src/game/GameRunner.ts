import { MainScene } from "./MainScene";
import Sprite = Phaser.GameObjects.Sprite;
import {ZombieMoved} from "./ZombieMoved";

export class GameRunner {
    private game:Phaser.Game;
    private scene:MainScene;

    run():void {
        console.log("Starting game!");

        this.scene = new MainScene();

        this.game = new Phaser.Game({
            width: 800,
            height: 350,
            type: Phaser.AUTO,
            scene: this.scene
        });
    }

    private zombies:{
        [key: string]: Zombie
    } = {};

    zombieMoved(zombieMoved:ZombieMoved) {
        //console.log(zombieMoved);

        if (this.zombies.hasOwnProperty(zombieMoved.id)) {
            let zombie = this.zombies[zombieMoved.id];
            // console.log("Existing zombie:");
            // console.log(zombie);
            zombie.sprite.setPosition(zombieMoved.coordinate.x * 15, zombieMoved.coordinate.y * 15);
        } else {
            let sprite = this.scene.add.sprite(zombieMoved.coordinate.x, zombieMoved.coordinate.y, "skeleton");
            sprite.setScale(0.2 , 0.2);

            let zombie:Zombie = { 
                id: zombieMoved.id,
                sprite: sprite
            }

            // console.log("New zombie:");
            // console.log(zombie);

            this.zombies[zombie.id] = zombie;
        }
    }
}


class Zombie {
    readonly id:string;
    readonly sprite:Sprite;
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
